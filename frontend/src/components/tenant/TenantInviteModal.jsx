// src/components/tenant/TenantInviteModal.jsx
import { useState } from "react";
import { validateInvite } from "../../services/inviteApi";
import { joinHouse } from "../../services/tenantHouseApi";

export default function TenantInviteModal({ onClose }) {
  const [step, setStep] = useState(1);

  const [inviteCode, setInviteCode] = useState("");
  const [error, setError] = useState(null);

  const [building, setBuilding] = useState(null);

  // STEP 2 입력값
  const [name, setName] = useState("");
  const [phone, setPhone] = useState("");
  const [unitNumber, setUnitNumber] = useState("");

  /* =========================
     STEP 1: 초대코드 검증
  ========================= */
  const handleInviteCheck = async () => {
    setError(null);

    if (!inviteCode.trim()) {
      setError("초대코드를 입력해주세요.");
      return;
    }

    try {
      const res = await validateInvite(inviteCode);
      // res: { buildingId, buildingName, address }
      setBuilding(res);
      setStep(2);
    } catch {
      setError("초대코드가 유효하지 않습니다.");
    }
  };

  /* =========================
     STEP 2: 집 추가 완료
  ========================= */
  const handleComplete = async () => {
    setError(null);

    if (!name.trim() || !phone.trim() || !unitNumber.trim()) {
      setError("모든 정보를 입력해주세요.");
      return;
    }

    try {
      await joinHouse({
        inviteCode,
        name: name.trim(),
        phoneNumber: phone.trim(),
        unitNumber: unitNumber.trim(),
      });

      onClose(); // 성공 시 닫기
    } catch (e) {
      // 🔥 호수 불일치 에러 처리
      if (
        e?.response?.data?.message?.includes("호수") ||
        e?.message?.includes("호수")
      ) {
        setError("존재하지 않는 호수입니다. 관리자에게 문의해주세요.");
      } else {
        setError("집 추가에 실패했습니다.");
      }
    }
  };

  return (
    <div style={overlay}>
      <div style={modal}>
        {/* STEP 1 */}
        {step === 1 && (
          <>
            <h2 style={{ marginBottom: "8px" }}>초대코드 입력</h2>
            <p style={subText}>관리자에게 받은 초대코드를 입력하세요</p>

            <input
              value={inviteCode}
              onChange={(e) => setInviteCode(e.target.value)}
              placeholder="ABCD-1234"
              style={input}
            />

            {error && <p style={errorText}>{error}</p>}

            <button style={primaryBtn} onClick={handleInviteCheck}>
              확인
            </button>
          </>
        )}

        {/* STEP 2 */}
        {step === 2 && (
          <>
            <div style={{ marginBottom: "12px" }}>
              <h2 style={{ marginBottom: "4px" }}>
                {building?.buildingName}
              </h2>
              <p style={addressText}>{building?.address}</p>

              {/* ✅ 추가된 설명 문구 */}
              <p
                style={{
                  marginTop: "8px",
                  fontSize: "14px",
                  color: "#374151",
                }}
              >
                실제로 입주하는 사람의 정보를 입력해주세요
              </p>
            </div>

            <input
              placeholder="이름"
              value={name}
              onChange={(e) => setName(e.target.value)}
              style={input}
            />

            <input
              placeholder="전화번호"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              style={input}
            />

            <input
              placeholder="호수 (예: 502)"
              value={unitNumber}
              onChange={(e) => setUnitNumber(e.target.value)}
              style={input}
            />

            {error && <p style={errorText}>{error}</p>}

            <button style={primaryBtn} onClick={handleComplete}>
              집 추가 완료
            </button>
          </>
        )}

        <button onClick={onClose} style={closeBtn}>
          닫기
        </button>
      </div>
    </div>
  );
}

/* =========================
   styles
========================= */

const overlay = {
  position: "fixed",
  inset: 0,
  backgroundColor: "rgba(0,0,0,0.4)",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  zIndex: 1000,
};

const modal = {
  width: "360px",
  background: "#ffffff",
  padding: "24px",
  borderRadius: "12px",
  boxShadow: "0 4px 16px rgba(0,0,0,0.12)",
};

const subText = {
  color: "#6b7280",
  fontSize: "14px",
  marginBottom: "16px",
};

const addressText = {
  color: "#9ca3af",
  fontSize: "13px",
};

const input = {
  width: "100%",
  padding: "12px",
  borderRadius: "8px",
  border: "1px solid #d1d5db",
  marginBottom: "12px",
};

const primaryBtn = {
  width: "100%",
  padding: "12px",
  borderRadius: "8px",
  border: "none",
  backgroundColor: "#10b981",
  color: "#ffffff",
  fontWeight: "600",
  cursor: "pointer",
};

const closeBtn = {
  marginTop: "12px",
  background: "none",
  border: "none",
  color: "#6b7280",
  cursor: "pointer",
};

const errorText = {
  color: "#dc2626",
  fontSize: "13px",
  marginBottom: "8px",
};
