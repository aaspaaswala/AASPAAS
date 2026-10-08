import { MapPin, Mail, Phone, Share2, Heart, Briefcase } from "lucide-react";
import { Link } from "react-router-dom";

export default function Footer() {
  return (
    <footer className="bg-gray-900 text-gray-400">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-10 mb-12">
          <div className="md:col-span-1">
            <div className="flex items-center gap-2 mb-4">
              <div className="w-8 h-8 bg-brand-500 rounded-lg flex items-center justify-center">
                <MapPin className="w-5 h-5 text-white" />
              </div>
              <span className="text-xl font-bold text-white">AasPaas</span>
            </div>
            <p className="text-sm leading-relaxed mb-4">
              Hyperlocal offline shopping discovery — apne aas-paas ki duniya ko phone pe laao.
            </p>
            <div className="flex gap-3">
              {[Share2, Heart, Briefcase].map((Icon, i) => (
                <a key={i} href="#" className="w-8 h-8 bg-gray-800 rounded-lg flex items-center justify-center hover:bg-brand-500 transition-colors">
                  <Icon className="w-4 h-4" />
                </a>
              ))}
            </div>
          </div>

          <div>
            <h4 className="text-white font-semibold mb-4">Company</h4>
            <ul className="space-y-2 text-sm">
              {[
                { label: "About Us", to: "/about" },
                { label: "For Customers", to: "/for-customers" },
                { label: "For Business", to: "/for-business" },
                { label: "Inquiry", to: "/inquiry" },
              ].map((l) => (
                <li key={l.label}><Link to={l.to} className="hover:text-brand-400 transition-colors">{l.label}</Link></li>
              ))}
            </ul>
          </div>

          <div>
            <h4 className="text-white font-semibold mb-4">Support</h4>
            <ul className="space-y-2 text-sm">
              {[
                { label: "FAQ", to: "/faq" },
                { label: "Contact Us", to: "/contact" },
                { label: "Privacy Policy", to: "/privacy-policy" },
                { label: "Terms & Conditions", to: "/terms" },
              ].map((l) => (
                <li key={l.label}><Link to={l.to} className="hover:text-brand-400 transition-colors">{l.label}</Link></li>
              ))}
            </ul>
          </div>

          <div>
            <h4 className="text-white font-semibold mb-4">Contact</h4>
            <ul className="space-y-3 text-sm">
              <li className="flex items-center gap-2"><Mail className="w-4 h-4 text-brand-400" /><span>hello@aaspaas.in</span></li>
              <li className="flex items-center gap-2"><Phone className="w-4 h-4 text-brand-400" /><span>+91 98765 43210</span></li>
              <li className="flex items-start gap-2"><MapPin className="w-4 h-4 text-brand-400 mt-0.5" /><span>New Delhi, India</span></li>
            </ul>
          </div>
        </div>

        <div className="border-t border-gray-800 pt-6 flex flex-col sm:flex-row justify-between items-center gap-4 text-xs">
          <p>© 2025 AasPaas Wala. All rights reserved.</p>
          <div className="flex gap-6">
            <Link to="/privacy-policy" className="hover:text-brand-400 transition-colors">Privacy Policy</Link>
            <Link to="/terms" className="hover:text-brand-400 transition-colors">Terms of Service</Link>
          </div>
        </div>
      </div>
    </footer>
  );
}
