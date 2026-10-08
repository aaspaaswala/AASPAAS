import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./lib/auth";
import AdminLayout from "./layouts/AdminLayout";
import Login from "./pages/Login";
import Businesses from "./pages/Businesses";
import Stores from "./pages/Stores";
import StoreProfilePage from "./pages/StoreProfilePage";
import Products from "./pages/Products";
import Reservations from "./pages/Reservations";
import Customers from "./pages/Customers";
import Categories from "./pages/Categories";
import Inquiries from "./pages/Inquiries";
import Settings from "./pages/Settings";
import CustomerDashboardPage from "./pages/CustomerDashboardPage";
import BusinessDashboardPage from "./pages/BusinessDashboardPage";
import WebsiteDashboardPage from "./pages/WebsiteDashboardPage";
import DashboardSelectorPage from "./pages/DashboardSelectorPage";

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { user, loading } = useAuth();
  if (loading) return (
    <div className="min-h-screen flex items-center justify-center bg-slate-900">
      <div className="text-center">
        <div className="w-10 h-10 border-2 border-brand-500 border-t-transparent rounded-full animate-spin mx-auto mb-3" />
        <p className="text-slate-400 text-sm">Loading...</p>
      </div>
    </div>
  );
  return user ? <>{children}</> : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/*" element={
            <ProtectedRoute>
              <AdminLayout />
            </ProtectedRoute>
          }>
            <Route index element={<DashboardSelectorPage />} />
            <Route path="customer-dashboard" element={<CustomerDashboardPage />} />
            <Route path="business-dashboard" element={<BusinessDashboardPage />} />
            <Route path="frontend-dashboard" element={<WebsiteDashboardPage />} />
            <Route path="website-dashboard" element={<WebsiteDashboardPage />} />
            <Route path="stores" element={<Stores />} />
            <Route path="businesses" element={<Businesses />} />
            <Route path="stores/:storeId" element={<StoreProfilePage />} />
            <Route path="products" element={<Products />} />
            <Route path="reservations" element={<Reservations />} />
            <Route path="customers" element={<Customers />} />
            <Route path="categories" element={<Categories />} />
            <Route path="inquiries" element={<Inquiries />} />
            <Route path="settings" element={<Settings />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
