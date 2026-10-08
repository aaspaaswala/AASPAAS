import { useState } from "react";
import { Eye, RefreshCw, ShieldOff, ShieldCheck } from "lucide-react";
import api from "../lib/api";
import { useApi } from "../hooks/useApi";
import Badge from "../components/Badge";
import SearchBar from "../components/SearchBar";
import Pagination from "../components/Pagination";
import Modal from "../components/Modal";

export default function Businesses() {
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(1);
  const [selected, setSelected] = useState<any>(null);
  const [blockTarget, setBlockTarget] = useState<any>(null);
  const [blockReason, setBlockReason] = useState("");
  const [blocking, setBlocking] = useState(false);
  const [actionError, setActionError] = useState("");
  const PER_PAGE = 10;

  const { data, loading, refetch } = useApi<any>("/admin/businesses", { page, limit: PER_PAGE, search }, [page, search]);

  const businesses = data?.businesses || [];
  const total = data?.total || 0;

  const handleBlock = async (retailer: any, isBlocked: boolean) => {
    setBlocking(true);
    setActionError("");
    try {
      // Block via the store's account endpoint (retailer is linked to store)
      if (retailer.store?._id) {
        await api.patch(`/admin/stores/${retailer.store._id}/account`, { isBlocked, blockReason });
      }
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
          <h2 className="text-lg font-bold text-gray-900">Businesses</h2>
          <p className="text-sm text-gray-500">{total} registered businesses</p>
        </div>
        <button onClick={refetch} className="flex items-center gap-2 text-sm text-gray-500 hover:text-brand-500 border border-gray-200 px-3 py-2 rounded-xl transition-colors">
          <RefreshCw className="w-4 h-4" /> Refresh
        </button>
      </div>

      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-4 flex justify-end">
        <SearchBar value={search} onChange={(v) => { setSearch(v); setPage(1); }} placeholder="Search businesses..." />
      </div>

      {actionError && (
        <div role="alert" className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{actionError}</div>
      )}

      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-sm text-gray-400">Loading businesses...</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="bg-gray-50 border-b border-gray-100">
                  {["Business", "Owner", "Phone", "Verification", "Store", "Account", "Actions"].map((h) => (
                    <th key={h} className="text-left text-xs font-semibold text-gray-500 px-4 py-3 whitespace-nowrap">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {businesses.length === 0 ? (
                  <tr><td colSpan={7} className="px-4 py-10 text-center text-sm text-gray-400">No businesses found</td></tr>
                ) : businesses.map((b: any) => (
                  <tr key={b._id} className="border-t border-gray-50 hover:bg-gray-50/50 transition-colors">
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-2.5">
                        <div className="w-8 h-8 bg-blue-50 rounded-lg flex items-center justify-center text-blue-600 text-sm font-bold flex-shrink-0">
                          {b.businessName?.[0] || "B"}
                        </div>
                        <span className="text-sm font-semibold text-gray-800 whitespace-nowrap">{b.businessName}</span>
                      </div>
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-600 whitespace-nowrap">{b.ownerName}</td>
                    <td className="px-4 py-3 text-xs text-gray-500 whitespace-nowrap">{b.phone}</td>
                    <td className="px-4 py-3">
                      <span className={`text-xs px-2 py-0.5 rounded-full font-semibold border ${
                        b.verificationStatus === "VERIFIED"
                          ? "bg-green-50 text-green-600 border-green-100"
                          : b.verificationStatus === "REJECTED"
                          ? "bg-red-50 text-red-600 border-red-100"
                          : "bg-amber-50 text-amber-600 border-amber-100"
                      }`}>
                        {b.verificationStatus}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      {b.store ? (
                        <Badge status={b.store.isActive ? "active" : "inactive"} />
                      ) : (
                        <span className="text-xs text-gray-400">No store</span>
                      )}
                    </td>
                    <td className="px-4 py-3">
                      {b.isBlocked
                        ? <span className="text-xs bg-red-50 text-red-600 border border-red-100 px-2 py-0.5 rounded-full font-semibold">Blocked</span>
                        : <span className="text-xs bg-green-50 text-green-600 border border-green-100 px-2 py-0.5 rounded-full font-semibold">Active</span>
                      }
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-1">
                        <button onClick={() => setSelected(b)} className="p-1.5 hover:bg-blue-50 rounded-lg transition-colors">
                          <Eye className="w-4 h-4 text-blue-500" />
                        </button>
                        {b.isBlocked ? (
                          <button onClick={() => handleBlock(b, false)} title="Unblock" className="p-1.5 hover:bg-green-50 rounded-lg transition-colors">
                            <ShieldCheck className="w-4 h-4 text-green-500" />
                          </button>
                        ) : (
                          <button onClick={() => { setBlockTarget(b); setBlockReason(""); }} title="Block" className="p-1.5 hover:bg-red-50 rounded-lg transition-colors">
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

      <Modal open={!!selected} onClose={() => setSelected(null)} title="Business Details" size="lg">
        {selected && (
          <div className="space-y-4">
            <div className="flex items-center gap-3 p-4 bg-gray-50 rounded-xl">
              <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center text-blue-700 text-xl font-bold">
                {selected.businessName?.[0] || "B"}
              </div>
              <div>
                <p className="font-bold text-gray-900">{selected.businessName}</p>
                <p className="text-sm text-gray-500">{selected.ownerName}</p>
              </div>
            </div>
            <div className="grid grid-cols-2 gap-3">
              {[
                { label: "Phone", value: selected.phone },
                { label: "Email", value: selected.email || "—" },
                { label: "Category", value: selected.category || "—" },
                { label: "Verification", value: selected.verificationStatus },
                { label: "Account", value: selected.isBlocked ? "Blocked" : "Active" },
                { label: "Registered", value: new Date(selected.createdAt).toLocaleDateString("en-IN") },
              ].map((i) => (
                <div key={i.label} className="bg-gray-50 rounded-xl p-3">
                  <p className="text-xs text-gray-400 mb-0.5">{i.label}</p>
                  <p className="text-sm font-semibold text-gray-800">{i.value}</p>
                </div>
              ))}
            </div>
            {selected.blockReason && (
              <div className="bg-red-50 border border-red-100 rounded-xl p-3">
                <p className="text-xs font-semibold text-red-600 mb-1">Block Reason</p>
                <p className="text-sm text-red-700">{selected.blockReason}</p>
              </div>
            )}
          </div>
        )}
      </Modal>

      <Modal open={!!blockTarget} onClose={() => setBlockTarget(null)} title="Block Business Account" size="sm">
        <div className="space-y-4">
          <p className="text-sm text-gray-600">The business owner will immediately lose access to the business app.</p>
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
