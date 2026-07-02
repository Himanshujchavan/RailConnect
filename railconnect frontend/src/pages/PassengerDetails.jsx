import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useForm, useFieldArray } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { motion, AnimatePresence } from "framer-motion";
import { toast } from "sonner";
import { useApp } from "../context/AppContext";
import PageHeader from "../components/layout/PageHeader";
import { Card } from "../components/ui/Card";
import { Input, Field } from "../components/ui/Input";
import Button from "../components/ui/Button";

const passengerSchema = z.object({
  name: z.string().min(2, "Enter full name"),
  age: z.coerce.number().min(1, "Enter age").max(120),
  gender: z.string(),
  berth: z.string(),
});
const schema = z.object({ passengers: z.array(passengerSchema).min(1) });

export default function PassengerDetails() {
  const navigate = useNavigate();
  const { search, setPassengers } = useApp();

  const { control, register, handleSubmit, formState: { errors } } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      passengers: Array.from({ length: search.pax }, () => ({ name: "", age: "", gender: "Male", berth: "No preference" })),
    },
  });
  const { fields, append } = useFieldArray({ control, name: "passengers" });

  const onSubmit = (data) => {
    setPassengers(data.passengers);
    navigate("/review");
  };

  const autofill = (e) => {
    if (e.target.value) toast.info("Passenger details auto-filled");
  };

  return (
    <div>
      <PageHeader
        crumbs={[{ label: "Home", to: "/" }, { label: "Passenger details" }]}
        title="Passenger details"
        steps={["Select train", "Passengers", "Review", "Payment"]}
        activeStep={1}
      />
      <div className="max-w-[760px] mx-auto px-6 py-7 pb-16">
        <Field label="Saved traveller">
          <select onChange={autofill} className="w-full px-3.5 py-2.5 rounded-lg border border-border text-[14.5px] bg-white">
            <option>Choose a saved passenger…</option>
            <option>Aarav Sharma (32, M)</option>
            <option>Priya Sharma (29, F)</option>
          </select>
        </Field>

        <form onSubmit={handleSubmit(onSubmit)}>
          <AnimatePresence>
            {fields.map((field, i) => (
              <motion.div key={field.id} initial={{ opacity: 0, x: 24 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 0.05 }}>
                <Card className="p-5 mb-3.5">
                  <div className="flex justify-between items-center mb-3.5">
                    <b>Passenger {i + 1}</b>
                    {i === 0 && <span className="text-muted text-[12.5px]">Primary contact</span>}
                  </div>
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                    <Field label="Full name" error={errors.passengers?.[i]?.name?.message}>
                      <Input placeholder="Full name" {...register(`passengers.${i}.name`)} />
                    </Field>
                    <Field label="Age" error={errors.passengers?.[i]?.age?.message}>
                      <Input type="number" placeholder="Age" {...register(`passengers.${i}.age`)} />
                    </Field>
                    <Field label="Gender">
                      <select {...register(`passengers.${i}.gender`)} className="w-full px-3.5 py-2.5 rounded-lg border border-border text-[14.5px] bg-white">
                        <option>Male</option><option>Female</option><option>Other</option>
                      </select>
                    </Field>
                    <Field label="Berth preference">
                      <select {...register(`passengers.${i}.berth`)} className="w-full px-3.5 py-2.5 rounded-lg border border-border text-[14.5px] bg-white">
                        <option>No preference</option><option>Lower</option><option>Upper</option><option>Window</option>
                      </select>
                    </Field>
                  </div>
                </Card>
              </motion.div>
            ))}
          </AnimatePresence>

          <Button type="button" variant="outline" size="sm" onClick={() => append({ name: "", age: "", gender: "Male", berth: "No preference" })}>
            + Add another passenger
          </Button>

          <div className="mt-8">
            <Button type="submit" size="lg" full>Continue to review →</Button>
          </div>
        </form>
      </div>
    </div>
  );
}
