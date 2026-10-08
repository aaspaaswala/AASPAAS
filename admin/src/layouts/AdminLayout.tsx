import { useState } from "react";
import { Outlet, useLocation } from "react-router-dom";
import { Menu, Bell, Search } from "lucide-react";
import Sidebar from "../components/Sidebar";

const titles: Record<string, string> = {
  "/": "Select Dashboard",
  "/customer-dashboard": "Customer Dashboard",
  "/business-dashboard": "Business Dashboard",
  "/frontend-dashboard": "Frontend Dashboard",
  "/website-dashboard": "Frontend Dashboard",
  "/stores": "Stores",
  "/products": "Products",
  "/reservations": "Reservations",
  "/customers": "Customers",
  "/categories": "Categories",
  "/settings": "Settings",
};

export default function AdminLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { pathname } = useLocation();
  const pageTitle = titles[pathname] || (pathname.startsWith("/stores/") ? "Business Profile" : "Admin");

  return (
    <div className="flex h-screen bg-slate-50 overflow-hidden">
      <Sidebar open={sidebarOpen} onClose={() => setSidebarOpen(false)} />

      <div className="flex-1 flex flex-col min-w-0 lg:ml-64">
        <header className="h-16 bg-white border-b border-gray-100 flex items-center justify-between px-4 sm:px-6 flex-shrink-0 z-20">
          <div className="flex items-center gap-3">
            <button onClick={() => setSidebarOpen(true)} className="lg:hidden p-2 hover:bg-gray-100 rounded-xl">
              <Menu className="w-5 h-5 text-gray-600" />
            </button>
            <div>
              <h1 className="font-bold text-gray-900 text-lg leading-tight">{pageTitle}</h1>
              <p className="text-xs text-gray-400 hidden sm:block">AasPaas Admin Panel</p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <button className="hidden sm:flex items-center gap-2 px-3 py-2 text-sm text-gray-500 bg-gray-50 border border-gray-200 rounded-xl hover:bg-gray-100 transition-colors">
              <Search className="w-4 h-4" />
              <span className="text-xs">Quick search...</span>
            </button>
            <button className="relative p-2 hover:bg-gray-100 rounded-xl transition-colors">
              <Bell className="w-5 h-5 text-gray-600" />
              <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-brand-500 rounded-full" />
            </button>
            <div className="w-8 h-8 bg-brand-500 rounded-full flex items-center justify-center text-white text-sm font-bold">A</div>
          </div>
        </header>

        <main className="flex-1 overflow-y-auto p-4 sm:p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
