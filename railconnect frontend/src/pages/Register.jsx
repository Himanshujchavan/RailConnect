import { useNavigate, Link } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { motion } from "framer-motion";
import { toast } from "sonner";
import { useEffect, useState } from "react";
import Button from "../components/ui/Button";
import { Input, Field } from "../components/ui/Input";

const schema = z
  .object({
    name: z.string().min(2, "Enter your full name"),
    email: z.string().email("Enter a valid email"),
    phone: z.string().min(10, "Enter a valid phone number"),
    password: z.string().min(6, "At least 6 characters"),
    confirm: z.string(),
    terms: z.literal(true, { errorMap: () => ({ message: "You must accept the terms" }) }),
  })
  .refine((d) => d.password === d.confirm, { message: "Passwords do not match", path: ["confirm"] });

export default function Register() {
  const navigate = useNavigate();
  const { register, handleSubmit, watch, formState: { errors, isSubmitting } } = useForm({
    resolver: zodResolver(schema),
    defaultValues: { terms: true },
  });
  const [strength, setStrength] = useState("Weak");
  const passwordValue = watch("password") || "";

  useEffect(() => {
    if (!passwordValue) setStrength("Weak");
    else if (passwordValue.length >= 10) setStrength("Strong");
    else if (passwordValue.length >= 7) setStrength("Medium");
    else setStrength("Weak");
  }, [passwordValue]);

  const onSubmit = async () => {
    await new Promise((r) => setTimeout(r, 500));
    toast.success("Account created — welcome!");
    navigate("/dashboard");
  };

  return (
    <div className="min-h-[calc(100vh-68px)] flex items-center justify-center px-5 py-16 bg-[radial-gradient(900px_400px_at_80%_0%,#DBEAFE_0%,transparent_60%)]">
      <motion.div initial={{ opacity: 0, y: 14 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.35 }}
        className="w-full max-w-[420px] bg-white border border-border rounded-2xl shadow-lift p-9">
        <h2 className="text-[23px] font-bold mb-1.5">Create your account</h2>
        <p className="text-muted text-sm mb-6">Join RailConnect in under a minute.</p>

        <form onSubmit={handleSubmit(onSubmit)}>
          <Field label="Full name" error={errors.name?.message}><Input placeholder="Aarav Sharma" {...register("name")} /></Field>
          <Field label="Email address" error={errors.email?.message}><Input type="email" placeholder="you@example.com" {...register("email")} /></Field>
          <Field label="Phone number" error={errors.phone?.message}><Input placeholder="+91 98765 43210" {...register("phone")} /></Field>
          <Field label="Password" error={errors.password?.message}>
            <Input type="password" placeholder="Create a password" {...register("password")} />
            <div className="mt-2 text-[12.5px] text-muted">Password strength meter: {strength}</div>
          </Field>
          <Field label="Confirm password" error={errors.confirm?.message}><Input type="password" placeholder="Re-enter password" {...register("confirm")} /></Field>
          <label className="flex items-center gap-2 text-muted text-[13.5px] my-4">
            <input type="checkbox" {...register("terms")} /> I agree to the <span className="text-primary font-medium">Terms & Privacy Policy</span>
          </label>
          <Button type="submit" full size="lg" disabled={isSubmitting}>
            {isSubmitting ? "Creating…" : "Create account"}
          </Button>
        </form>
        <div className="text-center text-[13.5px] text-muted mt-5">
          Already have an account? <Link to="/login" className="text-primary font-semibold">Log in</Link>
        </div>
      </motion.div>
    </div>
  );
}
