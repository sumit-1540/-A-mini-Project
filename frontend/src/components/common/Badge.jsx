import React from 'react';

export const Badge = ({
  children,
  variant = 'default',
  size = 'md',
  className = '',
}) => {
  const variantStyles = {
    default: 'bg-slate-800 text-slate-300 border-slate-700',
    success: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30',
    warning: 'bg-amber-500/10 text-amber-400 border-amber-500/30',
    danger: 'bg-rose-500/10 text-rose-400 border-rose-500/30',
    info: 'bg-sky-500/10 text-sky-400 border-sky-500/30',
    purple: 'bg-indigo-500/10 text-indigo-400 border-indigo-500/30',
    amber: 'bg-orange-500/10 text-orange-400 border-orange-500/30',
  };

  const sizeStyles = {
    sm: 'px-2 py-0.5 text-xs font-medium',
    md: 'px-2.5 py-1 text-xs font-semibold',
  };

  return (
    <span
      className={`inline-flex items-center gap-1 rounded-full border ${variantStyles[variant] || variantStyles.default} ${sizeStyles[size] || sizeStyles.md} ${className}`}
    >
      {children}
    </span>
  );
};

export const StockStatusBadge = ({
  status,
  stock,
  unit = 'pcs',
}) => {
  switch (status) {
    case 'HEALTHY':
      return (
        <Badge variant="success">
          <span className="w-1.5 h-1.5 rounded-full bg-emerald-400"></span>
          In Stock {stock !== undefined ? `(${stock} ${unit})` : ''}
        </Badge>
      );
    case 'LOW':
      return (
        <Badge variant="warning">
          <span className="w-1.5 h-1.5 rounded-full bg-amber-400 animate-pulse"></span>
          Low Stock {stock !== undefined ? `(${stock} ${unit})` : ''}
        </Badge>
      );
    case 'CRITICAL':
      return (
        <Badge variant="danger">
          <span className="w-1.5 h-1.5 rounded-full bg-rose-400 animate-ping"></span>
          Out of Stock {stock !== undefined ? `(${stock} ${unit})` : ''}
        </Badge>
      );
    case 'EXPIRED':
      return (
        <Badge variant="danger">
          <span className="w-1.5 h-1.5 rounded-full bg-rose-500"></span>
          Expired
        </Badge>
      );
    default:
      return <Badge>{status}</Badge>;
  }
};
