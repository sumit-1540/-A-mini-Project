import React, { useState, useEffect } from 'react';
import { supplierService } from '../../services/api';
import { Modal } from '../common/Modal';
import { Button } from '../common/Button';
import { useToast } from '../../context/ToastContext';

export const SupplierFormModal = ({
  isOpen,
  onClose,
  supplier,
  onSaved,
}) => {
  const { success, error } = useToast();
  const [name, setName] = useState('');
  const [company, setCompany] = useState('');
  const [phone, setPhone] = useState('');
  const [email, setEmail] = useState('');
  const [address, setAddress] = useState('');
  const [gstNumber, setGstNumber] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (supplier) {
      setName(supplier.name || '');
      setCompany(supplier.company || '');
      setPhone(supplier.phone || '');
      setEmail(supplier.email || '');
      setAddress(supplier.address || '');
      setGstNumber(supplier.gstNumber || '');
    } else {
      setName('');
      setCompany('');
      setPhone('');
      setEmail('');
      setAddress('');
      setGstNumber('');
    }
  }, [supplier, isOpen]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!name.trim()) return;

    try {
      setIsLoading(true);
      const payload = {
        name: name.trim(),
        company: company.trim() || undefined,
        phone: phone.trim() || undefined,
        email: email.trim() || undefined,
        address: address.trim() || undefined,
        gstNumber: gstNumber.trim() || undefined,
        active: true,
      };

      if (supplier) {
        await supplierService.updateSupplier(supplier.id, payload);
        success('Supplier details updated!');
      } else {
        await supplierService.createSupplier(payload);
        success('New supplier registered!');
      }
      onSaved();
      onClose();
    } catch (err) {
      error(err.response?.data?.message || 'Failed to save supplier');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={supplier ? `Edit Supplier: ${supplier.name}` : 'Register New Supplier / Dealer'}
      subtitle="Manage wholesale contacts, company details, GSTIN and reorder channels"
      maxWidth="md"
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-xs font-semibold text-slate-300 mb-1.5">Supplier / Dealer Name *</label>
          <input
            type="text"
            required
            placeholder="e.g. Ramesh Wholesale Distributors"
            value={name}
            onChange={(e) => setName(e.target.value)}
            className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-300 mb-1.5">Company / Firm Name</label>
          <input
            type="text"
            placeholder="e.g. Ramesh Agro Traders Pvt Ltd"
            value={company}
            onChange={(e) => setCompany(e.target.value)}
            className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
          />
        </div>

        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">Phone Number</label>
            <input
              type="tel"
              placeholder="+91 98450 11223"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white font-mono focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">GSTIN Number</label>
            <input
              type="text"
              placeholder="27AABCR1234F1Z8"
              value={gstNumber}
              onChange={(e) => setGstNumber(e.target.value.toUpperCase())}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white font-mono uppercase focus:outline-none focus:border-emerald-500"
            />
          </div>
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-300 mb-1.5">Email Address</label>
          <input
            type="email"
            placeholder="orders@rameshwholesale.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-300 mb-1.5">Godown / Office Address</label>
          <textarea
            rows={2}
            placeholder="Plot 45, APMC Market Yard, Navi Mumbai"
            value={address}
            onChange={(e) => setAddress(e.target.value)}
            className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
          />
        </div>

        <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-800">
          <Button variant="secondary" type="button" onClick={onClose}>
            Cancel
          </Button>
          <Button variant="primary" type="submit" isLoading={isLoading}>
            {supplier ? 'Update Supplier' : 'Register Supplier'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};
