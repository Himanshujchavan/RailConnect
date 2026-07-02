import { Link, NavLink } from "react-router-dom";
import { motion } from "framer-motion";
import { TrainFront, Moon, Sun } from "lucide-react";
import { useTheme } from "next-themes";
import Button from "../ui/Button";

const links = [
  { to: "/", label: "Home" },
  { to: "/search", label: "Train Search" },
  { to: "/pnr", label: "PNR Status" },
  { to: "/booking/history", label: "Bookings" },
  { to: "/about", label: "About" },
  { to: "/contact", label: "Contact" },
  { to: "/help", label: "Help" },
];

export default function Navbar() {
  const { theme, setTheme } = useTheme();

  return (
    <motion.header
      initial={{ y: -16, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ duration: 0.35 }}
      className="sticky top-0 z-50 bg-white/85 backdrop-blur-md border-b border-border"
    >
      <div className="max-w-[1180px] mx-auto px-6 h-[68px] flex items-center justify-between">
        <Link to="/" className="flex items-center gap-2.5 font-display font-bold text-[19px]">
          <span className="w-[34px] h-[34px] rounded-[9px] bg-gradient-to-br from-primary to-blue-900 text-white flex items-center justify-center">
            <TrainFront size={18} />
          </span>
          RailConnect
        </Link>

        <nav className="hidden md:flex items-center gap-1.5">
          {links.map((l) => (
            <NavLink
              key={l.to}
              to={l.to}
              className={({ isActive }) =>
                `px-3.5 py-2 rounded-lg text-[14.5px] font-medium transition ${
                  isActive ? "text-secondary bg-indigo-50" : "text-muted hover:text-secondary hover:bg-slate-100"
                }`
              }
            >
              {l.label}
            </NavLink>
          ))}
        </nav>

        <div className="flex items-center gap-2.5">
          <button
            aria-label="Toggle dark mode"
            onClick={() => setTheme(theme === "dark" ? "light" : "dark")}
            className="w-9 h-9 rounded-lg border border-border flex items-center justify-center text-muted hover:text-primary hover:border-primary transition"
          >
            {theme === "dark" ? <Sun size={16} /> : <Moon size={16} />}
          </button>
          <Link to="/login">
            <Button variant="ghost" size="sm">Log in</Button>
          </Link>
          <Link to="/register">
            <Button variant="primary" size="sm">Sign up</Button>
          </Link>
        </div>
      </div>
    </motion.header>
  );
}
