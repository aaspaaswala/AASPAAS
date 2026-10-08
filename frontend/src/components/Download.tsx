import { Smartphone, ArrowRight } from "lucide-react";

export default function Download() {
  return (
    <section id="download" className="py-24 bg-gradient-to-br from-brand-500 to-brand-700 relative overflow-hidden">
      <div className="absolute inset-0 opacity-10">
        <div className="absolute top-0 left-1/4 w-96 h-96 bg-white rounded-full blur-3xl" />
        <div className="absolute bottom-0 right-1/4 w-80 h-80 bg-white rounded-full blur-3xl" />
      </div>

      <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <div className="w-16 h-16 bg-white/20 rounded-2xl flex items-center justify-center mx-auto mb-6">
          <Smartphone className="w-8 h-8 text-white" />
        </div>

        <h2 className="text-4xl lg:text-5xl font-extrabold text-white mb-4">
          Abhi Download Karo
        </h2>
        <p className="text-brand-100 text-lg mb-10 max-w-xl mx-auto">
          Customer ho ya business owner — AasPaas ka app download karo aur hyperlocal shopping revolution ka hissa bano.
        </p>

        <div className="flex flex-wrap justify-center gap-4 mb-12">
          <a href="#" className="flex items-center gap-3 bg-black text-white px-6 py-4 rounded-2xl hover:bg-gray-900 transition-colors shadow-xl">
            <span className="text-3xl">🍎</span>
            <div className="text-left">
              <p className="text-xs text-gray-400">Download on the</p>
              <p className="font-bold text-lg leading-tight">App Store</p>
            </div>
          </a>
          <a href="#" className="flex items-center gap-3 bg-black text-white px-6 py-4 rounded-2xl hover:bg-gray-900 transition-colors shadow-xl">
            <span className="text-3xl">🤖</span>
            <div className="text-left">
              <p className="text-xs text-gray-400">Get it on</p>
              <p className="font-bold text-lg leading-tight">Google Play</p>
            </div>
          </a>
        </div>

        <div className="border-t border-white/20 pt-8">
          <p className="text-brand-100 text-sm mb-4">Business owner ho? Apni dukaan register karo</p>
          <a href="#" className="inline-flex items-center gap-2 bg-white text-brand-600 px-6 py-3 rounded-xl font-semibold hover:bg-brand-50 transition-colors">
            Business App Download Karo <ArrowRight className="w-4 h-4" />
          </a>
        </div>
      </div>
    </section>
  );
}
