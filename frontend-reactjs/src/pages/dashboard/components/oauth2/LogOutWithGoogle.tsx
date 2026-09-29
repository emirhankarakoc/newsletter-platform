import { Button } from "@nextui-org/button";
import { http, httpError } from "@/assets/http";
import { useNavigate } from "react-router-dom";
import { toast } from "sonner";

export default function LogOutWithGoogle() {
  const navigate = useNavigate();

  const handleDisconnect = async () => {
    try {
      await http.put("/connect/google/disconnect");
      console.log("Gmail disconnect done. (backend + frontend).");

      navigate("/dashboard"); //f5
    } catch (err) {
      toast.error("An error occured: " + httpError(err));
      console.error("An error occured while logging out the account:", err);
    }
  };

  return (
    <Button
      onPress={handleDisconnect}
      color="danger"
      variant="ghost"
      className="mt-2"
    >
      Logout from Google Account
    </Button>
  );
}
