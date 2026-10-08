import { useState } from "react";
import { Plus, Pencil, Trash2, RefreshCw } from "lucide-react";
import api from "../lib/api";
import { useApi } from "../hooks/useApi";
import Badge from "../components/Badge";
import Modal from "../components/Modal";

export default function Categories() {
  const { data: categories, loading, refetch } = useApi<any[]>("/admin/categories");
  const [showAdd, setShowAdd] = useState(false);
  const [editing, setEditing] = useState<any>(null);
  const [form, setForm] = useState({ name: "", description: "", icon: "🏷️" });
  const [saving, setSaving] = useState(false);

  const handleAdd = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      await api.post("/admin/categories", { name: form.name, description: form.description });
      setForm({ name: "", description: "", icon: "🏷️" });
      setShowAdd(false);
      refetch();
    } finally { setSaving(false); }
  };

  const handleEdit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      await api.patch(`/admin/categories/${editing._id}`, { name: form.name, description: form.description });
      setEditing(null);
      refetch();
    } finally { setSaving(false); }
  };

  const toggleStatus = async (id: string, current: boolean) => {
    await api.patch(`/admin/categories/${id}`, { isActive: !current });
    refetch();
  };

  const deleteCategory = async (id: string) => {
    if (!confirm("Delete this category?")) return;
    await api.delete(`/admin/categories/${id}`);
    refetch();
  };

  const openEdit = (c: any) => {
    setForm({ name: c.name, description: c.description || "", icon: "🏷️" });
    setEditing(c);
  };

  return (
    <div className="space-y-5">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h2 className="text-lg font-bold text-gray-900">Categories</h2>
          <p className="text-sm text-gray-500">{categories?.length || 0} categories</p>
        </div>
        <div className="flex gap-2">
          <button onClick={refetch} className="flex items-center gap-2 text-sm text-gray-500 hover:text-brand-500 border border-gray-200 px-3 py-2 rounded-xl transition-colors">
            <RefreshCw className="w-4 h-4" />
          </button>
          <button onClick={() => { setForm({ name: "", description: "", icon: "🏷️" }); setShowAdd(true); }} className="flex items-center gap-2 bg-brand-500 text-white px-4 py-2.5 rounded-xl text-sm font-semibold hover:bg-brand-600 transition-colors shadow-sm">
            <Plus className="w-4 h-4" /> Add Category
          </button>
        </div>
      </div>

      {loading ? (
        <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {Array(6).fill(0).map((_, i) => <div key={i} className="h-40 bg-gray-100 rounded-2xl animate-pulse" />)}
        </div>
      ) : (
        <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {(categories || []).map((c: any) => (
            <div key={c._id} className="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 hover:shadow-md transition-shadow">
              <div className="flex items-start justify-between mb-3">
                <div className="flex items-center gap-3">
                  <div className="w-12 h-12 bg-brand-50 rounded-xl flex items-center justify-center text-2xl">🏷️</div>
                  <div>
                    <p className="font-bold text-gray-900">{c.name}</p>
                    {c.description && <p className="text-xs text-gray-400 mt-0.5 line-clamp-1">{c.description}</p>}
                  </div>
                </div>
                <Badge status={c.isActive ? "active" : "inactive"} />
              </div>
              <div className="grid grid-cols-2 gap-2 mb-4">
                <div className="bg-gray-50 rounded-xl p-2.5 text-center">
                  <p className="text-lg font-extrabold text-gray-900">{c.storeCount ?? 0}</p>
                  <p className="text-xs text-gray-400">Stores</p>
                </div>
                <div className="bg-gray-50 rounded-xl p-2.5 text-center">
                  <p className="text-lg font-extrabold text-gray-900">{new Date(c.createdAt).toLocaleDateString("en-IN", { month: "short", year: "numeric" })}</p>
                  <p className="text-xs text-gray-400">Created</p>
                </div>
              </div>
              <div className="flex items-center gap-2">
                <button onClick={() => toggleStatus(c._id, c.isActive)} className={`flex-1 py-2 rounded-xl text-xs font-semibold transition-colors ${c.isActive ? "bg-amber-50 text-amber-600 hover:bg-amber-100" : "bg-green-50 text-green-600 hover:bg-green-100"}`}>
                  {c.isActive ? "Deactivate" : "Activate"}
                </button>
                <button onClick={() => openEdit(c)} className="p-2 hover:bg-blue-50 rounded-xl transition-colors">
                  <Pencil className="w-4 h-4 text-blue-500" />
                </button>
                <button onClick={() => deleteCategory(c._id)} className="p-2 hover:bg-red-50 rounded-xl transition-colors">
                  <Trash2 className="w-4 h-4 text-red-400" />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Add Modal */}
      <Modal open={showAdd} onClose={() => setShowAdd(false)} title="Add New Category" size="sm">
        <form onSubmit={handleAdd} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-gray-600 mb-1.5">Category Name *</label>
            <input required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} placeholder="e.g. Electronics" className="w-full px-4 py-3 border border-gray-200 rounded-xl text-sm focus:outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100" />
          </div>
          <div>
            <label className="block text-xs font-semibold text-gray-600 mb-1.5">Description</label>
            <input value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} placeholder="Optional description" className="w-full px-4 py-3 border border-gray-200 rounded-xl text-sm focus:outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100" />
          </div>
          <div className="flex gap-3 pt-2">
            <button type="button" onClick={() => setShowAdd(false)} className="flex-1 py-3 border border-gray-200 rounded-xl text-sm font-semibold text-gray-600 hover:bg-gray-50">Cancel</button>
            <button type="submit" disabled={saving} className="flex-1 py-3 bg-brand-500 text-white rounded-xl text-sm font-semibold hover:bg-brand-600 disabled:opacity-60">
              {saving ? "Saving..." : "Add Category"}
            </button>
          </div>
        </form>
      </Modal>

      {/* Edit Modal */}
      <Modal open={!!editing} onClose={() => setEditing(null)} title="Edit Category" size="sm">
        <form onSubmit={handleEdit} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-gray-600 mb-1.5">Category Name *</label>
            <input required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} className="w-full px-4 py-3 border border-gray-200 rounded-xl text-sm focus:outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100" />
          </div>
          <div>
            <label className="block text-xs font-semibold text-gray-600 mb-1.5">Description</label>
            <input value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} className="w-full px-4 py-3 border border-gray-200 rounded-xl text-sm focus:outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100" />
          </div>
          <div className="flex gap-3 pt-2">
            <button type="button" onClick={() => setEditing(null)} className="flex-1 py-3 border border-gray-200 rounded-xl text-sm font-semibold text-gray-600 hover:bg-gray-50">Cancel</button>
            <button type="submit" disabled={saving} className="flex-1 py-3 bg-brand-500 text-white rounded-xl text-sm font-semibold hover:bg-brand-600 disabled:opacity-60">
              {saving ? "Saving..." : "Save Changes"}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
