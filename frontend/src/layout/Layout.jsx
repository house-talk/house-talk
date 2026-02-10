import { Outlet, useNavigate, useLocation } from "react-router-dom";
import { useState } from "react";

export default function Layout() {
  const navigate = useNavigate();
  const location = useLocation();
  const [isContactOpen, setIsContactOpen] = useState(false);

  // ⭐ 로고 클릭 처리
  const handleLogoClick = async () => {
    if (location.pathname.startsWith("/tenant")) {
      try {
        const res = await fetch("/api/tenant/homes", {
          credentials: "include",
        });

        if (res.ok) {
          navigate("/tenant");
          return;
        }
      } catch (e) {}

      navigate("/");
      return;
    }

    navigate("/");
  };

  return (
    <div
      className="app-root"
      style={{
        minHeight: "100vh",
        display: "flex",
        flexDirection: "column",
      }}
    >
      {/* ===== Header ===== */}
      <header style={styles.header}>
        <div style={styles.headerInner}>
          {/* 🔹 로고 */}
          <span onClick={handleLogoClick} style={styles.logo}>
            HOUSETALK
          </span>

          <nav style={styles.nav}>
            <button
              className="nav-button"
              style={styles.navButton}
              onClick={() => navigate("/about")}
            >
              서비스 소개
            </button>

            <button
              className="nav-button"
              style={styles.navButton}
              onClick={() => setIsContactOpen(true)}
            >
              문의하기
            </button>
          </nav>
        </div>
      </header>

      {/* ===== Main ===== */}
      <main style={styles.main}>
        <Outlet />
      </main>

      {/* ===== Footer ===== */}
      <footer style={styles.footer}>
        <p style={{ color: "#ffffff" }}>
          © 2026 HOUSETALK. All rights reserved.
        </p>
      </footer>

      {/* ===== Contact Modal ===== */}
      {isContactOpen && (
        <div style={styles.modalOverlay} onClick={() => setIsContactOpen(false)}>
          <div
            style={styles.modal}
            onClick={(e) => e.stopPropagation()}
          >
            <h2 style={styles.modalTitle}>문의하기</h2>
            <p style={styles.modalText}>
              서비스 관련 문의는 아래 이메일을 통해 연락해 주세요.
              <br />
              확인 후 순차적으로 답변드리겠습니다.
            </p>

            <div style={styles.emailBox}>
              ich053012@naver.com
            </div>

            <button
              style={styles.closeButton}
              onClick={() => setIsContactOpen(false)}
            >
              닫기
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

/* ===== Styles ===== */
const styles = {
  header: {
    width: "100%",
    height: "64px",
    backgroundColor: "#ffffff",
    borderBottom: "1px solid #e5e7eb",
    position: "fixed",
    top: 0,
    left: 0,
    zIndex: 10,
  },

  headerInner: {
    maxWidth: "1200px",
    height: "100%",
    margin: "0 auto",
    padding: "0 24px",
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
  },

  logo: {
    fontSize: "18px",
    fontWeight: 700,
    letterSpacing: "-0.02em",
    color: "#111827",
    cursor: "pointer",
  },

  nav: {
    display: "flex",
    gap: "24px",
  },

  navButton: {
    background: "none",
    border: "none",
    padding: 0,
    fontSize: "14px",
  },

  main: {
    flex: 1,
    paddingTop: "64px",
    background: `
      radial-gradient(
        circle at top,
        #eef3f8 0%,
        #f5f7fa 60%,
        #f5f7fa 100%
      )
    `,
  },

  footer: {
    width: "100%",
    padding: "12px 0",
    backgroundColor: "#1f4fa3",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontSize: "12px",
    color: "#ffffff",
  },

  /* ===== Modal ===== */
  modalOverlay: {
    position: "fixed",
    top: 0,
    left: 0,
    width: "100vw",
    height: "100vh",
    backgroundColor: "rgba(0,0,0,0.4)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    zIndex: 50,
  },

  modal: {
    width: "400px",
    backgroundColor: "#ffffff",
    borderRadius: "12px",
    padding: "24px",
    boxShadow: "0 20px 40px rgba(0,0,0,0.15)",
    textAlign: "center",
  },

  modalTitle: {
    fontSize: "20px",
    fontWeight: 600,
    marginBottom: "12px",
  },

  modalText: {
    fontSize: "14px",
    color: "#374151",
    lineHeight: 1.6,
    marginBottom: "16px",
  },

  emailBox: {
    padding: "12px",
    backgroundColor: "#f3f4f6",
    borderRadius: "8px",
    fontSize: "14px",
    fontWeight: 500,
    marginBottom: "20px",
  },

  closeButton: {
    width: "100%",
    padding: "10px 0",
    border: "none",
    borderRadius: "8px",
    backgroundColor: "#2563eb",
    color: "#ffffff",
    fontSize: "14px",
    fontWeight: 600,
    cursor: "pointer",
  },
};
