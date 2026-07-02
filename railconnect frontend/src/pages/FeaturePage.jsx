import { Link, useParams } from "react-router-dom";
import { motion } from "framer-motion";
import { FEATURE_PAGES } from "../data/featurePages";
import PageHeader from "../components/layout/PageHeader";
import { Card } from "../components/ui/Card";
import Button from "../components/ui/Button";

export default function FeaturePage({ pageKey }) {
  const params = useParams();
  const page = FEATURE_PAGES[pageKey];

  if (!page) return null;

  const title = pageKey === "journey-detail" && params.id ? `Journey details · ${params.id}` : page.title;

  return (
    <div>
      <PageHeader crumbs={page.crumbs} title={title} subtitle={page.subtitle} />

      <div className="max-w-[1180px] mx-auto px-6 py-7 pb-16">
        <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {page.overview.map((item) => (
            <Card key={item.label} className="p-5">
              <div className="text-[12.5px] text-muted mb-2">{item.label}</div>
              <div className="text-[26px] font-bold font-mono text-secondary">{item.value}</div>
              <div className="text-[12.5px] text-accent mt-1.5 font-semibold">{item.note}</div>
            </Card>
          ))}
        </motion.div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-5">
          {page.sections.map((section) => (
            <Card key={section.title} className="p-6">
              <h4 className="font-semibold mb-3">{section.title}</h4>
              <div className="flex flex-wrap gap-2">
                {section.items.map((item) => (
                  <span key={item} className="px-3 py-2 rounded-lg border border-border bg-white text-[13px] text-muted">
                    {item}
                  </span>
                ))}
              </div>
            </Card>
          ))}
        </div>

        <Card className="p-6 mt-5">
          <div className="flex items-center justify-between flex-wrap gap-3">
            <div>
              <h4 className="font-semibold">Keep the flow moving</h4>
              <p className="text-muted text-[13.5px] mt-1">Every page links into the booking or account path, so there is no dead end.</p>
            </div>
            <div className="flex gap-2 flex-wrap">
              {page.actions.map((action) => (
                <Link key={action.to} to={action.to}>
                  <Button variant="outline" size="sm">{action.label}</Button>
                </Link>
              ))}
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
}
