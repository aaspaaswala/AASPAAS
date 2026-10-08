import { useState } from "react";
import { Store, Users, Handshake, Send, CheckCircle } from "lucide-react";
import api from "../lib/api";

const types = [
  { icon: Store, label: "Business Owner", desc: "Apni dukaan AasPaas pe list karna chahta hoon" },
  { icon: Users, label: "Customer", desc: "App ke baare mein jaanna chahta hoon" },
  { icon: Handshake, label: "Partnership", desc: "Business partnership ya collaboration" },
];

export default function Inquiry() {
  const [form, setForm] = useState({ type: "", name: "", business: "", city: "", phone: "", email: "", message: "" });
  const [submitted, setSubmitted] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      await api.post("/admin/inquiry", form);
      setSubmitted(true);
    } catch (err: any) {
      setError(err.response?.data?.error?.message || "Inquiry send nahi ho saki. Thodi der baad dobara try karein.");
    } finally {
      setLoading(false);
    }
  };

  if (submitted) {
    return (
      <div className="pt-16 min-h-screen bg-gradient-to-br from-orange-50 via-white to-amber-50 flex items-center justify-center px-4">
        <div className="text-center max-w-md">
          <div className="w-20 h-20 bg-green-50 rounded-full flex items-center justify-center mx-auto mb-6">
            <CheckCircle className="w-10 h-10 text-green-500" />
          </div>
          <h2 className="text-3xl font-extrabold text-gray-900 mb-3">Inquiry Bhej Di! 🎉</h2>
          <p className="text-gray-500 mb-6">Hamari team 24 ghante mein aapse contact karegi. Shukriya!</p>
          <button onClick={() => { setSubmitted(false); setForm({ type: "", name: "", business: "", city: "", phone: "", email: "", message: "" }); }} className="bg-brand-500 text-white px-6 py-3 rounded-xl font-semibold hover:bg-brand-600 transition-colors">
            Nayi Inquiry Bhejo
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="pt-16">
      <section className="bg-gradient-to-br from-orange-50 via-white to-amber-50 py-20">
        <div className="max-w-3xl mx-auto px-4 text-center">
          <h1 className="text-5xl font-extrabold text-gray-900 mb-4">Inquiry Bhejo</h1>
          <p className="text-gray-500 text-lg">AasPaas ke baare mein jaanna chahte ho? Hamari team se baat karo.</p>
        </div>
      </section>

      <section className="py-16 bg-white">
        <div className="max-w-2xl mx-auto px-4 sm:px-6">
          {/* Type selector */}
          <div className="mb-8">
            <p className="text-sm font-semibold text-gray-700 mb-3">Aap kaun hain? *</p>
            <div className="grid sm:grid-cols-3 gap-3">
              {types.map((t) => (
                <button
                  key={t.label}
                  type="button"
                  onClick={() => setForm({ ...form, type: t.label })}
                  className={`p-4 rounded-2xl border-2 text-left transition-all ${form.type === t.label ? "border-brand-500 bg-brand-50" : "border-gray-100 hover:border-gray-200"}`}
                >
                  <t.icon className={`w-6 h-6 mb-2 ${form.type === t.label ? "text-brand-500" : "text-gray-400"}`} />
                  <p className={`text-sm font-bold ${form.type === t.label ? "text-brand-600" : "text-gray-700"}`}>{t.label}</p>
                  <p className="text-xs text-gray-400 mt-0.5 leading-tight">{t.desc}</p>
                </button>
              ))}
            </div>
          </div>

          <form onSubmit={handleSubmit} className="space-y-5">
            <div className="grid sm:grid-cols-2 gap-5">
              <div>
                <label className="block text-xs font-semibold text-gray-600 mb-1.5">Aapka Naam *</label>
                <input required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} placeholder="Full naam" className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100 text-sm" />
              </div>
              <div>
                <label className="block text-xs font-semibold text-gray-600 mb-1.5">Business / Dukaan Ka Naam</label>
                <input value={form.business} onChange={(e) => setForm({ ...form, business: e.target.value })} placeholder="Agar applicable ho" className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100 text-sm" />
              </div>
            </div>

            <div className="grid sm:grid-cols-2 gap-5">
              <div>
                <label className="block text-xs font-semibold text-gray-600 mb-1.5">Phone Number *</label>
                <input required value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} placeholder="+91 XXXXX XXXXX" className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100 text-sm" />
              </div>
              <div>
                <label className="block text-xs font-semibold text-gray-600 mb-1.5">City *</label>
                <input required value={form.city} onChange={(e) => setForm({ ...form, city: e.target.value })} placeholder="Aapki city" className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100 text-sm" />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-600 mb-1.5">Email *</label>
              <input required type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} placeholder="aapka@email.com" className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100 text-sm" />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-600 mb-1.5">Message / Sawaal</label>
              <textarea rows={4} value={form.message} onChange={(e) => setForm({ ...form, message: e.target.value })} placeholder="Koi bhi sawaal ya requirement likhein..." className="w-full px-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100 text-sm resize-none" />
            </div>

            {error && <p role="alert" className="text-sm text-red-600">{error}</p>}
            <button
              type="submit"
              disabled={!form.type || loading}
              className="w-full flex items-center justify-center gap-2 bg-brand-500 text-white py-3.5 rounded-xl font-semibold hover:bg-brand-600 transition-colors shadow-lg shadow-brand-200 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <Send className="w-4 h-4" /> {loading ? "Bhej rahe hain..." : "Inquiry Bhejo"}
            </button>
          </form>
        </div>
      </section>
    </div>
  );
}
