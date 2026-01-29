import { useEffect, useState } from "react";
import { searchNotices } from "../../services/noticeApi";
import { useNavigate, useSearchParams } from "react-router-dom";

/**
 * props
 * - buildingId: Long
 * - isAdmin: boolean
 */
export default function NoticeList({ buildingId, isAdmin }) {
  const [notices, setNotices] = useState([]);
  const [loading, setLoading] = useState(true);

  // ⭐ URL page 파라미터
  const [searchParams, setSearchParams] = useSearchParams();
  const pageFromUrl = Number(searchParams.get("page")) || 1;

  // ⭐ 페이지 / 검색 상태
  const [currentPage, setCurrentPage] = useState(pageFromUrl);
  const [keyword, setKeyword] = useState("");
  const [searchKeyword, setSearchKeyword] = useState("");
  const [searchType, setSearchType] = useState("title");
  const [totalPages, setTotalPages] = useState(0);

  const navigate = useNavigate();
  const PAGE_SIZE = 10;

  /* =========================
     공지 목록 조회 (검색 + 페이징)
  ========================= */
  const loadNotices = async () => {
    try {
      setLoading(true);

      const data = await searchNotices(buildingId, {
        keyword: searchKeyword,
        type: searchType,
        page: currentPage,
        size: PAGE_SIZE,
      });

      setNotices(data.content);
      setTotalPages(data.totalPages);
    } catch (e) {
      console.error(e);
      alert("공지 목록을 불러오지 못했습니다.");
    } finally {
      setLoading(false);
    }
  };

  // ✅ 목록 조회
  useEffect(() => {
    loadNotices();
  }, [buildingId, currentPage, searchKeyword]);

  // ✅ URL → state 동기화 (뒤로가기)
  useEffect(() => {
    if (pageFromUrl !== currentPage) {
      setCurrentPage(pageFromUrl);
    }
  }, [pageFromUrl]);

  if (loading) return <p>공지 불러오는 중...</p>;

  return (
    <div>
      {/* ================= 상단 ================= */}
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginBottom: "20px",
        }}
      >
        <h3 style={{ margin: 0 }}>공지 목록</h3>

        {isAdmin && (
          <button
            onClick={() =>
              navigate(`/admin/buildings/${buildingId}/notices/new`)
            }
            style={{
              padding: "10px 16px",
              borderRadius: "8px",
              border: "none",
              backgroundColor: "#2563eb",
              color: "#ffffff",
              cursor: "pointer",
              fontSize: "14px",
              fontWeight: "500",
            }}
          >
            + 공지 추가
          </button>
        )}
      </div>

      {/* ================= 목록 ================= */}
      {notices.length === 0 ? (
        <p style={{ color: "#9ca3af" }}>등록된 글이 없습니다.</p>
      ) : (
        <table
          style={{
            width: "100%",
            borderCollapse: "collapse",
            backgroundColor: "#ffffff",
            fontSize: "14px",
          }}
        >
          <thead>
            <tr>
              <th style={thStyle}>제목</th>
              <th style={{ ...thStyle, width: "140px" }}>작성자</th>
              <th style={{ ...thStyle, width: "140px" }}>등록일</th>
            </tr>
          </thead>
          <tbody>
            {notices.map((notice) => {
              // 🔥 여기만 수정됨 (관리자 / 세입자 분기)
              const detailPath = isAdmin
                ? `/admin/buildings/${buildingId}/notices/${notice.id}`
                : `/tenant/buildings/${buildingId}/notices/${notice.id}`;

              return (
                <tr
                  key={notice.id}
                  onClick={() =>
                    navigate(`${detailPath}?page=${currentPage}`)
                  }
                  style={{
                    cursor: "pointer",
                    borderBottom: "1px solid #e5e7eb",
                  }}
                >
                  <td style={tdTitleStyle}>{notice.title}</td>
                  <td style={tdStyle}>{notice.writer}</td>
                  <td style={tdStyle}>
                    {notice.createdAt?.slice(0, 10)}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      )}

      {/* ================= 페이지네이션 ================= */}
      {totalPages > 1 && (
        <div
          style={{
            display: "flex",
            justifyContent: "center",
            gap: "6px",
            margin: "32px 0",
          }}
        >
          <PageButton
            onClick={() =>
              setSearchParams(prev => {
                const p = new URLSearchParams(prev);
                p.set("page", 1);
                return p;
              })
            }
            disabled={currentPage === 1}
          >
            {"<<"}
          </PageButton>

          <PageButton
            onClick={() =>
              setSearchParams(prev => {
                const p = new URLSearchParams(prev);
                p.set("page", currentPage - 1);
                return p;
              })
            }
            disabled={currentPage === 1}
          >
            {"<"}
          </PageButton>

          {Array.from({ length: totalPages }).map((_, idx) => {
            const page = idx + 1;
            return (
              <PageButton
                key={page}
                active={currentPage === page}
                onClick={() =>
                  setSearchParams(prev => {
                    const p = new URLSearchParams(prev);
                    p.set("page", page);
                    return p;
                  })
                }
              >
                {page}
              </PageButton>
            );
          })}

          <PageButton
            onClick={() =>
              setSearchParams(prev => {
                const p = new URLSearchParams(prev);
                p.set("page", currentPage + 1);
                return p;
              })
            }
            disabled={currentPage === totalPages}
          >
            {">"}
          </PageButton>

          <PageButton
            onClick={() =>
              setSearchParams(prev => {
                const p = new URLSearchParams(prev);
                p.set("page", totalPages);
                return p;
              })
            }
            disabled={currentPage === totalPages}
          >
            {">>"}
          </PageButton>
        </div>
      )}

      {/* ================= 검색 ================= */}
      <div
        style={{
          border: "1px solid #e5e7eb",
          padding: "16px",
          display: "flex",
          justifyContent: "center",
          gap: "8px",
        }}
      >
        <select
          value={searchType}
          onChange={(e) => setSearchType(e.target.value)}
          style={{ padding: "6px 8px" }}
        >
          <option value="title">제목</option>
          <option value="content">내용</option>
        </select>

        <input
          type="text"
          placeholder="검색어를 입력하세요"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              setSearchKeyword(keyword);
              setSearchParams(prev => {
                const p = new URLSearchParams(prev);
                p.set("page", 1);
                return p;
              });
            }
          }}
          style={{
            width: "260px",
            padding: "6px 8px",
          }}
        />

        <button
          type="button"
          onClick={() => {
            setSearchKeyword(keyword);
            setSearchParams(prev => {
              const p = new URLSearchParams(prev);
              p.set("page", 1);
              return p;
            });
          }}
          style={{
            padding: "6px 20px",
            backgroundColor: "#4b5563",
            color: "#ffffff",
            border: "none",
            cursor: "pointer",
          }}
        >
          검색
        </button>
      </div>
    </div>
  );
}

/* ================= 공통 스타일 ================= */
const thStyle = {
  textAlign: "left",
  padding: "12px",
  borderBottom: "2px solid #e5e7eb",
};

const tdStyle = {
  padding: "12px",
  color: "#6b7280",
};

const tdTitleStyle = {
  ...tdStyle,
  fontWeight: "500",
  color: "#111827",
};

function PageButton({ children, onClick, disabled, active }) {
  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled}
      style={{
        minWidth: "32px",
        height: "32px",
        border: "1px solid #d1d5db",
        backgroundColor: active ? "#111827" : "#ffffff",
        color: active ? "#ffffff" : "#111827",
        cursor: disabled ? "not-allowed" : "pointer",
      }}
    >
      {children}
    </button>
  );
}
