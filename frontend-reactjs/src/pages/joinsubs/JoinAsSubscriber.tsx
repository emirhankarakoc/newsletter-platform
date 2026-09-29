import { APIURL, http, httpError } from "@/assets/http";
import { Button } from "@nextui-org/button";
import { Input } from "@nextui-org/react";
import axios from "axios";
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

export const JoinAsSubscriber = () => {
  const [isLoading, setLoading] = useState(false);
  const { newsletterId } = useParams();
  const [newsletter, setNewsletter] = useState<Newsletter>();
  const [email, setEmail] = useState("");
  const [isInvalidEmail, setIsInvalidEmail] = useState(false);
  const [responseMessage, setResponseMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    const getNewsletter = async () => {
      try {
        const response = await http.get(`/newsletters/${newsletterId}`);
        setNewsletter(response.data);
        console.log(response.data);
      } catch (error) {
        httpError(error);
      }
    };

    getNewsletter();
  }, [newsletterId]);

  const validateEmail = (value: string) =>
    value.match(/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    setErrorMessage("");
    setLoading(true);
    e.preventDefault();

    // Eğer email geçerli değilse, formu gönderme
    if (isInvalidEmail) {
      return;
    }

    const form = new FormData(e.currentTarget);
    const data = Object.fromEntries(form.entries());
    console.log(data);
    try {
      await axios.post(`${APIURL}/customers/newsletters/${newsletterId}`, data);
      setResponseMessage("Subscribed successfully.");

      setTimeout(() => {
        setResponseMessage("");
      }, 3000);
    } catch (err: any) {
      setErrorMessage(httpError(err));

      setTimeout(() => {
        setErrorMessage("");
      }, 3000);
    }
    setLoading(false);
  };

  const handleEmailChange = (value: string) => {
    setEmail(value);
    setIsInvalidEmail(!validateEmail(value));
  };

  return (
    <div className="grid place-items-center  h-screen bg-black">
      <div className=" text-white border-3 border-green-300 p-10">
        <h1 className="text-3xl font-bold font-sfpro">{newsletter?.name}</h1>
        <img
          className="mx-auto my-10 w-56 h-56"
          src={newsletter?.imageUrl}
          alt="resim"
        />
        <p className="text-xl font-bold font-sfpro">
          {newsletter?.description}
        </p>
        <h1 className=" mt-10 text-xl font-bold font-sfpro ">Join now !</h1>

        <form onSubmit={handleSubmit} className=" mb-10 rounded-3xl p-2">
          <Input isRequired className="my-1" name="name" label="Name" />
          <Input
            isRequired
            errorMessage="Please enter a valid email"
            className="my-1"
            name="email"
            label="e-Mail Address"
            type="email"
            value={email}
            onValueChange={handleEmailChange} // Email değişiminde doğrulama
            isInvalid={isInvalidEmail}
          />
          <Button
            isLoading={isLoading}
            className="my-1"
            color="success"
            fullWidth
            type="submit"
          >
            Join !
          </Button>
          {responseMessage && (
            <div className="bg-green-400 mt-10 font-bold text-black font-sfpro grid place-items-center rounded-3xl text-md p-3">
              {responseMessage}
            </div>
          )}{" "}
          {errorMessage && (
            <div className="bg-red-400 mt-10 font-bold text-black font-sfpro grid place-items-center rounded-3xl text-md p-3">
              {errorMessage}
            </div>
          )}
        </form>
      </div>
    </div>
  );
};
