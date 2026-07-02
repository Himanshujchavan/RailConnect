import { Link, useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { useQuery } from "@tanstack/react-query";
import { useReactTable, getCoreRowModel, flexRender, createColumnHelper } from "@tanstack/react-table";
import { BarChart, Bar, ResponsiveContainer, XAxis, Tooltip } from "recharts";
import {
  LayoutDashboard, Search, Ticket, Wallet, ScanSearch, Bell, User, LogOut,
} from "lucide-react";
import { useApp } from "../context/AppContext";
import { fetchRecentBookings } from "../data/trains";
import { Card } from "../components/ui/Card";
import Button from "../components/ui/Button";
import { stagger, fadeUp } from "../components/ui/PageTransition";

const chartData = [
  { m: "Feb", trips: 4 }, { m: "Mar", trips: 6 }, { m: "Apr", trips: 5 },
  { m: "May", trips: 8 }, { m: "Jun", trips: 5 }, { m: "Jul", trips: 9 },
];

const columnHelper = createColumnHelper();
const columns = [
  columnHelper.accessor("pnr", { header: "PNR", cell: (info) => <span className="font-mono">{info.getValue()}</span> }),
  columnHelper.accessor("train", { header: "Train" }),
  columnHelper.accessor("route", { header: "Route" }),
  columnHelper.accessor("status", {
    header: "Status",
    cell: (info) => (
      <span className={`px-2.5 py-1 rounded-full text-xs font-bold ${
        info.getValue() === "Confirmed" ? "bg-emerald-50 text-emerald-600" : "bg-amber-50 text-amber-700"
      }`}>{info.getValue()}</span>
    ),
  }),
];

export default function Dashboard() {
  const navigate = useNavigate();
  const { search, user, pnr } = useApp();
  const { data: bookings = [] } = useQuery({ queryKey: ["recent-bookings"], queryFn: fetchRecentBookings });

  const table = useReactTable({ data: bookings, columns, getCoreRowModel: getCoreRowModel() });

  return (
    <div>
      <header className="sticky top-0 z-50 bg-white/85 backdrop-blur-md border-b border-border">
        <div className="max-w-[1180px] mx-auto px-6 h-[68px] flex items-center justify-between">
          <Link to="/" className="flex items-center gap-2.5 font-display font-bold text-[19px]">
            <span className="w-[34px] h-[34px] rounded-[9px] bg-gradient-to-br from-primary to-blue-900 text-white flex items-center justify-center">🚆</span>
            RailConnect
          </Link>
          <div className="flex items-center gap-3">
            <span className="text-muted text-[13.5px]">Hi, {user.name.split(" ")[0]} 👋</span>
            <Link to="/"><Button variant="ghost" size="sm"><LogOut size={15} /> Sign out</Button></Link>
          </div>
        </div>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-[220px_1fr] min-h-[calc(100vh-68px)]">
        <aside className="hidden md:block bg-white border-r border-border p-3.5">
          <SideLink icon={LayoutDashboard} label="Dashboard" active />
          <SideLink icon={Search} label="Search train" onClick={() => navigate("/search")} />
          <SideLink icon={Ticket} label="Bookings" onClick={() => navigate("/booking/history")} />
          <SideLink icon={Wallet} label="Wallet" onClick={() => navigate("/wallet")} />
          <SideLink icon={ScanSearch} label="PNR status" onClick={() => navigate("/pnr")} />
          <SideLink icon={Bell} label="Notifications" onClick={() => navigate("/notification")} />
          <SideLink icon={User} label="Profile" onClick={() => navigate("/profile")} />
          <SideLink icon={LayoutDashboard} label="Journey" onClick={() => navigate("/journey")} />
          <SideLink icon={User} label="Settings" onClick={() => navigate("/settings")} />
        </aside>

        <main className="p-7">
          <motion.div initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} className="flex justify-between items-center mb-6">
            <h2 className="text-[22px] font-bold">Dashboard</h2>
            <Button size="sm" onClick={() => navigate("/search")}>+ Book new ticket</Button>
          </motion.div>

          <motion.div variants={stagger} initial="hidden" animate="show" className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
            <Stat variants={fadeUp} label="Upcoming journey" value={`${search.from}→${search.to}`} delta="in 4 days" small />
            <Stat variants={fadeUp} label="Wallet balance" value="₹320" delta="+₹40 cashback" />
            <Stat variants={fadeUp} label="Total trips" value="12" delta="↑ 3 this year" />
            <Stat variants={fadeUp} label="Active coupons" value="2" delta="Save up to ₹300" />
          </motion.div>

          <div className="grid grid-cols-1 md:grid-cols-[1.6fr_1fr] gap-5">
            <Card className="p-6">
              <h4 className="font-semibold mb-0.5">Monthly trips</h4>
              <p className="text-muted text-xs mb-2">Last 6 months</p>
              <ResponsiveContainer width="100%" height={180}>
                <BarChart data={chartData}>
                  <XAxis dataKey="m" tick={{ fontSize: 12, fill: "#64748B" }} axisLine={false} tickLine={false} />
                  <Tooltip cursor={{ fill: "#F1F5F9" }} />
                  <Bar dataKey="trips" fill="#2563EB" radius={[6, 6, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </Card>

            <Card className="p-6">
              <h4 className="font-semibold mb-3">Recent bookings</h4>
              <table className="w-full text-[13px]">
                <tbody>
                  {table.getRowModel().rows.map((row) => (
                    <tr key={row.id} className="border-b border-border last:border-0">
                      {row.getVisibleCells().map((cell) => (
                        <td key={cell.id} className="py-2.5">{flexRender(cell.column.columnDef.cell, cell.getContext())}</td>
                      ))}
                    </tr>
                  ))}
                </tbody>
              </table>
              <Link to="/pnr" className="text-primary text-[13px] font-semibold mt-2.5 inline-block">View all bookings →</Link>
            </Card>
          </div>

          <Card className="p-6 mt-5">
            <h4 className="font-semibold mb-3">Quick actions</h4>
            <div className="grid grid-cols-2 gap-2.5">
              <QuickAction label="🎫 Book ticket" onClick={() => navigate("/search")} />
              <QuickAction label="🔎 Check PNR" onClick={() => navigate("/pnr")} />
              <QuickAction label="👛 Recharge wallet" onClick={() => navigate("/wallet")} />
              <QuickAction label="📜 Booking history" onClick={() => navigate("/booking/history")} />
            </div>
          </Card>
        </main>
      </div>
    </div>
  );
}

function SideLink({ icon: Icon, label, active, onClick }) {
  return (
    <button onClick={onClick} className={`w-full flex items-center gap-2.5 px-3 py-2.5 rounded-lg text-[14px] font-medium mb-1 transition ${
      active ? "bg-indigo-50 text-primary font-semibold" : "text-muted hover:bg-slate-100 hover:text-secondary"
    }`}>
      <Icon size={16} /> {label}
    </button>
  );
}

function Stat({ label, value, delta, small, variants }) {
  return (
    <motion.div variants={variants} className="bg-white border border-border rounded-2xl p-4.5">
      <div className="text-[12.5px] text-muted mb-2">{label}</div>
      <div className={`font-bold font-mono ${small ? "text-[15px]" : "text-2xl"}`}>{value}</div>
      <div className="text-xs text-accent mt-1.5 font-semibold">{delta}</div>
    </motion.div>
  );
}

function QuickAction({ label, onClick }) {
  return (
    <button onClick={onClick} className="bg-bg border border-border rounded-xl p-4 text-left font-semibold text-[13.5px] hover:border-primary hover:text-primary transition">
      {label}
    </button>
  );
}
