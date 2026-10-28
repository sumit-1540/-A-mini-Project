import React, { useState, useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import {
  Boxes,
  SlidersHorizontal,
  History,
  AlertTriangle,
  Clock,
  Search,
  Truck,
} from 'lucide-react';
import { inventoryService } from '../services/api';
import { Button } from '../components/common/Button';
import { Badge, StockStatusBadge } from '../components/common/Badge';
import { StockAdjustModal } from '../components/inventory/StockAdjustModal';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { useAuth } from '../context/AuthContext';

export const InventoryPage = () => {
  const [searchParams] = useSearchParams();
  const searchFilter = searchParams.get('search') || '';

  const { isAdmin } = useAuth();
  const [activeTab, setActiveTab] = useState('inventory');
  const [filterMode, setFilterMode] = useState('ALL');

  const [inventory, setInventory] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [searchQuery, setSearchQuery] = useState(searchFilter);
  const [isLoading, setIsLoading] = useState(true);

  // Adjustment Modal
  const [selectedItem, setSelectedItem] = useState(null);
  const [isAdjustModalOpen, setIsAdjustModalOpen] = useState(false);

  useEffect(() => {
    loadData();
  }, [filterMode, activeTab]);

  const loadData = async () => {
    try {
      setIsLoading(true);
      if (activeTab === 'inventory') {
        let items = [];
        if (filterMode === 'LOW') {
          items = await inventoryService.getLowStock();
        } else if (filterMode === 'EXPIRING') {
          items = await inventoryService.getExpiring(30);
        } else {
          items = await inventoryService.getAllInventory();
        }
        setInventory(items || []);
      } else {
        const txs = await inventoryService.getStockTransactions();
        setTransactions(txs || []);
      }
    } catch (e) {
      console.error('Failed to load inventory data:', e);
    } finally {
      setIsLoading(false);
    }
  };

  const filteredInventory = inventory.filter((item) => {
    if (!searchQuery.trim()) return true;
    const q = searchQuery.toLowerCase();
    return (
      item.productName?.toLowerCase().includes(q) ||
      item.sku?.toLowerCase().includes(q) ||
      (item.barcode && item.barcode.includes(q)) ||
      String(item.productId) === searchQuery.trim()
    );
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white tracking-tight flex items-center gap-2.5">
            <Boxes className="w-7 h-7 text-emerald-400" />
            Inventory & Shelf Management
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Real-time stock monitoring, shelf locations, write-offs, and stock audit logs
          </p>
        </div>

        <div className="flex items-center gap-2.5">
          <Link to="/purchases/new">
            <Button variant="primary" leftIcon={<Truck className="w-4 h-4" />}>
              Create Purchase Order
            </Button>
          </Link>
        </div>
      </div>

      {/* Navigation Tabs */}
      <div className="flex border-b border-slate-800 space-x-6 text-sm font-semibold">
        <button
          onClick={() => setActiveTab('inventory')}
          className={`pb-3 flex items-center gap-2 transition-colors relative ${
            activeTab === 'inventory' ? 'text-emerald-400' : 'text-slate-400 hover:text-white'
          }`}
        >
          <Boxes className="w-4 h-4" />
          <span>Shelf Stock Tracking</span>
          {activeTab === 'inventory' && (
            <span className="absolute bottom-0 left-0 right-0 h-0.5 bg-emerald-400 rounded-full" />
          )}
        </button>

        <button
          onClick={() => setActiveTab('transactions')}
          className={`pb-3 flex items-center gap-2 transition-colors relative ${
            activeTab === 'transactions' ? 'text-emerald-400' : 'text-slate-400 hover:text-white'
          }`}
        >
          <History className="w-4 h-4" />
          <span>Stock Audit Log</span>
          {activeTab === 'transactions' && (
            <span className="absolute bottom-0 left-0 right-0 h-0.5 bg-emerald-400 rounded-full" />
          )}
        </button>
      </div>

      {activeTab === 'inventory' ? (
        <div className="space-y-4">
          {/* Filter Bar */}
          <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl flex flex-col md:flex-row gap-3 items-center justify-between">
            <div className="relative w-full md:w-80">
              <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
              <input
                type="text"
                placeholder="Search inventory by title, SKU..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full pl-10 pr-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-xs text-white placeholder:text-slate-400 focus:outline-none focus:border-emerald-500"
              />
            </div>

            <div className="flex items-center gap-2 w-full md:w-auto overflow-x-auto pb-1">
              <button
                onClick={() => setFilterMode('ALL')}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-all ${
                  filterMode === 'ALL'
                    ? 'bg-emerald-600 text-white shadow-md shadow-emerald-950/40'
                    : 'bg-slate-800 text-slate-300 hover:bg-slate-700'
                }`}
              >
                All Shelf Stock
              </button>

              <button
                onClick={() => setFilterMode('LOW')}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-all flex items-center gap-1.5 ${
                  filterMode === 'LOW'
                    ? 'bg-amber-600 text-white shadow-md shadow-amber-950/40'
                    : 'bg-slate-800 text-amber-400 hover:bg-slate-700'
                }`}
              >
                <AlertTriangle className="w-3.5 h-3.5" />
                Low Stock
              </button>

              <button
                onClick={() => setFilterMode('EXPIRING')}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-all flex items-center gap-1.5 ${
                  filterMode === 'EXPIRING'
                    ? 'bg-purple-600 text-white shadow-md shadow-purple-950/40'
                    : 'bg-slate-800 text-purple-400 hover:bg-slate-700'
                }`}
              >
                <Clock className="w-3.5 h-3.5" />
                Expiring (&le; 30 Days)
              </button>
            </div>
          </div>

          {/* Table */}
          <div className="bg-slate-900 border border-slate-800 rounded-3xl overflow-hidden shadow-2xl">
            {isLoading ? (
              <LoadingSpinner message="Calculating stock balance..." />
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs">
                  <thead className="text-slate-400 uppercase tracking-wider bg-slate-900/80 border-b border-slate-800">
                    <tr>
                      <th className="px-5 py-4 font-semibold">Product</th>
                      <th className="px-4 py-4 font-semibold">Location</th>
                      <th className="px-4 py-4 font-semibold text-center">Available Stock</th>
                      <th className="px-4 py-4 font-semibold text-center">Min Level</th>
                      <th className="px-4 py-4 font-semibold text-center">Status</th>
                      <th className="px-4 py-4 font-semibold">Expiry Date</th>
                      <th className="px-4 py-4 font-semibold">Supplier</th>
                      {isAdmin && <th className="px-5 py-4 font-semibold text-right">Adjustment</th>}
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-800/60">
                    {filteredInventory.map((item) => (
                      <tr key={item.id} className="hover:bg-slate-800/40 transition-colors">
                        <td className="px-5 py-3.5">
                          <div className="font-semibold text-white">{item.productName}</div>
                          <div className="text-[11px] text-slate-400 font-mono">SKU: {item.sku}</div>
                        </td>

                        <td className="px-4 py-3.5 text-slate-300">
                          {item.aisle || item.shelf ? (
                            <span className="font-mono text-[11px] bg-slate-800 px-2 py-0.5 rounded border border-slate-700">
                              {item.aisle || 'Aisle'} {item.shelf ? `/ ${item.shelf}` : ''}
                            </span>
                          ) : (
                            <span className="text-slate-600">Unassigned</span>
                          )}
                        </td>

                        <td className="px-4 py-3.5 text-center font-mono">
                          <span className="font-bold text-white text-sm">{item.currentStock}</span>{' '}
                          <span className="text-slate-400 text-[10px]">{item.unit}</span>
                        </td>

                        <td className="px-4 py-3.5 text-center font-mono text-slate-400">
                          {item.minimumStock} {item.unit}
                        </td>

                        <td className="px-4 py-3.5 text-center">
                          <StockStatusBadge status={item.status} />
                        </td>

                        <td className="px-4 py-3.5">
                          {item.expiryDate ? (
                            <div>
                              <div className="font-mono text-slate-300">{item.expiryDate}</div>
                              {item.daysUntilExpiry !== undefined && (
                                <div
                                  className={`text-[10px] font-semibold ${
                                    item.daysUntilExpiry <= 7
                                      ? 'text-rose-400'
                                      : item.daysUntilExpiry <= 30
                                      ? 'text-amber-400'
                                      : 'text-slate-400'
                                  }`}
                                >
                                  {item.daysUntilExpiry < 0
                                    ? 'Expired'
                                    : item.daysUntilExpiry === 0
                                    ? 'Expires Today'
                                    : `In ${item.daysUntilExpiry} days`}
                                </div>
                              )}
                            </div>
                          ) : (
                            <span className="text-slate-600">—</span>
                          )}
                        </td>

                        <td className="px-4 py-3.5 text-slate-400 text-[11px]">
                          {item.supplierName || 'Wholesale Direct'}
                        </td>

                        {isAdmin && (
                          <td className="px-5 py-3.5 text-right">
                            <Button
                              size="sm"
                              variant="outline"
                              leftIcon={<SlidersHorizontal className="w-3.5 h-3.5" />}
                              onClick={() => {
                                setSelectedItem(item);
                                setIsAdjustModalOpen(true);
                              }}
                            >
                              Adjust Stock
                            </Button>
                          </td>
                        )}
                      </tr>
                    ))}

                    {filteredInventory.length === 0 && (
                      <tr>
                        <td colSpan={8} className="px-5 py-12 text-center text-slate-400 text-xs">
                          No inventory items match the selected filter.
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      ) : (
        /* Stock Audit Log Table */
        <div className="bg-slate-900 border border-slate-800 rounded-3xl overflow-hidden shadow-2xl">
          {isLoading ? (
            <LoadingSpinner message="Loading audit transaction trail..." />
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead className="text-slate-400 uppercase tracking-wider bg-slate-900/80 border-b border-slate-800">
                  <tr>
                    <th className="px-5 py-4 font-semibold">Timestamp</th>
                    <th className="px-4 py-4 font-semibold">Product</th>
                    <th className="px-4 py-4 font-semibold">Type</th>
                    <th className="px-4 py-4 font-semibold text-center">Qty Change</th>
                    <th className="px-4 py-4 font-semibold text-center">Prev Stock</th>
                    <th className="px-4 py-4 font-semibold text-center">New Stock</th>
                    <th className="px-4 py-4 font-semibold">Reason / Ref</th>
                    <th className="px-4 py-4 font-semibold text-right">Performed By</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/60 font-mono">
                  {transactions.map((tx) => (
                    <tr key={tx.id} className="hover:bg-slate-800/40 transition-colors">
                      <td className="px-5 py-3.5 text-slate-400 text-[11px]">
                        {new Date(tx.createdAt).toLocaleString()}
                      </td>

                      <td className="px-4 py-3.5 font-sans font-semibold text-white">
                        {tx.productName}
                      </td>

                      <td className="px-4 py-3.5 font-sans">
                        <Badge
                          variant={
                            tx.type === 'SALE'
                              ? 'default'
                              : tx.type === 'RESTOCK'
                              ? 'success'
                              : tx.type?.includes('WRITE_OFF')
                              ? 'danger'
                              : 'warning'
                          }
                          size="sm"
                        >
                          {tx.type}
                        </Badge>
                      </td>

                      <td
                        className={`px-4 py-3.5 text-center font-bold ${
                          tx.quantityChange > 0 ? 'text-emerald-400' : 'text-rose-400'
                        }`}
                      >
                        {tx.quantityChange > 0 ? `+${tx.quantityChange}` : tx.quantityChange}
                      </td>

                      <td className="px-4 py-3.5 text-center text-slate-400">{tx.previousStock}</td>

                      <td className="px-4 py-3.5 text-center font-bold text-white">{tx.newStock}</td>

                      <td className="px-4 py-3.5 font-sans text-slate-400 text-[11px]">
                        {tx.reason}
                        {tx.referenceId && ` (${tx.referenceId})`}
                      </td>

                      <td className="px-4 py-3.5 text-right font-sans text-slate-400 text-[11px]">
                        {tx.performedBy || 'System'}
                      </td>
                    </tr>
                  ))}

                  {transactions.length === 0 && (
                    <tr>
                      <td colSpan={8} className="px-5 py-12 text-center text-slate-400 text-xs font-sans">
                        No stock adjustment audit records found.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* Stock Adjust Modal */}
      <StockAdjustModal
        isOpen={isAdjustModalOpen}
        onClose={() => setIsAdjustModalOpen(false)}
        item={selectedItem}
        onAdjusted={loadData}
      />
    </div>
  );
};
