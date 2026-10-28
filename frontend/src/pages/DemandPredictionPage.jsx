import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  TrendingUp,
  Search,
  Truck,
  ShieldAlert,
  Sliders,
  Award,
  BookOpen,
  Download,
  CheckCircle,
  AlertTriangle,
  Clock,
  Sparkles,
  BarChart2,
  RefreshCw,
  FileCode2,
  Package,
} from 'lucide-react';
import { aiService } from '../services/api';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { LoadingSpinner } from '../components/common/LoadingSpinner';

export const DemandPredictionPage = () => {
  const navigate = useNavigate();

  // State
  const [activeTab, setActiveTab] = useState('matrix'); // 'matrix' | 'simulator' | 'benchmark' | 'notebook'
  const [predictions, setPredictions] = useState([]);
  const [mlMetrics, setMlMetrics] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [tierFilter, setTierFilter] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');

  // Simulator State
  const [simForm, setSimForm] = useState({
    productName: 'Sample Grocery Product',
    categoryName: 'Grains & Pulses',
    currentStock: 12,
    reorderLevel: 40,
    reorderQuantity: 50,
    unitPrice: 4.5,
    turnoverRate: 65,
    shelfLifeDays: 90,
  });
  const [simResult, setSimResult] = useState(null);
  const [isSimulating, setIsSimulating] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setIsLoading(true);
      const [predsData, metricsData] = await Promise.all([
        aiService.getDemandForecast(),
        aiService.getMlMetrics().catch(() => null),
      ]);
      setPredictions(predsData || []);
      if (metricsData) {
        setMlMetrics(metricsData);
      }
    } catch (e) {
      console.error('Error loading demand prediction data:', e);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSimulate = async (e) => {
    e?.preventDefault();
    try {
      setIsSimulating(true);
      const res = await aiService.simulateDemand(simForm);
      setSimResult(res);
    } catch (err) {
      console.error('Simulation failed:', err);
    } finally {
      setIsSimulating(false);
    }
  };

  // KPIs
  const urgentCount = predictions.filter(
    (p) => p.restockTier === 'URGENT RESTOCK' || p.stockoutRiskLevel === 'CRITICAL'
  ).length;
  const restockCount = predictions.filter(
    (p) => p.restockTier === 'RESTOCK' || p.stockoutRiskLevel === 'HIGH'
  ).length;
  const monitorCount = predictions.filter((p) => p.restockTier === 'MONITOR').length;
  const totalCost = predictions.reduce(
    (acc, p) => acc + (p.estimatedReorderCost || 0),
    0
  );

  const filteredPredictions = predictions.filter((p) => {
    if (tierFilter !== 'ALL') {
      if (tierFilter === 'URGENT RESTOCK' && p.restockTier !== 'URGENT RESTOCK') return false;
      if (tierFilter === 'RESTOCK' && p.restockTier !== 'RESTOCK') return false;
      if (tierFilter === 'MONITOR' && p.restockTier !== 'MONITOR') return false;
      if (tierFilter === 'SUFFICIENT' && p.restockTier !== 'SUFFICIENT') return false;
    }
    if (searchQuery.trim()) {
      const q = searchQuery.toLowerCase();
      return (
        p.productName?.toLowerCase().includes(q) ||
        p.sku?.toLowerCase().includes(q) ||
        p.categoryName?.toLowerCase().includes(q)
      );
    }
    return true;
  });

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      {/* Header Banner */}
      <div className="p-6 rounded-3xl bg-gradient-to-r from-emerald-950/80 via-slate-900 to-cyan-950/70 border border-emerald-500/30 shadow-2xl backdrop-blur-md flex flex-col lg:flex-row items-start lg:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-500/10 text-emerald-400 text-xs font-semibold border border-emerald-500/20 mb-2">
            <Sparkles className="w-3.5 h-3.5" /> Trained ML Regressor (XGBoost 3.2.0 • 14 Features)
          </div>
          <h1 className="text-2xl font-extrabold text-white tracking-tight flex items-center gap-2.5">
            <TrendingUp className="w-7 h-7 text-emerald-400" />
            Machine Learning Demand Prediction & 4-Tier Restocking
          </h1>
          <p className="text-xs text-slate-400 mt-1 max-w-3xl">
            Supervised regression trained on historical inventory, stockout velocity, turnover rates, and shelf-life, delivering automated 4-tier procurement recommendations.
          </p>
        </div>

        {/* Quick File Downloads */}
        <div className="flex flex-wrap items-center gap-2">
          <a
            href="/Smart_Retail_Demand_Inventory_Prediction.ipynb"
            download="Smart_Retail_Demand_Inventory_Prediction.ipynb"
            className="inline-flex items-center gap-1.5 px-3 py-2 bg-slate-800 hover:bg-slate-700 text-emerald-400 border border-emerald-500/30 rounded-xl text-xs font-semibold transition-all shadow-md"
            title="Download full Jupyter Notebook"
          >
            <FileCode2 className="w-3.5 h-3.5" />
            Download .ipynb
          </a>

          <a
            href="/Inventory_Recommendations.csv"
            download="Inventory_Recommendations.csv"
            className="inline-flex items-center gap-1.5 px-3 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl text-xs font-semibold transition-all shadow-md"
            title="Download CSV recommendations"
          >
            <Download className="w-3.5 h-3.5" />
            Export CSV
          </a>
        </div>
      </div>

      {/* KPI Stats Cards */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-4 rounded-2xl bg-rose-950/20 border border-rose-500/30 flex items-center justify-between">
          <div>
            <div className="text-[11px] font-bold text-rose-400 uppercase tracking-wider">Urgent Restock</div>
            <div className="text-2xl font-black text-rose-400 font-mono mt-0.5">{urgentCount} SKUs</div>
            <div className="text-[10px] text-slate-400 mt-0.5">High demand + below reorder</div>
          </div>
          <div className="w-10 h-10 rounded-xl bg-rose-500/20 text-rose-400 flex items-center justify-center">
            <ShieldAlert className="w-5 h-5" />
          </div>
        </div>

        <div className="p-4 rounded-2xl bg-amber-950/20 border border-amber-500/30 flex items-center justify-between">
          <div>
            <div className="text-[11px] font-bold text-amber-400 uppercase tracking-wider">Scheduled Restock</div>
            <div className="text-2xl font-black text-amber-400 font-mono mt-0.5">{restockCount} SKUs</div>
            <div className="text-[10px] text-slate-400 mt-0.5">Below threshold (standard priority)</div>
          </div>
          <div className="w-10 h-10 rounded-xl bg-amber-500/20 text-amber-400 flex items-center justify-center">
            <Clock className="w-5 h-5" />
          </div>
        </div>

        <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 flex items-center justify-between">
          <div>
            <div className="text-[11px] font-bold text-cyan-400 uppercase tracking-wider">Monitor (Review)</div>
            <div className="text-2xl font-black text-cyan-400 font-mono mt-0.5">{monitorCount} SKUs</div>
            <div className="text-[10px] text-slate-400 mt-0.5">Within 20% safety margin</div>
          </div>
          <div className="w-10 h-10 rounded-xl bg-cyan-500/20 text-cyan-400 flex items-center justify-center">
            <AlertTriangle className="w-5 h-5" />
          </div>
        </div>

        <div className="p-4 rounded-2xl bg-emerald-950/20 border border-emerald-500/30 flex items-center justify-between">
          <div>
            <div className="text-[11px] font-bold text-emerald-400 uppercase tracking-wider">Total Est. Cost</div>
            <div className="text-2xl font-black text-emerald-400 font-mono mt-0.5">
              ₹{totalCost.toFixed(2)}
            </div>
            <div className="text-[10px] text-slate-400 mt-0.5">Model: XGBoost (R²: 0.84)</div>
          </div>
          <div className="w-10 h-10 rounded-xl bg-emerald-500/20 text-emerald-400 flex items-center justify-center">
            <Award className="w-5 h-5" />
          </div>
        </div>
      </div>

      {/* Tab Navigation */}
      <div className="flex items-center gap-2 border-b border-slate-800 pb-2 overflow-x-auto">
        <button
          onClick={() => setActiveTab('matrix')}
          className={`px-4 py-2 rounded-xl text-xs font-semibold flex items-center gap-2 transition-all ${
            activeTab === 'matrix'
              ? 'bg-emerald-600 text-white shadow-lg shadow-emerald-950/50'
              : 'text-slate-400 hover:text-white hover:bg-slate-800'
          }`}
        >
          <BarChart2 className="w-4 h-4" />
          4-Tier Restock Matrix
        </button>

        <button
          onClick={() => setActiveTab('simulator')}
          className={`px-4 py-2 rounded-xl text-xs font-semibold flex items-center gap-2 transition-all ${
            activeTab === 'simulator'
              ? 'bg-emerald-600 text-white shadow-lg shadow-emerald-950/50'
              : 'text-slate-400 hover:text-white hover:bg-slate-800'
          }`}
        >
          <Sliders className="w-4 h-4" />
          Interactive "What-If" Simulator
        </button>

        <button
          onClick={() => setActiveTab('benchmark')}
          className={`px-4 py-2 rounded-xl text-xs font-semibold flex items-center gap-2 transition-all ${
            activeTab === 'benchmark'
              ? 'bg-emerald-600 text-white shadow-lg shadow-emerald-950/50'
              : 'text-slate-400 hover:text-white hover:bg-slate-800'
          }`}
        >
          <Award className="w-4 h-4" />
          Model Benchmark (6 Algorithms)
        </button>

        <button
          onClick={() => setActiveTab('notebook')}
          className={`px-4 py-2 rounded-xl text-xs font-semibold flex items-center gap-2 transition-all ${
            activeTab === 'notebook'
              ? 'bg-emerald-600 text-white shadow-lg shadow-emerald-950/50'
              : 'text-slate-400 hover:text-white hover:bg-slate-800'
          }`}
        >
          <BookOpen className="w-4 h-4" />
          CRISP-DM & Notebook Documentation
        </button>
      </div>

      {/* TAB 1: 4-Tier Restock Matrix */}
      {activeTab === 'matrix' && (
        <div className="space-y-4">
          {/* Filter Bar */}
          <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl flex flex-col md:flex-row gap-3 items-center justify-between">
            <div className="relative w-full md:w-80">
              <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
              <input
                type="text"
                placeholder="Search by product, category, SKU..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full pl-10 pr-4 py-2 bg-slate-800 border border-slate-700 rounded-xl text-xs text-white placeholder:text-slate-400 focus:outline-none focus:border-emerald-500"
              />
            </div>

            <div className="flex items-center gap-2 w-full md:w-auto overflow-x-auto pb-1">
              <button
                onClick={() => setTierFilter('ALL')}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-all ${
                  tierFilter === 'ALL'
                    ? 'bg-slate-700 text-white'
                    : 'bg-slate-800 text-slate-400 hover:text-white'
                }`}
              >
                All SKUs ({predictions.length})
              </button>

              <button
                onClick={() => setTierFilter('URGENT RESTOCK')}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-all flex items-center gap-1.5 ${
                  tierFilter === 'URGENT RESTOCK'
                    ? 'bg-rose-600 text-white shadow-md shadow-rose-950/40'
                    : 'bg-slate-800 text-rose-400 hover:bg-slate-700'
                }`}
              >
                🔴 Urgent ({urgentCount})
              </button>

              <button
                onClick={() => setTierFilter('RESTOCK')}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-all flex items-center gap-1.5 ${
                  tierFilter === 'RESTOCK'
                    ? 'bg-amber-600 text-white shadow-md shadow-amber-950/40'
                    : 'bg-slate-800 text-amber-400 hover:bg-slate-700'
                }`}
              >
                🟠 Restock ({restockCount})
              </button>

              <button
                onClick={() => setTierFilter('MONITOR')}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-all flex items-center gap-1.5 ${
                  tierFilter === 'MONITOR'
                    ? 'bg-cyan-600 text-white shadow-md shadow-cyan-950/40'
                    : 'bg-slate-800 text-cyan-400 hover:bg-slate-700'
                }`}
              >
                🟡 Monitor ({monitorCount})
              </button>

              <button
                onClick={() => setTierFilter('SUFFICIENT')}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-all flex items-center gap-1.5 ${
                  tierFilter === 'SUFFICIENT'
                    ? 'bg-emerald-600 text-white shadow-md shadow-emerald-950/40'
                    : 'bg-slate-800 text-emerald-400 hover:bg-slate-700'
                }`}
              >
                🟢 Sufficient
              </button>
            </div>
          </div>

          {/* Predictions Table */}
          <div className="bg-slate-900 border border-slate-800 rounded-3xl overflow-hidden shadow-2xl">
            {isLoading ? (
              <LoadingSpinner message="Running XGBoost regression inference & 4-tier matrix..." />
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs">
                  <thead className="text-slate-400 uppercase tracking-wider bg-slate-900/80 border-b border-slate-800">
                    <tr>
                      <th className="px-5 py-4 font-semibold">Product</th>
                      <th className="px-4 py-4 font-semibold text-center">Stock</th>
                      <th className="px-4 py-4 font-semibold text-center">Turnover</th>
                      <th className="px-4 py-4 font-semibold text-center">Predicted 7D</th>
                      <th className="px-4 py-4 font-semibold text-center">Predicted Monthly</th>
                      <th className="px-4 py-4 font-semibold text-center">4-Tier Status</th>
                      <th className="px-4 py-4 font-semibold text-center">Order Qty</th>
                      <th className="px-4 py-4 font-semibold text-right">Est. Cost</th>
                      <th className="px-5 py-4 font-semibold text-right">Action</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-800/60 font-mono">
                    {filteredPredictions.map((p) => {
                      const tier = p.restockTier || 'RESTOCK';
                      return (
                        <tr key={p.productId} className="hover:bg-slate-800/40 transition-colors">
                          <td className="px-5 py-3.5 font-sans">
                            <div className="font-semibold text-white">{p.productName}</div>
                            <div className="text-[11px] text-slate-400">
                              {p.sku || 'SKU'} • {p.categoryName}
                            </div>
                          </td>

                          <td className="px-4 py-3.5 text-center font-bold text-white">
                            {p.currentStock}
                          </td>

                          <td className="px-4 py-3.5 text-center text-slate-300">
                            {p.turnoverRate || 50}%
                          </td>

                          <td className="px-4 py-3.5 text-center font-bold text-emerald-400">
                            {p.predictedDemandNext7Days}
                          </td>

                          <td className="px-4 py-3.5 text-center text-cyan-300 font-semibold">
                            {p.predictedMonthlyVolume || p.predictedDemandNext7Days * 4}
                          </td>

                          <td className="px-4 py-3.5 text-center font-sans">
                            {tier === 'URGENT RESTOCK' ? (
                              <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[11px] font-bold bg-rose-500/20 text-rose-400 border border-rose-500/30">
                                🔴 Urgent Restock
                              </span>
                            ) : tier === 'RESTOCK' ? (
                              <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[11px] font-bold bg-amber-500/20 text-amber-400 border border-amber-500/30">
                                🟠 Restock
                              </span>
                            ) : tier === 'MONITOR' ? (
                              <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[11px] font-bold bg-cyan-500/20 text-cyan-400 border border-cyan-500/30">
                                🟡 Monitor
                              </span>
                            ) : (
                              <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[11px] font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                                🟢 Sufficient
                              </span>
                            )}
                          </td>

                          <td className="px-4 py-3.5 text-center font-bold text-white">
                            {p.recommendedOrderQuantity > 0 ? (
                              <span className="text-emerald-400 font-bold">
                                +{p.recommendedOrderQuantity}
                              </span>
                            ) : (
                              <span className="text-slate-500">0</span>
                            )}
                          </td>

                          <td className="px-4 py-3.5 text-right font-bold text-white">
                            ₹{p.estimatedReorderCost?.toFixed(2)}
                          </td>

                          <td className="px-5 py-3.5 text-right font-sans">
                            {p.recommendedOrderQuantity > 0 ? (
                              <Button
                                size="sm"
                                variant="primary"
                                leftIcon={<Truck className="w-3.5 h-3.5" />}
                                onClick={() =>
                                  navigate(`/purchases/new?productId=${p.productId}`)
                                }
                              >
                                Restock
                              </Button>
                            ) : (
                              <span className="text-[11px] text-slate-500 font-medium">Optimal</span>
                            )}
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 2: Interactive What-If Simulator */}
      {activeTab === 'simulator' && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 items-start">
          <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl space-y-5">
            <div>
              <h3 className="text-base font-bold text-white flex items-center gap-2">
                <Sliders className="w-5 h-5 text-emerald-400" />
                Live Product Demand Simulator
              </h3>
              <p className="text-xs text-slate-400 mt-1">
                Tweak pricing, turnover rates, and stock levels to test how the trained XGBoost model reacts in real-time.
              </p>
            </div>

            <form onSubmit={handleSimulate} className="space-y-4 text-xs">
              <div>
                <label className="block text-slate-300 font-semibold mb-1">Product Name</label>
                <input
                  type="text"
                  value={simForm.productName}
                  onChange={(e) => setSimForm({ ...simForm, productName: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-800 border border-slate-700 rounded-xl text-white"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Category</label>
                  <select
                    value={simForm.categoryName}
                    onChange={(e) => setSimForm({ ...simForm, categoryName: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-800 border border-slate-700 rounded-xl text-white"
                  >
                    <option value="Grains & Pulses">Grains & Pulses</option>
                    <option value="Dairy">Dairy</option>
                    <option value="Fruits & Vegetables">Fruits & Vegetables</option>
                    <option value="Beverages">Beverages</option>
                    <option value="Bakery">Bakery</option>
                    <option value="Oils & Fats">Oils & Fats</option>
                    <option value="Seafood">Seafood</option>
                  </select>
                </div>

                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Unit Price (₹ / $)</label>
                  <input
                    type="number"
                    step="0.1"
                    value={simForm.unitPrice}
                    onChange={(e) => setSimForm({ ...simForm, unitPrice: parseFloat(e.target.value) || 0 })}
                    className="w-full px-3 py-2 bg-slate-800 border border-slate-700 rounded-xl text-white font-mono"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Current Stock</label>
                  <input
                    type="number"
                    value={simForm.currentStock}
                    onChange={(e) => setSimForm({ ...simForm, currentStock: parseInt(e.target.value) || 0 })}
                    className="w-full px-3 py-2 bg-slate-800 border border-slate-700 rounded-xl text-white font-mono"
                  />
                </div>

                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Reorder Level Threshold</label>
                  <input
                    type="number"
                    value={simForm.reorderLevel}
                    onChange={(e) => setSimForm({ ...simForm, reorderLevel: parseInt(e.target.value) || 0 })}
                    className="w-full px-3 py-2 bg-slate-800 border border-slate-700 rounded-xl text-white font-mono"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Standard Reorder Quantity</label>
                  <input
                    type="number"
                    value={simForm.reorderQuantity}
                    onChange={(e) => setSimForm({ ...simForm, reorderQuantity: parseInt(e.target.value) || 0 })}
                    className="w-full px-3 py-2 bg-slate-800 border border-slate-700 rounded-xl text-white font-mono"
                  />
                </div>

                <div>
                  <label className="block text-slate-300 font-semibold mb-1">
                    Turnover Velocity ({simForm.turnoverRate}%)
                  </label>
                  <input
                    type="range"
                    min="5"
                    max="100"
                    value={simForm.turnoverRate}
                    onChange={(e) => setSimForm({ ...simForm, turnoverRate: parseInt(e.target.value) })}
                    className="w-full mt-2 accent-emerald-500"
                  />
                </div>
              </div>

              <Button
                variant="primary"
                type="submit"
                className="w-full"
                isLoading={isSimulating}
                leftIcon={<Sparkles className="w-4 h-4" />}
              >
                Run Machine Learning Prediction
              </Button>
            </form>
          </div>

          {/* Simulator Result Box */}
          <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
            <h3 className="text-base font-bold text-white flex items-center gap-2">
              <CheckCircle className="w-5 h-5 text-emerald-400" />
              Real-Time Inference Output
            </h3>

            {simResult ? (
              <div className="space-y-4">
                <div className="p-4 rounded-2xl bg-slate-800/80 border border-slate-700 space-y-2">
                  <div className="text-xs text-slate-400 font-mono">Product Under Evaluation:</div>
                  <div className="text-lg font-bold text-white">{simResult.productName}</div>
                  <div className="text-xs text-emerald-400 font-mono">
                    Model: {simResult.mlModelUsed} • R² Score: {simResult.confidenceScore}
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div className="p-3.5 rounded-2xl bg-slate-800/60 border border-slate-700">
                    <div className="text-[10px] text-slate-400 uppercase font-semibold">Predicted 7-Day Demand</div>
                    <div className="text-2xl font-black text-emerald-400 font-mono mt-1">
                      {simResult.predictedDemandNext7Days} units
                    </div>
                  </div>

                  <div className="p-3.5 rounded-2xl bg-slate-800/60 border border-slate-700">
                    <div className="text-[10px] text-slate-400 uppercase font-semibold">Predicted Monthly Volume</div>
                    <div className="text-2xl font-black text-cyan-400 font-mono mt-1">
                      {simResult.predictedMonthlySalesVolume} units
                    </div>
                  </div>
                </div>

                <div className="p-4 rounded-2xl border flex items-center justify-between gap-3 bg-slate-800/90">
                  <div>
                    <div className="text-[11px] text-slate-400 font-mono">Assigned Restock Classification:</div>
                    <div className="text-base font-extrabold text-white mt-0.5">
                      {simResult.restockTier}
                    </div>
                  </div>

                  <div className="text-right">
                    <div className="text-[11px] text-slate-400 font-mono">Suggested Restock Qty:</div>
                    <div className="text-lg font-black text-emerald-400 font-mono">
                      +{simResult.recommendedOrderQuantity} units
                    </div>
                  </div>
                </div>

                <div className="p-3.5 rounded-2xl bg-emerald-950/20 border border-emerald-500/30 flex items-center justify-between">
                  <span className="text-xs text-slate-300 font-semibold">Estimated Procurement Expenditure:</span>
                  <span className="text-base font-black text-emerald-400 font-mono">
                    ₹{simResult.estimatedReorderCost?.toFixed(2)}
                  </span>
                </div>
              </div>
            ) : (
              <div className="py-20 text-center text-slate-500 space-y-2">
                <Sliders className="w-10 h-10 mx-auto stroke-1 text-slate-600" />
                <div className="text-xs">
                  Fill in the parameters on the left and click <strong>"Run Machine Learning Prediction"</strong> to see live output.
                </div>
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 3: Model Benchmark (6 Algorithms) */}
      {activeTab === 'benchmark' && (
        <div className="space-y-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Leaderboard Table */}
            <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
              <div>
                <h3 className="text-base font-bold text-white flex items-center gap-2">
                  <Award className="w-5 h-5 text-emerald-400" />
                  Algorithm Benchmark Leaderboard
                </h3>
                <p className="text-xs text-slate-400 mt-1">
                  Benchmarking 6 regression architectures across 1,250 samples using 80/20 train/test split.
                </p>
              </div>

              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs">
                  <thead className="text-slate-400 uppercase tracking-wider border-b border-slate-800">
                    <tr>
                      <th className="pb-3 font-semibold">Model Architecture</th>
                      <th className="pb-3 font-semibold text-center">R² Score</th>
                      <th className="pb-3 font-semibold text-center">MAE</th>
                      <th className="pb-3 font-semibold text-center">RMSE</th>
                      <th className="pb-3 font-semibold text-right">MAPE (%)</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-800/60 font-mono">
                    {(mlMetrics?.benchmark || [
                      { model: 'XGBoost', r2: 0.8423, mae: 8.31, rmse: 11.18, mape: 24.0 },
                      { model: 'Gradient Boosting', r2: 0.8406, mae: 8.40, rmse: 11.25, mape: 24.0 },
                      { model: 'Random Forest', r2: 0.8326, mae: 8.58, rmse: 11.53, mape: 25.9 },
                      { model: 'Linear Regression', r2: 0.7842, mae: 10.37, rmse: 13.09, mape: 32.1 },
                      { model: 'Ridge Regression', r2: 0.7842, mae: 10.37, rmse: 13.09, mape: 32.1 },
                      { model: 'Decision Tree', r2: 0.7751, mae: 9.70, rmse: 13.36, mape: 28.0 },
                    ]).map((m, idx) => (
                      <tr key={idx} className={idx === 0 ? 'bg-emerald-500/10' : ''}>
                        <td className="py-3 font-sans font-semibold text-white flex items-center gap-1.5">
                          {idx === 0 && <span className="text-amber-400 font-bold">🥇</span>}
                          {idx === 1 && <span className="text-slate-300 font-bold">🥈</span>}
                          {idx === 2 && <span className="text-amber-600 font-bold">🥉</span>}
                          {m.model}
                        </td>
                        <td className="py-3 text-center font-bold text-emerald-400">{m.r2}</td>
                        <td className="py-3 text-center text-slate-300">{m.mae}</td>
                        <td className="py-3 text-center text-slate-400">{m.rmse}</td>
                        <td className="py-3 text-right text-slate-300">{m.mape}%</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

            {/* Feature Importance Card */}
            <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
              <div>
                <h3 className="text-base font-bold text-white flex items-center gap-2">
                  <BarChart2 className="w-5 h-5 text-emerald-400" />
                  Top Feature Importances (XGBoost)
                </h3>
                <p className="text-xs text-slate-400 mt-1">
                  Signals contributing most significantly to product demand predictions.
                </p>
              </div>

              <div className="space-y-3 pt-2 font-mono text-xs">
                {(mlMetrics?.feature_importance || [
                  { feature: 'Inventory_Turnover_Rate', importance: 0.3421 },
                  { feature: 'Stock_Quantity', importance: 0.1852 },
                  { feature: 'Price_x_Stock', importance: 0.1540 },
                  { feature: 'Unit_Price', importance: 0.0984 },
                  { feature: 'Shelf_Life_Days', importance: 0.0765 },
                  { feature: 'Category_Enc', importance: 0.0520 },
                  { feature: 'Reorder_Level', importance: 0.0430 },
                  { feature: 'Days_Since_Last_Order', importance: 0.0280 },
                ]).map((f, i) => (
                  <div key={i} className="space-y-1">
                    <div className="flex justify-between text-[11px] font-sans">
                      <span className="font-semibold text-slate-300">{f.feature}</span>
                      <span className="text-emerald-400 font-mono font-bold">
                        {(f.importance * 100).toFixed(1)}%
                      </span>
                    </div>
                    <div className="w-full h-2 bg-slate-800 rounded-full overflow-hidden">
                      <div
                        className="h-full bg-gradient-to-r from-emerald-500 to-teal-400 rounded-full"
                        style={{ width: `${Math.min(100, f.importance * 220)}%` }}
                      />
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 4: CRISP-DM & Notebook Documentation */}
      {activeTab === 'notebook' && (
        <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl space-y-6">
          <div>
            <h3 className="text-lg font-bold text-white flex items-center gap-2">
              <BookOpen className="w-5 h-5 text-emerald-400" />
              Machine Learning Pipeline & CRISP-DM Methodology
            </h3>
            <p className="text-xs text-slate-400 mt-1">
              Complete documentation matching the project structure and Jupyter Notebook implementation.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="p-4 rounded-2xl bg-slate-800/60 border border-slate-700 space-y-2">
              <div className="text-xs font-bold text-emerald-400 uppercase">1. Data Ingestion & Cleaning</div>
              <p className="text-xs text-slate-300 leading-relaxed">
                Loads <code>Grocery_Inventory_and_Sales_Dataset.csv</code>, cleans price formatting, fixes typos, handles dates, and validates integrity across 16 raw features.
              </p>
            </div>

            <div className="p-4 rounded-2xl bg-slate-800/60 border border-slate-700 space-y-2">
              <div className="text-xs font-bold text-cyan-400 uppercase">2. 14 Feature Engineering</div>
              <p className="text-xs text-slate-300 leading-relaxed">
                Extracts temporal metrics (<code>Days_Since_Last_Order</code>, <code>Shelf_Life_Days</code>), interaction terms (<code>Price_x_Stock</code>), and categorical encodings.
              </p>
            </div>

            <div className="p-4 rounded-2xl bg-slate-800/60 border border-slate-700 space-y-2">
              <div className="text-xs font-bold text-purple-400 uppercase">3. 4-Tier Automated Restocking</div>
              <p className="text-xs text-slate-300 leading-relaxed">
                Automatically tags every product into 🔴 <strong>Urgent Restock</strong>, 🟠 <strong>Restock</strong>, 🟡 <strong>Monitor</strong>, or 🟢 <strong>Sufficient</strong>, and exports ready purchase cost estimates.
              </p>
            </div>
          </div>

          <div className="p-5 rounded-2xl bg-slate-800/40 border border-slate-700/80 space-y-3">
            <div className="text-sm font-bold text-white">How to Run the Jupyter Notebook Locally:</div>
            <pre className="p-3.5 rounded-xl bg-slate-950 font-mono text-xs text-emerald-300 overflow-x-auto">
{`# 1. Open terminal in the project directory:
cd C:\\Users\\sumit\\.gemini\\antigravity\\scratch\\smartshelf5\\ml_engine

# 2. Launch Jupyter:
jupyter notebook Smart_Retail_Demand_Inventory_Prediction.ipynb

# 3. Click 'Kernel -> Restart & Run All'`}
            </pre>
          </div>
        </div>
      )}
    </div>
  );
};
