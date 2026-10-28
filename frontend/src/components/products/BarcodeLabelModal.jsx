import React, { useRef } from 'react';
import { Printer, Barcode as BarcodeIcon, Tag, QrCode } from 'lucide-react';
import { Modal } from '../common/Modal';
import { Button } from '../common/Button';

export const BarcodeLabelModal = ({
  isOpen,
  onClose,
  product,
}) => {
  const printRef = useRef(null);

  if (!product) return null;

  const handlePrint = () => {
    const printContent = printRef.current;
    if (!printContent) return;

    const windowPrint = window.open('', '', 'left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0');
    if (!windowPrint) return;

    windowPrint.document.write(`
      <!DOCTYPE html>
      <html>
        <head>
          <title>Shelf Barcode Label - ${product.name}</title>
          <style>
            @page { size: auto; margin: 5mm; }
            body { font-family: monospace, sans-serif; padding: 10px; margin: 0; background: #fff; color: #000; }
            .label-card {
              width: 60mm;
              height: 38mm;
              border: 1.5px dashed #000;
              padding: 6px 8px;
              box-sizing: border-box;
              text-align: center;
              margin: 0 auto 10px auto;
              page-break-inside: avoid;
              display: flex;
              flex-direction: column;
              justify-content: space-between;
            }
            .shop-name { font-size: 8px; font-weight: bold; text-transform: uppercase; letter-spacing: 0.5px; }
            .prod-title { font-size: 11px; font-weight: bold; margin: 2px 0; overflow: hidden; max-height: 24px; }
            .price-row { display: flex; justify-content: space-between; align-items: baseline; font-size: 10px; margin: 2px 0; }
            .price-val { font-size: 15px; font-weight: 900; }
            .barcode-lines { font-family: 'Libre Barcode 128', monospace; font-size: 26px; line-height: 1; letter-spacing: 3px; margin: 2px 0; }
            .barcode-num { font-size: 8px; letter-spacing: 2px; }
            .grid-labels { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; }
          </style>
        </head>
        <body>
          <div class="grid-labels">
            ${Array.from({ length: 6 })
              .map(
                () => `
              <div class="label-card">
                <div class="shop-name">SmartShelf Supermarket</div>
                <div class="prod-title">${product.name}</div>
                <div class="price-row">
                  <span>Unit: ${product.unit}</span>
                  <span class="price-val">₹${product.sellingPrice?.toFixed(2)}</span>
                </div>
                <div class="barcode-lines">||||||||||||||||||||||||</div>
                <div class="barcode-num">${product.barcode || product.sku}</div>
              </div>
            `
              )
              .join('')}
          </div>
          <script>
            window.onload = function() {
              window.focus();
              window.print();
              window.close();
            };
          </script>
        </body>
      </html>
    `);
    windowPrint.document.close();
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title="Printable Shelf Price & Barcode Label"
      subtitle={`Generate adhesive shelf sticker tags for ${product.name}`}
      maxWidth="md"
    >
      <div className="space-y-5">
        {/* Label Preview Card */}
        <div className="flex justify-center">
          <div
            ref={printRef}
            className="w-72 p-4 rounded-2xl bg-white text-slate-950 border-2 border-dashed border-slate-300 shadow-xl space-y-2 text-center select-none"
          >
            <div className="text-[10px] font-bold uppercase tracking-widest text-slate-500">
              SmartShelf Supermarket
            </div>
            <div className="text-sm font-extrabold line-clamp-2 leading-snug">
              {product.name}
            </div>

            <div className="flex items-baseline justify-between px-2 pt-1 border-t border-slate-200">
              <span className="text-xs text-slate-500 font-medium">Per {product.unit}</span>
              <span className="text-xl font-black text-slate-950 font-mono">
                ₹{product.sellingPrice?.toFixed(2)}
              </span>
            </div>

            {/* Simulated Barcode Graphic */}
            <div className="pt-2">
              <div className="flex justify-center items-center h-10 gap-0.5 overflow-hidden">
                {[2, 1, 3, 1, 2, 4, 1, 2, 1, 3, 2, 1, 4, 2, 1, 3, 1, 2, 3, 1, 2, 4, 1, 2].map(
                  (w, i) => (
                    <div
                      key={i}
                      style={{ width: `${w * 1.5}px` }}
                      className={`h-full ${i % 2 === 0 ? 'bg-black' : 'bg-transparent'}`}
                    />
                  )
                )}
              </div>
              <div className="text-[11px] font-mono tracking-widest text-slate-700 mt-1 font-bold">
                {product.barcode || product.sku}
              </div>
            </div>

            {product.aisle || product.shelf ? (
              <div className="text-[9px] text-slate-400 font-mono pt-1 border-t border-slate-100">
                Loc: {product.aisle || 'Aisle'} / {product.shelf || 'Shelf'}
              </div>
            ) : null}
          </div>
        </div>

        {/* Info Pill */}
        <div className="p-3.5 rounded-xl bg-slate-800 border border-slate-700 text-xs text-slate-300 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Tag className="w-4 h-4 text-emerald-400" />
            <span>Ready for 60mm &times; 38mm sticker thermal printers</span>
          </div>
          <span className="text-emerald-400 font-bold font-mono">6 per sheet</span>
        </div>

        <div className="flex justify-end gap-3 pt-3 border-t border-slate-800">
          <Button variant="secondary" onClick={onClose}>
            Close
          </Button>
          <Button
            variant="primary"
            leftIcon={<Printer className="w-4 h-4" />}
            onClick={handlePrint}
          >
            Print Barcode Stickers
          </Button>
        </div>
      </div>
    </Modal>
  );
};
