import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { fetchUnits, deleteUnit } from "../services/unitApi";

export default function UnitDetail() {
  const { buildingId, unitId } = useParams();
  const navigate = useNavigate();

  const [unit, setUnit] = useState(null);
  const [loading, setLoading] = useState(true);

  /* ===============================
     세대 정보 로딩
  =============================== */
  useEffect(() => {
    loadUnit();
  }, [buildingId, unitId]);

  const loadUnit = async () => {
    try {
      setLoading(true);
      const units = await fetchUnits(buildingId);
      const found = units.find((u) => String(u.id) === unitId);

      if (!found) {
        alert("존재하지 않는 세대입니다.");
        navigate(-1);
        return;
      }

      setUnit(found);
    } catch (e) {
      alert(e.message);
    } finally {
      setLoading(false);
    }
  };

  /* ===============================
     삭제
  =============================== */
  const handleDelete = async () => {
    const confirmed = window.confirm("정말 이 세대를 삭제하시겠습니까?");
    if (!confirmed) return;

    try {
      await deleteUnit(buildingId, unitId);
      alert("세대가 삭제되었습니다.");
      navigate(`/admin/buildings/${buildingId}`);
    } catch (e) {
      alert(e.message);
    }
  };

  /* ===============================
     렌더링
  =============================== */
  if (loading) return <p>불러오는 중...</p>;
  if (!unit) return null;

  return (
    <div style={{ padding: "24px", maxWidth: "800px", margin: "0 auto" }}>
      {/* 🔙 뒤로 */}
      <button
        onClick={() => navigate(-1)}
        style={backBtn}
      >
        ← 세대 목록으로
      </button>

      {/* 📦 카드 */}
      <div style={cardStyle}>
        <h2 style={{ marginBottom: "8px" }}>
          {unit.floor}층 {unit.unitNumber}호
        </h2>

        <p
          style={{
            fontSize: "14px",
            color: unit.isOccupied ? "#16a34a" : "#dc2626",
            marginBottom: "12px",
          }}
        >
          {unit.isOccupied ? "입주 중" : "공실"}
        </p>

        {unit.memo && (
          <p style={{ color: "#6b7280", marginBottom: "16px" }}>
            {unit.memo}
          </p>
        )}

        {/* 🔧 버튼 */}
        <div style={{ display: "flex", gap: "8px" }}>
          <button
            style={btnStyle}
            onClick={() =>
              alert("수정은 세대 관리 화면에서 처리 (이미 구현됨)")
            }
          >
            수정
          </button>

          <button
            style={{ ...btnStyle, color: "#dc2626" }}
            onClick={handleDelete}
          >
            삭제
          </button>
        </div>
      </div>

      {/* 🚧 세입자 영역 (미래 확장) */}
      <div style={placeholder}>
        <h4>세입자 정보</h4>
        <p style={{ fontSize: "13px", color: "#6b7280" }}>
          세입자가 초대코드를 통해 입주하면 이 영역에 표시됩니다.
        </p>
      </div>
    </div>
  );
}

/* ===== 스타일 ===== */

const backBtn = {
  marginBottom: "16px",
  border: "none",
  background: "none",
  cursor: "pointer",
  color: "#2563eb",
  fontSize: "14px",
};

const cardStyle = {
  padding: "24px",
  borderRadius: "16px",
  backgroundColor: "#ffffff",
  boxShadow: "0 8px 24px rgba(0,0,0,0.08)",
  marginBottom: "24px",
};

const btnStyle = {
  padding: "8px 14px",
  borderRadius: "8px",
  border: "1px solid #d1d5db",
  background: "#ffffff",
  cursor: "pointer",
  fontSize: "14px",
};

const placeholder = {
  padding: "20px",
  borderRadius: "12px",
  backgroundColor: "#f9fafb",
};
