import { useState } from "react";
import { Eye, RefreshCw, ShieldOff, ShieldCheck } from "lucide-react";
import api from "../lib/api";
import { useApi } from "../hooks/useApi";
import Badge from "../components/Badge";
import SearchBar from "../components/SearchBar";
import Pagination from "../components/Pagination";
import Modal from "../components/Modal";

export default function Customers() {
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(1);
  const [selected, setSelected] = useState<any>(null);
  const [blockTarget, setBlockTarget] = useState<any>(null);
  const [blockReason, setBlockReason] = useState("");
  const [blocking, setBlocking] = useState(false);
  const [actionError, setActionError] = useState("");
  const PER_PAGE = 10;

  const { data, loading, refetch } = useApi<any>("/admin/users", { page, limit: PER_PAGE, search }, [page, search]);

  const users = data?.users || [];
  const total = data?.total || 0;

  const handleBlock = async (user: any, isBlocked: boolean) => {
    setBlocking(true);
    setActionError("");
    try {
      await api.patch(`/admin/users/${user._id}/account`, { isBlocked, blockReason });
      setBlockTarget(null);
      setBlockReason("");
      refetch();
    } catch (e: any) {
      setActionError(e.response?.data?.error?.message || "Could not update account.");
    } finally {
      setBlocking(false);
    }
  };

  return (
    <div className="space-y-5">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h2 className="text-lg font-bold text-gray-900">Customers</h2>
          <p className="text-sm text-gray-500">{total} registered customers</p>
        </div>
        <button onClick={refetch} className="flex items-center gap-2 text-sm text-gray-500 hover:text-brand-500 border border-gray-200 px-3 py-2 rounded-xl transition-colors">
          <RefreshCw className="w-4 h-4" /> Refresh
        </button>
      </div>

      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-4 flex justify-end">
        <SearchBar value={search} onChange={(v) => { setSearch(v); setPage(1); }} placeholder="Search customers..." />
      </div>

      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-sm text-gray-400">Loading customers...</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="bg-gray-50 border-b border-gray-100">
                  {["Customer", "Phone", "Email", "Subscription", "Status", "Joined", "Actions"].map((h) => (
                    <th key={h} className="text-left text-xs font-semibold text-gray-500 px-4 py-3 whitespace-nowrap">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {users.length === 0 ? (
                  <tr><td colSpan={7} className="px-4 py-10 text-center text-sm text-gray-400">No customers found</td></tr>
                ) : users.map((u: any) => (
                  <tr key={u._id} className="border-t border-gray-50 hover:bg-gray-50/50 transition-colors">
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-2.5">
                        <div className="w-8 h-8 bg-gradient-to-br from-brand-100 to-brand-200 rounded-full flex items-center justify-center text-brand-700 text-sm font-bold flex-shrink-0">
                          {u.name?.[0] || "?"}
                        </div>
                        <span className="text-sm font-semibold text-gray-800 whitespace-nowrap">{u.name || "—"}</span>
                      </div>
                    </td>
                    <td className="px-4 py-3 text-xs text-gray-600 whitespace-nowrap">{u.phone}</td>
                    <td className="px-4 py-3 text-xs text-gray-500">{u.email || "—"}</td>
                    <td className="px-4 py-3">
                      <span className="text-xs bg-blue-50 text-blue-600 border border-blue-100 px-2 py-0.5 rounded-full font-semibold">
                        {u.subscription?.plan || "FREE"}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-xs text-gray-500">{new Date(u.createdAt).toLocaleDateString("en-IN")}</td>
                    <td className="px-4 py-3">
                      {u.isBlocked
                        ? <span className="text-xs bg-red-50 text-red-600 border border-red-100 px-2 py-0.5 rounded-full font-semibold">Blocked</span>
                        : <span className="text-xs bg-green-50 text-green-600 border border-green-100 px-2 py-0.5 rounded-full font-semibold">Active</span>
                      }
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-1">
                        <button onClick={() => setSelected(u)} className="p-1.5 hover:bg-blue-50 rounded-lg transition-colors">
                          <Eye className="w-4 h-4 text-blue-500" />
                        </button>
                        {u.isBlocked ? (
                          <button onClick={() => handleBlock(u, false)} title="Unblock" className="p-1.5 hover:bg-green-50 rounded-lg transition-colors">
                            <ShieldCheck className="w-4 h-4 text-green-500" />
                          </button>
                        ) : (
                          <button onClick={() => { setBlockTarget(u); setBlockReason(""); }} title="Block" className="p-1.5 hover:bg-red-50 rounded-lg transition-colors">
                            <ShieldOff className="w-4 h-4 text-red-400" />
                          </button>
                        )}
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

      <Modal open={!!selected} onClose={() => setSelected(null)} title="Customer Details" size="lg">
        {selected && (
          <div className="space-y-4">
            <div className="flex items-center gap-3 p-4 bg-gray-50 rounded-xl">
              <div className="w-12 h-12 bg-gradient-to-br from-brand-100 to-brand-200 rounded-full flex items-center justify-center text-brand-700 text-xl font-bold">
                {selected.name?.[0] || "?"}
              </div>
              <div>
                <p className="font-bold text-gray-900">{selected.name || "—"}</p>
                <p className="text-sm text-gray-500">{selected.email || "No email"}</p>
              </div>
            </div>
            <div className="grid grid-cols-2 gap-3">
              {[
                { label: "Phone", value: selected.phone },
                { label: "Email", value: selected.email || "—" },
                { label: "Subscription", value: selected.subscription?.plan || "FREE" },
                { label: "Joined", value: new Date(selected.createdAt).toLocaleDateString("en-IN") },
              ].map((i) => (
                <div key={i.label} className="bg-gray-50 rounded-xl p-3">
                  <p className="text-xs text-gray-400 mb-0.5">{i.label}</p>
                  <p className="text-sm font-semibold text-gray-800">{i.value}</p>
                </div>
              ))}
            </div>
            {selected.reservations?.length > 0 && (
              <div>
                <p className="text-xs font-semibold text-gray-500 mb-2">RECENT RESERVATIONS</p>
                <div className="space-y-2">
                  {selected.reservations.slice(0, 3).map((r: any) => (
                    <div key={r._id} className="flex items-center justify-between bg-gray-50 rounded-xl p-3">
                      <div>
                        <p className="text-xs font-semibold text-gray-800">{r.productName}</p>
                        <p className="text-xs text-gray-400">{r.storeName}</p>
                      </div>
                      <Badge status={r.status} />
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </Modal>

      {actionError && (
        <div role="alert" className="fixed bottom-4 right-4 bg-red-50 border border-red-200 text-red-700 text-sm px-4 py-3 rounded-xl shadow-lg z-50">
          {actionError}
        </div>
      )}

      <Modal open={!!blockTarget} onClose={() => setBlockTarget(null)} title="Block Customer Account" size="sm">
        <div className="space-y-4">
          <p className="text-sm text-gray-600">The customer will not be able to log in while blocked.</p>
          <div>
            <label className="block text-xs font-semibold text-gray-600 mb-1.5">Reason (optional)</label>
            <textarea
              value={blockReason}
              onChange={(e) => setBlockReason(e.target.value)}
              rows={3}
              maxLength={500}
              className="w-full px-4 py-3 border border-gray-200 rounded-xl text-sm focus:outline-none focus:border-red-400"
            />
          </div>
          <div className="flex gap-3">
            <button type="button" onClick={() => setBlockTarget(null)} className="flex-1 py-3 border border-gray-200 rounded-xl text-sm font-semibold text-gray-600 hover:bg-gray-50">Cancel</button>
            <button type="button" disabled={blocking} onClick={() => blockTarget && handleBlock(blockTarget, true)} className="flex-1 py-3 bg-red-600 text-white rounded-xl text-sm font-semibold hover:bg-red-700 disabled:opacity-60">
              {blocking ? "Blocking..." : "Block Account"}
            </button>
          </div>
        </div>
      </Modal>
    </div>
  );
}
