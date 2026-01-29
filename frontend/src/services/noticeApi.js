const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

/**
 * 공지 목록 조회 (기존 - 유지)
 * GET /api/buildings/{buildingId}/notices
 */
export async function fetchNotices(buildingId) {
  const res = await fetch(
    `${API_BASE_URL}/api/buildings/${buildingId}/notices`,
    {
      credentials: "include",
    }
  );

  if (!res.ok) {
    throw new Error("공지 목록 조회 실패");
  }

  return res.json();
}

/**
 * ⭐ 공지 검색 + 페이징 조회 (신규)
 * GET /api/buildings/{buildingId}/notices/search
 */
export async function searchNotices(buildingId, { keyword = "", page = 1, size = 10 }) {
  const params = new URLSearchParams();

  if (keyword) params.append("keyword", keyword);
  params.append("page", page);
  params.append("size", size);

  const res = await fetch(
    `${API_BASE_URL}/api/buildings/${buildingId}/notices/search?${params.toString()}`,
    {
      credentials: "include",
    }
  );

  if (!res.ok) {
    throw new Error("공지 검색 조회 실패");
  }

  return res.json(); // Page<NoticeListResponse>
}

/**
 * 공지 생성 (관리자)
 * POST /api/buildings/{buildingId}/notices
 */
export async function createNotice(buildingId, formData) {
  const res = await fetch(
    `${API_BASE_URL}/api/admin/buildings/${buildingId}/notices`,
    {
      method: "POST",
      credentials: "include",
      body: formData,
    }
  );

  if (!res.ok) {
    throw new Error("공지 생성 실패");
  }

  return res.json();
}

/**
 * 공지 수정 (관리자)
 */
export async function updateNotice(buildingId, noticeId, formData) {
  const res = await fetch(
    `${API_BASE_URL}/api/admin/buildings/${buildingId}/notices/${noticeId}`,
    {
      method: "PATCH",
      credentials: "include",
      body: formData,
    }
  );

  if (!res.ok) {
    throw new Error("공지 수정 실패");
  }
}

/**
 * 공지 삭제 (관리자)
 */
export async function deleteNotice(buildingId, noticeId) {
  const res = await fetch(
    `${API_BASE_URL}/api/admin/buildings/${buildingId}/notices/${noticeId}`,
    {
      method: "DELETE",
      credentials: "include",
    }
  );

  if (!res.ok) {
    throw new Error("공지 삭제 실패");
  }
}

/**
 * 공지 단건 조회
 */
export async function fetchNoticeDetail(buildingId, noticeId) {
  const res = await fetch(
    `${API_BASE_URL}/api/buildings/${buildingId}/notices/${noticeId}`,
    {
      credentials: "include",
    }
  );

  if (!res.ok) {
    throw new Error("공지 상세 조회 실패");
  }

  return res.json();
}
