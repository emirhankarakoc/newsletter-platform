import { CLIENTURL, http, httpError } from "@/assets/http";
import { Accordion, AccordionItem, Button } from "@nextui-org/react";
import { useEffect, useState } from "react";
import { toast } from "sonner";

export default function GoogleVerificatedDashboard({
  newsletters,
}: GoogleVerificatedDashboardProps) {
  const [isLoading, setLoading] = useState<boolean>();
  const [images, setImages] = useState<Image[]>();

  useEffect(() => {
    const handleGetImages = async () => {
      try {
        const response = await http.get("/images");
        setImages(response.data);
      } catch (err) {
        console.log(httpError(err));
      }
    };

    handleGetImages();
  }, []);
  // Linki kopyalama fonksiyonu
  const copyLink = (url: string) => {
    navigator.clipboard
      .writeText(url)
      .then(() => {
        toast.success("Link copied!", { position: "top-center" });
      })
      .catch((err) => {
        console.error("Failed to copy link: ", err);
      });
  };

  const handleImageUpload = async (e: React.FormEvent<HTMLFormElement>) => {
    setLoading(true);

    e.preventDefault();
    const formdata = new FormData(e.currentTarget);
    try {
      const response = await http.post("/images", formdata);
      e.currentTarget.reset(); // Formu sıfırlıyoruz
      setImages([...(images ?? []), response.data]);
      toast.success("Image upload done.", { position: "top-center" });
    } catch (err) {
      console.log(httpError(err));
    }
    setLoading(false);
  };
  // Resmi silme fonksiyonu
  const handleDeleteImage = async (id: string) => {
    setLoading(true);
    try {
      await http.delete(`/images/${id}`);
      toast.success("Image deleted.", { position: "top-center" });
      // State güncellemesi: Silinen resmi listeden çıkarıyoruz
      setImages(images?.filter((img) => img.id !== id));
    } catch (error) {
      console.error(httpError(error));
      toast.error("Failed to delete image.", { position: "top-center" });
    }
    setLoading(false);
  };

  return (
    <div className="w-full p-5 border border-pink-400 rounded-md">
      <h1 className="text-3xl font-bold font-sfpro text-center mb-4">
        Dashboard
      </h1>
      <Accordion defaultExpandedKeys={["1"]}>
        <AccordionItem key="1" aria-label="Newsletters" title="Newsletters">
          <div className="p-4">
            <a href="/dashboard/newsletters/create">
              <Button fullWidth color="success">
                Create new one!
              </Button>
            </a>
            <div className="space-y-4 mt-4">
              {newsletters?.map((newsletter, index) => (
                <div
                  key={index}
                  className="grid grid-cols-1 sm:grid-cols-12 gap-4 bg-gray-200 rounded-3xl p-5"
                >
                  <div className="sm:col-span-2 flex items-center justify-center">
                    <img
                      className="w-20 h-20 object-cover rounded"
                      src={newsletter.imageUrl}
                      alt="newsletter thumbnail"
                    />
                  </div>
                  <div className="sm:col-span-10 grid grid-cols-1 sm:grid-cols-5 items-center">
                    <div className="sm:col-span-1 text-center font-bold font-sfpro break-words">
                      {newsletter.name}
                    </div>
                    <div className="sm:col-span-1 text-center font-bold font-sfpro">
                      {newsletter.customers.length} members
                    </div>
                    <div className="sm:col-span-3 flex flex-wrap justify-center gap-2">
                      <a href={`/sendmail/${newsletter.id}`}>
                        <Button color="success" title="Send Mail">
                          <i className="fa-solid fa-plus"></i>
                        </Button>
                      </a>
                      <Button
                        color="secondary"
                        title="Copy sharing link"
                        onClick={() => {
                          const shareUrl = `${CLIENTURL}/join/${newsletter.id}`;
                          navigator.clipboard
                            .writeText(shareUrl)
                            .then(() => {
                              console.log(
                                "URL copied to clipboard: ",
                                shareUrl
                              );
                              toast.success("URL copied to clipboard.");
                            })
                            .catch((err) => {
                              console.error("Failed to copy the URL: ", err);
                            });
                        }}
                      >
                        <i className="fa-solid fa-share-nodes"></i>
                      </Button>
                      <Button
                        color="primary"
                        title="Subscribers"
                        onClick={() => {
                          window.location.href = `/dashboard/newsletters/${newsletter.id}/subscribers`;
                        }}
                      >
                        <i className="fa-solid fa-user"></i>
                      </Button>
                      <Button
                        color="warning"
                        title="Update"
                        onClick={() => {
                          window.location.href = "/update/" + newsletter.id;
                        }}
                      >
                        <i className="fa-solid fa-pen-to-square"></i>
                      </Button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </AccordionItem>

        <AccordionItem key="3" aria-label="Images" title="Image Gallery">
          <div className="p-4">
            <form
              onSubmit={handleImageUpload}
              className="flex flex-col md:flex-row items-center gap-2"
            >
              <input
                className="border p-2 rounded w-full md:w-2/3"
                type="file"
                name="file"
              />
              <Button
                color="success"
                isLoading={isLoading}
                className="w-full md:w-1/3"
                type="submit"
              >
                Upload
              </Button>
            </form>

            <div className="mt-4 grid grid-cols-1 sm:grid-cols-3 gap-4">
              {images?.map((image) => (
                <div key={image.id} className="flex flex-col items-center">
                  <img
                    className="max-w-full rounded"
                    src={image.imageUrl}
                    alt="image url"
                  />
                  <div className="flex gap-2 mt-2">
                    <Button
                      size="sm"
                      color="secondary"
                      onClick={() => copyLink(image.imageUrl)}
                    >
                      <i className="fa-solid fa-copy"></i>
                    </Button>
                    <Button
                      isLoading={isLoading}
                      size="sm"
                      color="danger"
                      onClick={() => handleDeleteImage(image.id)}
                    >
                      <i className="fa-solid fa-trash"></i>
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </AccordionItem>
      </Accordion>
    </div>
  );
}
