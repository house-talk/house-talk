// src/pages/AdminLogin.jsx
import "./AdminLogin.css";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
export default function AdminLogin() {
  // Google OAuth 로그인 시작
  const handleGoogleLogin = () => {
    window.location.href =
      `${API_BASE_URL}/oauth2/authorization/google?prompt=select_account`;
  };

  return (
    <div className="admin-login-page">
      <div className="admin-login-container">
        <h1 className="login-title">LOGIN</h1>

        <p className="login-desc">
          서비스 사용을 위해 로그인이 필요합니다
        </p>

        {/* Google 로그인 버튼 */}
        <img
          src="/google-login.png"
          alt="Google 로그인"
          className="google-login-btn"
          onClick={handleGoogleLogin}
        />
      </div>
    </div>
  );
}
