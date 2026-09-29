import { useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";

export default function OAuthSuccess() {
  const navigate = useNavigate();
  const tokenRef = useRef<string | null>(null);

  const query = new URLSearchParams(window.location.search);
  console.log("queryden cekilen:", query.get("token")); // BİRİNCİ LOG (component seviyesinde)

  useEffect(() => {
    const token = query.get("token");
    tokenRef.current = token;

    if (token) {
      localStorage.setItem("jwtToken", token);
      navigate("/dashboard"); // yönlendiriyoruz
    } else {
      console.log("Token bulunamadı, bağlantı başarısız.");
      navigate("/dashboard");
    }
  }, []);

  return (
    <div className="flex items-center justify-center h-screen text-xl">
      Google ile giriş yapılıyor...
    </div>
  );
}
