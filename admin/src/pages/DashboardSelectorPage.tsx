import { ArrowRight, Globe2, ShoppingBag, Store } from "lucide-react";
import { Link } from "react-router-dom";

const dashboards = [
  {
    title: "Customer App",
    description: "View customer activity, discovery, and reservation engagement.",
    to: "/customer-dashboard",
    icon: ShoppingBag,
    color: "text-emerald-600",
    iconBackground: "bg-emerald-50",
    border: "hover:border-emerald-200",
  },
  {
    title: "Business App",
    description: "Manage businesses, products, and store reservations.",
    to: "/business-dashboard",
    icon: Store,
    color: "text-brand-600",
    iconBackground: "bg-brand-50",
    border: "hover:border-brand-200",
  },
  {
    title: "Website",
    description: "Review website performance, campaigns, and inquiries.",
    to: "/website-dashboard",
    icon: Globe2,
    color: "text-violet-600",
    iconBackground: "bg-violet-50",
    border: "hover:border-violet-200",
  },
];

export default function DashboardSelectorPage() {
  return (
    <div className="mx-auto max-w-6xl space-y-6">
      <div>
        <p className="text-xs font-semibold uppercase tracking-[0.18em] text-brand-600">Admin workspace</p>
        <h2 className="mt-2 text-2xl font-extrabold text-gray-900">Choose a dashboard</h2>
        <p className="mt-2 text-sm text-gray-500">Select the platform area you want to manage.</p>
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        {dashboards.map(({ title, description, to, icon: Icon, color, iconBackground, border }) => (
          <Link
            key={to}
            to={to}
            className={`group flex min-h-60 flex-col rounded-2xl border border-gray-100 bg-white p-6 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md ${border}`}
          >
            <div className={`flex h-12 w-12 items-center justify-center rounded-xl ${iconBackground} ${color}`}>
              <Icon className="h-6 w-6" />
            </div>
            <h3 className="mt-5 text-lg font-bold text-gray-900">{title}</h3>
            <p className="mt-2 flex-1 text-sm leading-6 text-gray-500">{description}</p>
            <span className={`mt-5 inline-flex items-center gap-2 text-sm font-semibold ${color}`}>
              Open dashboard
              <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-1" />
            </span>
          </Link>
        ))}
      </div>
    </div>
  );
}
