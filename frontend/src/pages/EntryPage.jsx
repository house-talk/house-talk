// src/pages/EntryPage.jsx
import { useNavigate, useLocation } from "react-router-dom";
import { useState, useEffect } from "react";
import TenantEntryModal from "./tenant/TenantEntryModal";
import "./EntryPage.css";

export default function EntryPage() {
  const navigate = useNavigate();
  const location = useLocation(); // ⭐ 추가
  const [showTenantModal, setShowTenantModal] = useState(false);

  // ⭐ 보호된 페이지에서 튕겨서 온 경우 안내
  useEffect(() => {
    if (location.state?.authRequired) {
      alert("로그인이 필요한 화면입니다.");
    }
  }, [location.state]);

  // 관리자: 로그인 페이지 이동
  const handleAdminEntry = () => {
    navigate("/admin/login");
  };

  // 세입자: 모달 오픈
  const handleTenantEntry = () => {
    setShowTenantModal(true);
  };

  return (
    <div className="entry-page">
      <div className="entry-container">
        <h1 className="title">HOUSETALK</h1>
        <p className="subtitle">자취방 관리 서비스</p>

        <div className="card-wrapper">
          {/* 관리자 카드 */}
          <div className="entry-card admin">
            <h2>관리자</h2>
            <p>
              빌라 · 세대 · 공지 · 납부 내역을
              <br />
              한 곳에서 효율적으로 관리합니다
            </p>
            <button onClick={handleAdminEntry}>
              관리자로 입장
            </button>
          </div>

          {/* 세입자 카드 */}
          <div className="entry-card tenant">
            <h2>세입자</h2>
            <p>
              공지사항 확인 및
              <br />
              납부 내역을 간편하게 조회합니다
            </p>
            <button onClick={handleTenantEntry}>
              세입자로 입장
            </button>
          </div>
        </div>
      </div>

      {/* 세입자 로그인 모달 */}
      {showTenantModal && (
        <TenantEntryModal
          onClose={() => setShowTenantModal(false)}
          onSuccess={() => navigate("/tenant")}
        />
      )}
    </div>
  );
}
