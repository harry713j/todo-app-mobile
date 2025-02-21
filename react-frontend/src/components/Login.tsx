import { loginSchema } from "../schema/loginSchema";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import Cookies from "js-cookie";
import { useMutation } from "@tanstack/react-query";
import { loginUser } from "../api";
import { Button } from "./ui/button";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "./ui/form";
import { Input } from "./ui/input";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "./ui/card";
import { Link, useNavigate } from "react-router-dom";
import { useToast } from "../hooks/use-toast";
import { LoaderCircle } from "lucide-react";

function Login() {
  const { toast } = useToast();
  const navigate = useNavigate();
  const form = useForm<z.infer<typeof loginSchema>>({
    resolver: zodResolver(loginSchema),
    mode: "onBlur",
    defaultValues: {
      username: "",
      password: "",
    },
  });

  const { mutate, isPending } = useMutation({
    mutationFn: loginUser,
    onSuccess: (data) => {
      if (typeof data === "string") {
        return;
      }

      const { accessToken, refreshToken } = data;

      Cookies.set("accessToken", accessToken);
      Cookies.set("refreshToken", refreshToken);

      navigate("/");
    },
    onError: (error) => {
      toast({
        variant: "destructive",
        title: error.message,
      });
    },
  });

  const onSubmit = (data: z.infer<typeof loginSchema>) => {
    mutate(data);
  };

  return (
    <>
      <Card className="w-2/5 h-4/5 px-8 py-6">
        <CardHeader className="mb-4">
          <CardTitle className="text-4xl text-center font-bold">
            Log In
          </CardTitle>
        </CardHeader>
        <CardContent>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
              <FormField
                control={form.control}
                name="username"
                render={({ field }) => (
                  <FormItem className="flex flex-col items-start space-y-1.5">
                    <FormLabel className="text-sm font-medium ">
                      Username
                    </FormLabel>
                    <FormControl>
                      <Input placeholder="Username" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="password"
                render={({ field }) => (
                  <FormItem className="flex flex-col items-start space-y-1.5">
                    <FormLabel className="text-sm font-medium ">
                      Password
                    </FormLabel>
                    <FormControl>
                      <Input
                        type="password"
                        placeholder="Password"
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <Button type="submit" disabled={isPending}>
                Log in
              </Button>
            </form>
          </Form>
        </CardContent>
        <CardFooter className="mt-4 w-full flex justify-center">
          <p className="text-sm font-normal text-center">
            Don't have an account?{" "}
            <Link
              className="text-blue-400 hover:text-blue-500 hover:underline "
              to={`/signup`}
            >
              Signup
            </Link>{" "}
            here.{" "}
          </p>
        </CardFooter>
      </Card>
      {isPending && (
        <div className="absolute top-0 left-0 w-full h-full z-40 bg-slate-500/50 flex justify-center items-center">
          <LoaderCircle className="w-8 h-8 animate-spin" />
        </div>
      )}
    </>
  );
}

export default Login;
