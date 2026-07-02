import { cva } from "class-variance-authority";
import { motion } from "framer-motion";
import { cn } from "../../lib/utils";

export const buttonVariants = cva(
  "inline-flex items-center justify-center gap-2 rounded-lg font-semibold transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed whitespace-nowrap",
  {
    variants: {
      variant: {
        primary: "bg-primary text-white hover:bg-primary-dark shadow-sm",
        outline: "bg-white text-secondary border border-border hover:border-primary hover:text-primary",
        ghost: "bg-transparent text-secondary hover:bg-slate-100",
        accent: "bg-accent text-white hover:brightness-95",
      },
      size: {
        sm: "px-3.5 py-2 text-sm",
        md: "px-4.5 py-2.5 text-[14.5px]",
        lg: "px-6 py-3.5 text-[15.5px]",
      },
      full: { true: "w-full" },
    },
    defaultVariants: { variant: "primary", size: "md" },
  }
);

export default function Button({ className, variant, size, full, ...props }) {
  return (
    <motion.button
      whileHover={{ y: -1, scale: 1.01 }}
      whileTap={{ scale: 0.97 }}
      transition={{ duration: 0.15 }}
      className={cn(buttonVariants({ variant, size, full }), className)}
      {...props}
    />
  );
}
