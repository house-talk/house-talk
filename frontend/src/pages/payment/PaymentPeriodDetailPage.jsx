import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import PaymentStatusGrid from "../../components/payment/PaymentStatusGrid";

export default function PaymentPeriodDetailPage() {
  const { buildingId, paymentPeriodId } = useParams();
  const navigate = useNavigate();

  const [paymentPeriod, setPaymentPeriod] = useState(null);
  const [statuses, setStatuses] = useState([]);
  const [loading, setLoading] = useState(true);

  // 수정 모달
  const [showEditModal, setShowEditModal] = useState(false);
  const [editYear, setEditYear] = useState("");
  const [editMonth, setEditMonth] = useState("");
  const [editTitle, setEditTitle] = useState("");

  useEffect(() => {
    const loadAll = async () => {
      try {
        setLoading(true);

        const periodRes = await fetch(
          `/api/admin/buildings/${buildingId}/payments/periods/${paymentPeriodId}`,
          { credentials: "include" }
        );
        if (!periodRes.ok) throw new Error("납부 기간 조회 실패");
        const periodData = await periodRes.json();

        const statusRes = await fetch(
          `/api/admin/payments/periods/${paymentPeriodId}/statuses`,
          { credentials: "include" }
        );
        if (!statusRes.ok) throw new Error("납부 상태 조회 실패");
        const statusData = await statusRes.json();

        const totalCount = statusData.length;
        const paidCount = statusData.filter((s) => s.paid).length;
        const paidRate =
          totalCount === 0 ? 0 : Math.round((paidCount / totalCount) * 100);

        setPaymentPeriod({ ...periodData, paidRate });
        setStatuses(statusData);

        setEditYear(periodData.year);
        setEditMonth(periodData.month);
        setEditTitle(periodData.title);
      } catch (e) {
        console.error(e);
      } finally {
        setLoading(false);
      }
    };

    loadAll();
  }, [buildingId, paymentPeriodId]);

  /* 수정 */
  const handleUpdate = async () => {
    try {
      const res = await fetch(
        `/api/admin/buildings/${buildingId}/payments/${paymentPeriodId}`,
        {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          credentials: "include",
          body: JSON.stringify({
            year: Number(editYear),
            month: Number(editMonth),
            title: editTitle,
          }),
        }
      );

      if (!res.ok) throw new Error("수정 실패");

      alert("수정되었습니다.");
      window.location.reload();
    } catch (e) {
      alert(e.message);
    }
  };

  /* 삭제 */
  const handleDelete = async () => {
    if (!window.confirm("이 납부 기간을 삭제하시겠습니까?")) return;

    try {
      const res = await fetch(
        `/api/admin/buildings/${buildingId}/payments/${paymentPeriodId}`,
        { method: "DELETE", credentials: "include" }
      );

      if (!res.ok) throw new Error("삭제 실패");

      alert("삭제되었습니다.");
      navigate(-1);
    } catch {
      alert("삭제 중 오류 발생");
    }
  };

  if (loading) return <p>불러오는 중...</p>;

  return (
    <div style={{ width: "100%" }}>
      <div style={{ maxWidth: "960px", margin: "0 auto" }}>
        {/* 제목 + 버튼 */}
        <div style={{ position: "relative", marginBottom: "24px" }}>
          <h2
            style={{
              fontSize: "18px",
              textAlign: "center",
              fontWeight: "600",
            }}
          >
            {paymentPeriod &&
              `${paymentPeriod.year}년도 ${paymentPeriod.month}월 납부 관리`}
          </h2>

          <div
            style={{
              position: "absolute",
              right: 0,
              top: 50,
              display: "flex",
              gap: "8px",
            }}
          >
            <button style={btnGhost} onClick={() => setShowEditModal(true)}>
              수정
            </button>
            <button style={btnDanger} onClick={handleDelete}>
              삭제
            </button>
          </div>
        </div>

        <PaymentStatusGrid
          statuses={statuses}
          paymentPeriodId={paymentPeriodId}
        />
      </div>

      {/* ===== 수정 모달 (생성 모달과 동일 UI) ===== */}
      {showEditModal && (
        <div style={overlay}>
          <div style={modalBox}>
            <h3 style={modalTitle}>납부 기간 수정</h3>

            <div style={row}>
              <div style={fieldBox}>
                <label style={label}>연도</label>
                <select
                  style={select}
                  value={editYear}
                  onChange={(e) => setEditYear(e.target.value)}
                >
                  {Array.from({ length: 10 }, (_, i) => {
                    const year = new Date().getFullYear() - 3 + i;
                    return (
                      <option key={year} value={year}>
                        {year}
                      </option>
                    );
                  })}

                </select>
              </div>

              <div style={fieldBox}>
                <label style={label}>월</label>
                <select
                  style={select}
                  value={editMonth}
                  onChange={(e) => setEditMonth(e.target.value)}
                >
                  {Array.from({ length: 12 }, (_, i) => i + 1).map((m) => (
                    <option key={m} value={m}>
                      {m}월
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div style={{ marginBottom: "24px" }}>
              <label style={label}>제목</label>
              <input
                style={input}
                value={editTitle}
                onChange={(e) => setEditTitle(e.target.value)}
              />
            </div>

            <div style={buttonRow}>
              <button style={cancelBtn} onClick={() => setShowEditModal(false)}>
                취소
              </button>
              <button style={submitBtn} onClick={handleUpdate}>
                저장
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

/* ===== 생성 모달과 동일한 스타일 ===== */

const overlay = {
  position: "fixed",
  inset: 0,
  background: "rgba(0,0,0,0.4)",
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
  zIndex: 1000,
};

const modalBox = {
  width: "480px",
  background: "#fff",
  borderRadius: "16px",
  padding: "28px",
};

const modalTitle = {
  fontSize: "18px",
  fontWeight: "600",
  marginBottom: "24px",
};

const row = {
  display: "flex",
  gap: "16px",
  marginBottom: "20px",
};

const fieldBox = {
  flex: 1,
  display: "flex",
  flexDirection: "column",
};

const label = {
  fontSize: "14px",
  color: "#374151",
  marginBottom: "6px",
};

const select = {
  height: "40px",
  borderRadius: "8px",
  border: "1px solid #d1d5db",
  padding: "0 10px",
};

const input = {
  height: "40px",
  borderRadius: "8px",
  border: "1px solid #d1d5db",
  padding: "0 10px",
  width: "100%",
};

const buttonRow = {
  display: "flex",
  justifyContent: "flex-end",
  gap: "10px",
};

const cancelBtn = {
  background: "#f3f4f6",
  border: "none",
  borderRadius: "8px",
  padding: "8px 18px",
  cursor: "pointer",
};

const submitBtn = {
  background: "#2563eb",
  color: "#fff",
  border: "none",
  borderRadius: "8px",
  padding: "8px 18px",
  cursor: "pointer",
};

const btnGhost = {
  background: "#fff",
  border: "1px solid #d1d5db",
  borderRadius: "8px",
  padding: "8px 14px",
  cursor: "pointer",
};

const btnDanger = {
  background: "#ef4444",
  color: "#fff",
  border: "none",
  borderRadius: "8px",
  padding: "8px 14px",
  cursor: "pointer",
};
