import { useCallback, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { motion, AnimatePresence } from "framer-motion";
import { useQuery } from "@tanstack/react-query";
import useEmblaCarousel from "embla-carousel-react";
import Skeleton from "react-loading-skeleton";
import "react-loading-skeleton/dist/skeleton.css";
import {
  ArrowLeftRight, Search, Ticket, ShieldCheck, MapPinned, Headphones,
  Star, ChevronDown, ArrowRight, ChevronLeft, ChevronRight,
} from "lucide-react";

import { useApp } from "../context/AppContext";
import { fetchPopularTrains, POPULAR_ROUTES, TESTIMONIALS } from "../data/trains";
import Button from "../components/ui/Button";
import { Card } from "../components/ui/Card";
import { Input, Field } from "../components/ui/Input";
import { fadeUp, stagger } from "../components/ui/PageTransition";
import { formatMoney } from "../lib/utils";

const searchSchema = z.object({
  from: z.string().min(2, "Enter an origin station"),
  to: z.string().min(2, "Enter a destination station"),
  date: z.string().min(1, "Pick a journey date"),
  pax: z.coerce.number().min(1).max(6),
  cls: z.string(),
});

const FEATURES = [
  { icon: Ticket, title: "Easy booking", text: "Search, select seats and pay in under a minute." },
  { icon: ShieldCheck, title: "Secure payment", text: "Bank-grade encryption on every transaction." },
  { icon: MapPinned, title: "Live status", text: "Real-time train position and delay tracking." },
  { icon: Headphones, title: "24×7 support", text: "Round-the-clock help whenever you need it." },
];

const FAQS = [
  { q: "Can I cancel my ticket?", a: "Yes, cancellations are allowed up to 4 hours before departure with a refund credited to your wallet within 24 hours." },
  { q: "How do I check PNR status?", a: "Use the PNR Status page from the navbar and enter your 10-digit PNR number." },
  { q: "Is Tatkal booking supported?", a: "Yes, Tatkal quota opens at 10:00 AM (AC classes) and 11:00 AM (non-AC) one day before travel." },
];

export default function Home() {
  const navigate = useNavigate();
  const { search, setSearch } = useApp();
  const [openFaq, setOpenFaq] = useState(null);

  const { data: popularTrains, isLoading } = useQuery({
    queryKey: ["popular-trains"],
    queryFn: fetchPopularTrains,
  });

  const { register, handleSubmit, setValue, watch, formState: { errors } } = useForm({
    resolver: zodResolver(searchSchema),
    defaultValues: search,
  });

  const onSubmit = (values) => {
    setSearch(values);
    navigate("/search/result");
  };

  const swap = () => {
    const { from, to } = watch();
    setValue("from", to);
    setValue("to", from);
  };

  const quickRoute = (from, to) => {
    setValue("from", from);
    setValue("to", to);
    handleSubmit(onSubmit)();
  };

  const [emblaRef, emblaApi] = useEmblaCarousel({ loop: true });
  const scrollPrev = useCallback(() => emblaApi && emblaApi.scrollPrev(), [emblaApi]);
  const scrollNext = useCallback(() => emblaApi && emblaApi.scrollNext(), [emblaApi]);

  return (
    <>
      {/* HERO */}
      <section className="relative overflow-hidden pt-16 pb-28 bg-[radial-gradient(1100px_480px_at_15%_-10%,#DBEAFE_0%,transparent_60%),linear-gradient(180deg,#F8FAFC_0%,#F1F5F9_100%)]">
        <div className="max-w-[1180px] mx-auto px-6">
          <motion.div initial={{ opacity: 0, y: 18 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.5 }}
            className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-indigo-50 text-primary text-[13px] font-semibold mb-4">
            🟢 4,800+ trains live right now
          </motion.div>

          <motion.h1 initial={{ opacity: 0, y: 24 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6, delay: 0.08 }}
            className="text-[42px] md:text-[46px] font-extrabold leading-[1.12] max-w-[640px] tracking-tight">
            Book train tickets<br />the <span className="text-primary">simple</span> way.
          </motion.h1>

          <motion.p initial={{ opacity: 0, y: 24 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6, delay: 0.16 }}
            className="text-muted text-[17px] max-w-[520px] mt-3.5">
            Search, compare seats and pay in under a minute — RailConnect gives you real-time availability across every route in India.
          </motion.p>

          <motion.form
            initial={{ opacity: 0, scale: 0.97, y: 20 }} animate={{ opacity: 1, scale: 1, y: 0 }} transition={{ duration: 0.5, delay: 0.24 }}
            onSubmit={handleSubmit(onSubmit)}
            className="mt-9 bg-white rounded-2xl shadow-lift border border-border p-5 md:p-6"
          >
            <div className="grid grid-cols-1 md:grid-cols-[1.3fr_auto_1.3fr_1fr_1fr] gap-3.5 items-end">
              <Field label="From" error={errors.from?.message}>
                <Input placeholder="Origin station" {...register("from")} />
              </Field>
              <button type="button" onClick={swap} title="Swap stations"
                className="hidden md:flex w-[38px] h-[38px] rounded-full bg-bg border border-border items-center justify-center text-primary mb-2 self-center">
                <ArrowLeftRight size={16} />
              </button>
              <Field label="To" error={errors.to?.message}>
                <Input placeholder="Destination station" {...register("to")} />
              </Field>
              <Field label="Journey date" error={errors.date?.message}>
                <Input type="date" {...register("date")} />
              </Field>
              <Field label="Class">
                <select {...register("cls")} className="w-full px-3.5 py-2.5 rounded-lg border border-border text-[14.5px] bg-white">
                  <option>SL</option><option>3A</option><option>2A</option><option>1A</option>
                </select>
              </Field>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-[1fr_1fr_auto] gap-3.5 mt-3.5">
              <Field label="Passengers">
                <Input type="number" min={1} max={6} {...register("pax")} />
              </Field>
              <Field label="Quota">
                <select className="w-full px-3.5 py-2.5 rounded-lg border border-border text-[14.5px] bg-white">
                  <option>General</option><option>Tatkal</option><option>Ladies</option><option>Senior Citizen</option>
                </select>
              </Field>
              <Button type="submit" size="lg" className="self-end">
                <Search size={17} /> Search Trains
              </Button>
            </div>
          </motion.form>

          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.4 }} className="flex gap-2.5 mt-6 flex-wrap items-center">
            <span className="text-muted text-[13.5px]">Popular:</span>
            {POPULAR_ROUTES.map((r) => (
              <button key={r.from + r.to} onClick={() => quickRoute(r.from, r.to)}
                className="px-3.5 py-2 rounded-full bg-white border border-border text-[13.5px] font-medium text-muted hover:border-primary hover:text-primary transition">
                {r.from} → {r.to}
              </button>
            ))}
          </motion.div>
        </div>
      </section>

      {/* POPULAR ROUTES */}
      <section className="py-16" id="routes">
        <div className="max-w-[1180px] mx-auto px-6">
          <SectionHead eyebrow="Trending" title="Popular routes" />
          <motion.div variants={stagger} initial="hidden" whileInView="show" viewport={{ once: true, amount: 0.2 }}
            className="grid grid-cols-2 md:grid-cols-4 gap-4.5">
            {POPULAR_ROUTES.map((r) => (
              <motion.div key={r.from + r.to} variants={fadeUp} whileHover={{ y: -4 }}>
                <Card className="p-5">
                  <div className="flex items-center gap-2 font-bold text-[16.5px]">
                    {r.from} <ArrowRight size={16} className="text-primary" /> {r.to}
                  </div>
                  <div className="text-muted text-[13.5px] mt-2.5">
                    Starting from <b className="text-accent font-mono text-[15px]">{formatMoney(r.price)}</b>
                  </div>
                </Card>
              </motion.div>
            ))}
          </motion.div>
        </div>
      </section>

      {/* POPULAR TRAINS */}
      <section className="py-16 bg-white" id="popular-trains">
        <div className="max-w-[1180px] mx-auto px-6">
          <div className="flex items-end justify-between flex-wrap gap-2.5 mb-8">
            <SectionHead eyebrow="Top rated" title="Popular trains" noMargin />
            <Button variant="outline" size="sm" onClick={() => navigate("/search/result")}>View all →</Button>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
            {isLoading
              ? Array.from({ length: 3 }).map((_, i) => <Skeleton key={i} height={190} borderRadius={14} />)
              : popularTrains?.map((t, i) => (
                  <motion.div key={t.id} initial={{ opacity: 0, y: 20 }} whileInView={{ opacity: 1, y: 0 }}
                    viewport={{ once: true }} transition={{ delay: i * 0.08 }} whileHover={{ y: -3 }}>
                    <Card className="p-5 flex flex-col gap-3.5">
                      <div className="flex justify-between items-start">
                        <div>
                          <div className="font-bold text-[16px]">{t.name}</div>
                          <div className="text-muted text-[12.5px] font-mono">#{t.num}</div>
                        </div>
                        <div className="flex items-center gap-1 bg-emerald-50 text-emerald-600 px-2.5 py-1 rounded-md text-[12.5px] font-bold">
                          <Star size={12} fill="currentColor" /> {t.rating}
                        </div>
                      </div>
                      <div className="flex gap-4.5 text-muted text-[13px]">
                        <span>⏱ {t.dur}</span><span>{t.depSt} → {t.arrSt}</span>
                      </div>
                      <div className="flex justify-between items-center pt-3 border-t border-dashed border-border">
                        <span className="text-muted text-[12.5px]">Avg fare</span>
                        <b className="text-[19px] font-mono">{formatMoney(t.classes[0].price)}</b>
                        <Button size="sm" onClick={() => navigate(`/train/${t.id}`)}>Book now</Button>
                      </div>
                    </Card>
                  </motion.div>
                ))}
          </div>
        </div>
      </section>

      {/* FEATURES */}
      <section className="py-16" id="features">
        <div className="max-w-[1180px] mx-auto px-6">
          <SectionHead eyebrow="Why RailConnect" title="Built for the modern traveller" />
          <motion.div variants={stagger} initial="hidden" whileInView="show" viewport={{ once: true, amount: 0.2 }}
            className="grid grid-cols-2 md:grid-cols-4 gap-4.5">
            {FEATURES.map((f) => (
              <motion.div key={f.title} variants={fadeUp}>
                <Card className="p-6">
                  <div className="w-11 h-11 rounded-[10px] bg-indigo-50 text-primary flex items-center justify-center mb-3.5">
                    <f.icon size={20} />
                  </div>
                  <h4 className="text-[15.5px] font-semibold mb-1.5">{f.title}</h4>
                  <p className="text-muted text-[13.5px]">{f.text}</p>
                </Card>
              </motion.div>
            ))}
          </motion.div>
        </div>
      </section>

      {/* TIMELINE */}
      <section className="py-16 bg-white">
        <div className="max-w-[1180px] mx-auto px-6">
          <SectionHead eyebrow="How it works" title="Your journey, four steps" center />
          <div className="flex justify-between relative mt-4">
            <div className="absolute top-[22px] left-[5%] right-[5%] h-[2px] bg-border" />
            {["Search", "Reserve", "Pay", "Travel"].map((s, i) => (
              <motion.div key={s} initial={{ opacity: 0, scale: 0.8 }} whileInView={{ opacity: 1, scale: 1 }}
                viewport={{ once: true }} transition={{ delay: i * 0.12 }} className="relative text-center flex-1">
                <div className={`w-11 h-11 rounded-full mx-auto mb-3 flex items-center justify-center font-bold relative z-10 ${
                  i < 2 ? "bg-primary text-white" : "bg-white border-2 border-primary text-primary"
                }`}>{i + 1}</div>
                <span className="text-[13.5px] font-semibold">{s}</span>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* TESTIMONIALS — embla carousel */}
      <section className="py-16">
        <div className="max-w-[1180px] mx-auto px-6">
          <div className="flex items-end justify-between mb-8">
            <SectionHead eyebrow="Loved by travellers" title="What people say" noMargin />
            <div className="flex gap-2">
              <button onClick={scrollPrev} className="w-9 h-9 rounded-full border border-border flex items-center justify-center hover:border-primary hover:text-primary"><ChevronLeft size={16} /></button>
              <button onClick={scrollNext} className="w-9 h-9 rounded-full border border-border flex items-center justify-center hover:border-primary hover:text-primary"><ChevronRight size={16} /></button>
            </div>
          </div>
          <div className="overflow-hidden" ref={emblaRef}>
            <div className="flex gap-5">
              {TESTIMONIALS.map((t) => (
                <div key={t.name} className="min-w-0 flex-[0_0_100%] md:flex-[0_0_32%]">
                  <Card className="p-6 h-full">
                    <div className="flex gap-1 text-amber-400 mb-3">
                      {Array.from({ length: 5 }).map((_, i) => <Star key={i} size={14} fill="currentColor" />)}
                    </div>
                    <p className="text-[14.5px] text-secondary mb-4">"{t.quote}"</p>
                    <div className="text-[13.5px] font-semibold">{t.name}</div>
                    <div className="text-[12.5px] text-muted">{t.role}</div>
                  </Card>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* FAQ */}
      <section className="py-16 bg-white">
        <div className="max-w-[1180px] mx-auto px-6">
          <SectionHead eyebrow="FAQ" title="Common questions" />
          <div className="max-w-[720px]">
            {FAQS.map((f, i) => (
              <div key={f.q} className="border-b border-border">
                <button onClick={() => setOpenFaq(openFaq === i ? null : i)}
                  className="w-full py-4.5 px-1 flex justify-between items-center font-semibold text-[14.5px] text-left">
                  {f.q}
                  <motion.span animate={{ rotate: openFaq === i ? 180 : 0 }}><ChevronDown size={18} /></motion.span>
                </button>
                <AnimatePresence initial={false}>
                  {openFaq === i && (
                    <motion.div initial={{ height: 0, opacity: 0 }} animate={{ height: "auto", opacity: 1 }}
                      exit={{ height: 0, opacity: 0 }} transition={{ duration: 0.25 }} className="overflow-hidden">
                      <p className="text-muted text-[14px] px-1 pb-4">{f.a}</p>
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>
            ))}
          </div>
        </div>
      </section>
    </>
  );
}

function SectionHead({ eyebrow, title, noMargin, center }) {
  return (
    <div className={`${noMargin ? "" : "mb-8"} ${center ? "text-center flex flex-col items-center" : ""}`}>
      <div className="text-[12.5px] font-bold text-primary uppercase tracking-wider mb-2">{eyebrow}</div>
      <h2 className="text-[28px] font-bold tracking-tight">{title}</h2>
    </div>
  );
}
