import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor: attach JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('smartshelf_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor: handle 401
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('smartshelf_token');
      localStorage.removeItem('smartshelf_user');
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

// Auth Service
export const authService = {
  login: async (credentials) => {
    const res = await api.post('/auth/login', credentials);
    return res.data;
  },
  register: async (userData) => {
    const res = await api.post('/auth/register', userData);
    return res.data;
  },
};

// Category Service
export const categoryService = {
  getAllCategories: async () => {
    const res = await api.get('/categories');
    return res.data;
  },
  getActiveCategories: async () => {
    const res = await api.get('/categories/active');
    return res.data;
  },
  createCategory: async (category) => {
    const res = await api.post('/categories', category);
    return res.data;
  },
  updateCategory: async (id, category) => {
    const res = await api.put(`/categories/${id}`, category);
    return res.data;
  },
  deleteCategory: async (id) => {
    await api.delete(`/categories/${id}`);
  },
};

// Product Service
export const productService = {
  getAllProducts: async () => {
    try {
      const res = await api.get('/products/active');
      return res.data || [];
    } catch {
      const res = await api.get('/products', { params: { size: 1000 } });
      return res.data?.content || res.data || [];
    }
  },
  getProductsByCategory: async (categoryId) => {
    const res = await api.get('/products', { params: { categoryId, size: 1000 } });
    return res.data?.content || res.data || [];
  },
  getProducts: async (params) => {
    const res = await api.get('/products', { params });
    return res.data;
  },
  searchActiveProducts: async (search) => {
    const res = await api.get('/products/active', { params: { search } });
    return res.data;
  },
  getProductById: async (id) => {
    const res = await api.get(`/products/${id}`);
    return res.data;
  },
  getProductByBarcode: async (barcode) => {
    const res = await api.get(`/products/barcode/${barcode}`);
    return res.data;
  },
  getProductBySku: async (sku) => {
    const res = await api.get(`/products/sku/${sku}`);
    return res.data;
  },
  createProduct: async (product) => {
    const res = await api.post('/products', product);
    return res.data;
  },
  updateProduct: async (id, product) => {
    const res = await api.put(`/products/${id}`, product);
    return res.data;
  },
  deleteProduct: async (id) => {
    await api.delete(`/products/${id}`);
  },
  importCsv: async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    const res = await api.post('/products/import-csv', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data;
  },
};

// Inventory Service
export const inventoryService = {
  getAllInventory: async () => {
    const res = await api.get('/inventory');
    return res.data;
  },
  getLowStock: async () => {
    const res = await api.get('/inventory/low-stock');
    return res.data;
  },
  getExpiring: async (days = 30) => {
    const res = await api.get('/inventory/expiring', { params: { days } });
    return res.data;
  },
  adjustStock: async (adjustment) => {
    const res = await api.post('/inventory/adjust', adjustment);
    return res.data;
  },
  getStockTransactions: async (productId) => {
    const res = await api.get('/inventory/transactions', { params: { productId } });
    return res.data;
  },
};

// Customer Service
export const customerService = {
  getCustomers: async (params) => {
    const res = await api.get('/customers', { params });
    return res.data;
  },
  searchCustomers: async (query) => {
    const res = await api.get('/customers/search', { params: { query } });
    return res.data;
  },
  getCustomerById: async (id) => {
    const res = await api.get(`/customers/${id}`);
    return res.data;
  },
  getCustomerInsights: async (id) => {
    const res = await api.get(`/customers/${id}/insights`);
    return res.data;
  },
  getCustomerOrders: async (id) => {
    const res = await api.get(`/customers/${id}/orders`);
    return res.data;
  },
  getPendingCreditCustomers: async () => {
    const res = await api.get('/customers/pending-credit');
    return res.data;
  },
  createCustomer: async (customer) => {
    const res = await api.post('/customers', customer);
    return res.data;
  },
  updateCustomer: async (id, customer) => {
    const res = await api.put(`/customers/${id}`, customer);
    return res.data;
  },
  deleteCustomer: async (id) => {
    await api.delete(`/customers/${id}`);
  },
};

// Order & Billing Service
export const orderService = {
  getOrders: async (params) => {
    const res = await api.get('/orders', { params });
    return res.data;
  },
  getOrderById: async (id) => {
    const res = await api.get(`/orders/${id}`);
    return res.data;
  },
  getReceipt: async (id) => {
    const res = await api.get(`/orders/${id}/receipt`);
    return res.data;
  },
  createOrder: async (orderData) => {
    const res = await api.post('/orders', orderData);
    return res.data;
  },
};

// Credit Service
export const creditService = {
  getAllTransactions: async () => {
    const res = await api.get('/credits');
    return res.data;
  },
  getCustomerTransactions: async (customerId) => {
    const res = await api.get(`/credits/customer/${customerId}`);
    return res.data;
  },
  recordPayment: async (payment) => {
    const res = await api.post('/credits/payment', payment);
    return res.data;
  },
};

// Supplier Service
export const supplierService = {
  getAllSuppliers: async () => {
    const res = await api.get('/suppliers');
    return res.data;
  },
  getActiveSuppliers: async () => {
    const res = await api.get('/suppliers/active');
    return res.data;
  },
  createSupplier: async (supplier) => {
    const res = await api.post('/suppliers', supplier);
    return res.data;
  },
  updateSupplier: async (id, supplier) => {
    const res = await api.put(`/suppliers/${id}`, supplier);
    return res.data;
  },
  deleteSupplier: async (id) => {
    await api.delete(`/suppliers/${id}`);
  },
};

// Purchase Service
export const purchaseService = {
  getAllPurchases: async () => {
    const res = await api.get('/purchases');
    return res.data;
  },
  getPurchaseById: async (id) => {
    const res = await api.get(`/purchases/${id}`);
    return res.data;
  },
  createPurchase: async (purchase) => {
    const res = await api.post('/purchases', purchase);
    return res.data;
  },
  markAsReceived: async (id) => {
    const res = await api.put(`/purchases/${id}/receive`);
    return res.data;
  },
};

// Notification Service
export const notificationService = {
  getAll: async () => {
    const res = await api.get('/notifications');
    return res.data;
  },
  getUnread: async () => {
    const res = await api.get('/notifications/unread');
    return res.data;
  },
  getUnreadCount: async () => {
    const res = await api.get('/notifications/unread-count');
    return typeof res.data === 'object' && res.data !== null ? (res.data.unreadCount ?? res.data.count ?? 0) : res.data;
  },
  markAsRead: async (id) => {
    await api.put(`/notifications/${id}/read`);
  },
  markAllAsRead: async () => {
    await api.put('/notifications/read-all');
  },
};

// Analytics Service
export const analyticsService = {
  getDashboardMetrics: async () => {
    const res = await api.get('/analytics/dashboard');
    return res.data;
  },
  getTopProducts: async (limit = 10) => {
    const res = await api.get('/analytics/top-products', { params: { limit } });
    return res.data;
  },
  getCategorySales: async () => {
    const res = await api.get('/analytics/category-sales');
    return res.data;
  },
  getProfitSummary: async () => {
    const res = await api.get('/analytics/profit-summary');
    return res.data;
  },
};

// AI Intelligence Service
export const aiService = {
  getRecommendations: async (cartProductIds, customerId) => {
    const res = await api.post('/ai/recommendations', cartProductIds || [], {
      params: { customerId },
    });
    return res.data;
  },
  getDemandForecast: async () => {
    const res = await api.get('/ai/demand-forecast');
    return res.data;
  },
  getMlMetrics: async () => {
    const res = await api.get('/ai/ml-metrics');
    return res.data;
  },
  simulateDemand: async (data) => {
    const res = await api.post('/ai/simulate-demand', data);
    return res.data;
  },
  getSmartReorders: async () => {
    const res = await api.get('/ai/smart-reorder');
    return res.data;
  },
  getSmartDiscounts: async () => {
    const res = await api.get('/ai/smart-discounts');
    return res.data;
  },
  parseHandwrittenListText: async (text, apiKey, model) => {
    const formData = new FormData();
    formData.append('text', text);
    if (apiKey) formData.append('apiKey', apiKey);
    if (model) formData.append('model', model);
    const res = await api.post('/ai/handwritten-list', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data;
  },
  uploadHandwrittenListPhoto: async (file, apiKey, model) => {
    const formData = new FormData();
    formData.append('file', file);
    if (apiKey) formData.append('apiKey', apiKey);
    if (model) formData.append('model', model);
    const res = await api.post('/ai/handwritten-list', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data;
  },
};

// Helper function to trigger browser file downloads with JWT auth
const triggerDownload = async (url, fallbackFilename) => {
  try {
    const res = await api.get(url, { responseType: 'blob' });
    const blob = new Blob([res.data], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = fallbackFilename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(link.href);
  } catch (e) {
    console.error('Download error:', e);
    // Fallback direct open
    window.open(url, '_blank');
  }
};

// Reports Service
export const reportService = {
  downloadInventoryCsv: () => {
    triggerDownload('/reports/inventory/csv', `SmartShelf_Inventory_${new Date().toISOString().split('T')[0]}.csv`);
  },
  downloadSalesCsv: () => {
    triggerDownload('/reports/sales/csv', `SmartShelf_Sales_${new Date().toISOString().split('T')[0]}.csv`);
  },
  downloadCreditCsv: () => {
    triggerDownload('/reports/credit/csv', `SmartShelf_Credit_${new Date().toISOString().split('T')[0]}.csv`);
  },
};

// User Management Service
export const userService = {
  getAllUsers: async () => {
    const res = await api.get('/users');
    return res.data;
  },
  registerUser: async (data) => {
    const res = await api.post('/users', data);
    return res.data;
  },
  toggleUserStatus: async (id) => {
    const res = await api.put(`/users/${id}/toggle-status`);
    return res.data;
  },
  activateUser: async (id) => {
    const res = await api.put(`/users/${id}/toggle-status`);
    return res.data;
  },
  deactivateUser: async (id) => {
    const res = await api.put(`/users/${id}/toggle-status`);
    return res.data;
  },
};

// Payment Service
export const paymentService = {
  generateUpiQr: async (data) => {
    const res = await api.post('/payments/generate-upi-qr', data);
    return res.data;
  },
  verifyUpiPayment: async (data) => {
    const res = await api.post('/payments/verify-upi', data);
    return res.data;
  },
  getPaymentConfig: async () => {
    const res = await api.get('/payments/config');
    return res.data;
  },
};

export default api;
