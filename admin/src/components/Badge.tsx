interface BadgeProps { status: string; }

const config: Record<string, string> = {
  active: "bg-green-50 text-green-700 border border-green-200",
  inactive: "bg-gray-100 text-gray-500 border border-gray-200",
  pending: "bg-amber-50 text-amber-700 border border-amber-200",
  out_of_stock: "bg-red-50 text-red-600 border border-red-200",
  PENDING: "bg-amber-50 text-amber-700 border border-amber-200",
  CONFIRMED: "bg-blue-50 text-blue-700 border border-blue-200",
  READY: "bg-purple-50 text-purple-700 border border-purple-200",
  COMPLETED: "bg-green-50 text-green-700 border border-green-200",
  EXPIRED: "bg-gray-100 text-gray-500 border border-gray-200",
  CANCELLED: "bg-red-50 text-red-600 border border-red-200",
  REJECTED: "bg-red-100 text-red-700 border border-red-300",
  NO_SHOW: "bg-orange-50 text-orange-700 border border-orange-200",
};

export default function Badge({ status }: BadgeProps) {
  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold ${config[status] || "bg-gray-100 text-gray-500"}`}>
      {status.replace("_", " ")}
    </span>
  );
}
