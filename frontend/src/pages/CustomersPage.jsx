import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import {
  Users,
  Search,
  Plus,
  Phone,
  MapPin,
  Sparkles,
  Receipt,
  User,
  History,
  ArrowRight,
} from 'lucide-react';
import { customerService } from '../services/api';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { CustomerProfileModal } from '../components/customers/CustomerProfileModal';
import { ReceiptModal } from '../components/pos/ReceiptModal';
import { useToast } from '../context/ToastContext';

export const CustomersPage = () => {
  const [searchParams] = useSearchParams();
  const initialSearch = searchParams.get('search') || '';

  const { success, error } = useToast();
  const [customers, setCustomers] = useState([]);
  const [searchQuery, setSearchQuery] = useState(initialSearch);
  const [isLoading, setIsLoading] = useState(true);

  // Customer Form Modal
  const [isFormModalOpen, setIsFormModalOpen] = useState(false);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [formData, setFormData] = useState({ name: '', phone: '', email: '', address: '' });
  const [isSaving, setIsSaving] = useState(false);

  // Customer Profile & History Modal
  const [isProfileModalOpen, setIsProfileModalOpen] = useState(false);
  const [profileCustomer, setProfileCustomer] = useState(null);

  // Receipt Modal (for reprinting/viewing from history)
  const [isReceiptModalOpen, setIsReceiptModalOpen] = useState(false);
  const [selectedReceipt, setSelectedReceipt] = useState(null);

  // Insights Modal
  const [isInsightModalOpen, setIsInsightModalOpen] = useState(false);
  const [activeInsight, setActiveInsight] = useState(null);
  const [isLoadingInsight, setIsLoadingInsight] = useState(false);

  useEffect(() => {
    loadCustomers();
  }, []);

  const loadCustomers = async (query = searchQuery) => {
    try {
      setIsLoading(true);
      const res = await customerService.getCustomers({ search: query.trim() || undefined, size: 50 });
      setCustomers(res?.content || []);
    } catch (e) {
      console.error(e);
    } finally {
      setIsLoading(false);
    }
  };

  const handleOpenProfile = (customer) => {
    setProfileCustomer(customer);
    setIsProfileModalOpen(true);
  };

  const handleOpenForm = (customer) => {
    if (customer) {
      setSelectedCustomer(customer);
      setFormData({
        name: customer.name || '',
        phone: customer.phone || '',
        email: customer.email || '',
        address: customer.address || '',
      });
    } else {
      setSelectedCustomer(null);
      setFormData({ name: '', phone: '', email: '', address: '' });
    }
    setIsFormModalOpen(true);
  };

  const handleSubmitForm = async (e) => {
    e.preventDefault();
    try {
      setIsSaving(true);
      if (selectedCustomer) {
        await customerService.updateCustomer(selectedCustomer.id, formData);
        success('Customer details updated!');
      } else {
        await customerService.createCustomer(formData);
        success('New customer profile created!');
      }
      setIsFormModalOpen(false);
      loadCustomers();
    } catch (err) {
      error(err.response?.data?.message || 'Failed to save customer');
    } finally {
      setIsSaving(false);
    }
  };

  const handleViewInsights = async (customer) => {
    try {
      setIsLoadingInsight(true);
      setIsInsightModalOpen(true);
      const insight = await customerService.getCustomerInsights(customer.id);
      setActiveInsight(insight);
    } catch (e) {
      console.error(e);
      error('Failed to load customer profile insights');
    } finally {
      setIsLoadingInsight(false);
    }
  };

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      {/* Header Banner */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white tracking-tight flex items-center gap-2.5">
            <Users className="w-7 h-7 text-emerald-400" />
            Customer Directory & Profiles
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Click on any customer to view their complete profile, previous purchase order history, and credit ledger
          </p>
        </div>

        <Button
          variant="primary"
          leftIcon={<Plus className="w-4 h-4" />}
          onClick={() => handleOpenForm(null)}
        >
          Add Customer
        </Button>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl">
          <div className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Registered Accounts</div>
          <div className="text-2xl font-black text-white font-mono mt-1">{customers.length}</div>
          <div className="text-[10px] text-slate-500 mt-0.5">Active retail buyers</div>
        </div>

        <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl">
          <div className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Total Credit Outstanding</div>
          <div className="text-2xl font-black text-rose-400 font-mono mt-1">
            ₹
            {customers
              .reduce((acc, c) => acc + (c.creditBalance || 0), 0)
              .toFixed(2)}
          </div>
          <div className="text-[10px] text-slate-500 mt-0.5">Across all customer accounts</div>
        </div>

        <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl">
          <div className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Orders Completed</div>
          <div className="text-2xl font-black text-cyan-400 font-mono mt-1">
            {customers.reduce((acc, c) => acc + (c.totalOrders || 0), 0)}
          </div>
          <div className="text-[10px] text-slate-500 mt-0.5">Store transactions</div>
        </div>

        <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl">
          <div className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Loyalty Points Issued</div>
          <div className="text-2xl font-black text-emerald-400 font-mono mt-1">
            {customers.reduce((acc, c) => acc + (c.rewardPoints || 0), 0)} pts
          </div>
          <div className="text-[10px] text-slate-500 mt-0.5">Redeemable in billing</div>
        </div>
      </div>

      {/* Search Bar */}
      <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl flex items-center justify-between gap-4">
        <div className="relative w-full max-w-md">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search by customer name, phone, or address..."
            value={searchQuery}
            onChange={(e) => {
              setSearchQuery(e.target.value);
              loadCustomers(e.target.value);
            }}
            className="w-full pl-10 pr-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-xs text-white placeholder:text-slate-400 focus:outline-none focus:border-emerald-500"
          />
        </div>
        <div className="text-xs text-slate-400 font-medium">
          Showing {customers.length} customers
        </div>
      </div>

      {/* Customer Grid */}
      {isLoading ? (
        <LoadingSpinner message="Loading customer directory..." />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {customers.map((c) => (
            <div
              key={c.id}
              className="p-5 rounded-3xl bg-slate-900 border border-slate-800 hover:border-emerald-500/50 transition-all flex flex-col justify-between shadow-xl space-y-4 group cursor-pointer"
              onClick={() => handleOpenProfile(c)}
            >
              <div>
                <div className="flex items-start justify-between gap-2">
                  <div className="flex items-center gap-3">
                    <div className="w-12 h-12 rounded-2xl bg-emerald-500/10 border border-emerald-500/20 group-hover:border-emerald-500/40 text-emerald-400 font-extrabold text-base flex items-center justify-center transition-colors shadow-inner">
                      {c.name?.charAt(0) || 'C'}
                    </div>
                    <div>
                      <h3 className="text-sm font-bold text-white group-hover:text-emerald-400 transition-colors leading-tight">
                        {c.name}
                      </h3>
                      <div className="text-xs text-slate-400 font-mono flex items-center gap-1 mt-0.5">
                        <Phone className="w-3 h-3 text-slate-400" /> {c.phone}
                      </div>
                    </div>
                  </div>

                  {c.creditBalance > 0 ? (
                    <Badge variant="danger" size="sm">
                      Credit: ₹{c.creditBalance?.toFixed(2)}
                    </Badge>
                  ) : (
                    <Badge variant="success" size="sm">
                      Cleared
                    </Badge>
                  )}
                </div>

                {/* Meta details */}
                <div className="mt-4 pt-3 border-t border-slate-800/80 space-y-1.5 text-xs text-slate-400">
                  <div className="flex justify-between">
                    <span>Lifetime Purchases:</span>
                    <span className="font-mono font-semibold text-white">₹{c.totalPurchases?.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Orders Completed:</span>
                    <span className="font-mono text-slate-300">{c.totalOrders || 0} bills</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Reward Points:</span>
                    <span className="font-mono font-bold text-emerald-400">{c.rewardPoints || 0} pts</span>
                  </div>
                  {c.address && (
                    <div className="text-[11px] text-slate-400 flex items-start gap-1 pt-1 truncate">
                      <MapPin className="w-3 h-3 text-slate-500 shrink-0 mt-0.5" />
                      <span className="truncate">{c.address}</span>
                    </div>
                  )}
                </div>
              </div>

              {/* Action Buttons */}
              <div
                className="flex items-center justify-between gap-2 pt-2 border-t border-slate-800/80"
                onClick={(e) => e.stopPropagation()}
              >
                <button
                  type="button"
                  onClick={() => handleOpenProfile(c)}
                  className="inline-flex items-center gap-1 text-xs font-semibold text-emerald-400 hover:text-emerald-300 py-1"
                >
                  <History className="w-3.5 h-3.5" />
                  Profile & History
                  <ArrowRight className="w-3 h-3" />
                </button>

                <div className="flex items-center gap-1.5">
                  <Button size="sm" variant="ghost" onClick={() => handleOpenForm(c)}>
                    Edit
                  </Button>
                  <Button
                    size="sm"
                    variant="outline"
                    leftIcon={<Sparkles className="w-3.5 h-3.5 text-emerald-400" />}
                    onClick={() => handleViewInsights(c)}
                  >
                    AI
                  </Button>
                </div>
              </div>
            </div>
          ))}

          {customers.length === 0 && (
            <div className="col-span-full py-16 text-center text-slate-400 text-xs">
              No customers found. Click "Add Customer" to register your first regular buyer.
            </div>
          )}
        </div>
      )}

      {/* Customer Profile & Purchase History Modal */}
      <CustomerProfileModal
        isOpen={isProfileModalOpen}
        onClose={() => setIsProfileModalOpen(false)}
        customer={profileCustomer}
        onOpenReceipt={(receipt) => {
          setSelectedReceipt(receipt);
          setIsReceiptModalOpen(true);
        }}
      />

      {/* Printable Receipt Modal */}
      <ReceiptModal
        isOpen={isReceiptModalOpen}
        onClose={() => setIsReceiptModalOpen(false)}
        receipt={selectedReceipt}
      />

      {/* Customer Form Modal */}
      <Modal
        isOpen={isFormModalOpen}
        onClose={() => setIsFormModalOpen(false)}
        title={selectedCustomer ? `Edit Customer: ${selectedCustomer.name}` : 'Register New Regular Customer'}
        subtitle="Track customer loyalty rewards, monthly bills, and credit limits"
        maxWidth="md"
      >
        <form onSubmit={handleSubmitForm} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">Full Name *</label>
            <input
              type="text"
              required
              placeholder="e.g. Ramesh Patel"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">Mobile Phone (10 Digits) *</label>
            <input
              type="tel"
              required
              placeholder="9820012345"
              value={formData.phone}
              onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white font-mono focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">Email Address</label>
            <input
              type="email"
              placeholder="ramesh@gmail.com"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">Residential / Delivery Address</label>
            <textarea
              rows={2}
              placeholder="Flat 302, Gokul Heights, Station Road"
              value={formData.address}
              onChange={(e) => setFormData({ ...formData, address: e.target.value })}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-800">
            <Button variant="secondary" type="button" onClick={() => setIsFormModalOpen(false)}>
              Cancel
            </Button>
            <Button variant="primary" type="submit" isLoading={isSaving}>
              {selectedCustomer ? 'Update Customer' : 'Save Customer Profile'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* AI Customer Insights Modal */}
      <Modal
        isOpen={isInsightModalOpen}
        onClose={() => setIsInsightModalOpen(false)}
        title={activeInsight ? `${activeInsight.customerName}'s Profile & Insights` : 'Customer Insights'}
        subtitle="AI purchase frequency, preferred categories, and basket averages"
        maxWidth="lg"
      >
        {isLoadingInsight || !activeInsight ? (
          <LoadingSpinner message="Analyzing customer purchase habits..." />
        ) : (
          <div className="space-y-5">
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
              <div className="p-3 rounded-2xl bg-slate-800/80 border border-slate-700">
                <div className="text-[10px] text-slate-400 uppercase font-semibold">Total Spent</div>
                <div className="text-sm font-extrabold text-white mt-1 font-mono">
                  ₹{activeInsight.totalSpending?.toFixed(2)}
                </div>
              </div>

              <div className="p-3 rounded-2xl bg-slate-800/80 border border-slate-700">
                <div className="text-[10px] text-slate-400 uppercase font-semibold">Avg Basket</div>
                <div className="text-sm font-extrabold text-white mt-1 font-mono">
                  ₹{activeInsight.averageOrderValue?.toFixed(2)}
                </div>
              </div>

              <div className="p-3 rounded-2xl bg-slate-800/80 border border-slate-700">
                <div className="text-[10px] text-slate-400 uppercase font-semibold">Loyalty Balance</div>
                <div className="text-sm font-extrabold text-emerald-400 mt-1 font-mono">
                  {activeInsight.rewardPoints} pts
                </div>
              </div>

              <div className="p-3 rounded-2xl bg-slate-800/80 border border-slate-700">
                <div className="text-[10px] text-slate-400 uppercase font-semibold">Pending Credit</div>
                <div
                  className={`text-sm font-extrabold mt-1 font-mono ${
                    activeInsight.outstandingUdhar > 0 ? 'text-rose-400' : 'text-slate-400'
                  }`}
                >
                  ₹{activeInsight.outstandingUdhar?.toFixed(2)}
                </div>
              </div>
            </div>

            <div>
              <h4 className="text-xs font-bold text-slate-300 uppercase tracking-wider mb-2 flex items-center gap-1.5">
                <Sparkles className="w-3.5 h-3.5 text-emerald-400" />
                Frequently Reordered Items
              </h4>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                {(activeInsight.frequentlyPurchasedProducts || []).map((p) => (
                  <div
                    key={p.id}
                    className="p-2.5 rounded-xl bg-slate-800/60 border border-slate-700/80 flex items-center justify-between"
                  >
                    <div className="truncate">
                      <div className="text-xs font-semibold text-white truncate">{p.name}</div>
                      <div className="text-[10px] text-slate-400 font-mono">₹{p.sellingPrice?.toFixed(2)}</div>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {activeInsight.favoriteCategories && activeInsight.favoriteCategories.length > 0 && (
              <div>
                <h4 className="text-xs font-bold text-slate-300 uppercase tracking-wider mb-2">
                  Top Preferred Categories
                </h4>
                <div className="flex flex-wrap gap-2">
                  {activeInsight.favoriteCategories.map((cat, idx) => (
                    <Badge key={idx} variant="info">
                      {cat}
                    </Badge>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </Modal>
    </div>
  );
};
