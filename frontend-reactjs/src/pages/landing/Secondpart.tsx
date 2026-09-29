import { Button } from "@nextui-org/button";
import { Code, Input } from "@nextui-org/react";
import React, { useState } from "react";
import { toast } from "sonner";

export const Secondpart = () => {
  const [email, setEmail] = useState("");
  const [name, setName] = useState("");
  const [message, setMessage] = useState("");

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    console.log("Sending data to the server:", { email, name });

    e.preventDefault();
    toast.success("Registered to our newsletter service!");
    setMessage("We won't send any e-mails, don't worry.");

    setTimeout(() => {
      setMessage("");
    }, 2500);
  };

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 bg-red-100">
      <div className="col-span-1 flex flex-col items-center p-5">
        <p className="text-lg mb-4 text-center font-bold">
          Send name and e-mail address for register our newsletter!
        </p>
        <form
          onSubmit={handleSubmit}
          className="space-y-4 w-full max-w-xs sm:max-w-sm"
        >
          <Input
            label="Email"
            type="text"
            placeholder="Enter your email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            fullWidth
          />
          <Input
            label="Name"
            type="text"
            placeholder="Enter your name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
            fullWidth
          />
          <Button type="submit" color="danger" variant="shadow" fullWidth>
            Send
          </Button>
          {message && (
            <div className="font-sfpro  grid place-items-center">{message}</div>
          )}
        </form>

        <div className="mt-4 w-full max-w-[1000px] border-3 border-red-300">
          <Code
            size="lg"
            className="block p-4 overflow-auto  whitespace-pre-wrap"
          >
            {`
              // Sample POST request code
              fetch('http://newsletterservice.com/newsletter/yournewsletterId', {
                method: 'POST',
                headers: {
                  'Content-Type': 'application/json',
                },
                body: JSON.stringify({ 
                  email: '${email}', 
                  name: '${name}' 
                }),
              });
            `}
          </Code>
        </div>
      </div>
      <div className="col-span-1 flex gap-10 items-center justify-center h-[700px]">
        <div className="mx-10 text-center">
          <p className="text-4xl sm:text-5xl font-bold font-sfpro text-red-700">
            GETTING STARTED
          </p>
          <a href="/register">
            <Button
              className="bg-red-700 mt-10"
              color="danger"
              variant="shadow"
              fullWidth
            >
              Signup for Free!
            </Button>
          </a>
          <a href="/login">
            <Button className=" mt-10" color="danger" variant="ghost" fullWidth>
              Already have an account?
            </Button>
          </a>
        </div>
      </div>
    </div>
  );
};
