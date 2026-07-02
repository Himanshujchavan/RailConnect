import { useNavigate, Link } from "react-router-dom";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { motion } from "framer-motion";
import { toast } from "sonner";
import { Chrome, Eye, EyeOff } from "lucide-react";
import Button from "../components/ui/Button";
import { Input, Field } from "../components/ui/Input";

const schema = z.object({
  identifier: z.string().min(3, "Enter your email or mobile number"),
  password: z.string().min(6, "Password must be at least 6 characters"),
  remember: z.boolean().optional(),
});

export default function Login() {
  const navigate = useNavigate();
  const [mode, setMode] = useState("email");
  const [showPassword, setShowPassword] = useState(false);
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm({
    resolver: zodResolver(schema),
    defaultValues: { identifier: "aarav.sharma@email.com", password: "railconnect", remember: true },
  });

  const onSubmit = async () => {
    await new Promise((r) => setTimeout(r, 500));
    toast.success("Welcome back!");
    navigate("/dashboard");
  };

  const onError = () => {
    const el = document.getElementById("login-card");
    el.animate([{ transform: "translateX(0)" }, { transform: "translateX(-8px)" }, { transform: "translateX(8px)" }, { transform: "translateX(0)" }], { duration: 300 });
  };

  return (
    <div className="min-h-[calc(100vh-68px)] flex items-center justify-center px-5 py-16 bg-[radial-gradient(900px_400px_at_80%_0%,#DBEAFE_0%,transparent_60%)]">
      <motion.div id="login-card" initial={{ opacity: 0, scale: 0.95, y: 14 }} animate={{ opacity: 1, scale: 1, y: 0 }}
        transition={{ duration: 0.35 }} className="w-full max-w-[420px] bg-white border border-border rounded-2xl shadow-lift p-9">
        <h2 className="text-[23px] font-bold mb-1.5">Welcome back</h2>
        <p className="text-muted text-sm mb-6">Log in to manage your bookings.</p>

        <form onSubmit={handleSubmit(onSubmit, onError)}>
          <div className="grid grid-cols-2 gap-2 mb-4">
            <button type="button" onClick={() => setMode("email")} className={`px-3 py-2 rounded-lg border text-[13px] font-semibold ${mode === "email" ? "border-primary bg-indigo-50 text-primary" : "border-border text-muted"}`}>
              Email login
            </button>
            <button type="button" onClick={() => setMode("mobile")} className={`px-3 py-2 rounded-lg border text-[13px] font-semibold ${mode === "mobile" ? "border-primary bg-indigo-50 text-primary" : "border-border text-muted"}`}>
              Mobile login
            </button>
          </div>
          <Field label={mode === "mobile" ? "Mobile number" : "Email address"} error={errors.identifier?.message}>
            <Input type={mode === "mobile" ? "tel" : "email"} placeholder={mode === "mobile" ? "+91 98765 43210" : "you@example.com"} {...register("identifier")} />
          </Field>
          <Field label="Password" error={errors.password?.message}>
            <div className="relative">
              <Input type={showPassword ? "text" : "password"} placeholder="••••••••" {...register("password")} className="pr-11" />
              <button type="button" aria-label="Toggle password visibility" onClick={() => setShowPassword((value) => !value)} className="absolute inset-y-0 right-0 px-3 text-muted hover:text-primary">
                {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
              </button>
            </div>
          </Field>
          <div className="flex justify-between items-center text-[13.5px] my-4">
            <label className="flex items-center gap-2 text-muted">
              <input type="checkbox" {...register("remember")} /> Remember me
            </label>
            <Link to="/forgot-password" className="text-primary font-semibold">Forgot password?</Link>
          </div>
          <Button type="submit" full size="lg" disabled={isSubmitting}>
            {isSubmitting ? "Logging in…" : "Log in"}
          </Button>
        </form>

        <div className="flex items-center gap-3 my-5.5 text-muted text-[12.5px]">
          <span className="flex-1 h-px bg-border" /> OR <span className="flex-1 h-px bg-border" />
        </div>
        <Button variant="outline" full onClick={() => toast.info("Google sign-in coming soon")}>
          <Chrome size={16} /> Continue with Google
        </Button>
        <div className="text-center text-[13.5px] text-muted mt-5">
          Don't have an account? <Link to="/register" className="text-primary font-semibold">Sign up</Link>
        </div>
      </motion.div>
    </div>
  );
}
