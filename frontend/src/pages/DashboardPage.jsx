import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import {
  IndianRupee,
  TrendingUp,
  ShoppingCart,
  Boxes,
  AlertTriangle,
  Users,
  CreditCard,
  Sparkles,
  ArrowRight,
  ChevronRight,
} from 'lucide-react';
import { analyticsService } from '../services/api';
import { StatCard } from '../components/common/StatCard';
import { Badge } from '../components/common/Badge';
import { Button } from '../components/common/Button';
import { LoadingSpinner } from '../components/common/LoadingSpinner';

export const DashboardPage = () => {
  const navigate = useNavigate();
  const [metrics, setMetrics] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      setIsLoading(true);
      const data = await analyticsService.getDashboardMetrics();
      setMetrics(data);
    } catch (e) {
      console.error('Failed to load dashboard metrics:', e);
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading || !metrics) {
    return <LoadingSpinner message="Calculating store metrics & AI insights..." />;
  }

  const maxSale = Math.max(...(metrics.salesTrend || []).map((s) => s.sales), 1000);

  return (
    <div className="space-y-6">
      {/* Top Banner / Welcome */}
      <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4 p-6 rounded-3xl bg-gradient-to-r from-emerald-950/60 via-slate-900 to-slate-900 border border-emerald-500/20 shadow-2xl backdrop-blur-md">
        <div>
          <div className="inline-flex items-center gap-2 px-2.5 py-1 rounded-full bg-emerald-500/10 text-emerald-400 text-xs font-semibold border border-emerald-500/20 mb-2">
            <Sparkles className="w-3.5 h-3.5" /> AI Supermarket Operations Active
          </div>
          <h1 className="text-2xl lg:text-3xl font-extrabold text-white tracking-tight">
            Store Performance Overview
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Real-time analytics, stockout risk assessments, and cashier billing metrics.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Link to="/ai/handwritten-list">
            <Button variant="secondary" size="md" leftIcon={<Sparkles className="w-4 h-4 text-emerald-400" />}>
              Scan Shopping List
            </Button>
          </Link>
          <Link to="/pos">
            <Button variant="primary" size="md" leftIcon={<ShoppingCart className="w-4 h-4" />}>
              Open POS Terminal
            </Button>
          </Link>
        </div>
      </div>

      {/* KPI Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Today's Sales"
          value={`₹${metrics.todaySales?.toFixed(2) || '0.00'}`}
          subtitle={`${metrics.todayOrders || 0} Orders completed today`}
          icon={IndianRupee}
          variant="emerald"
          trend={{ value: '14.2%', isPositive: true }}
          onClick={() => navigate('/sales')}
        />

        <StatCard
          title="Total Gross Profit"
          value={`₹${metrics.totalProfit?.toFixed(2) || '0.00'}`}
          subtitle={`Overall Margin: ${metrics.profitMarginPercentage || 0}%`}
          icon={TrendingUp}
          variant="sky"
          trend={{ value: '8.4%', isPositive: true }}
          onClick={() => navigate('/analytics')}
        />

        <StatCard
          title="Pending Customer Credit"
          value={`₹${metrics.pendingUdharTotal?.toFixed(2) || '0.00'}`}
          subtitle="Total credit across customers"
          icon={CreditCard}
          variant={(metrics.pendingUdharTotal || 0) > 5000 ? 'rose' : 'amber'}
          onClick={() => navigate('/credit')}
        />

        <StatCard
          title="Stock & Expiry Alerts"
          value={(metrics.lowStockCount || 0) + (metrics.expiringSoonCount || 0)}
          subtitle={`${metrics.lowStockCount || 0} Low stock, ${metrics.expiringSoonCount || 0} Expiring`}
          icon={AlertTriangle}
          variant={(metrics.lowStockCount || 0) > 0 ? 'rose' : 'default'}
          onClick={() => navigate('/notifications')}
        />
      </div>

      {/* Actionable AI Insights Banner Section */}
      {metrics.insights && metrics.insights.length > 0 && (
        <div className="space-y-3">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2 text-sm font-bold text-white">
              <Sparkles className="w-4 h-4 text-emerald-400" />
              Smart Recommendations & Instant Actions
            </div>
            <Link to="/ai/insights" className="text-xs text-emerald-400 hover:text-emerald-300 font-semibold flex items-center gap-1">
              View All AI Insights <ChevronRight className="w-3 h-3" />
            </Link>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {metrics.insights.map((insight) => (
              <div
                key={insight.id}
                className="p-5 rounded-2xl bg-slate-900/90 border border-slate-800 hover:border-emerald-500/40 transition-all flex flex-col justify-between shadow-xl"
              >
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <Badge variant={insight.impact === 'HIGH' ? 'danger' : 'warning'} size="sm">
                      {insight.impact} PRIORITY
                    </Badge>
                    <span className="text-[10px] uppercase font-bold tracking-wider text-slate-400">
                      {insight.category}
                    </span>
                  </div>
                  <h4 className="text-sm font-bold text-white mb-1">{insight.title}</h4>
                  <p className="text-xs text-slate-400 leading-relaxed">{insight.description}</p>
                </div>

                <div className="mt-4 pt-3 border-t border-slate-800/80 flex justify-end">
                  <Button
                    size="sm"
                    variant="outline"
                    rightIcon={<ArrowRight className="w-3.5 h-3.5" />}
                    onClick={() => navigate(insight.actionLink)}
                  >
                    {insight.actionLabel}
                  </Button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* 2-Column Analytics Layout */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* 7-Day Sales Trend Bar Chart (2 Cols) */}
        <div className="lg:col-span-2 p-6 rounded-3xl bg-slate-900/80 border border-slate-800 backdrop-blur-md shadow-xl space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-base font-bold text-white">7-Day Sales Velocity</h3>
              <p className="text-xs text-slate-400">Daily total revenue from POS checkout orders</p>
            </div>
            <div className="text-right">
              <div className="text-xs text-slate-400">7-Day Avg</div>
              <div className="text-sm font-bold text-emerald-400">
                ₹{((metrics.salesTrend || []).reduce((acc, curr) => acc + curr.sales, 0) / 7).toFixed(0)} / day
              </div>
            </div>
          </div>

          {/* CSS Dynamic Bar Chart */}
          <div className="pt-6 h-56 flex items-end justify-between gap-3 border-b border-slate-800 pb-2">
            {(metrics.salesTrend || []).map((item, idx) => {
              const heightPercent = Math.max(12, Math.round((item.sales / maxSale) * 100));
              const isToday = idx === metrics.salesTrend.length - 1;

              return (
                <div key={item.date} className="flex-1 flex flex-col items-center gap-2 group relative">
                  {/* Tooltip */}
                  <div className="opacity-0 group-hover:opacity-100 transition-opacity absolute -top-10 bg-slate-800 text-white text-[11px] font-mono px-2 py-1 rounded-lg border border-slate-700 pointer-events-none shadow-lg whitespace-nowrap z-20">
                    ₹{item.sales?.toFixed(2)} ({item.orderCount} orders)
                  </div>

                  {/* Bar */}
                  <div className="w-full max-w-[42px] bg-slate-800 rounded-t-xl overflow-hidden flex items-end h-44">
                    <div
                      style={{ height: `${heightPercent}%` }}
                      className={`w-full rounded-t-xl transition-all duration-500 ${
                        isToday
                          ? 'bg-gradient-to-t from-emerald-600 to-teal-400 shadow-lg shadow-emerald-500/20'
                          : 'bg-slate-700 group-hover:bg-emerald-500/70'
                      }`}
                    />
                  </div>

                  {/* Date Label */}
                  <span className={`text-[11px] font-mono ${isToday ? 'text-emerald-400 font-bold' : 'text-slate-400'}`}>
                    {item.date?.substring(5)}
                  </span>
                </div>
              );
            })}
          </div>
        </div>

        {/* Inventory Status Breakdown (1 Col) */}
        <div className="p-6 rounded-3xl bg-slate-900/80 border border-slate-800 backdrop-blur-md shadow-xl flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-base font-bold text-white">Inventory Health</h3>
              <Link to="/inventory" className="text-xs text-emerald-400 hover:text-emerald-300 font-semibold">
                Manage
              </Link>
            </div>

            <div className="space-y-4">
              <div>
                <div className="flex justify-between text-xs mb-1.5">
                  <span className="text-slate-300">Healthy Stock</span>
                  <span className="font-bold text-emerald-400">{metrics.inventoryStatus?.healthyCount || 0} items</span>
                </div>
                <div className="w-full h-2 rounded-full bg-slate-800 overflow-hidden">
                  <div
                    style={{
                      width: `${((metrics.inventoryStatus?.healthyCount || 0) / (metrics.inventoryStatus?.totalProducts || 1)) * 100}%`,
                    }}
                    className="h-full bg-emerald-500 rounded-full"
                  />
                </div>
              </div>

              <div>
                <div className="flex justify-between text-xs mb-1.5">
                  <span className="text-slate-300">Low Stock Threshold</span>
                  <span className="font-bold text-amber-400">{metrics.inventoryStatus?.lowStockCount || 0} items</span>
                </div>
                <div className="w-full h-2 rounded-full bg-slate-800 overflow-hidden">
                  <div
                    style={{
                      width: `${((metrics.inventoryStatus?.lowStockCount || 0) / (metrics.inventoryStatus?.totalProducts || 1)) * 100}%`,
                    }}
                    className="h-full bg-amber-500 rounded-full"
                  />
                </div>
              </div>

              <div>
                <div className="flex justify-between text-xs mb-1.5">
                  <span className="text-slate-300">Critical / Out of Stock</span>
                  <span className="font-bold text-rose-400">{metrics.inventoryStatus?.criticalCount || 0} items</span>
                </div>
                <div className="w-full h-2 rounded-full bg-slate-800 overflow-hidden">
                  <div
                    style={{
                      width: `${((metrics.inventoryStatus?.criticalCount || 0) / (metrics.inventoryStatus?.totalProducts || 1)) * 100}%`,
                    }}
                    className="h-full bg-rose-500 rounded-full"
                  />
                </div>
              </div>

              <div>
                <div className="flex justify-between text-xs mb-1.5">
                  <span className="text-slate-300">Expiring in &le; 7 Days</span>
                  <span className="font-bold text-purple-400">{metrics.inventoryStatus?.expiredCount || 0} items</span>
                </div>
                <div className="w-full h-2 rounded-full bg-slate-800 overflow-hidden">
                  <div
                    style={{
                      width: `${((metrics.inventoryStatus?.expiredCount || 0) / (metrics.inventoryStatus?.totalProducts || 1)) * 100}%`,
                    }}
                    className="h-full bg-indigo-500 rounded-full"
                  />
                </div>
              </div>
            </div>
          </div>

          <div className="mt-6 pt-4 border-t border-slate-800">
            <Link to="/ai/demand">
              <Button size="sm" variant="outline" className="w-full" leftIcon={<TrendingUp className="w-4 h-4" />}>
                View Demand Forecast & Reorder
              </Button>
            </Link>
          </div>
        </div>
      </div>

      {/* Top Best-Selling Products Table */}
      <div className="p-6 rounded-3xl bg-slate-900/80 border border-slate-800 backdrop-blur-md shadow-xl space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-base font-bold text-white">Top Selling Products</h3>
            <p className="text-xs text-slate-400">High-velocity grocery items contributing to sales and gross margin</p>
          </div>
          <Link to="/products" className="text-xs text-emerald-400 hover:text-emerald-300 font-semibold">
            All Products
          </Link>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="text-slate-400 uppercase tracking-wider border-b border-slate-800">
              <tr>
                <th className="pb-3 font-semibold">Product</th>
                <th className="pb-3 font-semibold">Category</th>
                <th className="pb-3 font-semibold text-center">Units Sold</th>
                <th className="pb-3 font-semibold text-right">Revenue</th>
                <th className="pb-3 font-semibold text-right">Gross Profit</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60">
              {(metrics.topProducts || []).map((p) => (
                <tr key={p.productId} className="hover:bg-slate-800/40 transition-colors">
                  <td className="py-3 font-medium text-white">{p.productName}</td>
                  <td className="py-3 text-slate-400">{p.categoryName}</td>
                  <td className="py-3 text-center font-mono text-slate-300">{p.unitsSold}</td>
                  <td className="py-3 text-right font-mono font-bold text-white">₹{p.totalRevenue?.toFixed(2)}</td>
                  <td className="py-3 text-right font-mono font-bold text-emerald-400">
                    +₹{p.grossProfit?.toFixed(2)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
