import { MapPin, Clock, Bell, Shield, Zap, Star } from "lucide-react";

const features = [
  {
    icon: MapPin,
    title: "Hyperlocal Search",
    desc: "GPS-based real-time search — sirf wahi stores dikhte hain jo actually tere paas hain.",
    color: "text-brand-500 bg-brand-50",
  },
  {
    icon: Clock,
    title: "6-Hour Reservation",
    desc: "Product reserve karo, 6 ghante ka window milta hai pickup ke liye. No prepayment needed.",
    color: "text-blue-500 bg-blue-50",
  },
  {
    icon: Bell,
    title: "Live Notifications",
    desc: "Reservation confirm hua, expire hone wala hai — har update ka instant notification milega.",
    color: "text-purple-500 bg-purple-50",
  },
  {
    icon: Shield,
    title: "Secure & Trusted",
    desc: "OTP-based login, JWT authentication — tera data aur transactions 100% secure hain.",
    color: "text-green-500 bg-green-50",
  },
  {
    icon: Zap,
    title: "Instant Availability",
    desc: "Real-time inventory sync — agar store ne stock update kiya toh turant reflect hoga.",
    color: "text-amber-500 bg-amber-50",
  },
  {
    icon: Star,
    title: "Store Ratings",
    desc: "Community reviews aur ratings se best nearby stores dhundho — quality guaranteed.",
    color: "text-pink-500 bg-pink-50",
  },
];

export default function Features() {
  return (
    <section id="features" className="py-24 bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <span className="text-brand-500 font-semibold text-sm uppercase tracking-widest">Why AasPaas</span>
          <h2 className="text-4xl font-extrabold text-gray-900 mt-2 mb-4">Features Jo Fark Karte Hain</h2>
          <p className="text-gray-500 max-w-xl mx-auto">Offline shopping ko online ki convenience dena — yahi hai AasPaas ka mission.</p>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {features.map((f) => (
            <div key={f.title} className="bg-white rounded-2xl p-6 shadow-sm hover:shadow-md transition-shadow border border-gray-100 group">
              <div className={`w-12 h-12 rounded-xl ${f.color} flex items-center justify-center mb-4 group-hover:scale-110 transition-transform`}>
                <f.icon className="w-6 h-6" />
              </div>
              <h3 className="text-lg font-bold text-gray-900 mb-2">{f.title}</h3>
              <p className="text-sm text-gray-500 leading-relaxed">{f.desc}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
