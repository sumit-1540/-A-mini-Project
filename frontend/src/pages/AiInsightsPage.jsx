import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import {
  Sparkles,
  Tag,
  Truck,
  TrendingDown,
  ArrowRight,
} from 'lucide-react';
import { aiService, productService } from '../services/api';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { useToast } from '../context/ToastContext';

export const AiInsightsPage = () => {
  const navigate = useNavigate();
  const { success, error } = useToast();

  const [discounts, setDiscounts] = useState([]);
  const [reorders, setReorders] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [applyingDiscountId, setApplyingDiscountId] = useState(null);

  useEffect(() => {
    loadAiInsights();
  }, []);

  const loadAiInsights = async () => {
    try {
      setIsLoading(true);
      const [discList, reorderList] = await Promise.all([
        aiService.getSmartDiscounts(),
        aiService.getSmartReorders(),
      ]);
      setDiscounts(discList || []);
      setReorders(reorderList || []);
    } catch (e) {
      console.error(e);
    } finally {
      setIsLoading(false);
    }
  };

  const handleApplyMarkdown = async (item) => {
    try {
      setApplyingDiscountId(item.productId);
      const product = await productService.getProductById(item.productId);
      await productService.updateProduct(item.productId, {
        name: product.name,
        sku: product.sku,
        barcode: product.barcode,
        categoryId: product.categoryId || 1,
        brand: product.brand,
        unit: product.unit,
        purchasePrice: product.purchasePrice,
        sellingPrice: item.discountedPrice,
        initialStock: product.currentStock,
        minimumStock: product.minimumStock,
        expiryDate: product.expiryDate,
        supplierId: product.supplierId,
        description: product.description,
        imageUrl: product.imageUrl,
        aisle: product.aisle,
        shelf: product.shelf,
      });

      success(
        `Applied ${item.recommendedDiscountPercent}% Markdown! New Price: ₹${item.discountedPrice?.toFixed(
          2
        )}`
      );
      loadAiInsights();
    } catch (err) {
      error(err.response?.data?.message || 'Failed to apply discount');
    } finally {
      setApplyingDiscountId(null);
    }
  };

  if (isLoading) {
    return <LoadingSpinner message="Evaluating expiry horizons & reorder risks..." />;
  }

  return (
    <div className="space-y-8">
      {/* Top Banner */}
      <div className="p-6 rounded-3xl bg-gradient-to-r from-emerald-950/60 via-slate-900 to-slate-900 border border-emerald-500/30 shadow-2xl backdrop-blur-md">
        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-500/10 text-emerald-400 text-xs font-semibold border border-emerald-500/20 mb-2">
          <Sparkles className="w-3.5 h-3.5" /> Actionable Intelligence Engine
        </div>
        <h1 className="text-2xl font-extrabold text-white tracking-tight">
          Smart Expiry Clearance & Restock Automations
        </h1>
        <p className="text-xs text-slate-400 mt-1">
          Machine learning rules that prevent inventory spoilage losses and predict optimal reorder batch sizes
        </p>
      </div>

      {/* 1. Smart Discounts for Expiring Items */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-lg font-bold text-white flex items-center gap-2">
              <Tag className="w-5 h-5 text-amber-400" />
              Smart Markdown Discounts (Clearance Recommendations)
            </h2>
            <p className="text-xs text-slate-400">
              Recommended price drops to clear stock before expiration date
            </p>
          </div>
          <span className="text-xs font-mono font-bold text-amber-400">
            {discounts.length} Items Expiring Soon
          </span>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {discounts.map((item) => (
            <div
              key={item.productId}
              className="p-5 rounded-3xl bg-slate-900 border border-slate-800 hover:border-amber-500/40 transition-all flex flex-col justify-between shadow-xl space-y-4"
            >
              <div>
                <div className="flex items-start justify-between gap-2">
                  <div>
                    <h3 className="text-sm font-bold text-white">{item.productName}</h3>
                    <div className="text-[11px] text-slate-400 font-mono">SKU: {item.sku}</div>
                  </div>
                  <Badge
                    variant={item.daysUntilExpiry <= 7 ? 'danger' : 'warning'}
                    size="sm"
                  >
                    {item.daysUntilExpiry <= 0
                      ? 'Expired!'
                      : `Expires in ${item.daysUntilExpiry} days`}
                  </Badge>
                </div>

                <p className="text-xs text-slate-400 mt-2 leading-relaxed">{item.reason}</p>

                {/* Pricing comparison */}
                <div className="mt-4 p-3 rounded-2xl bg-slate-800/80 border border-slate-700/80 space-y-1.5 text-xs">
                  <div className="flex justify-between">
                    <span className="text-slate-400">Current Selling Price:</span>
                    <span className="font-mono line-through text-slate-400">
                      ₹{item.currentPrice?.toFixed(2)}
                    </span>
                  </div>
                  <div className="flex justify-between items-center text-sm font-bold">
                    <span className="text-amber-400 flex items-center gap-1">
                      <TrendingDown className="w-4 h-4" /> Recommended ({item.recommendedDiscountPercent}% Off):
                    </span>
                    <span className="font-mono text-emerald-400 text-base">
                      ₹{item.discountedPrice?.toFixed(2)}
                    </span>
                  </div>
                  <div className="flex justify-between text-[11px] pt-1 border-t border-slate-700 text-slate-400">
                    <span>Stock at Risk:</span>
                    <span className="font-mono text-white">{item.currentStock} units</span>
                  </div>
                  <div className="flex justify-between text-[11px] text-slate-400">
                    <span>Potential Loss Prevented:</span>
                    <span className="font-mono font-bold text-emerald-400">
                      {item.potentialLossPrevented}
                    </span>
                  </div>
                </div>
              </div>

              <div className="pt-2 border-t border-slate-800/80 flex justify-end">
                <Button
                  size="sm"
                  variant="primary"
                  isLoading={applyingDiscountId === item.productId}
                  onClick={() => handleApplyMarkdown(item)}
                >
                  Apply {item.recommendedDiscountPercent}% Markdown
                </Button>
              </div>
            </div>
          ))}

          {discounts.length === 0 && (
            <div className="col-span-full py-12 text-center text-slate-400 text-xs bg-slate-900 border border-slate-800 rounded-3xl">
              ✓ All inventory items are fresh. No products expiring within the next 30 days.
            </div>
          )}
        </div>
      </div>

      {/* 2. Smart Reorder Automation Section */}
      <div className="space-y-4 pt-4 border-t border-slate-800">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-lg font-bold text-white flex items-center gap-2">
              <Truck className="w-5 h-5 text-emerald-400" />
              Smart Reorder Recommendations
            </h2>
            <p className="text-xs text-slate-400">
              Optimal order quantities calculated from daily velocity and supplier minimums
            </p>
          </div>
          <Link to="/ai/demand" className="text-xs text-emerald-400 hover:text-emerald-300 font-semibold">
            View Full 7-Day Forecast &rarr;
          </Link>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {reorders.slice(0, 6).map((item) => (
            <div
              key={item.productId}
              className="p-5 rounded-3xl bg-slate-900 border border-slate-800 hover:border-emerald-500/40 transition-all flex flex-col justify-between shadow-xl space-y-4"
            >
              <div>
                <div className="flex items-start justify-between gap-2">
                  <div>
                    <h3 className="text-sm font-bold text-white">{item.productName}</h3>
                    <div className="text-[11px] text-slate-400 font-mono">SKU: {item.sku}</div>
                  </div>
                  <Badge variant={item.urgency === 'CRITICAL' ? 'danger' : 'warning'} size="sm">
                    {item.urgency}
                  </Badge>
                </div>

                <div className="mt-4 p-3 rounded-2xl bg-slate-800/80 border border-slate-700/80 space-y-1.5 text-xs">
                  <div className="flex justify-between">
                    <span className="text-slate-400">Current Shelf Stock:</span>
                    <span className="font-mono font-bold text-white">{item.currentStock} units</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-slate-400">Predicted 7-Day Demand:</span>
                    <span className="font-mono text-slate-200">{item.predictedDemand} units</span>
                  </div>
                  <div className="flex justify-between text-emerald-400 font-bold">
                    <span>Recommended Order:</span>
                    <span className="font-mono text-sm">+{item.recommendedReorderQuantity} units</span>
                  </div>
                  <div className="flex justify-between text-[11px] pt-1 border-t border-slate-700 text-slate-400">
                    <span>Est. Procurement Cost:</span>
                    <span className="font-mono font-bold text-white">
                      ₹{item.estimatedCost?.toFixed(2)}
                    </span>
                  </div>
                  <div className="flex justify-between text-[11px] text-slate-400">
                    <span>Supplier:</span>
                    <span className="text-slate-200 truncate">{item.supplierName}</span>
                  </div>
                </div>
              </div>

              <div className="pt-2 border-t border-slate-800/80 flex justify-end">
                <Button
                  size="sm"
                  variant="outline"
                  rightIcon={<ArrowRight className="w-3.5 h-3.5" />}
                  onClick={() => navigate(`/purchases/new?productId=${item.productId}`)}
                >
                  Order from Supplier
                </Button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
