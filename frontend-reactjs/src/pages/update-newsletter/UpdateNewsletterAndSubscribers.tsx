import { http, httpError } from "@/assets/http";
import Footer from "@/components/Footer";
import Navigation from "@/components/Navigation";
import {
  Button,
  Input,
  Modal,
  ModalBody,
  ModalContent,
  ModalFooter,
  ModalHeader,
  useDisclosure,
} from "@nextui-org/react";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

export const UpdateNewsletterAndSubscribers = () => {
  const { newsletterId } = useParams();
  const [newsletter, setNewsletter] = useState<Newsletter>();
  const [customers, setCustomers] = useState<Customer[]>();
  const deleteSubscriberModal = useDisclosure();
  const updateSubsriberModal = useDisclosure();
  const updateNewsletterNameAndDescriptionModal = useDisclosure();
  const updateNewsletterImageModal = useDisclosure();

  const [selectedCustomerForUpdateOrDelete, setCustomer] = useState<Customer>();
  const [isLoading, setLoading] = useState<boolean>();
  const [selectedImageUrl, setImageUrl] = useState<string>();

  useEffect(() => {
    const getNewsletter = async () => {
      try {
        const response = await http.get(`/newsletters/${newsletterId}`);
        setNewsletter(response.data);
      } catch (err) {
        console.log(httpError(err));
      }
    };
    const getNewslettersSubscribers = async (newsletterId: string) => {
      try {
        const response = await http.get(
          `/customers/newsletters/${newsletterId}`
        );
        setCustomers(response.data);
      } catch (err) {
        console.log(httpError(err));
      }
    };

    getNewsletter();
    getNewslettersSubscribers(newsletterId!);
  }, [newsletterId]);

  const handleRegisteredDate = (date: string) => {
    let tarihVeSaat = date.split("T");
    let response =
      tarihVeSaat[1].substring(0, 5) + " " + tarihVeSaat[0].substring(0, 10);
    return response;
  };

  const handleDeleteSubscriber = async () => {
    //id zaten selectedUserId stateinden gelecek. bizde onu silecegiz.
    setLoading(true);
    try {
      await http.delete(`/customers/${selectedCustomerForUpdateOrDelete?.id}`);
    } catch (err) {
      console.log(httpError(err));
    }
    setLoading(false);

    //en sonda
    setCustomer({
      id: "null",
      email: "null",
      name: "null",
      registerDate: "null",
    });
    deleteSubscriberModal.onOpenChange();
    window.location.reload();
  };
  const handleUpdateSubscriber = async (
    e: React.FormEvent<HTMLFormElement>
  ) => {
    e.preventDefault();
    const form = new FormData(e.currentTarget);
    const data = Object.fromEntries(form.entries());
    //datayi al apiye put olarak yolla. bizi bozmaz.
    setLoading(true);
    try {
      await http.put(
        `/customers/${selectedCustomerForUpdateOrDelete?.id}`,
        data
      );
    } catch (err) {
      console.log(httpError(err));
    }
    setLoading(false);
    //en sonda
    setCustomer({
      id: "null",
      email: "null",
      name: "null",
      registerDate: "null",
    });
    updateSubsriberModal.onOpenChange();
    window.location.reload();
  };
  const handleUpdateNewsletterNameAndDescription = async (
    e: React.FormEvent<HTMLFormElement>
  ) => {
    e.preventDefault();
    const form = new FormData(e.currentTarget);
    const data = Object.fromEntries(form.entries());
    setLoading(true);
    try {
      await http.put(
        `/newsletters/${newsletter?.id}/name-and-description`,
        data
      );
    } catch (error) {
      console.log(httpError(error));
    }
    setLoading(false);
    //datayi apiye aticaz.
    updateNewsletterNameAndDescriptionModal.onOpenChange();
    window.location.reload();
  };
  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]; // Kullanıcı tarafından seçilen ilk dosya
    if (file) {
      const imageUrl = URL.createObjectURL(file); // Seçilen resmin URL'sini oluşturuyoruz
      setImageUrl(imageUrl);
    }
  };

  const handleUpdateNewsletterImage = async (
    e: React.FormEvent<HTMLFormElement>
  ) => {
    e.preventDefault();
    const form = new FormData(e.currentTarget);
    //formu apiye aticaz.
    setLoading(true);

    try {
      await http.put(`/newsletters/${newsletter?.id}/image`, form);
    } catch (error) {
      console.log(httpError(error));
    }
    setLoading(false);

    updateNewsletterImageModal.onOpenChange();
    window.location.reload();
  };
  return (
    <div>
      <Navigation />
      <div className="grid grid-cols-4 mx-20 min-h-[600px]">
        <div className="col-span-1">
          <div className="border-3 border-green-300 ">
            <h1 className="grid place-items-center font-bold font-sfpro text-xl pt-5">
              {newsletter?.name}
            </h1>
            <img
              src={newsletter?.imageUrl}
              alt="newsletter image"
              className="mx-auto h-[450px] w-[450px]"
            />
            <h1 className="font-bold font-sfpro text-xl px-5">Description</h1>
            <p className="font-sfpro p-5">{newsletter?.description}</p>
            <div className="px-5">
              <h1 className="grid place-items-center font-bold font-sfpro text-xl">
                Update Menu
              </h1>
              <Button
                fullWidth
                className="my-2"
                color="secondary"
                onClick={updateNewsletterNameAndDescriptionModal.onOpenChange}
              >
                Update Name And Description
              </Button>
              <Button
                fullWidth
                className="my-2"
                color="success"
                onClick={updateNewsletterImageModal.onOpenChange}
              >
                Update Image
              </Button>
              {/* normal datalari guncelleme modali */}
              <Modal
                isOpen={updateNewsletterNameAndDescriptionModal.isOpen}
                onOpenChange={
                  updateNewsletterNameAndDescriptionModal.onOpenChange
                }
              >
                <ModalContent>
                  {(onClose) => (
                    <>
                      <ModalHeader className="flex flex-col gap-1">
                        Update Newsletter
                      </ModalHeader>
                      <ModalBody>
                        <form
                          id="updatenewsletternameanddescription"
                          onSubmit={handleUpdateNewsletterNameAndDescription}
                        >
                          <Input
                            className="my-2"
                            name="name"
                            label="Name"
                            defaultValue={newsletter?.name}
                          />
                          <Input
                            className="my-2"
                            name="description"
                            label="Description"
                            defaultValue={newsletter?.description}
                          />
                        </form>
                      </ModalBody>
                      <ModalFooter className="grid grid-cols-5">
                        <Button
                          className="col-span-4"
                          form="updatenewsletternameanddescription"
                          color="secondary"
                          type="submit"
                        >
                          Update
                        </Button>
                        <Button
                          className="col-span-1"
                          color="secondary"
                          variant="light"
                          onPress={onClose}
                        >
                          Close
                        </Button>
                      </ModalFooter>
                    </>
                  )}
                </ModalContent>
              </Modal>
              {/* resim yukleme modali */}
              <Modal
                isOpen={updateNewsletterImageModal.isOpen}
                onOpenChange={updateNewsletterImageModal.onOpenChange}
              >
                <ModalContent>
                  {(onClose) => (
                    <>
                      <ModalHeader className="flex flex-col gap-1">
                        Update Newsletter Image
                      </ModalHeader>
                      <ModalBody>
                        <div className="grid grid-cols-2">
                          <div className="col-span-1">
                            <div>
                              <h1 className=" text-xl">Old Image</h1>
                              <img
                                className="max-h-[300px]"
                                src={newsletter?.imageUrl}
                                alt="newsletter image"
                              />
                            </div>
                          </div>
                          <div className="col-span-1">
                            <div>
                              <h1 className="font-bold text-xl">New Image</h1>
                              <img
                                className="max-h-[300px]"
                                src={selectedImageUrl}
                                alt="newsletter image"
                              />
                            </div>
                          </div>
                        </div>

                        <form
                          id="updatingnewsletterimage"
                          onSubmit={handleUpdateNewsletterImage}
                        >
                          <Input
                            type="file"
                            name="file"
                            onChange={handleImageChange}
                          />
                        </form>
                      </ModalBody>
                      <ModalFooter className="grid grid-cols-5">
                        <Button
                          isLoading={isLoading}
                          type="submit"
                          className="col-span-4"
                          form="updatingnewsletterimage"
                          color="success"
                        >
                          Update
                        </Button>
                        <Button
                          className="col-span-1"
                          color="success"
                          variant="light"
                          onPress={onClose}
                        >
                          Close
                        </Button>
                      </ModalFooter>
                    </>
                  )}
                </ModalContent>
              </Modal>
            </div>
          </div>
        </div>
        <div className="col-span-3">
          <div className="overflow-x-auto">
            {/* Tablo Başlığı */}
            <div className="min-w-full divide-y divide-gray-200">
              <div className="bg-gray-50">
                <div className="grid grid-cols-5 px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  <div>Name</div>
                  <div>Email</div>
                  <div>Register Date</div>
                  <div>Update</div>
                  <div>Delete</div>
                </div>
              </div>
              {/* Tablo Satırları */}
              <div className="bg-white divide-y divide-gray-200">
                {customers?.map((sub, index) => (
                  <div
                    key={index}
                    className="grid grid-cols-5 px-6 py-4 whitespace-nowrap text-sm text-gray-500"
                  >
                    <div>{sub.name}</div>
                    <div>{sub.email}</div>
                    <div>
                      {sub.registerDate
                        ? handleRegisteredDate(sub.registerDate)
                        : "Bir problem var, tarih yok."}
                    </div>
                    <div>
                      <Button
                        color="warning"
                        onClick={() => {
                          setCustomer(sub);
                          updateSubsriberModal.onOpen();
                        }}
                      >
                        Update
                      </Button>
                    </div>
                    <div>
                      <Button
                        color="danger"
                        onClick={() => {
                          setCustomer(sub);
                          deleteSubscriberModal.onOpen();
                        }}
                      >
                        Delete
                      </Button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* update subscriber modal */}
          <Modal
            isOpen={updateSubsriberModal.isOpen}
            onOpenChange={updateSubsriberModal.onOpenChange}
          >
            <ModalContent>
              {(onClose) => (
                <>
                  <ModalHeader className="flex flex-col gap-1">
                    Update Modal
                  </ModalHeader>
                  <ModalBody>
                    <form onSubmit={handleUpdateSubscriber} id="update">
                      <Input
                        className="my-5"
                        name="email"
                        label="E-Mail Address"
                        defaultValue={selectedCustomerForUpdateOrDelete?.email}
                      />
                      <Input
                        name="name"
                        label="Name"
                        defaultValue={selectedCustomerForUpdateOrDelete?.name}
                      />
                    </form>
                  </ModalBody>
                  <ModalFooter className="grid grid-cols-5">
                    <Button
                      className="col-span-4"
                      form="update"
                      type="submit"
                      color="warning"
                      isLoading={isLoading}
                    >
                      Update
                    </Button>
                    <Button
                      className="col-span-1"
                      color="warning"
                      variant="ghost"
                      onPress={onClose}
                    >
                      Close
                    </Button>
                  </ModalFooter>
                </>
              )}
            </ModalContent>
          </Modal>
          {/* delete subscriber  modal */}
          <Modal
            isOpen={deleteSubscriberModal.isOpen}
            onOpenChange={deleteSubscriberModal.onOpenChange}
          >
            <ModalContent>
              {(onClose) => (
                <>
                  <ModalHeader className="flex flex-col gap-1">
                    Delete Modal
                  </ModalHeader>
                  <ModalBody>
                    <h1 className="font-bold font-sfpro text-xl">
                      Are you sure for deleting subscriber?
                    </h1>
                  </ModalBody>
                  <ModalFooter className="grid grid-cols-5">
                    <Button
                      isLoading={isLoading}
                      color="danger"
                      className="col-span-4"
                      onClick={handleDeleteSubscriber}
                    >
                      Delete
                    </Button>
                    <Button
                      className="col-span-1"
                      color="danger"
                      variant="ghost"
                      onPress={onClose}
                    >
                      Cancel
                    </Button>
                  </ModalFooter>
                </>
              )}
            </ModalContent>
          </Modal>
        </div>
      </div>
      <Footer />
    </div>
  );
};
