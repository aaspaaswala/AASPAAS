import { Search, MapPin, ShoppingBag, CheckCircle, Bell, Shield, Star, ArrowRight, Smartphone } from "lucide-react";
import { Link } from "react-router-dom";

const steps = [
  { icon: Search, step: "01", title: "App Download Karo", desc: "AasPaas app Google Play ya App Store se download karo — bilkul free hai." },
  { icon: MapPin, step: "02", title: "Location Allow Karo", desc: "App ko location access do — yeh tere aas-paas ke stores dhundne ke liye zaroori hai." },
  { icon: ShoppingBag, step: "03", title: "Product Search Karo", desc: "Jo chahiye woh search karo — electronics, clothes, grocery, kuch bhi." },
  { icon: CheckCircle, step: "04", title: "Reserve Karo & Pickup Karo", desc: "Pasand aaya store? Reserve karo, 6 ghante mein jaake le aao. Simple!" },
];

const benefits = [
  { icon: MapPin, title: "Nearby Stores Dekho", desc: "GPS se real-time nearby stores aur unka stock dekho — koi guessing nahi." },
  { icon: Bell, title: "Instant Notifications", desc: "Reservation confirm hua ya expire hone wala hai — turant alert milega." },
  { icon: Shield, title: "Safe & Secure", desc: "OTP login, secure payments — tera data 100% safe hai hamare paas." },
  { icon: Star, title: "Store Reviews", desc: "Community ratings se best stores dhundho — quality guaranteed." },
  { icon: ShoppingBag, title: "6-Hour Hold", desc: "Reserve karo, 6 ghante ka window milta hai — no prepayment needed." },
  { icon: CheckCircle, title: "Easy Cancellation", desc: "Plan change hua? Reservation cancel karo — koi charge nahi, koi tension nahi." },
];

const faqs = [
  { q: "Kya AasPaas free hai?", a: "Haan! Customers ke liye AasPaas bilkul free hai — download karo, search karo, reserve karo." },
  { q: "Reservation ke liye payment karni padti hai?", a: "Nahi! Reservation free hai. Payment sirf store pe jaake product lete waqt karni hai." },
  { q: "Agar reservation expire ho jaaye toh?", a: "6 ghante mein pickup nahi kiya toh reservation automatically cancel ho jaata hai — koi penalty nahi." },
  { q: "Kaunse cities mein available hai?", a: "Abhi Delhi, Mumbai, Jaipur, Bangalore mein available hai. Jaldi aur cities mein aayega!" },
];

export default function ForCustomers() {
  return (
    <div className="pt-16">
      {/* Hero */}
      <section className="bg-gradient-to-br from-blue-50 via-white to-brand-50 py-24">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 grid lg:grid-cols-2 gap-16 items-center">
          <div>
            <div className="inline-flex items-center gap-2 bg-blue-50 border border-blue-100 text-blue-600 text-sm font-medium px-4 py-2 rounded-full mb-6">
              <Smartphone className="w-4 h-4" /> For Customers
            </div>
            <h1 className="text-5xl font-extrabold text-gray-900 mb-6 leading-tight">
              Shopping Ko Banao{" "}
              <span className="text-brand-500">Smart</span>
            </h1>
            <p className="text-lg text-gray-500 mb-8 leading-relaxed">
              Kisi bhi product ke liye dukane ghoomna band karo. AasPaas se pehle check karo — kahan available hai, kitne mein hai, aur reserve karo — sab phone pe.
            </p>
            <div className="flex flex-wrap gap-4">
              <a href="#" className="flex items-center gap-3 bg-gray-900 text-white px-6 py-3 rounded-xl font-semibold hover:bg-gray-800 transition-colors">
                <span className="text-xl">🤖</span> Google Play
              </a>
              <a href="#" className="flex items-center gap-3 bg-gray-900 text-white px-6 py-3 rounded-xl font-semibold hover:bg-gray-800 transition-colors">
                <span className="text-xl">🍎</span> App Store
              </a>
            </div>
          </div>

          {/* Phone mockup */}
          <div className="flex justify-center">
            <div className="w-64 h-[520px] bg-gray-900 rounded-[2.5rem] shadow-2xl border-4 border-gray-800 overflow-hidden flex flex-col">
              <div className="bg-brand-500 px-4 py-5">
                <div className="flex items-center gap-2 mb-3">
                  <MapPin className="w-4 h-4 text-white" />
                  <span className="text-white text-xs font-medium">Lajpat Nagar, Delhi</span>
                </div>
                <div className="bg-white rounded-xl px-3 py-2">
                  <span className="text-gray-400 text-xs">🔍 Nike Air Max...</span>
                </div>
              </div>
              <div className="flex-1 bg-white px-3 py-3 overflow-hidden">
                <p className="text-xs font-bold text-gray-400 mb-2">3 STORES FOUND NEARBY</p>
                {[
                  { name: "Sports Zone", dist: "0.2 km", price: "₹8,999", avail: true },
                  { name: "Nike Store", dist: "0.6 km", price: "₹9,499", avail: true },
                  { name: "Shoe Palace", dist: "1.1 km", price: "₹8,499", avail: false },
                ].map((s) => (
                  <div key={s.name} className="flex items-center gap-2 py-2.5 border-b border-gray-50">
                    <div className="w-8 h-8 bg-brand-50 rounded-lg flex items-center justify-center text-sm">👟</div>
                    <div className="flex-1">
                      <p className="text-xs font-bold text-gray-800">{s.name}</p>
                      <p className="text-xs text-gray-400">{s.dist} • {s.price}</p>
                    </div>
                    <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${s.avail ? "bg-green-50 text-green-600" : "bg-red-50 text-red-400"}`}>
                      {s.avail ? "Reserve" : "Out"}
                    </span>
                  </div>
                ))}
                <div className="mt-3 bg-brand-500 rounded-xl p-3 text-center">
                  <p className="text-white text-xs font-bold">✅ Reserved!</p>
                  <p className="text-brand-100 text-xs">RES-4821 • 6 hrs left</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* How it works */}
      <section className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-14">
            <h2 className="text-3xl font-extrabold text-gray-900">Shuru Kaise Karein?</h2>
            <p className="text-gray-500 mt-2">Sirf 4 steps — 2 minute mein ready</p>
          </div>
          <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-8">
            {steps.map((s) => (
              <div key={s.step} className="relative text-center group">
                <div className="w-16 h-16 bg-brand-50 rounded-2xl flex items-center justify-center mx-auto mb-4 group-hover:bg-brand-500 transition-colors">
                  <s.icon className="w-7 h-7 text-brand-500 group-hover:text-white transition-colors" />
                </div>
                <span className="text-xs font-bold text-gray-300">STEP {s.step}</span>
                <h3 className="text-base font-bold text-gray-900 mt-1 mb-2">{s.title}</h3>
                <p className="text-sm text-gray-500 leading-relaxed">{s.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Benefits */}
      <section className="py-20 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-14">
            <h2 className="text-3xl font-extrabold text-gray-900">Tumhe Kya Milega?</h2>
            <p className="text-gray-500 mt-2">AasPaas use karne ke fayde</p>
          </div>
          <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {benefits.map((b) => (
              <div key={b.title} className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 flex gap-4 hover:shadow-md transition-shadow">
                <div className="w-10 h-10 bg-brand-50 rounded-xl flex items-center justify-center flex-shrink-0">
                  <b.icon className="w-5 h-5 text-brand-500" />
                </div>
                <div>
                  <h3 className="font-bold text-gray-900 text-sm mb-1">{b.title}</h3>
                  <p className="text-xs text-gray-500 leading-relaxed">{b.desc}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* FAQ */}
      <section className="py-20 bg-white">
        <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-extrabold text-gray-900">Customer FAQs</h2>
          </div>
          <div className="space-y-4">
            {faqs.map((f) => (
              <div key={f.q} className="bg-gray-50 rounded-2xl p-5 border border-gray-100">
                <p className="font-semibold text-gray-900 text-sm mb-2">❓ {f.q}</p>
                <p className="text-gray-500 text-sm leading-relaxed">{f.a}</p>
              </div>
            ))}
          </div>
          <div className="text-center mt-8">
            <Link to="/faq" className="text-brand-500 font-semibold text-sm hover:underline flex items-center justify-center gap-1">
              Aur sawaal hain? Full FAQ dekho <ArrowRight className="w-4 h-4" />
            </Link>
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-16 bg-brand-500">
        <div className="max-w-3xl mx-auto px-4 text-center">
          <h2 className="text-3xl font-extrabold text-white mb-4">Abhi Download Karo — Free Hai!</h2>
          <p className="text-brand-100 mb-8">10,000+ customers already use kar rahe hain. Tum kab shuru karoge?</p>
          <div className="flex flex-wrap justify-center gap-4">
            <a href="#" className="flex items-center gap-3 bg-black text-white px-6 py-3 rounded-xl font-semibold hover:bg-gray-900 transition-colors">
              <span className="text-xl">🤖</span> Google Play
            </a>
            <a href="#" className="flex items-center gap-3 bg-black text-white px-6 py-3 rounded-xl font-semibold hover:bg-gray-900 transition-colors">
              <span className="text-xl">🍎</span> App Store
            </a>
          </div>
        </div>
      </section>
    </div>
  );
}
