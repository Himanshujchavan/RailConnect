import { Link } from "react-router-dom";
import { motion } from "framer-motion";
import Button from "../components/ui/Button";

export default function NotFound() {
  return (
    <div className="min-h-[calc(100vh-68px)] flex items-center justify-center px-5 py-16 bg-[radial-gradient(900px_400px_at_50%_0%,#DBEAFE_0%,transparent_60%)]">
      <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} className="text-center max-w-[520px]">
        <div className="text-[72px] leading-none mb-4">🚆</div>
        <h1 className="text-[34px] font-bold mb-3">Train lost the route</h1>
        <p className="text-muted text-[15px] mb-7">The page you tried to open does not exist, but the booking journey is still one click away.</p>
        <div className="flex justify-center gap-3 flex-wrap">
          <Link to="/"><Button>Go home</Button></Link>
          <Link to="/search"><Button variant="outline">Search train</Button></Link>
        </div>
      </motion.div>
    </div>
  );
}
