// src/pages/AdminLogin.jsx
import "./AdminLogin.css";

export default function AdminLogin() {
  const handleGoogleLogin = () => {
    const OAUTH_BASE = import.meta.env.VITE_OAUTH_BASE ?? "";
    window.location.href =
      `${OAUTH_BASE}/oauth2/authorization/google?prompt=select_account`;
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
