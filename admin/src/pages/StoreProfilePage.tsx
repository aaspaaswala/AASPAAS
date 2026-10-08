import { useState, type FormEvent, type InputHTMLAttributes } from "react";
import { ArrowLeft, Ban, Check, Clock3, Edit3, PackagePlus, RefreshCw, ShieldCheck, Store as StoreIcon, Trash2 } from "lucide-react";
import { Link, useParams } from "react-router-dom";
import Modal from "../components/Modal";
import Pagination from "../components/Pagination";
import { useApi } from "../hooks/useApi";
import api from "../lib/api";

type RetailerProfile = {
  _id: string;
  ownerName: string;
  businessName: string;
  phone: string;
  email?: string;
  category?: string;
  verificationStatus: "PENDING" | "VERIFIED" | "REJECTED";
  isBlocked: boolean;
  blockedAt?: string;
  blockReason?: string;
  createdAt: string;
};

type StoreProfile = {
  _id: string;
  name: string;
  description?: string;
  address: string;
  phone?: string;
  openingHours: string;
  images: string[];
  categories: string[];
  location: { coordinates: [number, number] };
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
  productCount: number;
  reservationCount: number;
  retailer: RetailerProfile;
};

type ProductVariant = {
  _id: string;
  size: string;
  color: string;
  price: number;
  inventory: {
    _id: string;
    totalStock: number;
    availableStock: number;
    reservedStock: number;
    soldStock: number;
    lowStockThreshold: number;
  };
};

type StoreProduct = {
  _id: string;
  name: string;
  brand?: string;
  description?: string;
  category: string;
  isActive: boolean;
  variants: ProductVariant[];
};

type ReservationActivity = {
  _id: string;
  reservationCode: string;
  customerName: string;
  customerMobile: string;
  productName: string;
  variantDescription: string;
  quantity: number;
  price: number;
  status: string;
  createdAt: string;
  expiresAt: string;
};

type ActivityPage = { reservations: ReservationActivity[]; total: number; pages: number };
type ProfileDraft = {
  ownerName: string;
  businessName: string;
  ownerPhone: string;
  email: string;
  category: string;
  verificationStatus: RetailerProfile["verificationStatus"];
  storeName: string;
  storePhone: string;
  address: string;
  description: string;
  openingHours: string;
  categories: string;
  imageUrl: string;
  latitude: string;
  longitude: string;
};
type ProductDraft = {
  name: string;
  brand: string;
  description: string;
  category: string;
  variants: Array<{ _id?: string; size: string; color: string; price: number; stock: number }>;
};

const inputClass = "mt-1 w-full rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm text-gray-900 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100";
const tabClass = (active: boolean) => `border-b-2 px-1 py-3 text-sm font-semibold ${active ? "border-blue-600 text-blue-700" : "border-transparent text-gray-500 hover:text-gray-800"}`;
const money = new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR", maximumFractionDigits: 2 });

function TextField({ label, ...props }: { label: string } & InputHTMLAttributes<HTMLInputElement>) {
  return <label className="block text-sm font-medium text-gray-700">{label}<input className={inputClass} {...props} /></label>;
}

function formatDate(value?: string) {
  return value ? new Date(value).toLocaleString("en-IN", { dateStyle: "medium", timeStyle: "short" }) : "-";
}

export default function StoreProfilePage() {
  const { storeId = "" } = useParams();
  const [tab, setTab] = useState<"overview" | "products" | "activity">("overview");
  const [editingProfile, setEditingProfile] = useState(false);
  const [profileDraft, setProfileDraft] = useState<ProfileDraft | null>(null);
  const [productDraft, setProductDraft] = useState<ProductDraft | null>(null);
  const [editingProductId, setEditingProductId] = useState<string | null>(null);
  const [blockDialogOpen, setBlockDialogOpen] = useState(false);
  const [blockReason, setBlockReason] = useState("");
  const [activityPage, setActivityPage] = useState(1);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const profileQuery = useApi<StoreProfile>(`/admin/stores/${storeId}`, undefined, [storeId]);
  const productsQuery = useApi<StoreProduct[]>(`/admin/stores/${storeId}/products`, undefined, [storeId]);
  const activityQuery = useApi<ActivityPage>(`/admin/stores/${storeId}/activity`, { page: activityPage, limit: 10 }, [storeId, activityPage]);
  const profile = profileQuery.data;
  const products = productsQuery.data ?? [];
  const reservations = activityQuery.data?.reservations ?? [];
  const pageError = error || profileQuery.error || productsQuery.error || activityQuery.error;

  const beginProfileEdit = () => {
    if (!profile) return;
    const [longitude, latitude] = profile.location?.coordinates ?? [0, 0];
    setProfileDraft({
      ownerName: profile.retailer.ownerName,
      businessName: profile.retailer.businessName,
      ownerPhone: profile.retailer.phone,
      email: profile.retailer.email ?? "",
      category: profile.retailer.category ?? "",
      verificationStatus: profile.retailer.verificationStatus,
      storeName: profile.name,
      storePhone: profile.phone ?? "",
      address: profile.address,
      description: profile.description ?? "",
      openingHours: profile.openingHours ?? "",
      categories: profile.categories.join(", "),
      imageUrl: profile.images?.[0] ?? "",
      latitude: String(latitude ?? ""),
      longitude: String(longitude ?? ""),
    });
    setEditingProfile(true);
  };

  const saveProfile = async (event: FormEvent) => {
    event.preventDefault();
    if (!profileDraft) return;
    setBusy(true);
    setError(null);
    try {
      const store: Record<string, unknown> = {
        name: profileDraft.storeName,
        phone: profileDraft.storePhone,
        address: profileDraft.address,
        description: profileDraft.description,
        openingHours: profileDraft.openingHours,
        categories: profileDraft.categories.split(",").map((category) => category.trim()).filter(Boolean),
        imageUrl: profileDraft.imageUrl,
      };
      if (profileDraft.latitude.trim()) store.latitude = Number(profileDraft.latitude);
      if (profileDraft.longitude.trim()) store.longitude = Number(profileDraft.longitude);
      await api.put(`/admin/stores/${storeId}/profile`, {
        retailer: {
          ownerName: profileDraft.ownerName,
          businessName: profileDraft.businessName,
          phone: profileDraft.ownerPhone,
          email: profileDraft.email,
          category: profileDraft.category,
          verificationStatus: profileDraft.verificationStatus,
        },
        store,
      });
      setEditingProfile(false);
      await profileQuery.refetch();
    } catch (requestError: any) {
      setError(requestError.response?.data?.error?.message || "Could not save the business profile.");
    } finally {
      setBusy(false);
    }
  };

  const toggleAccount = async (isBlocked: boolean, reason = "") => {
    setBusy(true);
    setError(null);
    try {
      await api.patch(`/admin/stores/${storeId}/account`, { isBlocked, blockReason: reason });
      setBlockDialogOpen(false);
      setBlockReason("");
      await profileQuery.refetch();
    } catch (requestError: any) {
      setError(requestError.response?.data?.error?.message || "Could not update the business account.");
    } finally {
      setBusy(false);
    }
  };

  const toggleStoreStatus = async () => {
    setBusy(true);
    setError(null);
    try {
      await api.patch(`/admin/stores/${storeId}/toggle`);
      await profileQuery.refetch();
    } catch (requestError: any) {
      setError(requestError.response?.data?.error?.message || "Could not update store availability.");
    } finally {
      setBusy(false);
    }
  };

  const openProductEditor = (product?: StoreProduct) => {
    setEditingProductId(product?._id ?? null);
    setProductDraft(product ? {
      name: product.name,
      brand: product.brand ?? "",
      description: product.description ?? "",
      category: product.category,
      variants: product.variants.map((variant) => ({
        _id: variant._id,
        size: variant.size,
        color: variant.color,
        price: variant.price,
        stock: variant.inventory.totalStock,
      })),
    } : { name: "", brand: "", description: "", category: "", variants: [{ size: "", color: "", price: 0, stock: 0 }] });
  };

  const saveProduct = async (event: FormEvent) => {
    event.preventDefault();
    if (!productDraft) return;
    setBusy(true);
    setError(null);
    const payload = {
      ...productDraft,
      variants: productDraft.variants.map((variant) => ({ ...variant, price: Number(variant.price), stock: Number(variant.stock) })),
    };
    try {
      if (editingProductId) await api.put(`/admin/stores/${storeId}/products/${editingProductId}`, payload);
      else await api.post(`/admin/stores/${storeId}/products`, payload);
      setProductDraft(null);
      setEditingProductId(null);
      await Promise.all([productsQuery.refetch(), profileQuery.refetch()]);
    } catch (requestError: any) {
      setError(requestError.response?.data?.error?.message || "Could not save this product.");
    } finally {
      setBusy(false);
    }
  };

  const deleteProduct = async (product: StoreProduct) => {
    if (!window.confirm(`Delete ${product.name} from this business?`)) return;
    setBusy(true);
    setError(null);
    try {
      await api.delete(`/admin/stores/${storeId}/products/${product._id}`);
      await Promise.all([productsQuery.refetch(), profileQuery.refetch()]);
    } catch (requestError: any) {
      setError(requestError.response?.data?.error?.message || "Could not delete this product.");
    } finally {
      setBusy(false);
    }
  };

  if (profileQuery.loading && !profile) return <div className="p-8 text-center text-sm text-gray-500">Loading business profile...</div>;
  if (!profile) return <div role="alert" className="rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-700">{pageError || "Business profile not found."}</div>;

  const tabs = [
    { id: "overview" as const, label: "Overview" },
    { id: "products" as const, label: `Products (${profile.productCount})` },
    { id: "activity" as const, label: `Activity (${profile.reservationCount})` },
  ];

  return (
    <div className="mx-auto max-w-7xl space-y-5">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-3">
          <Link to="/stores" aria-label="Back to stores" title="Back to stores" className="rounded-lg border border-gray-200 bg-white p-2 text-gray-600 hover:bg-gray-50">
            <ArrowLeft className="h-4 w-4" />
          </Link>
          <div>
            <p className="text-xs font-semibold uppercase tracking-wide text-blue-600">Business profile</p>
            <h2 className="mt-1 text-xl font-bold text-gray-900">{profile.name}</h2>
          </div>
        </div>
        <button type="button" onClick={() => { void Promise.all([profileQuery.refetch(), productsQuery.refetch(), activityQuery.refetch()]); }} className="inline-flex items-center gap-2 rounded-lg border border-gray-200 bg-white px-3 py-2 text-sm font-semibold text-gray-600 hover:bg-gray-50">
          <RefreshCw className="h-4 w-4" /> Refresh
        </button>
      </div>

      {pageError && <div role="alert" className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{pageError}</div>}

      <section className="flex flex-wrap items-center justify-between gap-4 rounded-lg border border-gray-200 bg-white p-5 shadow-sm">
        <div className="flex min-w-0 items-center gap-4">
          <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-lg bg-blue-50 text-blue-700"><StoreIcon className="h-6 w-6" /></div>
          <div className="min-w-0">
            <h3 className="truncate text-lg font-bold text-gray-900">{profile.retailer.businessName}</h3>
            <p className="mt-1 truncate text-sm text-gray-500">{profile.retailer.ownerName} · {profile.address}</p>
            <div className="mt-2 flex flex-wrap gap-2">
              <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${profile.isActive ? "bg-emerald-50 text-emerald-700" : "bg-gray-100 text-gray-600"}`}>Store {profile.isActive ? "Active" : "Inactive"}</span>
              <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${profile.retailer.isBlocked ? "bg-red-50 text-red-700" : "bg-blue-50 text-blue-700"}`}>Account {profile.retailer.isBlocked ? "Blocked" : "Enabled"}</span>
              <span className="rounded-full bg-gray-100 px-2.5 py-1 text-xs font-semibold text-gray-700">{profile.retailer.verificationStatus}</span>
            </div>
          </div>
        </div>
        <div className="flex flex-wrap gap-2">
          <button type="button" onClick={beginProfileEdit} className="inline-flex items-center gap-2 rounded-lg border border-gray-300 px-3 py-2 text-sm font-semibold text-gray-700 hover:bg-gray-50"><Edit3 className="h-4 w-4" /> Edit profile</button>
          <button type="button" disabled={busy} onClick={() => void toggleStoreStatus()} className="rounded-lg border border-amber-300 px-3 py-2 text-sm font-semibold text-amber-800 hover:bg-amber-50 disabled:opacity-50">{profile.isActive ? "Deactivate store" : "Activate store"}</button>
          {profile.retailer.isBlocked ? (
            <button type="button" disabled={busy} onClick={() => void toggleAccount(false)} className="inline-flex items-center gap-2 rounded-lg bg-emerald-700 px-3 py-2 text-sm font-semibold text-white hover:bg-emerald-800 disabled:opacity-50"><ShieldCheck className="h-4 w-4" /> Unblock account</button>
          ) : (
            <button type="button" disabled={busy} onClick={() => setBlockDialogOpen(true)} className="inline-flex items-center gap-2 rounded-lg bg-red-700 px-3 py-2 text-sm font-semibold text-white hover:bg-red-800 disabled:opacity-50"><Ban className="h-4 w-4" /> Block account</button>
          )}
        </div>
      </section>

      <nav aria-label="Business profile sections" className="flex gap-6 border-b border-gray-200">
        {tabs.map((item) => <button key={item.id} type="button" onClick={() => setTab(item.id)} className={tabClass(tab === item.id)}>{item.label}</button>)}
      </nav>

      {tab === "overview" && (
        <div className="grid gap-5 lg:grid-cols-[1fr_0.8fr]">
          <section className="rounded-lg border border-gray-200 bg-white p-5 shadow-sm">
            <div className="mb-4 flex items-center justify-between"><h3 className="font-bold text-gray-900">Owner and store details</h3><button type="button" onClick={beginProfileEdit} className="text-sm font-semibold text-blue-700 hover:text-blue-800">Edit details</button></div>
            <dl className="grid gap-x-6 gap-y-4 sm:grid-cols-2">
              {[
                ["Business name", profile.retailer.businessName], ["Owner", profile.retailer.ownerName],
                ["Account phone", profile.retailer.phone], ["Store phone", profile.phone || "-"],
                ["Email", profile.retailer.email || "-"], ["Category", profile.retailer.category || profile.categories.join(", ") || "-"],
                ["Address", profile.address], ["Opening hours", profile.openingHours || "-"],
                ["Verification", profile.retailer.verificationStatus], ["Registered", formatDate(profile.retailer.createdAt)],
              ].map(([label, value]) => <div key={label}><dt className="text-xs font-medium text-gray-500">{label}</dt><dd className="mt-1 break-words text-sm font-semibold text-gray-900">{value}</dd></div>)}
            </dl>
            {profile.description && <div className="mt-5 border-t border-gray-100 pt-4"><p className="text-xs font-medium text-gray-500">Description</p><p className="mt-1 text-sm text-gray-800">{profile.description}</p></div>}
            {profile.retailer.isBlocked && <div className="mt-5 rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-800"><p className="font-semibold">Account blocked {formatDate(profile.retailer.blockedAt)}</p>{profile.retailer.blockReason && <p className="mt-1">Reason: {profile.retailer.blockReason}</p>}</div>}
          </section>
          <section className="rounded-lg border border-gray-200 bg-white p-5 shadow-sm">
            <h3 className="font-bold text-gray-900">Business activity summary</h3>
            <div className="mt-4 grid grid-cols-2 gap-3">
              {[["Products", profile.productCount], ["Reservations", profile.reservationCount], ["Store status", profile.isActive ? "Active" : "Inactive"], ["Account", profile.retailer.isBlocked ? "Blocked" : "Enabled"]].map(([label, value]) => (
                <div key={String(label)} className="rounded-lg border border-gray-100 bg-gray-50 p-4"><p className="text-xs text-gray-500">{label}</p><p className="mt-1 text-lg font-bold text-gray-900">{value}</p></div>
              ))}
            </div>
            <button type="button" onClick={() => setTab("activity")} className="mt-4 inline-flex items-center gap-2 text-sm font-semibold text-blue-700 hover:text-blue-800"><Clock3 className="h-4 w-4" /> Review complete reservation activity</button>
          </section>
        </div>
      )}

      {tab === "products" && (
        <section className="overflow-hidden rounded-lg border border-gray-200 bg-white shadow-sm">
          <div className="flex flex-wrap items-center justify-between gap-3 border-b border-gray-100 p-5">
            <div><h3 className="font-bold text-gray-900">Products and inventory</h3><p className="mt-1 text-sm text-gray-500">Changes here apply to this business's product inventory.</p></div>
            <button type="button" onClick={() => openProductEditor()} className="inline-flex items-center gap-2 rounded-lg bg-blue-700 px-3 py-2 text-sm font-semibold text-white hover:bg-blue-800"><PackagePlus className="h-4 w-4" /> Add product</button>
          </div>
          {productsQuery.loading && products.length === 0 ? <p className="p-8 text-center text-sm text-gray-500">Loading products...</p> : products.length === 0 ? <p className="p-8 text-center text-sm text-gray-500">No products for this business yet.</p> : (
            <div className="divide-y divide-gray-100">
              {products.map((product) => (
                <article key={product._id} className="p-5">
                  <div className="flex flex-wrap items-start justify-between gap-3">
                    <div><h4 className="font-semibold text-gray-900">{product.name}</h4><p className="mt-1 text-xs text-gray-500">{[product.brand, product.category].filter(Boolean).join(" · ") || "Uncategorized"} · {product.isActive ? "Catalog active" : "Catalog inactive"}</p>{product.description && <p className="mt-2 text-sm text-gray-600">{product.description}</p>}</div>
                    <div className="flex gap-2">
                      <button type="button" disabled={busy} onClick={() => openProductEditor(product)} className="rounded-lg border border-gray-300 px-3 py-1.5 text-xs font-semibold text-gray-700 hover:bg-gray-50 disabled:opacity-50">Edit</button>
                      <button type="button" disabled={busy} onClick={() => void deleteProduct(product)} aria-label={`Delete ${product.name}`} title="Delete product" className="rounded-lg border border-red-200 p-1.5 text-red-700 hover:bg-red-50 disabled:opacity-50"><Trash2 className="h-4 w-4" /></button>
                    </div>
                  </div>
                  <div className="mt-4 overflow-x-auto">
                    <table className="w-full min-w-[520px] text-left text-sm">
                      <thead><tr className="text-xs text-gray-500"><th className="pb-2 font-medium">Variant</th><th className="pb-2 font-medium">Price</th><th className="pb-2 font-medium">In stock</th><th className="pb-2 font-medium">Reserved</th><th className="pb-2 font-medium">Sold</th></tr></thead>
                      <tbody>{product.variants.map((variant) => <tr key={variant._id} className="border-t border-gray-100"><td className="py-2.5 text-gray-800">{[variant.size, variant.color].filter(Boolean).join(" / ") || "Standard"}</td><td className="py-2.5 font-medium text-gray-900">{money.format(variant.price)}</td><td className="py-2.5 text-gray-700">{variant.inventory.availableStock} / {variant.inventory.totalStock}</td><td className="py-2.5 text-gray-700">{variant.inventory.reservedStock}</td><td className="py-2.5 text-gray-700">{variant.inventory.soldStock}</td></tr>)}</tbody>
                    </table>
                  </div>
                </article>
              ))}
            </div>
          )}
        </section>
      )}

      {tab === "activity" && (
        <section className="overflow-hidden rounded-lg border border-gray-200 bg-white shadow-sm">
          <div className="border-b border-gray-100 p-5"><h3 className="font-bold text-gray-900">Reservation activity</h3><p className="mt-1 text-sm text-gray-500">Complete reservation history for this store, newest first.</p></div>
          {activityQuery.loading && reservations.length === 0 ? <p className="p-8 text-center text-sm text-gray-500">Loading activity...</p> : reservations.length === 0 ? <p className="p-8 text-center text-sm text-gray-500">No reservation activity yet.</p> : (
            <div className="overflow-x-auto">
              <table className="w-full min-w-[900px] text-left">
                <thead className="bg-gray-50"><tr>{["Reservation", "Customer", "Product", "Qty", "Amount", "Status", "Created", "Expires"].map((heading) => <th key={heading} className="px-4 py-3 text-xs font-semibold text-gray-500">{heading}</th>)}</tr></thead>
                <tbody>{reservations.map((reservation) => <tr key={reservation._id} className="border-t border-gray-100 align-top">
                  <td className="px-4 py-3 text-sm font-semibold text-gray-900">{reservation.reservationCode}</td>
                  <td className="px-4 py-3"><p className="text-sm font-medium text-gray-800">{reservation.customerName}</p><p className="mt-1 text-xs text-gray-500">{reservation.customerMobile}</p></td>
                  <td className="px-4 py-3"><p className="text-sm text-gray-800">{reservation.productName}</p><p className="mt-1 text-xs text-gray-500">{reservation.variantDescription || "Standard"}</p></td>
                  <td className="px-4 py-3 text-sm text-gray-700">{reservation.quantity}</td>
                  <td className="px-4 py-3 text-sm font-semibold text-gray-900">{money.format(reservation.price)}</td>
                  <td className="px-4 py-3"><span className="rounded-full bg-gray-100 px-2 py-1 text-xs font-semibold text-gray-700">{reservation.status}</span></td>
                  <td className="px-4 py-3 text-xs text-gray-600">{formatDate(reservation.createdAt)}</td>
                  <td className="px-4 py-3 text-xs text-gray-600">{formatDate(reservation.expiresAt)}</td>
                </tr>)}</tbody>
              </table>
            </div>
          )}
          <Pagination page={activityPage} total={activityQuery.data?.total ?? 0} perPage={10} onChange={setActivityPage} />
        </section>
      )}

      <Modal open={editingProfile} onClose={() => setEditingProfile(false)} title="Edit business profile" size="lg">
        {profileDraft && <form onSubmit={(event) => void saveProfile(event)} className="space-y-5">
          <div><h4 className="mb-3 text-sm font-bold text-gray-900">Owner account</h4><div className="grid gap-4 sm:grid-cols-2">
            <TextField label="Owner name" required value={profileDraft.ownerName} onChange={(event) => setProfileDraft({ ...profileDraft, ownerName: event.target.value })} />
            <TextField label="Business legal name" required value={profileDraft.businessName} onChange={(event) => setProfileDraft({ ...profileDraft, businessName: event.target.value })} />
            <TextField label="Login phone" required value={profileDraft.ownerPhone} onChange={(event) => setProfileDraft({ ...profileDraft, ownerPhone: event.target.value })} />
            <TextField label="Email" type="email" value={profileDraft.email} onChange={(event) => setProfileDraft({ ...profileDraft, email: event.target.value })} />
            <TextField label="Business category" value={profileDraft.category} onChange={(event) => setProfileDraft({ ...profileDraft, category: event.target.value })} />
            <label className="block text-sm font-medium text-gray-700">Verification status<select className={inputClass} value={profileDraft.verificationStatus} onChange={(event) => setProfileDraft({ ...profileDraft, verificationStatus: event.target.value as ProfileDraft["verificationStatus"] })}><option value="PENDING">Pending</option><option value="VERIFIED">Verified</option><option value="REJECTED">Rejected</option></select></label>
          </div></div>
          <div><h4 className="mb-3 text-sm font-bold text-gray-900">Store profile</h4><div className="grid gap-4 sm:grid-cols-2">
            <TextField label="Store name" required value={profileDraft.storeName} onChange={(event) => setProfileDraft({ ...profileDraft, storeName: event.target.value })} />
            <TextField label="Store contact phone" value={profileDraft.storePhone} onChange={(event) => setProfileDraft({ ...profileDraft, storePhone: event.target.value })} />
            <TextField label="Address" required value={profileDraft.address} onChange={(event) => setProfileDraft({ ...profileDraft, address: event.target.value })} />
            <TextField label="Opening hours" value={profileDraft.openingHours} onChange={(event) => setProfileDraft({ ...profileDraft, openingHours: event.target.value })} />
            <TextField label="Categories, comma separated" value={profileDraft.categories} onChange={(event) => setProfileDraft({ ...profileDraft, categories: event.target.value })} />
            <TextField label="Store image URL" type="url" value={profileDraft.imageUrl} onChange={(event) => setProfileDraft({ ...profileDraft, imageUrl: event.target.value })} />
            <TextField label="Latitude" type="number" step="any" value={profileDraft.latitude} onChange={(event) => setProfileDraft({ ...profileDraft, latitude: event.target.value })} />
            <TextField label="Longitude" type="number" step="any" value={profileDraft.longitude} onChange={(event) => setProfileDraft({ ...profileDraft, longitude: event.target.value })} />
            <label className="block text-sm font-medium text-gray-700 sm:col-span-2">Description<textarea className={inputClass} rows={3} value={profileDraft.description} onChange={(event) => setProfileDraft({ ...profileDraft, description: event.target.value })} /></label>
          </div></div>
          <div className="flex justify-end gap-2 border-t border-gray-100 pt-4"><button type="button" onClick={() => setEditingProfile(false)} className="rounded-lg border border-gray-300 px-4 py-2 text-sm font-semibold text-gray-700">Cancel</button><button type="submit" disabled={busy} className="inline-flex items-center gap-2 rounded-lg bg-blue-700 px-4 py-2 text-sm font-semibold text-white disabled:opacity-50"><Check className="h-4 w-4" /> Save profile</button></div>
        </form>}
      </Modal>

      <Modal open={blockDialogOpen} onClose={() => setBlockDialogOpen(false)} title="Block business account" size="md">
        <div className="space-y-4"><p className="text-sm text-gray-600">The owner will immediately lose access to business APIs and cannot sign in while this account is blocked.</p><label className="block text-sm font-medium text-gray-700">Reason (optional)<textarea className={inputClass} rows={3} maxLength={500} value={blockReason} onChange={(event) => setBlockReason(event.target.value)} /></label><div className="flex justify-end gap-2"><button type="button" onClick={() => setBlockDialogOpen(false)} className="rounded-lg border border-gray-300 px-4 py-2 text-sm font-semibold text-gray-700">Cancel</button><button type="button" disabled={busy} onClick={() => void toggleAccount(true, blockReason)} className="rounded-lg bg-red-700 px-4 py-2 text-sm font-semibold text-white disabled:opacity-50">Block account</button></div></div>
      </Modal>

      <Modal open={!!productDraft} onClose={() => setProductDraft(null)} title={editingProductId ? "Edit product" : "Add product"} size="lg">
        {productDraft && <form onSubmit={(event) => void saveProduct(event)} className="space-y-4">
          <div className="grid gap-4 sm:grid-cols-2">
            <TextField label="Product name" required value={productDraft.name} onChange={(event) => setProductDraft({ ...productDraft, name: event.target.value })} />
            <TextField label="Category" required value={productDraft.category} onChange={(event) => setProductDraft({ ...productDraft, category: event.target.value })} />
            <TextField label="Brand" value={productDraft.brand} onChange={(event) => setProductDraft({ ...productDraft, brand: event.target.value })} />
            <label className="block text-sm font-medium text-gray-700 sm:col-span-2">Description<textarea className={inputClass} rows={2} value={productDraft.description} onChange={(event) => setProductDraft({ ...productDraft, description: event.target.value })} /></label>
          </div>
          <div className="space-y-3"><div className="flex items-center justify-between"><h4 className="text-sm font-bold text-gray-900">Variants and stock</h4><button type="button" onClick={() => setProductDraft({ ...productDraft, variants: [...productDraft.variants, { size: "", color: "", price: 0, stock: 0 }] })} className="text-sm font-semibold text-blue-700">Add variant</button></div>
            {productDraft.variants.map((variant, index) => <div key={variant._id ?? index} className="grid items-end gap-3 rounded-lg border border-gray-200 p-3 sm:grid-cols-[1fr_1fr_1fr_1fr_auto]">
              <TextField label="Size" value={variant.size} onChange={(event) => setProductDraft({ ...productDraft, variants: productDraft.variants.map((item, itemIndex) => itemIndex === index ? { ...item, size: event.target.value } : item) })} />
              <TextField label="Color" value={variant.color} onChange={(event) => setProductDraft({ ...productDraft, variants: productDraft.variants.map((item, itemIndex) => itemIndex === index ? { ...item, color: event.target.value } : item) })} />
              <TextField label="Price (INR)" type="number" min="0" step="0.01" required value={variant.price} onChange={(event) => setProductDraft({ ...productDraft, variants: productDraft.variants.map((item, itemIndex) => itemIndex === index ? { ...item, price: Number(event.target.value) } : item) })} />
              <TextField label="Total stock" type="number" min="0" step="1" required value={variant.stock} onChange={(event) => setProductDraft({ ...productDraft, variants: productDraft.variants.map((item, itemIndex) => itemIndex === index ? { ...item, stock: Number(event.target.value) } : item) })} />
              <button type="button" disabled={productDraft.variants.length === 1} onClick={() => setProductDraft({ ...productDraft, variants: productDraft.variants.filter((_, itemIndex) => itemIndex !== index) })} title="Remove variant" aria-label="Remove variant" className="mb-1 rounded-lg p-2 text-red-700 hover:bg-red-50 disabled:opacity-30"><Trash2 className="h-4 w-4" /></button>
            </div>)}
          </div>
          <div className="flex justify-end gap-2 border-t border-gray-100 pt-4"><button type="button" onClick={() => setProductDraft(null)} className="rounded-lg border border-gray-300 px-4 py-2 text-sm font-semibold text-gray-700">Cancel</button><button type="submit" disabled={busy} className="rounded-lg bg-blue-700 px-4 py-2 text-sm font-semibold text-white disabled:opacity-50">Save product</button></div>
        </form>}
      </Modal>
    </div>
  );
}