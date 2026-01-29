import Button from "../common/Button";
import { FaHouseUser, FaBuilding } from "react-icons/fa";
import { useNavigate } from "react-router-dom";

export default function BuildingCard({
  building,
  onEdit,
  onDelete,
}) {
  const navigate = useNavigate();

  return (
    <div
      onClick={() => navigate(`/admin/buildings/${building.id}`)}
      style={{
        padding: "20px",
        borderRadius: "16px",
        backgroundColor: "#ffffff",
        boxShadow: "0 4px 16px rgba(0,0,0,0.08)",
        marginBottom: "20px",
        cursor: "pointer",

        /* ⭐ hover 효과 준비 */
        transition: "transform 0.2s ease, box-shadow 0.2s ease",
      }}
      onMouseEnter={(e) => {
        e.currentTarget.style.transform = "translateY(-4px)";
        e.currentTarget.style.boxShadow =
          "0 8px 24px rgba(0,0,0,0.12)";
      }}
      onMouseLeave={(e) => {
        e.currentTarget.style.transform = "translateY(0)";
        e.currentTarget.style.boxShadow =
          "0 4px 16px rgba(0,0,0,0.08)";
      }}
    >
      {/* 🔝 상단: 건물명 + 버튼 */}
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginBottom: "8px",
        }}
      >
        <h3 style={{ margin: 0 }}>
          {building.name}
        </h3>

        <div style={{ display: "flex", gap: "8px" }}>
          <Button
            onClick={(e) => {
              e.stopPropagation(); // 카드 클릭 방지
              onEdit(building);
            }}
          >
            수정
          </Button>
          <Button
            variant="danger"
            onClick={(e) => {
              e.stopPropagation(); // 카드 클릭 방지
              onDelete(building);
            }}
          >
            삭제
          </Button>
        </div>
      </div>

      {/* 주소 */}
      <p
        style={{
          color: "#6b7280",
          fontSize: "14px",
          marginBottom: "16px",
        }}
      >
        {building.address}
      </p>

      {/* 구분선 */}
      <hr
        style={{
          border: "none",
          borderTop: "1px solid #e5e7eb",
          margin: "16px 0",
        }}
      />

      {/* 🔽 하단 요약 정보 */}
      <div
        style={{
          display: "flex",
          gap: "32px",
        }}
      >
        {/* 총 층수 */}
        <div>
          <div style={{ display: "flex", alignItems: "center", gap: "6px" }}>
            <FaBuilding size={18} color="#1f4fa3" />
            <span style={{ fontSize: "20px", fontWeight: "600" }}>
              {building.totalFloors}
            </span>
          </div>
          <div
            style={{
              fontSize: "12px",
              color: "#000000ff",
            }}
          >
            총 층수
          </div>
        </div>

        {/* 총 세대 */}
        <div>
          <div style={{ display: "flex", alignItems: "center", gap: "6px" }}>
            <FaHouseUser size={18} color="#1f4fa3" />
            <span style={{ fontSize: "20px", fontWeight: "600" }}>
              {building.totalUnits}
            </span>
          </div>
          <div
            style={{
              fontSize: "12px",
              color: "#000000ff",
            }}
          >
            총 세대
          </div>
        </div>
      </div>
    </div>
  );
}
