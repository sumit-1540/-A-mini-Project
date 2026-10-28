import React, { useState, useRef, useEffect } from 'react';
import {
  Barcode,
  Search,
  ShoppingCart,
  Plus,
  Minus,
  Trash2,
  User as UserIcon,
  Sparkles,
  CreditCard,
  QrCode,
  Banknote,
  Tag,
  Star,
  Award,
  AlertTriangle,
  Gift,
  CheckCircle,
  Package,
} from 'lucide-react';
import { Button } from '../common/Button';
import { Badge } from '../common/Badge';

export const RegularCustomerPos = ({
  products,
  categories,
  cart,
  customer,
  setCustomer,
  onOpenCustomerModal,
  onBarcodeScan,
  onCheckout,
  isProcessing,
  onOpenUpiModal,
  subtotal,
  grandTotal,
  discountAmount,
  setDiscountAmount,
  taxPercent,
  setTaxPercent,
  addToCart,
  updateQuantity,
  removeFromCart,
  clearCart,
  recommendations,
  customerFavorites,
}) => {
  const [barcodeInput, setBarcodeInput] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState('CASH');
  const [amountPaid, setAmountPaid] = useState(grandTotal);
  const [notes, setNotes] = useState('');
  const [redeemPoints, setRedeemPoints] = useState(false);
  const barcodeRef = useRef(null);

  // Sync amount paid with grand total
  useEffect(() => {
    if (paymentMethod !== 'CREDIT_UDHAR') {
      setAmountPaid(grandTotal);
    } else {
      setAmountPaid(0);
    }
  }, [grandTotal, paymentMethod]);

  const handleBarcodeSubmit = (e) => {
    e.preventDefault();
    if (!barcodeInput.trim()) return;
    onBarcodeScan(barcodeInput.trim());
    setBarcodeInput('');
    barcodeRef.current?.focus();
  };

  const changeToReturn = Math.max(0, amountPaid - grandTotal);

  // Points redemption calculation: e.g. 50 points = ₹50 discount (or ₹1 per point up to total)
  const availablePoints = customer?.rewardPoints || 0;
  const maxPointDiscount = Math.min(availablePoints, Math.floor(subtotal));

  const handleToggleRedeemPoints = () => {
    if (!redeemPoints) {
      setRedeemPoints(true);
      setDiscountAmount((prev) => prev + maxPointDiscount);
    } else {
      setRedeemPoints(false);
      setDiscountAmount((prev) => Math.max(0, prev - maxPointDiscount));
    }
  };

  // Filter products
  const filteredProducts = products.filter((p) => {
    const matchesCat = selectedCategory ? p.categoryId === selectedCategory : true;
    const matchesSearch = searchQuery
      ? p.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        p.barcode?.includes(searchQuery) ||
        p.sku?.toLowerCase().includes(searchQuery.toLowerCase())
      : true;
    return matchesCat && matchesSearch;
  });

  return (
    <div className="space-y-4">
      {/* Regular Customer Mode Banner */}
      <div className="p-3 rounded-2xl bg-gradient-to-r from-indigo-500/10 via-slate-900 to-emerald-500/10 border border-indigo-500/20 flex flex-wrap items-center justify-between gap-2">
        <div className="flex items-center gap-2">
          <span className="p-1.5 rounded-xl bg-indigo-500/20 text-indigo-400">
            <Star className="w-4 h-4 fill-indigo-400" />
          </span>
          <div>
            <div className="text-xs font-bold text-white flex items-center gap-2">
              Daily Customer POS
              <span className="text-[10px] bg-indigo-500/20 text-indigo-300 px-2 py-0.5 rounded-full border border-indigo-500/30">
                Full Loyalty & Credit Ledger
              </span>
            </div>
            <div className="text-[11px] text-slate-400">
              Customer profile lookup • Loyalty point redemption • Customer credit ledger • AI customer favorites
            </div>
          </div>
        </div>

        {/* Customer Quick Selector Button */}
        <div>
          {customer ? (
            <div className="flex items-center gap-2">
              <span className="text-xs font-semibold text-emerald-400 flex items-center gap-1">
                <CheckCircle className="w-3.5 h-3.5" /> {customer.name}
              </span>
              <Button size="sm" variant="outline" onClick={onOpenCustomerModal}>
                Switch Customer
              </Button>
            </div>
          ) : (
            <Button
              size="sm"
              variant="primary"
              className="bg-indigo-600 hover:bg-indigo-500 text-xs"
              onClick={onOpenCustomerModal}
              leftIcon={<UserIcon className="w-3.5 h-3.5" />}
            >
              Attach Daily Customer
            </Button>
          )}
        </div>
      </div>

      {/* Attached Customer Intelligence Dossier */}
      {customer && (
        <div className="p-4 rounded-2xl bg-slate-900 border border-indigo-500/30 shadow-xl grid grid-cols-1 sm:grid-cols-4 gap-4 items-center">
          <div className="sm:col-span-1 border-r border-slate-800 pr-2">
            <div className="text-[10px] uppercase font-bold text-slate-400 tracking-wider">
              Customer Account
            </div>
            <div className="text-base font-extrabold text-white mt-0.5">{customer.name}</div>
            <div className="text-xs text-slate-400 font-mono">{customer.phone}</div>
            <Badge variant="secondary" size="sm" className="mt-1">
              {customer.totalOrders > 5 ? '⭐ VIP Regular' : 'Standard Member'}
            </Badge>
          </div>

          {/* Outstanding Credit */}
          <div className="sm:col-span-1">
            <div className="text-[10px] uppercase font-bold text-slate-400 tracking-wider flex items-center gap-1">
              Outstanding Credit
              {customer.creditBalance > 0 && <AlertTriangle className="w-3 h-3 text-rose-400" />}
            </div>
            <div className={`text-xl font-black font-mono mt-0.5 ${customer.creditBalance > 0 ? 'text-rose-400' : 'text-emerald-400'}`}>
              ₹{customer.creditBalance?.toFixed(2) || '0.00'}
            </div>
            <div className="text-[10px] text-slate-500">
              Limit: ₹{customer.creditLimit?.toFixed(2) || '5,000.00'}
            </div>
          </div>

          {/* Reward Points & Redemption */}
          <div className="sm:col-span-1">
            <div className="text-[10px] uppercase font-bold text-slate-400 tracking-wider flex items-center gap-1">
              <Gift className="w-3 h-3 text-amber-400" /> Loyalty Points
            </div>
            <div className="text-xl font-black text-amber-400 font-mono mt-0.5">
              {availablePoints} pts
            </div>
            {availablePoints > 0 && (
              <button
                type="button"
                onClick={handleToggleRedeemPoints}
                className={`text-[10px] font-bold mt-1 px-2 py-0.5 rounded border transition-colors ${
                  redeemPoints
                    ? 'bg-amber-500 text-slate-950 border-amber-400'
                    : 'bg-amber-500/10 text-amber-400 border-amber-500/30 hover:bg-amber-500/20'
                }`}
              >
                {redeemPoints ? '✓ Redeeming ₹' + maxPointDiscount : `Redeem ₹${maxPointDiscount} Off`}
              </button>
            )}
          </div>

          {/* AI Customer Frequent Habits */}
          <div className="sm:col-span-1">
            <div className="text-[10px] uppercase font-bold text-emerald-400 tracking-wider flex items-center gap-1">
              <Sparkles className="w-3 h-3" /> Frequent Items
            </div>
            <div className="flex flex-wrap gap-1 mt-1">
              {customerFavorites.length > 0 ? (
                customerFavorites.slice(0, 3).map((item, idx) => (
                  <button
                    key={idx}
                    type="button"
                    onClick={() => {
                      const match = products.find((p) => p.name.toLowerCase() === item.toLowerCase());
                      if (match) addToCart(match, 1);
                    }}
                    className="text-[10px] px-1.5 py-0.5 rounded bg-slate-800 text-slate-300 hover:text-emerald-400 border border-slate-700 hover:border-emerald-500/40"
                  >
                    + {item}
                  </button>
                ))
              ) : (
                <span className="text-[10px] text-slate-500">First-time / No history</span>
              )}
            </div>
          </div>
        </div>
      )}

      {/* Main 2-Column POS Layout */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-5 items-start">
        {/* LEFT COLUMN: Product Catalog (7 Cols) */}
        <div className="lg:col-span-7 space-y-4">
          {/* Scanner & Search Bar */}
          <div className="grid grid-cols-1 sm:grid-cols-12 gap-2.5 p-4 rounded-2xl bg-slate-900 border border-slate-800 shadow-xl">
            {/* Barcode Fast Input */}
            <form onSubmit={handleBarcodeSubmit} className="sm:col-span-5 relative">
              <Barcode className="w-4 h-4 text-emerald-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
              <input
                ref={barcodeRef}
                type="text"
                placeholder="Scan Barcode / SKU..."
                value={barcodeInput}
                onChange={(e) => setBarcodeInput(e.target.value)}
                className="w-full pl-10 pr-3 py-2 bg-slate-800 border border-emerald-500/40 rounded-xl text-xs text-white font-mono placeholder:text-slate-400 focus:outline-none focus:border-emerald-400 focus:ring-1 focus:ring-emerald-400"
              />
            </form>

            {/* Keyword Search */}
            <div className="sm:col-span-7 relative">
              <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
              <input
                type="text"
                placeholder="Search catalog by title, brand..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full pl-10 pr-3 py-2 bg-slate-800 border border-slate-700 rounded-xl text-xs text-white placeholder:text-slate-400 focus:outline-none focus:border-emerald-500"
              />
            </div>
          </div>

          {/* Category Filter Pills */}
          <div className="flex items-center gap-2 overflow-x-auto pb-1 scrollbar-none">
            <button
              onClick={() => setSelectedCategory(null)}
              className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-all ${
                selectedCategory === null
                  ? 'bg-emerald-600 text-white shadow-md shadow-emerald-950/40'
                  : 'bg-slate-800 text-slate-300 hover:bg-slate-700 hover:text-white'
              }`}
            >
              All Items
            </button>
            {categories.map((cat) => (
              <button
                key={cat.id}
                onClick={() => setSelectedCategory(cat.id)}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-all ${
                  selectedCategory === cat.id
                    ? 'bg-emerald-600 text-white shadow-md shadow-emerald-950/40'
                    : 'bg-slate-800 text-slate-300 hover:bg-slate-700 hover:text-white'
                }`}
              >
                {cat.name}
              </button>
            ))}
          </div>

          {/* Catalog Grid */}
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3 max-h-[380px] overflow-y-auto pr-1">
            {filteredProducts.map((p) => {
              const isOutOfStock = p.currentStock <= 0;
              return (
                <div
                  key={p.id}
                  onClick={() => !isOutOfStock && addToCart(p, 1)}
                  className={`p-3 rounded-2xl border text-left flex flex-col justify-between transition-all duration-150 cursor-pointer ${
                    isOutOfStock
                      ? 'bg-slate-900/40 border-slate-800/40 opacity-40 cursor-not-allowed'
                      : 'bg-slate-900 hover:bg-slate-800 border-slate-800 hover:border-emerald-500/50 hover:shadow-lg hover:shadow-emerald-950/20 active:scale-95'
                  }`}
                >
                  <div>
                    <div className="font-semibold text-white text-xs truncate">{p.name}</div>
                    <div className="text-[10px] text-slate-400 font-mono mt-0.5">
                      {p.unit} • Stock: {p.currentStock}
                    </div>
                  </div>

                  <div className="mt-2.5 flex items-center justify-between">
                    <span className="text-sm font-black text-emerald-400 font-mono">
                      ₹{p.sellingPrice?.toFixed(2)}
                    </span>
                    <span className="w-5 h-5 rounded-md bg-emerald-500/20 text-emerald-400 group-hover:bg-emerald-500 group-hover:text-slate-950 flex items-center justify-center text-xs font-bold transition-colors">
                      +
                    </span>
                  </div>
                </div>
              );
            })}
          </div>

          {/* AI Bundle Recommendations */}
          {recommendations.length > 0 && (
            <div className="p-3.5 rounded-2xl bg-indigo-950/30 border border-indigo-500/30 space-y-2">
              <div className="flex items-center gap-1.5 text-xs font-bold text-indigo-300">
                <Sparkles className="w-4 h-4 text-indigo-400" />
                Frequently Bought Together Recommendations
              </div>
              <div className="flex gap-2 overflow-x-auto pb-1">
                {recommendations.map((rec) => (
                  <button
                    key={rec.id}
                    onClick={() => {
                      const match = products.find((p) => p.id === rec.id);
                      if (match) addToCart(match, 1);
                    }}
                    className="flex-shrink-0 px-3 py-1.5 rounded-xl bg-slate-900 border border-indigo-500/40 hover:border-indigo-400 text-left transition-colors"
                  >
                    <div className="text-xs font-semibold text-white truncate max-w-[140px]">
                      {rec.name}
                    </div>
                    <div className="text-[11px] text-emerald-400 font-mono font-bold">
                      + ₹{rec.price?.toFixed(2)}
                    </div>
                  </button>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* RIGHT COLUMN: Full Regular Cart & Detailed Checkout (5 Cols) */}
        <div className="lg:col-span-5 space-y-4">
          <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 shadow-2xl space-y-3">
            {/* Cart Header */}
            <div className="flex items-center justify-between border-b border-slate-800 pb-2">
              <div className="flex items-center gap-2">
                <ShoppingCart className="w-4 h-4 text-emerald-400" />
                <span className="text-sm font-bold text-white">Active Order</span>
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
            <div className="space-y-2 max-h-[190px] overflow-y-auto pr-1 divide-y divide-slate-800/60">
              {cart.length === 0 ? (
                <div className="py-8 text-center text-slate-500 text-xs">
                  Cart is empty. Select or scan items.
                </div>
              ) : (
                cart.map((item) => {
                  const itemTotal = (item.customPrice || item.product.sellingPrice) * item.quantity;
                  return (
                    <div key={item.product.id} className="pt-2 flex items-center justify-between gap-2 text-xs">
                      <div className="flex-1 min-w-0">
                        <div className="font-semibold text-white truncate">{item.product.name}</div>
                        <div className="text-[10px] text-slate-400 font-mono">
                          ₹{(item.customPrice || item.product.sellingPrice)?.toFixed(2)} / {item.product.unit}
                        </div>
                      </div>

                      <div className="flex items-center gap-1 bg-slate-800 p-1 rounded-lg border border-slate-700">
                        <button
                          onClick={() => updateQuantity(item.product.id, item.quantity - 1)}
                          className="p-1 hover:text-rose-400 text-slate-400"
                        >
                          <Minus className="w-3 h-3" />
                        </button>
                        <span className="w-5 text-center font-mono font-bold text-white text-xs">
                          {item.quantity}
                        </span>
                        <button
                          onClick={() => updateQuantity(item.product.id, item.quantity + 1)}
                          className="p-1 hover:text-emerald-400 text-slate-400"
                        >
                          <Plus className="w-3 h-3" />
                        </button>
                      </div>

                      <div className="w-16 text-right font-mono font-bold text-white text-xs">
                        ₹{itemTotal.toFixed(2)}
                      </div>

                      <button
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

            {/* Calculations & Discounts */}
            <div className="pt-2 border-t border-slate-800 space-y-2 text-xs">
              <div className="flex justify-between text-slate-400">
                <span>Subtotal ({cart.reduce((a, b) => a + b.quantity, 0)} items):</span>
                <span className="font-mono text-slate-200">₹{subtotal.toFixed(2)}</span>
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="text-[10px] font-semibold text-slate-400">Discount (₹)</label>
                  <input
                    type="number"
                    min="0"
                    max={subtotal}
                    value={discountAmount}
                    onChange={(e) => setDiscountAmount(Number(e.target.value))}
                    className="w-full px-2 py-1 bg-slate-800 border border-slate-700 rounded-lg text-xs font-mono text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>
                <div>
                  <label className="text-[10px] font-semibold text-slate-400">GST / Tax</label>
                  <select
                    value={taxPercent}
                    onChange={(e) => setTaxPercent(Number(e.target.value))}
                    className="w-full px-2 py-1 bg-slate-800 border border-slate-700 rounded-lg text-xs text-white focus:outline-none focus:border-emerald-500"
                  >
                    <option value={0}>0% (Tax Exempt)</option>
                    <option value={5}>5% GST</option>
                    <option value={12}>12% GST</option>
                    <option value={18}>18% GST</option>
                  </select>
                </div>
              </div>

              <div className="flex justify-between items-center pt-2 border-t border-slate-800 text-base font-extrabold text-white">
                <span>Total Payable:</span>
                <span className="text-xl font-black text-emerald-400 font-mono">
                  ₹{grandTotal.toFixed(2)}
                </span>
              </div>
            </div>

            {/* Payment Method Selector */}
            <div className="space-y-2 pt-1">
              <div className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">
                Payment Mode
              </div>
              <div className="grid grid-cols-4 gap-1.5">
                <button
                  type="button"
                  onClick={() => setPaymentMethod('CASH')}
                  className={`p-2 rounded-xl border flex flex-col items-center gap-1 text-[11px] font-semibold transition-all ${
                    paymentMethod === 'CASH'
                      ? 'bg-emerald-500/20 border-emerald-500 text-emerald-400'
                      : 'bg-slate-800/80 border-slate-700 text-slate-400 hover:text-white'
                  }`}
                >
                  <Banknote className="w-4 h-4" />
                  <span>Cash</span>
                </button>

                <button
                  type="button"
                  onClick={() => {
                    setPaymentMethod('UPI_QR');
                    if (cart.length > 0) {
                      onOpenUpiModal({
                        amount: grandTotal,
                        customer,
                        paymentMethod: 'UPI_QR',
                      });
                    }
                  }}
                  className={`p-2 rounded-xl border flex flex-col items-center gap-1 text-[11px] font-semibold transition-all ${
                    paymentMethod === 'UPI_QR'
                      ? 'bg-emerald-500/20 border-emerald-500 text-emerald-400'
                      : 'bg-slate-800/80 border-slate-700 text-slate-400 hover:text-white'
                  }`}
                >
                  <QrCode className="w-4 h-4" />
                  <span>UPI QR</span>
                </button>

                <button
                  type="button"
                  onClick={() => setPaymentMethod('CARD')}
                  className={`p-2 rounded-xl border flex flex-col items-center gap-1 text-[11px] font-semibold transition-all ${
                    paymentMethod === 'CARD'
                      ? 'bg-emerald-500/20 border-emerald-500 text-emerald-400'
                      : 'bg-slate-800/80 border-slate-700 text-slate-400 hover:text-white'
                  }`}
                >
                  <CreditCard className="w-4 h-4" />
                  <span>Card</span>
                </button>

                <button
                  type="button"
                  onClick={() => {
                    if (!customer) {
                      onOpenCustomerModal();
                    }
                    setPaymentMethod('CREDIT_UDHAR');
                  }}
                  className={`p-2 rounded-xl border flex flex-col items-center gap-1 text-[11px] font-semibold transition-all ${
                    paymentMethod === 'CREDIT_UDHAR'
                      ? 'bg-rose-500/20 border-rose-500 text-rose-400'
                      : 'bg-slate-800/80 border-slate-700 text-slate-400 hover:text-white'
                  }`}
                >
                  <Tag className="w-4 h-4" />
                  <span>Credit</span>
                </button>
              </div>
            </div>

            {/* Cash details */}
            {paymentMethod === 'CASH' && (
              <div className="p-2.5 rounded-xl bg-slate-800 border border-slate-700 flex items-center justify-between text-xs">
                <div>
                  <label className="text-[10px] text-slate-400 block mb-0.5">Cash Tendered (₹)</label>
                  <input
                    type="number"
                    value={amountPaid}
                    onChange={(e) => setAmountPaid(Number(e.target.value))}
                    className="w-28 px-2 py-1 bg-slate-900 border border-slate-700 rounded-lg text-xs font-mono font-bold text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>
                <div className="text-right">
                  <span className="text-[10px] text-slate-400 block">Change to Return:</span>
                  <span className="text-sm font-mono font-bold text-emerald-400">
                    ₹{changeToReturn.toFixed(2)}
                  </span>
                </div>
              </div>
            )}

            {/* Credit Confirmation Box */}
            {paymentMethod === 'CREDIT_UDHAR' && (
              <div className="p-3 rounded-xl bg-rose-500/10 border border-rose-500/30 text-xs text-rose-400 space-y-1">
                <div className="font-bold flex items-center gap-1">
                  <Tag className="w-3.5 h-3.5" /> Credit Ledger Record
                </div>
                <div>
                  ₹{grandTotal.toFixed(2)} will be debited to {customer?.name || 'Customer'}'s credit account.
                </div>
              </div>
            )}

            {/* Complete Checkout Button */}
            <Button
              variant="primary"
              size="lg"
              className="w-full"
              disabled={cart.length === 0}
              isLoading={isProcessing}
              onClick={() => {
                if (paymentMethod === 'UPI_QR') {
                  onOpenUpiModal({
                    amount: grandTotal,
                    customer,
                    paymentMethod: 'UPI_QR',
                  });
                } else {
                  onCheckout({
                    paymentMethod,
                    amountPaid,
                    customer,
                    notes,
                    redeemRewardPoints: redeemPoints ? maxPointDiscount : 0,
                  });
                }
              }}
            >
              Complete Checkout (₹{grandTotal.toFixed(2)})
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
};
