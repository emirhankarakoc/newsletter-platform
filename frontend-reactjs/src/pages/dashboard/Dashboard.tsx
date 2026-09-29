import { http, httpError } from "@/assets/http";
import { useEffect, useState } from "react";
import Navigation from "@/components/Navigation";
import GoogleVerificatedDashboard from "./components/GoogleVerificatedDashboard";
import LoginWithGoogle from "./components/oauth2/LoginWithGoogle";
import LogOutWithGoogle from "./components/oauth2/LogOutWithGoogle";

export default function Dashboard() {
  const [user, setUser] = useState<User>();
  const [newsletters, setNewsletters] = useState<Newsletter[]>();

  useEffect(() => {
    const handleGetMe = async () => {
      try {
        const response = await http.get("/accounts/getme");
        setUser(response.data);
        console.log(response.data);
      } catch (err) {
        httpError(err);
      }
    };
    const handleGetNewsletters = async () => {
      try {
        const response = await http.get("/newsletters/my");
        setNewsletters(response.data);
      } catch (err) {
        httpError(err);
      }
    };

    handleGetNewsletters();
    handleGetMe();
  }, []);

  return (
    <div>
      <Navigation />
      {/* Container ile responsive aralık sağlanıyor */}
      <div className="container mx-auto px-4 py-4">
        {/* 
          grid-cols-1 : 0-767px tek kolon 
          md:grid-cols-2 : 768-1023px iki kolon 
          lg:grid-cols-4 : 1024px ve üzeri 4 kolon (sol:1, sağ:3) 
        */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-5">
          {/* Sol sütun: Account detayları */}
          <div className="md:col-span-1 lg:col-span-1">
            <div className="border border-pink-400 p-5 grid place-items-center gap-3">
              <h1 className="text-xl font-bold font-sfpro text-center">
                Account
              </h1>

              <p className="text-center break-words whitespace-normal">
                e-mail address: {user?.email}
              </p>
              {/* <p className="font-bold">status: {user?.userStatus}</p> */}
            </div>
            {user?.userStatus === "GOOGLE_NOT_VERIFICATED" && (
              <LoginWithGoogle />
            )}
            {user?.userStatus === "GOOGLE_VERIFICATED" && (
              <div className="border border-pink-400 p-5 grid place-items-center gap-3">
                <h1 className="text-xl font-bold font-sfpro text-center">
                  Mail Sender Account
                </h1>
                <p>Sender e-mail:{user.googleTokenEmailAddress}</p>
                <div>
                  <LogOutWithGoogle />
                </div>
              </div>
            )}
          </div>
          {/* Sağ sütun: Dashboard içeriği */}
          <div className="md:col-span-1 lg:col-span-3">
            {user?.userStatus === "GOOGLE_VERIFICATED" ? (
              <GoogleVerificatedDashboard newsletters={newsletters} />
            ) : (
              <div className="border border-pink-300 p-5 grid place-items-center">
                <h1 className="font-bold font-sfpro text-3xl text-center">
                  Connect Gmail account for use this website.
                </h1>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
