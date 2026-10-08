import { BarChart3, Package, Bell, TrendingUp, ArrowRight } from "lucide-react";

const perks = [
  { icon: Package, title: "Inventory Management", desc: "Products aur variants easily manage karo — stock update karo real-time mein." },
  { icon: Bell, title: "Reservation Alerts", desc: "Customer ne reserve kiya toh turant notification — kabhi koi order miss mat karo." },
  { icon: BarChart3, title: "Business Dashboard", desc: "Daily reservations, revenue, aur popular products — sab ek jagah dekho." },
  { icon: TrendingUp, title: "Grow Your Business", desc: "Nearby customers tak pahuncho jo actually tere store mein aana chahte hain." },
];

export default function ForBusiness() {
  return (
    <section id="for-business" className="py-24 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid lg:grid-cols-2 gap-16 items-center">
          {/* Left */}
          <div>
            <span className="text-brand-500 font-semibold text-sm uppercase tracking-widest">For Business Owners</span>
            <h2 className="text-4xl font-extrabold text-gray-900 mt-2 mb-4">
              Apni Dukaan Ko{" "}
              <span className="text-brand-500">Digital Karo</span>
            </h2>
            <p className="text-gray-500 mb-8 leading-relaxed">
              AasPaas Business App se apna inventory list karo, customer reservations manage karo, aur apne aas-paas ke customers tak pahuncho — bilkul free mein shuru karo.
            </p>

            <div className="space-y-5 mb-8">
              {perks.map((p) => (
                <div key={p.title} className="flex items-start gap-4">
                  <div className="w-10 h-10 bg-brand-50 rounded-xl flex items-center justify-center flex-shrink-0">
                    <p.icon className="w-5 h-5 text-brand-500" />
                  </div>
                  <div>
                    <h4 className="font-semibold text-gray-900 text-sm">{p.title}</h4>
                    <p className="text-sm text-gray-500 mt-0.5">{p.desc}</p>
                  </div>
                </div>
              ))}
            </div>

            <a href="#download" className="inline-flex items-center gap-2 bg-gray-900 text-white px-6 py-3 rounded-xl font-semibold hover:bg-gray-800 transition-colors">
              Business App Download Karo <ArrowRight className="w-4 h-4" />
            </a>
          </div>

          {/* Right — Dashboard mockup */}
          <div className="bg-gray-900 rounded-3xl p-6 shadow-2xl">
            <div className="flex items-center justify-between mb-6">
              <div>
                <p className="text-gray-400 text-xs">Good morning,</p>
                <p className="text-white font-bold">Sharma Electronics 👋</p>
              </div>
              <div className="w-8 h-8 bg-brand-500 rounded-full flex items-center justify-center">
                <Bell className="w-4 h-4 text-white" />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3 mb-6">
              {[
                { label: "Today's Reservations", val: "24", color: "bg-brand-500" },
                { label: "Pending Pickup", val: "8", color: "bg-amber-500" },
                { label: "Completed", val: "16", color: "bg-green-500" },
                { label: "Revenue Today", val: "₹12.4K", color: "bg-blue-500" },
              ].map((stat) => (
                <div key={stat.label} className="bg-gray-800 rounded-2xl p-4">
                  <div className={`w-2 h-2 rounded-full ${stat.color} mb-2`} />
                  <p className="text-white text-xl font-bold">{stat.val}</p>
                  <p className="text-gray-400 text-xs mt-1">{stat.label}</p>
                </div>
              ))}
            </div>

            <div className="bg-gray-800 rounded-2xl p-4">
              <p className="text-gray-400 text-xs font-semibold mb-3">RECENT RESERVATIONS</p>
              {[
                { name: "Rahul S.", product: "iPhone 15", status: "PENDING", code: "RES-4821" },
                { name: "Priya M.", product: "Samsung TV", status: "CONFIRMED", code: "RES-4820" },
                { name: "Amit K.", product: "Nike Air Max", status: "COMPLETED", code: "RES-4819" },
              ].map((r) => (
                <div key={r.code} className="flex items-center justify-between py-2 border-b border-gray-700 last:border-0">
                  <div>
                    <p className="text-white text-sm font-medium">{r.name}</p>
                    <p className="text-gray-400 text-xs">{r.product}</p>
                  </div>
                  <span className={`text-xs px-2 py-1 rounded-full font-medium ${
                    r.status === "PENDING" ? "bg-amber-500/20 text-amber-400" :
                    r.status === "CONFIRMED" ? "bg-blue-500/20 text-blue-400" :
                    "bg-green-500/20 text-green-400"
                  }`}>
                    {r.status}
                  </span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
