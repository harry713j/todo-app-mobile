import { signupSchema } from "../schema/signupSchema";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import Cookies from "js-cookie";
import { useMutation } from "@tanstack/react-query";
import { format } from "date-fns";
import { registerUser } from "../api";
import { Calendar } from "./ui/calendar";
import { Popover, PopoverContent, PopoverTrigger } from "./ui/popover";
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

function Signup() {
  const { toast } = useToast();
  const navigate = useNavigate();
  const form = useForm<z.infer<typeof signupSchema>>({
    resolver: zodResolver(signupSchema),
    mode: "onBlur",
    defaultValues: {
      username: "",
      email: "",
      password: "",
      name: "",
      dateOfBirth: new Date(),
    },
  });
  const { mutate, isPending } = useMutation({
    mutationFn: registerUser,
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

  const onSubmit = async (data: z.infer<typeof signupSchema>) => {
    mutate(data);
  };

  return (
    <>
      <Card className="w-2/5 px-8 py-6">
        <CardHeader className="mb-4">
          <CardTitle className="text-4xl text-center font-bold">
            Sign Up
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
                name="email"
                render={({ field }) => (
                  <FormItem className="flex flex-col items-start space-y-1.5">
                    <FormLabel className="text-sm font-medium ">
                      Email
                    </FormLabel>
                    <FormControl>
                      <Input placeholder="Email" {...field} />
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
              <FormField
                control={form.control}
                name="name"
                render={({ field }) => (
                  <FormItem className="flex flex-col items-start space-y-1.5">
                    <FormLabel className="text-sm font-medium ">Name</FormLabel>
                    <FormControl>
                      <Input placeholder="Name" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="dateOfBirth"
                render={({ field }) => (
                  <FormItem className="flex flex-col items-start space-y-1.5">
                    <FormLabel className="text-sm font-medium ">
                      Date of Birth
                    </FormLabel>
                    <Popover>
                      <PopoverTrigger asChild>
                        <FormControl>
                          <Button className="w-full" variant="outline">
                            {field.value
                              ? format(field.value, "PPP")
                              : "Pick a date"}
                          </Button>
                        </FormControl>
                      </PopoverTrigger>
                      <PopoverContent>
                        <Calendar
                          mode="single"
                          selected={field.value}
                          onSelect={field.onChange}
                          disabled={(date) =>
                            date > new Date() || date < new Date("1900-01-01")
                          }
                          initialFocus
                        />
                      </PopoverContent>
                    </Popover>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <Button type="submit" disabled={isPending}>
                Register
              </Button>
            </form>
          </Form>
        </CardContent>
        <CardFooter className="mt-4 w-full flex justify-center">
          <p className="text-sm font-normal text-center">
            Already have an account?{" "}
            <Link
              className="text-blue-400 hover:text-blue-500 hover:underline "
              to={`/login`}
            >
              Login
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

export default Signup;
