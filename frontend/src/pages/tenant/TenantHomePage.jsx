import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom"; // ⭐ 추가
import TenantInviteModal from "../../components/tenant/TenantInviteModal";
import {
  fetchMyHouses,
  fetchTenantMe,
} from "../../services/tenantHouseApi";


export default function TenantHomePage() {
  const navigate = useNavigate(); // ⭐ 추가

  const [isInviteModalOpen, setIsInviteModalOpen] = useState(false);
  const [houses, setHouses] = useState([]);
  const [tenantName, setTenantName] = useState("");

  // ⭐ 인증 만료 모달
  const [isAuthExpiredModalOpen, setIsAuthExpiredModalOpen] = useState(false);

  useEffect(() => {
    let cancelled = false;

    fetchMyHouses()
      .then((data) => {
        if (cancelled) return;
        setHouses(data);
        return fetchTenantMe();
      })
      .then((data) => {
        if (cancelled || !data) return;
        setTenantName(data.name);
      })
      .catch(() => {
        if (cancelled) return;
        // ⭐ 인증 만료 → 모달 표시
        setIsAuthExpiredModalOpen(true);
      });

    return () => {
      cancelled = true;
    };
  }, []);

  // ⭐ 로그아웃
  const handleLogout = async () => {
    try {
      await fetch(`/api/tenant/logout`, {
        method: "POST",
        credentials: "include",
      });
    } finally {
      window.location.href = "/";
    }
  };

  // ⭐ 인증 만료 모달 확인
  const handleAuthExpiredConfirm = () => {
    window.location.href = "/";
  };

  return (
    <div style={{ maxWidth: "720px", margin: "0 auto", padding: "40px 24px" }}>
      {/* ===== 상단 헤더 ===== */}
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginBottom: "24px",
        }}
      >
        <div>
          <h2 style={{ marginBottom: "6px" }}>
            {tenantName
              ? `${tenantName}님, 하우스톡에 오신 걸 환영합니다`
              : "하우스톡"}
          </h2>
          <p style={{ color: "#6b7280", fontSize: "14px" }}>
            내가 살고 있는 집
          </p>
        </div>

        <button
          onClick={handleLogout}
          style={{
            background: "none",
            border: "none",
            color: "#6b7280",
            fontSize: "14px",
            cursor: "pointer",
          }}
        >
          로그아웃
        </button>
      </div>

      {/* ===== 집 목록 ===== */}
      <div style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
        {houses.length === 0 ? (
          <p style={{ color: "#6b7280", fontSize: "14px" }}>
            아직 등록된 집이 없습니다.
          </p>
        ) : (
          houses.map((house) => (
            <div
              key={house.tenantBuildingId}
              onClick={() => navigate(`/tenant/buildings/${house.tenantBuildingId}`)} // ⭐ 수정
              style={{
                padding: "16px",
                borderRadius: "12px",
                backgroundColor: "#ffffff",
                boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
                cursor: "pointer",
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
              }}
            >
              <div>
                <div style={{ fontWeight: "600", fontSize: "16px" }}>
                  {house.buildingName}
                </div>
                <div style={{ color: "#6b7280", fontSize: "14px" }}>
                  {house.unitNumber}호
                </div>
              </div>
              <span style={{ color: "#9ca3af", fontSize: "18px" }}>›</span>
            </div>
          ))
        )}
      </div>

      <button
        onClick={() => setIsInviteModalOpen(true)}
        style={{
          marginTop: "24px",
          width: "100%",
          padding: "14px",
          borderRadius: "12px",
          border: "2px dashed #c7d2fe",
          backgroundColor: "#eef2ff",
          color: "#4338ca",
          fontWeight: "600",
          cursor: "pointer",
        }}
      >
        + 초대코드로 집 추가하기
      </button>

      {isInviteModalOpen && (
        <TenantInviteModal onClose={() => setIsInviteModalOpen(false)} />
      )}

      {/* ===== 인증 만료 모달 ===== */}
      {isAuthExpiredModalOpen && (
        <div
          style={{
            position: "fixed",
            inset: 0,
            backgroundColor: "rgba(0,0,0,0.4)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            zIndex: 100,
          }}
        >
          <div
            style={{
              width: "320px",
              backgroundColor: "#ffffff",
              borderRadius: "12px",
              padding: "24px",
              textAlign: "center",
            }}
          >
            <h3 style={{ marginBottom: "12px" }}>로그인 만료</h3>
            <p
              style={{
                fontSize: "14px",
                color: "#6b7280",
                marginBottom: "20px",
              }}
            >
              로그인 정보가 만료되었습니다.
              <br />
              다시 로그인해주세요.
            </p>
            <button
              onClick={handleAuthExpiredConfirm}
              style={{
                width: "100%",
                padding: "10px",
                borderRadius: "8px",
                border: "none",
                backgroundColor: "#2563eb",
                color: "#ffffff",
                fontWeight: "600",
                cursor: "pointer",
              }}
            >
              확인
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
