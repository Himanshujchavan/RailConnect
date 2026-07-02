import { useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { motion } from "framer-motion";
import { toast } from "sonner";
import { Star } from "lucide-react";
import { useApp } from "../context/AppContext";
import { TRAINS } from "../data/trains";
import PageHeader from "../components/layout/PageHeader";
import { Card } from "../components/ui/Card";
import Button from "../components/ui/Button";
import { formatMoney } from "../lib/utils";

const COACHES = ["A1", "A2", "B1", "B2", "S1", "S2"];
const BOOKED = [3, 8, 14, 19];
const LAYOUT = ["1", "2", "A", "3", "4", "5", "6", "A", "7", "8", "9", "10", "A", "11", "12", "13", "14", "A", "15", "16", "17", "18", "A", "19", "20"];
const PREFS = ["Window", "Lower", "Upper", "Side Lower", "Side Upper", "Family Together"];

export default function TrainDetails() {
  const navigate = useNavigate();
  const { id } = useParams();
  const { search, selectedTrain, selectedClass, selectedSeats, setSelectedSeats, fare } = useApp();
  const [coach, setCoach] = useState(COACHES[0]);
  const t = selectedTrain || TRAINS.find((train) => String(train.id) === String(id)) || TRAINS[0];
  const cls = selectedClass || t.classes[0].c;

  const seatCount = selectedSeats.length || 1;
  const baseFare = fare.base * seatCount;
  const total = baseFare + fare.tax;

  const toggleSeat = (n) => {
    if (selectedSeats.includes(n)) {
      setSelectedSeats(selectedSeats.filter((s) => s !== n));
    } else {
      if (selectedSeats.length >= search.pax) {
        toast.warning(`You can only select ${search.pax} seat(s)`);
        return;
      }
      setSelectedSeats([...selectedSeats, n]);
    }
  };

  const continueBooking = () => {
    if (selectedSeats.length === 0) {
      toast.warning("Select at least one seat to continue");
      return;
    }
    navigate("/booking/passenger");
  };

  return (
    <div>
      <PageHeader crumbs={[{ label: "Home", to: "/" }, { label: "Search", to: "/search/result" }, { label: t.name }]}
        title={t.name} subtitle={`#${t.num}`} />

      <div className="max-w-[1180px] mx-auto px-6 grid grid-cols-1 md:grid-cols-[1fr_320px] gap-6 py-7 pb-16">
        <Card className="p-6">
          <div className="flex gap-6 text-muted text-[13px] flex-wrap">
            <span>⏱ {t.dur}</span>
            <span>{t.depSt} {t.dep} → {t.arrSt} {t.arr}</span>
            <span className="flex items-center gap-1 bg-emerald-50 text-emerald-600 px-2.5 py-1 rounded-md font-bold"><Star size={12} fill="currentColor" /> {t.rating}</span>
          </div>

          <h4 className="mt-6 mb-2 font-semibold text-sm">Choose coach</h4>
          <div className="flex gap-2 flex-wrap">
            {COACHES.map((c) => (
              <button key={c} onClick={() => setCoach(c)}
                className={`px-3.5 py-2.5 rounded-lg border-2 font-bold text-[13px] font-mono transition ${
                  coach === c ? "border-primary bg-indigo-50 text-primary" : "border-border bg-white"
                }`}>
                {c}
              </button>
            ))}
          </div>

          <h4 className="mt-6 mb-1 font-semibold text-sm">Select seats — {cls}</h4>
          <p className="text-muted text-[13px] mb-2.5">Tap available seats to select (up to {search.pax})</p>
          <div className="grid grid-cols-6 gap-2.5 max-w-[320px]">
            {LAYOUT.map((l, i) => {
              if (l === "A") return <div key={i} className="invisible" />;
              const n = parseInt(l);
              const isBooked = BOOKED.includes(n);
              const isSel = selectedSeats.includes(n);
              return (
                <motion.button
                  key={i}
                  disabled={isBooked}
                  onClick={() => toggleSeat(n)}
                  whileTap={!isBooked ? { scale: 0.85 } : {}}
                  animate={isSel ? { scale: [1, 1.15, 1] } : { scale: 1 }}
                  transition={{ duration: 0.25 }}
                  className={`w-11 h-11 rounded-lg flex items-center justify-center text-xs font-bold font-mono border-2 transition ${
                    isBooked
                      ? "bg-slate-100 text-slate-300 border-slate-100 cursor-not-allowed"
                      : isSel
                      ? "bg-primary text-white border-primary"
                      : "bg-white border-border hover:border-primary"
                  }`}
                >
                  {n}
                </motion.button>
              );
            })}
          </div>
          <div className="flex gap-4.5 mt-5 flex-wrap text-[12.5px] text-muted">
            <Legend swatch="bg-white border-2 border-border" label="Available" />
            <Legend swatch="bg-primary" label="Selected" />
            <Legend swatch="bg-slate-100" label="Booked" />
          </div>

          <h4 className="mt-6 mb-2.5 font-semibold text-sm">Berth preference</h4>
          <div className="flex gap-2 flex-wrap">
            {PREFS.map((p) => <PrefPill key={p} label={p} />)}
          </div>
        </Card>

        <Card className="p-6 h-fit md:sticky md:top-[84px]">
          <h4 className="font-semibold mb-3.5">Fare summary</h4>
          <SummaryRow label="Train" value={t.name} />
          <SummaryRow label="Class" value={cls} />
          <SummaryRow label="Seats selected" value={`${selectedSeats.length} / ${search.pax}`} />
          <SummaryRow label="Base fare" value={formatMoney(baseFare)} />
          <SummaryRow label="Taxes" value={formatMoney(fare.tax)} />
          <div className="flex justify-between pt-3.5 mt-1.5 border-t border-border font-bold text-[16px]">
            <span>Total</span><b className="font-mono text-primary">{formatMoney(total)}</b>
          </div>
          <Button full size="lg" className="mt-4" onClick={continueBooking}>Continue →</Button>
        </Card>
      </div>
    </div>
  );
}

function Legend({ swatch, label }) {
  return (
    <span className="flex items-center gap-1.5">
      <i className={`w-4 h-4 rounded ${swatch} inline-block`} /> {label}
    </span>
  );
}

function PrefPill({ label }) {
  const [active, setActive] = useState(false);
  return (
    <button onClick={() => setActive(!active)}
      className={`px-3.5 py-2 rounded-lg border text-[13px] transition ${active ? "border-primary bg-indigo-50 text-primary" : "border-border"}`}>
      {label}
    </button>
  );
}

function SummaryRow({ label, value }) {
  return (
    <div className="flex justify-between text-[13.5px] py-2 text-muted">
      <span>{label}</span><b className="text-secondary font-semibold">{value}</b>
    </div>
  );
}
