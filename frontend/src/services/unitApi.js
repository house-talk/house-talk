

/**
 * 특정 건물의 세대 목록 조회
 */
export async function fetchUnits(buildingId) {
  const res = await fetch(
    `/api/buildings/${buildingId}/units/status`,
    {
      method: "GET",
      credentials: "include", // ⭐ JWT 쿠키 인증
    }
  );

  if (!res.ok) {
    throw new Error("세대 목록 조회 실패");
  }

  return res.json();
}

/**
 * 세대 생성
 */
export async function createUnit(buildingId, data) {
  const res = await fetch(
    `/api/buildings/${buildingId}/units`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify(data),
    }
  );

  if (!res.ok) {
    throw new Error("세대 생성 실패");
  }

  return res.json();
}

/**
 * 세대 수정
 */
export async function updateUnit(buildingId, unitId, data) {
  const res = await fetch(
    `/api/buildings/${buildingId}/units/${unitId}`,
    {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify(data),
    }
  );

  if (!res.ok) {
    throw new Error("세대 수정 실패");
  }

  return res.json();
}

/**
 * 세대 삭제
 */
export async function deleteUnit(buildingId, unitId) {
  const res = await fetch(
    `/api/buildings/${buildingId}/units/${unitId}`,
    {
      method: "DELETE",
      credentials: "include",
    }
  );

  if (!res.ok) {
    throw new Error("세대 삭제 실패");
  }
}

/**
 * ⭐ 세대 순서(orderIndex) 저장
 */
export async function updateUnitOrder(buildingId, orders) {
  const res = await fetch(
    `/api/buildings/${buildingId}/units/order`,
    {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify({ orders }),
    }
  );

  if (!res.ok) {
    throw new Error("세대 순서 저장 실패");
  }

  
}

/**
 * 여러 세대 한 번에 생성
 */
export async function createUnitsBulk(buildingId, data) {
  const res = await fetch(
    `/api/buildings/${buildingId}/units/bulk`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify(data),
    }
  );

  if (!res.ok) {
    throw new Error("여러 세대 생성 실패");
  }
}

