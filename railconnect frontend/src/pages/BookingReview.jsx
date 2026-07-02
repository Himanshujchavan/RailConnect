import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { useApp } from "../context/AppContext";
import { TRAINS } from "../data/trains";
import PageHeader from "../components/layout/PageHeader";
import { Card } from "../components/ui/Card";
import Button from "../components/ui/Button";
import { formatMoney } from "../lib/utils";

export default function BookingReview() {
  const navigate = useNavigate();
  const { search, selectedTrain, selectedClass, selectedSeats, fare } = useApp();
  const t = selectedTrain || TRAINS[0];
  const seatN = selectedSeats.length || search.pax;
  const base = fare.base * seatN;
  const total = base + fare.tax - fare.coupon;

  return (
    <div>
      <PageHeader
        crumbs={[{ label: "Home", to: "/" }, { label: "Booking review" }]}
        title="Review your booking"
        steps={["Select train", "Passengers", "Review", "Payment"]}
        activeStep={2}
      />
      <motion.div initial={{ opacity: 0, y: 14 }} animate={{ opacity: 1, y: 0 }}
        className="max-w-[760px] mx-auto px-6 py-7 pb-16">
        <Card className="p-6">
          <h4 className="font-semibold mb-3.5">Journey</h4>
          <Row label="Train" value={`${t.name} (#${t.num})`} />
          <Row label="Route" value={`${search.from} → ${search.to}`} />
          <Row label="Date" value={search.date} />
          <Row label="Class · Seats" value={`${selectedClass || t.classes[0].c} · ${seatN} passenger(s)`} />
        </Card>

        <Card className="p-6 mt-4">
          <h4 className="font-semibold mb-3.5">Fare breakdown</h4>
          <Row label="Base fare" value={formatMoney(base)} />
          <Row label="Taxes & fees" value={formatMoney(fare.tax)} />
          <Row label="Coupon discount" value={`−${formatMoney(fare.coupon)}`} accent />
          <Row label="Wallet applied" value={formatMoney(0)} />
          <div className="flex justify-between pt-3.5 mt-1.5 border-t border-border font-bold text-[16px]">
            <span>Total payable</span><b className="font-mono text-primary">{formatMoney(total)}</b>
          </div>
        </Card>

        <Button size="lg" full className="mt-6" onClick={() => navigate("/booking/payment")}>Proceed to payment →</Button>
      </motion.div>
    </div>
  );
}

function Row({ label, value, accent }) {
  return (
    <div className="flex justify-between text-[13.5px] py-2 text-muted">
      <span>{label}</span><b className={accent ? "text-accent" : "text-secondary font-semibold"}>{value}</b>
    </div>
  );
}
