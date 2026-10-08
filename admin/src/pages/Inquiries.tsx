import { useState } from "react";
import { RefreshCw } from "lucide-react";
import api from "../lib/api";
import { useApi } from "../hooks/useApi";

interface Inquiry {
  _id: string;
  type: string;
  name: string;
  business?: string;
  city?: string;
  phone?: string;
  email: string;
  subject?: string;
  message?: string;
  status: "NEW" | "READ" | "CLOSED";
  createdAt: string;
}

interface InquiryPage {
  inquiries: Inquiry[];
  total: number;
}

export default function Inquiries() {
  const [page, setPage] = useState(1);
  const [status, setStatus] = useState("");
  const [actionError, setActionError] = useState("");
  const limit = 20;
  const { data, loading, error, refetch } = useApi<InquiryPage>(
    "/admin/inquiries",
    { page, limit, status },
    [page, status]
  );

  const updateStatus = async (id: string, nextStatus: Inquiry["status"]) => {
    setActionError("");
    try {
      await api.patch(`/admin/inquiries/${id}/status`, { status: nextStatus });
      await refetch();
    } catch (err: any) {
      setActionError(err.response?.data?.error?.message || "Inquiry status could not be updated.");
    }
  };

  const totalPages = Math.max(1, Math.ceil((data?.total || 0) / limit));

  return (
    <div className="space-y-5">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h2 className="text-lg font-bold text-gray-900">Inquiries</h2>
          <p className="text-sm text-gray-500">{data?.total || 0} received messages</p>
        </div>
        <button onClick={refetch} className="flex items-center gap-2 text-sm text-gray-500 border border-gray-200 px-3 py-2 rounded-xl hover:text-brand-500">
          <RefreshCw className="w-4 h-4" /> Refresh
        </button>
      </div>

      <div className="flex items-center gap-3">
        <label htmlFor="inquiry-status" className="text-sm text-gray-600">Status</label>
        <select id="inquiry-status" value={status} onChange={(event) => { setPage(1); setStatus(event.target.value); }} className="border border-gray-200 rounded-lg px-3 py-2 text-sm">
          <option value="">All</option>
          <option value="NEW">New</option>
          <option value="READ">Read</option>
          <option value="CLOSED">Closed</option>
        </select>
      </div>

      {(error || actionError) && <p role="alert" className="text-sm text-red-600">{actionError || error}</p>}

      <div className="bg-white border border-gray-100 rounded-xl overflow-hidden">
        {loading ? <p className="p-8 text-center text-sm text-gray-500">Loading inquiries...</p> : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50 text-left text-xs text-gray-500">
                <tr>{["Received", "Contact", "Type", "Message", "Status"].map((item) => <th key={item} className="px-4 py-3 font-semibold">{item}</th>)}</tr>
              </thead>
              <tbody>
                {(data?.inquiries || []).map((inquiry) => (
                  <tr key={inquiry._id} className="border-t border-gray-100 align-top">
                    <td className="px-4 py-3 text-xs text-gray-500 whitespace-nowrap">{new Date(inquiry.createdAt).toLocaleString("en-IN")}</td>
                    <td className="px-4 py-3 min-w-48">
                      <p className="text-sm font-semibold text-gray-800">{inquiry.name}</p>
                      <a className="text-xs text-brand-600" href={`mailto:${inquiry.email}`}>{inquiry.email}</a>
                      {inquiry.phone && <p className="text-xs text-gray-500">{inquiry.phone}</p>}
                      {(inquiry.business || inquiry.city) && <p className="text-xs text-gray-500">{[inquiry.business, inquiry.city].filter(Boolean).join(" · ")}</p>}
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-600">{inquiry.type}</td>
                    <td className="px-4 py-3 min-w-64">
                      {inquiry.subject && <p className="text-sm font-medium text-gray-800">{inquiry.subject}</p>}
                      <p className="text-sm text-gray-600 whitespace-pre-wrap">{inquiry.message || "—"}</p>
                    </td>
                    <td className="px-4 py-3">
                      <select aria-label={`Status for ${inquiry.name}`} value={inquiry.status} onChange={(event) => updateStatus(inquiry._id, event.target.value as Inquiry["status"])} className="border border-gray-200 rounded-lg px-2 py-1.5 text-xs">
                        <option value="NEW">New</option>
                        <option value="READ">Read</option>
                        <option value="CLOSED">Closed</option>
                      </select>
                    </td>
                  </tr>
                ))}
                {!data?.inquiries?.length && <tr><td colSpan={5} className="px-4 py-10 text-center text-sm text-gray-400">No inquiries found</td></tr>}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <div className="flex items-center justify-between text-sm text-gray-500">
        <span>Page {page} of {totalPages}</span>
        <div className="flex gap-2">
          <button disabled={page <= 1} onClick={() => setPage(page - 1)} className="border border-gray-200 rounded-lg px-3 py-1.5 disabled:opacity-40">Previous</button>
          <button disabled={page >= totalPages} onClick={() => setPage(page + 1)} className="border border-gray-200 rounded-lg px-3 py-1.5 disabled:opacity-40">Next</button>
        </div>
      </div>
    </div>
  );
}
