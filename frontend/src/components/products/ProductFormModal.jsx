import React, { useState, useEffect } from 'react';
import { productService, categoryService, supplierService } from '../../services/api';
import { Modal } from '../common/Modal';
import { Button } from '../common/Button';
import { useToast } from '../../context/ToastContext';

export const ProductFormModal = ({
  isOpen,
  onClose,
  product,
  onSaved,
}) => {
  const { success, error } = useToast();
  const [categories, setCategories] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  const [formData, setFormData] = useState({
    name: '',
    sku: '',
    barcode: '',
    categoryId: 1,
    brand: '',
    unit: 'pcs',
    purchasePrice: 0,
    sellingPrice: 0,
    initialStock: 0,
    minimumStock: 5,
    expiryDate: '',
    supplierId: undefined,
    description: '',
    imageUrl: '',
    aisle: '',
    shelf: '',
  });

  useEffect(() => {
    if (isOpen) {
      loadDependencies();
      if (product) {
        setFormData({
          name: product.name,
          sku: product.sku,
          barcode: product.barcode || '',
          categoryId: product.categoryId || 1,
          brand: product.brand || '',
          unit: product.unit || 'pcs',
          purchasePrice: product.purchasePrice,
          sellingPrice: product.sellingPrice,
          initialStock: product.currentStock,
          minimumStock: product.minimumStock,
          expiryDate: product.expiryDate || '',
          supplierId: product.supplierId,
          description: product.description || '',
          imageUrl: product.imageUrl || '',
          aisle: product.aisle || '',
          shelf: product.shelf || '',
        });
      } else {
        setFormData({
          name: '',
          sku: 'SKU-' + Math.floor(100000 + Math.random() * 900000),
          barcode: '',
          categoryId: 1,
          brand: '',
          unit: 'pcs',
          purchasePrice: 0,
          sellingPrice: 0,
          initialStock: 0,
          minimumStock: 5,
          expiryDate: '',
          supplierId: undefined,
          description: '',
          imageUrl: '',
          aisle: '',
          shelf: '',
        });
      }
    }
  }, [isOpen, product]);

  const loadDependencies = async () => {
    try {
      const [catList, supList] = await Promise.all([
        categoryService.getActiveCategories(),
        supplierService.getActiveSuppliers(),
      ]);
      setCategories(catList || []);
      setSuppliers(supList || []);
      if (catList && catList.length > 0 && !product) {
        setFormData((prev) => ({ ...prev, categoryId: catList[0].id }));
      }
    } catch (e) {
      console.error(e);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setIsLoading(true);
      if (product) {
        await productService.updateProduct(product.id, formData);
        success('Product updated successfully!');
      } else {
        await productService.createProduct(formData);
        success('Product created successfully!');
      }
      onSaved();
      onClose();
    } catch (err) {
      error(err.response?.data?.message || 'Failed to save product');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={product ? `Edit Product: ${product.name}` : 'Add New Grocery Product'}
      subtitle="Configure pricing, SKU barcodes, inventory reorder levels and shelf placement"
      maxWidth="2xl"
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="md:col-span-2">
            <label className="block text-xs font-semibold text-slate-300 mb-1">Product Title *</label>
            <input
              type="text"
              required
              placeholder="e.g. India Gate Basmati Rice 5kg"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">SKU / Item Code *</label>
            <input
              type="text"
              required
              value={formData.sku}
              onChange={(e) => setFormData({ ...formData, sku: e.target.value })}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white font-mono uppercase focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Barcode (EAN / UPC / QR)</label>
            <input
              type="text"
              placeholder="Scan or type barcode"
              value={formData.barcode}
              onChange={(e) => setFormData({ ...formData, barcode: e.target.value })}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white font-mono focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Category *</label>
            <select
              required
              value={formData.categoryId}
              onChange={(e) => setFormData({ ...formData, categoryId: Number(e.target.value) })}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
            >
              {categories.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Brand</label>
            <input
              type="text"
              placeholder="e.g. Amul, Tata, ITC"
              value={formData.brand}
              onChange={(e) => setFormData({ ...formData, brand: e.target.value })}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Unit of Measure *</label>
            <select
              value={formData.unit}
              onChange={(e) => setFormData({ ...formData, unit: e.target.value })}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
            >
              <option value="pcs">Pieces (pcs)</option>
              <option value="kg">Kilograms (kg)</option>
              <option value="g">Grams (g)</option>
              <option value="L">Liters (L)</option>
              <option value="ml">Milliliters (ml)</option>
              <option value="pack">Pack / Packet</option>
              <option value="bag">Bag</option>
              <option value="box">Box</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Supplier / Dealer</label>
            <select
              value={formData.supplierId || ''}
              onChange={(e) =>
                setFormData({
                  ...formData,
                  supplierId: e.target.value ? Number(e.target.value) : undefined,
                })
              }
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
            >
              <option value="">-- Direct Wholesale / None --</option>
              {suppliers.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.name}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Purchase Cost (₹) *</label>
            <input
              type="number"
              step="0.01"
              required
              min="0"
              value={formData.purchasePrice}
              onChange={(e) => setFormData({ ...formData, purchasePrice: Number(e.target.value) })}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white font-mono focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Retail Selling Price (₹) *</label>
            <input
              type="number"
              step="0.01"
              required
              min="0"
              value={formData.sellingPrice}
              onChange={(e) => setFormData({ ...formData, sellingPrice: Number(e.target.value) })}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white font-mono focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">
              {product ? 'Current Stock Qty' : 'Initial Stock Qty'} *
            </label>
            <input
              type="number"
              required
              min="0"
              value={formData.initialStock}
              onChange={(e) => setFormData({ ...formData, initialStock: Number(e.target.value) })}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white font-mono focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Low-Stock Alert Level *</label>
            <input
              type="number"
              required
              min="1"
              value={formData.minimumStock}
              onChange={(e) => setFormData({ ...formData, minimumStock: Number(e.target.value) })}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white font-mono focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Expiry Date</label>
            <input
              type="date"
              value={formData.expiryDate || ''}
              onChange={(e) => setFormData({ ...formData, expiryDate: e.target.value })}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div className="grid grid-cols-2 gap-2">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Aisle</label>
              <input
                type="text"
                placeholder="e.g. Aisle 2"
                value={formData.aisle}
                onChange={(e) => setFormData({ ...formData, aisle: e.target.value })}
                className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Shelf / Rack</label>
              <input
                type="text"
                placeholder="e.g. Shelf B1"
                value={formData.shelf}
                onChange={(e) => setFormData({ ...formData, shelf: e.target.value })}
                className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
              />
            </div>
          </div>
        </div>

        <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-800">
          <Button variant="secondary" type="button" onClick={onClose}>
            Cancel
          </Button>
          <Button variant="primary" type="submit" isLoading={isLoading}>
            {product ? 'Update Product' : 'Save Product'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};
