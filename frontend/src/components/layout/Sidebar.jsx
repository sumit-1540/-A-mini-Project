import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  ShoppingCart,
  Package,
  Boxes,
  Users,
  Building2,
  Truck,
  Receipt,
  CreditCard,
  BarChart3,
  Sparkles,
  TrendingUp,
  FileText,
  Bell,
  Download,
  UserCheck,
  LogOut,
  Store,
  Settings,
  Zap,
  Star,
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { useNotifications } from '../../context/NotificationContext';

export const Sidebar = ({ isOpen, onClose }) => {
  const { user, logout, isAdmin } = useAuth();
  const { unreadCount } = useNotifications();

  const mainNavItems = [
    { label: 'Dashboard', path: '/', icon: LayoutDashboard, role: 'ALL' },
    { label: '⚡ Walk-in Express POS', path: '/pos?mode=express', icon: Zap, role: 'ALL', highlight: true },
    { label: '⭐ Daily Customer POS', path: '/pos?mode=regular', icon: Star, role: 'ALL' },
    { label: 'Products', path: '/products', icon: Package, role: 'ALL' },
    { label: 'Inventory', path: '/inventory', icon: Boxes, role: 'ALL' },
    { label: 'Customers', path: '/customers', icon: Users, role: 'ALL' },
    { label: 'Customer Credit', path: '/credit', icon: CreditCard, role: 'ALL' },
    { label: 'Sales History', path: '/sales', icon: Receipt, role: 'ALL' },
  ];

  const aiIntelligenceItems = [
    { label: 'AI Insights & Actions', path: '/ai/insights', icon: Sparkles, role: 'ADMIN' },
    { label: 'Demand Prediction', path: '/ai/demand', icon: TrendingUp, role: 'ADMIN' },
    { label: 'Handwritten List OCR', path: '/ai/handwritten-list', icon: FileText, role: 'ALL' },
    { label: 'Live Alerts', path: '/notifications', icon: Bell, role: 'ALL', badge: unreadCount },
  ];

  const adminNavItems = [
    { label: 'Suppliers / Dealers', path: '/suppliers', icon: Building2, role: 'ADMIN' },
    { label: 'Purchases & Restock', path: '/purchases', icon: Truck, role: 'ADMIN' },
    { label: 'Business Reports', path: '/reports', icon: Download, role: 'ADMIN' },
    { label: 'Analytics & Profit', path: '/analytics', icon: BarChart3, role: 'ADMIN' },
    { label: 'Staff Management', path: '/users', icon: UserCheck, role: 'ADMIN' },
    { label: 'Store Settings', path: '/settings', icon: Settings, role: 'ADMIN' },
  ];

  const filteredNav = (items) => {
    return items.filter((item) => item.role === 'ALL' || (item.role === 'ADMIN' && isAdmin));
  };

  return (
    <>
      {/* Mobile backdrop */}
      {isOpen && (
        <div
          onClick={onClose}
          className="fixed inset-0 bg-slate-950/80 backdrop-blur-sm z-40 lg:hidden"
        />
      )}

      <aside
        className={`fixed top-0 bottom-0 left-0 z-40 w-64 bg-slate-900 border-r border-slate-800/80 flex flex-col transition-transform duration-300 lg:translate-x-0 ${
          isOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        {/* Brand Logo Header */}
        <div className="h-16 flex items-center justify-between px-6 border-b border-slate-800/80 bg-slate-900/60">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-emerald-400 to-teal-600 flex items-center justify-center shadow-lg shadow-emerald-950/40 text-slate-950 font-black">
              <Store className="w-5 h-5 text-slate-950" />
            </div>
            <div>
              <div className="font-extrabold text-base tracking-tight text-white flex items-center gap-1.5">
                SmartShelf <span className="text-[10px] px-1.5 py-0.5 rounded bg-emerald-500/20 text-emerald-400 font-bold border border-emerald-500/30">AI</span>
              </div>
              <div className="text-[11px] text-slate-400">Grocery Management</div>
            </div>
          </div>
        </div>

        {/* Navigation Sections */}
        <div className="flex-1 overflow-y-auto px-4 py-4 space-y-6">
          {/* Main Core Workflows */}
          <div>
            <div className="px-3 mb-2 text-[10px] font-bold uppercase tracking-wider text-slate-400">
              Operations & POS
            </div>
            <nav className="space-y-1">
              {filteredNav(mainNavItems).map((item) => (
                <NavLink
                  key={item.path}
                  to={item.path}
                  onClick={() => onClose && onClose()}
                  className={({ isActive }) =>
                    `flex items-center justify-between px-3.5 py-2.5 rounded-xl text-sm font-medium transition-all ${
                      isActive
                        ? 'bg-emerald-600 text-white shadow-lg shadow-emerald-950/40'
                        : item.highlight
                        ? 'text-emerald-400 hover:bg-emerald-500/10 hover:text-emerald-300'
                        : 'text-slate-300 hover:bg-slate-800 hover:text-white'
                    }`
                  }
                >
                  <div className="flex items-center gap-3">
                    <item.icon className="w-4 h-4 shrink-0" />
                    <span>{item.label}</span>
                  </div>
                  {item.highlight && (
                    <span className="text-[10px] px-1.5 py-0.5 rounded bg-emerald-400/20 text-emerald-300 font-bold">
                      POS
                    </span>
                  )}
                </NavLink>
              ))}
            </nav>
          </div>

          {/* AI Intelligence */}
          <div>
            <div className="px-3 mb-2 text-[10px] font-bold uppercase tracking-wider text-emerald-400 flex items-center gap-1.5">
              <Sparkles className="w-3 h-3" />
              AI Intelligence
            </div>
            <nav className="space-y-1">
              {filteredNav(aiIntelligenceItems).map((item) => (
                <NavLink
                  key={item.path}
                  to={item.path}
                  onClick={() => onClose && onClose()}
                  className={({ isActive }) =>
                    `flex items-center justify-between px-3.5 py-2.5 rounded-xl text-sm font-medium transition-all ${
                      isActive
                        ? 'bg-emerald-600 text-white shadow-lg shadow-emerald-950/40'
                        : 'text-slate-300 hover:bg-slate-800 hover:text-white'
                    }`
                  }
                >
                  <div className="flex items-center gap-3">
                    <item.icon className="w-4 h-4 shrink-0" />
                    <span>{item.label}</span>
                  </div>
                  {item.badge !== undefined && item.badge > 0 && (
                    <span className="text-[11px] px-2 py-0.5 rounded-full bg-rose-500 text-white font-bold animate-pulse">
                      {item.badge}
                    </span>
                  )}
                </NavLink>
              ))}
            </nav>
          </div>

          {/* Administration & Backoffice */}
          {isAdmin && (
            <div>
              <div className="px-3 mb-2 text-[10px] font-bold uppercase tracking-wider text-slate-400">
                Administration
              </div>
              <nav className="space-y-1">
                {filteredNav(adminNavItems).map((item) => (
                  <NavLink
                    key={item.path}
                    to={item.path}
                    onClick={() => onClose && onClose()}
                    className={({ isActive }) =>
                      `flex items-center justify-between px-3.5 py-2.5 rounded-xl text-sm font-medium transition-all ${
                        isActive
                          ? 'bg-emerald-600 text-white shadow-lg shadow-emerald-950/40'
                          : 'text-slate-300 hover:bg-slate-800 hover:text-white'
                      }`
                    }
                  >
                    <div className="flex items-center gap-3">
                      <item.icon className="w-4 h-4 shrink-0" />
                      <span>{item.label}</span>
                    </div>
                  </NavLink>
                ))}
              </nav>
            </div>
          )}
        </div>

        {/* User Footer Profile & Logout */}
        <div className="p-4 border-t border-slate-800/80 bg-slate-900/80">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3 overflow-hidden">
              <div className="w-9 h-9 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center font-bold text-emerald-400 text-sm">
                {user?.fullName?.charAt(0) || 'U'}
              </div>
              <div className="truncate">
                <div className="text-xs font-semibold text-white truncate">{user?.fullName}</div>
                <div className="text-[10px] text-slate-400 capitalize">
                  {user?.role === 'ROLE_ADMIN' ? 'Owner / Admin' : 'Store Cashier'}
                </div>
              </div>
            </div>
            <button
              onClick={logout}
              title="Sign Out"
              className="p-2 text-slate-400 hover:text-rose-400 hover:bg-slate-800 rounded-lg transition-colors"
            >
              <LogOut className="w-4 h-4" />
            </button>
          </div>
        </div>
      </aside>
    </>
  );
};
