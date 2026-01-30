import { useEffect, useState } from "react";
import {
  createUnit,
  createUnitsBulk,
  updateUnit,
  deleteUnit,
} from "../../services/unitApi";

export default function UnitForm({
  buildingId,
  unit, // ⭐ 수정 대상 (없으면 추가)
  onClose,
  onSuccess,
}) {
  const [floor, setFloor] = useState("");
  const [unitNumber, setUnitNumber] = useState("");

  // ⭐ bulk 전용
  const [mode, setMode] = useState("single"); // single | bulk
  const [startUnit, setStartUnit] = useState("");
  const [endUnit, setEndUnit] = useState("");

  const [isOccupied, setIsOccupied] = useState(false);
  const [memo, setMemo] = useState("");
  const [loading, setLoading] = useState(false);

  /* ===============================
     수정 모드일 경우 초기값 세팅
  =============================== */
  useEffect(() => {
    if (unit) {
      setFloor(unit.floor?.toString() ?? "");
      setUnitNumber(unit.unitNumber ?? "");
      setIsOccupied(!!unit.isOccupied);
      setMemo(unit.memo ?? "");
    } else {
      // ⭐ 추가 모드일 때 반드시 초기화
      setFloor("");
      setUnitNumber("");
      setIsOccupied(false);
      setMemo("");
      setStartUnit("");
      setEndUnit("");
      setMode("single");
    }
  }, [unit]);

  /* ===============================
     제출 (추가 / 수정 / 여러 세대 추가)
  =============================== */
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!floor) {
      alert("층수는 필수입니다.");
      return;
    }

    try {
      setLoading(true);

      // =====================
      // ⭐ 수정
      // =====================
      if (unit) {
        const payload = {};

        if (Number(floor) !== unit.floor) {
          payload.floor = Number(floor);
        }

        if (unitNumber !== unit.unitNumber) {
          payload.unitNumber = unitNumber;
        }

        if (memo !== unit.memo) {
          payload.memo = memo;
        }

        // ❌ isOccupied는 수정 시 절대 보내지 않음

        await updateUnit(buildingId, unit.unitId, payload);
      }
      // =====================
      // ⭐ 여러 세대 추가
      // =====================
      else if (mode === "bulk") {
        if (!startUnit || !endUnit) {
          alert("시작/끝 호수를 입력하세요.");
          return;
        }

        await createUnitsBulk(buildingId, {
          floor: Number(floor),
          startUnit: Number(startUnit),
          endUnit: Number(endUnit),
          isOccupied,
          memo,
        });
      }
      // =====================
      // ⭐ 단일 추가
      // =====================
      else {
        if (!unitNumber) {
          alert("호수는 필수입니다.");
          return;
        }

        await createUnit(buildingId, {
          floor: Number(floor),
          unitNumber,
          isOccupied,
          memo,
        });
      }

      onSuccess();
      onClose();
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
    if (!window.confirm("이 세대를 삭제하시겠습니까?")) return;

    try {
      setLoading(true);
      await deleteUnit(buildingId, unit.unitId);
      onSuccess();
      onClose();
    } catch (e) {
      alert(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      {/* 🌑 배경 */}
      <div
        onClick={onClose}
        style={{
          position: "fixed",
          inset: 0,
          backgroundColor: "rgba(0,0,0,0.4)",
          zIndex: 100,
        }}
      />

      {/* 📦 모달 */}
      <div
        style={{
          position: "fixed",
          top: "50%",
          left: "50%",
          transform: "translate(-50%, -50%)",
          width: "420px",
          padding: "24px",
          borderRadius: "16px",
          backgroundColor: "#ffffff",
          boxShadow: "0 8px 32px rgba(0,0,0,0.2)",
          zIndex: 101,
        }}
      >
        <h3 style={{ marginBottom: "16px" }}>
          {unit ? "세대 상세 / 수정" : "세대 추가"}
        </h3>

        {/* ⭐ 추가 모드 선택 (추가일 때만) */}
        {!unit && (
          <div style={{ display: "flex", gap: "8px", marginBottom: "12px" }}>
            <button
              type="button"
              onClick={() => setMode("single")}
              style={{
                ...btnStyle,
                backgroundColor: mode === "single" ? "#2563eb" : "#fff",
                color: mode === "single" ? "#fff" : "#000",
              }}
            >
              단일 추가
            </button>
            <button
              type="button"
              onClick={() => setMode("bulk")}
              style={{
                ...btnStyle,
                backgroundColor: mode === "bulk" ? "#2563eb" : "#fff",
                color: mode === "bulk" ? "#fff" : "#000",
              }}
            >
              여러 세대 추가
            </button>
          </div>
        )}

        <form
          onSubmit={handleSubmit}
          style={{ display: "flex", flexDirection: "column", gap: "12px" }}
        >
          {/* 층수 */}
          <input
            type="number"
            placeholder="층수 (예: 5)"
            value={floor}
            onChange={(e) => setFloor(e.target.value)}
            style={inputStyle}
          />

          {/* ⭐ 단일 추가 */}
          {!unit && mode === "single" && (
            <input
              type="text"
              placeholder="호수 (예: 501)"
              value={unitNumber}
              onChange={(e) => setUnitNumber(e.target.value)}
              style={inputStyle}
            />
          )}

          {/* ⭐ 여러 세대 추가 */}
          {!unit && mode === "bulk" && (
            <>
              <input
                type="number"
                placeholder="시작 호수 (예: 501)"
                value={startUnit}
                onChange={(e) => setStartUnit(e.target.value)}
                style={inputStyle}
              />
              <input
                type="number"
                placeholder="끝 호수 (예: 509)"
                value={endUnit}
                onChange={(e) => setEndUnit(e.target.value)}
                style={inputStyle}
              />
            </>
          )}

          {/* ⭐ 입주 여부 (추가일 때만) */}
          {!unit && (
            <label style={{ fontSize: "14px" }}>
              <input
                type="checkbox"
                checked={isOccupied}
                onChange={(e) => setIsOccupied(e.target.checked)}
                style={{ marginRight: "6px" }}
              />
              입주 중
            </label>
          )}

          {/* 메모 */}
          <textarea
            placeholder="메모 (선택)"
            value={memo}
            onChange={(e) => setMemo(e.target.value)}
            style={{ ...inputStyle, height: "80px", resize: "none" }}
          />

          {/* 버튼 */}
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              marginTop: "12px",
            }}
          >
            {unit && (
              <button
                type="button"
                onClick={handleDelete}
                disabled={loading}
                style={{ ...btnStyle, color: "#dc2626" }}
              >
                삭제
              </button>
            )}

            <div style={{ display: "flex", gap: "8px" }}>
              <button type="button" onClick={onClose} style={btnStyle}>
                취소
              </button>
              <button
                type="submit"
                disabled={loading}
                style={{
                  ...btnStyle,
                  backgroundColor: "#2563eb",
                  color: "#fff",
                  border: "none",
                }}
              >
                {loading ? "저장 중..." : "저장"}
              </button>
            </div>
          </div>
        </form>
      </div>
    </>
  );
}

/* ===== 스타일 ===== */
const inputStyle = {
  padding: "10px 12px",
  borderRadius: "8px",
  border: "1px solid #d1d5db",
  fontSize: "14px",
};

const btnStyle = {
  padding: "8px 14px",
  borderRadius: "8px",
  border: "1px solid #d1d5db",
  backgroundColor: "#ffffff",
  cursor: "pointer",
  fontSize: "14px",
};
