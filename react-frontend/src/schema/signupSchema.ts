import z from "zod";

export const usernameValidation = z
  .string()
  .min(4, {
    message: "Username must be at least 4 character long",
  })
  .max(20, "Username must not contain more than 20 characters")
  .regex(
    /^[a-zA-Z0-9]+$/,
    "Username should only contain alphabets and numbers"
  );

export const signupSchema = z.object({
  username: usernameValidation,
  email: z.string().email({ message: "Invalid email address" }),
  password: z
    .string()
    .min(8, { message: "Password must be at least 8 characters" })
    .regex(/[a-zA-Z]/, "Password must contain alphabets")
    .regex(/[0-9]/, "Password must contain at least one number")
    .regex(/[@#$%&_-]/, "Password must contain at least one character @#$%&_-"),
  name: z
    .string()
    .min(3, { message: "Name must be at least 3 characters" })
    .max(20, "Name must not be more than 20 characters"),
  dateOfBirth: z.date(),
});
