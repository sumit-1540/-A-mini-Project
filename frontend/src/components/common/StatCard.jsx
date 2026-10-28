import React from 'react';

export const StatCard = ({
  title,
  value,
  subtitle,
  icon: Icon,
  trend,
  variant = 'default',
  onClick,
}) => {
  const variantStyles = {
    default: 'from-slate-800/80 to-slate-900/80 border-slate-800 text-slate-100',
    emerald: 'from-emerald-950/40 to-slate-900/80 border-emerald-500/20 text-emerald-400',
    amber: 'from-amber-950/40 to-slate-900/80 border-amber-500/20 text-amber-400',
    rose: 'from-rose-950/40 to-slate-900/80 border-rose-500/20 text-rose-400',
    sky: 'from-sky-950/40 to-slate-900/80 border-sky-500/20 text-sky-400',
    indigo: 'from-indigo-950/40 to-slate-900/80 border-indigo-500/20 text-indigo-400',
  };

  const iconBgStyles = {
    default: 'bg-slate-800 text-slate-300',
    emerald: 'bg-emerald-500/20 text-emerald-400',
    amber: 'bg-amber-500/20 text-amber-400',
    rose: 'bg-rose-500/20 text-rose-400',
    sky: 'bg-sky-500/20 text-sky-400',
    indigo: 'bg-indigo-500/20 text-indigo-400',
  };

  return (
    <div
      onClick={onClick}
      className={`p-5 rounded-2xl bg-gradient-to-br border ${variantStyles[variant] || variantStyles.default} backdrop-blur-md shadow-xl transition-all duration-200 ${
        onClick ? 'cursor-pointer hover:-translate-y-1 hover:border-slate-700' : ''
      }`}
    >
      <div className="flex items-center justify-between">
        <span className="text-xs font-semibold uppercase tracking-wider text-slate-400">{title}</span>
        {Icon && (
          <div className={`p-2.5 rounded-xl ${iconBgStyles[variant] || iconBgStyles.default}`}>
            <Icon className="w-5 h-5" />
          </div>
        )}
      </div>

      <div className="mt-3">
        <div className="text-2xl lg:text-3xl font-extrabold text-white tracking-tight">{value}</div>
        {subtitle && <div className="text-xs text-slate-400 mt-1">{subtitle}</div>}
      </div>

      {trend && (
        <div className="mt-3 pt-3 border-t border-slate-800/80 flex items-center gap-1.5 text-xs font-medium">
          <span className={trend.isPositive ? 'text-emerald-400' : 'text-rose-400'}>
            {trend.isPositive ? '↑' : '↓'} {trend.value}
          </span>
          <span className="text-slate-500">vs last week</span>
        </div>
      )}
    </div>
  );
};
