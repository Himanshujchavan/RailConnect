import { useRef } from "react";
import { Link } from "react-router-dom";
import { motion } from "framer-motion";
import { toast } from "sonner";
import { QRCodeSVG } from "qrcode.react";
import { Check, Download } from "lucide-react";
import jsPDF from "jspdf";
import html2canvas from "html2canvas";
import { useApp } from "../context/AppContext";
import { TRAINS } from "../data/trains";
import Button from "../components/ui/Button";
import { formatMoney } from "../lib/utils";

export default function BookingSuccess() {
  const { search, selectedTrain, selectedClass, selectedSeats, user, pnr } = useApp();
  const t = selectedTrain || TRAINS[0];
  const seatN = selectedSeats.length || search.pax;
  const ticketRef = useRef(null);

  const downloadPdf = async () => {
    if (!ticketRef.current) return;
    const canvas = await html2canvas(ticketRef.current, { scale: 2, backgroundColor: "#ffffff" });
    const img = canvas.toDataURL("image/png");
    const pdf = new jsPDF({ orientation: "portrait", unit: "px", format: [canvas.width / 2, canvas.height / 2] });
    pdf.addImage(img, "PNG", 0, 0, canvas.width / 2, canvas.height / 2);
    pdf.save(`RailConnect-${pnr}.pdf`);
    toast.success("Ticket PDF downloaded");
  };

  return (
    <div className="max-w-[640px] mx-auto px-5 py-16 text-center">
      <motion.div
        initial={{ scale: 0 }}
        animate={{ scale: 1 }}
        transition={{ type: "spring", stiffness: 260, damping: 16 }}
        className="w-[82px] h-[82px] rounded-full bg-emerald-50 text-accent flex items-center justify-center mx-auto mb-5"
      >
        <motion.div initial={{ pathLength: 0 }} animate={{ pathLength: 1 }} transition={{ duration: 0.5, delay: 0.2 }}>
          <Check size={40} />
        </motion.div>
      </motion.div>

      <motion.h1 initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.3 }} className="text-[26px] font-bold">
        Booking confirmed!
      </motion.h1>
      <p className="text-muted mt-2">A confirmation and e-ticket have been sent to {user.email}</p>

      <motion.div
        ref={ticketRef}
        initial={{ opacity: 0, y: 24 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.4 }}
        className="bg-white border border-border rounded-2xl mt-7 text-left overflow-hidden shadow-lift"
      >
        <div className="bg-gradient-to-br from-primary to-blue-900 text-white px-6 py-5 flex justify-between items-center">
          <div>
            <div className="text-xs opacity-80">TRAIN</div>
            <b className="text-[16px]">{t.name}</b>
          </div>
          <div className="text-right">
            <div className="text-xs opacity-80">PNR</div>
            <b className="font-mono text-[16px] tracking-wider">{pnr}</b>
          </div>
        </div>

        <div className="p-6 grid grid-cols-2 gap-4">
          <TF label="From" value={`${search.from} (${t.depSt})`} />
          <TF label="To" value={`${search.to} (${t.arrSt})`} />
          <TF label="Date" value={search.date} />
          <TF label="Departure" value={t.dep} />
          <TF label="Class" value={selectedClass || t.classes[0].c} />
          <TF label="Passengers" value={seatN} />
        </div>

        <div className="border-t-2 border-dashed border-border px-6 py-4.5 flex justify-between items-center">
          <span className="px-3 py-1.5 rounded-full text-xs font-bold bg-emerald-50 text-emerald-600">Confirmed</span>
          <QRCodeSVG value={`RAILCONNECT-PNR-${pnr}`} size={64} />
        </div>
      </motion.div>

      <div className="flex gap-3 justify-center mt-7 flex-wrap">
        <Link to="/"><Button variant="outline">Back to home</Button></Link>
        <Link to="/journey"><Button variant="outline">Journey</Button></Link>
        <Link to="/booking/history"><Button variant="outline">Booking history</Button></Link>
        <Button onClick={downloadPdf}><Download size={16} /> Download ticket</Button>
      </div>
    </div>
  );
}

function TF({ label, value }) {
  return (
    <div>
      <label className="block text-[11px] text-muted uppercase tracking-wide mb-1">{label}</label>
      <b className="text-[14.5px]">{value}</b>
    </div>
  );
}
