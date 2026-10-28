import React from 'react';
import { Loader2 } from 'lucide-react';

export const LoadingSpinner = ({ message = 'Loading...' }) => {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-slate-400 gap-3">
      <Loader2 className="w-8 h-8 animate-spin text-emerald-500" />
      <span className="text-sm font-medium">{message}</span>
    </div>
  );
};
