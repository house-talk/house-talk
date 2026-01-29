const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

/**
 * 세입자 - 내가 승인된 집 목록 조회
 */
export async function fetchMyHouses() {
  const res = await fetch(
    `${API_BASE_URL}/api/tenant/homes`,
    {
      credentials: "include",
    }
  );

  if (!res.ok) {
    throw new Error("내 집 목록 조회 실패");
  }

  return res.json();
}

/**
 * 세입자 - 집 참여 요청 (초대코드 기반)
 */
export async function joinHouse({ inviteCode, name, phoneNumber, unitNumber }) {
  const res = await fetch(
    `${API_BASE_URL}/api/tenant/join`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify({
        inviteCode,
        name,
        phoneNumber,
        unitNumber,
      }),
    }
  );

  if (!res.ok) {
    throw new Error("집 참여 요청 실패");
  }

  // ✅ 백엔드가 void라서 json() 호출하면 안 됨
  return;
}

/**
 * ⭐ 세입자 - 이름 + 비밀번호 인증 (기존/신규 공통)
 *
 * - 기존 세입자: 로그인
 * - 신규 세입자: 자동 생성
 * - 성공 시: tenantCode 쿠키 발급
 */
export async function authenticateTenant({ name, phoneNumber, password, newUser }) {
  const res = await fetch(`${API_BASE_URL}/api/tenant/auth`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
    body: JSON.stringify({
      name,
      phoneNumber,
      password,
      newUser,
    }),
  });

    if (!res.ok) {
    const error = await res.json(); // ⭐ 핵심
    throw new Error(error.message);
  }

  return;
}

/**
 * ⭐ 세입자 정보 조회 (me)
 * 👉 이 API만 인증 쿠키가 필요
 * 👉 proxy 경로(/api) 사용
 */
export async function fetchTenantMe() {
  const res = await fetch(`${API_BASE_URL}/api/tenant/me`, {
    credentials: "include",
  });

  if (!res.ok) {
    throw new Error("세입자 정보 조회 실패");
  }

  return res.json();
}
