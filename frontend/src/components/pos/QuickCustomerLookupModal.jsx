import React, { useState, useEffect } from 'react';
import { Search, UserPlus, Phone } from 'lucide-react';
import { customerService } from '../../services/api';
import { Modal } from '../common/Modal';
import { Button } from '../common/Button';
import { Badge } from '../common/Badge';

export const QuickCustomerLookupModal = ({
  isOpen,
  onClose,
  onSelectCustomer,
}) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [customers, setCustomers] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isCreatingNew, setIsCreatingNew] = useState(false);

  // New Customer Form
  const [newName, setNewName] = useState('');
  const [newPhone, setNewPhone] = useState('');
  const [newEmail, setNewEmail] = useState('');
  const [newAddress, setNewAddress] = useState('');

  useEffect(() => {
    if (isOpen) {
      handleSearch();
    }
  }, [isOpen]);

  const handleSearch = async (query = searchQuery) => {
    try {
      setIsLoading(true);
      const res = await customerService.searchCustomers(query);
      setCustomers(res || []);
    } catch (e) {
      console.error(e);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCreateCustomer = async (e) => {
    e.preventDefault();
    if (!newName.trim() || !newPhone.trim()) return;

    try {
      setIsLoading(true);
      const created = await customerService.createCustomer({
        name: newName.trim(),
        phone: newPhone.trim(),
        email: newEmail.trim() || undefined,
        address: newAddress.trim() || undefined,
      });
      onSelectCustomer(created);
      setIsCreatingNew(false);
      onClose();
    } catch (e) {
      console.error(e);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={isCreatingNew ? 'Register New Customer' : 'Select Customer for Billing'}
      subtitle="Attach customer for loyalty points, store credit, and personalized history"
      maxWidth="lg"
    >
      {!isCreatingNew ? (
        <div className="space-y-4">
          {/* Search Box & Add New Button */}
          <div className="flex gap-2">
            <div className="relative flex-1">
              <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
              <input
                type="text"
                placeholder="Search by customer name or 10-digit mobile..."
                value={searchQuery}
                onChange={(e) => {
                  setSearchQuery(e.target.value);
                  handleSearch(e.target.value);
                }}
                className="w-full pl-10 pr-4 py-2.5 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white placeholder:text-slate-400 focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500"
              />
            </div>
            <Button
              variant="secondary"
              leftIcon={<UserPlus className="w-4 h-4" />}
              onClick={() => {
                setIsCreatingNew(true);
                if (/^\d{10}$/.test(searchQuery)) {
                  setNewPhone(searchQuery);
                } else if (searchQuery.trim()) {
                  setNewName(searchQuery);
                }
              }}
            >
              Add New
            </Button>
          </div>

          {/* Customer Selection List */}
          <div className="max-h-72 overflow-y-auto space-y-2 pr-1">
            {customers.map((c) => (
              <div
                key={c.id}
                onClick={() => {
                  onSelectCustomer(c);
                  onClose();
                }}
                className="p-3.5 rounded-xl bg-slate-800/60 hover:bg-slate-800 border border-slate-700/80 hover:border-emerald-500/50 cursor-pointer transition-all flex items-center justify-between group"
              >
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 flex items-center justify-center font-bold text-sm">
                    {c.name?.charAt(0) || 'C'}
                  </div>
                  <div>
                    <div className="text-sm font-semibold text-white group-hover:text-emerald-400 transition-colors">
                      {c.name}
                    </div>
                    <div className="flex items-center gap-3 text-xs text-slate-400 mt-0.5">
                      <span className="flex items-center gap-1 font-mono">
                        <Phone className="w-3 h-3" /> {c.phone}
                      </span>
                      <span>•</span>
                      <span>Orders: {c.totalOrders || 0}</span>
                    </div>
                  </div>
                </div>

                <div className="text-right">
                  <div className="text-xs text-slate-400">
                    Points: <span className="text-emerald-400 font-bold">{c.rewardPoints || 0}</span>
                  </div>
                  {c.creditBalance > 0 ? (
                    <Badge variant="danger" size="sm" className="mt-1">
                      Credit: ₹{c.creditBalance?.toFixed(2)}
                    </Badge>
                  ) : (
                    <span className="text-[10px] text-slate-500">No Due</span>
                  )}
                </div>
              </div>
            ))}

            {customers.length === 0 && !isLoading && (
              <div className="py-8 text-center text-slate-400 text-xs">
                No customer found matching "{searchQuery}". Click "Add New" to register.
              </div>
            )}
          </div>
        </div>
      ) : (
        <form onSubmit={handleCreateCustomer} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">Customer Name *</label>
            <input
              type="text"
              required
              placeholder="e.g. Ramesh Kumar"
              value={newName}
              onChange={(e) => setNewName(e.target.value)}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">Phone Number (10 digits) *</label>
            <input
              type="tel"
              required
              placeholder="e.g. 9876543210"
              value={newPhone}
              onChange={(e) => setNewPhone(e.target.value)}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white font-mono focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">Email Address (Optional)</label>
            <input
              type="email"
              placeholder="e.g. ramesh@gmail.com"
              value={newEmail}
              onChange={(e) => setNewEmail(e.target.value)}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">Address / Landmark (Optional)</label>
            <textarea
              rows={2}
              placeholder="e.g. House 14, Main Bazaar"
              value={newAddress}
              onChange={(e) => setNewAddress(e.target.value)}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-800">
            <Button variant="ghost" type="button" onClick={() => setIsCreatingNew(false)}>
              Back to Search
            </Button>
            <Button variant="primary" type="submit" isLoading={isLoading}>
              Save & Attach Customer
            </Button>
          </div>
        </form>
      )}
    </Modal>
  );
};
