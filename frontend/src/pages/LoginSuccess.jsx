// src/pages/LoginSuccess.jsx
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
export default function LoginSuccess() {
  const navigate = useNavigate();

  useEffect(() => {
    // ✅ 쿠키 기반 인증 확인
    fetch(`${API_BASE_URL}/api/admin/me`, {
      credentials: "include", // ⭐ 쿠키 인증 핵심
    })
      .then((res) => {
        if (!res.ok) {
          throw new Error("인증 실패");
        }
        return res.json();
      })
      .then(() => {
        // 인증 성공 → 관리자 대시보드
        navigate("/admin", { replace: true });
      })
      .catch(() => {
        // 인증 실패 → 진입 페이지
        navigate("/", { replace: true });
      });
  }, [navigate]);

  return (
    <div style={{ padding: "40px", textAlign: "center" }}>
      <p>로그인 처리 중입니다...</p>
    </div>
  );
}
