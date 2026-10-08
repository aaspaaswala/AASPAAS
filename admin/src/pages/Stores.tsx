import { useState } from "react";
import { ExternalLink, MapPin, RefreshCw } from "lucide-react";
import { Link } from "react-router-dom";
import api from "../lib/api";
import { useApi } from "../hooks/useApi";
import Badge from "../components/Badge";
import SearchBar from "../components/SearchBar";
import Pagination from "../components/Pagination";

export default function Stores() {
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState("all");
  const [page, setPage] = useState(1);
  const PER_PAGE = 10;

  const { data, loading, refetch } = useApi<any>("/admin/stores", {
    page, limit: PER_PAGE, search, status: filter === "all" ? "" : filter,
  }, [page, search, filter]);

  const stores = data?.stores || [];
  const total = data?.total || 0;

  const toggleStatus = async (id: string) => {
    await api.patch(`/admin/stores/${id}/toggle`);
    refetch();
  };

  return (
    <div className="space-y-5">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h2 className="text-lg font-bold text-gray-900">Stores Management</h2>
          <p className="text-sm text-gray-500">{total} total stores</p>
        </div>
        <button onClick={refetch} className="flex items-center gap-2 text-sm text-gray-500 hover:text-brand-500 border border-gray-200 px-3 py-2 rounded-xl transition-colors">
          <RefreshCw className="w-4 h-4" /> Refresh
        </button>
      </div>

      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-4">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div className="flex gap-2 flex-wrap">
            {["all", "active", "inactive"].map((f) => (
              <button key={f} onClick={() => { setFilter(f); setPage(1); }} className={`px-3 py-1.5 rounded-lg text-xs font-semibold capitalize transition-colors ${filter === f ? "bg-brand-500 text-white" : "bg-gray-100 text-gray-600 hover:bg-gray-200"}`}>
                {f}
              </button>
            ))}
          </div>
          <SearchBar value={search} onChange={(v) => { setSearch(v); setPage(1); }} placeholder="Search stores..." />
        </div>
      </div>

      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-sm text-gray-400">Loading stores...</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="bg-gray-50 border-b border-gray-100">
                  {["Store", "Owner", "City", "Category", "Products", "Reservations", "Status", "Actions"].map((h) => (
                    <th key={h} className="text-left text-xs font-semibold text-gray-500 px-4 py-3 whitespace-nowrap">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {stores.length === 0 ? (
                  <tr><td colSpan={8} className="px-4 py-10 text-center text-sm text-gray-400">No stores found</td></tr>
                ) : stores.map((s: any) => (
                  <tr key={s._id} className="border-t border-gray-50 hover:bg-gray-50/50 transition-colors">
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-2.5">
                        <div className="w-8 h-8 bg-brand-50 rounded-lg flex items-center justify-center text-base">🏪</div>
                        <Link to={`/stores/${s._id}`} className="text-sm font-semibold text-gray-800 whitespace-nowrap hover:text-brand-600">{s.name}</Link>
                      </div>
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-600 whitespace-nowrap">{s.retailer?.ownerName || "—"}</td>
                    <td className="px-4 py-3">
                      <span className="flex items-center gap-1 text-xs text-gray-500"><MapPin className="w-3 h-3" />{s.address?.split(",").pop()?.trim() || "—"}</span>
                    </td>
                    <td className="px-4 py-3 text-xs text-gray-500">{s.categories?.[0] || "—"}</td>
                    <td className="px-4 py-3 text-sm font-semibold text-gray-800">{s.productCount ?? 0}</td>
                    <td className="px-4 py-3 text-sm font-semibold text-gray-800">{s.reservationCount ?? 0}</td>
                    <td className="px-4 py-3"><Badge status={s.isActive ? "active" : "inactive"} /></td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-1">
                        <Link to={`/stores/${s._id}`} aria-label={`Open ${s.name} profile`} title="Open business profile" className="p-1.5 hover:bg-blue-50 rounded-lg transition-colors">
                          <ExternalLink className="w-4 h-4 text-blue-500" />
                        </Link>
                        <button onClick={() => toggleStatus(s._id)} className="p-1.5 hover:bg-amber-50 rounded-lg transition-colors text-xs font-medium text-amber-600 whitespace-nowrap">
                          {s.isActive ? "Deactivate" : "Activate"}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        <Pagination page={page} total={total} perPage={PER_PAGE} onChange={setPage} />
      </div>

    </div>
  );
}
