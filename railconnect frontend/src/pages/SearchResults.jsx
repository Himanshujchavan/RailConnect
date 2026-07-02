import { useNavigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { motion } from "framer-motion";
import Skeleton from "react-loading-skeleton";
import { Star } from "lucide-react";
import { useApp } from "../context/AppContext";
import { fetchTrainsForSearch } from "../data/trains";
import PageHeader from "../components/layout/PageHeader";
import { Card } from "../components/ui/Card";
import Button from "../components/ui/Button";
import { formatMoney } from "../lib/utils";
import { fadeUp, stagger } from "../components/ui/PageTransition";

export default function SearchResults() {
  const { search, setSelectedTrain, setSelectedClass, setSelectedSeats } = useApp();
  const navigate = useNavigate();

  const { data: trains, isLoading } = useQuery({
    queryKey: ["search-trains", search],
    queryFn: () => fetchTrainsForSearch(search),
  });

  const pickClass = (train, cls) => {
    setSelectedTrain(train);
    setSelectedClass(cls);
    setSelectedSeats([]);
    navigate(`/train/${train.id}`);
  };

  return (
    <div>
      <PageHeader
        crumbs={[{ label: "Home", to: "/" }, { label: "Search results" }]}
        title={`${search.from} → ${search.to}`}
        subtitle={`· ${search.date} · ${search.pax} passenger(s)`}
      />

      <div className="max-w-[1180px] mx-auto px-6 grid grid-cols-1 md:grid-cols-[260px_1fr] gap-6 py-7 pb-16">
        <aside className="bg-white border border-border rounded-card p-5 h-fit md:sticky md:top-[84px]">
          <FilterGroup title="Departure" opts={["Before 6 AM", "6 AM – 12 PM", "12 PM – 6 PM", "After 6 PM"]} checked={[false, true, true, false]} />
          <FilterGroup title="Class" opts={["Sleeper (SL)", "AC 3 Tier", "AC 2 Tier", "AC 1st Class"]} checked={[true, true, true, false]} />
          <FilterGroup title="Train type" opts={["Rajdhani", "Duronto", "Shatabdi"]} checked={[true, true, false]} />
          <Button variant="outline" size="sm" full>Reset filters</Button>
        </aside>

        <div>
          <div className="flex justify-between items-center mb-4">
            <span className="text-muted text-[13.5px]">{trains?.length ?? 0} trains found</span>
            <select className="px-3 py-2 rounded-lg border border-border text-[13.5px]">
              <option>Sort: Departure (earliest)</option>
              <option>Sort: Cheapest</option>
              <option>Sort: Fastest</option>
            </select>
          </div>

          {isLoading ? (
            <div className="flex flex-col gap-3.5">
              {Array.from({ length: 4 }).map((_, i) => <Skeleton key={i} height={150} borderRadius={14} />)}
            </div>
          ) : (
            <motion.div variants={stagger} initial="hidden" animate="show" className="flex flex-col gap-3.5">
              {trains.map((t) => (
                <motion.div key={t.id} variants={fadeUp}>
                  <Card className="p-5 hover:border-primary hover:shadow-lift transition">
                    <div className="flex justify-between">
                      <div>
                        <div className="font-bold text-[16px]">{t.name}</div>
                        <div className="text-muted text-[12.5px] font-mono">#{t.num} · Runs daily</div>
                      </div>
                      <div className="flex items-center gap-1 bg-emerald-50 text-emerald-600 px-2.5 py-1 rounded-md text-[12.5px] font-bold">
                        <Star size={12} fill="currentColor" /> {t.rating}
                      </div>
                    </div>

                    <div className="flex items-center gap-4 mt-3">
                      <div className="text-center">
                        <div className="text-xl font-bold font-mono">{t.dep}</div>
                        <div className="text-xs text-muted">{t.depSt}</div>
                      </div>
                      <div className="flex-1 text-center">
                        <div className="text-xs text-muted mb-1">{t.dur}</div>
                        <div className="h-[2px] bg-border relative">
                          <span className="absolute right-0 -top-[3px] text-primary">●</span>
                        </div>
                      </div>
                      <div className="text-center">
                        <div className="text-xl font-bold font-mono">{t.arr}</div>
                        <div className="text-xs text-muted">{t.arrSt}</div>
                      </div>
                    </div>

                    <div className="flex gap-2 mt-3.5 flex-wrap">
                      {t.classes.map((c) => (
                        <button
                          key={c.c}
                          disabled={c.seats === 0}
                          onClick={() => pickClass(t, c.c)}
                          className={`px-3 py-2 rounded-lg border text-center min-w-[76px] text-[12.5px] transition ${
                            c.seats === 0 ? "opacity-50 cursor-not-allowed border-border" : "border-border hover:border-primary"
                          }`}
                        >
                          {c.c}
                          <b className="block text-[14px] font-mono text-secondary">{formatMoney(c.price)}</b>
                          <span className={c.seats === 0 ? "text-danger" : "text-muted"}>{c.seats === 0 ? "Full" : `${c.seats} left`}</span>
                        </button>
                      ))}
                    </div>
                  </Card>
                </motion.div>
              ))}
            </motion.div>
          )}
        </div>
      </div>
    </div>
  );
}

function FilterGroup({ title, opts, checked }) {
  return (
    <div className="mb-5.5">
      <h4 className="text-sm font-semibold mb-3">{title}</h4>
      {opts.map((o, i) => (
        <label key={o} className="flex items-center gap-2 text-[13.5px] text-muted mb-2">
          <input type="checkbox" defaultChecked={checked[i]} /> {o}
        </label>
      ))}
    </div>
  );
}
