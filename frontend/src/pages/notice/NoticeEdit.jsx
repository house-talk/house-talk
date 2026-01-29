import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  fetchNoticeDetail,
  updateNotice,
  deleteNotice,
} from "../../services/noticeApi";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export default function NoticeEdit() {
  const { buildingId, noticeId } = useParams();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");

  const [existingFiles, setExistingFiles] = useState([]);
  const [deleteImageIds, setDeleteImageIds] = useState([]);
  const [newFiles, setNewFiles] = useState([]);

  const [previewImage, setPreviewImage] = useState(null);

  const isImageFile = (name) =>
    /\.(jpg|jpeg|png|gif|webp)$/i.test(name);

  /* =========================
     기존 공지 불러오기
  ========================= */
  useEffect(() => {
    const loadNotice = async () => {
      try {
        setLoading(true);
        const notice = await fetchNoticeDetail(buildingId, noticeId);
        setTitle(notice.title);
        setContent(notice.content);
        setExistingFiles(notice.files || []);
      } catch (e) {
        console.error(e);
        alert("공지 정보를 불러오지 못했습니다.");
        navigate(-1);
      } finally {
        setLoading(false);
      }
    };

    loadNotice();
  }, [buildingId, noticeId, navigate]);

  /* =========================
     기존 파일 삭제
  ========================= */
  const handleRemoveExisting = (imageId) => {
    if (!imageId) return;

    setDeleteImageIds((prev) => [...prev, imageId]);
    setExistingFiles((prev) =>
      prev.filter((file) => file.imageId !== imageId)
    );
  };

  /* =========================
     새 파일 추가 / 삭제
  ========================= */
  const handleNewFileChange = (e) => {
    setNewFiles((prev) => [...prev, ...Array.from(e.target.files)]);
  };

  const handleRemoveNewFile = (index) => {
    setNewFiles((prev) => prev.filter((_, i) => i !== index));
  };

  /* =========================
     수정 제출
  ========================= */
  const handleSubmit = async () => {
    if (!title.trim() || !content.trim()) {
      alert("제목과 내용을 입력해주세요.");
      return;
    }

    try {
      setSubmitting(true);

      const formData = new FormData();
      formData.append("title", title);
      formData.append("content", content);

      newFiles.forEach((file) => {
        formData.append("files", file);
      });

      deleteImageIds.forEach((id) => {
        formData.append("deleteImageIds", id);
      });

      await updateNotice(buildingId, noticeId, formData);

      alert("공지 수정 완료");
      navigate(`/admin/buildings/${buildingId}?tab=notices`);
    } catch (e) {
      console.error(e);
      alert("공지 수정 실패");
    } finally {
      setSubmitting(false);
    }
  };

  /* =========================
     공지 삭제
  ========================= */
  const handleDelete = async () => {
    if (!window.confirm("정말 이 공지를 삭제하시겠습니까?")) return;

    try {
      setSubmitting(true);
      await deleteNotice(buildingId, noticeId);
      alert("공지 삭제 완료");
      navigate(`/admin/buildings/${buildingId}?tab=notices`);
    } catch (e) {
      console.error(e);
      alert("공지 삭제 실패");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <p style={{ padding: "40px" }}>공지 불러오는 중...</p>;
  }

  const imageFiles = existingFiles.filter(file =>
  isImageFile(file.name)
);

const otherFiles = existingFiles.filter(file =>
  !isImageFile(file.name)
);


  return (
    <>
      <div style={{ maxWidth: "800px", margin: "0 auto", padding: "40px" }}>
        <h2 style={{ marginBottom: "24px" }}>공지 수정</h2>

        {/* 제목 */}
        <input
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="제목"
          style={{
            width: "100%",
            padding: "12px",
            borderRadius: "8px",
            border: "1px solid #d1d5db",
            marginBottom: "12px",
          }}
        />

        {/* 내용 */}
        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="내용"
          rows={8}
          style={{
            width: "100%",
            padding: "12px",
            borderRadius: "8px",
            border: "1px solid #d1d5db",
            resize: "vertical",
          }}
        />

        {/* 기존 파일 */}
{existingFiles.length > 0 && (
  <div style={{ marginTop: "20px" }}>
    <h4>기존 파일</h4>

    {/* 🖼 이미지 파일 (위) */}
    <div style={{ display: "flex", gap: "12px", flexWrap: "wrap" }}>
      {existingFiles
        .filter((file) => isImageFile(file.name))
        .map((file) => (
          <div
            key={file.imageId}
            style={{
              position: "relative",
              width: "120px",
              height: "120px",
              borderRadius: "8px",
              overflow: "hidden",
              border: "1px solid #e5e7eb",
            }}
          >
            <img
              src={`${API_BASE_URL}${file.url}`}
              alt={file.name}
              onClick={() =>
                setPreviewImage(`${API_BASE_URL}${file.url}`)
              }
              style={{
                width: "100%",
                height: "100%",
                objectFit: "cover",
                cursor: "pointer",
              }}
            />

            <button
              type="button"
              onClick={(e) => {
                e.stopPropagation();
                handleRemoveExisting(file.imageId);
              }}
              style={{
                position: "absolute",
                top: "4px",
                right: "4px",
                width: "20px",
                height: "20px",
                borderRadius: "50%",
                border: "none",
                background: "rgba(0,0,0,0.6)",
                color: "#fff",
                cursor: "pointer",
              }}
            >
              ×
            </button>
          </div>
        ))}
    </div>

    {/* 📄 PDF / 기타 파일 (아래) */}
    <div style={{ marginTop: "12px" }}>
      {existingFiles
        .filter((file) => !isImageFile(file.name))
        .map((file) => (
          <div
            key={file.imageId}
            style={{
              display: "flex",
              alignItems: "center",
              gap: "6px",
              marginBottom: "6px",
            }}
          >
            <a
              href={`${API_BASE_URL}${file.url}`}
              target="_blank"
              rel="noreferrer"
            >
              📄 {file.name}
            </a>
            <button
              onClick={() => handleRemoveExisting(file.imageId)}
              style={{
                border: "none",
                background: "transparent",
                cursor: "pointer",
              }}
            >
              ×
            </button>
          </div>
        ))}
    </div>
  </div>
)}


        {/* 새 파일 추가 */}
<div style={{ marginTop: "20px" }}>
  <h4>파일 추가</h4>
  <input
    type="file"
    multiple
    accept="image/*,application/pdf"
    onChange={handleNewFileChange}
  />

  {/* 🔥 새로 추가한 파일 목록 (미리보기 없음) */}
  {newFiles.length > 0 && (
    <div style={{ marginTop: "10px" }}>
      {newFiles.map((file, idx) => (
        <div
          key={idx}
          style={{
            display: "flex",
            alignItems: "center",
            gap: "6px",
            marginBottom: "6px",
            fontSize: "14px",
          }}
        >
          <span>{file.name}</span>
          <button
            type="button"
            onClick={() => handleRemoveNewFile(idx)}
            style={{
              border: "none",
              background: "transparent",
              cursor: "pointer",
              fontSize: "14px",
            }}
          >
            ×
          </button>
        </div>
      ))}
    </div>
  )}
</div>


        {/* 버튼 */}
        <div style={{ display: "flex", gap: "12px", marginTop: "32px" }}>
          <button
            onClick={handleSubmit}
            disabled={submitting}
            style={{
              padding: "10px 16px",
              borderRadius: "8px",
              border: "none",
              background: "#2563eb",
              color: "#fff",
              cursor: "pointer",
              opacity: submitting ? 0.6 : 1,
            }}
          >
            {submitting ? "저장 중..." : "수정 완료"}
          </button>

          <button
            onClick={handleDelete}
            disabled={submitting}
            style={{
              padding: "10px 16px",
              borderRadius: "8px",
              border: "none",
              background: "#ef4444",
              color: "#fff",
              cursor: "pointer",
              opacity: submitting ? 0.6 : 1,
            }}
          >
            삭제
          </button>

          <button
            onClick={() => navigate(`/admin/buildings/${buildingId}?tab=notices`)}
            style={{
              padding: "10px 16px",
              borderRadius: "8px",
              border: "1px solid #d1d5db",
              background: "#fff",
              cursor: "pointer",
            }}
          >
            취소
          </button>
        </div>
      </div>

      {/* 이미지 미리보기 */}
      {previewImage && (
        <div
          onClick={() => setPreviewImage(null)}
          style={{
            position: "fixed",
            inset: 0,
            background: "rgba(0,0,0,0.7)",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            zIndex: 1000,
          }}
        >
          <img
            src={previewImage}
            alt="미리보기"
            style={{ maxWidth: "90vw", maxHeight: "90vh" }}
          />
        </div>
      )}
    </>
  );
}
