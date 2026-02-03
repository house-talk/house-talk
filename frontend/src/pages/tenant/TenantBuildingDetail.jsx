import { useEffect, useState } from "react";
import { useParams, useNavigate, useSearchParams } from "react-router-dom";
import NoticeList from "../notice/NoticeList";


/**
 * 세입자 전용 건물 상세 페이지
 * - 공지 확인
 * - (추후) 민원 탭
 */
export default function TenantBuildingDetail() {
  const { tenantBuildingId } = useParams();
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  const activeTab = searchParams.get("tab") || "notices";

  const [building, setBuilding] = useState(null);
  const [loading, setLoading] = useState(true);

  /* =========================
     세입자 건물 상세 조회
  ========================= */
 useEffect(() => {
  let cancelled = false;

  const loadBuilding = async () => {
    try {
      setLoading(true);

      const res = await fetch(
        `/api/tenant/buildings/${tenantBuildingId}`,
        {
          credentials: "include",
        }
      );

      // ✅ 인증 / 권한 / 잘못된 접근은 RequireAuth가 처리
      if (res.status === 401 || res.status === 403 || res.status === 400) {
        return;
      }

      if (!res.ok) {
        throw new Error("FETCH_FAILED");
      }

      const data = await res.json();
      if (cancelled) return;

      setBuilding({
        id: data.buildingId,
        name: data.buildingName,
        address: data.address,
      });
    } catch (e) {
      if (e.message === "FETCH_FAILED") {
        alert("건물 정보를 불러오지 못했습니다.");
      }
    } finally {
      if (!cancelled) {
        setLoading(false);
      }
    }
  };

  loadBuilding();

  return () => {
    cancelled = true;
  };
}, [tenantBuildingId]);




  if (loading) return <p style={{ padding: "40px" }}>불러오는 중...</p>;
  if (!building) return <p>건물을 찾을 수 없습니다.</p>;

  return (
    <div style={{ maxWidth: "1200px", margin: "0 auto", padding: "32px 24px" }}>
      {/* ================= 상단 ================= */}
      <div style={{ marginBottom: "24px" }}>
        <button
          onClick={() => navigate("/tenant")}
          style={{
            background: "none",
            border: "none",
            color: "#2563eb",
            cursor: "pointer",
            fontSize: "14px",
            marginBottom: "12px",
          }}
        >
          ← 내 집 목록으로 돌아가기
        </button>

        <h1 style={{ marginBottom: "6px" }}>{building.name}</h1>
        <p style={{ color: "#6b7280" }}>{building.address}</p>
      </div>

      {/* ================= 탭 ================= */}
      <div
        style={{
          borderRadius: "16px",
          backgroundColor: "#ffffff",
          boxShadow: "0 4px 16px rgba(0,0,0,0.08)",
          overflow: "hidden",
        }}
      >
        {/* 탭 헤더 */}
        <div style={{ display: "flex", borderBottom: "1px solid #e5e7eb" }}>
          {[
            { key: "notices", label: "공지사항" },
            { key: "complaints", label: "민원" },
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

        {/* 탭 내용 */}
        <div style={{ padding: "24px" }}>
          {activeTab === "notices" && (
            <NoticeList buildingId={building.id} isAdmin={false} />
          )}

          {activeTab === "complaints" && (
            <p style={{ color: "#6b7280" }}>
              🛠 민원 기능은 준비 중입니다.
            </p>
          )}
        </div>
      </div>
    </div>
  );
}
