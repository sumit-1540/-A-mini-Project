import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  User,
  Phone,
  Mail,
  MapPin,
  Calendar,
  ShoppingBag,
  CreditCard,
  Star,
  Receipt,
  Clock,
  Sparkles,
  ArrowUpRight,
  MessageCircle,
  ChevronDown,
  ChevronUp,
  Package,
  TrendingUp,
} from 'lucide-react';
import { Modal } from '../common/Modal';
import { Button } from '../common/Button';
import { Badge } from '../common/Badge';
import { customerService, creditService, orderService } from '../../services/api';
import { LoadingSpinner } from '../common/LoadingSpinner';

export const CustomerProfileModal = ({
  isOpen,
  onClose,
  customer,
  onSelectForPos,
  onOpenReceipt,
}) => {
  const navigate = useNavigate();

  const [activeTab, setActiveTab] = useState('orders'); // 'orders' | 'credit' | 'insights'
  const [orders, setOrders] = useState([]);
  const [creditHistory, setCreditHistory] = useState([]);
  const [insights, setInsights] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [expandedOrderId, setExpandedOrderId] = useState(null);

  useEffect(() => {
    if (isOpen && customer?.id) {
      loadCustomerHistory(customer.id);
    }
  }, [isOpen, customer]);

  const loadCustomerHistory = async (customerId) => {
    try {
      setIsLoading(true);
      const [ordersData, creditData, insightsData] = await Promise.all([
        customerService.getCustomerOrders(customerId).catch(() => []),
        creditService.getCustomerTransactions(customerId).catch(() => []),
        customerService.getCustomerInsights(customerId).catch(() => null),
      ]);
      setOrders(ordersData || []);
      setCreditHistory(creditData || []);
      setInsights(insightsData);
    } catch (e) {
      console.error('Error fetching customer profile history:', e);
    } finally {
      setIsLoading(false);
    }
  };

  if (!customer) return null;

  const handleSendWhatsAppReminder = () => {
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

  const handleViewReceipt = async (orderId) => {
    try {
      const receipt = await orderService.getReceipt(orderId);
      if (onOpenReceipt) {
        onOpenReceipt(receipt);
      }
    } catch (err) {
      console.error('Failed to load receipt:', err);
    }
  };

  const handleStartPos = () => {
    if (onSelectForPos) {
      onSelectForPos(customer);
    } else {
      navigate(`/pos?customerId=${customer.id}`);
    }
    onClose();
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title=""
      maxWidth="3xl"
    >
      <div className="space-y-6">
        {/* Customer Profile Banner */}
        <div className="p-5 rounded-2xl bg-gradient-to-r from-emerald-950/70 via-slate-900 to-slate-900 border border-emerald-500/30 flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            <div className="w-14 h-14 rounded-2xl bg-emerald-500/20 border border-emerald-500/30 text-emerald-400 font-extrabold text-2xl flex items-center justify-center shadow-lg shadow-emerald-950/50">
              {customer.name?.charAt(0) || 'C'}
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h2 className="text-xl font-extrabold text-white">{customer.name}</h2>
                {customer.creditBalance > 0 ? (
                  <Badge variant="danger" size="sm">
                    Pending Credit: ₹{customer.creditBalance?.toFixed(2)}
                  </Badge>
                ) : (
                  <Badge variant="success" size="sm">
                    Clean Account
                  </Badge>
                )}
              </div>

              <div className="flex flex-wrap items-center gap-3 text-xs text-slate-400 mt-1 font-mono">
                <span className="flex items-center gap-1 text-slate-300">
                  <Phone className="w-3.5 h-3.5 text-emerald-400" /> {customer.phone}
                </span>
                {customer.email && (
                  <span className="flex items-center gap-1">
                    <Mail className="w-3.5 h-3.5 text-slate-500" /> {customer.email}
                  </span>
                )}
                {customer.address && (
                  <span className="flex items-center gap-1 font-sans">
                    <MapPin className="w-3.5 h-3.5 text-slate-500" /> {customer.address}
                  </span>
                )}
              </div>
            </div>
          </div>

          {/* Quick Header Actions */}
          <div className="flex items-center gap-2 w-full md:w-auto">
            {customer.creditBalance > 0 && (
              <Button
                size="sm"
                variant="outline"
                leftIcon={<MessageCircle className="w-3.5 h-3.5 text-emerald-400" />}
                onClick={handleSendWhatsAppReminder}
                title="Send WhatsApp payment reminder"
              >
                Reminder
              </Button>
            )}
            <Button
              size="sm"
              variant="primary"
              leftIcon={<ShoppingBag className="w-3.5 h-3.5" />}
              onClick={handleStartPos}
            >
              New POS Bill
            </Button>
          </div>
        </div>

        {/* Lifetime Metrics Strip */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
          <div className="p-3.5 rounded-xl bg-slate-900 border border-slate-800">
            <div className="text-[10px] text-slate-400 uppercase font-semibold">Lifetime Spend</div>
            <div className="text-lg font-black text-white font-mono mt-0.5">
              ₹{customer.totalPurchases?.toFixed(2) || '0.00'}
            </div>
          </div>

          <div className="p-3.5 rounded-xl bg-slate-900 border border-slate-800">
            <div className="text-[10px] text-slate-400 uppercase font-semibold">Total Orders</div>
            <div className="text-lg font-black text-cyan-400 font-mono mt-0.5">
              {customer.totalOrders || orders.length || 0} bills
            </div>
          </div>

          <div className="p-3.5 rounded-xl bg-slate-900 border border-slate-800">
            <div className="text-[10px] text-slate-400 uppercase font-semibold">Reward Points</div>
            <div className="text-lg font-black text-emerald-400 font-mono mt-0.5 flex items-center gap-1">
              <Star className="w-4 h-4 fill-emerald-400 text-emerald-400" />
              {customer.rewardPoints || 0} pts
            </div>
          </div>

          <div className="p-3.5 rounded-xl bg-slate-900 border border-slate-800">
            <div className="text-[10px] text-slate-400 uppercase font-semibold">Outstanding Credit</div>
            <div className={`text-lg font-black font-mono mt-0.5 ${customer.creditBalance > 0 ? 'text-rose-400' : 'text-emerald-400'}`}>
              ₹{customer.creditBalance?.toFixed(2) || '0.00'}
            </div>
          </div>
        </div>

        {/* Tab Navigation */}
        <div className="flex items-center gap-2 border-b border-slate-800 pb-2">
          <button
            onClick={() => setActiveTab('orders')}
            className={`px-3 py-1.5 rounded-xl text-xs font-semibold flex items-center gap-2 transition-all ${
              activeTab === 'orders'
                ? 'bg-emerald-600 text-white shadow-md'
                : 'text-slate-400 hover:text-white hover:bg-slate-800'
            }`}
          >
            <Receipt className="w-3.5 h-3.5" />
            Previous Purchase History ({orders.length})
          </button>

          <button
            onClick={() => setActiveTab('credit')}
            className={`px-3 py-1.5 rounded-xl text-xs font-semibold flex items-center gap-2 transition-all ${
              activeTab === 'credit'
                ? 'bg-emerald-600 text-white shadow-md'
                : 'text-slate-400 hover:text-white hover:bg-slate-800'
            }`}
          >
            <CreditCard className="w-3.5 h-3.5" />
            Credit Ledger ({creditHistory.length})
          </button>

          <button
            onClick={() => setActiveTab('insights')}
            className={`px-3 py-1.5 rounded-xl text-xs font-semibold flex items-center gap-2 transition-all ${
              activeTab === 'insights'
                ? 'bg-emerald-600 text-white shadow-md'
                : 'text-slate-400 hover:text-white hover:bg-slate-800'
            }`}
          >
            <Sparkles className="w-3.5 h-3.5" />
            Buying Habits & Favorites
          </button>
        </div>

        {/* Content Container */}
        {isLoading ? (
          <div className="py-12">
            <LoadingSpinner message="Loading customer purchase history..." />
          </div>
        ) : (
          <div className="space-y-4">
            {/* TAB 1: Previous Purchase Orders */}
            {activeTab === 'orders' && (
              <div className="space-y-3 max-h-96 overflow-y-auto pr-1">
                {orders.length > 0 ? (
                  orders.map((order) => {
                    const isExpanded = expandedOrderId === order.id;
                    const items = order.items || [];
                    return (
                      <div
                        key={order.id}
                        className="rounded-2xl bg-slate-900 border border-slate-800 overflow-hidden transition-all hover:border-slate-700"
                      >
                        <div
                          onClick={() => setExpandedOrderId(isExpanded ? null : order.id)}
                          className="p-3.5 flex flex-wrap items-center justify-between gap-3 cursor-pointer select-none"
                        >
                          <div className="flex items-center gap-3">
                            <div className="w-9 h-9 rounded-xl bg-slate-800 flex items-center justify-center text-slate-300">
                              <Receipt className="w-4 h-4 text-emerald-400" />
                            </div>
                            <div>
                              <div className="flex items-center gap-2">
                                <span className="font-mono font-bold text-white text-xs">
                                  {order.billNumber || order.orderNumber}
                                </span>
                                <Badge variant="secondary" size="sm">
                                  {order.paymentMethod}
                                </Badge>
                              </div>
                              <div className="text-[11px] text-slate-400 flex items-center gap-2 mt-0.5">
                                <span className="flex items-center gap-1">
                                  <Clock className="w-3 h-3 text-slate-500" />
                                  {order.createdAt ? new Date(order.createdAt).toLocaleString() : 'Recent'}
                                </span>
                                <span>•</span>
                                <span>{items.length} items</span>
                                {order.cashierName && (
                                  <>
                                    <span>•</span>
                                    <span>Cashier: {order.cashierName}</span>
                                  </>
                                )}
                              </div>
                            </div>
                          </div>

                          <div className="flex items-center gap-3">
                            <div className="text-right font-mono">
                              <div className="text-sm font-black text-white">
                                ₹{order.totalAmount?.toFixed(2)}
                              </div>
                              {order.discountAmount > 0 && (
                                <div className="text-[10px] text-emerald-400">
                                  -₹{order.discountAmount?.toFixed(2)} off
                                </div>
                              )}
                            </div>

                            <button
                              type="button"
                              onClick={(e) => {
                                e.stopPropagation();
                                handleViewReceipt(order.id);
                              }}
                              className="p-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white transition-colors"
                              title="Reprint / View Bill Receipt"
                            >
                              <ArrowUpRight className="w-4 h-4" />
                            </button>

                            <button className="text-slate-400">
                              {isExpanded ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
                            </button>
                          </div>
                        </div>

                        {/* Expanded Itemized Breakdown */}
                        {isExpanded && (
                          <div className="p-3 bg-slate-950/60 border-t border-slate-800 text-xs">
                            <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-2">
                              Itemized Invoice Items:
                            </div>
                            <div className="divide-y divide-slate-800/60">
                              {items.map((item, idx) => (
                                <div key={idx} className="py-1.5 flex items-center justify-between font-mono">
                                  <div className="flex items-center gap-2 font-sans">
                                    <span className="text-slate-500 text-[11px]">{idx + 1}.</span>
                                    <span className="text-white font-medium">{item.productName}</span>
                                    <span className="text-slate-400 text-[11px]">
                                      &times; {item.quantity} {item.unit || 'pcs'}
                                    </span>
                                  </div>
                                  <div className="text-slate-300 font-bold">
                                    ₹{item.totalPrice?.toFixed(2) || (item.unitPrice * item.quantity).toFixed(2)}
                                  </div>
                                </div>
                              ))}
                            </div>
                          </div>
                        )}
                      </div>
                    );
                  })
                ) : (
                  <div className="py-12 text-center text-slate-500 text-xs">
                    No past purchase records found for this customer.
                  </div>
                )}
              </div>
            )}

            {/* TAB 2: Credit Ledger */}
            {activeTab === 'credit' && (
              <div className="space-y-3 max-h-96 overflow-y-auto pr-1">
                {creditHistory.length > 0 ? (
                  <div className="divide-y divide-slate-800 bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden">
                    {creditHistory.map((tx) => (
                      <div key={tx.id} className="p-3.5 flex items-center justify-between text-xs font-mono">
                        <div className="flex items-center gap-3">
                          <div
                            className={`w-8 h-8 rounded-xl flex items-center justify-center ${
                              tx.type === 'PAYMENT_RECEIVED'
                                ? 'bg-emerald-500/20 text-emerald-400'
                                : 'bg-rose-500/20 text-rose-400'
                            }`}
                          >
                            <CreditCard className="w-4 h-4" />
                          </div>
                          <div>
                            <div className="font-sans font-bold text-white">
                              {tx.type === 'PAYMENT_RECEIVED' ? 'Payment Settle Received' : 'Credit Billed on Purchase'}
                            </div>
                            <div className="text-[11px] text-slate-400">
                              {tx.createdAt ? new Date(tx.createdAt).toLocaleString() : 'Recent'} • Handled by: {tx.recordedBy || 'Cashier'}
                            </div>
                          </div>
                        </div>

                        <div className="text-right">
                          <div
                            className={`font-black text-sm ${
                              tx.type === 'PAYMENT_RECEIVED' ? 'text-emerald-400' : 'text-rose-400'
                            }`}
                          >
                            {tx.type === 'PAYMENT_RECEIVED' ? '-' : '+'}₹{tx.amount?.toFixed(2)}
                          </div>
                          <div className="text-[10px] text-slate-400">
                            Balance: ₹{tx.balanceAfter?.toFixed(2)}
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="py-12 text-center text-slate-500 text-xs">
                    No credit transactions recorded for this customer.
                  </div>
                )}
              </div>
            )}

            {/* TAB 3: Buying Habits & AI Preferences */}
            {activeTab === 'insights' && (
              <div className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 space-y-3">
                    <div className="text-xs font-bold text-white flex items-center gap-2">
                      <TrendingUp className="w-4 h-4 text-emerald-400" />
                      Shopping Behavior & Analytics
                    </div>

                    <div className="space-y-2 text-xs">
                      <div className="flex justify-between py-1 border-b border-slate-800">
                        <span className="text-slate-400">Average Order Value (AOV):</span>
                        <span className="font-mono font-bold text-white">
                          ₹{insights?.averageOrderValue?.toFixed(2) || '0.00'}
                        </span>
                      </div>
                      <div className="flex justify-between py-1 border-b border-slate-800">
                        <span className="text-slate-400">Preferred Payment Mode:</span>
                        <span className="font-semibold text-emerald-400">
                          {insights?.preferredPaymentMethod || 'CASH'}
                        </span>
                      </div>
                      <div className="flex justify-between py-1">
                        <span className="text-slate-400">Favorite Categories:</span>
                        <span className="text-slate-200">
                          {(insights?.favoriteCategories || ['Grains & Pulses', 'Dairy']).join(', ')}
                        </span>
                      </div>
                    </div>
                  </div>

                  <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 space-y-3">
                    <div className="text-xs font-bold text-white flex items-center gap-2">
                      <Package className="w-4 h-4 text-cyan-400" />
                      Frequently Purchased Products
                    </div>

                    <div className="space-y-2 max-h-48 overflow-y-auto">
                      {(insights?.frequentlyPurchasedProducts || []).length > 0 ? (
                        insights.frequentlyPurchasedProducts.map((p, i) => (
                          <div
                            key={i}
                            className="p-2 rounded-xl bg-slate-800/60 border border-slate-700/60 flex items-center justify-between text-xs font-mono"
                          >
                            <span className="font-sans text-white font-medium truncate max-w-xs">{p.name}</span>
                            <span className="text-emerald-400 font-bold">₹{p.sellingPrice?.toFixed(2)}</span>
                          </div>
                        ))
                      ) : (
                        <div className="text-xs text-slate-500 py-6 text-center">
                          Builds as more orders are placed by this customer.
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}

        {/* Footer */}
        <div className="flex justify-end pt-3 border-t border-slate-800">
          <Button variant="secondary" onClick={onClose}>
            Close Profile
          </Button>
        </div>
      </div>
    </Modal>
  );
};
