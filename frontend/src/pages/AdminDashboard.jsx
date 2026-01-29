import { useEffect, useState } from "react";
import Button from "../components/common/Button";
import BuildingList from "../components/building/BuildingList";
import BuildingForm from "../components/building/BuildingForm";
import {
  getBuildings,
  deleteBuilding,
} from "../services/buildingApi";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export default function AdminDashboard() {
  const [admin, setAdmin] = useState(null);
  const [loading, setLoading] = useState(true);

  const [buildings, setBuildings] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [editingBuilding, setEditingBuilding] = useState(null);

  // ⭐ 삭제 모달 상태 추가
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [deleteInput, setDeleteInput] = useState("");

  useEffect(() => {
    fetch(`${API_BASE_URL}/api/admin/me`, {
      credentials: "include",
    })
      .then((res) => {
        if (!res.ok) throw new Error("인증 실패");
        return res.json();
      })
      .then((data) => {
        setAdmin(data);
        setLoading(false);
      })
      .catch(() => {
        alert("로그인이 필요합니다.");
        window.location.href = "/";
      });
  }, []);

  const fetchBuildings = async () => {
    const data = await getBuildings();
    setBuildings(data);
  };

  useEffect(() => {
    if (admin) {
      fetchBuildings();
    }
  }, [admin]);

  const handleEdit = (building) => {
    setEditingBuilding(building);
    setShowForm(true);
    document.body.style.overflow = "hidden";
  };

  // 삭제 버튼 클릭 → 모달 오픈
  const handleDelete = (building) => {
    setDeleteTarget(building);
    setDeleteInput("");
    document.body.style.overflow = "hidden";
  };

  const closeForm = () => {
    setShowForm(false);
    setEditingBuilding(null);
    document.body.style.overflow = "auto";
  };

  // 삭제 모달 닫기
  const closeDeleteModal = () => {
    setDeleteTarget(null);
    setDeleteInput("");
    document.body.style.overflow = "auto";
  };

  const handleLogout = () => {
    fetch(`${API_BASE_URL}/api/auth/logout`, {
      method: "POST",
      credentials: "include",
    }).finally(() => {
      window.location.href = "/";
    });
  };

  if (loading) {
    return <p style={{ textAlign: "center" }}>불러오는 중...</p>;
  }

  return (
    <>
      <div
        style={{
          minHeight: "calc(100vh - 120px)",
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          paddingTop: "80px",
          width: "100%",
        }}
      >
        <h2 style={{ marginBottom: "40px" }}>
          {admin.name}님 환영합니다
        </h2>

        <div style={{ width: "100%", maxWidth: "900px" }}>
          <BuildingList
            buildings={buildings}
            onEdit={handleEdit}
            onDelete={handleDelete}
          />
        </div>

        <div style={{ marginTop: "32px" }}>
          <Button
            onClick={() => {
              setEditingBuilding(null);
              setShowForm(true);
              document.body.style.overflow = "hidden";
            }}
          >
            건물 추가
          </Button>
        </div>

        <div style={{ marginTop: "auto", marginBottom: "60px" }}>
          <Button onClick={handleLogout}>로그아웃</Button>
        </div>
      </div>

      {/* 🔹 수정/추가 모달 */}
      {showForm && (
        <div
          style={{
            position: "fixed",
            inset: 0,
            backgroundColor: "rgba(0,0,0,0.45)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            zIndex: 1000,
          }}
          onClick={closeForm}
        >
          <div
            onClick={(e) => e.stopPropagation()}
            style={{
              width: "100%",
              maxWidth: "900px",
              padding: "0 16px",
            }}
          >
            <BuildingForm
              building={editingBuilding}
              onSuccess={() => {
                fetchBuildings();
                closeForm();
              }}
              onClose={closeForm}
            />
          </div>
        </div>
      )}

      {/* ⭐ 삭제 확인 모달 (가운데 카드) */}
      {deleteTarget && (
        <div
          style={{
            position: "fixed",
            inset: 0,
            backgroundColor: "rgba(0,0,0,0.45)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            zIndex: 1000,
          }}
          onClick={closeDeleteModal}
        >
          <div
            onClick={(e) => e.stopPropagation()}
            style={{
              backgroundColor: "#ffffff",
              borderRadius: "16px",
              padding: "32px",
              width: "100%",
              maxWidth: "420px",
              boxShadow: "0 10px 30px rgba(0,0,0,0.2)",
            }}
          >
            <h3 style={{ marginBottom: "12px" }}>건물 삭제</h3>

            <p style={{ fontSize: "14px", color: "#374151", marginBottom: "16px" }}>
              <b>{deleteTarget.name}</b> 건물을 삭제하려면<br />
              아래에 <b>삭제</b>를 입력하세요.
            </p>

            <input
              value={deleteInput}
              onChange={(e) => setDeleteInput(e.target.value)}
              placeholder="삭제"
              style={{
                width: "100%",
                padding: "10px 12px",
                borderRadius: "10px",
                border: "1px solid #d1d5db",
                marginBottom: "20px",
              }}
            />

            <div
              style={{
                display: "flex",
                justifyContent: "flex-end",
                gap: "8px",
              }}
            >
              <Button onClick={closeDeleteModal}>
                취소
              </Button>

              <Button
                variant="danger"
                disabled={deleteInput !== "삭제"}
                onClick={async () => {
                  await deleteBuilding(deleteTarget.id);
                  fetchBuildings();
                  closeDeleteModal();
                }}
              >
                삭제
              </Button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
