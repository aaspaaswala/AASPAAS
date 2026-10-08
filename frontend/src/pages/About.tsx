import { Target, Eye, Heart, Users, Store, MapPin } from "lucide-react";
import { Link } from "react-router-dom";

const team = [
  { name: "Rohit Sharma", role: "Founder & CEO", emoji: "👨‍💼" },
  { name: "Priya Singh", role: "Co-Founder & CTO", emoji: "👩‍💻" },
  { name: "Amit Verma", role: "Head of Product", emoji: "👨‍🎨" },
  { name: "Neha Gupta", role: "Head of Marketing", emoji: "👩‍📢" },
];

const values = [
  { icon: Heart, title: "Community First", desc: "Hum local businesses aur customers dono ki community build kar rahe hain — ek dusre ke liye." },
  { icon: Target, title: "Hyperlocal Focus", desc: "Sirf aas-paas — global nahi, local. Teri gali ki dukaan ko digital banana hai." },
  { icon: Users, title: "Inclusive Growth", desc: "Chhoti se chhoti dukaan bhi AasPaas pe aa sake — technology sabke liye honi chahiye." },
];

const milestones = [
  { year: "2023", event: "AasPaas ka idea aaya — local shopping problem solve karne ki soch" },
  { year: "2024 Q1", event: "Beta launch — Delhi mein 50 stores ke saath shuruat" },
  { year: "2024 Q3", event: "500+ stores onboard, 10,000+ customers" },
  { year: "2025", event: "Pan-India expansion — 10 cities mein launch" },
];

export default function About() {
  return (
    <div className="pt-16">
      {/* Hero */}
      <section className="bg-gradient-to-br from-orange-50 via-white to-amber-50 py-24">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <div className="inline-flex items-center gap-2 bg-brand-50 border border-brand-100 text-brand-600 text-sm font-medium px-4 py-2 rounded-full mb-6">
            <MapPin className="w-4 h-4" /> Our Story
          </div>
          <h1 className="text-5xl font-extrabold text-gray-900 mb-6">
            Hum Kaun Hain?
          </h1>
          <p className="text-xl text-gray-500 leading-relaxed max-w-2xl mx-auto">
            AasPaas Wala ek hyperlocal shopping discovery platform hai — jahan customers apne nearby stores mein products dhundh sakte hain aur reserve kar sakte hain, bina ghar se nikle.
          </p>
        </div>
      </section>

      {/* Mission & Vision */}
      <section className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 grid md:grid-cols-2 gap-10">
          <div className="bg-brand-50 rounded-3xl p-8 border border-brand-100">
            <div className="w-12 h-12 bg-brand-500 rounded-2xl flex items-center justify-center mb-4">
              <Target className="w-6 h-6 text-white" />
            </div>
            <h2 className="text-2xl font-bold text-gray-900 mb-3">Hamara Mission</h2>
            <p className="text-gray-600 leading-relaxed">
              India ke har chhote-bade offline store ko digital visibility dena — taaki customers ko pata chale ki unke aas-paas kya available hai, aur dukandaar ko zyada customers mile.
            </p>
          </div>
          <div className="bg-gray-900 rounded-3xl p-8">
            <div className="w-12 h-12 bg-white/10 rounded-2xl flex items-center justify-center mb-4">
              <Eye className="w-6 h-6 text-white" />
            </div>
            <h2 className="text-2xl font-bold text-white mb-3">Hamara Vision</h2>
            <p className="text-gray-300 leading-relaxed">
              Ek aisa India jahan koi bhi customer kisi bhi product ke liye 3-4 dukane na ghume — AasPaas se pehle check kare, reserve kare, aur seedha jaake le aaye.
            </p>
          </div>
        </div>
      </section>

      {/* Stats */}
      <section className="py-16 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
            {[
              { icon: Store, val: "500+", label: "Partner Stores", color: "text-brand-500 bg-brand-50" },
              { icon: Users, val: "10K+", label: "Happy Customers", color: "text-blue-500 bg-blue-50" },
              { icon: MapPin, val: "10", label: "Cities", color: "text-purple-500 bg-purple-50" },
              { icon: Heart, val: "50K+", label: "Products Listed", color: "text-green-500 bg-green-50" },
            ].map((s) => (
              <div key={s.label} className="bg-white rounded-2xl p-6 text-center shadow-sm border border-gray-100">
                <div className={`w-12 h-12 ${s.color} rounded-xl flex items-center justify-center mx-auto mb-3`}>
                  <s.icon className="w-6 h-6" />
                </div>
                <p className="text-3xl font-extrabold text-gray-900">{s.val}</p>
                <p className="text-sm text-gray-500 mt-1">{s.label}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Values */}
      <section className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-extrabold text-gray-900">Hamare Values</h2>
            <p className="text-gray-500 mt-2">Jo cheezein hume drive karti hain</p>
          </div>
          <div className="grid md:grid-cols-3 gap-6">
            {values.map((v) => (
              <div key={v.title} className="text-center p-6">
                <div className="w-14 h-14 bg-brand-50 rounded-2xl flex items-center justify-center mx-auto mb-4">
                  <v.icon className="w-7 h-7 text-brand-500" />
                </div>
                <h3 className="text-lg font-bold text-gray-900 mb-2">{v.title}</h3>
                <p className="text-gray-500 text-sm leading-relaxed">{v.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Timeline */}
      <section className="py-20 bg-gray-50">
        <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-extrabold text-gray-900">Hamara Safar</h2>
            <p className="text-gray-500 mt-2">Idea se product tak ka journey</p>
          </div>
          <div className="relative">
            <div className="absolute left-4 top-0 bottom-0 w-0.5 bg-brand-100" />
            <div className="space-y-8">
              {milestones.map((m, i) => (
                <div key={i} className="flex gap-6 pl-12 relative">
                  <div className="absolute left-0 w-8 h-8 bg-brand-500 rounded-full flex items-center justify-center text-white text-xs font-bold flex-shrink-0">
                    {i + 1}
                  </div>
                  <div className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 flex-1">
                    <span className="text-brand-500 text-xs font-bold uppercase tracking-wider">{m.year}</span>
                    <p className="text-gray-700 text-sm mt-1">{m.event}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Team */}
      <section className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-extrabold text-gray-900">Hamari Team</h2>
            <p className="text-gray-500 mt-2">Jo log AasPaas ko banate hain</p>
          </div>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
            {team.map((t) => (
              <div key={t.name} className="text-center group">
                <div className="w-20 h-20 bg-gradient-to-br from-brand-100 to-brand-200 rounded-2xl flex items-center justify-center text-4xl mx-auto mb-3 group-hover:scale-105 transition-transform">
                  {t.emoji}
                </div>
                <p className="font-bold text-gray-900 text-sm">{t.name}</p>
                <p className="text-xs text-gray-500 mt-0.5">{t.role}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-16 bg-brand-500">
        <div className="max-w-3xl mx-auto px-4 text-center">
          <h2 className="text-3xl font-extrabold text-white mb-4">Hamare Saath Judiye</h2>
          <p className="text-brand-100 mb-8">Customer ho ya business owner — AasPaas aapka intezaar kar raha hai.</p>
          <div className="flex flex-wrap justify-center gap-4">
            <Link to="/for-customers" className="bg-white text-brand-600 px-6 py-3 rounded-xl font-semibold hover:bg-brand-50 transition-colors">
              Customer Hoon
            </Link>
            <Link to="/for-business" className="border border-white text-white px-6 py-3 rounded-xl font-semibold hover:bg-white/10 transition-colors">
              Business Owner Hoon
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
}
