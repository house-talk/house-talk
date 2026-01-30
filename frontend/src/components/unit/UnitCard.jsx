import { rejectTenant } from "../../services/adminTenantApi"; // ⭐ 추가

export default function UnitCard({ unit, onClick, onCheckoutSuccess }) {
  const handleCheckoutClick = async (e) => {
    e.stopPropagation(); // ⭐ 카드 클릭 방지

    if (!window.confirm("정말 퇴실 처리하시겠습니까?")) return;

    try {
      // ⭐ 퇴실 API 호출
      await rejectTenant(unit.tenantBuildingId);

      // ⭐ 성공 후 부모에게 알림 (UnitList에서 loadUnits)
      if (onCheckoutSuccess) {
        onCheckoutSuccess();
      }
    } catch (e) {
      alert(e.message || "퇴실 처리 실패");
    }
  };

  return (
    <div
      onClick={() => onClick(unit)}
      style={{
        padding: "16px",
        borderRadius: "12px",
        backgroundColor: "#f9fafb",
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        cursor: "pointer",
        transition: "transform 0.2s ease, box-shadow 0.2s ease",
      }}
      onMouseEnter={(e) => {
        e.currentTarget.style.transform = "translateY(-2px)";
        e.currentTarget.style.boxShadow =
          "0 6px 16px rgba(0,0,0,0.08)";
      }}
      onMouseLeave={(e) => {
        e.currentTarget.style.transform = "translateY(0)";
        e.currentTarget.style.boxShadow = "none";
      }}
    >
      {/* 왼쪽: 세대 정보 */}
      <div>
        <div style={{ fontWeight: "600", fontSize: "14px" }}>
          {unit.floor}층 {unit.unitNumber}호
        </div>

        <div
          style={{
            marginTop: "4px",
            fontSize: "12px",
            color: unit.occupied ? "#16a34a" : "#dc2626",
          }}
        >
          {unit.occupied ? "입주 중" : "공실"}
        </div>

        {/* ⭐ 세입자 정보 */}
        {unit.occupied && unit.tenantName && (
          <div
            style={{
              marginTop: "6px",
              fontSize: "12px",
              color: "#374151",
            }}
          >
            <div>세입자: {unit.tenantName}</div>
            <div>연락처: {unit.tenantPhoneNumber}</div>
          </div>
        )}

      </div>

      {/* 오른쪽: 액션 영역 */}
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "flex-end",
          gap: "8px",
        }}
      >
        {/* ⭐ 퇴실 버튼 */}
        {unit.occupied && unit.tenantName && (
          <button
            onClick={handleCheckoutClick}
            style={{
              padding: "6px 10px",
              fontSize: "12px",
              borderRadius: "6px",
              border: "1px solid #dc2626",
              backgroundColor: "#fff",
              color: "#dc2626",
              cursor: "pointer",
            }}
          >
            퇴실 처리
          </button>
        )}

        {/* 기존 상세 안내 */}
        <div
          style={{
            fontSize: "12px",
            color: "#9ca3af",
            display: "flex",
            alignItems: "center",
            gap: "4px",
          }}
        >
          상세보기
          <span style={{ fontSize: "14px" }}>›</span>
        </div>
      </div>
    </div>
  );
}
