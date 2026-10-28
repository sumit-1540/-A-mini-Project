import React, { useState, useEffect } from 'react';
import { Receipt, Search, Printer, Download } from 'lucide-react';
import { orderService, reportService } from '../services/api';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { ReceiptModal } from '../components/pos/ReceiptModal';
import { LoadingSpinner } from '../components/common/LoadingSpinner';

export const SalesPage = () => {
  const [orders, setOrders] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  // Receipt Modal
  const [selectedReceipt, setSelectedReceipt] = useState(null);
  const [isReceiptModalOpen, setIsReceiptModalOpen] = useState(false);

  useEffect(() => {
    loadOrders();
  }, []);

  const loadOrders = async () => {
    try {
      setIsLoading(true);
      const res = await orderService.getOrders({ size: 100 });
      setOrders(res?.content || []);
    } catch (e) {
      console.error(e);
    } finally {
      setIsLoading(false);
    }
  };

  const handleReprintReceipt = async (orderId) => {
    try {
      const receipt = await orderService.getReceipt(orderId);
      setSelectedReceipt(receipt);
      setIsReceiptModalOpen(true);
    } catch (e) {
      console.error(e);
    }
  };

  const filteredOrders = orders.filter((o) => {
    if (!searchQuery.trim()) return true;
    const q = searchQuery.toLowerCase();
    return (
      o.orderNumber?.toLowerCase().includes(q) ||
      (o.customerName && o.customerName.toLowerCase().includes(q)) ||
      (o.customerPhone && o.customerPhone.includes(q)) ||
      (o.billNumber && o.billNumber.toLowerCase().includes(q))
    );
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white tracking-tight flex items-center gap-2.5">
            <Receipt className="w-7 h-7 text-emerald-400" />
            Sales Transactions & Invoices
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Complete store billing history, payment settlements, gross margin, and reprintable invoices
          </p>
        </div>

        <Button
          variant="outline"
          leftIcon={<Download className="w-4 h-4" />}
          onClick={() => reportService.downloadSalesCsv()}
        >
          Export Sales CSV
        </Button>
      </div>

      {/* Search Filter */}
      <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl">
        <div className="relative max-w-sm">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search by order #, bill #, customer phone..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-10 pr-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-xs text-white placeholder:text-slate-400 focus:outline-none focus:border-emerald-500"
          />
        </div>
      </div>

      {/* Orders Table */}
      <div className="bg-slate-900 border border-slate-800 rounded-3xl overflow-hidden shadow-2xl">
        {isLoading ? (
          <LoadingSpinner message="Loading sales records..." />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="text-slate-400 uppercase tracking-wider bg-slate-900/80 border-b border-slate-800">
                <tr>
                  <th className="px-5 py-4 font-semibold">Order / Bill #</th>
                  <th className="px-4 py-4 font-semibold">Date & Time</th>
                  <th className="px-4 py-4 font-semibold">Customer</th>
                  <th className="px-4 py-4 font-semibold">Cashier</th>
                  <th className="px-4 py-4 font-semibold text-center">Items</th>
                  <th className="px-4 py-4 font-semibold text-right">Total Amount</th>
                  <th className="px-4 py-4 font-semibold text-right">Gross Profit</th>
                  <th className="px-4 py-4 font-semibold text-center">Payment Mode</th>
                  <th className="px-5 py-4 font-semibold text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {filteredOrders.map((order) => (
                  <tr key={order.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-5 py-3.5 font-mono">
                      <div className="font-bold text-white">{order.orderNumber}</div>
                      <div className="text-[10px] text-slate-400">{order.billNumber || 'BILL-AUTO'}</div>
                    </td>

                    <td className="px-4 py-3.5 font-mono text-[11px] text-slate-300">
                      {new Date(order.createdAt).toLocaleString()}
                    </td>

                    <td className="px-4 py-3.5">
                      <div className="font-semibold text-white">
                        {order.customerName || 'Walk-in Customer'}
                      </div>
                      {order.customerPhone && (
                        <div className="text-[11px] text-slate-400 font-mono">{order.customerPhone}</div>
                      )}
                    </td>

                    <td className="px-4 py-3.5 text-slate-300">{order.cashierName}</td>

                    <td className="px-4 py-3.5 text-center font-mono text-slate-300">
                      {order.items?.length || 0}
                    </td>

                    <td className="px-4 py-3.5 text-right font-mono font-bold text-white text-sm">
                      ₹{order.totalAmount?.toFixed(2)}
                    </td>

                    <td className="px-4 py-3.5 text-right font-mono font-bold text-emerald-400">
                      +₹{order.grossProfit?.toFixed(2)}
                    </td>

                    <td className="px-4 py-3.5 text-center">
                      <Badge
                        variant={
                          order.paymentMethod === 'CREDIT_UDHAR'
                            ? 'danger'
                            : order.paymentMethod === 'UPI_QR'
                            ? 'info'
                            : 'success'
                        }
                        size="sm"
                      >
                        {order.paymentMethod}
                      </Badge>
                    </td>

                    <td className="px-5 py-3.5 text-right">
                      <Button
                        size="sm"
                        variant="ghost"
                        leftIcon={<Printer className="w-3.5 h-3.5 text-emerald-400" />}
                        onClick={() => handleReprintReceipt(order.id)}
                      >
                        Receipt
                      </Button>
                    </td>
                  </tr>
                ))}

                {filteredOrders.length === 0 && (
                  <tr>
                    <td colSpan={9} className="px-5 py-12 text-center text-slate-400 text-xs">
                      No sales records found matching your search.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Printable Receipt Modal */}
      <ReceiptModal
        isOpen={isReceiptModalOpen}
        onClose={() => setIsReceiptModalOpen(false)}
        receipt={selectedReceipt}
      />
    </div>
  );
};
