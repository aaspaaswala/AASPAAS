import { useState } from "react";
import { Mail, Phone, MapPin, Clock, Send, CheckCircle } from "lucide-react";
import api from "../lib/api";

const info = [
  { icon: Mail, label: "Email", value: "hello@aaspaas.in", sub: "24 ghante mein reply" },
  { icon: Phone, label: "Phone", value: "+91 98765 43210", sub: "Mon–Sat, 10am–6pm" },
  { icon: MapPin, label: "Office", value: "New Delhi, India", sub: "Connaught Place" },
  { icon: Clock, label: "Support Hours", value: "Mon–Sat", sub: "10:00 AM – 6:00 PM IST" },
];

export default function Contact() {
  const [form, setForm] = useState({ name: "", email: "", phone: "", subject: "", message: "" });
  const [submitted, setSubmitted] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      await api.post("/admin/inquiry", { ...form, type: "Contact" });
      setSubmitted(true);
    } catch (err: any) {
      setError(err.response?.data?.error?.message || "Message send nahi ho saka. Thodi der baad dobara try karein.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="pt-16">
      {/* Hero */}
      <section className="bg-gradient-to-br from-orange-50 via-white to-amber-50 py-20">
        <div className="max-w-3xl mx-auto px-4 text-center">
          <h1 className="text-5xl font-extrabold text-gray-900 mb-4">Hamare Saath Baat Karo</h1>
          <p className="text-gray-500 text-lg">Koi bhi sawaal, suggestion, ya problem — hum yahan hain.</p>
        </div>
      </section>

      <section className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 grid lg:grid-cols-2 gap-16">
          {/* Info */}
          <div>
            <h2 className="text-2xl font-extrabold text-gray-900 mb-2">Contact Information</h2>
            <p className="text-gray-500 mb-8 text-sm">Seedha hamare saath connect karo — hum jaldi reply karte hain.</p>

            <div className="space-y-5 mb-10">
              {info.map((i) => (
                <div key={i.label} className="flex items-start gap-4">
                  <div className="w-11 h-11 bg-brand-50 rounded-xl flex items-center justify-center flex-shrink-0">
                    <i.icon className="w-5 h-5 text-brand-500" />
                  </div>
                  <div>
                    <p className="text-xs text-gray-400 font-medium uppercase tracking-wider">{i.label}</p>
                    <p className="font-semibold text-gray-900 text-sm">{i.value}</p>
                    <p className="text-xs text-gray-400">{i.sub}</p>
                  </div>
                </div>
              ))}
            </div>

            <div className="bg-gray-900 rounded-3xl p-6">
              <p className="text-white font-bold mb-1">Quick Response Chahiye?</p>
              <p className="text-gray-400 text-sm mb-4">WhatsApp pe message karo — 1 ghante mein reply milega.</p>
              <a href="https://wa.me/919876543210" target="_blank" rel="noreferrer" className="inline-flex items-center gap-2 bg-green-500 text-white px-5 py-2.5 rounded-xl text-sm font-semibold hover:bg-green-600 transition-colors">
                <span className="text-lg">💬</span> WhatsApp Karo
              </a>
            </div>
          </div>

          {/* Form */}
          <div>
            {submitted ? (
              <div className="h-full flex flex-col items-center justify-center text-center py-16">
                <div className="w-16 h-16 bg-green-50 rounded-full flex items-center justify-center mb-4">
                  <CheckCircle className="w-8 h-8 text-green-500" />
                </div>
                <h3 className="text-2xl font-extrabold text-gray-900 mb-2">Message Bhej Diya! 🎉</h3>
                <p className="text-gray-500 text-sm max-w-xs">Hum 24 ghante mein reply karenge. Shukriya hamare saath contact karne ke liye!</p>
                <button onClick={() => { setSubmitted(false); setForm({ name: "", email: "", phone: "", subject: "", message: "" }); }} className="mt-6 text-brand-500 text-sm font-semibold hover:underline">
                  Aur message bhejo
                </button>
              </div>
            ) : (
              <form onSubmit={handleSubmit} className="space-y-5">
                <h2 className="text-2xl font-extrabold text-gray-900 mb-6">Message Bhejo</h2>
                {error && <p role="alert" className="text-sm text-red-600">{error}</p>}
                <div className="grid sm:grid-cols-2 gap-5">
                  <div>
                    <label className="block text-xs font-semibold text-gray-600 mb-1.5">Naam *</label>
                    <input required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} placeholder="Aapka naam" className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100 text-sm" />
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-gray-600 mb-1.5">Phone</label>
                    <input value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} placeholder="+91 XXXXX XXXXX" className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100 text-sm" />
                  </div>
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-600 mb-1.5">Email *</label>
                  <input required type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} placeholder="aapka@email.com" className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100 text-sm" />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-600 mb-1.5">Subject *</label>
                  <select required value={form.subject} onChange={(e) => setForm({ ...form, subject: e.target.value })} className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100 text-sm text-gray-700">
                    <option value="">Select karo</option>
                    <option>General Inquiry</option>
                    <option>Technical Support</option>
                    <option>Business Partnership</option>
                    <option>Feedback / Suggestion</option>
                    <option>Report a Problem</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-600 mb-1.5">Message *</label>
                  <textarea required rows={5} value={form.message} onChange={(e) => setForm({ ...form, message: e.target.value })} placeholder="Apna message yahan likhein..." className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100 text-sm resize-none" />
                </div>
                <button type="submit" disabled={loading} className="w-full flex items-center justify-center gap-2 bg-brand-500 text-white py-3.5 rounded-xl font-semibold hover:bg-brand-600 transition-colors shadow-lg shadow-brand-200 disabled:opacity-60">
                  <Send className="w-4 h-4" /> {loading ? "Bhej rahe hain..." : "Message Bhejo"}
                </button>
              </form>
            )}
          </div>
        </div>
      </section>
    </div>
  );
}
