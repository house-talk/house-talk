import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import PaymentPeriodCreateModal from "../../components/payment/PaymentPeriodCreateModal";
import { fetchPaymentPeriods } from "../../services/paymentApi";
import "./PaymentPeriodListPage.css";

export default function PaymentPeriodListPage() {
  const { id: buildingId } = useParams();
  const navigate = useNavigate();

  const [periods, setPeriods] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

  const [keyword, setKeyword] = useState("");
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  const PAGE_SIZE = 10;

  const loadPeriods = async () => {
    try {
      setLoading(true);

      const data = await fetchPaymentPeriods(
        buildingId,
        page - 1,
        PAGE_SIZE,
        keyword
      );

      const sorted = [...data.content].sort(
        (a, b) => new Date(b.createdAt) - new Date(a.createdAt)
      );

      setPeriods(sorted);
      setTotalPages(data.totalPages);
    } catch (e) {
      console.error(e);
      setPeriods([]);
      setTotalPages(1);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPeriods();
  }, [buildingId, page]);

  if (loading) return <p>불러오는 중...</p>;

  return (
    <div>
      {/* 헤더 */}
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginBottom: "12px",
        }}
      >
        <h2 style={{ margin: 0, fontSize: "18px" }}>납부 관리</h2>
        <button
          onClick={() => setIsCreateModalOpen(true)}
          style={{
            padding: "8px 14px",
            borderRadius: "8px",
            background: "#2563eb",
            color: "#fff",
            border: "none",
            cursor: "pointer",
            fontSize: "13px",
          }}
        >
          + 납부 기간 생성
        </button>
      </div>

      {/* 테이블 */}
      <table
        style={{
          width: "100%",
          borderCollapse: "collapse",
          tableLayout: "fixed",
        }}
      >
        <thead>
          <tr style={{ borderBottom: "1px solid #e5e7eb" }}>
            <th
              style={{
                textAlign: "left",
                padding: "10px 8px",
                fontSize: "13px",
                fontWeight: "600",
              }}
            >
              제목
            </th>
            <th
              style={{
                textAlign: "right",
                padding: "10px 8px",
                fontSize: "13px",
                fontWeight: "600",
                width: "160px",
              }}
            >
              <span>납부율</span>
  <span style={{ margin: "0 15px" }}>/</span>
  <span>기간</span>

            </th>

          </tr>
        </thead>

        <tbody>
          {periods.length === 0 ? (
            <tr>
              <td
                colSpan={2}
                style={{
                  textAlign: "center",
                  padding: "48px 0",
                  color: "#6b7280",
                  fontSize: "14px",
                }}
              >
                생성된 납부 기간이 없습니다.
              </td>
            </tr>
          ) : (
            periods.map((p) => (
              <tr
                key={p.id}
                style={{
                  borderBottom: "1px solid #e5e7eb",
                  cursor: "pointer",
                }}
                onClick={() =>
                  navigate(`/admin/buildings/${buildingId}/payments/${p.id}`)
                }
              >
                <td style={{ padding: "10px 8px" }}>{p.title}</td>

                {/* ✅ 여기만 변경 */}
                <td
                  style={{
                    padding: "10px 8px",
                    textAlign: "right",
                    fontSize: "13px",
                  }}
                >
                  <div
                    style={{
                      display: "flex",
                      justifyContent: "flex-end",
                      alignItems: "center",
                      gap: "8px",
                    }}
                  >
                    {/* 납부율 */}
                    <span
                      style={{
                        fontWeight: "600",
                        color:
                          p.paidRate === 100
                            ? "#16a34a"
                            : p.paidRate === 0
                            ? "#dc2626"
                            : "#2563eb",
                      }}
                    >
                      {p.paidRate ?? 0}%
                    </span>

                    {/* 기간 */}
                    <span style={{ color: "#374151" }}>
                      {p.year}.{String(p.month).padStart(2, "0")}
                    </span>
                  </div>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>

      {/* 페이지네이션 */}
      <div className="pagination">
        <button className="page-btn" onClick={() => setPage(1)} disabled={page === 1}>
          {"<<"}
        </button>
        <button
          className="page-btn"
          onClick={() => setPage((p) => Math.max(1, p - 1))}
          disabled={page === 1}
        >
          {"<"}
        </button>

        {Array.from({ length: totalPages }).map((_, i) => (
          <button
            key={i}
            className={`page-btn ${page === i + 1 ? "active" : ""}`}
            onClick={() => setPage(i + 1)}
          >
            {i + 1}
          </button>
        ))}

        <button
          className="page-btn"
          onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
          disabled={page === totalPages}
        >
          {">"}
        </button>
        <button
          className="page-btn"
          onClick={() => setPage(totalPages)}
          disabled={page === totalPages}
        >
          {">>"}
        </button>
      </div>

      {/* 검색 */}
      <div
        style={{
          border: "1px solid #e5e7eb",
          padding: "16px",
          display: "flex",
          justifyContent: "center",
          gap: "8px",
          marginTop: "20px",
        }}
      >
        <input
          type="text"
          placeholder="검색어를 입력하세요"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              setPage(1);
              loadPeriods();
            }
          }}
          style={{
            width: "260px",
            padding: "6px 8px",
            fontSize: "13px",
          }}
        />

        <button
          type="button"
          onClick={() => {
            setPage(1);
            loadPeriods();
          }}
          style={{
            padding: "6px 20px",
            backgroundColor: "#4b5563",
            color: "#ffffff",
            border: "none",
            cursor: "pointer",
            fontSize: "13px",
          }}
        >
          검색
        </button>
      </div>

      {isCreateModalOpen && (
        <PaymentPeriodCreateModal
          buildingId={buildingId}
          onClose={() => setIsCreateModalOpen(false)}
          onSuccess={loadPeriods}
        />
      )}
    </div>
  );
}
