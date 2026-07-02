import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { toast } from "sonner";
import { Smartphone, CreditCard, Wallet, Landmark, Lock } from "lucide-react";
import { useApp } from "../context/AppContext";
import { TRAINS } from "../data/trains";
import PageHeader from "../components/layout/PageHeader";
import { Card } from "../components/ui/Card";
import { Input } from "../components/ui/Input";
import Button from "../components/ui/Button";
import { formatMoney, generatePNR } from "../lib/utils";

const METHODS = [
  { id: "upi", label: "UPI", icon: Smartphone },
  { id: "card", label: "Card", icon: CreditCard },
  { id: "wallet", label: "Wallet", icon: Wallet },
  { id: "netbanking", label: "Net Banking", icon: Landmark },
];

export default function Payment() {
  const navigate = useNavigate();
  const { search, selectedTrain, selectedClass, selectedSeats, fare, setFare, setPnr } = useApp();
  const [method, setMethod] = useState("upi");
  const [coupon, setCoupon] = useState("");
  const [processing, setProcessing] = useState(false);

  const t = selectedTrain || TRAINS[0];
  const seatN = selectedSeats.length || search.pax;
  const base = fare.base * seatN;
  const total = base + fare.tax - fare.coupon;

  const applyCoupon = () => {
    const code = coupon.trim().toUpperCase();
    if (code === "RAIL100") {
      setFare({ ...fare, coupon: 100 });
      toast.success("Coupon applied — ₹100 off!");
    } else {
      setFare({ ...fare, coupon: 0 });
      toast.error(code ? "Invalid coupon code" : "Try code RAIL100");
    }
  };

  const confirmPayment = async () => {
    setProcessing(true);
    await new Promise((r) => setTimeout(r, 900));
    setPnr(generatePNR());
    toast.success("Payment successful!");
    setProcessing(false);
    navigate("/booking/success");
  };

  return (
    <div>
      <PageHeader
        crumbs={[{ label: "Home", to: "/" }, { label: "Payment" }]}
        title="Complete payment"
        steps={["Select train", "Passengers", "Review", "Payment"]}
        activeStep={3}
      />
      <div className="max-w-[760px] mx-auto px-6 py-7 pb-16">
        <Card className="p-6">
          <h4 className="font-semibold mb-4">Choose payment method</h4>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-2.5 mb-5">
            {METHODS.map((m) => (
              <motion.button
                key={m.id}
                onClick={() => setMethod(m.id)}
                animate={method === m.id ? { rotateY: [0, 12, 0] } : {}}
                transition={{ duration: 0.35 }}
                className={`border-2 rounded-xl p-4 text-center transition ${
                  method === m.id ? "border-primary bg-indigo-50" : "border-border bg-white"
                }`}
              >
                <m.icon size={22} className="mx-auto mb-2" />
                <span className="text-[13px] font-semibold">{m.label}</span>
              </motion.button>
            ))}
          </div>

          {method === "upi" && <Input placeholder="yourname@upi" className="mb-4" />}
          {method === "card" && (
            <div className="grid grid-cols-2 gap-3 mb-4">
              <Input placeholder="Card number" className="col-span-2" />
              <Input placeholder="MM/YY" />
              <Input placeholder="CVV" />
            </div>
          )}
          {method === "wallet" && (
            <label className="flex items-center gap-2 text-muted text-[13.5px] mb-4">
              <input type="checkbox" /> Use wallet balance ({formatMoney(320)} available)
            </label>
          )}
          {method === "netbanking" && (
            <select className="w-full px-3.5 py-2.5 rounded-lg border border-border text-[14.5px] mb-4">
              <option>Select your bank</option><option>HDFC Bank</option><option>ICICI Bank</option><option>SBI</option>
            </select>
          )}

          <div className="flex gap-2.5 my-4">
            <Input placeholder="Enter coupon code" value={coupon} onChange={(e) => setCoupon(e.target.value)} />
            <Button variant="outline" onClick={applyCoupon}>Apply</Button>
          </div>
        </Card>

        <Card className="p-6 mt-4">
          <Row label="Base fare" value={formatMoney(base)} />
          <Row label="Taxes & fees" value={formatMoney(fare.tax)} />
          <Row label="Coupon discount" value={`−${formatMoney(fare.coupon)}`} accent />
          <div className="flex justify-between pt-3.5 mt-1.5 border-t border-border font-bold text-[16px]">
            <span>Amount payable</span><b className="font-mono text-primary">{formatMoney(total)}</b>
          </div>
        </Card>

        <Button variant="accent" size="lg" full className="mt-6" onClick={confirmPayment} disabled={processing}>
          <Lock size={16} /> {processing ? "Processing…" : `Pay ${formatMoney(total)} securely`}
        </Button>
      </div>
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
