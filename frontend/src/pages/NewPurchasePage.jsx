import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams, Link } from 'react-router-dom';
import { Plus, Trash2, ArrowLeft } from 'lucide-react';
import { supplierService, productService, purchaseService } from '../services/api';
import { Button } from '../components/common/Button';
import { useToast } from '../context/ToastContext';

export const NewPurchasePage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const preselectedProductId = searchParams.get('productId');

  const { success, error } = useToast();
  const [suppliers, setSuppliers] = useState([]);
  const [products, setProducts] = useState([]);

  const [supplierId, setSupplierId] = useState('');
  const [invoiceNumber, setInvoiceNumber] = useState(
    'PO-' + Math.floor(100000 + Math.random() * 900000)
  );
  const [purchaseDate, setPurchaseDate] = useState(new Date().toISOString().split('T')[0]);
  const [notes, setNotes] = useState('');
  const [items, setItems] = useState([]);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [supList, prodList] = await Promise.all([
        supplierService.getActiveSuppliers(),
        productService.searchActiveProducts(),
      ]);
      setSuppliers(supList || []);
      setProducts(prodList || []);

      if (supList && supList.length > 0) {
        setSupplierId(supList[0].id);
      }

      if (preselectedProductId) {
        const match = (prodList || []).find((p) => String(p.id) === preselectedProductId);
        if (match) {
          setItems([
            {
              productId: match.id,
              productName: match.name,
              quantity: 20,
              unitCost: match.purchasePrice,
            },
          ]);
          if (match.supplierId) {
            setSupplierId(match.supplierId);
          }
        }
      } else if (prodList && prodList.length > 0) {
        setItems([
          {
            productId: prodList[0].id,
            productName: prodList[0].name,
            quantity: 10,
            unitCost: prodList[0].purchasePrice,
          },
        ]);
      }
    } catch (e) {
      console.error(e);
    }
  };

  const handleAddItem = () => {
    if (products.length === 0) return;
    const p = products[0];
    setItems((prev) => [
      ...prev,
      {
        productId: p.id,
        productName: p.name,
        quantity: 10,
        unitCost: p.purchasePrice,
      },
    ]);
  };

  const handleProductChange = (index, newProductId) => {
    const p = products.find((prod) => prod.id === newProductId);
    if (!p) return;
    setItems((prev) =>
      prev.map((item, i) =>
        i === index
          ? {
              ...item,
              productId: p.id,
              productName: p.name,
              unitCost: p.purchasePrice,
            }
          : item
      )
    );
  };

  const handleQuantityChange = (index, qty) => {
    setItems((prev) =>
      prev.map((item, i) => (i === index ? { ...item, quantity: Math.max(1, qty) } : item))
    );
  };

  const handleCostChange = (index, cost) => {
    setItems((prev) =>
      prev.map((item, i) => (i === index ? { ...item, unitCost: Math.max(0, cost) } : item))
    );
  };

  const handleRemoveItem = (index) => {
    setItems((prev) => prev.filter((_, i) => i !== index));
  };

  const totalCost = items.reduce((sum, item) => sum + item.quantity * item.unitCost, 0);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!supplierId || items.length === 0) {
      error('Please select a supplier and add at least one line item');
      return;
    }

    try {
      setIsSubmitting(true);
      await purchaseService.createPurchase({
        supplierId: Number(supplierId),
        invoiceNumber: invoiceNumber.trim() || undefined,
        purchaseDate,
        notes: notes.trim() || undefined,
        items: items.map((i) => ({
          productId: i.productId,
          quantity: i.quantity,
          unitCost: i.unitCost,
        })),
      });

      success(`Purchase Order #${invoiceNumber} created successfully!`);
      navigate('/purchases');
    } catch (err) {
      error(err.response?.data?.message || 'Failed to create purchase order');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      {/* Top Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <Link to="/purchases" className="p-2 text-slate-400 hover:text-white rounded-xl bg-slate-900 border border-slate-800">
            <ArrowLeft className="w-4 h-4" />
          </Link>
          <div>
            <h1 className="text-2xl font-extrabold text-white tracking-tight">
              Create Restocking Purchase Order
            </h1>
            <p className="text-xs text-slate-400">
              Procure wholesale items from suppliers and automatically update purchase cost prices
            </p>
          </div>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* PO Meta Card */}
        <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">Supplier / Dealer *</label>
              <select
                required
                value={supplierId}
                onChange={(e) => setSupplierId(Number(e.target.value))}
                className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
              >
                {suppliers.map((s) => (
                  <option key={s.id} value={s.id}>
                    {s.name} ({s.company || 'Wholesale'})
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">PO / Invoice Number</label>
              <input
                type="text"
                value={invoiceNumber}
                onChange={(e) => setInvoiceNumber(e.target.value)}
                className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white font-mono focus:outline-none focus:border-emerald-500"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">Order Date *</label>
              <input
                type="date"
                required
                value={purchaseDate}
                onChange={(e) => setPurchaseDate(e.target.value)}
                className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
              />
            </div>
          </div>
        </div>

        {/* Line Items Card */}
        <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-bold text-white">Line Items & Quantities</h3>
            <Button
              type="button"
              variant="outline"
              size="sm"
              leftIcon={<Plus className="w-3.5 h-3.5" />}
              onClick={handleAddItem}
            >
              Add Product Line
            </Button>
          </div>

          <div className="space-y-3">
            {items.map((item, idx) => (
              <div
                key={idx}
                className="p-3 rounded-2xl bg-slate-800/80 border border-slate-700/80 grid grid-cols-1 sm:grid-cols-12 gap-3 items-center"
              >
                <div className="sm:col-span-5">
                  <label className="text-[10px] font-semibold text-slate-400 block mb-1">Product</label>
                  <select
                    value={item.productId}
                    onChange={(e) => handleProductChange(idx, Number(e.target.value))}
                    className="w-full px-3 py-1.5 bg-slate-900 border border-slate-700 rounded-xl text-xs text-white focus:outline-none focus:border-emerald-500"
                  >
                    {products.map((p) => (
                      <option key={p.id} value={p.id}>
                        {p.name} (Stock: {p.currentStock} {p.unit})
                      </option>
                    ))}
                  </select>
                </div>

                <div className="sm:col-span-2">
                  <label className="text-[10px] font-semibold text-slate-400 block mb-1">Order Qty</label>
                  <input
                    type="number"
                    min="1"
                    required
                    value={item.quantity}
                    onChange={(e) => handleQuantityChange(idx, Number(e.target.value))}
                    className="w-full px-3 py-1.5 bg-slate-900 border border-slate-700 rounded-xl text-xs font-mono text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>

                <div className="sm:col-span-2">
                  <label className="text-[10px] font-semibold text-slate-400 block mb-1">Unit Cost (₹)</label>
                  <input
                    type="number"
                    step="0.01"
                    min="0"
                    required
                    value={item.unitCost}
                    onChange={(e) => handleCostChange(idx, Number(e.target.value))}
                    className="w-full px-3 py-1.5 bg-slate-900 border border-slate-700 rounded-xl text-xs font-mono text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>

                <div className="sm:col-span-2 text-right">
                  <label className="text-[10px] text-slate-400 block mb-1">Line Total</label>
                  <div className="font-mono font-bold text-white text-xs">
                    ₹{(item.quantity * item.unitCost).toFixed(2)}
                  </div>
                </div>

                <div className="sm:col-span-1 flex justify-end">
                  <button
                    type="button"
                    onClick={() => handleRemoveItem(idx)}
                    className="p-2 text-slate-500 hover:text-rose-400 transition-colors"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* Grand Total */}
          <div className="pt-4 border-t border-slate-800 flex justify-between items-center text-sm font-bold text-white">
            <span>Estimated Total Purchase Cost:</span>
            <span className="text-xl font-mono font-black text-emerald-400">
              ₹{totalCost.toFixed(2)}
            </span>
          </div>
        </div>

        {/* Submit */}
        <div className="flex justify-end gap-3">
          <Button variant="secondary" type="button" onClick={() => navigate('/purchases')}>
            Cancel
          </Button>
          <Button variant="primary" type="submit" size="lg" isLoading={isSubmitting}>
            Create Purchase Order
          </Button>
        </div>
      </form>
    </div>
  );
};
