import React, { useState } from 'react';
import { Upload, FileSpreadsheet, Download } from 'lucide-react';
import { productService } from '../../services/api';
import { Modal } from '../common/Modal';
import { Button } from '../common/Button';
import { useToast } from '../../context/ToastContext';

export const CsvImportModal = ({
  isOpen,
  onClose,
  onImportSuccess,
}) => {
  const { success, error } = useToast();
  const [file, setFile] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [result, setResult] = useState(null);

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
      setResult(null);
    }
  };

  const handleUpload = async () => {
    if (!file) return;
    try {
      setIsLoading(true);
      const res = await productService.importCsv(file);
      setResult(res);
      if (res && res.importedCount > 0) {
        success(`Successfully imported ${res.importedCount} products!`);
        onImportSuccess();
      } else {
        error('No products could be imported. Please review file format.');
      }
    } catch (err) {
      error(err.response?.data?.message || 'Failed to import CSV');
    } finally {
      setIsLoading(false);
    }
  };

  const downloadSampleTemplate = () => {
    const csvContent =
      'Name,Category,SKU,Barcode,PurchasePrice,SellingPrice,Stock,MinStock,ExpiryDate,Supplier,Unit,Brand\n' +
      'Fortune Sunlite Oil 1L,Cooking Oils & Ghee,SKU-OIL-100,890103099,120.00,150.00,40,10,2026-12-31,Ramesh Agro Traders,pouch,Fortune\n' +
      'Aashirvaad Atta 5kg,Grains & Flours,SKU-ATTA-200,890103098,190.00,235.00,25,8,2026-10-15,ITC Hub,bag,Aashirvaad\n';

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = 'SmartShelf_Product_Import_Sample.csv';
    link.click();
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="Bulk Product Import via CSV"
      subtitle="Import whole product catalogs with prices, barcodes, stock & supplier mappings"
      maxWidth="lg"
    >
      <div className="space-y-5">
        {/* Sample Download Banner */}
        <div className="flex items-center justify-between p-3.5 rounded-xl bg-slate-800 border border-slate-700">
          <div className="flex items-center gap-3">
            <FileSpreadsheet className="w-5 h-5 text-emerald-400" />
            <div className="text-xs">
              <div className="font-semibold text-white">Need the correct CSV format?</div>
              <div className="text-slate-400">Download our sample template with sample items.</div>
            </div>
          </div>
          <Button
            size="sm"
            variant="outline"
            leftIcon={<Download className="w-3.5 h-3.5" />}
            onClick={downloadSampleTemplate}
          >
            Template
          </Button>
        </div>

        {/* Upload Drop Zone */}
        <div className="border-2 border-dashed border-slate-700 hover:border-emerald-500/60 rounded-2xl p-8 text-center bg-slate-800/40 transition-colors">
          <input
            type="file"
            id="csv-file-input"
            accept=".csv"
            onChange={handleFileChange}
            className="hidden"
          />
          <label htmlFor="csv-file-input" className="cursor-pointer flex flex-col items-center gap-2">
            <div className="w-12 h-12 rounded-2xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 flex items-center justify-center">
              <Upload className="w-6 h-6" />
            </div>
            <span className="text-sm font-semibold text-white mt-1">
              {file ? file.name : 'Click to select CSV file'}
            </span>
            <span className="text-xs text-slate-400">Supported format: .csv (Max 10MB)</span>
          </label>
        </div>

        {/* Import Results if any */}
        {result && (
          <div className="p-4 rounded-xl bg-slate-800 border border-slate-700 text-xs space-y-2">
            <div className="flex items-center justify-between font-semibold">
              <span className="text-white">Import Summary:</span>
              <span className="text-emerald-400">✓ {result.importedCount} Imported</span>
            </div>
            <div className="text-slate-400">Total Rows Processed: {result.totalRows}</div>
            {result.skippedCount > 0 && (
              <div className="text-amber-400">Skipped Rows: {result.skippedCount}</div>
            )}
            {result.errors && result.errors.length > 0 && (
              <div className="mt-2 text-rose-400 bg-rose-500/10 p-2.5 rounded-lg border border-rose-500/20 max-h-28 overflow-y-auto">
                <div className="font-semibold mb-1">Errors:</div>
                <ul className="list-disc pl-4 space-y-0.5 text-[11px]">
                  {result.errors.map((err, i) => (
                    <li key={i}>{err}</li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        )}

        <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-800">
          <Button variant="secondary" onClick={onClose}>
            Cancel
          </Button>
          <Button
            variant="primary"
            disabled={!file}
            isLoading={isLoading}
            onClick={handleUpload}
          >
            Import Products
          </Button>
        </div>
      </div>
    </Modal>
  );
};
