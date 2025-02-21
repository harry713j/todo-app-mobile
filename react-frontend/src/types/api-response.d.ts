interface Tokens {
  accessToken: string;
  refreshToken: string;
}

interface RegisterUser {
  name: string;
  username: string;
  email: string;
  date_of_birth: string;
  password: string;
}

interface UserData {
  userId: string;
  username: string;
  name: string;
  email: string;
  dateOfBirth: Date;
}
