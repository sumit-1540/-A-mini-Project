import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  FileText,
  Upload,
  Sparkles,
  ShoppingCart,
  CheckCircle2,
  AlertTriangle,
  Key,
  Cpu,
  Eye,
  Check,
  RotateCcw,
} from 'lucide-react';
import { aiService } from '../services/api';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { useToast } from '../context/ToastContext';

export const HandwrittenListPage = () => {
  const navigate = useNavigate();
  const { success, error } = useToast();

  const [rawText, setRawText] = useState(
    '1. Basmati Rice - 5kg\n2. Aashirvaad Atta - 10kg\n3. Amul Butter - 500g\n4. Tata Salt - 1 pkt\n5. Surf Excel - 1kg\n6. Fortune Oil - 2L'
  );
  const [selectedFile, setSelectedFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [isProcessing, setIsProcessing] = useState(false);
  const [result, setResult] = useState(null);

  // Gemini Configuration State
  const [geminiApiKey, setGeminiApiKey] = useState(
    () => localStorage.getItem('smartshelf_gemini_key') || ''
  );
  const [geminiModel, setGeminiModel] = useState('gemini-1.5-flash');
  const [showConfig, setShowConfig] = useState(false);

  useEffect(() => {
    if (geminiApiKey) {
      localStorage.setItem('smartshelf_gemini_key', geminiApiKey);
    }
  }, [geminiApiKey]);

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      setSelectedFile(file);
      setPreviewUrl(URL.createObjectURL(file));
      setResult(null);
    }
  };

  const handleParseText = async () => {
    if (!rawText.trim()) return;
    try {
      setIsProcessing(true);
      const res = await aiService.parseHandwrittenListText(rawText, geminiApiKey, geminiModel);
      setResult(res);
      const count = res?.items?.length || res?.matchedItems?.length || 0;
      success(`Gemini AI parsed and matched ${count} grocery products!`);
    } catch (err) {
      error(err.response?.data?.message || 'Failed to parse shopping list with Gemini AI');
    } finally {
      setIsProcessing(false);
    }
  };

  const handleUploadPhoto = async () => {
    if (!selectedFile) return;
    try {
      setIsProcessing(true);
      const res = await aiService.uploadHandwrittenListPhoto(selectedFile, geminiApiKey, geminiModel);
      setResult(res);
      const count = res?.items?.length || res?.matchedItems?.length || 0;
      success(`Gemini Vision OCR extracted ${count} items from handwritten slip!`);
    } catch (err) {
      error(err.response?.data?.message || 'Failed to OCR image with Gemini Vision AI');
    } finally {
      setIsProcessing(false);
    }
  };

  const handlePushToPos = () => {
    const list = result?.items || result?.matchedItems || [];
    if (list.length === 0) return;

    const cartItemsToTransfer = list
      .filter((item) => item.matchedProductId)
      .map((item) => ({
        productId: item.matchedProductId,
        quantity: item.requestedQuantity || item.extractedQuantity || 1,
      }));

    if (cartItemsToTransfer.length === 0) {
      error('No matching catalog products found to transfer to POS.');
      return;
    }

    localStorage.setItem('smartshelf_pending_cart', JSON.stringify(cartItemsToTransfer));
    success(`Transferred ${cartItemsToTransfer.length} items to POS terminal!`);
    navigate('/pos');
  };

  const itemsList = result?.items || result?.matchedItems || [];

  return (
    <div className="space-y-6 max-w-5xl mx-auto">
      {/* Header Banner */}
      <div className="p-6 rounded-3xl bg-gradient-to-r from-emerald-950/70 via-slate-900 to-teal-950/50 border border-emerald-500/30 shadow-2xl backdrop-blur-md relative overflow-hidden">
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-500/10 text-emerald-400 text-xs font-semibold border border-emerald-500/20 mb-2">
              <Sparkles className="w-3.5 h-3.5" /> Powered by Google Gemini AI
            </div>
            <h1 className="text-2xl font-extrabold text-white tracking-tight">
              Handwritten Shopping Slip OCR
            </h1>
            <p className="text-xs text-slate-400 mt-1">
              Multimodal Vision AI reads customer handwritten grocery notes or WhatsApp orders and matches them to store SKUs
            </p>
          </div>

          <Button
            size="sm"
            variant="outline"
            leftIcon={<Key className="w-3.5 h-3.5 text-emerald-400" />}
            onClick={() => setShowConfig(!showConfig)}
          >
            {geminiApiKey ? 'Gemini Key Configured ✓' : 'Set Gemini API Key'}
          </Button>
        </div>

        {/* Collapsible Gemini Settings Panel */}
        {showConfig && (
          <div className="mt-4 pt-4 border-t border-slate-800/80 grid grid-cols-1 sm:grid-cols-3 gap-3 bg-slate-900/90 p-4 rounded-2xl border">
            <div className="sm:col-span-2">
              <label className="block text-[11px] font-semibold text-slate-300 mb-1 flex items-center gap-1.5">
                <Key className="w-3.5 h-3.5 text-emerald-400" />
                Google Gemini API Key (Optional Override)
              </label>
              <input
                type="password"
                placeholder="AIzaSy... (leave blank to use backend configured key)"
                value={geminiApiKey}
                onChange={(e) => setGeminiApiKey(e.target.value)}
                className="w-full px-3 py-1.5 bg-slate-800 border border-slate-700 rounded-xl text-xs text-white placeholder:text-slate-500 focus:outline-none focus:border-emerald-500 font-mono"
              />
            </div>

            <div>
              <label className="block text-[11px] font-semibold text-slate-300 mb-1 flex items-center gap-1.5">
                <Cpu className="w-3.5 h-3.5 text-emerald-400" />
                Gemini Vision Model
              </label>
              <select
                value={geminiModel}
                onChange={(e) => setGeminiModel(e.target.value)}
                className="w-full px-3 py-1.5 bg-slate-800 border border-slate-700 rounded-xl text-xs text-white focus:outline-none focus:border-emerald-500"
              >
                <option value="gemini-1.5-flash">Gemini 1.5 Flash (Fast & Accurate)</option>
                <option value="gemini-2.0-flash">Gemini 2.0 Flash (Next-Gen)</option>
                <option value="gemini-1.5-pro">Gemini 1.5 Pro (Deep Reasoning)</option>
              </select>
            </div>
          </div>
        )}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 items-start">
        {/* Left Column: Input Form (Photo OCR or Text) */}
        <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl space-y-5">
          {/* Method 1: Photo Upload */}
          <div>
            <div className="flex items-center justify-between mb-2">
              <h3 className="text-sm font-bold text-white flex items-center gap-2">
                <Upload className="w-4 h-4 text-emerald-400" />
                Method 1: Upload Handwritten Slip Photo
              </h3>
            </div>

            <div className="border border-dashed border-slate-700 hover:border-emerald-500/60 rounded-2xl p-5 text-center bg-slate-800/40 transition-colors">
              <input
                type="file"
                id="list-photo-input"
                accept="image/*"
                onChange={handleFileChange}
                className="hidden"
              />
              <label htmlFor="list-photo-input" className="cursor-pointer flex flex-col items-center gap-2">
                {previewUrl ? (
                  <div className="relative group">
                    <img
                      src={previewUrl}
                      alt="Handwritten Slip Preview"
                      className="max-h-48 rounded-xl object-contain border border-slate-700"
                    />
                    <div className="absolute inset-0 bg-slate-950/60 rounded-xl opacity-0 group-hover:opacity-100 flex items-center justify-center text-xs text-white font-semibold transition-opacity">
                      Click to choose another photo
                    </div>
                  </div>
                ) : (
                  <>
                    <div className="w-12 h-12 rounded-2xl bg-emerald-500/10 text-emerald-400 flex items-center justify-center">
                      <Upload className="w-6 h-6" />
                    </div>
                    <span className="text-xs font-semibold text-white">
                      Upload photo of handwritten slip (JPG, PNG)
                    </span>
                    <span className="text-[11px] text-slate-400">
                      Supports phone camera photos, WhatsApp order screenshots, and paper notes
                    </span>
                  </>
                )}
              </label>
            </div>

            {selectedFile && (
              <Button
                variant="primary"
                className="w-full mt-3"
                isLoading={isProcessing && !!selectedFile}
                leftIcon={<Sparkles className="w-4 h-4" />}
                onClick={handleUploadPhoto}
              >
                Run Gemini Vision OCR ({geminiModel})
              </Button>
            )}
          </div>

          <div className="relative flex py-1 items-center">
            <div className="flex-grow border-t border-slate-800"></div>
            <span className="flex-shrink mx-4 text-[10px] text-slate-500 uppercase font-bold tracking-wider">
              Or Type / Paste Text Slip
            </span>
            <div className="flex-grow border-t border-slate-800"></div>
          </div>

          {/* Method 2: Raw Text Input */}
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">
              Method 2: Paste WhatsApp Order / Customer Notes
            </label>
            <textarea
              rows={6}
              value={rawText}
              onChange={(e) => setRawText(e.target.value)}
              placeholder="e.g. 5kg Basmati rice, 2 pkts Tata salt, 1kg sugar, 500g butter..."
              className="w-full p-3.5 bg-slate-800 border border-slate-700 rounded-2xl text-xs font-mono text-white placeholder:text-slate-500 focus:outline-none focus:border-emerald-500 leading-relaxed"
            />

            <Button
              variant="secondary"
              className="w-full mt-2"
              isLoading={isProcessing && !selectedFile}
              leftIcon={<Sparkles className="w-4 h-4 text-emerald-400" />}
              onClick={handleParseText}
            >
              Parse Text with Gemini AI
            </Button>
          </div>
        </div>

        {/* Right Column: AI Extraction & Catalog Matching */}
        <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-sm font-bold text-white flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-emerald-400" />
                Gemini Matched Products ({itemsList.length})
              </h3>
              {result?.aiModelUsed && (
                <div className="text-[10px] text-emerald-400 font-mono mt-0.5">
                  Engine: {result.aiModelUsed}
                </div>
              )}
            </div>

            {result && result.estimatedTotal > 0 && (
              <div className="text-right">
                <div className="text-[10px] text-slate-400">Estimated Total</div>
                <div className="text-sm font-mono font-bold text-emerald-400">
                  ₹{result.estimatedTotal?.toFixed(2)}
                </div>
              </div>
            )}
          </div>

          {result ? (
            <div className="space-y-4">
              {/* Transcription Box if OCR was performed */}
              {result.rawExtractedText && (
                <div className="p-3 rounded-2xl bg-slate-800/60 border border-slate-700/60">
                  <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-1 flex items-center gap-1">
                    <Eye className="w-3 h-3 text-slate-400" /> OCR Transcription:
                  </div>
                  <div className="text-xs text-slate-300 font-mono whitespace-pre-line leading-relaxed max-h-24 overflow-y-auto">
                    {result.rawExtractedText}
                  </div>
                </div>
              )}

              {/* Matched Items */}
              <div className="space-y-2.5 max-h-80 overflow-y-auto pr-1">
                {itemsList.map((item, idx) => {
                  const qty = item.requestedQuantity || item.extractedQuantity || 1;
                  const unit = item.extractedUnit || 'pcs';
                  const price = item.matchedPrice || 0;
                  const lineTotal = item.estimatedLineTotal || price * qty;

                  return (
                    <div
                      key={idx}
                      className={`p-3.5 rounded-2xl border transition-all flex items-center justify-between gap-3 text-xs ${
                        item.found
                          ? 'bg-slate-800/80 border-slate-700 hover:border-emerald-500/50'
                          : 'bg-rose-950/20 border-rose-500/30'
                      }`}
                    >
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center gap-2">
                          <span className="font-semibold text-white truncate">
                            {item.matchedProductName || item.extractedProductName}
                          </span>
                          {item.found && (
                            <Badge variant="success" size="sm">
                              {Math.round((item.matchConfidence || 0.95) * 100)}% match
                            </Badge>
                          )}
                        </div>

                        <div className="text-[11px] text-slate-400 flex items-center gap-2 mt-1 font-mono">
                          <span className="text-emerald-400 font-bold">
                            Qty: {qty} {unit}
                          </span>
                          {item.found && (
                            <>
                              <span>•</span>
                              <span>Stock: {item.availableStock}</span>
                              <span>•</span>
                              <span>₹{price.toFixed(2)}/u</span>
                            </>
                          )}
                        </div>

                        {item.rawText && (
                          <div className="text-[10px] text-slate-400 mt-0.5 truncate">
                            Slip Line: "{item.rawText}"
                          </div>
                        )}
                      </div>

                      <div className="text-right font-mono shrink-0">
                        {item.found ? (
                          <>
                            <div className="font-bold text-white text-sm">₹{lineTotal.toFixed(2)}</div>
                            <div className="text-[10px] text-emerald-400 flex items-center justify-end gap-0.5">
                              <Check className="w-3 h-3" /> Ready
                            </div>
                          </>
                        ) : (
                          <Badge variant="danger" size="sm">
                            Not in Catalog
                          </Badge>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>

              {/* Unmatched / Out of Stock warnings */}
              {result.unmatchedLines && result.unmatchedLines.length > 0 && (
                <div className="p-3.5 rounded-2xl bg-amber-500/10 border border-amber-500/30 space-y-1.5">
                  <div className="text-xs font-bold text-amber-400 flex items-center gap-1.5">
                    <AlertTriangle className="w-3.5 h-3.5" />
                    Unmatched / Clarification Needed ({result.unmatchedLines.length}):
                  </div>
                  <ul className="list-disc pl-4 text-[11px] text-slate-300 space-y-0.5 font-mono">
                    {result.unmatchedLines.map((line, i) => (
                      <li key={i}>{line}</li>
                    ))}
                  </ul>
                </div>
              )}

              <div className="pt-2">
                <Button
                  variant="primary"
                  size="lg"
                  className="w-full"
                  leftIcon={<ShoppingCart className="w-4 h-4" />}
                  onClick={handlePushToPos}
                >
                  Transfer {itemsList.filter((i) => i.found).length} Items to POS Billing
                </Button>
              </div>
            </div>
          ) : (
            <div className="py-24 flex flex-col items-center justify-center text-center text-slate-500 space-y-2">
              <FileText className="w-12 h-12 stroke-1 text-slate-600" />
              <div className="text-xs max-w-xs">
                Upload a handwritten photo or paste text on the left to see Gemini AI OCR extraction and catalog mapping.
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
