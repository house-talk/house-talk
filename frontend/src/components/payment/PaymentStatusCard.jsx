export default function PaymentStatusCard({ status, onToggle }) {
  const {
    floor,
    unitNumber,
    tenantName,
    tenantPhoneNumber,
    paid,
  } = status;

  // 🔥 상태별 컬러
  const bgColor = paid ? "#ecfdf5" : "#fef2f2";
  const borderColor = paid ? "#22c55e" : "#ef4444";
  const textColor = paid ? "#166534" : "#991b1b";

  return (
    <div
      onClick={() => onToggle(status)}
      style={{
        padding: "12px",
        borderRadius: "12px",
        backgroundColor: bgColor,
        border: `1.5px solid ${borderColor}`,
        fontSize: "12px",
        cursor: "pointer",
        transition: "all 0.15s ease",
        boxShadow: "0 2px 6px rgba(0,0,0,0.04)",
        minHeight: "80px",

        // ✅ hover UX
        transform: "translateY(0)",
      }}
      onMouseEnter={(e) => {
        e.currentTarget.style.transform = "translateY(-2px)";
        e.currentTarget.style.boxShadow =
          "0 6px 12px rgba(0,0,0,0.08)";
      }}
      onMouseLeave={(e) => {
        e.currentTarget.style.transform = "translateY(0)";
        e.currentTarget.style.boxShadow =
          "0 2px 6px rgba(0,0,0,0.04)";
      }}
    >
      {/* 호수 */}
      <div
        style={{
          fontWeight: "600",
          fontSize: "13px",
          marginBottom: "6px",
          color: "#111827",
        }}
      >
        {floor}층 {unitNumber}호
      </div>

      {/* 입주자 정보 */}
      {tenantName ? (
        <>
          <div
            style={{
              fontSize: "12px",
              color: textColor,
              fontWeight: "500",
            }}
          >
            {tenantName}
          </div>
          <div
            style={{
              fontSize: "11px",
              color: "#6b7280",
            }}
          >
            {tenantPhoneNumber}
          </div>
        </>
      ) : (
        <div
          style={{
            fontSize: "12px",
            color: "#6b7280",
          }}
        >
          공실
        </div>
      )}
    </div>
  );
}
