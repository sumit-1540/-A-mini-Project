import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import {
  Package,
  Plus,
  Search,
  Upload,
  Edit2,
  Trash2,
  Barcode,
} from 'lucide-react';
import { productService, categoryService } from '../services/api';
import { Button } from '../components/common/Button';
import { StockStatusBadge } from '../components/common/Badge';
import { ProductFormModal } from '../components/products/ProductFormModal';
import { CsvImportModal } from '../components/products/CsvImportModal';
import { BarcodeLabelModal } from '../components/products/BarcodeLabelModal';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';

export const ProductsPage = () => {
  const [searchParams] = useSearchParams();
  const initialSearch = searchParams.get('search') || '';

  const { isAdmin } = useAuth();
  const { success, error } = useToast();

  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState(null);
  const [searchQuery, setSearchQuery] = useState(initialSearch);
  const [isLoading, setIsLoading] = useState(true);

  // Modals
  const [isFormModalOpen, setIsFormModalOpen] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [isCsvModalOpen, setIsCsvModalOpen] = useState(false);
  const [isBarcodeModalOpen, setIsBarcodeModalOpen] = useState(false);
  const [barcodeProduct, setBarcodeProduct] = useState(null);

  useEffect(() => {
    loadCategories();
    loadProducts();
  }, []);

  const loadCategories = async () => {
    try {
      const data = await categoryService.getAllCategories();
      setCategories(data || []);
    } catch (e) {
      console.error(e);
    }
  };

  const loadProducts = async (catId = selectedCategoryId, query = searchQuery) => {
    try {
      setIsLoading(true);
      const res = await productService.getProducts({
        categoryId: catId || undefined,
        search: query.trim() || undefined,
        size: 100,
      });
      setProducts(res?.content || []);
    } catch (e) {
      console.error(e);
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async (id, name) => {
    if (!window.confirm(`Are you sure you want to deactivate product: ${name}?`)) return;
    try {
      await productService.deleteProduct(id);
      success(`Product '${name}' deactivated.`);
      loadProducts();
    } catch {
      error('Failed to delete product');
    }
  };

  return (
    <div className="space-y-6">
      {/* Header Bar */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white tracking-tight flex items-center gap-2.5">
            <Package className="w-7 h-7 text-emerald-400" />
            Product Catalog
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Manage SKUs, barcodes, pricing, supplier links, and minimum inventory alerts
          </p>
        </div>

        {isAdmin && (
          <div className="flex items-center gap-2.5">
            <Button
              variant="secondary"
              leftIcon={<Upload className="w-4 h-4" />}
              onClick={() => setIsCsvModalOpen(true)}
            >
              Import CSV
            </Button>
            <Button
              variant="primary"
              leftIcon={<Plus className="w-4 h-4" />}
              onClick={() => {
                setSelectedProduct(null);
                setIsFormModalOpen(true);
              }}
            >
              Add Product
            </Button>
          </div>
        )}
      </div>

      {/* Filter and Search Controls */}
      <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl flex flex-col md:flex-row gap-3 items-center justify-between">
        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search by title, SKU, barcode, brand..."
            value={searchQuery}
            onChange={(e) => {
              setSearchQuery(e.target.value);
              loadProducts(selectedCategoryId, e.target.value);
            }}
            className="w-full pl-10 pr-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-xs text-white placeholder:text-slate-400 focus:outline-none focus:border-emerald-500"
          />
        </div>

        <div className="flex items-center gap-2 w-full md:w-auto overflow-x-auto pb-1">
          <button
            onClick={() => {
              setSelectedCategoryId(null);
              loadProducts(null, searchQuery);
            }}
            className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-all ${
              selectedCategoryId === null
                ? 'bg-emerald-600 text-white shadow-md shadow-emerald-950/40'
                : 'bg-slate-800 text-slate-300 hover:bg-slate-700'
            }`}
          >
            All Categories
          </button>
          {categories.map((c) => (
            <button
              key={c.id}
              onClick={() => {
                setSelectedCategoryId(c.id);
                loadProducts(c.id, searchQuery);
              }}
              className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-all ${
                selectedCategoryId === c.id
                  ? 'bg-emerald-600 text-white shadow-md shadow-emerald-950/40'
                  : 'bg-slate-800 text-slate-300 hover:bg-slate-700'
              }`}
            >
              {c.name}
            </button>
          ))}
        </div>
      </div>

      {/* Products Table Card */}
      <div className="bg-slate-900 border border-slate-800 rounded-3xl overflow-hidden shadow-2xl">
        {isLoading ? (
          <LoadingSpinner message="Loading product catalog..." />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="text-slate-400 uppercase tracking-wider bg-slate-900/80 border-b border-slate-800">
                <tr>
                  <th className="px-5 py-4 font-semibold">Product Name</th>
                  <th className="px-4 py-4 font-semibold">SKU & Barcode</th>
                  <th className="px-4 py-4 font-semibold">Category</th>
                  <th className="px-4 py-4 font-semibold text-right">Cost</th>
                  <th className="px-4 py-4 font-semibold text-right">Retail Price</th>
                  <th className="px-4 py-4 font-semibold text-center">Stock Level</th>
                  <th className="px-4 py-4 font-semibold text-center">Status</th>
                  <th className="px-4 py-4 font-semibold">Location</th>
                  {isAdmin && <th className="px-5 py-4 font-semibold text-right">Actions</th>}
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {products.map((product) => (
                  <tr key={product.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-5 py-3.5">
                      <div className="font-semibold text-white">{product.name}</div>
                      {product.brand && (
                        <div className="text-[11px] text-slate-400">{product.brand}</div>
                      )}
                    </td>

                    <td className="px-4 py-3.5 font-mono text-[11px]">
                      <div className="text-slate-300 font-bold">{product.sku}</div>
                      {product.barcode && (
                        <div className="text-slate-400 flex items-center gap-1">
                          <Barcode className="w-3 h-3 text-slate-400" /> {product.barcode}
                        </div>
                      )}
                    </td>

                    <td className="px-4 py-3.5 text-slate-300">
                      {product.categoryName || 'General'}
                    </td>

                    <td className="px-4 py-3.5 text-right font-mono text-slate-400">
                      ₹{product.purchasePrice?.toFixed(2)}
                    </td>

                    <td className="px-4 py-3.5 text-right font-mono font-bold text-white">
                      ₹{product.sellingPrice?.toFixed(2)}
                    </td>

                    <td className="px-4 py-3.5 text-center font-mono">
                      <span className="font-bold text-white">{product.currentStock}</span>{' '}
                      <span className="text-slate-400 text-[10px]">{product.unit}</span>
                    </td>

                    <td className="px-4 py-3.5 text-center">
                      <StockStatusBadge status={product.stockStatus} />
                    </td>

                    <td className="px-4 py-3.5 text-[11px] text-slate-400">
                      {product.aisle || product.shelf ? (
                        <span>
                          {product.aisle} {product.shelf ? `• ${product.shelf}` : ''}
                        </span>
                      ) : (
                        <span className="text-slate-600">—</span>
                      )}
                    </td>

                    {isAdmin && (
                      <td className="px-5 py-3.5 text-right">
                        <div className="flex items-center justify-end gap-1.5">
                          <button
                            onClick={() => {
                              setBarcodeProduct(product);
                              setIsBarcodeModalOpen(true);
                            }}
                            className="p-1.5 text-slate-400 hover:text-emerald-400 hover:bg-slate-800 rounded-lg transition-colors"
                            title="Print Shelf Barcode Sticker"
                          >
                            <Barcode className="w-3.5 h-3.5" />
                          </button>
                          <button
                            onClick={() => {
                              setSelectedProduct(product);
                              setIsFormModalOpen(true);
                            }}
                            className="p-1.5 text-slate-400 hover:text-emerald-400 hover:bg-slate-800 rounded-lg transition-colors"
                            title="Edit"
                          >
                            <Edit2 className="w-3.5 h-3.5" />
                          </button>
                          <button
                            onClick={() => handleDelete(product.id, product.name)}
                            className="p-1.5 text-slate-400 hover:text-rose-400 hover:bg-slate-800 rounded-lg transition-colors"
                            title="Deactivate"
                          >
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        </div>
                      </td>
                    )}
                  </tr>
                ))}

                {products.length === 0 && (
                  <tr>
                    <td colSpan={9} className="px-5 py-12 text-center text-slate-400 text-xs">
                      No products found. Click "Add Product" or "Import CSV" to start.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Form Modal */}
      <ProductFormModal
        isOpen={isFormModalOpen}
        onClose={() => setIsFormModalOpen(false)}
        product={selectedProduct}
        onSaved={loadProducts}
      />

      {/* CSV Import Modal */}
      <CsvImportModal
        isOpen={isCsvModalOpen}
        onClose={() => setIsCsvModalOpen(false)}
        onImportSuccess={loadProducts}
      />

      {/* Barcode Label Modal */}
      <BarcodeLabelModal
        isOpen={isBarcodeModalOpen}
        onClose={() => setIsBarcodeModalOpen(false)}
        product={barcodeProduct}
      />
    </div>
  );
};
