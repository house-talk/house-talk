// src/App.jsx
import { Routes, Route } from "react-router-dom";
import Layout from "./layout/Layout";

import EntryPage from "./pages/EntryPage";
import AboutPage from "./pages/about/AboutPage";
import AdminLogin from "./pages/AdminLogin";
import LoginSuccess from "./pages/LoginSuccess";
import AdminDashboard from "./pages/AdminDashboard";
import BuildingDetail from "./pages/BuildingDetail";

// ⭐ tenant 홈 페이지
import TenantHomePage from "./pages/tenant/TenantHomePage";

// ⭐ tenant 건물 상세
import TenantBuildingDetail from "./pages/tenant/TenantBuildingDetail";

// ⭐ notice pages
import NoticeCreate from "./pages/notice/NoticeCreate";
import NoticeDetail from "./pages/notice/NoticeDetail";
import NoticeEdit from "./pages/notice/NoticeEdit";

// ⭐ payment detail page
import PaymentPeriodDetailPage from "./pages/payment/PaymentPeriodDetailPage";

// ⭐ 인증 가드
import RequireAdminAuth from "./auth/RequireAdminAuth";
import RequireTenantAuth from "./auth/RequireTenantAuth";

function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        {/* ⭐ 홈 */}
        <Route path="/" element={<EntryPage />} />

        {/* ⭐ 서비스 소개 */}
       <Route path="/about" element={<AboutPage />} />


        <Route path="/admin/login" element={<AdminLogin />} />
        <Route path="/login/success" element={<LoginSuccess />} />

        {/* =========================
            🔐 admin 보호 라우트
        ========================= */}
        <Route path="/admin/*" element={<RequireAdminAuth />}>
          <Route index element={<AdminDashboard />} />
          <Route path="buildings/:id" element={<BuildingDetail />} />
          <Route
            path="buildings/:buildingId/payments/:paymentPeriodId"
            element={<PaymentPeriodDetailPage />}
          />
          <Route
            path="buildings/:buildingId/notices/new"
            element={<NoticeCreate />}
          />
          <Route
            path="buildings/:buildingId/notices/:noticeId"
            element={<NoticeDetail isAdmin={true} />}
          />
          <Route
            path="buildings/:buildingId/notices/:noticeId/edit"
            element={<NoticeEdit />}
          />
        </Route>

        {/* =========================
            🔐 tenant 보호 라우트
        ========================= */}
        <Route path="/tenant/*" element={<RequireTenantAuth />}>
          <Route index element={<TenantHomePage />} />
          <Route
            path="buildings/:tenantBuildingId"
            element={<TenantBuildingDetail />}
          />
          <Route
            path="buildings/:buildingId/notices/:noticeId"
            element={<NoticeDetail />}
          />
        </Route>
      </Route>
    </Routes>
  );
}

export default App;
