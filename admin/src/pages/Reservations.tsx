import { useState } from "react";
import { Eye, Clock, RefreshCw } from "lucide-react";
import { useApi } from "../hooks/useApi";
import Badge from "../components/Badge";
import SearchBar from "../components/SearchBar";
import Pagination from "../components/Pagination";
import Modal from "../components/Modal";

const STATUSES = ["PENDING", "CONFIRMED", "READY", "COMPLETED", "EXPIRED", "CANCELLED", "REJECTED", "NO_SHOW"];

export default function Reservations() {
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState("");
  const [page, setPage] = useState(1);
  const [selected, setSelected] = useState<any>(null);
  const PER_PAGE = 10;

  const { data, loading, refetch } = useApi<any>("/admin/reservations", {
    page, limit: PER_PAGE, search, status: filter,
  }, [page, search, filter]);

  const reservations = data?.reservations || [];
  const total = data?.total || 0;

  return (
    <div className="space-y-5">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h2 className="text-lg font-bold text-gray-900">Reservations</h2>
          <p className="text-sm text-gray-500">{total} total reservations</p>
        </div>
        <button onClick={refetch} className="flex items-center gap-2 text-sm text-gray-500 hover:text-brand-500 border border-gray-200 px-3 py-2 rounded-xl transition-colors">
          <RefreshCw className="w-4 h-4" /> Refresh
        </button>
      </div>

      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-4">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div className="flex gap-2 flex-wrap">
            <button onClick={() => { setFilter(""); setPage(1); }} className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors ${filter === "" ? "bg-brand-500 text-white" : "bg-gray-100 text-gray-600 hover:bg-gray-200"}`}>All</button>
            {STATUSES.map((s) => (
              <button key={s} onClick={() => { setFilter(s); setPage(1); }} className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors ${filter === s ? "bg-brand-500 text-white" : "bg-gray-100 text-gray-600 hover:bg-gray-200"}`}>{s}</button>
            ))}
          </div>
          <SearchBar value={search} onChange={(v) => { setSearch(v); setPage(1); }} placeholder="Search reservations..." />
        </div>
      </div>

      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-sm text-gray-400">Loading reservations...</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="bg-gray-50 border-b border-gray-100">
                  {["Code", "Customer", "Product", "Store", "Price", "Status", "Expires", "Actions"].map((h) => (
                    <th key={h} className="text-left text-xs font-semibold text-gray-500 px-4 py-3 whitespace-nowrap">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {reservations.length === 0 ? (
                  <tr><td colSpan={8} className="px-4 py-10 text-center text-sm text-gray-400">No reservations found</td></tr>
                ) : reservations.map((r: any) => (
                  <tr key={r._id} className="border-t border-gray-50 hover:bg-gray-50/50 transition-colors">
                    <td className="px-4 py-3 text-xs font-mono font-bold text-brand-500">{r.reservationCode || r._id?.toString().slice(-8)}</td>
                    <td className="px-4 py-3">
                      <p className="text-sm font-semibold text-gray-800 whitespace-nowrap">{r.customerName}</p>
                      <p className="text-xs text-gray-400">{r.customerMobile}</p>
                    </td>
                    <td className="px-4 py-3 text-xs text-gray-600 max-w-[120px] truncate">{r.productName}</td>
                    <td className="px-4 py-3 text-xs text-gray-500 max-w-[100px] truncate">{r.storeName}</td>
                    <td className="px-4 py-3 text-sm font-bold text-gray-900">₹{r.price?.toLocaleString()}</td>
                    <td className="px-4 py-3"><Badge status={r.status} /></td>
                    <td className="px-4 py-3">
                      <span className="flex items-center gap-1 text-xs text-gray-400">
                        <Clock className="w-3 h-3" />
                        {new Date(r.expiresAt).toLocaleString("en-IN", { hour: "2-digit", minute: "2-digit", day: "2-digit", month: "short" })}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <button onClick={() => setSelected(r)} className="p-1.5 hover:bg-blue-50 rounded-lg transition-colors">
                        <Eye className="w-4 h-4 text-blue-500" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        <Pagination page={page} total={total} perPage={PER_PAGE} onChange={setPage} />
      </div>

      <Modal open={!!selected} onClose={() => setSelected(null)} title="Reservation Details">
        {selected && (
          <div className="space-y-4">
            <div className="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
              <div>
                <p className="text-xs text-gray-400">Reservation Code</p>
                <p className="font-mono font-bold text-brand-500 text-lg">{selected.reservationCode || selected._id}</p>
              </div>
              <Badge status={selected.status} />
            </div>
            <div className="grid grid-cols-2 gap-3">
              {[
                { label: "Customer", value: selected.customerName },
                { label: "Phone", value: selected.customerMobile },
                { label: "Product", value: selected.productName },
                { label: "Store", value: selected.storeName },
                { label: "Price", value: `₹${selected.price?.toLocaleString()}` },
                { label: "Quantity", value: selected.quantity },
                { label: "Created", value: new Date(selected.createdAt).toLocaleString("en-IN") },
                { label: "Expires", value: new Date(selected.expiresAt).toLocaleString("en-IN") },
              ].map((i) => (
                <div key={i.label} className="bg-gray-50 rounded-xl p-3">
                  <p className="text-xs text-gray-400 mb-0.5">{i.label}</p>
                  <p className="text-sm font-semibold text-gray-800">{i.value}</p>
                </div>
              ))}
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
}
