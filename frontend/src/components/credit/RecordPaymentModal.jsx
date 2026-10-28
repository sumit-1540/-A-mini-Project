import React, { useState, useEffect } from 'react';
import { creditService } from '../../services/api';
import { Modal } from '../common/Modal';
import { Button } from '../common/Button';
import { useToast } from '../../context/ToastContext';

export const RecordPaymentModal = ({
  isOpen,
  onClose,
  customer,
  onPaymentRecorded,
}) => {
  const { success, error } = useToast();
  const [amount, setAmount] = useState(0);
  const [paymentMethod, setPaymentMethod] = useState('CASH');
  const [notes, setNotes] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (customer) {
      setAmount(customer.creditBalance || 0);
      setNotes('');
    }
  }, [customer]);

  if (!customer) return null;

  const currentDue = customer.creditBalance || 0;
  const remainingDue = Math.max(0, currentDue - amount);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (amount <= 0) return;

    try {
      setIsLoading(true);
      await creditService.recordPayment({
        customerId: customer.id,
        amount,
        paymentMethod,
        notes: notes.trim() || 'Credit payment recorded',
      });
      success(`Payment of ₹${amount.toFixed(2)} received from ${customer.name}!`);
      onPaymentRecorded();
      onClose();
    } catch (err) {
      error(err.response?.data?.message || 'Failed to record repayment');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={`Receive Credit Payment: ${customer.name}`}
      subtitle={`Mobile: ${customer.phone} | Outstanding Balance: ₹${currentDue.toFixed(2)}`}
      maxWidth="md"
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Outstanding Badge */}
        <div className="p-4 rounded-xl bg-rose-500/10 border border-rose-500/30 flex items-center justify-between text-xs">
          <span className="text-slate-300">Total Outstanding Balance:</span>
          <span className="font-bold text-rose-400 text-base font-mono">₹{currentDue.toFixed(2)}</span>
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-300 mb-1.5">Repayment Amount (₹) *</label>
          <input
            type="number"
            step="0.01"
            min="0.01"
            max={currentDue}
            required
            value={amount}
            onChange={(e) => setAmount(Number(e.target.value))}
            className="w-full px-3.5 py-2.5 bg-slate-800 border border-slate-700 rounded-xl text-base text-white font-mono focus:outline-none focus:border-emerald-500"
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-300 mb-1.5">Payment Method *</label>
          <select
            value={paymentMethod}
            onChange={(e) => setPaymentMethod(e.target.value)}
            className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
          >
            <option value="CASH">Cash</option>
            <option value="UPI_QR">UPI / QR Code</option>
            <option value="CARD">Debit / Credit Card</option>
            <option value="NET_BANKING">Net Banking / Bank Transfer</option>
          </select>
        </div>

        {/* Projected Remaining Balance */}
        <div className="p-3 rounded-xl bg-slate-800 border border-slate-700 flex items-center justify-between text-xs">
          <span className="text-slate-400">Remaining Balance After Payment:</span>
          <span className="font-mono font-bold text-emerald-400">₹{remainingDue.toFixed(2)}</span>
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-300 mb-1.5">Notes / Receipt Reference</label>
          <input
            type="text"
            placeholder="e.g. UPI Ref #401928, Paid by son, etc."
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
          />
        </div>

        <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-800">
          <Button variant="secondary" type="button" onClick={onClose}>
            Cancel
          </Button>
          <Button variant="primary" type="submit" disabled={amount <= 0} isLoading={isLoading}>
            Record Payment (₹{amount.toFixed(2)})
          </Button>
        </div>
      </form>
    </Modal>
  );
};
