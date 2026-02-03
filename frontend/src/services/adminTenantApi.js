

/**
 * 승인 대기 세입자 목록 조회
 */
export async function fetchPendingTenants(buildingId) {
  const res = await fetch(
    `/api/admin/tenants/pending?buildingId=${buildingId}`,
    {
      credentials: "include", // ⭐ 관리자 JWT
    }
  );

  if (!res.ok) {
    throw new Error("승인 요청 조회 실패");
  }

  return res.json();
}

/**
 * 승인
 */
export async function approveTenant(requestId) {
  const res = await fetch(
    `/api/admin/tenants/${requestId}/approve`,
    {
      method: "POST",
      credentials: "include",
    }
  );

  if (!res.ok) {
    const message = await res.text();
    const error = new Error(message);
    error.status = res.status;
    throw error;
  }
}


/**
 * 거절
 */
export async function rejectTenant(requestId) {
  const res = await fetch(
    `/api/admin/tenants/${requestId}`,
    {
      method: "DELETE",
      credentials: "include",
    }
  );

  if (!res.ok) {
    throw new Error("거절 실패");
  }
}
