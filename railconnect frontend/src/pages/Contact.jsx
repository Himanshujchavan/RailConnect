import { toast } from "sonner";
import PageHeader from "../components/layout/PageHeader";
import { Card, CardBody } from "../components/ui/Card";
import { Input, Field } from "../components/ui/Input";
import Button from "../components/ui/Button";

export default function Contact() {
  const submit = (e) => {
    e.preventDefault();
    toast.success("Message sent — we'll reply soon.");
    e.target.reset();
  };

  return (
    <div>
      <PageHeader crumbs={[{ label: "Home", to: "/" }, { label: "Contact" }]} title="Get in touch" />
      <section className="py-14">
        <div className="max-w-[1180px] mx-auto px-6 grid grid-cols-1 md:grid-cols-2 gap-6">
          <Card>
            <CardBody>
              <form onSubmit={submit}>
                <Field label="Your name"><Input placeholder="Full name" required /></Field>
                <Field label="Email"><Input type="email" placeholder="you@example.com" required /></Field>
                <Field label="Message">
                  <textarea rows={4} required placeholder="How can we help?"
                    className="w-full px-3.5 py-2.5 rounded-lg border border-border text-[14.5px] font-body" />
                </Field>
                <Button type="submit">Send message</Button>
              </form>
            </CardBody>
          </Card>
          <Card>
            <CardBody>
              <h4 className="font-semibold mb-2">Support hours</h4>
              <p className="text-muted text-sm">24×7 chat support · support@railconnect.in · 1800-123-4567</p>
            </CardBody>
          </Card>
        </div>
      </section>
    </div>
  );
}
