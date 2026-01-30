import { useEffect, useState } from "react";
import Button from "../common/Button";
import {
  createBuilding,
  updateBuilding,
} from "../../services/buildingApi";

export default function BuildingForm({ building, onSuccess, onClose }) {
  const [name, setName] = useState("");
  const [address, setAddress] = useState("");
  const [totalFloors, setTotalFloors] = useState("");
  const [totalUnits, setTotalUnits] = useState("");
  const [loading, setLoading] = useState(false);

  // ⭐ 수정 모드일 경우 초기값 세팅
  useEffect(() => {
    if (building) {
      setName(building.name);
      setAddress(building.address);
      setTotalFloors(building.totalFloors);
      setTotalUnits(building.totalUnits);
    }
  }, [building]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!name || !address || !totalFloors || !totalUnits) {
      alert("모든 항목을 입력해주세요.");
      return;
    }

    try {
      setLoading(true);

      if (building) {
        // ⭐ 수정
        await updateBuilding(building.id, {
          name,
          address,
          totalFloors: Number(totalFloors),
          totalUnits: Number(totalUnits),
        });
      } else {
        // ⭐ 신규 등록
        await createBuilding({
          name,
          address,
          totalFloors: Number(totalFloors),
          totalUnits: Number(totalUnits),
        });
      }

      onSuccess();
    } catch (e) {
      alert(
        building
          ? "건물 수정에 실패했습니다."
          : "건물 등록에 실패했습니다."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        borderRadius: "16px",
        padding: "24px",
        backgroundColor: "#ffffff",
        boxShadow: "0 6px 20px rgba(0,0,0,0.08)",
      }}
    >
      <h3 style={{ marginBottom: "20px" }}>
        {building ? "건물 수정" : "건물 추가"}
      </h3>

      <form onSubmit={handleSubmit}>
        {/* 건물명 */}
        <div style={{ marginBottom: "16px" }}>
          <label>건물명</label>
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            style={inputStyle}
            placeholder="예: OO빌라"
          />
        </div>

        {/* 주소 */}
        <div style={{ marginBottom: "16px" }}>
          <label>주소</label>
          <input
            type="text"
            value={address}
            onChange={(e) => setAddress(e.target.value)}
            style={inputStyle}
            placeholder="예: 서울시 OO구 OO동"
          />
        </div>

        {/* 층수 / 세대수 */}
        <div style={{ display: "flex", gap: "16px", marginBottom: "24px" }}>
          <div style={{ flex: 1 }}>
            <label>총 층수</label>
            <input
              type="number"
              value={totalFloors}
              onChange={(e) => setTotalFloors(e.target.value)}
              style={inputStyle}
              placeholder="예: 5"
            />
          </div>

          <div style={{ flex: 1 }}>
            <label>총 세대 수</label>
            <input
              type="number"
              value={totalUnits}
              onChange={(e) => setTotalUnits(e.target.value)}
              style={inputStyle}
              placeholder="예: 20"
            />
          </div>
        </div>

        {/* 🔹 버튼 영역 */}
        <div
          style={{
            display: "flex",
            justifyContent: "flex-end",
            gap: "12px",
          }}
        >
          <Button type="button" onClick={onClose}>
            닫기
          </Button>

          <Button variant="primary" type="submit">
            {loading
              ? building
                ? "수정 중..."
                : "등록 중..."
              : building
              ? "수정"
              : "등록"}
          </Button>
        </div>
      </form>
    </div>
  );
}

const inputStyle = {
  width: "100%",
  marginTop: "6px",
  padding: "10px 12px",
  borderRadius: "10px",
  border: "1px solid #d1d5db",
  fontSize: "14px",
};
