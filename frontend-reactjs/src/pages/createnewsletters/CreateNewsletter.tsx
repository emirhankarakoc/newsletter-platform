import { http, httpError } from "@/assets/http";
import Navigation from "@/components/Navigation";
import { Button, Input } from "@nextui-org/react";
import React, { useState } from "react";

export const CreateNewsletter = () => {
  const [isLoading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    name: "",
    description: "",
    image: null as string | null,
  });
  const [newsletter, setNewsletter] = useState<Newsletter>();

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    setLoading(true);
    e.preventDefault();
    const form = new FormData(e.currentTarget);
    console.log(form);

    try {
      const response = await http.post("/newsletters", form);
      setNewsletter(response.data);
    } catch (err) {
      httpError(err);
    }

    setLoading(false);
    // Burada formu API'ye yollayabilirsiniz
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({ ...prevData, [name]: value }));
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]; // Kullanıcı tarafından seçilen ilk dosya
    if (file) {
      const imageUrl = URL.createObjectURL(file); // Seçilen resmin URL'sini oluşturuyoruz
      setFormData((prevData) => ({ ...prevData, image: imageUrl })); // Resmi state'e kaydediyoruz
    }
  };

  return (
    <div>
      <Navigation />
      <p>
        Welcome. You need to use external HTML code editor for testing this
        code.
      </p>
      <div className="grid grid-cols-2 p-10">
        <div className="col-span-1">
          <form id="1" onSubmit={handleSubmit}>
            <h1 className="text-3xl font-bold font-sfpro">
              Create a newsletter
            </h1>
            <Input
              className="my-5"
              isRequired
              label="Newsletter name"
              name="name"
              value={formData.name}
              onChange={handleInputChange} // İsmi her değiştiğinde sağ tarafta gösterecek
            />

            <Input
              className="my-5"
              isRequired
              label="Description"
              name="description"
              value={formData.description}
              onChange={handleInputChange} // Açıklama her değiştiğinde sağ tarafta gösterecek
            />

            <Input
              className="my-5"
              label="Choose newsletter image"
              name="file"
              isRequired
              type="file"
              onChange={handleImageChange} // Resim seçildiğinde handleImageChange çalışacak
            />
          </form>
        </div>

        {/* sag taraf */}
        <div className="col-span-1 p-10 bg-neutral-200 rounded-3xl mx-10">
          <h1></h1>
          {/* İsim ve açıklama burada anında gösterilecek */}
          <h1 className="text-2xl font-bold font-sfpro">
            {formData.name ? formData.name : "Newsletter name"}
          </h1>

          {/* Resim önizleme */}
          {formData.image && (
            <div className="my-5">
              <img
                src={formData.image}
                alt="Selected Preview"
                className="w-40 h-40 object-cover"
              />
            </div>
          )}

          {/* Açıklama */}
          <p className="my-10 font-bold">
            {formData.description
              ? formData.description
              : "Açıklama burada gözükecek"}
          </p>
          <Button
            isLoading={isLoading}
            color="success"
            fullWidth
            form="1"
            type="submit"
          >
            Create
          </Button>
          {isLoading === true && (
            <div className="grid place-items-center">
              Please wait while loading.
            </div>
          )}
          {newsletter && (
            <div className="border-green-400 border-3 p-5 m-5 rounded-3xl">
              <p>Newsletter created successfully!</p>
              <a
                className="text-blue-900 hover:text-blue-600"
                href={`/update/${newsletter.id}`}
              >
                Click here for see
              </a>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
