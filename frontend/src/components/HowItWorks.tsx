import { Search, MapPin, ShoppingBag, CheckCircle } from "lucide-react";

const steps = [
  {
    icon: Search,
    step: "01",
    title: "Product Search Karo",
    desc: "Jo bhi chahiye — electronics, clothes, grocery — search karo. AasPaas turant nearby stores mein dhundta hai.",
    color: "bg-blue-50 text-blue-500",
  },
  {
    icon: MapPin,
    step: "02",
    title: "Nearby Store Dekho",
    desc: "GPS se tere aas-paas ke stores dikhte hain jahan woh product available hai — distance aur stock ke saath.",
    color: "bg-brand-50 text-brand-500",
  },
  {
    icon: ShoppingBag,
    step: "03",
    title: "Reserve Karo",
    desc: "Pasand aaya? Ek tap mein reserve karo. Store 6 ghante tak product hold karta hai sirf tere liye.",
    color: "bg-purple-50 text-purple-500",
  },
  {
    icon: CheckCircle,
    step: "04",
    title: "Jaake Pickup Karo",
    desc: "Reservation code lekar dukan jaao, product lo. No waiting, no disappointment!",
    color: "bg-green-50 text-green-500",
  },
];

export default function HowItWorks() {
  return (
    <section id="how-it-works" className="py-24 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <span className="text-brand-500 font-semibold text-sm uppercase tracking-widest">Simple Process</span>
          <h2 className="text-4xl font-extrabold text-gray-900 mt-2 mb-4">Kaam Kaise Karta Hai?</h2>
          <p className="text-gray-500 max-w-xl mx-auto">Sirf 4 steps mein apne nearby store se product reserve karo — bilkul asaan!</p>
        </div>

        <div className="relative">
          {/* Connector line */}
          <div className="hidden lg:block absolute top-16 left-[12.5%] right-[12.5%] h-0.5 bg-gradient-to-r from-blue-200 via-brand-200 to-green-200" />

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
            {steps.map((s) => (
              <div key={s.step} className="relative flex flex-col items-center text-center group">
                <div className={`w-16 h-16 rounded-2xl ${s.color} flex items-center justify-center mb-4 group-hover:scale-110 transition-transform shadow-sm`}>
                  <s.icon className="w-7 h-7" />
                </div>
                <span className="text-xs font-bold text-gray-300 mb-1">STEP {s.step}</span>
                <h3 className="text-lg font-bold text-gray-900 mb-2">{s.title}</h3>
                <p className="text-sm text-gray-500 leading-relaxed">{s.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  );
}
