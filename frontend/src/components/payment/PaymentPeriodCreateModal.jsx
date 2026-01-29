import { useEffect, useState } from "react";
import { createPaymentPeriod } from "../../services/paymentApi";
import "./PaymentPeriodCreateModal.css";

export default function PaymentPeriodCreateModal({
  buildingId,
  onClose,
  onSuccess,
}) {
  const now = new Date();

  const [year, setYear] = useState(now.getFullYear());
  const [month, setMonth] = useState(now.getMonth() + 1);
  const [title, setTitle] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setTitle(`${year}년 ${month}월 납부 내역`);
  }, [year, month]);

  const handleSubmit = async () => {
    if (!title.trim()) {
      alert("제목을 입력해주세요.");
      return;
    }

    try {
      setLoading(true);
      await createPaymentPeriod(buildingId, { year, month, title });
      onSuccess();
      onClose();
    } catch (e) {
      alert("이미 존재하는 납부 기간입니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-box">
        <h2 className="modal-title">납부 기간 생성</h2>

        <div className="form-row">
          <div className="form-group">
            <label>연도</label>
            <select value={year} onChange={(e) => setYear(+e.target.value)}>
              {Array.from({ length: 50 }, (_, i) => 2025 + i).map((y) => (
                <option key={y} value={y}>
                  {y}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>월</label>
            <select value={month} onChange={(e) => setMonth(+e.target.value)}>
              {Array.from({ length: 12 }, (_, i) => i + 1).map((m) => (
                <option key={m} value={m}>
                  {m}월
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="form-group">
          <label>제목</label>
          <input
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
        </div>

        <div className="modal-actions">
          <button className="btn-cancel" onClick={onClose} disabled={loading}>
            취소
          </button>
          <button
            className="btn-primary"
            onClick={handleSubmit}
            disabled={loading}
          >
            {loading ? "생성 중..." : "생성"}
          </button>
        </div>
      </div>
    </div>
  );
}
