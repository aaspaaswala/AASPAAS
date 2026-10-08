import { useEffect, useState } from "react";
import { Save, Bell, Shield, Globe, Palette, CheckCircle } from "lucide-react";
import api from "../lib/api";

export default function Settings() {
  const [saved, setSaved] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [general, setGeneral] = useState({ siteName: "AasPaas Wala", supportEmail: "support@aaspaas.in", supportPhone: "+91 98765 43210", timezone: "Asia/Kolkata" });
  const [notif, setNotif] = useState({ newStore: true, newReservation: true, expiredReservation: false, newCustomer: true });
  const [security, setSecurity] = useState({ twoFactor: false, sessionTimeout: "24", loginAlerts: true });
  const [brandColor, setBrandColor] = useState("#f97316");

  useEffect(() => {
    api.get("/admin/settings")
      .then(({ data }) => {
        const settings = data.data;
        setGeneral(settings.general);
        setNotif(settings.notifications);
        setSecurity({ ...settings.security, sessionTimeout: String(settings.security.sessionTimeout) });
        setBrandColor(settings.appearance.brandColor);
      })
      .catch((err) => setError(err.response?.data?.error?.message || "Settings could not be loaded."))
      .finally(() => setLoading(false));
  }, []);

  const handleSave = async () => {
    setSaving(true);
    setError("");
    setSaved(false);
    try {
      await api.put("/admin/settings", {
        general,
        notifications: notif,
        security: { ...security, sessionTimeout: Number(security.sessionTimeout) },
        appearance: { brandColor },
      });
      setSaved(true);
    } catch (err: any) {
      setError(err.response?.data?.error?.message || "Settings could not be saved.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="space-y-6 max-w-3xl">
      <div>
        <h2 className="text-lg font-bold text-gray-900">Settings</h2>
        <p className="text-sm text-gray-500">Admin panel configuration</p>
      </div>

      {loading && <p className="text-sm text-gray-500">Loading settings...</p>}
      {error && <p role="alert" className="text-sm text-red-600">{error}</p>}

      {saved && (
        <div className="flex items-center gap-2 bg-green-50 border border-green-200 text-green-700 text-sm px-4 py-3 rounded-xl">
          <CheckCircle className="w-4 h-4" /> Settings saved successfully!
        </div>
      )}

      {/* General */}
      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-4 border-b border-gray-100">
          <div className="w-8 h-8 bg-brand-50 rounded-lg flex items-center justify-center">
            <Globe className="w-4 h-4 text-brand-500" />
          </div>
          <h3 className="font-bold text-gray-900">General Settings</h3>
        </div>
        <div className="p-5 space-y-4">
          <div className="grid sm:grid-cols-2 gap-4">
            {[
              { label: "Site Name", key: "siteName", placeholder: "AasPaas Wala" },
              { label: "Support Email", key: "supportEmail", placeholder: "support@aaspaas.in" },
              { label: "Support Phone", key: "supportPhone", placeholder: "+91 XXXXX XXXXX" },
              { label: "Timezone", key: "timezone", placeholder: "Asia/Kolkata" },
            ].map((f) => (
              <div key={f.key}>
                <label className="block text-xs font-semibold text-gray-600 mb-1.5">{f.label}</label>
                <input
                  value={general[f.key as keyof typeof general]}
                  onChange={(e) => setGeneral({ ...general, [f.key]: e.target.value })}
                  placeholder={f.placeholder}
                  className="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100"
                />
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Notifications */}
      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-4 border-b border-gray-100">
          <div className="w-8 h-8 bg-blue-50 rounded-lg flex items-center justify-center">
            <Bell className="w-4 h-4 text-blue-500" />
          </div>
          <h3 className="font-bold text-gray-900">Notification Preferences</h3>
        </div>
        <div className="p-5 space-y-3">
          {[
            { key: "newStore", label: "New Store Registration", desc: "Alert when a new store registers" },
            { key: "newReservation", label: "New Reservation", desc: "Alert on every new reservation" },
            { key: "expiredReservation", label: "Expired Reservations", desc: "Alert when reservations expire" },
            { key: "newCustomer", label: "New Customer Signup", desc: "Alert when a new customer joins" },
          ].map((item) => (
            <div key={item.key} className="flex items-center justify-between py-2">
              <div>
                <p className="text-sm font-semibold text-gray-800">{item.label}</p>
                <p className="text-xs text-gray-400">{item.desc}</p>
              </div>
              <button
                onClick={() => setNotif({ ...notif, [item.key]: !notif[item.key as keyof typeof notif] })}
                className={`relative w-11 h-6 rounded-full transition-colors ${notif[item.key as keyof typeof notif] ? "bg-brand-500" : "bg-gray-200"}`}
              >
                <span className={`absolute top-0.5 left-0.5 w-5 h-5 bg-white rounded-full shadow transition-transform ${notif[item.key as keyof typeof notif] ? "translate-x-5" : "translate-x-0"}`} />
              </button>
            </div>
          ))}
        </div>
      </div>

      {/* Security */}
      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-4 border-b border-gray-100">
          <div className="w-8 h-8 bg-green-50 rounded-lg flex items-center justify-center">
            <Shield className="w-4 h-4 text-green-500" />
          </div>
          <h3 className="font-bold text-gray-900">Security</h3>
        </div>
        <div className="p-5 space-y-4">
          <div className="flex items-center justify-between py-2">
            <div>
              <p className="text-sm font-semibold text-gray-800">Two-Factor Authentication</p>
              <p className="text-xs text-gray-400">Extra security for admin login</p>
            </div>
            <button
              onClick={() => setSecurity({ ...security, twoFactor: !security.twoFactor })}
              className={`relative w-11 h-6 rounded-full transition-colors ${security.twoFactor ? "bg-brand-500" : "bg-gray-200"}`}
            >
              <span className={`absolute top-0.5 left-0.5 w-5 h-5 bg-white rounded-full shadow transition-transform ${security.twoFactor ? "translate-x-5" : "translate-x-0"}`} />
            </button>
          </div>
          <div className="flex items-center justify-between py-2">
            <div>
              <p className="text-sm font-semibold text-gray-800">Login Alerts</p>
              <p className="text-xs text-gray-400">Email alert on new admin login</p>
            </div>
            <button
              onClick={() => setSecurity({ ...security, loginAlerts: !security.loginAlerts })}
              className={`relative w-11 h-6 rounded-full transition-colors ${security.loginAlerts ? "bg-brand-500" : "bg-gray-200"}`}
            >
              <span className={`absolute top-0.5 left-0.5 w-5 h-5 bg-white rounded-full shadow transition-transform ${security.loginAlerts ? "translate-x-5" : "translate-x-0"}`} />
            </button>
          </div>
          <div>
            <label className="block text-xs font-semibold text-gray-600 mb-1.5">Session Timeout (hours)</label>
            <select
              value={security.sessionTimeout}
              onChange={(e) => setSecurity({ ...security, sessionTimeout: e.target.value })}
              className="w-full px-4 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:border-brand-400 focus:ring-2 focus:ring-brand-100"
            >
              {["1", "6", "12", "24", "48"].map((h) => (
                <option key={h} value={h}>{h} hour{h !== "1" ? "s" : ""}</option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Appearance */}
      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-4 border-b border-gray-100">
          <div className="w-8 h-8 bg-purple-50 rounded-lg flex items-center justify-center">
            <Palette className="w-4 h-4 text-purple-500" />
          </div>
          <h3 className="font-bold text-gray-900">Appearance</h3>
        </div>
        <div className="p-5">
          <p className="text-xs font-semibold text-gray-600 mb-3">Brand Color</p>
          <div className="flex gap-3">
            {["#f97316", "#3b82f6", "#10b981", "#8b5cf6", "#ef4444"].map((color) => (
              <button key={color} type="button" aria-label={`Set brand color ${color}`} aria-pressed={brandColor === color} onClick={() => setBrandColor(color)} className={`w-8 h-8 rounded-full border-2 shadow-md hover:scale-110 transition-transform ${brandColor === color ? "border-gray-900" : "border-white"}`} style={{ background: color }} />
            ))}
          </div>
        </div>
      </div>

      <button
        onClick={handleSave}
        disabled={loading || saving}
        className="flex items-center gap-2 bg-brand-500 text-white px-6 py-3 rounded-xl font-semibold hover:bg-brand-600 transition-colors shadow-lg shadow-brand-200 disabled:opacity-60"
      >
        <Save className="w-4 h-4" /> {saving ? "Saving..." : "Save Settings"}
      </button>
    </div>
  );
}
