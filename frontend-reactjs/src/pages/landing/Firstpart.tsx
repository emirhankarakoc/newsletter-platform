import { Button } from "@nextui-org/button";

export const Firstpart = () => {
  return (
    <div className="grid grid-cols-2 bg-yellow-50">
      <div className="col-span-1 grid place-items-center h-[700px]">
        <div className="mx-10 ">
          <p className="text-5xl font-bold font-sfpro text-yellow-700 ">
            THE EASY WAY TO START CREATING A NEWSLETTER
          </p>

          <a href="#howtouse">
            <Button
              className="bg-yellow-700 mt-10"
              color="danger"
              variant="shadow"
              fullWidth
            >
              Explore
            </Button>
          </a>
        </div>
      </div>
      <div className="col-span-1 grid place-items-center">
        <img
          src="https://cdn-icons-png.flaticon.com/512/4470/4470631.png"
          alt="resim"
        />
      </div>
    </div>
  );
};
