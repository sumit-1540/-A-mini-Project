import React, { useState } from 'react';
import { Settings as SettingsIcon, Store, Printer, Save } from 'lucide-react';
import { Button } from '../components/common/Button';
import { useToast } from '../context/ToastContext';

export const SettingsPage = () => {
  const { success } = useToast();

  const [shopName, setShopName] = useState('SmartShelf Supermarket');
  const [tagline, setTagline] = useState('Quality Groceries & Daily Needs');
  const [address, setAddress] = useState('Shop No. 12-14, Green Park Main Market, New Delhi - 110016');
  const [phone, setPhone] = useState('+91 98100 12345');
  const [gstin, setGstin] = useState('07AAAAA0000A1Z5');
  const [receiptFooter, setReceiptFooter] = useState('THANK YOU FOR SHOPPING! Visit Again.');
  const [loyaltyRate, setLoyaltyRate] = useState(1);

  const handleSave = (e) => {
    e.preventDefault();
    success('Store settings saved successfully!');
  };

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-extrabold text-white tracking-tight flex items-center gap-2.5">
          <SettingsIcon className="w-7 h-7 text-emerald-400" />
          Store Profile & Settings
        </h1>
        <p className="text-xs text-slate-400 mt-1">
          Configure business details, GSTIN, receipt invoice header, and loyalty program rules
        </p>
      </div>

      <form onSubmit={handleSave} className="space-y-6">
        {/* Store Info */}
        <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
          <h3 className="text-base font-bold text-white flex items-center gap-2">
            <Store className="w-5 h-5 text-emerald-400" />
            General Supermarket Information
          </h3>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">Shop / Store Name *</label>
              <input
                type="text"
                required
                value={shopName}
                onChange={(e) => setShopName(e.target.value)}
                className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">Store Tagline</label>
              <input
                type="text"
                value={tagline}
                onChange={(e) => setTagline(e.target.value)}
                className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">Customer Support Phone *</label>
              <input
                type="text"
                required
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white font-mono focus:outline-none focus:border-emerald-500"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">Store GSTIN Number *</label>
              <input
                type="text"
                required
                value={gstin}
                onChange={(e) => setGstin(e.target.value.toUpperCase())}
                className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white font-mono uppercase focus:outline-none focus:border-emerald-500"
              />
            </div>

            <div className="md:col-span-2">
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">Store Address (Appears on Tax Invoices) *</label>
              <textarea
                rows={2}
                required
                value={address}
                onChange={(e) => setAddress(e.target.value)}
                className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
              />
            </div>
          </div>
        </div>

        {/* Invoice & Receipt Customization */}
        <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
          <h3 className="text-base font-bold text-white flex items-center gap-2">
            <Printer className="w-5 h-5 text-emerald-400" />
            Thermal POS Receipt Customization
          </h3>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="md:col-span-2">
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">Receipt Footer Message</label>
              <input
                type="text"
                value={receiptFooter}
                onChange={(e) => setReceiptFooter(e.target.value)}
                className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">
                Loyalty Point Ratio (Points per ₹100 spent)
              </label>
              <input
                type="number"
                min="0"
                value={loyaltyRate}
                onChange={(e) => setLoyaltyRate(Number(e.target.value))}
                className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white font-mono focus:outline-none focus:border-emerald-500"
              />
            </div>
          </div>
        </div>

        <div className="flex justify-end">
          <Button variant="primary" size="lg" type="submit" leftIcon={<Save className="w-4 h-4" />}>
            Save Store Configuration
          </Button>
        </div>
      </form>
    </div>
  );
};
