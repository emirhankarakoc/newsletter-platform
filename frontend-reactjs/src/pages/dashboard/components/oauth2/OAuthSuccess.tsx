import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export default function OAuthSuccess() {
  const navigate = useNavigate();

  useEffect(() => {
    const token = new URLSearchParams(window.location.hash.slice(1)).get("token");

    if (token) {
      localStorage.setItem("jwtToken", token);
    }

    // Replace the callback URL so the token is not retained in browser history.
    navigate("/dashboard", { replace: true });
  }, [navigate]);

  return (
    <div className="flex items-center justify-center h-screen text-xl">
      Google hesabı bağlanıyor...
    </div>
  );
}
