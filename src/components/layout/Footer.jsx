import { Link } from "react-router-dom";
import { TrainFront, Twitter, Linkedin, Instagram } from "lucide-react";

export default function Footer() {
  return (
    <footer className="bg-secondary text-slate-300 pt-14 pb-7 mt-auto">
      <div className="max-w-[1180px] mx-auto px-6">
        <div className="grid grid-cols-1 md:grid-cols-[2fr_1fr_1fr_1fr] gap-8">
          <div>
            <div className="flex items-center gap-2.5 text-white font-display font-bold text-[17px] mb-3">
              <span className="w-8 h-8 rounded-lg bg-primary flex items-center justify-center">
                <TrainFront size={16} />
              </span>
              RailConnect
            </div>
            <p className="text-[13.5px] max-w-[280px] text-slate-400">
              Book train tickets across India in seconds — real-time seat maps, instant PNR status, and secure payments.
            </p>
            <div className="flex gap-2.5 mt-4">
              {[Twitter, Linkedin, Instagram].map((Icon, i) => (
                <a key={i} href="#" className="w-8 h-8 rounded-lg bg-slate-800 flex items-center justify-center hover:bg-slate-700">
                  <Icon size={15} />
                </a>
              ))}
            </div>
          </div>
          <div>
            <h5 className="text-white text-sm font-semibold mb-3.5">Company</h5>
            <ul className="space-y-2 text-[13.5px]">
              <li><Link to="/about" className="hover:text-white">About us</Link></li>
              <li><Link to="/contact" className="hover:text-white">Contact</Link></li>
              <li><a href="#" className="hover:text-white">Careers</a></li>
            </ul>
          </div>
          <div>
            <h5 className="text-white text-sm font-semibold mb-3.5">Support</h5>
            <ul className="space-y-2 text-[13.5px]">
              <li><Link to="/pnr" className="hover:text-white">PNR status</Link></li>
              <li><Link to="/booking/history" className="hover:text-white">Booking history</Link></li>
              <li><Link to="/help" className="hover:text-white">Help center</Link></li>
            </ul>
          </div>
          <div>
            <h5 className="text-white text-sm font-semibold mb-3.5">Legal</h5>
            <ul className="space-y-2 text-[13.5px]">
              <li><a href="#" className="hover:text-white">Terms of use</a></li>
              <li><a href="#" className="hover:text-white">Privacy policy</a></li>
            </ul>
          </div>
        </div>
        <div className="border-t border-slate-800 mt-9 pt-5 flex flex-wrap justify-between gap-2 text-[12.5px] text-slate-500">
          <span>© 2026 RailConnect. All rights reserved.</span>
          <span>Made for a smoother journey.</span>
        </div>
      </div>
    </footer>
  );
}
