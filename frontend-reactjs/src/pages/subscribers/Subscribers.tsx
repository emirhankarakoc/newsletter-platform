import { http, httpError } from "@/assets/http";
import Footer from "@/components/Footer";
import Navigation from "@/components/Navigation";
import {
  Button,
  Modal,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalFooter,
  Input,
  useDisclosure,
} from "@nextui-org/react";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

export const Subscribers = () => {
  const { newsletterId } = useParams();
  const [subs, setSubs] = useState<Customer[]>();
  const [newsletter, setNewsletter] = useState<Newsletter>();
  const [selectedSub, setSelectedSub] = useState<Customer | null>(null); // Güncellenecek müşteri
  const [newName, setNewName] = useState(""); // Güncelleme formu için
  const [newEmail, setNewEmail] = useState(""); // Güncelleme formu için
  const [responseMessage, setResponseMessage] = useState<string>(""); // Genel response mesajı
  const deleteModal = useDisclosure(); // Silme modalı kontrolü
  const updateModal = useDisclosure(); // Güncelleme modalı kontrolü

  useEffect(() => {
    const handleGetNewsletter = async () => {
      try {
        const response = await http.get(`/newsletters/${newsletterId}`);
        setNewsletter(response.data);
      } catch (error) {
        httpError(error);
      }
    };

    const handleGetSubscribers = async () => {
      try {
        const response = await http.get(
          `/customers/newsletters/${newsletterId}`
        );
        setSubs(response.data);
      } catch (error) {
        httpError(error);
      }
    };

    handleGetNewsletter();
    handleGetSubscribers();
  }, [newsletterId]);

  const handleDelete = async () => {
    try {
      await http.delete(`/customers/${selectedSub?.id}`);
      const updatedSubs = subs?.filter((sub) => sub.id !== selectedSub?.id);
      setSubs(updatedSubs);
      setResponseMessage("Customer deleted successfully.");
      deleteModal.onOpenChange(); // Modalı kapat
    } catch (err) {
      setResponseMessage(
        "Customer couldn't be deleted. Message: " + httpError(err)
      );
      httpError(err);
      deleteModal.onOpenChange();
    } finally {
      setTimeout(() => setResponseMessage(""), 3000); // 3 saniye sonra mesajı temizle
    }
  };

  const handleUpdate = async () => {
    try {
      const body = {
        name: newName,
        email: newEmail,
      };

      const response = await http.put(`/customers/${selectedSub?.id}`, body);
      setResponseMessage("Customer updated successfully.");

      const updatedCustomer = response.data;
      const updatedSubs = subs?.map((sub) =>
        sub.id === updatedCustomer.id ? updatedCustomer : sub
      );
      setSubs(updatedSubs);
      updateModal.onOpenChange(); // Modalı kapat
    } catch (err) {
      setResponseMessage(
        "Customer couldn't be updated. Message: " + httpError(err)
      );
      httpError(err);
      updateModal.onOpenChange();
    } finally {
      setTimeout(() => setResponseMessage(""), 3000);
    }
  };

  const handleRegisteredDate = (date: string) => {
    let [datePart, timePart] = date.split("T");
    const time = timePart.substring(0, 5);
    return `${time} ${datePart.substring(0, 10)}`;
  };

  return (
    <div>
      <Navigation />
      <div className="grid grid-cols-4 px-24 min-h-[600px]">
        {/* Müşteriler tablosu */}
        <div className="col-span-4 p-10">
          <div className="overflow-x-auto">
            <div className="min-w-full divide-y divide-gray-200">
              {/* Başlık */}
              <div className="bg-gray-50">
                <div className="grid grid-cols-5 px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  <div>Name</div>
                  <div>Email</div>
                  <div>Registered Date</div>
                  <div>Update</div>
                  <div>Delete</div>
                </div>
              </div>
              {/* Satırlar */}
              <div className="bg-white divide-y divide-gray-200">
                {subs?.map((sub, index) => (
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
                        size="sm"
                        onClick={() => {
                          setSelectedSub(sub);
                          setNewName(sub.name);
                          setNewEmail(sub.email);
                          updateModal.onOpenChange();
                        }}
                      >
                        Update
                      </Button>
                    </div>
                    <div>
                      <Button
                        color="danger"
                        size="sm"
                        onClick={() => {
                          setSelectedSub(sub);
                          deleteModal.onOpenChange();
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
        </div>
      </div>

      {/* Silme Modalı */}
      <Modal
        isOpen={deleteModal.isOpen}
        onOpenChange={() => {
          deleteModal.onOpenChange();
          setResponseMessage(""); // Modal kapandığında mesajı temizle
        }}
      >
        <ModalContent>
          <ModalHeader>
            Are you sure you want to delete subscriber {selectedSub?.name} from
            newsletter {newsletter?.name}?
          </ModalHeader>
          <ModalBody>
            <p>This action cannot be undone.</p>
          </ModalBody>
          <ModalFooter className="flex flex-col gap-2">
            <Button color="danger" onClick={handleDelete}>
              Delete
            </Button>
            <Button
              color="danger"
              variant="ghost"
              onPress={deleteModal.onOpenChange}
            >
              Cancel
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      {/* Güncelleme Modalı */}
      <Modal
        isOpen={updateModal.isOpen}
        onOpenChange={() => {
          updateModal.onOpenChange();
          setResponseMessage("");
        }}
      >
        <ModalContent>
          <ModalHeader>Update Subscriber</ModalHeader>
          <ModalBody>
            <Input
              label="Name"
              value={newName}
              onChange={(e) => setNewName(e.target.value)}
              placeholder="Enter new name"
            />
            <Input
              label="Email"
              value={newEmail}
              onChange={(e) => setNewEmail(e.target.value)}
              placeholder="Enter new email"
            />
          </ModalBody>
          <ModalFooter className="flex flex-col gap-2">
            <Button color="warning" onPress={handleUpdate}>
              Update
            </Button>
            <Button
              color="danger"
              variant="ghost"
              onPress={updateModal.onOpenChange}
            >
              Cancel
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      {/* Genel response mesajı */}
      {responseMessage && (
        <div
          className={`fixed bottom-5 right-5 p-4 text-white rounded-lg shadow-lg transition duration-300 ${
            responseMessage.includes("successfully")
              ? "bg-green-500"
              : "bg-red-500"
          }`}
        >
          {responseMessage}
        </div>
      )}

      <Footer />
    </div>
  );
};
