import React from 'react';
import { Download, FileSpreadsheet, FileText, Database } from 'lucide-react';
import { reportService } from '../services/api';
import { Button } from '../components/common/Button';

export const ReportsPage = () => {
  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-extrabold text-white tracking-tight flex items-center gap-2.5">
          <Download className="w-7 h-7 text-emerald-400" />
          Business Reports & CSV Exports
        </h1>
        <p className="text-xs text-slate-400 mt-1">
          Export store registers for accounting, GST filings, inventory valuation, and audits
        </p>
      </div>

      {/* Reports Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
        {/* Inventory Report */}
        <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl flex flex-col justify-between space-y-4">
          <div>
            <div className="w-12 h-12 rounded-2xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 flex items-center justify-center mb-3">
              <FileSpreadsheet className="w-6 h-6" />
            </div>
            <h3 className="text-base font-bold text-white">Inventory Valuation</h3>
            <p className="text-xs text-slate-400 mt-1.5 leading-relaxed">
              Complete catalog with current shelf stock, wholesale cost, retail valuation, and expiry dates.
            </p>
          </div>

          <Button
            variant="primary"
            className="w-full"
            leftIcon={<Download className="w-4 h-4" />}
            onClick={() => reportService.downloadInventoryCsv()}
          >
            Export Inventory CSV
          </Button>
        </div>

        {/* Sales Register */}
        <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl flex flex-col justify-between space-y-4">
          <div>
            <div className="w-12 h-12 rounded-2xl bg-sky-500/10 border border-sky-500/20 text-sky-400 flex items-center justify-center mb-3">
              <FileText className="w-6 h-6" />
            </div>
            <h3 className="text-base font-bold text-white">Sales & Revenue Register</h3>
            <p className="text-xs text-slate-400 mt-1.5 leading-relaxed">
              Itemized billing history, cashier IDs, payment modes, GST amounts, and gross profit margins.
            </p>
          </div>

          <Button
            variant="primary"
            className="w-full"
            leftIcon={<Download className="w-4 h-4" />}
            onClick={() => reportService.downloadSalesCsv()}
          >
            Export Sales CSV
          </Button>
        </div>

        {/* Customer Credit Ledger */}
        <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl flex flex-col justify-between space-y-4">
          <div>
            <div className="w-12 h-12 rounded-2xl bg-rose-500/10 border border-rose-500/20 text-rose-400 flex items-center justify-center mb-3">
              <Database className="w-6 h-6" />
            </div>
            <h3 className="text-base font-bold text-white">Customer Credit Ledger</h3>
            <p className="text-xs text-slate-400 mt-1.5 leading-relaxed">
              Customer balance summary with phone numbers, lifetime purchases, and pending dues.
            </p>
          </div>

          <Button
            variant="primary"
            className="w-full"
            leftIcon={<Download className="w-4 h-4" />}
            onClick={() => reportService.downloadCreditCsv()}
          >
            Export Credit CSV
          </Button>
        </div>
      </div>
    </div>
  );
};
