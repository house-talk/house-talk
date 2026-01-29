const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

/**
 * 관리자용 초대코드 발급
 * POST /api/admin/invites?buildingId=1
 */
export async function createInvite(buildingId) {
  const res = await fetch(
    `${API_BASE_URL}/api/admin/invites?buildingId=${buildingId}`,
    {
      method: "POST",
      credentials: "include", // ⭐ 관리자 JWT 쿠키
    }
  );

  if (!res.ok) {
    throw new Error("초대코드 발급 실패");
  }

  return res.json();
  // { inviteCode: "ABCD-1234" }
}

/**
 * 🔥 관리자용 초대코드 조회 (유지용)
 * GET /api/admin/invites?buildingId=1
 */
export async function fetchInvite(buildingId) {
  const res = await fetch(
    `${API_BASE_URL}/api/admin/invites?buildingId=${buildingId}`,
    {
      method: "GET",
      credentials: "include", // ⭐ 관리자 JWT 쿠키
    }
  );

  if (!res.ok) {
    throw new Error("초대코드 조회 실패");
  }

  return res.json();
  // { inviteCode: "ABCD-1234" } or { inviteCode: null }
}

/**
 * 세입자용 초대코드 검증 (비로그인)
 * POST /tenant/invites/validate?inviteCode=XXXX
 */
export async function validateInvite(inviteCode) {
  const res = await fetch(
    `${API_BASE_URL}/api/tenant/invites/validate`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify({
        inviteCode,
      }),
    }
  );

  if (!res.ok) {
    throw new Error("초대코드 검증 실패");
  }

  return res.json();
}

