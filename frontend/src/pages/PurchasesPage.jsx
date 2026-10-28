import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Truck, Plus } from 'lucide-react';
import { purchaseService } from '../services/api';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { useToast } from '../context/ToastContext';

export const PurchasesPage = () => {
  const { success, error } = useToast();
  const [purchases, setPurchases] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  // View Details Modal
  const [selectedPurchase, setSelectedPurchase] = useState(null);
  const [isViewModalOpen, setIsViewModalOpen] = useState(false);
  const [isReceiving, setIsReceiving] = useState(false);

  useEffect(() => {
    loadPurchases();
  }, []);

  const loadPurchases = async () => {
    try {
      setIsLoading(true);
      const data = await purchaseService.getAllPurchases();
      setPurchases(data || []);
    } catch (e) {
      console.error(e);
    } finally {
      setIsLoading(false);
    }
  };

  const handleMarkAsReceived = async (purchaseId, invoiceNo) => {
    try {
      setIsReceiving(true);
      await purchaseService.markAsReceived(purchaseId);
      success(`Purchase Order #${invoiceNo} received! Inventory restocked automatically.`);
      setIsViewModalOpen(false);
      loadPurchases();
    } catch (err) {
      error(err.response?.data?.message || 'Failed to receive purchase order');
    } finally {
      setIsReceiving(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white tracking-tight flex items-center gap-2.5">
            <Truck className="w-7 h-7 text-emerald-400" />
            Purchases & Restocking Orders
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Supplier purchase orders, invoice verification, and automatic stock level replenishment
          </p>
        </div>

        <Link to="/purchases/new">
          <Button variant="primary" leftIcon={<Plus className="w-4 h-4" />}>
            New Purchase Order
          </Button>
        </Link>
      </div>

      {/* Purchases Table */}
      <div className="bg-slate-900 border border-slate-800 rounded-3xl overflow-hidden shadow-2xl">
        {isLoading ? (
          <LoadingSpinner message="Loading purchase records..." />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="text-slate-400 uppercase tracking-wider bg-slate-900/80 border-b border-slate-800">
                <tr>
                  <th className="px-5 py-4 font-semibold">PO Number</th>
                  <th className="px-4 py-4 font-semibold">Supplier</th>
                  <th className="px-4 py-4 font-semibold">Date Ordered</th>
                  <th className="px-4 py-4 font-semibold text-center">Items Count</th>
                  <th className="px-4 py-4 font-semibold text-right">Total Cost</th>
                  <th className="px-4 py-4 font-semibold text-center">Status</th>
                  <th className="px-5 py-4 font-semibold text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {purchases.map((purchase) => (
                  <tr key={purchase.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-5 py-3.5 font-mono font-bold text-white">
                      {purchase.invoiceNumber}
                    </td>

                    <td className="px-4 py-3.5">
                      <div className="font-semibold text-white">{purchase.supplierName}</div>
                      {purchase.supplierCompany && (
                        <div className="text-[11px] text-slate-400">{purchase.supplierCompany}</div>
                      )}
                    </td>

                    <td className="px-4 py-3.5 font-mono text-slate-300">
                      {purchase.purchaseDate}
                    </td>

                    <td className="px-4 py-3.5 text-center font-mono text-slate-300">
                      {purchase.items?.length || 0} lines
                    </td>

                    <td className="px-4 py-3.5 text-right font-mono font-bold text-white">
                      ₹{purchase.totalAmount?.toFixed(2)}
                    </td>

                    <td className="px-4 py-3.5 text-center">
                      <Badge
                        variant={purchase.status === 'RECEIVED' ? 'success' : 'warning'}
                        size="sm"
                      >
                        {purchase.status === 'RECEIVED' ? '✓ Received & Stocked' : '⏳ Awaiting Delivery'}
                      </Badge>
                    </td>

                    <td className="px-5 py-3.5 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <Button
                          size="sm"
                          variant="ghost"
                          onClick={() => {
                            setSelectedPurchase(purchase);
                            setIsViewModalOpen(true);
                          }}
                        >
                          View Details
                        </Button>

                        {purchase.status === 'ORDERED' && (
                          <Button
                            size="sm"
                            variant="primary"
                            isLoading={isReceiving}
                            onClick={() => handleMarkAsReceived(purchase.id, purchase.invoiceNumber)}
                          >
                            Mark Received
                          </Button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}

                {purchases.length === 0 && (
                  <tr>
                    <td colSpan={7} className="px-5 py-12 text-center text-slate-400 text-xs">
                      No purchase orders recorded yet. Click "New Purchase Order" to restock your shelves.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* View Purchase Details Modal */}
      <Modal
        isOpen={isViewModalOpen}
        onClose={() => setIsViewModalOpen(false)}
        title={selectedPurchase ? `PO: ${selectedPurchase.invoiceNumber}` : 'Purchase Order'}
        subtitle={`Supplier: ${selectedPurchase?.supplierName} | Status: ${selectedPurchase?.status}`}
        maxWidth="lg"
      >
        {selectedPurchase && (
          <div className="space-y-4">
            <div className="p-4 rounded-xl bg-slate-800 border border-slate-700 grid grid-cols-2 gap-3 text-xs">
              <div>
                <span className="text-slate-400 block">Order Date:</span>
                <span className="font-mono text-white">{selectedPurchase.purchaseDate}</span>
              </div>
              <div>
                <span className="text-slate-400 block">Received Date:</span>
                <span className="font-mono text-white">
                  {selectedPurchase.receivedDate || 'Pending Delivery'}
                </span>
              </div>
            </div>

            {/* Line Items */}
            <div className="border border-slate-800 rounded-xl overflow-hidden">
              <table className="w-full text-left text-xs">
                <thead className="bg-slate-800 text-slate-400">
                  <tr>
                    <th className="p-3">Product</th>
                    <th className="p-3 text-center">Qty</th>
                    <th className="p-3 text-right">Unit Cost</th>
                    <th className="p-3 text-right">Line Total</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800">
                  {(selectedPurchase.items || []).map((item, idx) => (
                    <tr key={idx}>
                      <td className="p-3 text-white font-medium">{item.productName}</td>
                      <td className="p-3 text-center font-mono">{item.quantity}</td>
                      <td className="p-3 text-right font-mono text-slate-400">
                        ₹{item.unitCost?.toFixed(2)}
                      </td>
                      <td className="p-3 text-right font-mono font-bold text-white">
                        ₹{item.totalCost?.toFixed(2)}
                      </td>
                    </tr>
                  ))}
                </tbody>
                <tfoot className="bg-slate-800/60 font-bold">
                  <tr>
                    <td colSpan={3} className="p-3 text-right text-slate-300">
                      Total Invoice Amount:
                    </td>
                    <td className="p-3 text-right font-mono text-emerald-400 text-sm">
                      ₹{selectedPurchase.totalAmount?.toFixed(2)}
                    </td>
                  </tr>
                </tfoot>
              </table>
            </div>

            {selectedPurchase.notes && (
              <div className="text-xs text-slate-400">
                <span className="font-semibold text-slate-300">Notes: </span>
                {selectedPurchase.notes}
              </div>
            )}

            <div className="flex justify-end gap-2 pt-4 border-t border-slate-800">
              <Button variant="secondary" onClick={() => setIsViewModalOpen(false)}>
                Close
              </Button>
              {selectedPurchase.status === 'ORDERED' && (
                <Button
                  variant="primary"
                  isLoading={isReceiving}
                  onClick={() =>
                    handleMarkAsReceived(selectedPurchase.id, selectedPurchase.invoiceNumber)
                  }
                >
                  Confirm & Restock Shelf Inventory
                </Button>
              )}
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};
