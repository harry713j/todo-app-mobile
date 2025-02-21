import { z } from "zod";
import { SERVER } from "../constants";
import { loginSchema } from "../schema/loginSchema";
import { signupSchema } from "../schema/signupSchema";

const serverAddress = `${SERVER}/api/v1`;

export const registerUser = async (
  data: z.infer<typeof signupSchema>
): Promise<Tokens | string> => {
  const requestData: RegisterUser = {
    username: data.username,
    email: data.email,
    password: data.password,
    name: data.name,
    date_of_birth: data.dateOfBirth.toISOString().split("T")[0],
  };

  const response = await fetch(`${serverAddress}/auth/signup`, {
    method: "POST",
    body: JSON.stringify(requestData),
    headers: {
      "Content-Type": "application/json",
    },
  });

  return await response.json();
};

export const loginUser = async (
  data: z.infer<typeof loginSchema>
): Promise<Tokens | string> => {
  const response = await fetch(`${serverAddress}/auth/login`, {
    method: "POST",
    body: JSON.stringify(data),
    headers: {
      "Content-Type": "application/json",
    },
  });

  return await response.json();
};

// User:
export const getUser = async (username: string): Promise<UserData | string> => {
  const response = await fetch(`${serverAddress}/users/${username}`);

  return await response.json();
};

export const updateUser = async (
  username: string,
  data: z.infer<typeof signupSchema>
): Promise<UserData | string> => {
  const requestData: RegisterUser = {
    username: data.username,
    email: data.email,
    password: data.password,
    name: data.name,
    date_of_birth: data.dateOfBirth.toISOString().split("T")[0],
  };

  const response = await fetch(`${serverAddress}/users/${username}`, {
    method: "PATCH",
    body: JSON.stringify(requestData),
    headers: {
      "Content-Type": "application/json",
    },
  });

  return await response.json();
};

export const deleteUser = async (username: string): Promise<string> => {
  const response = await fetch(`${serverAddress}/users/${username}`, {
    method: "DELETE",
  });

  return await response.json();
};

// tasks
export const getAllCategories = async () => {};
