import { useState } from "react";
import { ArrowRight, BriefcaseBusiness, Coins, ExternalLink, PackageCheck, RefreshCw, TrendingUp } from "lucide-react";
import { Link } from "react-router-dom";
import { useApi } from "../hooks/useApi";
import api from "../lib/api";

type AdminStats = {
  totalStores: number;
  totalProducts: number;
  totalReservations: number;
  storesGrowth: number;
};

type StoreList = {
  stores: Array<{
    _id: string;
    name: string;
    address?: string;
    isActive: boolean;
    createdAt: string;
    productCount?: number;
    reservationCount?: number;
    retailer?: { ownerName?: string; businessName?: string };
  }>;
  total: number;
};

type RevenueMonth = { month: string; revenue: number };

const currency = new Intl.NumberFormat("en-IN", {
  style: "currency",
  currency: "INR",
  maximumFractionDigits: 0,
});

export default function BusinessDashboardPage() {
  const statsQuery = useApi<AdminStats>("/admin/stats");
  const storesQuery = useApi<StoreList>("/admin/stores", { page: 1, limit: 5 });
  const activeStoresQuery = useApi<StoreList>("/admin/stores", { page: 1, limit: 1, status: "active" });
  const revenueQuery = useApi<RevenueMonth[]>("/admin/revenue-chart");
  const [updatingStoreId, setUpdatingStoreId] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);

  const stats = statsQuery.data;
  const stores = storesQuery.data?.stores ?? [];
  const latestRevenue = revenueQuery.data?.at(-1)?.revenue ?? 0;
  const activeStores = activeStoresQuery.data?.total ?? 0;
  const loadError = statsQuery.error || storesQuery.error || activeStoresQuery.error || revenueQuery.error;

  const refresh = () => {
    void Promise.all([
      statsQuery.refetch(),
      storesQuery.refetch(),
      activeStoresQuery.refetch(),
      revenueQuery.refetch(),
    ]);
  };

  const toggleStore = async (storeId: string) => {
    setUpdatingStoreId(storeId);
    setActionError(null);
    try {
      await api.patch(`/admin/stores/${storeId}/toggle`);
      await Promise.all([storesQuery.refetch(), activeStoresQuery.refetch()]);
    } catch (error: any) {
      setActionError(error.response?.data?.error?.message || "Could not update this business. Please try again.");
    } finally {
      setUpdatingStoreId(null);
    }
  };

  const quickStats = [
    { label: "Active businesses", value: activeStores.toLocaleString("en-IN"), icon: BriefcaseBusiness },
    { label: "Products", value: (stats?.totalProducts ?? 0).toLocaleString("en-IN"), icon: PackageCheck },
    { label: "Latest month revenue", value: currency.format(latestRevenue / 100), icon: Coins },
    {
      label: "Business growth",
      value: `${(stats?.storesGrowth ?? 0) > 0 ? "+" : ""}${stats?.storesGrowth ?? 0}%`,
      icon: TrendingUp,
    },
  ];

  const loading = statsQuery.loading || storesQuery.loading || activeStoresQuery.loading || revenueQuery.loading;

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <p className="text-xs font-semibold uppercase tracking-[0.18em] text-blue-500">Business App</p>
          <h2 className="mt-2 text-2xl font-extrabold text-gray-900">Business dashboard</h2>
        </div>
        <div className="flex items-center gap-2">
          <button type="button" onClick={refresh} className="inline-flex items-center gap-2 rounded-lg border border-gray-200 bg-white px-3 py-2 text-sm font-semibold text-gray-600 hover:bg-gray-50">
            <RefreshCw className={`h-4 w-4 ${loading ? "animate-spin" : ""}`} /> Refresh
          </button>
          <Link to="/stores" className="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white shadow-sm hover:bg-blue-700">
          <BriefcaseBusiness className="h-4 w-4" /> Review operations
          </Link>
        </div>
      </div>

      {(loadError || actionError) && (
        <div role="alert" className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {actionError || loadError}
        </div>
      )}

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        {quickStats.map(({ label, value, icon: Icon }) => (
          <div key={label} className="rounded-lg border border-gray-200 bg-white p-5 shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs text-gray-500">{label}</p>
                <p className="mt-2 text-2xl font-extrabold text-gray-900">{loading && !stats ? "..." : value}</p>
              </div>
              <div className="flex h-11 w-11 items-center justify-center rounded-lg bg-blue-50 text-blue-600">
                <Icon className="h-5 w-5" />
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="grid gap-6 lg:grid-cols-[1.3fr_0.7fr]">
        <div className="rounded-lg border border-gray-200 bg-white p-5 shadow-sm">
          <div className="mb-4 flex items-center justify-between">
            <div>
              <h3 className="text-lg font-bold text-gray-900">Individual businesses</h3>
              <p className="mt-1 text-xs text-gray-500">{storesQuery.data?.total ?? 0} registered businesses</p>
            </div>
            <Link to="/stores" className="inline-flex items-center gap-1 text-sm font-semibold text-blue-600 hover:text-blue-700">
              Manage all <ArrowRight className="h-4 w-4" />
            </Link>
          </div>
          <div className="divide-y divide-gray-100">
            {loading && stores.length === 0 ? (
              <p className="py-8 text-center text-sm text-gray-500">Loading businesses...</p>
            ) : stores.length === 0 ? (
              <p className="py-8 text-center text-sm text-gray-500">No businesses found.</p>
            ) : stores.map((store) => (
              <div key={store._id} className="flex flex-wrap items-center justify-between gap-3 py-4 first:pt-1 last:pb-1">
                <div className="min-w-0">
                  <p className="truncate text-sm font-semibold text-gray-900">{store.name}</p>
                  <p className="mt-1 text-xs text-gray-500">
                    {store.retailer?.ownerName || store.retailer?.businessName || "Business owner"}
                    <span className="mx-1.5 text-gray-300">·</span>
                    {store.productCount ?? 0} products
                    <span className="mx-1.5 text-gray-300">·</span>
                    {store.reservationCount ?? 0} reservations
                  </p>
                </div>
                <div className="flex shrink-0 items-center gap-2">
                  <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${store.isActive ? "bg-emerald-50 text-emerald-700" : "bg-gray-100 text-gray-600"}`}>
                    {store.isActive ? "Active" : "Inactive"}
                  </span>
                  <button
                    type="button"
                    onClick={() => void toggleStore(store._id)}
                    disabled={updatingStoreId !== null}
                    className={`rounded-lg border px-3 py-1.5 text-xs font-semibold transition-colors disabled:cursor-wait disabled:opacity-50 ${store.isActive ? "border-red-200 text-red-700 hover:bg-red-50" : "border-emerald-200 text-emerald-700 hover:bg-emerald-50"}`}
                  >
                    {updatingStoreId === store._id ? "Saving..." : store.isActive ? "Deactivate" : "Activate"}
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="rounded-lg border border-gray-200 bg-white p-5 shadow-sm">
          <h3 className="text-lg font-bold text-gray-900">Business app controls</h3>
          <p className="mt-1 text-sm text-gray-500">Open operational tools for the connected business platform.</p>
          <div className="mt-4 divide-y divide-gray-100">
            {[
              { label: "Store directory", detail: "Review and activate businesses", to: "/stores" },
              { label: "Product catalog", detail: `${(stats?.totalProducts ?? 0).toLocaleString("en-IN")} active products`, to: "/products" },
              { label: "Reservations", detail: `${(stats?.totalReservations ?? 0).toLocaleString("en-IN")} total reservations`, to: "/reservations" },
            ].map((item) => (
              <Link key={item.to} to={item.to} className="flex items-center justify-between gap-3 py-4 first:pt-1 last:pb-1 hover:text-blue-700">
                <span>
                  <span className="block text-sm font-semibold text-gray-800">{item.label}</span>
                  <span className="mt-1 block text-xs text-gray-500">{item.detail}</span>
                </span>
                <ExternalLink className="h-4 w-4 shrink-0 text-gray-400" />
              </Link>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
