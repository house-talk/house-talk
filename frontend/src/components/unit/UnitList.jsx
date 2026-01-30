import { useEffect, useState } from "react";
import { fetchUnits, updateUnitOrder } from "../../services/unitApi";
import UnitCard from "./UnitCard";
import UnitForm from "./UnitForm";

/* ⭐ DnD */
import {
  DndContext,
  PointerSensor,
  MouseSensor,
  useSensor,
  useSensors,
} from "@dnd-kit/core";
import {
  SortableContext,
  useSortable,
  arrayMove,
  rectSortingStrategy, // ⭐ 변경
} from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";

/* ===============================
   Sortable Unit Card Wrapper
=============================== */
function SortableUnitCard({ unit, onClick, onCheckoutSuccess }) {
  const { attributes, listeners, setNodeRef, transform, transition } =
    useSortable({ id: unit.unitId });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
  };

  return (
    <div ref={setNodeRef} style={style} {...attributes} {...listeners}>
      <UnitCard
        unit={unit}
        onClick={onClick}
        onCheckoutSuccess={onCheckoutSuccess} // ⭐ 전달
      />
    </div>
  );
}


export default function UnitList({ buildingId }) {
  const [units, setUnits] = useState([]);
  const [loading, setLoading] = useState(true);

  const [showForm, setShowForm] = useState(false);
  const [editingUnit, setEditingUnit] = useState(null);

  /* ===============================
     Drag Sensor
  =============================== */
  const sensors = useSensors(
    useSensor(MouseSensor),
    useSensor(PointerSensor, {
      activationConstraint: { distance: 8 },
    })
  );

  /* ===============================
     세대 목록 조회
  =============================== */
  useEffect(() => {
    loadUnits();
  }, [buildingId]);

  const loadUnits = async () => {
    try {
      setLoading(true);
      const data = await fetchUnits(buildingId);
      setUnits(data);
    } catch (e) {
      alert(e.message);
    } finally {
      setLoading(false);
    }
  };

  /* ===============================
     카드 클릭 → 상세 모달
  =============================== */
  const handleCardClick = (unit) => {
    setEditingUnit(unit);
    setShowForm(true);
  };

  /* ===============================
     층수별 그룹핑
  =============================== */
  const groupedByFloor = units.reduce((acc, unit) => {
    if (!acc[unit.floor]) acc[unit.floor] = [];
    acc[unit.floor].push(unit);
    return acc;
  }, {});

  const sortedFloors = Object.keys(groupedByFloor)
    .map(Number)
    .sort((a, b) => b - a);

  /* ===============================
     ⭐ Drag End → 서버 저장
  =============================== */
  const handleDragEnd = (floor) => async (event) => {
    const { active, over } = event;
    if (!over || active.id === over.id) return;

    let updatedForServer = [];

    setUnits((prev) => {
      const floorUnits = prev.filter((u) => u.floor === floor);
      const otherUnits = prev.filter((u) => u.floor !== floor);

      const oldIndex = floorUnits.findIndex(
        (u) => u.unitId === active.id // ✅ 수정
      );
      const newIndex = floorUnits.findIndex(
        (u) => u.unitId === over.id // ✅ 수정
      );

      const reordered = arrayMove(floorUnits, oldIndex, newIndex);

      const updated = reordered.map((u, idx) => ({
        ...u,
        orderIndex: idx + 1,
      }));

      updatedForServer = updated.map((u) => ({
        unitId: u.unitId, // ✅ 수정
        orderIndex: u.orderIndex,
      }));

      return [...otherUnits, ...updated];
    });

    // ⭐⭐⭐ 실제 서버 저장 (핵심)
    await updateUnitOrder(buildingId, updatedForServer);
  };

  /* ===============================
     렌더링
  =============================== */
  if (loading) {
    return <p>세대 목록을 불러오는 중...</p>;
  }

  return (
    <div>
      {/* 🔝 헤더 */}
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginBottom: "16px",
        }}
      >
        <h3 style={{ margin: 0 }}>세대 관리</h3>

        <button
          style={{
            padding: "8px 14px",
            borderRadius: "8px",
            border: "1px solid #2563eb",
            backgroundColor: "#2563eb",
            color: "#ffffff",
            cursor: "pointer",
            fontSize: "14px",
          }}
          onClick={() => {
            setEditingUnit(null);
            setShowForm(true);
          }}
        >
          + 세대 추가
        </button>
      </div>

      {/* 📋 층별 세대 목록 */}
      <div style={{ display: "flex", flexDirection: "column", gap: "20px" }}>
        {sortedFloors.map((floor) => (
          <div key={floor}>
            <h4 style={{ marginBottom: "8px" }}>{floor}층</h4>

            <DndContext sensors={sensors} onDragEnd={handleDragEnd(floor)}>
              <SortableContext
                items={groupedByFloor[floor].map((u) => u.unitId)} // ✅ 수정
                strategy={rectSortingStrategy}
              >
                <div
                  style={{
                    display: "flex",
                    gap: "12px",
                    flexWrap: "wrap",
                  }}
                >
                  {groupedByFloor[floor].map((unit) => (
                    <SortableUnitCard
                      key={unit.unitId}
                      unit={unit}
                      onClick={handleCardClick}
                      onCheckoutSuccess={loadUnits} 
                    />
                  ))}
                </div>
              </SortableContext>
            </DndContext>
          </div>
        ))}
      </div>

      {/* 📦 모달 */}
      {showForm && (
        <UnitForm
          buildingId={buildingId}
          unit={editingUnit}
          onClose={() => {
            setShowForm(false);
            setEditingUnit(null);
          }}
          onSuccess={loadUnits}
        />
      )}
    </div>
  );
}
