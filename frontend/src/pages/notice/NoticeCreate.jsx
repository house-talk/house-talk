import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { createNotice } from "../../services/noticeApi";

/**
 * 공지 생성 페이지 (파일 업로드 방식)
 */
export default function NoticeCreate() {
  const { buildingId } = useParams();
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [files, setFiles] = useState([]);
  const [submitting, setSubmitting] = useState(false);

  /* =========================
     파일 선택
  ========================= */
  const handleFileChange = e => {
    setFiles(Array.from(e.target.files));
  };

  /* =========================
     제출
  ========================= */
  const handleSubmit = async () => {
    if (!title.trim() || !content.trim()) {
      alert("제목과 내용을 입력해주세요.");
      return;
    }

    try {
      setSubmitting(true);

      // ✅ FormData 구성 (@ModelAttribute 대응)
      const formData = new FormData();
      formData.append("title", title);
      formData.append("content", content);

      files.forEach(file => {
        formData.append("files", file); // ⭐ DTO 필드명과 동일
      });

      await createNotice(buildingId, formData);

      alert("공지 등록 완료");
      navigate(`/admin/buildings/${buildingId}?tab=notices`);
    } catch (e) {
      console.error(e);
      alert("공지 등록 실패");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={{ maxWidth: "900px", margin: "0 auto" }}>
      <h2 style={{ marginBottom: "20px" }}>공지 추가</h2>

      {/* ================= 제목 ================= */}
      <input
        placeholder="제목"
        value={title}
        onChange={e => setTitle(e.target.value)}
        style={{
          width: "100%",
          padding: "12px",
          marginBottom: "12px",
          borderRadius: "8px",
          border: "1px solid #d1d5db",
          fontSize: "15px",
        }}
      />

      {/* ================= 내용 ================= */}
      <textarea
        placeholder="내용"
        value={content}
        onChange={e => setContent(e.target.value)}
        rows={10}
        style={{
          width: "100%",
          padding: "12px",
          marginBottom: "20px",
          borderRadius: "8px",
          border: "1px solid #d1d5db",
          resize: "vertical",
          fontSize: "14px",
        }}
      />

      {/* ================= 파일 업로드 ================= */}
      <div style={{ marginBottom: "24px" }}>
        <h4 style={{ marginBottom: "8px" }}>
          파일 첨부 (이미지 / PDF)
        </h4>

        <input
          type="file"
          multiple
          accept="image/*,application/pdf"
          onChange={handleFileChange}
        />

        {files.length > 0 && (
          <ul style={{ marginTop: "10px", fontSize: "14px" }}>
            {files.map((file, idx) => (
              <li key={idx}>{file.name}</li>
            ))}
          </ul>
        )}
      </div>

      {/* ================= 버튼 ================= */}
      <div style={{ display: "flex", gap: "8px" }}>
        <button
          onClick={() => navigate(-1)}
          style={{
            padding: "10px 16px",
            borderRadius: "8px",
            border: "1px solid #d1d5db",
            background: "#ffffff",
            cursor: "pointer",
          }}
        >
          취소
        </button>

        <button
          onClick={handleSubmit}
          disabled={submitting}
          style={{
            padding: "10px 16px",
            borderRadius: "8px",
            border: "none",
            background: "#2563eb",
            color: "#ffffff",
            cursor: "pointer",
            opacity: submitting ? 0.6 : 1,
          }}
        >
          {submitting ? "등록 중..." : "등록"}
        </button>
      </div>
    </div>
  );
}
