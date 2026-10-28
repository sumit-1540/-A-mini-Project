import React, { useState, useRef, useEffect } from 'react';
import {
  Search,
  ShoppingCart,
  Plus,
  Minus,
  Trash2,
  Zap,
  Banknote,
  QrCode,
  CreditCard,
  Sparkles,
  Package,
  Layers,
  ArrowRight,
  Check,
} from 'lucide-react';
import { Button } from '../common/Button';

export const ExpressWalkthroughPos = ({
  products,
  categories,
  cart,
  setCart,
  onCheckout,
  isProcessing,
  onOpenUpiModal,
  subtotal,
  grandTotal,
  addToCart,
  updateQuantity,
  removeFromCart,
  clearCart,
}) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [paymentMode, setPaymentMode] = useState('CASH'); // 'CASH' | 'UPI_QR' | 'CARD'
  const [cashTendered, setCashTendered] = useState(grandTotal);
  const searchInputRef = useRef(null);

  // Auto-focus search input for instant typing
  useEffect(() => {
    searchInputRef.current?.focus();
  }, []);

  // Sync cash tendered with grand total
  useEffect(() => {
    setCashTendered(grandTotal);
  }, [grandTotal]);

  const changeToReturn = Math.max(0, cashTendered - grandTotal);

  // Filter products directly from database catalog
  const filteredProducts = products.filter((p) => {
    const matchesCat = selectedCategory ? p.categoryId === selectedCategory : true;
    const matchesSearch = searchQuery.trim()
      ? p.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        p.sku?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (p.categoryName && p.categoryName.toLowerCase().includes(searchQuery.toLowerCase()))
      : true;
    return matchesCat && matchesSearch;
  });

  // Top matching products for instant auto-complete suggestions
  const searchMatches = searchQuery.trim() ? filteredProducts.slice(0, 5) : [];

  const handleSearchKeyDown = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      if (searchMatches.length > 0) {
        addToCart(searchMatches[0], 1);
        setSearchQuery('');
      }
    }
  };

  // Fast cash presets
  const cashPresets = [
    { label: 'Exact', value: grandTotal },
    { label: '₹50', value: 50 },
    { label: '₹100', value: 100 },
    { label: '₹200', value: 200 },
    { label: '₹500', value: 500 },
    { label: '₹2,000', value: 2000 },
  ];

  return (
    <div className="space-y-4">
      {/* Walk-in Mode Banner */}
      <div className="p-3.5 rounded-2xl bg-gradient-to-r from-amber-500/10 via-slate-900 to-emerald-500/10 border border-amber-500/20 flex flex-wrap items-center justify-between gap-2">
        <div className="flex items-center gap-2.5">
          <span className="p-2 rounded-xl bg-amber-500/20 text-amber-400">
            <Zap className="w-4 h-4 fill-amber-400" />
          </span>
          <div>
            <div className="text-xs font-bold text-white flex items-center gap-2">
              Express Walk-in POS
              <span className="text-[10px] bg-emerald-500/20 text-emerald-400 px-2 py-0.5 rounded-full border border-emerald-500/30">
                Direct Database Entry
              </span>
            </div>
            <div className="text-[11px] text-slate-400">
              Type or select items directly from store inventory • No barcode scanner machine required • Zero customer sign-up
            </div>
          </div>
        </div>

        <div className="text-right">
          <span className="text-[10px] text-slate-400">Database Catalog:</span>
          <span className="ml-1 text-xs font-mono font-bold text-amber-400">
            {products.length} Products Available
          </span>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-5 items-start">
        {/* LEFT COLUMN: Database Search & Product Grid (7 Cols) */}
        <div className="lg:col-span-7 space-y-4">
          {/* Instant Database Product Search Box */}
          <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl space-y-3 relative">
            <div className="relative">
              <Search className="w-5 h-5 text-amber-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
              <input
                ref={searchInputRef}
                type="text"
                placeholder="🔍 Search items by name (e.g. Milk, Rice, Sugar, Tea, Oil)... [Hit Enter to Add]"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                onKeyDown={handleSearchKeyDown}
                className="w-full pl-11 pr-24 py-3 bg-slate-800/90 border-2 border-amber-500/40 rounded-xl text-sm text-white font-medium placeholder:text-slate-500 focus:outline-none focus:border-amber-400 focus:ring-2 focus:ring-amber-400/20 shadow-inner"
              />
              {searchQuery && (
                <button
                  type="button"
                  onClick={() => setSearchQuery('')}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-xs text-slate-400 hover:text-white bg-slate-700 px-2 py-0.5 rounded"
                >
                  Clear
                </button>
              )}
            </div>

            {/* Live Search Autocomplete Dropdown */}
            {searchQuery.trim() && searchMatches.length > 0 && (
              <div className="p-2 bg-slate-950/95 border border-amber-500/30 rounded-xl shadow-2xl divide-y divide-slate-800/60 max-h-56 overflow-y-auto">
                <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider px-2 py-1 flex justify-between items-center">
                  <span>Direct Match from Database:</span>
                  <span className="text-amber-400">Press Enter for 1st item</span>
                </div>
                {searchMatches.map((item, idx) => (
                  <div
                    key={item.id}
                    className="p-2 flex items-center justify-between hover:bg-slate-800/70 rounded-lg transition-colors cursor-pointer"
                    onClick={() => {
                      addToCart(item, 1);
                      setSearchQuery('');
                    }}
                  >
                    <div className="flex items-center gap-2">
                      <span className="w-5 h-5 rounded-md bg-amber-500/20 text-amber-400 flex items-center justify-center text-[10px] font-bold">
                        {idx + 1}
                      </span>
                      <div>
                        <div className="text-xs font-bold text-white">{item.name}</div>
                        <div className="text-[10px] text-slate-400">
                          {item.unit} • Available Stock: {item.currentStock}
                        </div>
                      </div>
                    </div>
                    <div className="flex items-center gap-3">
                      <span className="text-xs font-mono font-bold text-emerald-400">
                        ₹{item.sellingPrice?.toFixed(2)}
                      </span>
                      <button
                        type="button"
                        className="px-2.5 py-1 bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold text-xs rounded-md shadow"
                      >
                        + Add
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}

            {/* Category Filter Tabs */}
            <div className="flex items-center gap-1.5 overflow-x-auto scrollbar-none pt-1">
              <button
                type="button"
                onClick={() => setSelectedCategory(null)}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-colors ${
                  selectedCategory === null
                    ? 'bg-amber-500 text-slate-950 font-bold shadow-md shadow-amber-950/40'
                    : 'bg-slate-800 text-slate-300 hover:bg-slate-700'
                }`}
              >
                All Database Items ({products.length})
              </button>
              {categories.map((c) => (
                <button
                  key={c.id}
                  type="button"
                  onClick={() => setSelectedCategory(c.id)}
                  className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-colors ${
                    selectedCategory === c.id
                      ? 'bg-amber-500 text-slate-950 font-bold shadow-md shadow-amber-950/40'
                      : 'bg-slate-800 text-slate-300 hover:bg-slate-700'
                  }`}
                >
                  {c.name}
                </button>
              ))}
            </div>
          </div>

          {/* Database Product Tiles Grid */}
          <div className="space-y-2">
            <div className="flex items-center justify-between text-xs font-bold text-slate-400 uppercase tracking-wider px-1">
              <span>Store Products ({filteredProducts.length})</span>
              <span className="text-[10px] text-slate-500">1-click touch/click to add</span>
            </div>

            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-2.5 max-h-[380px] overflow-y-auto pr-1">
              {filteredProducts.map((p) => {
                const isOutOfStock = p.currentStock <= 0;
                const inCartItem = cart.find((i) => i.product.id === p.id);
                return (
                  <div
                    key={p.id}
                    onClick={() => !isOutOfStock && addToCart(p, 1)}
                    className={`p-3 rounded-2xl border text-left flex flex-col justify-between transition-all duration-150 relative group cursor-pointer ${
                      isOutOfStock
                        ? 'bg-slate-900/40 border-slate-800/40 opacity-40 cursor-not-allowed'
                        : inCartItem
                        ? 'bg-slate-800/90 border-amber-500/60 shadow-md shadow-amber-950/30'
                        : 'bg-slate-900 hover:bg-slate-800 border-slate-800 hover:border-amber-500/50 hover:shadow-lg hover:shadow-amber-950/20 active:scale-95'
                    }`}
                  >
                    <div>
                      {inCartItem && (
                        <span className="absolute top-2 right-2 px-1.5 py-0.5 rounded-full bg-amber-500 text-slate-950 font-mono text-[9px] font-black">
                          {inCartItem.quantity} in cart
                        </span>
                      )}
                      <div className="font-semibold text-white text-xs truncate group-hover:text-amber-400 transition-colors pr-6">
                        {p.name}
                      </div>
                      <div className="text-[10px] text-slate-400 font-mono mt-0.5">
                        {p.unit} • Stock: {p.currentStock}
                      </div>
                    </div>

                    <div className="mt-3 flex items-center justify-between">
                      <span className="text-sm font-black text-amber-400 font-mono">
                        ₹{p.sellingPrice?.toFixed(2)}
                      </span>
                      <span className="px-2 py-0.5 rounded-lg bg-amber-500/20 text-amber-400 text-xs font-bold group-hover:bg-amber-500 group-hover:text-slate-950 transition-colors flex items-center gap-1">
                        <Plus className="w-3 h-3" /> Add
                      </span>
                    </div>
                  </div>
                );
              })}

              {filteredProducts.length === 0 && (
                <div className="col-span-full py-12 text-center text-slate-500 text-xs">
                  No products matched your search. Check catalog or clear filters.
                </div>
              )}
            </div>
          </div>
        </div>

        {/* RIGHT COLUMN: Streamlined Express Cart & Payment Station (5 Cols) */}
        <div className="lg:col-span-5 space-y-4">
          <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 shadow-2xl space-y-4">
            {/* Cart Header */}
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <div className="flex items-center gap-2">
                <ShoppingCart className="w-4 h-4 text-amber-400" />
                <span className="text-sm font-bold text-white">Walk-in Cart</span>
                <span className="px-2 py-0.5 rounded-full bg-slate-800 text-[10px] font-mono text-slate-300">
                  {cart.reduce((sum, item) => sum + item.quantity, 0)} items
                </span>
              </div>

              {cart.length > 0 && (
                <button
                  type="button"
                  onClick={clearCart}
                  className="text-[11px] text-slate-500 hover:text-rose-400 transition-colors"
                >
                  Clear Cart
                </button>
              )}
            </div>

            {/* Cart Items List */}
            <div className="space-y-2 max-h-[220px] overflow-y-auto pr-1 divide-y divide-slate-800/60">
              {cart.length === 0 ? (
                <div className="py-10 text-center text-slate-500 space-y-2">
                  <Package className="w-8 h-8 stroke-1 text-slate-600 mx-auto" />
                  <div className="text-xs">No items in bill. Search or click items on the left to add.</div>
                </div>
              ) : (
                cart.map((item) => {
                  const lineTotal = (item.customPrice || item.product.sellingPrice) * item.quantity;
                  return (
                    <div key={item.product.id} className="pt-2 flex items-center justify-between gap-2 text-xs">
                      <div className="flex-1 min-w-0">
                        <div className="font-semibold text-white truncate">{item.product.name}</div>
                        <div className="text-[10px] text-slate-400 font-mono">
                          ₹{(item.customPrice || item.product.sellingPrice)?.toFixed(2)} × {item.quantity}
                        </div>
                      </div>

                      {/* Quantity Stepper */}
                      <div className="flex items-center gap-1 bg-slate-800 p-1 rounded-lg border border-slate-700">
                        <button
                          type="button"
                          onClick={() => updateQuantity(item.product.id, item.quantity - 1)}
                          className="p-1 hover:text-rose-400 text-slate-400"
                        >
                          <Minus className="w-3 h-3" />
                        </button>
                        <span className="w-5 text-center font-mono font-bold text-white text-xs">
                          {item.quantity}
                        </span>
                        <button
                          type="button"
                          onClick={() => updateQuantity(item.product.id, item.quantity + 1)}
                          className="p-1 hover:text-emerald-400 text-slate-400"
                        >
                          <Plus className="w-3 h-3" />
                        </button>
                      </div>

                      <div className="w-16 text-right font-mono font-bold text-white text-xs">
                        ₹{lineTotal.toFixed(2)}
                      </div>

                      <button
                        type="button"
                        onClick={() => removeFromCart(item.product.id)}
                        className="text-slate-500 hover:text-rose-400 p-1"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  );
                })
              )}
            </div>

            {/* Total Payable Box */}
            <div className="p-3.5 rounded-2xl bg-gradient-to-br from-slate-950 to-slate-900 border border-slate-800 space-y-1">
              <div className="flex justify-between items-center">
                <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">
                  Total Payable:
                </span>
                <span className="text-2xl font-black text-amber-400 font-mono">
                  ₹{grandTotal.toFixed(2)}
                </span>
              </div>
            </div>

            {/* Instant Payment Selector */}
            <div className="space-y-3">
              <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">
                Select Payment Mode
              </div>

              <div className="grid grid-cols-3 gap-2">
                <button
                  type="button"
                  onClick={() => setPaymentMode('CASH')}
                  className={`p-2.5 rounded-xl border flex flex-col items-center gap-1 text-xs font-bold transition-all ${
                    paymentMode === 'CASH'
                      ? 'bg-emerald-500/20 border-emerald-500 text-emerald-400 shadow-md shadow-emerald-950/40'
                      : 'bg-slate-800/80 border-slate-700 text-slate-400 hover:text-white'
                  }`}
                >
                  <Banknote className="w-5 h-5" />
                  <span>Cash</span>
                </button>

                <button
                  type="button"
                  onClick={() => {
                    setPaymentMode('UPI_QR');
                    if (cart.length > 0) {
                      onOpenUpiModal({
                        amount: grandTotal,
                        customer: null,
                        paymentMethod: 'UPI_QR',
                      });
                    }
                  }}
                  className={`p-2.5 rounded-xl border flex flex-col items-center gap-1 text-xs font-bold transition-all ${
                    paymentMode === 'UPI_QR'
                      ? 'bg-amber-500/20 border-amber-500 text-amber-400 shadow-md shadow-amber-950/40'
                      : 'bg-slate-800/80 border-slate-700 text-slate-400 hover:text-white'
                  }`}
                >
                  <QrCode className="w-5 h-5 text-amber-400" />
                  <span>UPI QR Code</span>
                </button>

                <button
                  type="button"
                  onClick={() => setPaymentMode('CARD')}
                  className={`p-2.5 rounded-xl border flex flex-col items-center gap-1 text-xs font-bold transition-all ${
                    paymentMode === 'CARD'
                      ? 'bg-cyan-500/20 border-cyan-500 text-cyan-400 shadow-md shadow-cyan-950/40'
                      : 'bg-slate-800/80 border-slate-700 text-slate-400 hover:text-white'
                  }`}
                >
                  <CreditCard className="w-5 h-5" />
                  <span>Card</span>
                </button>
              </div>

              {/* Cash Presets and Change Calculator */}
              {paymentMode === 'CASH' && (
                <div className="p-3 rounded-xl bg-slate-800/70 border border-slate-700/80 space-y-2 text-xs">
                  <div className="text-[10px] text-slate-400 font-semibold uppercase">
                    Quick Cash Tender Presets:
                  </div>
                  <div className="grid grid-cols-3 gap-1.5">
                    {cashPresets.map((preset) => (
                      <button
                        key={preset.label}
                        type="button"
                        onClick={() => setCashTendered(preset.value)}
                        className={`py-1 px-2 rounded-lg border text-xs font-mono font-bold transition-colors ${
                          cashTendered === preset.value
                            ? 'bg-emerald-600 text-white border-emerald-500'
                            : 'bg-slate-900 border-slate-700 text-slate-300 hover:border-slate-500'
                        }`}
                      >
                        {preset.label}
                      </button>
                    ))}
                  </div>

                  <div className="flex items-center justify-between pt-2 border-t border-slate-700/60">
                    <span className="text-[11px] text-slate-400">Change to Return:</span>
                    <span className="text-base font-black font-mono text-emerald-400">
                      ₹{changeToReturn.toFixed(2)}
                    </span>
                  </div>
                </div>
              )}

              {/* UPI Instant Scan Trigger */}
              {paymentMode === 'UPI_QR' && (
                <div className="p-3 rounded-xl bg-amber-500/10 border border-amber-500/30 text-xs space-y-2">
                  <div className="flex items-center justify-between text-amber-400 font-bold">
                    <span className="flex items-center gap-1.5">
                      <QrCode className="w-4 h-4" /> Store UPI QR (Sumit Farande)
                    </span>
                    <span className="font-mono">₹{grandTotal.toFixed(2)}</span>
                  </div>
                  <p className="text-[11px] text-slate-300">
                    Customer can scan with Google Pay, PhonePe, Paytm, BHIM, or Cred.
                  </p>
                  <Button
                    size="sm"
                    variant="outline"
                    className="w-full border-amber-500/50 text-amber-400 hover:bg-amber-500/20"
                    onClick={() =>
                      onOpenUpiModal({
                        amount: grandTotal,
                        customer: null,
                        paymentMethod: 'UPI_QR',
                      })
                    }
                  >
                    Open Store UPI QR Popup
                  </Button>
                </div>
              )}
            </div>

            {/* Express Checkout Action Button */}
            <Button
              variant="primary"
              size="lg"
              className="w-full bg-gradient-to-r from-amber-500 to-emerald-500 text-slate-950 font-black shadow-lg shadow-amber-950/40 hover:from-amber-400 hover:to-emerald-400"
              disabled={cart.length === 0}
              isLoading={isProcessing}
              onClick={() => {
                if (paymentMode === 'UPI_QR') {
                  onOpenUpiModal({
                    amount: grandTotal,
                    customer: null,
                    paymentMethod: 'UPI_QR',
                  });
                } else {
                  onCheckout({
                    paymentMethod: paymentMode,
                    amountPaid: cashTendered,
                    customer: null,
                  });
                }
              }}
              leftIcon={<Zap className="w-4 h-4 fill-slate-950" />}
            >
              Complete Bill & Print (₹{grandTotal.toFixed(2)})
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
};
