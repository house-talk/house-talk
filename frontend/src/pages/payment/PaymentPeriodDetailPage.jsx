import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import PaymentStatusGrid from "../../components/payment/PaymentStatusGrid";

const API_BASE = import.meta.env.VITE_API_BASE_URL;

export default function PaymentPeriodDetailPage() {
  const { buildingId, paymentPeriodId } = useParams();

  const [paymentPeriod, setPaymentPeriod] = useState(null);
  const [statuses, setStatuses] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadAll = async () => {
      try {
        setLoading(true);

        // 1️⃣ 납부 기간 단건 조회
        const periodRes = await fetch(
          `${API_BASE}/api/admin/buildings/${buildingId}/payments/periods/${paymentPeriodId}`,
          { credentials: "include" }
        );
        if (!periodRes.ok) throw new Error("납부 기간 조회 실패");
        const periodData = await periodRes.json();

        // 2️⃣ 납부 상태 목록 조회
        const statusRes = await fetch(
          `${API_BASE}/api/admin/payments/periods/${paymentPeriodId}/statuses`,
          { credentials: "include" }
        );
        if (!statusRes.ok) throw new Error("납부 상태 조회 실패");
        const statusData = await statusRes.json();

        // ✅ 3️⃣ 여기서 납부율 계산 (핵심)
        const totalCount = statusData.length;
        const paidCount = statusData.filter((s) => s.paid).length;
        const paidRate =
          totalCount === 0 ? 0 : Math.round((paidCount / totalCount) * 100);

        setPaymentPeriod({
          ...periodData,
          paidRate, // ✅ 계산된 납부율
        });

        setStatuses(statusData);
      } catch (e) {
        console.error(e);
        setPaymentPeriod(null);
        setStatuses([]);
      } finally {
        setLoading(false);
      }
    };

    loadAll();
  }, [buildingId, paymentPeriodId]);

  if (loading) return <p>불러오는 중...</p>;

  return (
    <div style={{ width: "100%" }}>
      <div
        style={{
          maxWidth: "960px",
          margin: "0 auto",
        }}
      >
        <h2
          style={{
            fontSize: "18px",
            marginBottom: "20px",
            textAlign: "center",
            fontWeight: "600",
          }}
        >
          {paymentPeriod
            ? `${paymentPeriod.year}년도 ${paymentPeriod.month}월 납부 관리`
            : "납부 관리"}
        </h2>

        <PaymentStatusGrid
          statuses={statuses}
          paymentPeriodId={paymentPeriodId}
        />
      </div>
    </div>
  );
}
