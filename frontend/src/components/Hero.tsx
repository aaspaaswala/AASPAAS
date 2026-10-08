import { ArrowRight, MapPin, Star } from "lucide-react";

export default function Hero() {
  return (
    <section id="home" className="relative min-h-screen bg-gradient-to-br from-orange-50 via-white to-amber-50 flex items-center overflow-hidden">
      {/* Background blobs */}
      <div className="absolute top-20 right-0 w-96 h-96 bg-brand-100 rounded-full blur-3xl opacity-60 -translate-y-1/2 translate-x-1/3" />
      <div className="absolute bottom-0 left-0 w-80 h-80 bg-amber-100 rounded-full blur-3xl opacity-50" />

      <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24 grid lg:grid-cols-2 gap-16 items-center">
        {/* Left */}
        <div>
          <div className="inline-flex items-center gap-2 bg-brand-50 border border-brand-100 text-brand-600 text-sm font-medium px-4 py-2 rounded-full mb-6">
            <MapPin className="w-4 h-4" />
            Hyperlocal Shopping Discovery
          </div>

          <h1 className="text-5xl lg:text-6xl font-extrabold text-gray-900 leading-tight mb-6">
            Aas Paas Ki{" "}
            <span className="text-brand-500">Dukaan,</span>
            <br />
            Phone Pe{" "}
            <span className="text-brand-500">Pehchaan</span>
          </h1>

          <p className="text-lg text-gray-500 mb-8 max-w-lg leading-relaxed">
            Apne nearby stores mein koi bhi product available hai ya nahi — phone se check karo, reserve karo, aur seedha jaake lo. No more wandering!
          </p>

          <div className="flex flex-wrap gap-4 mb-10">
            <a href="#download" className="flex items-center gap-2 bg-brand-500 text-white px-6 py-3 rounded-xl font-semibold hover:bg-brand-600 transition-all shadow-lg shadow-brand-200">
              Download App <ArrowRight className="w-4 h-4" />
            </a>
            <a href="#how-it-works" className="flex items-center gap-2 border border-gray-200 text-gray-700 px-6 py-3 rounded-xl font-semibold hover:border-brand-300 hover:text-brand-500 transition-all">
              How It Works
            </a>
          </div>

          <div className="flex items-center gap-6">
            <div className="flex -space-x-2">
              {["🧑", "👩", "👨", "🧕"].map((e, i) => (
                <div key={i} className="w-9 h-9 rounded-full bg-gradient-to-br from-brand-400 to-brand-600 border-2 border-white flex items-center justify-center text-sm">
                  {e}
                </div>
              ))}
            </div>
            <div>
              <div className="flex items-center gap-1">
                {[...Array(5)].map((_, i) => <Star key={i} className="w-4 h-4 fill-amber-400 text-amber-400" />)}
              </div>
              <p className="text-sm text-gray-500 mt-0.5">10,000+ happy users</p>
            </div>
          </div>
        </div>

        {/* Right — Phone mockup */}
        <div className="flex justify-center lg:justify-end">
          <div className="relative">
            <div className="w-72 h-[580px] bg-gray-900 rounded-[3rem] shadow-2xl border-4 border-gray-800 overflow-hidden flex flex-col">
              {/* Status bar */}
              <div className="bg-gray-900 px-6 pt-4 pb-2 flex justify-between items-center">
                <span className="text-white text-xs font-medium">9:41</span>
                <div className="w-24 h-5 bg-gray-800 rounded-full" />
              </div>

              {/* App UI mockup */}
              <div className="flex-1 bg-white overflow-hidden">
                <div className="bg-brand-500 px-4 py-4">
                  <div className="flex items-center gap-2 mb-3">
                    <MapPin className="w-4 h-4 text-white" />
                    <span className="text-white text-sm font-medium">Connaught Place, Delhi</span>
                  </div>
                  <div className="bg-white rounded-xl px-3 py-2 flex items-center gap-2">
                    <span className="text-gray-400 text-sm">🔍 Search products nearby...</span>
                  </div>
                </div>

                <div className="px-4 py-3">
                  <p className="text-xs font-semibold text-gray-500 mb-2">NEARBY STORES</p>
                  {[
                    { name: "Sharma Electronics", dist: "0.3 km", tag: "Electronics" },
                    { name: "Gupta Fashion", dist: "0.5 km", tag: "Clothing" },
                    { name: "Modi Grocery", dist: "0.8 km", tag: "Grocery" },
                  ].map((s) => (
                    <div key={s.name} className="flex items-center gap-3 py-2.5 border-b border-gray-50">
                      <div className="w-10 h-10 bg-brand-50 rounded-xl flex items-center justify-center text-lg">🏪</div>
                      <div className="flex-1">
                        <p className="text-sm font-semibold text-gray-800">{s.name}</p>
                        <p className="text-xs text-gray-400">{s.tag} • {s.dist}</p>
                      </div>
                      <span className="text-xs bg-green-50 text-green-600 px-2 py-1 rounded-full font-medium">Open</span>
                    </div>
                  ))}
                </div>

                <div className="px-4 py-2">
                  <p className="text-xs font-semibold text-gray-500 mb-2">TRENDING PRODUCTS</p>
                  <div className="grid grid-cols-2 gap-2">
                    {[
                      { name: "iPhone 15", price: "₹79,999", emoji: "📱" },
                      { name: "Nike Shoes", price: "₹4,999", emoji: "👟" },
                    ].map((p) => (
                      <div key={p.name} className="bg-gray-50 rounded-xl p-2">
                        <div className="text-2xl mb-1">{p.emoji}</div>
                        <p className="text-xs font-semibold text-gray-800">{p.name}</p>
                        <p className="text-xs text-brand-500 font-bold">{p.price}</p>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>

            {/* Floating badges */}
            <div className="absolute -left-12 top-24 bg-white rounded-2xl shadow-xl px-4 py-3 flex items-center gap-2">
              <span className="text-2xl">📍</span>
              <div>
                <p className="text-xs font-bold text-gray-800">Found!</p>
                <p className="text-xs text-gray-500">3 stores nearby</p>
              </div>
            </div>
            <div className="absolute -right-10 bottom-32 bg-white rounded-2xl shadow-xl px-4 py-3 flex items-center gap-2">
              <span className="text-2xl">✅</span>
              <div>
                <p className="text-xs font-bold text-gray-800">Reserved!</p>
                <p className="text-xs text-gray-500">6 hrs to pickup</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Stats bar */}
      <div className="absolute bottom-0 left-0 right-0 bg-white/80 backdrop-blur-sm border-t border-gray-100">
        <div className="max-w-7xl mx-auto px-4 py-4 grid grid-cols-3 gap-4 text-center">
          {[
            { val: "500+", label: "Partner Stores" },
            { val: "10K+", label: "Happy Customers" },
            { val: "50K+", label: "Products Listed" },
          ].map((s) => (
            <div key={s.label}>
              <p className="text-xl font-extrabold text-brand-500">{s.val}</p>
              <p className="text-xs text-gray-500">{s.label}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
