import { Star, Quote } from "lucide-react";

const testimonials = [
  {
    name: "Rahul Sharma",
    role: "Customer, Delhi",
    emoji: "👨",
    text: "Pehle 3-4 dukane ghoomta tha ek product ke liye. Ab AasPaas se pehle check karta hoon — time aur petrol dono bachta hai!",
    rating: 5,
  },
  {
    name: "Priya Mehta",
    role: "Customer, Mumbai",
    emoji: "👩",
    text: "Reservation feature best hai! Maine iPhone reserve kiya, 2 ghante baad jaake le aaya. Koi queue nahi, koi wait nahi.",
    rating: 5,
  },
  {
    name: "Suresh Gupta",
    role: "Shop Owner, Jaipur",
    emoji: "🧑‍💼",
    text: "Mere store mein AasPaas se 40% zyada customers aane lage. Business app se inventory manage karna bahut easy ho gaya.",
    rating: 5,
  },
];

export default function Testimonials() {
  return (
    <section className="py-24 bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <span className="text-brand-500 font-semibold text-sm uppercase tracking-widest">Testimonials</span>
          <h2 className="text-4xl font-extrabold text-gray-900 mt-2 mb-4">Log Kya Kehte Hain</h2>
          <p className="text-gray-500 max-w-xl mx-auto">Hamare users ki real stories — jo AasPaas se fark padha unki zindagi mein.</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {testimonials.map((t) => (
            <div key={t.name} className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 relative">
              <Quote className="w-8 h-8 text-brand-100 absolute top-4 right-4" />
              <div className="flex items-center gap-1 mb-4">
                {[...Array(t.rating)].map((_, i) => (
                  <Star key={i} className="w-4 h-4 fill-amber-400 text-amber-400" />
                ))}
              </div>
              <p className="text-gray-600 text-sm leading-relaxed mb-6">"{t.text}"</p>
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-brand-50 rounded-full flex items-center justify-center text-xl">
                  {t.emoji}
                </div>
                <div>
                  <p className="font-semibold text-gray-900 text-sm">{t.name}</p>
                  <p className="text-xs text-gray-400">{t.role}</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
