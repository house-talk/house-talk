import { useState, useEffect } from "react";
import PaymentStatusCard from "./PaymentStatusCard";


/**
 * ✅ Grid 책임
 * - paid → isPaid 정규화
 * - 표시 + 토글만 담당
 */
export default function PaymentStatusGrid({ statuses, paymentPeriodId }) {
  const [localStatuses, setLocalStatuses] = useState([]);

  /* ===================== 최초 로드 / 재조회 시 정규화 ===================== */
  useEffect(() => {
    if (statuses && statuses.length > 0) {
      setLocalStatuses(
        statuses.map((s) => ({
          ...s,
          isPaid: s.paid, // ✅ 여기서 딱 한 번만 변환
        }))
      );
    }
  }, [statuses]);

  /* ===================== empty 처리 ===================== */
  if (!localStatuses || localStatuses.length === 0) {
    return (
      <div
        style={{
          padding: "60px 0",
          textAlign: "center",
          color: "#6b7280",
          fontSize: "14px",
        }}
      >
        납부 상태 데이터가 없습니다.
      </div>
    );
  }

  /* ===================== 요약 (isPaid 기준) ===================== */
  const totalCount = localStatuses.length;
  const paidCount = localStatuses.filter((s) => s.isPaid).length;
  const unpaidCount = totalCount - paidCount;

  /* ===================== 토글 ===================== */
  const handleToggle = async (status) => {
    try {
      // 1️⃣ 토글 API
      await fetch(
        `/api/admin/payments/statuses/${status.paymentStatusId}/toggle`,
        {
          method: "POST",
          credentials: "include",
        }
      );

      // 2️⃣ 재조회
      const res = await fetch(
        `/api/admin/payments/periods/${paymentPeriodId}/statuses`,
        { credentials: "include" }
      );
      if (!res.ok) throw new Error("납부 상태 재조회 실패");

      const data = await res.json();

      // 3️⃣ 재조회 결과도 동일하게 정규화
      setLocalStatuses(
        data.map((s) => ({
          ...s,
          isPaid: s.paid,
        }))
      );
    } catch (e) {
      console.error("납부 상태 토글 실패", e);
    }
  };

  /* ===================== 층별 그룹 ===================== */
  const groupedByFloor = localStatuses.reduce((acc, s) => {
    if (!acc[s.floor]) acc[s.floor] = [];
    acc[s.floor].push(s);
    return acc;
  }, {});

  const floors = Object.keys(groupedByFloor)
    .map(Number)
    .sort((a, b) => b - a);

  return (
    <div>
      {/* ================= 요약 바 ================= */}
      <div
        style={{
          display: "flex",
          gap: "8px",
          marginBottom: "28px",
        }}
      >
        <SummaryPill label="총 세대" value={totalCount} />
        <SummaryPill label="납부" value={paidCount} color="#22c55e" />
        <SummaryPill label="미납" value={unpaidCount} color="#ef4444" />
      </div>

      {floors.map((floor) => (
        <div key={floor} style={{ marginBottom: "36px" }}>
          {/* 층 헤더 */}
          <div
            style={{
              display: "flex",
              alignItems: "center",
              marginBottom: "12px",
              gap: "12px",
            }}
          >
            <span style={{ fontWeight: "600", fontSize: "14px" }}>
              {floor}층
            </span>
            <div
              style={{
                flex: 1,
                height: "1px",
                backgroundColor: "#e5e7eb",
              }}
            />
          </div>

          {/* 카드 영역 */}
          <div
            style={{
              display: "grid",
              gridTemplateColumns: "repeat(auto-fill, minmax(150px, 1fr))",
              gap: "10px",
              justifyContent: "center",
            }}
          >
            {groupedByFloor[floor].map((status) => (
              <PaymentStatusCard
                key={status.paymentStatusId}
                status={status}     // ✅ 그대로 전달 (isPaid 기준)
                onToggle={handleToggle}
              />
            ))}
          </div>
        </div>
      ))}
    </div>
  );
}

/* ================= 요약 pill ================= */
function SummaryPill({ label, value, color = "#374151" }) {
  return (
    <div
      style={{
        backgroundColor: "#f3f4f6",
        borderRadius: "999px",
        padding: "6px 14px",
        fontSize: "13px",
        display: "flex",
        gap: "6px",
      }}
    >
      <span>{label}</span>
      <span style={{ fontWeight: "600", color }}>{value}</span>
    </div>
  );
}
