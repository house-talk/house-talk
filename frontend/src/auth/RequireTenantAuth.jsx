// src/auth/RequireTenantAuth.jsx
import { useEffect, useRef, useState } from "react";
import { useNavigate, useLocation, Outlet } from "react-router-dom";

export default function RequireTenantAuth() {
  const navigate = useNavigate();
  const location = useLocation();
  const [checked, setChecked] = useState(false);

  // 🔥 StrictMode / 재렌더 중복 실행 방지
  const calledRef = useRef(false);

  useEffect(() => {
    if (calledRef.current) return;
    calledRef.current = true;

    fetch(`/api/tenant/homes`, {
      credentials: "include",
    })
      .then((res) => {
        // ✅ 세입자 인증 성공
        if (res.ok) {
          setChecked(true);
          return;
        }

        // ❌ 인증 실패만 여기서 처리
        if (res.status === 401 || res.status === 403) {
          throw new Error("unauthorized");
        }

        // 그 외 에러는 그냥 막음
        throw new Error("error");
      })
      .catch(() => {
        navigate("/", {
          replace: true,
          state: {
            authRequired: true,
            from: location.pathname,
          },
        });
      });
  }, [navigate, location.pathname]);

  // 🔒 인증 확인 전엔 아무 것도 렌더 안 함
  if (!checked) return null;

  return <Outlet />;
}
