import React, { useState } from "react";
import Footer from "@/components/Footer";
import Navigation from "@/components/Navigation";
import { Accordion, AccordionItem, Button } from "@nextui-org/react";
import { APIURL, http } from "@/assets/http";
import { useParams } from "react-router-dom";
import { toast } from "sonner";

export const CreateAndSendMail = () => {
  const { newsletterId } = useParams();
  const [htmlContent, setHtmlContent] = useState(
    `<!doctype html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Document</title>
  </head>
  <body>
    <table width="100%" cellpadding="0" cellspacing="0" border="0">
      <tr>
        <td style="text-align: center">
          <h1 style="margin: 0">
            e-Newsletter Service, easy way to start blogging!
          </h1>
          <p style="margin: 0">degerli vayne severler dernegi. hosgeldiniz!</p>
          <table style="margin: 0 auto">
            <tr>
              <td style="border: 5px solid lightgreen; padding: 10px">
                <img
                  src="https://ih1.redbubble.net/image.810044737.8981/flat,750x,075,f-pad,750x1000,f8f8f8.u2.jpg"
                  alt="200x200 resim"
                  style="display: block; width: 200px; height: 200px;"
                />
              </td>
            </tr>
          </table>
          <p style="margin: 0">
          
            burasi da footer yeri, istege bagli unsub mesajini barindirabilir. koysaniz iyi de olur. 
          </p>
          <a href="${APIURL}/customers/ELECTRONICNEWSLETTER_USERID/newsletters/ELECTRONICNEWSLETTER_NEWSLETTERID/unsubscribe"> unsubscribe </a>
        </td>
      </tr>
    </table>
  </body>
</html>`
  );
  const [subject, setSubject] = useState(""); // Subject state
  const [isLoading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault(); // Prevent default form behavior
    setLoading(true);

    // Convert HTML content to a Blob file
    const htmlBlob = new Blob([htmlContent], { type: "text/html" });
    const formData = new FormData();

    // Append the subject and HTML file to FormData
    formData.append("subject", subject);
    formData.append("htmlFile", htmlBlob, "newsletter.html"); // Set the file name

    try {
      const response = await http.post(
        `/newsletters/${newsletterId}/sendMessage`,
        formData,
        { headers: { "Content-Type": "multipart/form-data" } } // Set the correct headers for FormData
      );
      toast.success("Mail successfully sent to subscribers");
      console.log("Mail successfully sent to subscribers:", response.data);
    } catch (error) {
      console.error("Error sending mail:", error);
    }

    setLoading(false);
  };

  return (
    <div>
      <Navigation />
      <div className="p-5 border-pink-400 border-3">
        <Accordion>
          <AccordionItem
            name="Help"
            aria-label="Help"
            key="1"
            title="Mail sending guide"
          >
            <p className="text-center">
              Welcome.
              <br />
              <strong>
                You should use external HTML code editor for test this code for
                better results
              </strong>
              <br />
              Each user's name, email address and unsubscribe link are
              different. If you want to customize them:
              <br />
              For user name:
              <strong>ELECTRONICNEWSLETTERSERVICE_USERNAME</strong>
              <br />
              For user email:
              <strong>ELECTRONICNEWSLETTERSERVICE_USERMAIL</strong>
              <br />
              For user id for unsub:
              <strong>ELECTRONICNEWSLETTER_USERID</strong>
              <br />
              For newsletter id for unsub:
              <strong>ELECTRONICNEWSLETTER_NEWSLETTERID</strong>
              <br />
              For newsletter's thumbnail url:
              <strong>ELECTRONICNEWSLETTER_NEWSLETTERIMAGE</strong>
              <br />
              For newsletters subscriber count:
              <strong>ELECTRONICNEWSLETTER_NEWSLETTERCUSTOMER_COUNT</strong>
              <br />
              For newsletters description:
              <strong>ELECTRONICNEWSLETTER_NEWSLETTERDESCRIPTION</strong>
              <div>
                <h1 className="text-xl font-sfpro">Unsubscribe Guide</h1>
                <p>
                  you should put unsubscribe link inside anchor &#40;
                  &lt;a&gt;&#41; tag
                </p>
                <p className="border-black p-2 border-1">
                  {APIURL}
                  /customers/ELECTRONICNEWSLETTER_USERID/newsletters/ELECTRONICNEWSLETTER_NEWSLETTERID/unsubscribe
                </p>
                <p>will enough... i think</p>
              </div>
            </p>
          </AccordionItem>
        </Accordion>
      </div>

      <form onSubmit={handleSubmit} className="min-h-[650px] p-5">
        {/* Subject Input Field */}
        <div className="mb-4">
          <label htmlFor="subject" className="block mb-2">
            Write here subject
          </label>
          <input
            type="text"
            id="subject"
            value={subject}
            onChange={(e) => setSubject(e.target.value)}
            className="w-full border p-2"
            placeholder="Enter a subject..."
            required
          />
        </div>

        {/* HTML Content Area */}
        <div className="mb-4">
          <label htmlFor="htmlContent" className="block mb-2">
            HTML İçeriği:
          </label>
          <textarea
            id="htmlContent"
            value={htmlContent}
            onChange={(e) => setHtmlContent(e.target.value)}
            className="w-full h-64 border p-2"
            placeholder="HTML içeriğinizi buraya yazın..."
            required
          />
        </div>

        {/* Preview Area */}
        <div className="mt-4">
          <h2>Önizleme:</h2>
          <div
            className="border p-2"
            dangerouslySetInnerHTML={{ __html: htmlContent }}
          />
        </div>

        {/* Submit Button */}
        <Button
          isLoading={isLoading}
          type="submit"
          fullWidth
          color="success"
          className="mt-4 p-2"
        >
          Send to Subscribers!
        </Button>
      </form>
      <Footer />
    </div>
  );
};
