import { ArrowRight, MapPin, RefreshCw, Search, ShoppingBag, Users } from "lucide-react";
import { Link } from "react-router-dom";
import { useApi } from "../hooks/useApi";

type AdminStats = {
  totalCustomers: number;
  activeReservations: number;
  totalReservations: number;
  customersGrowth: number;
};

export default function CustomerDashboardPage() {
  const statsQuery = useApi<AdminStats>("/admin/stats");
  const customersQuery = useApi<any>("/admin/users", { page: 1, limit: 5 });
  const reservationsQuery = useApi<any>("/admin/reservations", { page: 1, limit: 5, status: "PENDING" });

  const stats = statsQuery.data;
  const loading = statsQuery.loading;

  const refresh = () => {
    void Promise.all([statsQuery.refetch(), customersQuery.refetch(), reservationsQuery.refetch()]);
  };

  const quickStats = [
    { label: "Total customers", value: (stats?.totalCustomers ?? 0).toLocaleString("en-IN"), icon: Users },
    { label: "Active reservations", value: (stats?.activeReservations ?? 0).toLocaleString("en-IN"), icon: ShoppingBag },
    { label: "Total reservations", value: (stats?.totalReservations ?? 0).toLocaleString("en-IN"), icon: MapPin },
    {
      label: "Customer growth",
      value: `${(stats?.customersGrowth ?? 0) > 0 ? "+" : ""}${stats?.customersGrowth ?? 0}%`,
      icon: Search,
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <p className="text-xs font-semibold uppercase tracking-[0.18em] text-emerald-500">Customer App</p>
          <h2 className="mt-2 text-2xl font-extrabold text-gray-900">Customer dashboard</h2>
        </div>
        <button
          type="button"
          onClick={refresh}
          className="inline-flex items-center gap-2 rounded-lg border border-gray-200 bg-white px-3 py-2 text-sm font-semibold text-gray-600 hover:bg-gray-50"
        >
          <RefreshCw className={`h-4 w-4 ${loading ? "animate-spin" : ""}`} /> Refresh
        </button>
      </div>

      {statsQuery.error && (
        <div role="alert" className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {statsQuery.error}
        </div>
      )}

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        {quickStats.map(({ label, value, icon: Icon }) => (
          <div key={label} className="rounded-2xl border border-emerald-100 bg-white p-5 shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs text-gray-500">{label}</p>
                <p className="mt-2 text-2xl font-extrabold text-gray-900">
                  {loading && !stats ? "..." : value}
                </p>
              </div>
              <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-emerald-50 text-emerald-600">
                <Icon className="h-5 w-5" />
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="grid gap-6 lg:grid-cols-[1.3fr_0.7fr]">
        <div className="rounded-2xl border border-gray-100 bg-white p-5 shadow-sm">
          <div className="mb-4 flex items-center justify-between">
            <h3 className="text-lg font-bold text-gray-900">Recent customers</h3>
            <Link to="/customers" className="inline-flex items-center gap-1 text-sm font-semibold text-emerald-600 hover:text-emerald-700">
              View all <ArrowRight className="h-4 w-4" />
            </Link>
          </div>
          <div className="divide-y divide-gray-100">
            {customersQuery.loading ? (
              <p className="py-8 text-center text-sm text-gray-500">Loading customers...</p>
            ) : (customersQuery.data?.users ?? []).length === 0 ? (
              <p className="py-8 text-center text-sm text-gray-500">No customers yet.</p>
            ) : (customersQuery.data?.users ?? []).map((u: any) => (
              <div key={u._id} className="flex items-center justify-between gap-3 py-3 first:pt-1 last:pb-1">
                <div className="flex items-center gap-3">
                  <div className="flex h-8 w-8 items-center justify-center rounded-full bg-emerald-100 text-sm font-bold text-emerald-700">
                    {u.name?.[0] || "?"}
                  </div>
                  <div>
                    <p className="text-sm font-semibold text-gray-900">{u.name || "—"}</p>
                    <p className="text-xs text-gray-500">{u.phone || u.email || "—"}</p>
                  </div>
                </div>
                <span className="text-xs bg-blue-50 text-blue-600 border border-blue-100 px-2 py-0.5 rounded-full font-semibold">
                  {u.subscription?.plan || "FREE"}
                </span>
              </div>
            ))}
          </div>
        </div>

        <div className="rounded-2xl border border-gray-100 bg-white p-5 shadow-sm">
          <div className="mb-4 flex items-center justify-between">
            <h3 className="text-lg font-bold text-gray-900">Pending reservations</h3>
            <Link to="/reservations" className="inline-flex items-center gap-1 text-sm font-semibold text-emerald-600 hover:text-emerald-700">
              View all <ArrowRight className="h-4 w-4" />
            </Link>
          </div>
          <div className="divide-y divide-gray-100">
            {reservationsQuery.loading ? (
              <p className="py-8 text-center text-sm text-gray-500">Loading...</p>
            ) : (reservationsQuery.data?.reservations ?? []).length === 0 ? (
              <p className="py-8 text-center text-sm text-gray-500">No pending reservations.</p>
            ) : (reservationsQuery.data?.reservations ?? []).map((r: any) => (
              <div key={r._id} className="py-3 first:pt-1 last:pb-1">
                <p className="text-sm font-semibold text-gray-900">{r.productName}</p>
                <p className="text-xs text-gray-500">{r.customerName} · {r.storeName}</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
