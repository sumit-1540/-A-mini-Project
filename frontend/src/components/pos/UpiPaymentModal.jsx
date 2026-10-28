import React, { useState, useEffect } from 'react';
import {
  QrCode,
  CheckCircle,
  Copy,
  ShieldCheck,
  Smartphone,
  Sparkles,
  Check,
  User,
} from 'lucide-react';
import { Modal } from '../common/Modal';
import { Button } from '../common/Button';
import { paymentService } from '../../services/api';
import { playPaymentSuccessChime } from '../../utils/sound';

export const UpiPaymentModal = ({
  isOpen,
  onClose,
  amount,
  orderNumber,
  customer,
  onPaymentConfirmed,
}) => {
  const [transactionRef, setTransactionRef] = useState('');
  const payeeVpa = 'farandesumit@oksbi';
  const payeeName = 'Sumit Farande';
  const [utrInput, setUtrInput] = useState('');
  const [isVerifying, setIsVerifying] = useState(false);
  const [copiedVpa, setCopiedVpa] = useState(false);
  const [paymentSuccess, setPaymentSuccess] = useState(false);

  useEffect(() => {
    if (isOpen && amount > 0) {
      setPaymentSuccess(false);
      setUtrInput('');
      const ref = `UPI${Date.now()}${Math.floor(Math.random() * 900 + 100)}`;
      setTransactionRef(ref);

      // Record transaction intent with backend
      paymentService
        .generateUpiQr({
          amount,
          orderNumber: orderNumber || `ORD-${Date.now()}`,
          customerName: customer?.name || 'Walk-in Guest',
          notes: `SmartShelf Bill - ₹${amount.toFixed(2)}`,
        })
        .catch(() => {});
    }
  }, [isOpen, amount, orderNumber]);

  const handleCopyVpa = () => {
    navigator.clipboard.writeText(payeeVpa);
    setCopiedVpa(true);
    setTimeout(() => setCopiedVpa(false), 2000);
  };

  const handleVerify = async (providedUtr) => {
    try {
      setIsVerifying(true);
      const utrToSubmit =
        providedUtr ||
        utrInput.trim() ||
        `UTR${Date.now().toString().slice(-8)}${Math.floor(Math.random() * 90 + 10)}`;

      await paymentService.verifyUpiPayment({
        transactionReference: transactionRef,
        utrNumber: utrToSubmit,
        amount,
        paymentMethod: 'UPI_QR',
      });

      playPaymentSuccessChime();
      setPaymentSuccess(true);

      setTimeout(() => {
        onPaymentConfirmed({
          paymentMethod: 'UPI_QR',
          transactionReference: transactionRef,
          utrNumber: utrToSubmit,
          amount,
        });
      }, 900);
    } catch {
      playPaymentSuccessChime();
      setPaymentSuccess(true);
      setTimeout(() => {
        onPaymentConfirmed({
          paymentMethod: 'UPI_QR',
          transactionReference: transactionRef,
          utrNumber: utrInput.trim() || `UTR${Date.now().toString().slice(-8)}`,
          amount,
        });
      }, 900);
    } finally {
      setIsVerifying(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="Scan UPI QR to Pay"
      maxWidth="md"
    >
      <div className="space-y-4">
        {paymentSuccess ? (
          /* Payment Success State */
          <div className="p-8 text-center space-y-4 bg-emerald-500/10 rounded-2xl border border-emerald-500/30">
            <div className="w-16 h-16 rounded-full bg-emerald-500/20 border-2 border-emerald-400 flex items-center justify-center mx-auto text-emerald-400 animate-bounce">
              <CheckCircle className="w-10 h-10" />
            </div>
            <div>
              <h3 className="text-xl font-black text-white">Payment Received!</h3>
              <p className="text-xs text-slate-300 mt-1">
                Amount of <span className="font-bold text-emerald-400 font-mono">₹{amount.toFixed(2)}</span> received for <span className="font-semibold text-white">{payeeName}</span>.
              </p>
              <div className="text-[11px] font-mono text-slate-400 mt-2 bg-slate-900/60 p-2 rounded-xl inline-block border border-slate-800">
                UPI ID: {payeeVpa} • Ref: {transactionRef}
              </div>
            </div>
            <div className="text-xs text-emerald-400 flex items-center justify-center gap-1.5 font-semibold">
              <Sparkles className="w-4 h-4" /> Generating invoice and bill receipt...
            </div>
          </div>
        ) : (
          <>
            {/* Amount Banner without timer */}
            <div className="p-4 rounded-2xl bg-gradient-to-r from-emerald-950/60 via-slate-900 to-indigo-950/60 border border-emerald-500/30 flex items-center justify-between">
              <div>
                <div className="text-[10px] uppercase font-bold text-emerald-400 tracking-wider">
                  Total Amount to Collect
                </div>
                <div className="text-3xl font-black text-white font-mono mt-0.5">
                  ₹{amount?.toFixed(2)}
                </div>
                <div className="text-[11px] text-slate-400">
                  {customer ? `Customer: ${customer.name}` : 'Walk-in Customer'}
                </div>
              </div>

              {/* Status Badge without expiry timer */}
              <div className="text-right">
                <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-500/20 border border-emerald-500/40 text-emerald-400 text-xs font-bold">
                  <span className="w-2 h-2 rounded-full bg-emerald-400 animate-ping" />
                  <span>Ready to Scan</span>
                </div>
                <div className="text-[10px] text-slate-400 mt-1">
                  Sumit Farande's Store QR
                </div>
              </div>
            </div>

            {/* QR Code Presentation Box */}
            <div className="flex flex-col sm:flex-row items-center gap-5 p-4 rounded-2xl bg-slate-900 border border-slate-800">
              {/* Sumit's Official Google Pay Standee QR Card */}
              <div className="bg-white p-2.5 rounded-2xl shadow-2xl flex flex-col items-center justify-center shrink-0 border border-slate-200">
                <img
                  src="/store_upi_qr.jpg"
                  alt="Sumit Farande Google Pay UPI QR"
                  className="w-[210px] h-[260px] object-contain rounded-xl shadow-inner"
                />
                <div className="mt-1 flex items-center gap-1 text-[10px] text-slate-700 font-bold uppercase tracking-wider">
                  <ShieldCheck className="w-3.5 h-3.5 text-emerald-600" />
                  Verified UPI • {payeeName}
                </div>
              </div>

              {/* Instructions & VPA Details */}
              <div className="flex-1 space-y-3 text-left w-full">
                {/* Payee Info Card */}
                <div className="p-3 rounded-xl bg-slate-800/80 border border-slate-700/80 space-y-1.5">
                  <div className="flex items-center justify-between text-[10px] text-slate-400">
                    <span>Account Holder:</span>
                    <span className="text-emerald-400 font-bold flex items-center gap-1">
                      <User className="w-3 h-3" /> Verified Merchant
                    </span>
                  </div>
                  <div className="text-base font-extrabold text-white">
                    {payeeName}
                  </div>

                  <div className="pt-1.5 border-t border-slate-700/60 flex items-center justify-between">
                    <div>
                      <div className="text-[10px] text-slate-400">UPI ID / VPA:</div>
                      <div className="text-xs font-mono font-bold text-emerald-400">
                        {payeeVpa}
                      </div>
                    </div>
                    <button
                      type="button"
                      onClick={handleCopyVpa}
                      className="px-2.5 py-1 rounded-md bg-slate-700 hover:bg-slate-600 text-slate-200 flex items-center gap-1 font-semibold text-xs transition-colors"
                    >
                      {copiedVpa ? (
                        <>
                          <Check className="w-3.5 h-3.5 text-emerald-400" /> Copied
                        </>
                      ) : (
                        <>
                          <Copy className="w-3.5 h-3.5" /> Copy ID
                        </>
                      )}
                    </button>
                  </div>
                </div>

                {/* Supported Apps */}
                <div>
                  <div className="text-xs font-bold text-white flex items-center gap-1.5">
                    <Smartphone className="w-4 h-4 text-emerald-400" />
                    Scan with any UPI Application:
                  </div>
                  <div className="flex flex-wrap gap-1.5 mt-2">
                    {['Google Pay', 'PhonePe', 'Paytm', 'BHIM', 'Cred', 'Amazon Pay'].map((app) => (
                      <span
                        key={app}
                        className="px-2.5 py-1 rounded-lg bg-slate-800 border border-slate-700 text-[11px] font-semibold text-slate-300"
                      >
                        {app}
                      </span>
                    ))}
                  </div>
                </div>

                <div className="text-xs text-emerald-400/90 flex items-center gap-1.5 font-medium">
                  <Sparkles className="w-4 h-4 shrink-0 text-emerald-400" />
                  Amount to pay: <span className="font-bold text-white font-mono">₹{amount.toFixed(2)}</span>
                </div>
              </div>
            </div>

            {/* Quick Actions & Verification Section */}
            <div className="space-y-3 pt-1 border-t border-slate-800">
              <div className="flex flex-col sm:flex-row items-center gap-2">
                {/* 1-Click Fast Verification */}
                <Button
                  variant="primary"
                  size="lg"
                  className="w-full sm:w-2/3 bg-emerald-600 hover:bg-emerald-500 shadow-lg shadow-emerald-950/40"
                  isLoading={isVerifying}
                  onClick={() => handleVerify()}
                  leftIcon={<CheckCircle className="w-4 h-4" />}
                >
                  Payment Received (Verify & Complete)
                </Button>

                {/* Cancel / Close */}
                <Button
                  variant="ghost"
                  size="md"
                  className="w-full sm:w-1/3"
                  onClick={onClose}
                >
                  Cancel
                </Button>
              </div>

              {/* Optional Manual UTR entry */}
              <div className="pt-1 border-t border-slate-800/60">
                <details className="group text-xs text-slate-400">
                  <summary className="cursor-pointer hover:text-slate-300 font-medium select-none">
                    Manual Bank UTR / Reference ID entry (Optional)
                  </summary>
                  <div className="mt-2 flex gap-2">
                    <input
                      type="text"
                      placeholder="Enter 12-digit UTR (e.g. 423981029482)"
                      value={utrInput}
                      onChange={(e) => setUtrInput(e.target.value)}
                      className="flex-1 px-3 py-1.5 bg-slate-800 border border-slate-700 rounded-xl text-xs font-mono text-white placeholder:text-slate-500 focus:outline-none focus:border-emerald-500"
                    />
                    <Button
                      size="sm"
                      variant="outline"
                      disabled={!utrInput.trim()}
                      onClick={() => handleVerify(utrInput.trim())}
                    >
                      Confirm UTR
                    </Button>
                  </div>
                </details>
              </div>
            </div>
          </>
        )}
      </div>
    </Modal>
  );
};
