import { useState } from "react";
import { Eye, RefreshCw } from "lucide-react";
import { useApi } from "../hooks/useApi";
import Badge from "../components/Badge";
import SearchBar from "../components/SearchBar";
import Pagination from "../components/Pagination";
import Modal from "../components/Modal";

export default function Products() {
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(1);
  const [selected, setSelected] = useState<any>(null);
  const PER_PAGE = 10;

  const { data, loading, refetch } = useApi<any>("/admin/products", { page, limit: PER_PAGE, search }, [page, search]);

  const products = data?.products || [];
  const total = data?.total || 0;

  return (
    <div className="space-y-5">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h2 className="text-lg font-bold text-gray-900">Products</h2>
          <p className="text-sm text-gray-500">{total} total products</p>
        </div>
        <button onClick={refetch} className="flex items-center gap-2 text-sm text-gray-500 hover:text-brand-500 border border-gray-200 px-3 py-2 rounded-xl transition-colors">
          <RefreshCw className="w-4 h-4" /> Refresh
        </button>
      </div>

      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-4 flex justify-end">
        <SearchBar value={search} onChange={(v) => { setSearch(v); setPage(1); }} placeholder="Search products..." />
      </div>

      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-sm text-gray-400">Loading products...</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="bg-gray-50 border-b border-gray-100">
                  {["Product", "Category", "Price", "Stock", "Status", "Actions"].map((h) => (
                    <th key={h} className="text-left text-xs font-semibold text-gray-500 px-4 py-3 whitespace-nowrap">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {products.length === 0 ? (
                  <tr><td colSpan={6} className="px-4 py-10 text-center text-sm text-gray-400">No products found</td></tr>
                ) : products.map((p: any) => (
                  <tr key={p._id} className="border-t border-gray-50 hover:bg-gray-50/50 transition-colors">
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-2.5">
                        <div className="w-8 h-8 bg-gray-50 rounded-lg flex items-center justify-center text-base border border-gray-100">
                          {p.images?.[0] ? <img src={p.images[0]} className="w-full h-full object-cover rounded-lg" /> : "📦"}
                        </div>
                        <div>
                          <p className="text-sm font-semibold text-gray-800">{p.name}</p>
                          {p.brand && <p className="text-xs text-gray-400">{p.brand}</p>}
                        </div>
                      </div>
                    </td>
                    <td className="px-4 py-3 text-xs text-gray-500">{p.categoryName || "—"}</td>
                    <td className="px-4 py-3 text-sm font-bold text-gray-900">₹{p.price?.toLocaleString() || "—"}</td>
                    <td className="px-4 py-3">
                      <span className={`text-sm font-semibold ${!p.stock ? "text-red-500" : p.stock < 5 ? "text-amber-500" : "text-green-600"}`}>
                        {p.stock ?? "—"}
                      </span>
                    </td>
                    <td className="px-4 py-3"><Badge status={p.isActive ? (p.stock === 0 ? "out_of_stock" : "active") : "inactive"} /></td>
                    <td className="px-4 py-3">
                      <button onClick={() => setSelected(p)} className="p-1.5 hover:bg-blue-50 rounded-lg transition-colors">
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

      <Modal open={!!selected} onClose={() => setSelected(null)} title="Product Details">
        {selected && (
          <div className="space-y-4">
            <div className="flex items-center gap-3 p-4 bg-gray-50 rounded-xl">
              <div className="w-14 h-14 bg-white rounded-xl flex items-center justify-center text-3xl border border-gray-100">📦</div>
              <div>
                <p className="font-bold text-gray-900">{selected.name}</p>
                <p className="text-sm text-gray-500">{selected.categoryName || "—"}</p>
              </div>
              <div className="ml-auto"><Badge status={selected.isActive ? "active" : "inactive"} /></div>
            </div>
            {selected.description && <p className="text-sm text-gray-600 bg-gray-50 rounded-xl p-3">{selected.description}</p>}
            <div className="grid grid-cols-2 gap-3">
              {[
                { label: "Brand", value: selected.brand || "—" },
                { label: "Category", value: selected.categoryName || "—" },
                { label: "Price", value: `₹${selected.price?.toLocaleString() || "—"}` },
                { label: "Stock", value: selected.stock ?? "—" },
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
