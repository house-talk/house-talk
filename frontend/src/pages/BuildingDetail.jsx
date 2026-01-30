import { useParams, useNavigate, useSearchParams } from "react-router-dom";
import { useEffect, useState } from "react";
import UnitList from "../components/unit/UnitList";
import { createInvite, fetchInvite } from "../services/inviteApi";
import NoticeList from "./notice/NoticeList";
import PaymentPeriodListPage from "./payment/PaymentPeriodListPage";
import { fetchBuilding } from "../services/buildingApi";

import {
  fetchPendingTenants,
  approveTenant,
  rejectTenant,
} from "../services/adminTenantApi";
import { FaCopy } from "react-icons/fa";

export default function BuildingDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [unitRefreshKey, setUnitRefreshKey] = useState(0);

  const [building, setBuilding] = useState(null);
  const [loading, setLoading] = useState(true);
  const [searchParams, setSearchParams] = useSearchParams();
  const activeTab = searchParams.get("tab") || "units";

  // 승인/거절 버튼 로딩 상태
  const [actionLoadingId, setActionLoadingId] = useState(null);

  // 초대코드 모달
  const [isInviteModalOpen, setIsInviteModalOpen] = useState(false);
  const [inviteCode, setInviteCode] = useState(null);

  // 승인 요청 상태
  const [pendingTenants, setPendingTenants] = useState([]);

  useEffect(() => {
  let mounted = true;

  async function loadBuilding() {
    try {
      const data = await fetchBuilding(id);
      if (mounted) {
        setBuilding(data);
      }
    } catch (e) {
      console.error(e);
      if (mounted) {
        setBuilding(null);
      }
    } finally {
      if (mounted) {
        setLoading(false);
      }
    }
  }

  loadBuilding();

  return () => {
    mounted = false;
  };
}, [id]);


  // 승인 요청 조회
  useEffect(() => {
    if (!building) return;

    fetchPendingTenants(building.id)
      .then(setPendingTenants)
      .catch(() => setPendingTenants([]));
  }, [building]);

  const openInviteModal = async () => {
    setIsInviteModalOpen(true);
    try {
      const data = await fetchInvite(building.id);
      setInviteCode(data.inviteCode ?? null);
    } catch (e) {
      setInviteCode(null);
    }
  };

  if (loading) return <p style={{ padding: "40px" }}>불러오는 중...</p>;
  if (!building) return <p style={{ padding: "40px" }}>건물을 찾을 수 없습니다.</p>;

  return (
    <div style={{ position: "relative" }}>
      <div style={{ maxWidth: "1200px", margin: "0 auto", padding: "24px 24px 40px" }}>
        {/* ================= 상단 건물 정보 ================= */}
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "flex-start",
            marginBottom: "24px",
          }}
        >
          <div>
            <h1 style={{ marginBottom: "8px" }}>{building.name}</h1>
            <p style={{ color: "#6b7280", marginBottom: "16px" }}>
              {building.address}
            </p>

            <div style={{ display: "flex", gap: "32px" }}>
              <div>
                <div style={{ fontSize: "20px", fontWeight: "600" }}>
                  {building.totalFloors}
                </div>
                <div style={{ fontSize: "12px", color: "#6b7280" }}>총 층수</div>
              </div>

              <div>
                <div style={{ fontSize: "20px", fontWeight: "600" }}>
                  {building.totalUnits}
                </div>
                <div style={{ fontSize: "12px", color: "#6b7280" }}>총 세대</div>
              </div>
            </div>
          </div>

          <div style={{ display: "flex", flexDirection: "column", alignItems: "flex-end", gap: "12px" }}>
            <button
              onClick={() => navigate("/admin")}
              style={{
                background: "none",
                border: "none",
                color: "#2563eb",
                cursor: "pointer",
                fontSize: "14px",
              }}
            >
              ← 대시보드로 돌아가기
            </button>

            <button
              onClick={openInviteModal}
              style={{
                marginTop: "77px",
                padding: "10px 16px",
                borderRadius: "8px",
                border: "none",
                backgroundColor: "#2563eb",
                color: "#ffffff",
                cursor: "pointer",
                fontSize: "14px",
                fontWeight: "500",
              }}
            >
              초대코드 관리
            </button>
          </div>
        </div>

        {/* ================= 탭 영역 ================= */}
        <div
          style={{
            borderRadius: "16px",
            backgroundColor: "#ffffff",
            boxShadow: "0 4px 16px rgba(0,0,0,0.08)",
            overflow: "hidden",
          }}
        >
          <div style={{ display: "flex", borderBottom: "1px solid #e5e7eb" }}>
            {[
              { key: "units", label: "세대 관리" },
              { key: "notices", label: "공지 관리" },
              { key: "payments", label: "납부 관리" },
            ].map(tab => (
              <button
                key={tab.key}
                onClick={() =>
                  setSearchParams(prev => {
                    const params = new URLSearchParams(prev);
                    params.set("tab", tab.key);
                    return params;
                  })
                }
                style={{
                  flex: 1,
                  padding: "16px",
                  background: "none",
                  border: "none",
                  cursor: "pointer",
                  fontSize: "14px",
                  fontWeight: activeTab === tab.key ? "600" : "400",
                  color: activeTab === tab.key ? "#2563eb" : "#6b7280",
                  borderBottom:
                    activeTab === tab.key
                      ? "2px solid #2563eb"
                      : "2px solid transparent",
                }}
              >
                {tab.label}
              </button>
            ))}
          </div>

          <div style={{ padding: "24px" }}>
            {activeTab === "units" && (
              <UnitList key={unitRefreshKey} buildingId={building.id} />
            )}
            {activeTab === "notices" && (
              <NoticeList buildingId={building.id} isAdmin={true} />
            )}
            {activeTab === "payments" && (
              <PaymentPeriodListPage buildingId={building.id} />
            )}
          </div>
        </div>
      </div>

      {/* ================= 오른쪽 승인 요청 ================= */}
      <aside
        style={{
          position: "absolute",
          top: "190px",
          right: "20px",
          width: "260px",
          background: "#ffffff",
          borderRadius: "16px",
          boxShadow: "0 4px 16px rgba(0,0,0,0.08)",
          padding: "16px",
        }}
      >
        <h3 style={{ marginBottom: "12px" }}>승인 요청</h3>

        {pendingTenants.length === 0 ? (
          <p style={{ fontSize: "14px", color: "#9ca3af" }}>
            승인 대기 중인 요청이 없습니다.
          </p>
        ) : (
          pendingTenants.map(t => (
            <div key={t.requestId} style={{ marginBottom: "16px" }}>
              <strong>{t.unitNumber}호</strong>
              <div style={{ fontSize: "14px", fontWeight: "600", marginTop: "4px" }}>
                {t.name}
              </div>
              <div style={{ fontSize: "13px", color: "#6b7280" }}>
                {t.phoneNumber}
              </div>

              <div style={{ display: "flex", gap: "8px", marginTop: "8px" }}>
                <button
                  disabled={actionLoadingId === t.requestId}
                  onClick={async () => {
                    try {
                      setActionLoadingId(t.requestId);
                      await approveTenant(t.requestId);
                      alert("승인되었습니다.");
                      setPendingTenants(prev =>
                        prev.filter(p => p.requestId !== t.requestId)
                      );
                      setUnitRefreshKey(k => k + 1);
                    } catch (e) {
                      if (e.status === 400) {
                        alert("이미 해당 호수에 입주한 세입자가 있습니다.");
                      } else if (e.status === 401 || e.status === 403) {
                        alert("승인 권한이 없습니다.");
                      } else {
                        alert("승인 처리 중 오류가 발생했습니다.");
                      }
                    } finally {
                      setActionLoadingId(null);
                    }
                  }}
                  style={{
                    flex: 1,
                    background: "#10b981",
                    color: "#fff",
                    border: "none",
                    borderRadius: "6px",
                    padding: "6px",
                    cursor: "pointer",
                  }}
                >
                  승인
                </button>

                <button
                  disabled={actionLoadingId === t.requestId}
                  onClick={async () => {
                    try {
                      setActionLoadingId(t.requestId);
                      await rejectTenant(t.requestId);
                      setPendingTenants(prev =>
                        prev.filter(p => p.requestId !== t.requestId)
                      );
                    } finally {
                      setActionLoadingId(null);
                    }
                  }}
                  style={{
                    flex: 1,
                    background: "#ef4444",
                    color: "#fff",
                    border: "none",
                    borderRadius: "6px",
                    padding: "6px",
                    cursor: "pointer",
                  }}
                >
                  거절
                </button>
              </div>
            </div>
          ))
        )}
      </aside>

      {/* ================= 초대코드 모달 ================= */}
      {isInviteModalOpen && (
        <div
          style={{
            position: "fixed",
            inset: 0,
            backgroundColor: "rgba(0,0,0,0.4)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            zIndex: 1000,
          }}
        >
          <div style={{ background: "#fff", borderRadius: "12px", width: "360px", padding: "24px" }}>
            <h3 style={{ marginBottom: "16px" }}>초대코드 관리</h3>

            {!inviteCode ? (
              <p style={{ marginBottom: "24px", color: "#6b7280" }}>
                아직 발급된 초대코드가 없습니다.
              </p>
            ) : (
              <div style={{ display: "flex", gap: "8px", marginBottom: "24px" }}>
                <input
                  value={inviteCode}
                  readOnly
                  style={{
                    flex: 1,
                    padding: "10px 12px",
                    borderRadius: "8px",
                    border: "1px solid #d1d5db",
                    backgroundColor: "#f9fafb",
                    fontWeight: "600",
                  }}
                />
                <button
                  onClick={() => navigator.clipboard.writeText(inviteCode)}
                  style={{
                    padding: "10px",
                    borderRadius: "8px",
                    border: "1px solid #d1d5db",
                    backgroundColor: "#ffffff",
                    cursor: "pointer",
                  }}
                >
                  <FaCopy size={16} />
                </button>
              </div>
            )}

            <div style={{ display: "flex", gap: "12px" }}>
              <button
                onClick={async () => {
                  const data = await createInvite(building.id);
                  setInviteCode(data.inviteCode);
                }}
                style={{
                  flex: 1,
                  padding: "10px",
                  borderRadius: "8px",
                  border: "none",
                  backgroundColor: "#2563eb",
                  color: "#ffffff",
                  cursor: "pointer",
                }}
              >
                초대코드 발급
              </button>

              <button
                onClick={() => setIsInviteModalOpen(false)}
                style={{
                  flex: 1,
                  padding: "10px",
                  borderRadius: "8px",
                  border: "1px solid #d1d5db",
                  backgroundColor: "#ffffff",
                  cursor: "pointer",
                }}
              >
                닫기
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
