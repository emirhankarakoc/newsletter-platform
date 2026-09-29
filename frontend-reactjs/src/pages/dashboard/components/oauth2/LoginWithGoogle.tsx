import { APIURL, http, httpError } from "@/assets/http";
import { Button } from "@nextui-org/button";
import { toast } from "sonner";

export default function LoginWithGoogle() {
  const handleClick = async () => {
    try {
      const res = await http.post(`${APIURL}/connect/google`);
      console.log(res.data);
      const { url } = res.data;
      window.location.href = url;
    } catch (err) {
      toast.error("Error occured while google connection:" + httpError(err));

      console.error("Error occured while google connection:", err);
    }
  };

  return (
    <div className="my-4">
      <Button
        className="w-96 bg-white text-black shadow-md"
        onPress={handleClick}
      >
        Connect gmail account
      </Button>
    </div>
  );
}
