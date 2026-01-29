import { Outlet, useNavigate, useLocation } from "react-router-dom";

export default function Layout() {
  const navigate = useNavigate();
  const location = useLocation();

  // ⭐ 로고 클릭 처리 (쿠키 만료 대응)
  const handleLogoClick = async () => {
    // 세입자 영역에 있을 때만 인증 확인
    if (location.pathname.startsWith("/tenant")) {
      try {
        const res = await fetch("/api/tenant/homes", {
          credentials: "include",
        });

        if (res.ok) {
          navigate("/tenant"); // ✅ 쿠키 유효
          return;
        }
      } catch (e) {
        // 네트워크 에러 포함 → EntryPage로
      }

      // ❌ 쿠키 만료 / 인증 실패
      navigate("/");
      return;
    }

    // 그 외 영역은 그냥 EntryPage
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
          {/* 🔹 로고 클릭 */}
          <span
            onClick={handleLogoClick}
            style={styles.logo}
          >
            HOUSETALK
          </span>

          <nav style={styles.nav}>
            <a href="#" style={styles.navItem}>서비스 소개</a>
            <a href="#" style={styles.navItem}>문의하기</a>
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
          © 2024 HOUSETALK. All rights reserved. · 이용약관 · 개인정보처리방침
        </p>
      </footer>
    </div>
  );
}

/* ===== Layout Styles ===== */
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
    textDecoration: "none",
    cursor: "pointer",
  },

  nav: {
    display: "flex",
    gap: "24px",
  },

  navItem: {
    fontSize: "14px",
    color: "#6b7280",
    textDecoration: "none",
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
};
