import { ArrowRight, FileText, Globe, Megaphone, Rocket } from "lucide-react";

const quickStats = [
  { label: "Visitors", value: "52.8K", icon: Globe },
  { label: "Landing pages", value: "18", icon: FileText },
  { label: "Campaigns", value: "6", icon: Megaphone },
  { label: "Conversions", value: "3.7%", icon: Rocket },
];

const tasks = [
  { title: "SEO improvements", value: "3 pages need metadata updates" },
  { title: "Lead submissions", value: "41 new inquiries this week" },
  { title: "Marketing campaigns", value: "2 campaigns running live" },
];

export default function WebsiteDashboardPage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-xs font-semibold uppercase tracking-[0.18em] text-violet-500">Website</p>
          <h2 className="mt-2 text-2xl font-extrabold text-gray-900">Website dashboard</h2>
        </div>
        <button className="inline-flex items-center gap-2 rounded-xl bg-violet-500 px-4 py-2 text-sm font-semibold text-white shadow-sm hover:bg-violet-600">
          <Rocket className="h-4 w-4" /> View campaigns
        </button>
      </div>

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        {quickStats.map(({ label, value, icon: Icon }) => (
          <div key={label} className="rounded-2xl border border-violet-100 bg-white p-5 shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs text-gray-500">{label}</p>
                <p className="mt-2 text-2xl font-extrabold text-gray-900">{value}</p>
              </div>
              <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-violet-50 text-violet-600">
                <Icon className="h-5 w-5" />
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="grid gap-6 lg:grid-cols-[1.3fr_0.7fr]">
        <div className="rounded-2xl border border-gray-100 bg-white p-5 shadow-sm">
          <div className="mb-4 flex items-center justify-between">
            <h3 className="text-lg font-bold text-gray-900">Marketing overview</h3>
            <span className="rounded-full bg-violet-50 px-2 py-1 text-xs font-semibold text-violet-600">Active</span>
          </div>
          <div className="space-y-4">
            {tasks.map((item) => (
              <div key={item.title} className="flex items-center justify-between rounded-xl bg-slate-50 p-3">
                <div>
                  <p className="text-sm font-semibold text-gray-800">{item.title}</p>
                  <p className="text-xs text-gray-500">{item.value}</p>
                </div>
                <ArrowRight className="h-4 w-4 text-gray-400" />
              </div>
            ))}
          </div>
        </div>

        <div className="rounded-2xl border border-gray-100 bg-white p-5 shadow-sm">
          <h3 className="text-lg font-bold text-gray-900">Priority actions</h3>
          <div className="mt-4 space-y-3">
            {[
              "Refresh homepage and SEO content",
              "Review inquiry conversion funnel",
              "Optimize campaign CTAs and landing pages",
            ].map((task) => (
              <div key={task} className="rounded-xl border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-700">
                {task}
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
