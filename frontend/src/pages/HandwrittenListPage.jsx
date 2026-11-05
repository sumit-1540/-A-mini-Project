import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  FileText,
  Upload,
  Camera,
  Sparkles,
  ShoppingCart,
  CheckCircle2,
  AlertTriangle,
  Key,
  Cpu,
  Eye,
  Check,
  RotateCcw,
  Trash2,
  Plus,
  Minus,
  RefreshCw,
  Zap,
  X,
  ChevronDown,
  ChevronUp,
  Image as ImageIcon,
  Edit3,
} from 'lucide-react';
import Tesseract from 'tesseract.js';
import { aiService } from '../services/api';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { useToast } from '../context/ToastContext';

export const HandwrittenListPage = () => {
  const navigate = useNavigate();
  const { success, error, warning } = useToast();
  const fileInputRef = useRef(null);
  const cameraInputRef = useRef(null);

  // Tab State: 'photo' (Image Upload / Camera) vs 'text' (Direct Input / WhatsApp)
  const [activeTab, setActiveTab] = useState('photo');

  // Input Data States
  const [rawText, setRawText] = useState(
    '1. Basmati Rice - 5kg\n2. Aashirvaad Atta - 10kg\n3. Tata Salt - 1 pkt\n4. Amul Butter - 500g\n5. Amul Milk - 2L\n6. Fortune Oil - 1L\n7. Red Label Tea - 500g\n8. Maggi Noodles - 2 pack'
  );
  const [selectedFile, setSelectedFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [isProcessing, setIsProcessing] = useState(false);
  const [ocrProgress, setOcrProgress] = useState(null); // { status: string, progress: number }
  const [result, setResult] = useState(null);
  const [showRawTextEditor, setShowRawTextEditor] = useState(false);

  // Gemini Configuration State
  const DEFAULT_GEMINI_KEY = atob('QVEuQWI4Uk42Sk9HYnBUTHB5M2hPU3NIMk9qTVNEblFhSmxYd0t4UFRhTWpLRXBmRHBvQVE=');
  const [geminiApiKey, setGeminiApiKey] = useState(
    () => localStorage.getItem('smartshelf_gemini_key') || DEFAULT_GEMINI_KEY
  );
  const [geminiModel, setGeminiModel] = useState('gemini-3.1-pro-preview');
  const [showConfig, setShowConfig] = useState(false);

  useEffect(() => {
    if (geminiApiKey) {
      localStorage.setItem('smartshelf_gemini_key', geminiApiKey);
    }
  }, [geminiApiKey]);

  // Support clipboard paste (Ctrl+V) anywhere on page
  useEffect(() => {
    const handlePaste = (e) => {
      const items = e.clipboardData?.items;
      if (!items) return;
      for (let i = 0; i < items.length; i++) {
        if (items[i].type.indexOf('image') !== -1) {
          const blob = items[i].getAsFile();
          setSelectedFile(blob);
          setPreviewUrl(URL.createObjectURL(blob));
          setResult(null);
          setActiveTab('photo');
          success('Pasted slip image from clipboard!');
          break;
        }
      }
    };
    window.addEventListener('paste', handlePaste);
    return () => window.removeEventListener('paste', handlePaste);
  }, [success]);

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      setSelectedFile(file);
      setPreviewUrl(URL.createObjectURL(file));
      setResult(null);
    }
  };

  const handleClearImage = () => {
    setSelectedFile(null);
    setPreviewUrl(null);
    setOcrProgress(null);
    if (fileInputRef.current) fileInputRef.current.value = '';
    if (cameraInputRef.current) cameraInputRef.current.value = '';
  };

  // Run On-Device Tesseract Neural OCR (Free, Client-Side, Instant)
  const handleRunTesseractOcr = async () => {
    if (!selectedFile) return;
    try {
      setIsProcessing(true);
      setOcrProgress({ status: 'Starting Neural OCR engine...', progress: 10 });

      const res = await Tesseract.recognize(selectedFile, 'eng', {
        logger: (m) => {
          if (m.status === 'recognizing text') {
            const pct = Math.round((m.progress || 0) * 100);
            setOcrProgress({
              status: `Transcribing handwritten text (${pct}%)...`,
              progress: pct,
            });
          } else {
            setOcrProgress({
              status: `${m.status}...`,
              progress: Math.round((m.progress || 0) * 100),
            });
          }
        },
      });

      const extractedText = (res?.data?.text || '').trim();
      if (!extractedText) {
        warning('Could not detect clear text from image. Try another picture or type items directly.');
        return;
      }

      setRawText(extractedText);
      setOcrProgress({ status: 'Matching items with store catalog...', progress: 100 });

      const parsedRes = await aiService.parseHandwrittenListText(extractedText, geminiApiKey, geminiModel);
      parsedRes.aiModelUsed = 'Tesseract Neural OCR (On-Device) + SmartShelf Matcher';
      parsedRes.rawExtractedText = extractedText;
      setResult(parsedRes);
      const count = parsedRes?.items?.length || parsedRes?.matchedItems?.length || 0;
      success(`Transcribed and matched ${count} grocery products!`);
    } catch (err) {
      console.error('OCR Error:', err);
      error('Failed to run OCR on image: ' + (err.message || ''));
    } finally {
      setIsProcessing(false);
      setOcrProgress(null);
    }
  };

  // Run Google Gemini Multimodal Vision AI
  const handleRunGeminiOcr = async () => {
    if (!selectedFile) return;
    try {
      setIsProcessing(true);
      const res = await aiService.uploadHandwrittenListPhoto(selectedFile, geminiApiKey, geminiModel);
      setResult(res);
      if (res?.rawExtractedText) {
        setRawText(res.rawExtractedText);
      }
      const count = res?.items?.length || res?.matchedItems?.length || 0;
      success(`Gemini Vision AI processed slip: ${count} items matched!`);
    } catch (err) {
      console.error('Gemini Vision error:', err);
      const msg = err.response?.data?.message || err.message || 'Gemini Vision AI failed';
      error(`${msg}. You can also use the On-Device OCR button!`);
    } finally {
      setIsProcessing(false);
    }
  };

  // Parse Text Input
  const handleParseText = async (textToParse = rawText) => {
    if (!textToParse || !textToParse.trim()) {
      error('Please enter grocery items to match.');
      return;
    }
    try {
      setIsProcessing(true);
      const res = await aiService.parseHandwrittenListText(textToParse, geminiApiKey, geminiModel);
      setResult(res);
      const count = res?.items?.length || res?.matchedItems?.length || 0;
      success(`Matched ${count} products with store catalog!`);
    } catch (err) {
      console.error('Parse error:', err);
      error(err.response?.data?.message || 'Failed to match shopping list with catalog');
    } finally {
      setIsProcessing(false);
    }
  };

  // Live Quantity Controls
  const handleQuantityChange = (idx, delta) => {
    if (!result || !result.items) return;
    const newItems = [...result.items];
    const item = newItems[idx];
    const currentQty = item.requestedQuantity || item.extractedQuantity || 1;
    const newQty = Math.max(1, currentQty + delta);
    item.requestedQuantity = newQty;
    item.extractedQuantity = newQty;
    if (item.matchedPrice) {
      item.estimatedLineTotal = item.matchedPrice * newQty;
    }
    const newTotal = newItems.reduce((acc, curr) => acc + (curr.estimatedLineTotal || 0), 0);
    setResult({ ...result, items: newItems, estimatedTotal: newTotal });
  };

  // Remove Item
  const handleRemoveItem = (idx) => {
    if (!result || !result.items) return;
    const newItems = result.items.filter((_, i) => i !== idx);
    const newTotal = newItems.reduce((acc, curr) => acc + (curr.estimatedLineTotal || 0), 0);
    const matchedCount = newItems.filter((i) => i.found).length;
    setResult({
      ...result,
      items: newItems,
      estimatedTotal: newTotal,
      totalExtractedItems: newItems.length,
      successfullyMatchedItems: matchedCount,
    });
  };

  // Push to POS Billing
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

  const loadSample = () => {
    setRawText(
      '1. Basmati Rice 5kg\n2. Aashirvaad Atta 10kg\n3. Tata Salt 1kg\n4. Amul Butter 500g\n5. Amul Milk 2L\n6. Fortune Sunflower Oil 1L\n7. Red Label Tea 500g\n8. Maggi Noodles 2 packs'
    );
  };

  const itemsList = result?.items || result?.matchedItems || [];

  return (
    <div className="space-y-6 max-w-5xl mx-auto pb-12">
      {/* Sleek Top Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-slate-900/80 p-5 rounded-3xl border border-slate-800 shadow-xl backdrop-blur-md">
        <div>
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-500/10 text-emerald-400 text-xs font-semibold border border-emerald-500/20 mb-1.5">
            <Sparkles className="w-3.5 h-3.5" /> SmartSlip Scanner
          </div>
          <h1 className="text-2xl font-black text-white tracking-tight">
            Grocery Slip & WhatsApp Order Scanner
          </h1>
          <p className="text-xs text-slate-400 mt-0.5">
            Scan paper notes, customer slips, or paste WhatsApp orders to instantly create POS bills
          </p>
        </div>

        <div className="flex items-center gap-2">
          <Button
            size="sm"
            variant="outline"
            leftIcon={<Key className="w-3.5 h-3.5 text-emerald-400" />}
            onClick={() => setShowConfig(!showConfig)}
          >
            {geminiApiKey ? 'Gemini Pro Active ✓' : 'AI Settings'}
          </Button>
        </div>
      </div>

      {/* Collapsible Gemini Settings Panel */}
      {showConfig && (
        <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 grid grid-cols-1 sm:grid-cols-3 gap-3 shadow-lg">
          <div className="sm:col-span-2">
            <label className="block text-xs font-semibold text-slate-300 mb-1 flex items-center justify-between">
              <span className="flex items-center gap-1.5">
                <Key className="w-3.5 h-3.5 text-emerald-400" />
                Google Gemini API Key
              </span>
              <a
                href="https://aistudio.google.com/app/apikey"
                target="_blank"
                rel="noreferrer"
                className="text-[10px] text-emerald-400 underline hover:text-emerald-300"
              >
                Google AI Studio ↗
              </a>
            </label>
            <input
              type="password"
              placeholder="Enter Gemini API key (or leave default)"
              value={geminiApiKey}
              onChange={(e) => setGeminiApiKey(e.target.value)}
              className="w-full px-3 py-2 bg-slate-800 border border-slate-700 rounded-xl text-xs text-white placeholder:text-slate-500 focus:outline-none focus:border-emerald-500 font-mono"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1 flex items-center gap-1.5">
              <Cpu className="w-3.5 h-3.5 text-emerald-400" />
              Vision Model
            </label>
            <select
              value={geminiModel}
              onChange={(e) => setGeminiModel(e.target.value)}
              className="w-full px-3 py-2 bg-slate-800 border border-slate-700 rounded-xl text-xs text-white focus:outline-none focus:border-emerald-500"
            >
              <option value="gemini-3.1-pro-preview">Gemini Pro (High Accuracy OCR)</option>
              <option value="gemini-3.5-flash">Gemini 3.5 Flash (Ultra Fast Vision)</option>
              <option value="gemini-pro-latest">Gemini Pro Latest</option>
            </select>
          </div>
        </div>
      )}

      {/* Clean Mode Switcher Tabs */}
      <div className="flex p-1.5 bg-slate-900 rounded-2xl border border-slate-800 max-w-md mx-auto shadow-md">
        <button
          type="button"
          onClick={() => setActiveTab('photo')}
          className={`flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-xs font-bold transition-all ${
            activeTab === 'photo'
              ? 'bg-emerald-600 text-white shadow-md shadow-emerald-950/40'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          <Camera className="w-4 h-4" />
          <span>Scan Photo or Slip</span>
        </button>

        <button
          type="button"
          onClick={() => setActiveTab('text')}
          className={`flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-xs font-bold transition-all ${
            activeTab === 'text'
              ? 'bg-emerald-600 text-white shadow-md shadow-emerald-950/40'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          <Edit3 className="w-4 h-4" />
          <span>Type / Paste Text</span>
        </button>
      </div>

      {/* Main Action Area */}
      {activeTab === 'photo' ? (
        /* Tab 1: Photo Scanner */
        <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
          <input
            ref={fileInputRef}
            type="file"
            id="list-photo-input"
            accept="image/*"
            onChange={handleFileChange}
            className="hidden"
          />
          <input
            ref={cameraInputRef}
            type="file"
            id="camera-photo-input"
            accept="image/*"
            capture="environment"
            onChange={handleFileChange}
            className="hidden"
          />

          {!previewUrl ? (
            /* Empty State / Dropzone */
            <div className="border-2 border-dashed border-slate-700/80 hover:border-emerald-500/60 rounded-3xl p-10 text-center bg-slate-800/20 transition-all space-y-4">
              <div className="w-16 h-16 rounded-3xl bg-emerald-500/10 text-emerald-400 flex items-center justify-center mx-auto shadow-inner">
                <ImageIcon className="w-8 h-8" />
              </div>

              <div className="space-y-1">
                <div className="text-base font-bold text-white">
                  Upload customer slip or snap with camera
                </div>
                <div className="text-xs text-slate-400 max-w-sm mx-auto">
                  Supports JPG, PNG photos of paper notes, WhatsApp screenshots, or paste directly with <kbd className="px-1.5 py-0.5 rounded bg-slate-800 border border-slate-700 font-mono text-[10px] text-slate-300">Ctrl+V</kbd>
                </div>
              </div>

              <div className="flex items-center justify-center gap-3 pt-2">
                <Button
                  size="md"
                  variant="primary"
                  leftIcon={<Upload className="w-4 h-4" />}
                  onClick={() => fileInputRef.current?.click()}
                >
                  Choose Image File
                </Button>

                <Button
                  size="md"
                  variant="outline"
                  leftIcon={<Camera className="w-4 h-4 text-emerald-400" />}
                  onClick={() => cameraInputRef.current?.click()}
                >
                  Take Camera Photo
                </Button>
              </div>
            </div>
          ) : (
            /* Image Preview Card */
            <div className="space-y-4">
              <div className="relative rounded-2xl overflow-hidden border border-slate-700 bg-slate-950 p-4 max-w-lg mx-auto flex items-center justify-center">
                <img
                  src={previewUrl}
                  alt="Customer Slip Preview"
                  className="max-h-72 object-contain rounded-xl"
                />
                <button
                  type="button"
                  onClick={handleClearImage}
                  className="absolute top-6 right-6 p-2 rounded-xl bg-slate-900/80 border border-slate-700 text-rose-400 hover:text-white hover:bg-rose-600 transition-colors shadow-lg"
                  title="Remove photo"
                >
                  <X className="w-4 h-4" />
                </button>
              </div>

              {/* OCR Progress Bar */}
              {ocrProgress && (
                <div className="max-w-lg mx-auto p-4 rounded-2xl bg-emerald-950/40 border border-emerald-500/40 space-y-2">
                  <div className="flex items-center justify-between text-xs text-emerald-300 font-semibold">
                    <span className="flex items-center gap-2">
                      <RefreshCw className="w-4 h-4 animate-spin text-emerald-400" />
                      {ocrProgress.status}
                    </span>
                    <span className="font-mono">{ocrProgress.progress}%</span>
                  </div>
                  <div className="w-full bg-slate-800 h-2.5 rounded-full overflow-hidden">
                    <div
                      className="bg-emerald-500 h-full transition-all duration-300"
                      style={{ width: `${ocrProgress.progress}%` }}
                    />
                  </div>
                </div>
              )}

              {/* Action Buttons */}
              {!ocrProgress && (
                <div className="flex flex-col sm:flex-row items-center justify-center gap-3 max-w-lg mx-auto pt-1">
                  <Button
                    variant="primary"
                    size="md"
                    className="w-full sm:flex-1 bg-gradient-to-r from-amber-500 to-emerald-500 text-slate-950 font-bold hover:from-amber-400 hover:to-emerald-400"
                    isLoading={isProcessing && !geminiApiKey}
                    leftIcon={<Zap className="w-4 h-4 fill-slate-950" />}
                    onClick={handleRunTesseractOcr}
                  >
                    Run On-Device OCR (Instant)
                  </Button>

                  <Button
                    variant="secondary"
                    size="md"
                    className="w-full sm:flex-1"
                    isLoading={isProcessing}
                    leftIcon={<Sparkles className="w-4 h-4 text-emerald-400" />}
                    onClick={handleRunGeminiOcr}
                  >
                    Gemini Vision AI
                  </Button>
                </div>
              )}
            </div>
          )}
        </div>
      ) : (
        /* Tab 2: Text / WhatsApp Notes */
        <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl space-y-4">
          <div className="flex items-center justify-between">
            <label className="text-xs font-bold text-white flex items-center gap-2">
              <FileText className="w-4 h-4 text-emerald-400" />
              Customer Grocery List / WhatsApp Message
            </label>
            <div className="flex items-center gap-3">
              <button
                type="button"
                onClick={loadSample}
                className="text-xs text-indigo-400 hover:text-indigo-300 font-semibold"
              >
                Load Sample
              </button>
              <button
                type="button"
                onClick={() => setRawText('')}
                className="text-xs text-slate-400 hover:text-slate-300"
              >
                Clear
              </button>
            </div>
          </div>

          <textarea
            rows={7}
            value={rawText}
            onChange={(e) => setRawText(e.target.value)}
            placeholder="Type or paste items here...&#10;e.g.&#10;1. Basmati Rice 5kg&#10;2. Aashirvaad Atta 10kg&#10;3. Tata Salt 1kg&#10;4. Amul Butter 500g"
            className="w-full p-4 bg-slate-800 border border-slate-700 rounded-2xl text-xs font-mono text-white placeholder:text-slate-500 focus:outline-none focus:border-emerald-500 leading-relaxed resize-y"
          />

          <Button
            variant="primary"
            size="md"
            className="w-full sm:w-auto"
            isLoading={isProcessing}
            leftIcon={<Sparkles className="w-4 h-4" />}
            onClick={() => handleParseText(rawText)}
          >
            Match List with Store Inventory
          </Button>
        </div>
      )}

      {/* Matched Results Section */}
      {result && (
        <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 shadow-xl space-y-5">
          {/* Result Header & Subtotal */}
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-800">
            <div>
              <div className="flex items-center gap-2">
                <CheckCircle2 className="w-5 h-5 text-emerald-400" />
                <h2 className="text-lg font-bold text-white">
                  Matched Products ({itemsList.length})
                </h2>
              </div>
              {result.aiModelUsed && (
                <div className="text-[11px] text-emerald-400 font-mono mt-0.5">
                  Engine: {result.aiModelUsed}
                </div>
              )}
            </div>

            {result.estimatedTotal > 0 && (
              <div className="bg-slate-800/80 px-4 py-2 rounded-2xl border border-slate-700/60 sm:text-right">
                <div className="text-[10px] text-slate-400 font-semibold uppercase tracking-wider">
                  Estimated Cart Subtotal
                </div>
                <div className="text-xl font-black font-mono text-emerald-400">
                  ₹{Number(result.estimatedTotal).toFixed(2)}
                </div>
              </div>
            )}
          </div>

          {/* Collapsible Raw Text Editor */}
          {result.rawExtractedText && (
            <div className="rounded-2xl border border-slate-800 bg-slate-950/60 overflow-hidden">
              <button
                type="button"
                onClick={() => setShowRawTextEditor(!showRawTextEditor)}
                className="w-full px-4 py-2.5 flex items-center justify-between text-xs text-slate-400 hover:text-white transition-colors"
              >
                <span className="flex items-center gap-1.5 font-semibold">
                  <Eye className="w-3.5 h-3.5 text-slate-400" />
                  View & Edit Recognized Text ({result.rawExtractedText.split('\n').filter(Boolean).length} lines)
                </span>
                {showRawTextEditor ? (
                  <ChevronUp className="w-4 h-4 text-slate-400" />
                ) : (
                  <ChevronDown className="w-4 h-4 text-slate-400" />
                )}
              </button>

              {showRawTextEditor && (
                <div className="p-4 pt-1 space-y-3 border-t border-slate-800/80">
                  <textarea
                    rows={4}
                    value={rawText}
                    onChange={(e) => setRawText(e.target.value)}
                    className="w-full p-3 bg-slate-900 border border-slate-700 rounded-xl text-xs font-mono text-white focus:outline-none focus:border-emerald-500"
                  />
                  <Button
                    size="sm"
                    variant="secondary"
                    leftIcon={<RefreshCw className="w-3.5 h-3.5" />}
                    onClick={() => handleParseText(rawText)}
                  >
                    Re-analyze Edited Text
                  </Button>
                </div>
              )}
            </div>
          )}

          {/* Matched Product Cards */}
          <div className="space-y-3">
            {itemsList.map((item, idx) => {
              const qty = item.requestedQuantity || item.extractedQuantity || 1;
              const unit = item.extractedUnit || 'pcs';
              const price = item.matchedPrice || 0;
              const lineTotal = item.estimatedLineTotal || price * qty;

              return (
                <div
                  key={idx}
                  className={`p-4 rounded-2xl border transition-all flex flex-col sm:flex-row sm:items-center justify-between gap-3 ${
                    item.found
                      ? 'bg-slate-800/70 border-slate-700/80 hover:border-emerald-500/50'
                      : 'bg-rose-950/20 border-rose-500/30'
                  }`}
                >
                  <div className="flex-1 min-w-0 space-y-1">
                    <div className="flex items-center gap-2 flex-wrap">
                      <span className="font-bold text-white text-sm">
                        {item.matchedProductName || item.extractedProductName}
                      </span>
                      {item.found ? (
                        <Badge variant="success" size="sm">
                          {Math.round((item.matchConfidence || 0.95) * 100)}% match
                        </Badge>
                      ) : (
                        <Badge variant="danger" size="sm">
                          Not in Catalog
                        </Badge>
                      )}
                      {item.matchedSku && (
                        <span className="text-[10px] text-slate-400 font-mono bg-slate-900 px-2 py-0.5 rounded-lg border border-slate-800">
                          {item.matchedSku}
                        </span>
                      )}
                    </div>

                    <div className="text-xs text-slate-400 flex items-center gap-2 font-mono flex-wrap">
                      {item.found && (
                        <>
                          <span>In Stock: {item.availableStock ?? 'N/A'}</span>
                          <span>•</span>
                          <span>₹{Number(price).toFixed(2)} / {unit}</span>
                        </>
                      )}
                      {item.rawText && item.rawText !== item.matchedProductName && (
                        <span className="text-slate-500 italic text-[11px]">
                          (Slip: "{item.rawText}")
                        </span>
                      )}
                    </div>
                  </div>

                  {/* Quantity Stepper & Price */}
                  <div className="flex items-center justify-between sm:justify-end gap-3 shrink-0 pt-2 sm:pt-0 border-t sm:border-t-0 border-slate-800">
                    {item.found && (
                      <div className="flex items-center gap-1.5 bg-slate-900 px-2.5 py-1 rounded-xl border border-slate-700">
                        <button
                          type="button"
                          onClick={() => handleQuantityChange(idx, -1)}
                          className="w-6 h-6 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 flex items-center justify-center transition-colors"
                          title="Decrease quantity"
                        >
                          <Minus className="w-3 h-3" />
                        </button>
                        <span className="font-mono font-bold text-white px-2 text-xs min-w-[36px] text-center">
                          {qty} {unit}
                        </span>
                        <button
                          type="button"
                          onClick={() => handleQuantityChange(idx, 1)}
                          className="w-6 h-6 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 flex items-center justify-center transition-colors"
                          title="Increase quantity"
                        >
                          <Plus className="w-3 h-3" />
                        </button>
                      </div>
                    )}

                    <div className="text-right font-mono min-w-[80px]">
                      {item.found ? (
                        <>
                          <div className="font-bold text-white text-base">
                            ₹{Number(lineTotal).toFixed(2)}
                          </div>
                          <div className="text-[10px] text-emerald-400 flex items-center justify-end gap-0.5">
                            <Check className="w-3 h-3" /> Ready
                          </div>
                        </>
                      ) : (
                        <span className="text-xs text-rose-400 font-semibold">Unavailable</span>
                      )}
                    </div>

                    <button
                      type="button"
                      onClick={() => handleRemoveItem(idx)}
                      className="p-2 rounded-xl hover:bg-rose-950/40 text-slate-500 hover:text-rose-400 transition-colors"
                      title="Remove item"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              );
            })}
          </div>

          {/* Unmatched Warning Card */}
          {result.unmatchedLines && result.unmatchedLines.length > 0 && (
            <div className="p-4 rounded-2xl bg-amber-500/10 border border-amber-500/30 space-y-1.5">
              <div className="text-xs font-bold text-amber-400 flex items-center gap-1.5">
                <AlertTriangle className="w-4 h-4" />
                Items not found in store catalog ({result.unmatchedLines.length}):
              </div>
              <ul className="list-disc pl-5 text-xs text-slate-300 space-y-0.5 font-mono">
                {result.unmatchedLines.map((line, i) => (
                  <li key={i}>{line}</li>
                ))}
              </ul>
            </div>
          )}

          {/* Sticky/Prominent POS Transfer CTA */}
          <div className="pt-2">
            <Button
              variant="primary"
              size="lg"
              className="w-full py-4 text-base font-extrabold bg-gradient-to-r from-emerald-500 to-teal-500 text-slate-950 shadow-xl shadow-emerald-950/50 hover:from-emerald-400 hover:to-teal-400"
              leftIcon={<ShoppingCart className="w-5 h-5 fill-slate-950" />}
              onClick={handlePushToPos}
            >
              Transfer {itemsList.filter((i) => i.found).length} Items to POS Billing (₹{Number(result.estimatedTotal || 0).toFixed(2)}) →
            </Button>
          </div>
        </div>
      )}
    </div>
  );
};
