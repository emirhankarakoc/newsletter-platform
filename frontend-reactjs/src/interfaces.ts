interface User {
  id: string;
  email: string;
  role: string;
  userStatus: UserStatus;
  googleTokenEmailAddress: string;
}

type UserStatus = "GOOGLE_VERIFICATED" | "GOOGLE_NOT_VERIFICATED";

interface OAuth2Account {
  id: string;
  email: string;
  family_name: string;
  given_name: string;
  gmailId: string;
  name: string;
  picture: string;
  verified_email: boolean;
  access_token: string;
}
interface Newsletter {
  id: string;
  name: string;
  ownerUserId: string;
  description: string;
  imageUrl: string;
  imageId: string;
  customers: Customer[];
}
interface Customer {
  id: string;
  name: string;
  email: string;
  registerDate: string;
}

interface MailPreview {
  id: string;
  name: string;
  createdDate: string;
  updatedDate: string;
  htmlContent: string;
  ownerId: string;
}

interface Image {
  id: string;
  imageUrl: string;
  userId: string;
}

// Props arayüzünü tanımlıyoruz
interface GoogleVerificatedDashboardProps {
  newsletters?: Newsletter[];
  mailPreviews?: MailPreview[];
}
