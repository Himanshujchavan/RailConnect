import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { toast } from "sonner";
import { useApp } from "../context/AppContext";
import { TRAINS } from "../data/trains";
import PageHeader from "../components/layout/PageHeader";
import { Card } from "../components/ui/Card";
import { Input } from "../components/ui/Input";
import Button from "../components/ui/Button";

export default function PNRStatus() {
  const navigate = useNavigate();
  const { search, selectedTrain, selectedClass, selectedSeats, pnr, setPnr } = useApp();
  const [value, setValue] = useState(pnr || "");
  const [result, setResult] = useState(null);
  const t = selectedTrain || TRAINS[0];

  const lookup = () => {
    if (value.trim().length < 10) {
      toast.error("Enter a valid 10-digit PNR");
      return;
    }
    setPnr(value.trim());
    setResult(true);
  };

  return (
    <div>
      <PageHeader crumbs={[{ label: "Home", to: "/" }, { label: "PNR Status" }]} title="Check PNR status" />

      <motion.div initial={{ opacity: 0, y: 14 }} animate={{ opacity: 1, y: 0 }}
        className="max-w-[520px] mx-auto mt-10 mb-8 px-5">
        <Card className="p-8">
          <p className="text-muted text-[13.5px]">Enter your 10-digit PNR number to view live booking status.</p>
          <div className="flex gap-2.5 mt-4">
            <Input maxLength={10} placeholder="0123456789" value={value}
              onChange={(e) => setValue(e.target.value)} className="font-mono text-[16px] tracking-widest" />
            <Button onClick={lookup}>Check</Button>
          </div>
        </Card>
      </motion.div>

      <AnimatePresence>
        {result && (
          <motion.div initial={{ opacity: 0, y: 14 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0 }}
            className="max-w-[640px] mx-auto px-5 mb-16">
            <Card className="p-6">
              <Row label="PNR" value={<span className="font-mono">{pnr}</span>} />
              <Row label="Train" value={t.name} />
              <Row label="Journey" value={`${search.from} → ${search.to}, ${search.date}`} />
              <Row label="Class · Coach · Seat" value={`${selectedClass || "3A"} · B2 · ${selectedSeats[0] || 12}`} />
              <div className="flex justify-between text-[13.5px] py-2 text-muted items-center">
                <span>Status</span>
                <span className="px-3 py-1 rounded-full text-xs font-bold bg-emerald-50 text-emerald-600">Confirmed</span>
              </div>
              <div className="flex gap-2 mt-4 flex-wrap">
                <Button variant="outline" size="sm" onClick={() => navigate("/journey")}>Journey</Button>
                <Button variant="outline" size="sm" onClick={() => navigate("/booking/history")}>History</Button>
              </div>
            </Card>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}

function Row({ label, value }) {
  return (
    <div className="flex justify-between text-[13.5px] py-2 text-muted">
      <span>{label}</span><b className="text-secondary font-semibold">{value}</b>
    </div>
  );
}
