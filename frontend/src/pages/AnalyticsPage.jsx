import React, { useState, useEffect } from 'react';
import { BarChart3, TrendingUp, DollarSign, PieChart, ArrowUpRight, IndianRupee } from 'lucide-react';
import { analyticsService } from '../services/api';
import { StatCard } from '../components/common/StatCard';
import { LoadingSpinner } from '../components/common/LoadingSpinner';

export const AnalyticsPage = () => {
  const [profitSummary, setProfitSummary] = useState(null);
  const [categorySales, setCategorySales] = useState([]);
  const [topProducts, setTopProducts] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadAnalytics();
  }, []);

  const loadAnalytics = async () => {
    try {
      setIsLoading(true);
      const [profit, cats, prods] = await Promise.all([
        analyticsService.getProfitSummary(),
        analyticsService.getCategorySales(),
        analyticsService.getTopProducts(10),
      ]);
      setProfitSummary(profit);
      setCategorySales(cats || []);
      setTopProducts(prods || []);
    } catch (e) {
      console.error(e);
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading || !profitSummary) {
    return <LoadingSpinner message="Calculating financial metrics & gross profit margins..." />;
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-extrabold text-white tracking-tight flex items-center gap-2.5">
          <BarChart3 className="w-7 h-7 text-emerald-400" />
          Financial Analytics & Gross Profit
        </h1>
        <p className="text-xs text-slate-400 mt-1">
          Store margins, Cost of Goods Sold (COGS), category revenue distribution, and item profitability
        </p>
      </div>

      {/* P&L Financial Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Total Store Revenue"
          value={`₹${profitSummary.totalRevenue?.toFixed(2) || '0.00'}`}
          subtitle="Gross sales before expenses"
          icon={IndianRupee}
          variant="emerald"
        />

        <StatCard
          title="Cost of Goods Sold (COGS)"
          value={`₹${profitSummary.costOfGoodsSold?.toFixed(2) || '0.00'}`}
          subtitle="Wholesale procurement cost"
          icon={DollarSign}
          variant="rose"
        />

        <StatCard
          title="Gross Profit Earned"
          value={`₹${profitSummary.grossProfit?.toFixed(2) || '0.00'}`}
          subtitle={`Margin: ${profitSummary.grossProfitMargin || 0}%`}
          icon={TrendingUp}
          variant="sky"
        />

        <StatCard
          title="Net Estimated Profit"
          value={`₹${profitSummary.netEstimatedProfit?.toFixed(2) || '0.00'}`}
          subtitle={`After ₹${profitSummary.totalDiscountsGiven || 0} customer markdowns`}
          icon={ArrowUpRight}
          variant="indigo"
        />
      </div>

      {/* 2-Column Section */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Category Sales Distribution */}
        <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
          <div>
            <h3 className="text-base font-bold text-white flex items-center gap-2">
              <PieChart className="w-5 h-5 text-emerald-400" />
              Category Revenue Share
            </h3>
            <p className="text-xs text-slate-400">Contribution of each aisle department to store revenue</p>
          </div>

          <div className="space-y-4 pt-2">
            {categorySales.map((cat) => (
              <div key={cat.categoryName} className="space-y-1.5">
                <div className="flex justify-between text-xs font-semibold">
                  <span className="text-white">{cat.categoryName}</span>
                  <div className="flex items-center gap-2 font-mono">
                    <span className="text-slate-400">₹{cat.totalSales?.toFixed(2)}</span>
                    <span className="text-emerald-400 font-bold">{cat.percentageShare}%</span>
                  </div>
                </div>
                <div className="w-full h-2 rounded-full bg-slate-800 overflow-hidden">
                  <div
                    style={{ width: `${cat.percentageShare}%` }}
                    className="h-full bg-gradient-to-r from-emerald-600 to-teal-400 rounded-full"
                  />
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Top 10 Profit-Generating Items */}
        <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
          <div>
            <h3 className="text-base font-bold text-white flex items-center gap-2">
              <TrendingUp className="w-5 h-5 text-emerald-400" />
              Most Profitable Products
            </h3>
            <p className="text-xs text-slate-400">Highest gross profit generating grocery lines</p>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="text-slate-400 uppercase tracking-wider border-b border-slate-800">
                <tr>
                  <th className="pb-3">Product</th>
                  <th className="pb-3 text-center">Qty Sold</th>
                  <th className="pb-3 text-right">Revenue</th>
                  <th className="pb-3 text-right">Gross Profit</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {topProducts.map((p) => (
                  <tr key={p.productId} className="hover:bg-slate-800/40 transition-colors">
                    <td className="py-2.5 font-semibold text-white truncate max-w-[160px]">
                      {p.productName}
                    </td>
                    <td className="py-2.5 text-center font-mono text-slate-300">{p.unitsSold}</td>
                    <td className="py-2.5 text-right font-mono text-slate-300">
                      ₹{p.totalRevenue?.toFixed(2)}
                    </td>
                    <td className="py-2.5 text-right font-mono font-bold text-emerald-400">
                      +₹{p.grossProfit?.toFixed(2)}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};
