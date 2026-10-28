import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import {
  CreditCard,
  Search,
  History,
  Download,
  IndianRupee,
  MessageCircle,
} from 'lucide-react';
import { customerService, creditService, reportService } from '../services/api';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { RecordPaymentModal } from '../components/credit/RecordPaymentModal';
import { CustomerProfileModal } from '../components/customers/CustomerProfileModal';
import { LoadingSpinner } from '../components/common/LoadingSpinner';

export const CreditUdharPage = () => {
  const [searchParams] = useSearchParams();
  const customerParam = searchParams.get('customer');

  const [activeTab, setActiveTab] = useState('pending');
  const [pendingCustomers, setPendingCustomers] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [isLoading, setIsLoading] = useState(true);

  // Payment Modal
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [isPaymentModalOpen, setIsPaymentModalOpen] = useState(false);

  // Customer Profile & History Modal
  const [profileCustomer, setProfileCustomer] = useState(null);
  const [isProfileModalOpen, setIsProfileModalOpen] = useState(false);

  const handleSendWhatsAppReminder = (customer) => {
    const phone = (customer.phone || '').replace(/\D/g, '');
    const message =
      `Namaste *${customer.name}*,\n\n` +
      `This is a gentle reminder from *SmartShelf Supermarket* regarding your pending credit balance of *₹${customer.creditBalance?.toFixed(2)}*.\n\n` +
      `Kindly settle the outstanding amount at your earliest convenience via Cash or UPI.\n\n` +
      `Thank you for your valued patronage!`;
    const encoded = encodeURIComponent(message);
    const targetUrl = phone
      ? `https://api.whatsapp.com/send?phone=91${phone.slice(-10)}&text=${encoded}`
      : `https://api.whatsapp.com/send?text=${encoded}`;
    window.open(targetUrl, '_blank');
  };

  useEffect(() => {
    loadData();
  }, [activeTab]);

  const loadData = async () => {
    try {
      setIsLoading(true);
      if (activeTab === 'pending') {
        const data = await customerService.getPendingCreditCustomers();
        setPendingCustomers(data || []);

        if (customerParam) {
          const match = (data || []).find((c) => String(c.id) === customerParam);
          if (match) {
            setSelectedCustomer(match);
            setIsPaymentModalOpen(true);
          }
        }
      } else {
        const txs = await creditService.getAllTransactions();
        setTransactions(txs || []);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setIsLoading(false);
    }
  };

  const totalOutstanding = pendingCustomers.reduce((sum, c) => sum + (c.creditBalance || 0), 0);

  const filteredCustomers = pendingCustomers.filter(
    (c) =>
      c.name?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      c.phone?.includes(searchQuery.trim())
  );

  return (
    <div className="space-y-6">
      {/* Header Bar */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white tracking-tight flex items-center gap-2.5">
            <CreditCard className="w-7 h-7 text-rose-400" />
            Customer Credit Register
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Track customer balances, credit limits, settlements, and payment history
          </p>
        </div>

        <div className="flex items-center gap-2.5">
          <Button
            variant="outline"
            leftIcon={<Download className="w-4 h-4" />}
            onClick={() => reportService.downloadCreditCsv()}
          >
            Export Credit CSV
          </Button>
        </div>
      </div>

      {/* Summary KPI Banner */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="p-5 rounded-3xl bg-gradient-to-br from-rose-950/40 to-slate-900 border border-rose-500/30 shadow-xl">
          <div className="text-xs font-bold text-rose-400 uppercase tracking-wider">
            Total Outstanding Credit
          </div>
          <div className="text-3xl font-black text-white mt-1 font-mono">
            ₹{totalOutstanding.toFixed(2)}
          </div>
          <div className="text-[11px] text-slate-400 mt-1">Pending recovery from customers</div>
        </div>

        <div className="p-5 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl">
          <div className="text-xs font-bold text-slate-400 uppercase tracking-wider">
            Customers with Due Balance
          </div>
          <div className="text-3xl font-black text-white mt-1 font-mono">
            {pendingCustomers.length}
          </div>
          <div className="text-[11px] text-slate-400 mt-1">Active ledger accounts</div>
        </div>

        <div className="p-5 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl">
          <div className="text-xs font-bold text-emerald-400 uppercase tracking-wider">
            Settlement Mode
          </div>
          <div className="text-sm font-semibold text-white mt-1">
            Instant Partial or Full Repayments
          </div>
          <div className="text-[11px] text-slate-400 mt-1">Logs automatically into store cash register</div>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex border-b border-slate-800 space-x-6 text-sm font-semibold">
        <button
          onClick={() => setActiveTab('pending')}
          className={`pb-3 flex items-center gap-2 transition-colors relative ${
            activeTab === 'pending' ? 'text-rose-400' : 'text-slate-400 hover:text-white'
          }`}
        >
          <CreditCard className="w-4 h-4" />
          <span>Pending Customer Dues ({pendingCustomers.length})</span>
          {activeTab === 'pending' && (
            <span className="absolute bottom-0 left-0 right-0 h-0.5 bg-rose-400 rounded-full" />
          )}
        </button>

        <button
          onClick={() => setActiveTab('history')}
          className={`pb-3 flex items-center gap-2 transition-colors relative ${
            activeTab === 'history' ? 'text-emerald-400' : 'text-slate-400 hover:text-white'
          }`}
        >
          <History className="w-4 h-4" />
          <span>Credit Transaction Log</span>
          {activeTab === 'history' && (
            <span className="absolute bottom-0 left-0 right-0 h-0.5 bg-emerald-400 rounded-full" />
          )}
        </button>
      </div>

      {activeTab === 'pending' ? (
        <div className="space-y-4">
          {/* Search */}
          <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl">
            <div className="relative max-w-sm">
              <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
              <input
                type="text"
                placeholder="Search by customer name or phone..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full pl-10 pr-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-xs text-white placeholder:text-slate-400 focus:outline-none focus:border-emerald-500"
              />
            </div>
          </div>

          {/* Pending Customers Table */}
          <div className="bg-slate-900 border border-slate-800 rounded-3xl overflow-hidden shadow-2xl">
            {isLoading ? (
              <LoadingSpinner message="Calculating customer balances..." />
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs">
                  <thead className="text-slate-400 uppercase tracking-wider bg-slate-900/80 border-b border-slate-800">
                    <tr>
                      <th className="px-5 py-4 font-semibold">Customer</th>
                      <th className="px-4 py-4 font-semibold">Phone</th>
                      <th className="px-4 py-4 font-semibold text-right">Lifetime Spent</th>
                      <th className="px-4 py-4 font-semibold text-center">Orders</th>
                      <th className="px-4 py-4 font-semibold text-right">Outstanding Credit</th>
                      <th className="px-5 py-4 font-semibold text-right">Actions</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-800/60">
                    {filteredCustomers.map((customer) => (
                      <tr key={customer.id} className="hover:bg-slate-800/40 transition-colors">
                        <td className="px-5 py-3.5">
                          <button
                            type="button"
                            onClick={() => {
                              setProfileCustomer(customer);
                              setIsProfileModalOpen(true);
                            }}
                            className="text-left group cursor-pointer focus:outline-none"
                          >
                            <div className="font-semibold text-white group-hover:text-indigo-400 transition-colors flex items-center gap-1.5">
                              {customer.name}
                              <span className="text-[10px] text-indigo-400 opacity-0 group-hover:opacity-100 transition-opacity bg-indigo-500/10 px-1.5 py-0.5 rounded border border-indigo-500/20 font-sans">
                                Profile ↗
                              </span>
                            </div>
                            {customer.address && (
                              <div className="text-[11px] text-slate-400 truncate max-w-xs">
                                {customer.address}
                              </div>
                            )}
                          </button>
                        </td>

                        <td className="px-4 py-3.5 font-mono text-slate-300">{customer.phone}</td>

                        <td className="px-4 py-3.5 text-right font-mono text-slate-400">
                          ₹{customer.totalPurchases?.toFixed(2)}
                        </td>

                        <td className="px-4 py-3.5 text-center font-mono text-slate-300">
                          {customer.totalOrders || 0}
                        </td>

                        <td className="px-4 py-3.5 text-right font-mono font-bold text-rose-400 text-sm">
                          ₹{customer.creditBalance?.toFixed(2)}
                        </td>

                        <td className="px-5 py-3.5 text-right">
                          <div className="flex items-center justify-end gap-2">
                            <Button
                              size="sm"
                              variant="outline"
                              onClick={() => {
                                setProfileCustomer(customer);
                                setIsProfileModalOpen(true);
                              }}
                              title="View Customer Profile & Purchase History"
                            >
                              Profile
                            </Button>
                            <Button
                              size="sm"
                              variant="outline"
                              leftIcon={<MessageCircle className="w-3.5 h-3.5 text-emerald-400" />}
                              onClick={() => handleSendWhatsAppReminder(customer)}
                              title="Send WhatsApp Payment Reminder"
                            >
                              Reminder
                            </Button>
                            <Button
                              size="sm"
                              variant="primary"
                              leftIcon={<IndianRupee className="w-3.5 h-3.5" />}
                              onClick={() => {
                                setSelectedCustomer(customer);
                                setIsPaymentModalOpen(true);
                              }}
                            >
                              Receive Payment
                            </Button>
                          </div>
                        </td>
                      </tr>
                    ))}

                    {filteredCustomers.length === 0 && (
                      <tr>
                        <td colSpan={6} className="px-5 py-12 text-center text-slate-400 text-xs">
                          🎉 No pending customer dues found! All customer accounts are fully settled.
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      ) : (
        /* History Table */
        <div className="bg-slate-900 border border-slate-800 rounded-3xl overflow-hidden shadow-2xl">
          {isLoading ? (
            <LoadingSpinner message="Loading credit transaction audit trail..." />
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead className="text-slate-400 uppercase tracking-wider bg-slate-900/80 border-b border-slate-800">
                  <tr>
                    <th className="px-5 py-4 font-semibold">Timestamp</th>
                    <th className="px-4 py-4 font-semibold">Customer</th>
                    <th className="px-4 py-4 font-semibold">Type</th>
                    <th className="px-4 py-4 font-semibold text-right">Amount</th>
                    <th className="px-4 py-4 font-semibold text-right">Balance After</th>
                    <th className="px-4 py-4 font-semibold">Payment Mode / Notes</th>
                    <th className="px-4 py-4 font-semibold text-right">Recorded By</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/60 font-mono">
                  {transactions.map((tx) => (
                    <tr key={tx.id} className="hover:bg-slate-800/40 transition-colors">
                      <td className="px-5 py-3.5 text-slate-400 text-[11px]">
                        {new Date(tx.createdAt).toLocaleString()}
                      </td>

                      <td className="px-4 py-3.5 font-sans font-semibold text-white">
                        {tx.customerName}
                      </td>

                      <td className="px-4 py-3.5 font-sans">
                        <Badge
                          variant={tx.type === 'PAYMENT_RECEIVED' ? 'success' : 'danger'}
                          size="sm"
                        >
                          {tx.type === 'PAYMENT_RECEIVED' ? 'Payment Received' : 'Credit Extended'}
                        </Badge>
                      </td>

                      <td
                        className={`px-4 py-3.5 text-right font-bold ${
                          tx.type === 'PAYMENT_RECEIVED' ? 'text-emerald-400' : 'text-rose-400'
                        }`}
                      >
                        {tx.type === 'PAYMENT_RECEIVED' ? '-' : '+'}₹{tx.amount?.toFixed(2)}
                      </td>

                      <td className="px-4 py-3.5 text-right text-slate-200">
                        ₹{tx.balanceAfter?.toFixed(2)}
                      </td>

                      <td className="px-4 py-3.5 font-sans text-slate-400 text-[11px]">
                        {tx.notes} {tx.paymentMethod ? `via ${tx.paymentMethod}` : ''}
                      </td>

                      <td className="px-4 py-3.5 text-right font-sans text-slate-400 text-[11px]">
                        {tx.recordedBy || 'Staff'}
                      </td>
                    </tr>
                  ))}

                  {transactions.length === 0 && (
                    <tr>
                      <td colSpan={7} className="px-5 py-12 text-center text-slate-400 text-xs font-sans">
                        No credit transaction records found.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* Record Payment Modal */}
      <RecordPaymentModal
        isOpen={isPaymentModalOpen}
        onClose={() => setIsPaymentModalOpen(false)}
        customer={selectedCustomer}
        onPaymentRecorded={loadData}
      />

      {/* Customer Profile & Purchase History Modal */}
      <CustomerProfileModal
        isOpen={isProfileModalOpen}
        onClose={() => setIsProfileModalOpen(false)}
        customer={profileCustomer}
        onRefresh={loadData}
      />
    </div>
  );
};
