import { Link } from "react-router-dom";
import { motion } from "framer-motion";

export default function PageHeader({ crumbs = [], title, subtitle, steps, activeStep }) {
  return (
    <div className="bg-white border-b border-border py-6">
      <div className="max-w-[1180px] mx-auto px-6">
        <div className="text-[13px] text-muted mb-2">
          {crumbs.map((c, i) => (
            <span key={i}>
              {c.to ? <Link to={c.to} className="text-primary">{c.label}</Link> : c.label}
              {i < crumbs.length - 1 && " / "}
            </span>
          ))}
        </div>
        <h1 className="text-[22px] font-bold">
          {title} {subtitle && <span className="text-muted font-normal text-[15px]">{subtitle}</span>}
        </h1>

        {steps && (
          <div className="flex mt-5">
            {steps.map((s, i) => (
              <div key={s} className={`flex-1 text-center pb-3 border-b-[3px] font-semibold text-[13.5px] relative ${
                i < activeStep ? "border-accent text-accent" : i === activeStep ? "border-primary text-primary" : "border-border text-muted"
              }`}>
                {i + 1} · {s}
                {i === activeStep && (
                  <motion.div layoutId="step-underline" className="absolute -bottom-[3px] left-0 right-0 h-[3px] bg-primary" />
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
