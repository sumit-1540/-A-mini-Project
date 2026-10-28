import React, { useState, useRef, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import {
  Menu,
  Bell,
  Search,
  ShoppingCart,
  CheckCircle2,
  AlertTriangle,
  ChevronRight,
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { useNotifications } from '../../context/NotificationContext';
import { productService, customerService } from '../../services/api';

export const Navbar = ({ onOpenSidebar }) => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { notifications, unreadCount, markAsRead, markAllAsRead } = useNotifications();

  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState({
    products: [],
    customers: [],
  });
  const [isSearching, setIsSearching] = useState(false);
  const [isSearchOpen, setIsSearchOpen] = useState(false);

  const [isNotifOpen, setIsNotifOpen] = useState(false);
  const notifRef = useRef(null);
  const searchRef = useRef(null);

  // Close dropdowns on outside click
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (notifRef.current && !notifRef.current.contains(e.target)) {
        setIsNotifOpen(false);
      }
      if (searchRef.current && !searchRef.current.contains(e.target)) {
        setIsSearchOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Quick live global search
  useEffect(() => {
    if (!searchQuery.trim()) {
      setSearchResults({ products: [], customers: [] });
      setIsSearchOpen(false);
      return;
    }

    const timer = setTimeout(async () => {
      try {
        setIsSearching(true);
        const [prods, custs] = await Promise.all([
          productService.searchActiveProducts(searchQuery),
          customerService.searchCustomers(searchQuery),
        ]);
        setSearchResults({
          products: (prods || []).slice(0, 5),
          customers: (custs || []).slice(0, 3),
        });
        setIsSearchOpen(true);
      } catch (e) {
        console.error('Search error:', e);
      } finally {
        setIsSearching(false);
      }
    }, 250);

    return () => clearTimeout(timer);
  }, [searchQuery]);

  const handleNotificationClick = async (notif) => {
    if (!notif.isRead) {
      await markAsRead(notif.id);
    }
    setIsNotifOpen(false);

    if (notif.entityType === 'PRODUCT') {
      navigate(`/inventory?search=${notif.entityId}`);
    } else if (notif.entityType === 'CUSTOMER') {
      navigate(`/credit?customer=${notif.entityId}`);
    } else {
      navigate('/notifications');
    }
  };

  return (
    <header className="sticky top-0 z-30 h-16 bg-slate-900/90 backdrop-blur-md border-b border-slate-800/80 px-4 lg:px-8 flex items-center justify-between gap-4">
      {/* Mobile Menu Button & Brand */}
      <div className="flex items-center gap-3">
        <button
          onClick={onOpenSidebar}
          className="p-2 text-slate-400 hover:text-white rounded-lg hover:bg-slate-800 lg:hidden"
        >
          <Menu className="w-5 h-5" />
        </button>

        {/* Global Instant Search Bar */}
        <div ref={searchRef} className="relative hidden sm:block w-72 md:w-96">
          <div className="relative">
            <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              placeholder="Search products, barcodes, customers..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              onFocus={() => {
                if (searchQuery.trim()) setIsSearchOpen(true);
              }}
              className="w-full pl-10 pr-4 py-2 text-xs bg-slate-800/80 border border-slate-700/80 rounded-xl text-white placeholder:text-slate-400 focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 transition-all"
            />
          </div>

          {/* Search Results Dropdown */}
          {isSearchOpen && (searchResults.products.length > 0 || searchResults.customers.length > 0) && (
            <div className="absolute top-full left-0 right-0 mt-2 bg-slate-900 border border-slate-800 rounded-xl shadow-2xl overflow-hidden z-50 divide-y divide-slate-800/80">
              {searchResults.products.length > 0 && (
                <div className="p-2">
                  <div className="text-[10px] font-bold uppercase tracking-wider text-slate-400 px-2 py-1">
                    Products
                  </div>
                  {searchResults.products.map((p) => (
                    <button
                      key={p.id}
                      onClick={() => {
                        setIsSearchOpen(false);
                        setSearchQuery('');
                        navigate(`/products?search=${encodeURIComponent(p.name)}`);
                      }}
                      className="w-full flex items-center justify-between p-2 rounded-lg hover:bg-slate-800 text-left transition-colors"
                    >
                      <div>
                        <div className="text-xs font-semibold text-white">{p.name}</div>
                        <div className="text-[11px] text-slate-400">
                          SKU: {p.sku} | Stock: {p.currentStock} {p.unit}
                        </div>
                      </div>
                      <div className="text-xs font-bold text-emerald-400">₹{p.sellingPrice?.toFixed(2)}</div>
                    </button>
                  ))}
                </div>
              )}

              {searchResults.customers.length > 0 && (
                <div className="p-2">
                  <div className="text-[10px] font-bold uppercase tracking-wider text-slate-400 px-2 py-1">
                    Customers
                  </div>
                  {searchResults.customers.map((c) => (
                    <button
                      key={c.id}
                      onClick={() => {
                        setIsSearchOpen(false);
                        setSearchQuery('');
                        navigate(`/customers?search=${encodeURIComponent(c.phone)}`);
                      }}
                      className="w-full flex items-center justify-between p-2 rounded-lg hover:bg-slate-800 text-left transition-colors"
                    >
                      <div>
                        <div className="text-xs font-semibold text-white">{c.name}</div>
                        <div className="text-[11px] text-slate-400">{c.phone}</div>
                      </div>
                      {c.creditBalance > 0 && (
                        <div className="text-xs font-semibold text-rose-400">Credit: ₹{c.creditBalance}</div>
                      )}
                    </button>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      {/* Right Controls */}
      <div className="flex items-center gap-3">
        {/* POS Quick Button */}
        <Link
          to="/pos"
          className="flex items-center gap-2 px-3.5 py-1.5 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-bold shadow-md shadow-emerald-950/40 transition-all active:scale-95"
        >
          <ShoppingCart className="w-3.5 h-3.5" />
          <span className="hidden sm:inline">POS Billing</span>
        </Link>

        {/* Notifications Bell Dropdown */}
        <div ref={notifRef} className="relative">
          <button
            onClick={() => setIsNotifOpen(!isNotifOpen)}
            className="relative p-2 text-slate-300 hover:text-white rounded-xl hover:bg-slate-800 transition-colors"
          >
            <Bell className="w-5 h-5" />
            {unreadCount > 0 && (
              <span className="absolute top-1 right-1 w-4 h-4 bg-rose-500 text-white rounded-full text-[10px] font-bold flex items-center justify-center animate-pulse">
                {unreadCount > 9 ? '9+' : unreadCount}
              </span>
            )}
          </button>

          {/* Notifications Dropdown Panel */}
          {isNotifOpen && (
            <div className="absolute right-0 mt-2 w-80 sm:w-96 bg-slate-900 border border-slate-800 rounded-2xl shadow-2xl overflow-hidden z-50">
              <div className="flex items-center justify-between px-4 py-3 border-b border-slate-800 bg-slate-900/60">
                <div className="flex items-center gap-2">
                  <span className="text-sm font-bold text-white">Live Alerts</span>
                  {unreadCount > 0 && (
                    <span className="text-[10px] px-2 py-0.5 rounded-full bg-rose-500/20 text-rose-400 font-bold border border-rose-500/30">
                      {unreadCount} New
                    </span>
                  )}
                </div>
                {unreadCount > 0 && (
                  <button
                    onClick={markAllAsRead}
                    className="text-xs text-emerald-400 hover:text-emerald-300 font-semibold transition-colors"
                  >
                    Mark all read
                  </button>
                )}
              </div>

              <div className="max-h-80 overflow-y-auto divide-y divide-slate-800/60">
                {notifications.length === 0 ? (
                  <div className="p-6 text-center text-xs text-slate-400">
                    No active notifications. Everything is in order!
                  </div>
                ) : (
                  notifications.slice(0, 6).map((n) => (
                    <div
                      key={n.id}
                      onClick={() => handleNotificationClick(n)}
                      className={`p-3.5 hover:bg-slate-800/80 cursor-pointer transition-colors flex items-start gap-3 ${
                        !n.isRead ? 'bg-slate-800/40' : ''
                      }`}
                    >
                      <div className="mt-0.5">
                        {n.type === 'CRITICAL_STOCK' || n.type === 'EXPIRED_PRODUCT' ? (
                          <div className="p-1.5 rounded-lg bg-rose-500/20 text-rose-400">
                            <AlertTriangle className="w-4 h-4" />
                          </div>
                        ) : n.type === 'LOW_STOCK' || n.type === 'EXPIRING_SOON' ? (
                          <div className="p-1.5 rounded-lg bg-amber-500/20 text-amber-400">
                            <AlertTriangle className="w-4 h-4" />
                          </div>
                        ) : (
                          <div className="p-1.5 rounded-lg bg-emerald-500/20 text-emerald-400">
                            <CheckCircle2 className="w-4 h-4" />
                          </div>
                        )}
                      </div>
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center justify-between gap-1">
                          <span className={`text-xs font-semibold truncate ${!n.isRead ? 'text-white' : 'text-slate-300'}`}>
                            {n.title}
                          </span>
                          {!n.isRead && <span className="w-2 h-2 rounded-full bg-emerald-400 shrink-0"></span>}
                        </div>
                        <p className="text-[11px] text-slate-400 mt-0.5 line-clamp-2 leading-tight">
                          {n.message}
                        </p>
                      </div>
                    </div>
                  ))
                )}
              </div>

              <div className="p-2.5 border-t border-slate-800 bg-slate-900/60 text-center">
                <Link
                  to="/notifications"
                  onClick={() => setIsNotifOpen(false)}
                  className="text-xs text-emerald-400 hover:text-emerald-300 font-semibold inline-flex items-center gap-1"
                >
                  View all notifications <ChevronRight className="w-3 h-3" />
                </Link>
              </div>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
