import { forwardRef } from "react";
import { cn } from "../../lib/utils";

export const Input = forwardRef(function Input({ className, ...props }, ref) {
  return (
    <input
      ref={ref}
      className={cn(
        "w-full px-3.5 py-2.5 rounded-lg border border-border text-[14.5px] bg-white",
        "focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary transition",
        className
      )}
      {...props}
    />
  );
});

export function Field({ label, error, children }) {
  return (
    <div className="mb-4">
      {label && <label className="block text-[13px] font-semibold text-secondary mb-1.5">{label}</label>}
      {children}
      {error && <p className="text-danger text-xs mt-1.5">{error}</p>}
    </div>
  );
}
