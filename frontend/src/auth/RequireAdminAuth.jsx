// src/auth/RequireAdminAuth.jsx
import { useEffect, useState } from "react";
import { useNavigate, useLocation, Outlet } from "react-router-dom";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export default function RequireAdminAuth() {
  const navigate = useNavigate();
  const location = useLocation();
  const [checked, setChecked] = useState(false);

  useEffect(() => {
  fetch(`${API_BASE_URL}/api/admin/me`, {
    credentials: "include",
  })
    .then((res) => {
      if (!res.ok) {
        throw new Error("unauthorized");
      }
      setChecked(true);
    })
    .catch(() => {
      // ❌ alert 제거 (EntryPage에서만 안내)
      navigate("/", {
        replace: true,
        state: {
          authRequired: true,
          from: location.pathname,
        },
      });
    });
}, [navigate, location.pathname]);


  if (!checked) return null;

  return <Outlet />;
}
