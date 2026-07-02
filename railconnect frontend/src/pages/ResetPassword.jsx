import { useNavigate, Link } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { motion } from "framer-motion";
import { toast } from "sonner";
import Button from "../components/ui/Button";
import { Input, Field } from "../components/ui/Input";

const schema = z.object({
  otp: z.string().min(4, "Enter the OTP"),
  password: z.string().min(6, "At least 6 characters"),
  confirm: z.string(),
}).refine((data) => data.password === data.confirm, { message: "Passwords do not match", path: ["confirm"] });

export default function ResetPassword() {
  const navigate = useNavigate();
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm({ resolver: zodResolver(schema) });

  const onSubmit = async () => {
    await new Promise((resolve) => setTimeout(resolve, 450));
    toast.success("Password updated");
    navigate("/login");
  };

  return (
    <div className="min-h-[calc(100vh-68px)] flex items-center justify-center px-5 py-16 bg-[radial-gradient(900px_400px_at_80%_0%,#DBEAFE_0%,transparent_60%)]">
      <motion.div initial={{ opacity: 0, y: 14 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.35 }} className="w-full max-w-[420px] bg-white border border-border rounded-2xl shadow-lift p-9">
        <h2 className="text-[23px] font-bold mb-1.5">Reset password</h2>
        <p className="text-muted text-sm mb-6">Verify the OTP and choose a new password.</p>

        <form onSubmit={handleSubmit(onSubmit)}>
          <Field label="OTP" error={errors.otp?.message}><Input placeholder="123456" {...register("otp")} /></Field>
          <Field label="New password" error={errors.password?.message}><Input type="password" placeholder="New password" {...register("password")} /></Field>
          <Field label="Confirm password" error={errors.confirm?.message}><Input type="password" placeholder="Confirm password" {...register("confirm")} /></Field>
          <Button type="submit" full size="lg" disabled={isSubmitting}>{isSubmitting ? "Updating…" : "Reset password"}</Button>
        </form>

        <div className="text-center text-[13.5px] text-muted mt-5">
          Back to <Link to="/login" className="text-primary font-semibold">login</Link>
        </div>
      </motion.div>
    </div>
  );
}
