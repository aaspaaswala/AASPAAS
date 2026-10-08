import { Package, Bell, BarChart3, TrendingUp, Users, Shield, ArrowRight, Store, CheckCircle } from "lucide-react";
import { Link } from "react-router-dom";

const steps = [
  { step: "01", title: "Business App Download Karo", desc: "AasPaas Business app download karo — Android pe available hai." },
  { step: "02", title: "Store Register Karo", desc: "Apni dukaan ki details bharo — naam, address, category, timings." },
  { step: "03", title: "Products Add Karo", desc: "Apna inventory add karo — products, variants, prices, stock quantity." },
  { step: "04", title: "Customers Aayenge!", desc: "Nearby customers tere products dhundhenge, reserve karenge — tujhe sirf confirm karna hai." },
];

const features = [
  { icon: Package, title: "Easy Inventory Management", desc: "Products aur variants easily add/edit karo. Stock real-time update hota hai." },
  { icon: Bell, title: "Instant Reservation Alerts", desc: "Customer ne reserve kiya toh turant notification — kabhi koi order miss nahi hoga." },
  { icon: BarChart3, title: "Business Dashboard", desc: "Daily reservations, revenue, popular products — sab ek jagah dekho." },
  { icon: TrendingUp, title: "Grow Your Reach", desc: "Nearby customers tak pahuncho jo actually tere store mein aana chahte hain." },
  { icon: Users, title: "Customer Insights", desc: "Kaunse products popular hain, kaunse customers repeat aate hain — data se samjho." },
  { icon: Shield, title: "Secure Platform", desc: "Tera business data aur customer info 100% secure hai hamare platform pe." },
];

const plans = [
  {
    name: "Starter",
    price: "Free",
    desc: "Chhoti dukaan ke liye perfect",
    features: ["Up to 50 products", "Basic dashboard", "Reservation management", "Email support"],
    cta: "Free Mein Shuru Karo",
    highlight: false,
  },
  {
    name: "Growth",
    price: "₹499/mo",
    desc: "Growing businesses ke liye",
    features: ["Unlimited products", "Advanced analytics", "Priority notifications", "Phone support", "Featured listing"],
    cta: "14 Din Free Try Karo",
    highlight: true,
  },
  {
    name: "Enterprise",
    price: "Custom",
    desc: "Multiple stores ke liye",
    features: ["Multiple store management", "Custom integrations", "Dedicated account manager", "24/7 support"],
    cta: "Baat Karo Hamare Saath",
    highlight: false,
  },
];

export default function ForBusiness() {
  return (
    <div className="pt-16">
      {/* Hero */}
      <section className="bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 py-24">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 grid lg:grid-cols-2 gap-16 items-center">
          <div>
            <div className="inline-flex items-center gap-2 bg-white/10 text-white text-sm font-medium px-4 py-2 rounded-full mb-6">
              <Store className="w-4 h-4" /> For Business Owners
            </div>
            <h1 className="text-5xl font-extrabold text-white mb-6 leading-tight">
              Apni Dukaan Ko{" "}
              <span className="text-brand-400">Digital Karo</span>
            </h1>
            <p className="text-lg text-gray-300 mb-8 leading-relaxed">
              AasPaas Business App se apna inventory list karo, nearby customers tak pahuncho, aur reservations manage karo — sab ek jagah, bilkul asaan.
            </p>
            <div className="flex flex-wrap gap-4">
              <a href="#" className="flex items-center gap-3 bg-brand-500 text-white px-6 py-3 rounded-xl font-semibold hover:bg-brand-600 transition-colors">
                <span className="text-xl">🤖</span> Business App Download
              </a>
              <Link to="/inquiry" className="flex items-center gap-2 border border-white/30 text-white px-6 py-3 rounded-xl font-semibold hover:bg-white/10 transition-colors">
                Inquiry Karo <ArrowRight className="w-4 h-4" />
              </Link>
            </div>
          </div>

          {/* Dashboard mockup */}
          <div className="bg-gray-800 rounded-3xl p-5 shadow-2xl border border-gray-700">
            <div className="flex items-center justify-between mb-5">
              <div>
                <p className="text-gray-400 text-xs">Welcome back,</p>
                <p className="text-white font-bold">Sharma Electronics 👋</p>
              </div>
              <div className="w-8 h-8 bg-brand-500 rounded-full flex items-center justify-center">
                <Bell className="w-4 h-4 text-white" />
              </div>
            </div>
            <div className="grid grid-cols-2 gap-3 mb-4">
              {[
                { label: "Today's Reservations", val: "24", color: "bg-brand-500" },
                { label: "Pending Pickup", val: "8", color: "bg-amber-500" },
                { label: "Completed", val: "16", color: "bg-green-500" },
                { label: "Revenue Today", val: "₹12.4K", color: "bg-blue-500" },
              ].map((s) => (
                <div key={s.label} className="bg-gray-700 rounded-2xl p-3">
                  <div className={`w-2 h-2 rounded-full ${s.color} mb-2`} />
                  <p className="text-white text-lg font-bold">{s.val}</p>
                  <p className="text-gray-400 text-xs mt-0.5">{s.label}</p>
                </div>
              ))}
            </div>
            <div className="bg-gray-700 rounded-2xl p-3">
              <p className="text-gray-400 text-xs font-semibold mb-2">RECENT RESERVATIONS</p>
              {[
                { name: "Rahul S.", product: "iPhone 15", status: "PENDING" },
                { name: "Priya M.", product: "Samsung TV", status: "CONFIRMED" },
                { name: "Amit K.", product: "Nike Air Max", status: "COMPLETED" },
              ].map((r) => (
                <div key={r.name} className="flex items-center justify-between py-2 border-b border-gray-600 last:border-0">
                  <div>
                    <p className="text-white text-xs font-medium">{r.name}</p>
                    <p className="text-gray-400 text-xs">{r.product}</p>
                  </div>
                  <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${
                    r.status === "PENDING" ? "bg-amber-500/20 text-amber-400" :
                    r.status === "CONFIRMED" ? "bg-blue-500/20 text-blue-400" :
                    "bg-green-500/20 text-green-400"
                  }`}>{r.status}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* How to start */}
      <section className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-14">
            <h2 className="text-3xl font-extrabold text-gray-900">Shuru Kaise Karein?</h2>
            <p className="text-gray-500 mt-2">4 simple steps — 10 minute mein apni dukaan online</p>
          </div>
          <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-8">
            {steps.map((s) => (
              <div key={s.step} className="text-center group">
                <div className="w-14 h-14 bg-gray-900 rounded-2xl flex items-center justify-center mx-auto mb-4 group-hover:bg-brand-500 transition-colors">
                  <span className="text-white font-bold text-lg">{s.step}</span>
                </div>
                <h3 className="font-bold text-gray-900 mb-2">{s.title}</h3>
                <p className="text-sm text-gray-500 leading-relaxed">{s.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Features */}
      <section className="py-20 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-14">
            <h2 className="text-3xl font-extrabold text-gray-900">Business App Features</h2>
            <p className="text-gray-500 mt-2">Sab kuch jo ek dukandaar ko chahiye</p>
          </div>
          <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {features.map((f) => (
              <div key={f.title} className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 flex gap-4 hover:shadow-md transition-shadow">
                <div className="w-10 h-10 bg-gray-900 rounded-xl flex items-center justify-center flex-shrink-0">
                  <f.icon className="w-5 h-5 text-white" />
                </div>
                <div>
                  <h3 className="font-bold text-gray-900 text-sm mb-1">{f.title}</h3>
                  <p className="text-xs text-gray-500 leading-relaxed">{f.desc}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Pricing */}
      <section className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-14">
            <h2 className="text-3xl font-extrabold text-gray-900">Simple Pricing</h2>
            <p className="text-gray-500 mt-2">Koi hidden charges nahi — jo dikhta hai wahi hai</p>
          </div>
          <div className="grid md:grid-cols-3 gap-6 max-w-5xl mx-auto">
            {plans.map((p) => (
              <div key={p.name} className={`rounded-3xl p-6 border-2 ${p.highlight ? "border-brand-500 bg-brand-50 shadow-xl shadow-brand-100" : "border-gray-100 bg-white shadow-sm"}`}>
                {p.highlight && <span className="bg-brand-500 text-white text-xs font-bold px-3 py-1 rounded-full mb-4 inline-block">MOST POPULAR</span>}
                <h3 className="text-xl font-bold text-gray-900">{p.name}</h3>
                <p className="text-3xl font-extrabold text-gray-900 mt-2 mb-1">{p.price}</p>
                <p className="text-sm text-gray-500 mb-6">{p.desc}</p>
                <ul className="space-y-2 mb-6">
                  {p.features.map((f) => (
                    <li key={f} className="flex items-center gap-2 text-sm text-gray-600">
                      <CheckCircle className="w-4 h-4 text-green-500 flex-shrink-0" /> {f}
                    </li>
                  ))}
                </ul>
                <Link to="/inquiry" className={`block text-center py-3 rounded-xl font-semibold text-sm transition-colors ${p.highlight ? "bg-brand-500 text-white hover:bg-brand-600" : "bg-gray-900 text-white hover:bg-gray-800"}`}>
                  {p.cta}
                </Link>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-16 bg-gray-900">
        <div className="max-w-3xl mx-auto px-4 text-center">
          <h2 className="text-3xl font-extrabold text-white mb-4">Aaj Hi Apni Dukaan Register Karo</h2>
          <p className="text-gray-400 mb-8">500+ stores already AasPaas pe hain. Tum kab aoge?</p>
          <div className="flex flex-wrap justify-center gap-4">
            <a href="#" className="bg-brand-500 text-white px-6 py-3 rounded-xl font-semibold hover:bg-brand-600 transition-colors">
              Business App Download
            </a>
            <Link to="/contact" className="border border-gray-600 text-white px-6 py-3 rounded-xl font-semibold hover:border-gray-400 transition-colors">
              Hamare Saath Baat Karo
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
}
