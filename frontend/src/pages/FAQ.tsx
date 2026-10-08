import { useState } from "react";
import { ChevronDown, ChevronUp, Search } from "lucide-react";
import { Link } from "react-router-dom";

const categories = [
  {
    label: "General",
    emoji: "💡",
    faqs: [
      { q: "AasPaas Wala kya hai?", a: "AasPaas Wala ek hyperlocal shopping discovery platform hai. Customers apne nearby offline stores mein products search kar sakte hain, availability check kar sakte hain, aur reserve kar sakte hain — bina ghar se nikle." },
      { q: "Yeh kaise kaam karta hai?", a: "App download karo → location allow karo → product search karo → nearby stores dekho → reserve karo → 6 ghante mein jaake pickup karo. Itna simple!" },
      { q: "Kaunse cities mein available hai?", a: "Abhi Delhi, Mumbai, Jaipur, aur Bangalore mein available hai. Jaldi aur cities mein expand ho raha hai. Apni city ke liye waitlist mein join karo!" },
      { q: "AasPaas ka use karne ke liye koi fee hai?", a: "Customers ke liye bilkul free hai. Business owners ke liye free starter plan available hai, aur premium plans bhi hain." },
    ],
  },
  {
    label: "For Customers",
    emoji: "🛍️",
    faqs: [
      { q: "Reservation ke liye advance payment karni padti hai?", a: "Nahi! Reservation completely free hai. Payment sirf store pe jaake product lete waqt karni hai — cash ya card, jo bhi store accept kare." },
      { q: "Reservation kitne time ke liye valid hota hai?", a: "Reservation 6 ghante ke liye valid hota hai. Agar 6 ghante mein pickup nahi kiya toh automatically cancel ho jaata hai — koi penalty nahi." },
      { q: "Kya main reservation cancel kar sakta hoon?", a: "Haan! App se kabhi bhi reservation cancel kar sakte ho — koi charge nahi, koi sawaal nahi." },
      { q: "Agar store pe product available nahi hua toh?", a: "Reservation confirm hone ke baad store ne product hold kar liya hota hai. Agar koi issue aata hai toh store turant notify karega aur reservation cancel ho jaayega." },
      { q: "Ek baar mein kitne reservations kar sakte hain?", a: "Ek baar mein multiple reservations kar sakte ho — alag-alag stores pe, alag-alag products ke liye." },
    ],
  },
  {
    label: "For Business",
    emoji: "🏪",
    faqs: [
      { q: "Apni dukaan kaise register karein?", a: "AasPaas Business App download karo, sign up karo, store details bharo, aur products add karo. 10 minute mein done!" },
      { q: "Kya inventory manually update karni padti hai?", a: "Haan, abhi inventory manually update karni padti hai. Jaldi hi POS integration aayega jisse automatic sync hoga." },
      { q: "Reservation aane pe kya karna hota hai?", a: "App pe notification aayega. Confirm karo, product hold karo, aur customer ke aane pe reservation code verify karke product do." },
      { q: "Kya multiple stores manage kar sakte hain?", a: "Haan! Enterprise plan mein multiple stores ek hi account se manage kar sakte ho." },
      { q: "Commission kitna lagta hai?", a: "Abhi koi commission nahi hai. Flat monthly subscription plan hai — jo dikhta hai wahi pay karo." },
    ],
  },
  {
    label: "Technical",
    emoji: "⚙️",
    faqs: [
      { q: "Kaunse devices pe app available hai?", a: "Android (Play Store) aur iOS (App Store) dono pe available hai. Minimum Android 8.0 aur iOS 13 chahiye." },
      { q: "Kya data secure hai?", a: "Haan! OTP-based login, JWT authentication, encrypted data storage — tera data 100% secure hai." },
      { q: "App kaam nahi kar raha — kya karein?", a: "App force close karke reopen karo. Agar problem continue kare toh support@aaspaas.in pe email karo ya Contact page pe form bharo." },
      { q: "Location permission kyun chahiye?", a: "Nearby stores dhundne ke liye GPS location zaroori hai. Bina location ke app kaam nahi karega." },
    ],
  },
];

function FAQItem({ q, a }: { q: string; a: string }) {
  const [open, setOpen] = useState(false);
  return (
    <div className="border border-gray-100 rounded-2xl overflow-hidden">
      <button
        onClick={() => setOpen(!open)}
        className="w-full flex items-center justify-between p-5 text-left bg-white hover:bg-gray-50 transition-colors"
      >
        <span className="font-semibold text-gray-900 text-sm pr-4">{q}</span>
        {open ? <ChevronUp className="w-5 h-5 text-brand-500 flex-shrink-0" /> : <ChevronDown className="w-5 h-5 text-gray-400 flex-shrink-0" />}
      </button>
      {open && (
        <div className="px-5 pb-5 bg-white">
          <p className="text-gray-500 text-sm leading-relaxed border-t border-gray-50 pt-4">{a}</p>
        </div>
      )}
    </div>
  );
}

export default function FAQ() {
  const [active, setActive] = useState("General");
  const [search, setSearch] = useState("");

  const current = categories.find((c) => c.label === active)!;
  const filtered = search
    ? categories.flatMap((c) => c.faqs).filter((f) => f.q.toLowerCase().includes(search.toLowerCase()) || f.a.toLowerCase().includes(search.toLowerCase()))
    : current.faqs;

  return (
    <div className="pt-16">
      {/* Hero */}
      <section className="bg-gradient-to-br from-orange-50 via-white to-amber-50 py-20">
        <div className="max-w-3xl mx-auto px-4 text-center">
          <h1 className="text-5xl font-extrabold text-gray-900 mb-4">Frequently Asked Questions</h1>
          <p className="text-gray-500 text-lg mb-8">Koi sawaal hai? Yahan jawab milega.</p>
          <div className="relative">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              type="text"
              placeholder="Sawaal search karo..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-12 pr-4 py-4 rounded-2xl border border-gray-200 focus:outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100 text-sm shadow-sm"
            />
          </div>
        </div>
      </section>

      <section className="py-16 bg-white">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
          {!search && (
            <div className="flex flex-wrap gap-3 mb-10 justify-center">
              {categories.map((c) => (
                <button
                  key={c.label}
                  onClick={() => setActive(c.label)}
                  className={`flex items-center gap-2 px-5 py-2.5 rounded-xl text-sm font-semibold transition-all ${active === c.label ? "bg-brand-500 text-white shadow-md shadow-brand-200" : "bg-gray-100 text-gray-600 hover:bg-gray-200"}`}
                >
                  <span>{c.emoji}</span> {c.label}
                </button>
              ))}
            </div>
          )}

          {search && (
            <p className="text-sm text-gray-500 mb-6 text-center">
              "{search}" ke liye {filtered.length} result{filtered.length !== 1 ? "s" : ""} mila
            </p>
          )}

          <div className="space-y-3">
            {filtered.length > 0 ? (
              filtered.map((f, i) => <FAQItem key={i} q={f.q} a={f.a} />)
            ) : (
              <div className="text-center py-12">
                <p className="text-4xl mb-3">🤔</p>
                <p className="text-gray-500">Koi result nahi mila. Directly contact karo!</p>
                <Link to="/contact" className="inline-block mt-4 bg-brand-500 text-white px-6 py-2 rounded-xl text-sm font-semibold hover:bg-brand-600 transition-colors">
                  Contact Karo
                </Link>
              </div>
            )}
          </div>
        </div>
      </section>

      {/* Still have questions */}
      <section className="py-16 bg-gray-50">
        <div className="max-w-3xl mx-auto px-4 text-center">
          <p className="text-4xl mb-4">💬</p>
          <h2 className="text-2xl font-extrabold text-gray-900 mb-3">Aur Sawaal Hain?</h2>
          <p className="text-gray-500 mb-6">Hamari team se directly baat karo — hum help karne ke liye hain.</p>
          <div className="flex flex-wrap justify-center gap-4">
            <Link to="/contact" className="bg-brand-500 text-white px-6 py-3 rounded-xl font-semibold hover:bg-brand-600 transition-colors">
              Contact Karo
            </Link>
            <Link to="/inquiry" className="border border-gray-200 text-gray-700 px-6 py-3 rounded-xl font-semibold hover:border-brand-300 hover:text-brand-500 transition-colors">
              Inquiry Bhejo
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
}
