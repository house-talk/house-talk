// src/pages/tenant/TenantEntryModal.jsx
import { useState } from "react";
import { authenticateTenant } from "../../services/tenantHouseApi";
import "./TenantEntryModal.css";

export default function TenantEntryModal({ onClose, onSuccess }) {
  const [step, setStep] = useState("select");
  const [newUser, setIsNew] = useState(false);

  const [name, setName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const handleAuthSubmit = async (e) => {
    e.preventDefault();

    if (!phoneNumber || !password) {
      setErrorMessage("전화번호와 비밀번호를 입력해주세요.");
      return;
    }

    try {
      setLoading(true);
      setErrorMessage("");

      await authenticateTenant({
        phoneNumber,
        password,
        newUser,
        ...(newUser && { name }),
      });


      onSuccess();
    } catch (error) {
      const message = error?.message || "";

      if (message.includes("이미 가입된")) {
        setErrorMessage("이미 가입된 전화번호입니다. 기존 이용으로 로그인해주세요.");
      } else if (message.includes("비밀번호")) {
        setErrorMessage("비밀번호가 일치하지 않습니다.");
      } else if (message.includes("존재하지")) {
        setErrorMessage("존재하지 않는 세입자입니다.");
      } else {
        setErrorMessage("인증에 실패했습니다. 다시 시도해주세요.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal">
        <button className="modal-close" onClick={onClose}>×</button>

        {step === "select" && (
          <>
            <h2>세입자이신가요?</h2>
            <p>HOUSE-TALK은 간단하게 이용할 수 있어요.</p>

            <div className="button-group">
              <button
                className="primary-btn"
                onClick={() => {
                  setIsNew(false);
                  setStep("auth");
                }}
              >
                기존에 참여한 집이 있어요
              </button>

              <button
                className="secondary-btn"
                onClick={() => {
                  setIsNew(true);
                  setStep("auth");
                }}
              >
                처음 이용해요
              </button>
            </div>
          </>
        )}

        {step === "auth" && (
          <>
            {/* ⭐ 신규 / 기존에 따라 제목 변경 */}
            <h2>
              {newUser ? "신규 세입자 회원가입" : "기존 세입자 로그인"}
            </h2>

            <form onSubmit={handleAuthSubmit}>
              {/* ⭐ 신규일 때만 이름 입력 */}
              {newUser && (
                <input
                  type="text"
                  placeholder="이름"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  disabled={loading}
                />
              )}

              {/* ⭐ 전화번호: 숫자만 입력 */}
              <input
                type="tel"
                placeholder="전화번호(숫자만 입력)"
                value={phoneNumber}
                onChange={(e) =>
                  setPhoneNumber(e.target.value.replace(/[^0-9]/g, ""))
                }
                disabled={loading}
              />

              <input
                type="password"
                placeholder="비밀번호"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={loading}
              />

              {errorMessage && (
                <p className="error-message">{errorMessage}</p>
              )}

              <button type="submit" disabled={loading}>
                {loading ? "확인 중..." : "입장하기"}
              </button>
            </form>
          </>
        )}
      </div>
    </div>
  );
}
