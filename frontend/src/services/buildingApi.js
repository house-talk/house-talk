const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

/**
 * 건물 목록 조회
 */
export async function getBuildings() {
  const res = await fetch(`${API_BASE_URL}/api/buildings`, {
    credentials: "include",
  });

  if (!res.ok) {
    const text = await res.text();
    console.error("건물 목록 조회 서버 에러:", text);
    throw new Error("건물 목록 조회 실패");
  }

  return res.json();
}


/**
 * 건물 생성
 */
export async function createBuilding(data) {
  const res = await fetch(`${API_BASE_URL}/api/buildings`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
    body: JSON.stringify(data),
  });

  if (!res.ok) {
    throw new Error("건물 생성 실패");
  }

  return res.json();
}

/**
 * ⭐ 건물 수정
 */
export async function updateBuilding(buildingId, data) {
  const res = await fetch(
    `${API_BASE_URL}/api/buildings/${buildingId}`,
    {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify(data),
    }
  );

  if (!res.ok) {
    throw new Error("건물 수정 실패");
  }

  return res.json();
}

/**
 *  건물 삭제
 */
export async function deleteBuilding(buildingId) {
  const res = await fetch(
    `${API_BASE_URL}/api/buildings/${buildingId}`,
    {
      method: "DELETE",
      credentials: "include",
    }
  );

  if (!res.ok) {
    throw new Error("건물 삭제 실패");
  }
}

/**
 * ⭐ 건물 단건 조회
 */
export async function fetchBuilding(buildingId) {
  const res = await fetch(
    `${API_BASE_URL}/api/buildings/${buildingId}`,
    {
      credentials: "include",
    }
  );

  if (!res.ok) {
    const text = await res.text();
    console.error("건물 단건 조회 서버 에러:", text);
    throw new Error("건물 조회 실패");
  }

  return res.json();
}

