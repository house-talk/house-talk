import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  fetchNoticeDetail,
  deleteNotice,
} from "../../services/noticeApi";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

/**
 * 공지 상세 페이지
 * @param {boolean} isAdmin - 관리자 여부
 */
export default function NoticeDetail({ isAdmin = false }) {
  const { buildingId, noticeId } = useParams();
  const navigate = useNavigate();

  const [notice, setNotice] = useState(null);
  const [loading, setLoading] = useState(true);

  /* =========================
     공지 상세 조회
  ========================= */
  const loadNotice = async () => {
    try {
      setLoading(true);
      const data = await fetchNoticeDetail(buildingId, noticeId);
      setNotice(data);
    } catch (e) {
      console.error(e);
      alert("공지 정보를 불러오지 못했습니다.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadNotice();
  }, [buildingId, noticeId]);

  /* =========================
     삭제 (관리자만)
  ========================= */
  const handleDelete = async () => {
    if (!window.confirm("이 공지를 삭제하시겠습니까?")) return;

    try {
      await deleteNotice(buildingId, noticeId);
      alert("삭제되었습니다.");
      navigate(-1);
    } catch (e) {
      console.error(e);
      alert("삭제 실패");
    }
  };

  if (loading) return <p>공지 불러오는 중...</p>;
  if (!notice) return <p>존재하지 않는 공지입니다.</p>;

  /* =========================
     파일 분리
  ========================= */
  const imageFiles =
    notice.files?.filter(file =>
      /\.(jpg|jpeg|png|gif|webp)$/i.test(file.name)
    ) || [];

  const otherFiles =
    notice.files?.filter(file =>
      !/\.(jpg|jpeg|png|gif|webp)$/i.test(file.name)
    ) || [];

  return (
    <div style={{ maxWidth: "900px", margin: "0 auto", position: "relative" }}>
      {/* ================= 상단 ================= */}
      <div
        style={{
          position: "relative",
          borderBottom: "2px solid #111827",
          paddingBottom: "12px",
          marginBottom: "24px",
        }}
      >
        {/* ✅ 관리자만 수정 / 삭제 가능 */}
        {isAdmin && (
          <div
            style={{
              position: "absolute",
              top: "32px",
              right: 0,
              display: "flex",
              gap: "8px",
            }}
          >
            <button
              onClick={() =>
                navigate(
                  `/admin/buildings/${buildingId}/notices/${noticeId}/edit`
                )
              }
              style={{
                padding: "8px 12px",
                borderRadius: "6px",
                border: "1px solid #d1d5db",
                background: "#ffffff",
                cursor: "pointer",
                fontSize: "13px",
              }}
            >
              수정
            </button>

            <button
              onClick={handleDelete}
              style={{
                padding: "8px 12px",
                borderRadius: "6px",
                border: "none",
                background: "#ef4444",
                color: "#ffffff",
                cursor: "pointer",
                fontSize: "13px",
              }}
            >
              삭제
            </button>
          </div>
        )}

        <h2 style={{ marginBottom: "8px" }}>{notice.title}</h2>

        <div style={{ fontSize: "14px", color: "#6b7280" }}>
          작성자: {notice.writer}
        </div>
      </div>

      {/* ================= 본문 ================= */}
      <div
        style={{
          whiteSpace: "pre-wrap",
          lineHeight: "1.7",
          fontSize: "15px",
          color: "#374151",
          marginBottom: "24px",
        }}
      >
        {notice.content}
      </div>

      {/* ================= 이미지 ================= */}
      {imageFiles.length > 0 && (
        <div style={{ marginBottom: "32px" }}>
          {imageFiles.map((file, idx) => (
            <img
              key={idx}
              src={`${API_BASE_URL}${file.url}`}
              alt={file.name}
              style={{
                maxWidth: "100%",
                display: "block",
                margin: "0 auto 16px",
                borderRadius: "8px",
                border: "1px solid #e5e7eb",
              }}
            />
          ))}
        </div>
      )}

      {/* ================= 첨부파일 ================= */}
      {otherFiles.length > 0 && (
        <div style={{ marginTop: "40px" }}>
          <div
            style={{
              backgroundColor: "#2f2f2f",
              color: "#ffffff",
              padding: "10px 14px",
              fontSize: "14px",
              fontWeight: "500",
            }}
          >
            첨부파일
          </div>

          <div
            style={{
              backgroundColor: "#f5f5f5",
              padding: "16px",
            }}
          >
            {otherFiles.map((file, idx) => (
              <div
                key={idx}
                style={{
                  display: "flex",
                  alignItems: "center",
                  gap: "8px",
                  marginBottom: "8px",
                  fontSize: "14px",
                }}
              >
                <span>⬇</span>
                <a
                  href={`${API_BASE_URL}${file.url}`}
                  download={file.name}
                  style={{
                    color: "#1d4ed8",
                    textDecoration: "none",
                  }}
                >
                  {file.name}
                </a>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ================= 목록 버튼 ================= */}
      <div
        style={{
          display: "flex",
          justifyContent: "flex-end",
          marginTop: "32px",
        }}
      >
        <button
          onClick={() => navigate(-1)}
          style={{
            padding: "8px 14px",
            borderRadius: "6px",
            border: "1px solid #d1d5db",
            background: "#ffffff",
            cursor: "pointer",
            fontSize: "13px",
          }}
        >
          목록
        </button>
      </div>
    </div>
  );
}
