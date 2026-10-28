import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Bell,
  CheckCircle2,
  AlertTriangle,
  CreditCard,
  CheckCheck,
  ArrowRight,
  ShieldAlert,
} from 'lucide-react';
import { useNotifications } from '../context/NotificationContext';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';

export const NotificationsPage = () => {
  const navigate = useNavigate();
  const { notifications, unreadCount, markAsRead, markAllAsRead } = useNotifications();
  const [filterType, setFilterType] = useState('ALL');

  const filteredNotifications = (notifications || []).filter((n) => {
    if (filterType === 'UNREAD') return !n.isRead;
    if (filterType !== 'ALL') return n.type === filterType;
    return true;
  });

  const handleActionClick = (n) => {
    markAsRead(n.id);
    if (n.type === 'LOW_STOCK' || n.type === 'CRITICAL_STOCK') {
      navigate(`/purchases/new?productId=${n.entityId}`);
    } else if (n.type === 'EXPIRING_SOON' || n.type === 'EXPIRED_PRODUCT') {
      navigate('/ai/insights');
    } else if (n.type === 'CREDIT_LIMIT_EXCEEDED' || n.type === 'CREDIT_OVERDUE') {
      navigate(`/credit?customer=${n.entityId}`);
    } else {
      navigate('/inventory');
    }
  };

  const getNotificationIcon = (type) => {
    switch (type) {
      case 'CRITICAL_STOCK':
      case 'EXPIRED_PRODUCT':
        return <ShieldAlert className="w-5 h-5 text-rose-400" />;
      case 'LOW_STOCK':
      case 'EXPIRING_SOON':
        return <AlertTriangle className="w-5 h-5 text-amber-400" />;
      case 'CREDIT_LIMIT_EXCEEDED':
      case 'CREDIT_OVERDUE':
        return <CreditCard className="w-5 h-5 text-purple-400" />;
      default:
        return <CheckCircle2 className="w-5 h-5 text-emerald-400" />;
    }
  };

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      {/* Header */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white tracking-tight flex items-center gap-2.5">
            <Bell className="w-7 h-7 text-emerald-400" />
            Actionable Notification Hub
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Real-time stock alerts, expiry countdowns, customer credit thresholds, and restock actions
          </p>
        </div>

        {unreadCount > 0 && (
          <Button
            variant="outline"
            leftIcon={<CheckCheck className="w-4 h-4" />}
            onClick={markAllAsRead}
          >
            Mark All as Read ({unreadCount})
          </Button>
        )}
      </div>

      {/* Filter Tabs */}
      <div className="flex items-center gap-2 overflow-x-auto pb-1">
        <button
          onClick={() => setFilterType('ALL')}
          className={`px-3.5 py-2 rounded-xl text-xs font-semibold whitespace-nowrap transition-all ${
            filterType === 'ALL'
              ? 'bg-emerald-600 text-white'
              : 'bg-slate-900 border border-slate-800 text-slate-300 hover:bg-slate-800'
          }`}
        >
          All Notifications ({notifications?.length || 0})
        </button>

        <button
          onClick={() => setFilterType('UNREAD')}
          className={`px-3.5 py-2 rounded-xl text-xs font-semibold whitespace-nowrap transition-all ${
            filterType === 'UNREAD'
              ? 'bg-emerald-600 text-white'
              : 'bg-slate-900 border border-slate-800 text-slate-300 hover:bg-slate-800'
          }`}
        >
          Unread ({unreadCount})
        </button>

        <button
          onClick={() => setFilterType('LOW_STOCK')}
          className={`px-3.5 py-2 rounded-xl text-xs font-semibold whitespace-nowrap transition-all ${
            filterType === 'LOW_STOCK'
              ? 'bg-amber-600 text-white'
              : 'bg-slate-900 border border-slate-800 text-slate-300 hover:bg-slate-800'
          }`}
        >
          Low Stock
        </button>

        <button
          onClick={() => setFilterType('EXPIRING_SOON')}
          className={`px-3.5 py-2 rounded-xl text-xs font-semibold whitespace-nowrap transition-all ${
            filterType === 'EXPIRING_SOON'
              ? 'bg-purple-600 text-white'
              : 'bg-slate-900 border border-slate-800 text-slate-300 hover:bg-slate-800'
          }`}
        >
          Expiring Soon
        </button>
      </div>

      {/* Notification List */}
      <div className="space-y-3">
        {filteredNotifications.map((notif) => (
          <div
            key={notif.id}
            className={`p-4 rounded-2xl border transition-all flex items-start gap-4 shadow-lg ${
              !notif.isRead
                ? 'bg-slate-900 border-emerald-500/40'
                : 'bg-slate-900/60 border-slate-800/80 text-slate-400'
            }`}
          >
            <div className="p-2.5 rounded-xl bg-slate-800 border border-slate-700 mt-0.5">
              {getNotificationIcon(notif.type)}
            </div>

            <div className="flex-1 min-w-0">
              <div className="flex items-center justify-between gap-2">
                <h3 className={`text-sm font-bold truncate ${!notif.isRead ? 'text-white' : 'text-slate-300'}`}>
                  {notif.title}
                </h3>
                <span className="text-[11px] font-mono text-slate-400 shrink-0">
                  {new Date(notif.createdAt).toLocaleDateString()}
                </span>
              </div>

              <p className="text-xs text-slate-400 mt-1 leading-relaxed">{notif.message}</p>

              <div className="mt-3 flex items-center justify-between pt-2 border-t border-slate-800/80">
                <Badge variant={!notif.isRead ? 'success' : 'default'} size="sm">
                  {notif.type?.replace('_', ' ')}
                </Badge>

                <Button
                  size="sm"
                  variant="outline"
                  rightIcon={<ArrowRight className="w-3.5 h-3.5" />}
                  onClick={() => handleActionClick(notif)}
                >
                  Take Action
                </Button>
              </div>
            </div>
          </div>
        ))}

        {filteredNotifications.length === 0 && (
          <div className="py-20 text-center text-slate-400 text-xs bg-slate-900 border border-slate-800 rounded-3xl">
            No notifications in this category.
          </div>
        )}
      </div>
    </div>
  );
};
