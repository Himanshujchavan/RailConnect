import { useNavigate, Link } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { motion } from "framer-motion";
import { toast } from "sonner";
import Button from "../components/ui/Button";
import { Input, Field } from "../components/ui/Input";

const schema = z.object({ email: z.string().email("Enter a valid email") });

export default function ForgotPassword() {
  const navigate = useNavigate();
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm({ resolver: zodResolver(schema) });

  const onSubmit = async () => {
    await new Promise((resolve) => setTimeout(resolve, 450));
    toast.success("OTP sent to your email");
    navigate("/reset-password");
  };

  return (
    <div className="min-h-[calc(100vh-68px)] flex items-center justify-center px-5 py-16 bg-[radial-gradient(900px_400px_at_80%_0%,#DBEAFE_0%,transparent_60%)]">
      <motion.div initial={{ opacity: 0, y: 14 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.35 }} className="w-full max-w-[420px] bg-white border border-border rounded-2xl shadow-lift p-9">
        <h2 className="text-[23px] font-bold mb-1.5">Forgot password</h2>
        <p className="text-muted text-sm mb-6">Enter your email to receive an OTP for password reset.</p>

        <form onSubmit={handleSubmit(onSubmit)}>
          <Field label="Email address" error={errors.email?.message}>
            <Input type="email" placeholder="you@example.com" {...register("email")} />
          </Field>
          <Button type="submit" full size="lg" disabled={isSubmitting}>{isSubmitting ? "Sending…" : "Send OTP"}</Button>
        </form>

        <div className="text-center text-[13.5px] text-muted mt-5">
          Remembered it? <Link to="/login" className="text-primary font-semibold">Back to login</Link>
        </div>
      </motion.div>
    </div>
  );
}
