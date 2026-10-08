import { Link } from "react-router-dom";
import { Home, ArrowLeft } from "lucide-react";

export default function NotFound() {
  return (
    <div className="pt-16 min-h-screen bg-gradient-to-br from-orange-50 via-white to-amber-50 flex items-center justify-center px-4">
      <div className="text-center max-w-md">
        <p className="text-8xl mb-6">🗺️</p>
        <h1 className="text-6xl font-extrabold text-gray-900 mb-3">404</h1>
        <h2 className="text-2xl font-bold text-gray-700 mb-3">Yeh Page Nahi Mila!</h2>
        <p className="text-gray-500 mb-8">Lagta hai aap galat jagah aa gaye. Yeh page exist nahi karta ya move ho gaya hai.</p>
        <div className="flex flex-wrap justify-center gap-4">
          <Link to="/" className="flex items-center gap-2 bg-brand-500 text-white px-6 py-3 rounded-xl font-semibold hover:bg-brand-600 transition-colors">
            <Home className="w-4 h-4" /> Home Jaao
          </Link>
          <button onClick={() => window.history.back()} className="flex items-center gap-2 border border-gray-200 text-gray-700 px-6 py-3 rounded-xl font-semibold hover:border-brand-300 hover:text-brand-500 transition-colors">
            <ArrowLeft className="w-4 h-4" /> Wapas Jaao
          </button>
        </div>
      </div>
    </div>
  );
}
