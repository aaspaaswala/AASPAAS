export const mockStats = {
  totalStores: 524,
  totalCustomers: 10842,
  totalReservations: 3291,
  totalProducts: 48320,
  revenue: 2840000,
  activeReservations: 142,
  storesGrowth: 12,
  customersGrowth: 24,
  reservationsGrowth: 8,
  revenueGrowth: 18,
};

export const revenueData = [
  { month: "Aug", revenue: 180000, reservations: 210 },
  { month: "Sep", revenue: 220000, reservations: 260 },
  { month: "Oct", revenue: 195000, reservations: 230 },
  { month: "Nov", revenue: 310000, reservations: 380 },
  { month: "Dec", revenue: 420000, reservations: 510 },
  { month: "Jan", revenue: 380000, reservations: 460 },
];

export const categoryData = [
  { name: "Electronics", value: 35, color: "#f97316" },
  { name: "Clothing", value: 25, color: "#3b82f6" },
  { name: "Grocery", value: 20, color: "#10b981" },
  { name: "Footwear", value: 12, color: "#8b5cf6" },
  { name: "Others", value: 8, color: "#f59e0b" },
];

export const mockStores = [
  { _id: "1", name: "Sharma Electronics", owner: "Ramesh Sharma", city: "Delhi", category: "Electronics", status: "active", products: 142, reservations: 38, rating: 4.8, createdAt: "2024-03-15" },
  { _id: "2", name: "Gupta Fashion House", owner: "Suresh Gupta", city: "Mumbai", category: "Clothing", status: "active", products: 89, reservations: 21, rating: 4.5, createdAt: "2024-04-02" },
  { _id: "3", name: "Modi Grocery Store", owner: "Vijay Modi", city: "Jaipur", category: "Grocery", status: "active", products: 310, reservations: 67, rating: 4.7, createdAt: "2024-02-20" },
  { _id: "4", name: "Nike Exclusive", owner: "Priya Patel", city: "Bangalore", category: "Footwear", status: "active", products: 56, reservations: 29, rating: 4.9, createdAt: "2024-05-10" },
  { _id: "5", name: "Tech World", owner: "Amit Kumar", city: "Delhi", category: "Electronics", status: "inactive", products: 78, reservations: 12, rating: 4.2, createdAt: "2024-06-01" },
  { _id: "6", name: "Style Hub", owner: "Neha Singh", city: "Mumbai", category: "Clothing", status: "pending", products: 0, reservations: 0, rating: 0, createdAt: "2025-01-20" },
];

export const mockProducts = [
  { _id: "1", name: "iPhone 15 Pro", store: "Sharma Electronics", category: "Electronics", price: 134900, stock: 5, status: "active", image: "📱" },
  { _id: "2", name: "Samsung Galaxy S24", store: "Tech World", category: "Electronics", price: 79999, stock: 3, status: "active", image: "📱" },
  { _id: "3", name: "Nike Air Max 270", store: "Nike Exclusive", category: "Footwear", price: 12995, stock: 8, status: "active", image: "👟" },
  { _id: "4", name: "Levi's 511 Jeans", store: "Gupta Fashion House", category: "Clothing", price: 3999, stock: 15, status: "active", image: "👖" },
  { _id: "5", name: "Basmati Rice 5kg", store: "Modi Grocery Store", category: "Grocery", price: 450, stock: 50, status: "active", image: "🌾" },
  { _id: "6", name: "Sony WH-1000XM5", store: "Sharma Electronics", category: "Electronics", price: 29990, stock: 0, status: "out_of_stock", image: "🎧" },
];

export const mockReservations = [
  { _id: "RES-4821", customer: "Rahul Sharma", phone: "+91 98765 43210", product: "iPhone 15 Pro", store: "Sharma Electronics", price: 134900, status: "PENDING", createdAt: "2025-01-28T10:30:00Z", expiresAt: "2025-01-28T16:30:00Z" },
  { _id: "RES-4820", customer: "Priya Mehta", phone: "+91 87654 32109", product: "Nike Air Max 270", store: "Nike Exclusive", price: 12995, status: "CONFIRMED", createdAt: "2025-01-28T09:15:00Z", expiresAt: "2025-01-28T15:15:00Z" },
  { _id: "RES-4819", customer: "Amit Verma", phone: "+91 76543 21098", product: "Levi's 511 Jeans", store: "Gupta Fashion House", price: 3999, status: "COMPLETED", createdAt: "2025-01-27T14:00:00Z", expiresAt: "2025-01-27T20:00:00Z" },
  { _id: "RES-4818", customer: "Sunita Patel", phone: "+91 65432 10987", product: "Samsung Galaxy S24", store: "Tech World", price: 79999, status: "EXPIRED", createdAt: "2025-01-27T11:00:00Z", expiresAt: "2025-01-27T17:00:00Z" },
  { _id: "RES-4817", customer: "Vikram Singh", phone: "+91 54321 09876", product: "Sony WH-1000XM5", store: "Sharma Electronics", price: 29990, status: "CANCELLED", createdAt: "2025-01-27T08:00:00Z", expiresAt: "2025-01-27T14:00:00Z" },
  { _id: "RES-4816", customer: "Kavya Nair", phone: "+91 43210 98765", product: "Basmati Rice 5kg", store: "Modi Grocery Store", price: 450, status: "COMPLETED", createdAt: "2025-01-26T16:00:00Z", expiresAt: "2025-01-26T22:00:00Z" },
];

export const mockUsers = [
  { _id: "1", name: "Rahul Sharma", email: "rahul@example.com", phone: "+91 98765 43210", city: "Delhi", reservations: 12, status: "active", joinedAt: "2024-06-15" },
  { _id: "2", name: "Priya Mehta", email: "priya@example.com", phone: "+91 87654 32109", city: "Mumbai", reservations: 8, status: "active", joinedAt: "2024-07-20" },
  { _id: "3", name: "Amit Verma", email: "amit@example.com", phone: "+91 76543 21098", city: "Jaipur", reservations: 5, status: "active", joinedAt: "2024-08-10" },
  { _id: "4", name: "Sunita Patel", email: "sunita@example.com", phone: "+91 65432 10987", city: "Bangalore", reservations: 3, status: "inactive", joinedAt: "2024-09-05" },
  { _id: "5", name: "Vikram Singh", email: "vikram@example.com", phone: "+91 54321 09876", city: "Delhi", reservations: 19, status: "active", joinedAt: "2024-05-01" },
];

export const mockCategories = [
  { _id: "1", name: "Electronics", slug: "electronics", stores: 142, products: 18420, status: "active", icon: "📱" },
  { _id: "2", name: "Clothing", slug: "clothing", stores: 98, products: 12300, status: "active", icon: "👕" },
  { _id: "3", name: "Grocery", slug: "grocery", stores: 210, products: 9800, status: "active", icon: "🛒" },
  { _id: "4", name: "Footwear", slug: "footwear", stores: 74, products: 5200, status: "active", icon: "👟" },
  { _id: "5", name: "Furniture", slug: "furniture", stores: 32, products: 2100, status: "active", icon: "🪑" },
  { _id: "6", name: "Books", slug: "books", stores: 18, products: 500, status: "inactive", icon: "📚" },
];

export const recentActivity = [
  { type: "reservation", text: "New reservation RES-4821 by Rahul Sharma", time: "2 min ago", color: "bg-brand-500" },
  { type: "store", text: "Style Hub registered as new store", time: "15 min ago", color: "bg-blue-500" },
  { type: "user", text: "New customer Kavya Nair signed up", time: "32 min ago", color: "bg-green-500" },
  { type: "reservation", text: "RES-4818 expired — Samsung Galaxy S24", time: "1 hr ago", color: "bg-red-500" },
  { type: "store", text: "Tech World marked as inactive", time: "2 hr ago", color: "bg-amber-500" },
];
