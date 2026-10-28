import React from 'react';
import { Printer, CheckCircle, Share2, MessageCircle } from 'lucide-react';
import { Button } from '../common/Button';
import { Modal } from '../common/Modal';

export const ReceiptModal = ({ isOpen, onClose, receipt }) => {
  if (!receipt) return null;

  const handlePrint = () => {
    window.print();
  };

  const handleShareWhatsApp = () => {
    const phone = (receipt.customerPhone || '').replace(/\D/g, '');
    const itemsSummary = (receipt.items || [])
      .map((i) => `• ${i.productName} x${i.quantity} = ₹${i.totalPrice?.toFixed(2)}`)
      .join('\n');

    const message =
      `🛍️ *${receipt.shopName || 'SmartShelf Supermarket'}*\n` +
      `--------------------------------\n` +
      `📄 *Tax Invoice #:* ${receipt.billNumber}\n` +
      `📅 *Date:* ${new Date(receipt.dateTime).toLocaleString()}\n` +
      `👤 *Customer:* ${receipt.customerName || 'Customer'}\n` +
      `--------------------------------\n` +
      `*Items:*\n${itemsSummary}\n` +
      `--------------------------------\n` +
      `💰 *Grand Total:* ₹${receipt.totalAmount?.toFixed(2)}\n` +
      `💳 *Paid via:* ${receipt.paymentMethod}\n` +
      (receipt.balanceDue > 0 ? `⚠️ *Pending Credit:* ₹${receipt.balanceDue?.toFixed(2)}\n` : '') +
      (receipt.earnedLoyaltyPoints > 0 ? `⭐ *Loyalty Points Earned:* +${receipt.earnedLoyaltyPoints}\n` : '') +
      `--------------------------------\n` +
      `Thank you for shopping with us! Visit again.`;

    const encoded = encodeURIComponent(message);
    const targetUrl = phone
      ? `https://api.whatsapp.com/send?phone=91${phone.slice(-10)}&text=${encoded}`
      : `https://api.whatsapp.com/send?text=${encoded}`;

    window.open(targetUrl, '_blank');
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Tax Invoice & Receipt" maxWidth="md">
      <div className="space-y-6">
        {/* Success Header Notification */}
        <div className="flex items-center gap-3 p-3.5 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400">
          <CheckCircle className="w-5 h-5 shrink-0" />
          <div className="text-xs">
            <span className="font-bold">Transaction Completed!</span> Bill{' '}
            <span className="font-mono">{receipt.billNumber}</span> generated successfully.
          </div>
        </div>

        {/* Printable Thermal Receipt Card */}
        <div
          id="printable-receipt"
          className="bg-white text-slate-900 p-6 rounded-xl font-mono text-xs border border-slate-300 shadow-inner"
        >
          {/* Store Info */}
          <div className="text-center border-b border-dashed border-slate-300 pb-4">
            <h2 className="text-base font-bold uppercase tracking-wider">{receipt.shopName}</h2>
            <p className="text-[11px] text-slate-600 mt-0.5">{receipt.shopAddress}</p>
            <p className="text-[11px] text-slate-600">Phone: {receipt.shopPhone}</p>
            <p className="text-[11px] text-slate-600">GSTIN: {receipt.shopGstin}</p>
          </div>

          {/* Invoice Meta */}
          <div className="py-3 border-b border-dashed border-slate-300 text-[11px] space-y-1">
            <div className="flex justify-between">
              <span className="text-slate-500">Invoice No:</span>
              <span className="font-bold">{receipt.billNumber}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-500">Order Ref:</span>
              <span>{receipt.orderNumber}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-500">Date & Time:</span>
              <span>{new Date(receipt.dateTime).toLocaleString()}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-500">Cashier:</span>
              <span>{receipt.cashierName}</span>
            </div>
            {receipt.customerName && (
              <div className="flex justify-between font-medium">
                <span className="text-slate-500">Customer:</span>
                <span>
                  {receipt.customerName} {receipt.customerPhone ? `(${receipt.customerPhone})` : ''}
                </span>
              </div>
            )}
          </div>

          {/* Itemized Table */}
          <div className="py-3 border-b border-dashed border-slate-300">
            <div className="flex justify-between font-bold text-[11px] mb-2">
              <span className="w-1/2">Item</span>
              <span className="w-1/6 text-center">Qty</span>
              <span className="w-1/6 text-right">Price</span>
              <span className="w-1/6 text-right">Total</span>
            </div>
            <div className="space-y-1.5 text-[11px]">
              {(receipt.items || []).map((item, idx) => (
                <div key={idx} className="flex justify-between items-start">
                  <span className="w-1/2 truncate font-sans text-[11px]">{item.productName}</span>
                  <span className="w-1/6 text-center">{item.quantity}</span>
                  <span className="w-1/6 text-right">₹{item.unitPrice?.toFixed(2)}</span>
                  <span className="w-1/6 text-right font-semibold">₹{item.totalPrice?.toFixed(2)}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Financial Totals */}
          <div className="py-3 border-b border-dashed border-slate-300 space-y-1 text-[11px]">
            <div className="flex justify-between">
              <span>Subtotal:</span>
              <span>₹{receipt.subtotal?.toFixed(2)}</span>
            </div>
            {receipt.discountAmount > 0 && (
              <div className="flex justify-between text-emerald-700">
                <span>Discount:</span>
                <span>-₹{receipt.discountAmount?.toFixed(2)}</span>
              </div>
            )}
            {receipt.taxAmount > 0 && (
              <div className="flex justify-between">
                <span>Tax / GST:</span>
                <span>+₹{receipt.taxAmount?.toFixed(2)}</span>
              </div>
            )}
            <div className="flex justify-between text-sm font-black pt-1 border-t border-slate-200">
              <span>Grand Total:</span>
              <span>₹{receipt.totalAmount?.toFixed(2)}</span>
            </div>
          </div>

          {/* Payment & Balance */}
          <div className="py-3 border-b border-dashed border-slate-300 text-[11px] space-y-1">
            <div className="flex justify-between">
              <span className="text-slate-500">Payment Mode:</span>
              <span className="font-bold">{receipt.paymentMethod}</span>
            </div>
            {receipt.transactionReference && (
              <div className="flex justify-between text-indigo-700 font-mono text-[10px]">
                <span className="text-slate-500">Txn / UPI Ref:</span>
                <span className="font-semibold">{receipt.transactionReference}</span>
              </div>
            )}
            <div className="flex justify-between">
              <span className="text-slate-500">Amount Tendered:</span>
              <span>₹{receipt.amountPaid?.toFixed(2)}</span>
            </div>
            {receipt.changeReturned > 0 && (
              <div className="flex justify-between text-blue-700 font-bold">
                <span>Change Returned:</span>
                <span>₹{receipt.changeReturned?.toFixed(2)}</span>
              </div>
            )}
            {receipt.balanceDue > 0 && (
              <div className="flex justify-between text-rose-700 font-bold">
                <span>Credit Added:</span>
                <span>₹{receipt.balanceDue?.toFixed(2)}</span>
              </div>
            )}
          </div>

          {/* Customer Loyalty & Credit Status */}
          {receipt.customerName && (
            <div className="py-2 text-[10px] text-slate-600 space-y-0.5">
              <div>• Loyalty Points Earned: +{receipt.earnedLoyaltyPoints} (Total: {receipt.totalCustomerLoyaltyPoints})</div>
              {receipt.customerOutstandingUdhar > 0 && (
                <div className="font-semibold text-rose-700">
                  • Total Outstanding Credit Balance: ₹{receipt.customerOutstandingUdhar?.toFixed(2)}
                </div>
              )}
            </div>
          )}

          {/* Footer Note */}
          <div className="text-center pt-3 text-[10px] text-slate-500">
            <p className="font-bold">THANK YOU FOR SHOPPING WITH US!</p>
            <p className="mt-0.5">Please visit again. Goods once sold are returnable within 3 days.</p>
          </div>
        </div>

        {/* Action Controls */}
        <div className="flex flex-wrap items-center justify-between gap-3 pt-2">
          <Button
            variant="outline"
            size="sm"
            leftIcon={<MessageCircle className="w-4 h-4 text-emerald-400" />}
            onClick={handleShareWhatsApp}
          >
            Share on WhatsApp
          </Button>

          <div className="flex items-center gap-2">
            <Button variant="secondary" onClick={onClose}>
              Close
            </Button>
            <Button variant="primary" leftIcon={<Printer className="w-4 h-4" />} onClick={handlePrint}>
              Print Receipt
            </Button>
          </div>
        </div>
      </div>
    </Modal>
  );
};
