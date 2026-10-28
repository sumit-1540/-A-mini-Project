import React, { useState, useEffect, useRef } from 'react';
import { useSearchParams } from 'react-router-dom';
import {
  Zap,
  Star,
  Barcode,
} from 'lucide-react';
import confetti from 'canvas-confetti';
import {
  productService,
  categoryService,
  customerService,
  orderService,
  aiService,
} from '../services/api';
import { QuickCustomerLookupModal } from '../components/pos/QuickCustomerLookupModal';
import { ReceiptModal } from '../components/pos/ReceiptModal';
import { UpiPaymentModal } from '../components/pos/UpiPaymentModal';
import { ExpressWalkthroughPos } from '../components/pos/ExpressWalkthroughPos';
import { RegularCustomerPos } from '../components/pos/RegularCustomerPos';
import { useToast } from '../context/ToastContext';

export const PosPage = () => {
  const { success, error, warning } = useToast();
  const [searchParams, setSearchParams] = useSearchParams();

  // Mode Selection: 'express' (Walkthrough) vs 'regular' (Daily Customer)
  const initialMode = searchParams.get('mode') === 'regular' ? 'regular' : 'express';
  const [posMode, setPosMode] = useState(initialMode);

  // Sync mode with URL if param changes
  useEffect(() => {
    const urlMode = searchParams.get('mode');
    if (urlMode && (urlMode === 'express' || urlMode === 'regular')) {
      setPosMode(urlMode);
    }
  }, [searchParams]);

  // Catalog State
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [isLoadingProducts, setIsLoadingProducts] = useState(false);

  // Shared Cart State
  const [cart, setCart] = useState([]);
  const [customer, setCustomer] = useState(null);
  const [discountAmount, setDiscountAmount] = useState(0);
  const [taxPercent, setTaxPercent] = useState(0);
  const [notes, setNotes] = useState('');
  const [isProcessing, setIsProcessing] = useState(false);

  // Recommendations & Customer Insights
  const [recommendations, setRecommendations] = useState([]);
  const [customerFavorites, setCustomerFavorites] = useState([]);

  // Modals
  const [isCustomerModalOpen, setIsCustomerModalOpen] = useState(false);
  const [isReceiptModalOpen, setIsReceiptModalOpen] = useState(false);
  const [currentReceipt, setCurrentReceipt] = useState(null);

  // UPI QR Payment Modal
  const [isUpiModalOpen, setIsUpiModalOpen] = useState(false);
  const [upiModalData, setUpiModalData] = useState(null);

  useEffect(() => {
    loadCategories();
    loadProducts();
  }, []);

  // Fetch AI Recommendations whenever Cart or Customer changes
  useEffect(() => {
    const fetchRecommendations = async () => {
      try {
        const cartIds = cart.map((i) => i.product.id);
        const recs = await aiService.getRecommendations(cartIds, customer?.id);
        setRecommendations(recs || []);
      } catch (e) {
        console.error('Failed to get recommendations:', e);
      }
    };
    fetchRecommendations();
  }, [cart, customer]);

  // Load customer insight when customer is selected
  useEffect(() => {
    if (customer) {
      customerService.getCustomerInsights(customer.id).then((insight) => {
        setCustomerFavorites(insight?.frequentlyPurchasedProducts || []);
      });
    } else {
      setCustomerFavorites([]);
    }
  }, [customer]);

  const loadCategories = async () => {
    try {
      const data = await categoryService.getActiveCategories();
      setCategories(data || []);
    } catch (e) {
      console.error(e);
    }
  };

  const loadProducts = async (catId = selectedCategoryId, query = searchQuery) => {
    try {
      setIsLoadingProducts(true);
      let data = [];
      if (catId) {
        data = await productService.getProductsByCategory(catId);
      } else {
        data = await productService.getAllProducts();
      }

      if (query && query.trim() !== '') {
        const q = query.toLowerCase();
        data = data.filter(
          (p) =>
            p.name.toLowerCase().includes(q) ||
            p.sku?.toLowerCase().includes(q) ||
            p.barcode?.includes(q)
        );
      }

      setProducts(data || []);

      // Check for transferred items from Handwritten Shopping List
      const pendingCartJson = localStorage.getItem('smartshelf_pending_cart');
      if (pendingCartJson && data && data.length > 0) {
        try {
          const pendingItems = JSON.parse(pendingCartJson);
          if (Array.isArray(pendingItems) && pendingItems.length > 0) {
            const itemsToAdd = [];
            for (const pi of pendingItems) {
              const matchedProduct = data.find((p) => p.id === pi.productId);
              if (matchedProduct) {
                itemsToAdd.push({
                  product: matchedProduct,
                  quantity: pi.quantity || 1,
                  customPrice: null,
                });
              }
            }
            if (itemsToAdd.length > 0) {
              setCart((prev) => {
                const updated = [...prev];
                for (const item of itemsToAdd) {
                  const existingIdx = updated.findIndex((i) => i.product.id === item.product.id);
                  if (existingIdx >= 0) {
                    updated[existingIdx].quantity += item.quantity;
                  } else {
                    updated.push(item);
                  }
                }
                return updated;
              });
              localStorage.removeItem('smartshelf_pending_cart');
              success(`Transferred ${itemsToAdd.length} items from Handwritten Slip to bill!`);
            }
          }
        } catch (err) {
          console.error('Failed to parse pending cart:', err);
        }
      }
    } catch (e) {
      console.error(e);
    } finally {
      setIsLoadingProducts(false);
    }
  };

  const handleBarcodeScan = async (code) => {
    if (!code) return;
    try {
      const product = await productService.getProductByBarcode(code.trim());
      if (product) {
        addToCart(product);
        success(`Scanned: ${product.name}`);
        return;
      }
    } catch {
      try {
        const product = await productService.getProductBySku(code.trim());
        if (product) {
          addToCart(product);
          success(`Added: ${product.name}`);
          return;
        }
      } catch {
        error(`No product found with barcode / SKU: ${code}`);
      }
    }
  };

  const addToCart = (product, qty = 1) => {
    if (product.currentStock <= 0) {
      warning(`'${product.name}' is currently OUT OF STOCK!`);
      return;
    }

    setCart((prev) => {
      const existing = prev.find((item) => item.product.id === product.id);
      if (existing) {
        if (existing.quantity + qty > product.currentStock) {
          warning(`Cannot exceed available shelf stock (${product.currentStock} ${product.unit})`);
          return prev;
        }
        return prev.map((item) =>
          item.product.id === product.id
            ? { ...item, quantity: item.quantity + qty }
            : item
        );
      } else {
        return [...prev, { product, quantity: qty }];
      }
    });
  };

  const updateQuantity = (productId, newQty) => {
    if (newQty <= 0) {
      removeFromCart(productId);
      return;
    }
    setCart((prev) =>
      prev.map((item) => {
        if (item.product.id === productId) {
          if (newQty > item.product.currentStock) {
            warning(`Only ${item.product.currentStock} ${item.product.unit} available in stock`);
            return item;
          }
          return { ...item, quantity: newQty };
        }
        return item;
      })
    );
  };

  const removeFromCart = (productId) => {
    setCart((prev) => prev.filter((item) => item.product.id !== productId));
  };

  const clearCart = () => {
    setCart([]);
    setDiscountAmount(0);
    setNotes('');
  };

  // Calculations
  const subtotal = cart.reduce(
    (sum, item) => sum + (item.customPrice || item.product.sellingPrice) * item.quantity,
    0
  );
  const taxAmount = (subtotal - discountAmount) * (taxPercent / 100);
  const grandTotal = Math.max(0, subtotal - discountAmount + taxAmount);

  // Handle Checkout Execution
  const handleCheckout = async (checkoutData = {}) => {
    if (cart.length === 0) {
      error('Cart is empty. Scan or select items first.');
      return;
    }

    const targetPaymentMethod = checkoutData.paymentMethod || 'CASH';
    const targetCustomer = checkoutData.customer !== undefined ? checkoutData.customer : customer;

    if (targetPaymentMethod === 'CREDIT_UDHAR' && !targetCustomer) {
      error('Please attach a customer to record Credit payment!');
      setIsCustomerModalOpen(true);
      return;
    }

    try {
      setIsProcessing(true);
      const payload = {
        customerId: targetCustomer?.id,
        items: cart.map((item) => ({
          productId: item.product.id,
          quantity: item.quantity,
          customPrice: item.customPrice,
          discount: item.discount,
        })),
        discountAmount,
        taxAmount,
        paymentMethod: targetPaymentMethod,
        amountPaid:
          checkoutData.amountPaid !== undefined
            ? checkoutData.amountPaid
            : targetPaymentMethod === 'CREDIT_UDHAR'
            ? 0
            : grandTotal,
        transactionReference: checkoutData.transactionReference,
        notes: (checkoutData.notes || notes || '').trim() || undefined,
        redeemRewardPoints: checkoutData.redeemRewardPoints || 0,
      };

      const receipt = await orderService.createOrder(payload);
      setCurrentReceipt(receipt);
      setIsReceiptModalOpen(true);

      // Trigger Confetti Celebration for Cashier
      confetti({
        particleCount: 80,
        spread: 60,
        origin: { y: 0.8 },
      });

      loadProducts();
      clearCart();
    } catch (err) {
      error(err.response?.data?.message || 'Checkout failed. Please check stock levels.');
    } finally {
      setIsProcessing(false);
    }
  };

  // UPI Payment Flow
  const handleOpenUpiModal = (data) => {
    setUpiModalData(data);
    setIsUpiModalOpen(true);
  };

  const handleUpiPaymentConfirmed = (paymentInfo) => {
    setIsUpiModalOpen(false);
    handleCheckout({
      paymentMethod: 'UPI_QR',
      amountPaid: paymentInfo.amount,
      transactionReference: paymentInfo.transactionReference,
      customer: upiModalData?.customer,
    });
  };

  return (
    <div className="space-y-4">
      {/* Top Segmented Mode Switcher */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 p-2 bg-slate-900/90 rounded-2xl border border-slate-800 shadow-xl">
        <div className="flex items-center gap-1.5 p-1 bg-slate-950/80 rounded-xl border border-slate-800/80 w-full sm:w-auto">
          {/* Express Walkthrough Mode Tab */}
          <button
            type="button"
            onClick={() => {
              setPosMode('express');
              setSearchParams({ mode: 'express' });
            }}
            className={`flex-1 sm:flex-none flex items-center justify-center gap-2 px-4 py-2.5 rounded-lg text-xs font-bold transition-all ${
              posMode === 'express'
                ? 'bg-gradient-to-r from-amber-500 to-amber-600 text-slate-950 shadow-md shadow-amber-950/40'
                : 'text-slate-400 hover:text-white hover:bg-slate-900'
            }`}
          >
            <Zap className={`w-4 h-4 ${posMode === 'express' ? 'fill-slate-950' : 'text-amber-400'}`} />
            <span>⚡ Walkthrough / Express POS</span>
          </button>

          {/* Daily Customer Mode Tab */}
          <button
            type="button"
            onClick={() => {
              setPosMode('regular');
              setSearchParams({ mode: 'regular' });
            }}
            className={`flex-1 sm:flex-none flex items-center justify-center gap-2 px-4 py-2.5 rounded-lg text-xs font-bold transition-all ${
              posMode === 'regular'
                ? 'bg-gradient-to-r from-indigo-500 to-indigo-600 text-white shadow-md shadow-indigo-950/40'
                : 'text-slate-400 hover:text-white hover:bg-slate-900'
            }`}
          >
            <Star className={`w-4 h-4 ${posMode === 'regular' ? 'fill-white' : 'text-indigo-400'}`} />
            <span>⭐ Daily Customer POS</span>
          </button>
        </div>

        {/* Live Cart Snapshot on Right */}
        <div className="hidden sm:flex items-center gap-3 text-xs text-slate-400 pr-3 font-medium">
          <span>Active Cart:</span>
          <span className="font-mono font-bold text-emerald-400 bg-emerald-500/10 px-2 py-0.5 rounded-lg border border-emerald-500/20">
            {cart.reduce((s, i) => s + i.quantity, 0)} items • ₹{grandTotal.toFixed(2)}
          </span>
        </div>
      </div>

      {/* POS View Rendering based on active Mode */}
      {posMode === 'express' ? (
        <ExpressWalkthroughPos
          products={products}
          categories={categories}
          cart={cart}
          setCart={setCart}
          onBarcodeScan={handleBarcodeScan}
          onCheckout={handleCheckout}
          isProcessing={isProcessing}
          onOpenUpiModal={handleOpenUpiModal}
          subtotal={subtotal}
          grandTotal={grandTotal}
          addToCart={addToCart}
          updateQuantity={updateQuantity}
          removeFromCart={removeFromCart}
          clearCart={clearCart}
        />
      ) : (
        <RegularCustomerPos
          products={products}
          categories={categories}
          cart={cart}
          customer={customer}
          setCustomer={setCustomer}
          onOpenCustomerModal={() => setIsCustomerModalOpen(true)}
          onBarcodeScan={handleBarcodeScan}
          onCheckout={handleCheckout}
          isProcessing={isProcessing}
          onOpenUpiModal={handleOpenUpiModal}
          subtotal={subtotal}
          grandTotal={grandTotal}
          discountAmount={discountAmount}
          setDiscountAmount={setDiscountAmount}
          taxPercent={taxPercent}
          setTaxPercent={setTaxPercent}
          addToCart={addToCart}
          updateQuantity={updateQuantity}
          removeFromCart={removeFromCart}
          clearCart={clearCart}
          recommendations={recommendations}
          customerFavorites={customerFavorites}
        />
      )}

      {/* Quick Customer Lookup Modal */}
      <QuickCustomerLookupModal
        isOpen={isCustomerModalOpen}
        onClose={() => setIsCustomerModalOpen(false)}
        onSelectCustomer={(c) => {
          setCustomer(c);
          success(`Attached customer: ${c.name}`);
        }}
      />

      {/* Dynamic UPI QR Payment Modal */}
      <UpiPaymentModal
        isOpen={isUpiModalOpen}
        onClose={() => setIsUpiModalOpen(false)}
        amount={upiModalData?.amount || grandTotal}
        orderNumber={upiModalData?.orderNumber}
        customer={upiModalData?.customer || customer}
        onPaymentConfirmed={handleUpiPaymentConfirmed}
      />

      {/* Printable Receipt Modal */}
      <ReceiptModal
        isOpen={isReceiptModalOpen}
        onClose={() => setIsReceiptModalOpen(false)}
        receipt={currentReceipt}
      />
    </div>
  );
};
