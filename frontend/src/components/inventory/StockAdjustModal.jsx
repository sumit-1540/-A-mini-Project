import React, { useState } from 'react';
import { inventoryService } from '../../services/api';
import { Modal } from '../common/Modal';
import { Button } from '../common/Button';
import { useToast } from '../../context/ToastContext';

export const StockAdjustModal = ({
  isOpen,
  onClose,
  item,
  onAdjusted,
}) => {
  const { success, error } = useToast();
  const [quantityChange, setQuantityChange] = useState(0);
  const [type, setType] = useState('ADJUSTMENT_MANUAL');
  const [reason, setReason] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  if (!item) return null;

  const currentStock = item.currentStock || 0;
  const projectedStock = currentStock + quantityChange;

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (quantityChange === 0) return;

    try {
      setIsLoading(true);
      await inventoryService.adjustStock({
        productId: item.productId,
        quantityChange,
        type,
        reason: reason.trim() || 'Manual stock adjustment',
      });
      success(`Stock for ${item.productName} updated! New stock: ${projectedStock} ${item.unit}`);
      onAdjusted();
      onClose();
    } catch (err) {
      error(err.response?.data?.message || 'Failed to adjust stock');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={`Adjust Stock: ${item.productName}`}
      subtitle={`SKU: ${item.sku} | Current Shelf Stock: ${item.currentStock} ${item.unit}`}
      maxWidth="md"
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-xs font-semibold text-slate-300 mb-1.5">Adjustment Type</label>
          <select
            value={type}
            onChange={(e) => setType(e.target.value)}
            className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
          >
            <option value="ADJUSTMENT_MANUAL">Manual Stock Count Correction (+ / -)</option>
            <option value="RESTOCK">Direct In-Store Restock (+)</option>
            <option value="DAMAGE_WRITE_OFF">Damaged / Broken Goods Write-off (-)</option>
            <option value="EXPIRED_WRITE_OFF">Expired Product Discard (-)</option>
            <option value="RETURN">Customer Return Back to Shelf (+)</option>
          </select>
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-300 mb-1.5">
            Quantity Change (Use negative for deduction, positive for addition) *
          </label>
          <input
            type="number"
            required
            value={quantityChange}
            onChange={(e) => setQuantityChange(Number(e.target.value))}
            className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white font-mono focus:outline-none focus:border-emerald-500"
          />
        </div>

        {/* Real-time calculated projection */}
        <div className="p-3.5 rounded-xl bg-slate-800/80 border border-slate-700 flex items-center justify-between text-xs">
          <span className="text-slate-400">Projected Stock After Adjustment:</span>
          <span
            className={`font-mono font-bold text-sm ${
              projectedStock < 0
                ? 'text-rose-400'
                : projectedStock <= item.minimumStock
                ? 'text-amber-400'
                : 'text-emerald-400'
            }`}
          >
            {projectedStock} {item.unit}
          </span>
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-300 mb-1.5">Reason / Note</label>
          <input
            type="text"
            placeholder="e.g. Physical inventory count verified, spilled item, etc."
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
          />
        </div>

        <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-800">
          <Button variant="secondary" type="button" onClick={onClose}>
            Cancel
          </Button>
          <Button
            variant="primary"
            type="submit"
            disabled={quantityChange === 0 || projectedStock < 0}
            isLoading={isLoading}
          >
            Apply Adjustment
          </Button>
        </div>
      </form>
    </Modal>
  );
};
