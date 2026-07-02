import { motion } from "framer-motion";
import PageHeader from "../components/layout/PageHeader";

export default function About() {
  return (
    <div>
      <PageHeader crumbs={[{ label: "Home", to: "/" }, { label: "About" }]} title="About RailConnect" />
      <section className="py-14">
        <div className="max-w-[1180px] mx-auto px-6 grid grid-cols-1 md:grid-cols-2 gap-10">
          <motion.div initial={{ opacity: 0, y: 16 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }}>
            <h3 className="font-display text-xl mb-2.5">Our mission</h3>
            <p className="text-muted">
              RailConnect exists to make booking a train ticket as fast as checking the weather — transparent
              pricing, real seat availability, and zero surprises.
            </p>
          </motion.div>
          <motion.div initial={{ opacity: 0, y: 16 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }} transition={{ delay: 0.1 }}>
            <h3 className="font-display text-xl mb-2.5">Built on modern rails</h3>
            <p className="text-muted">
              A Spring Boot backend and a React frontend power millions of searches with sub-second response times.
            </p>
          </motion.div>
        </div>
      </section>
    </div>
  );
}
