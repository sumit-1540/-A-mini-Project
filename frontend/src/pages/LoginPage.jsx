import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Store, Lock, User as UserIcon, ArrowRight, ShieldCheck, Sparkles } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { Button } from '../components/common/Button';

export const LoginPage = () => {
  const navigate = useNavigate();
  const { login, isAuthenticated } = useAuth();
  const { error, success } = useToast();

  React.useEffect(() => {
    if (isAuthenticated) {
      navigate('/', { replace: true });
    }
  }, [isAuthenticated, navigate]);

  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('admin123');
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setIsLoading(true);
      await login({ username, password });
      success(`Welcome back! Logged in as ${username}`);
      navigate('/');
    } catch (err) {
      error(err.response?.data?.message || 'Invalid username or password');
    } finally {
      setIsLoading(false);
    }
  };

  const handleQuickDemoLogin = (userType) => {
    if (userType === 'admin') {
      setUsername('admin');
      setPassword('admin123');
    } else {
      setUsername('cashier');
      setPassword('cashier123');
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 flex flex-col justify-center items-center p-4 relative overflow-hidden font-sans">
      {/* Background Glows */}
      <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-emerald-500/10 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-1/4 right-1/3 w-80 h-80 bg-teal-500/10 rounded-full blur-3xl pointer-events-none" />

      <div className="max-w-md w-full relative z-10 space-y-6">
        {/* Brand Header */}
        <div className="text-center space-y-2">
          <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-gradient-to-br from-emerald-400 to-teal-600 shadow-xl shadow-emerald-950/60 mb-2">
            <Store className="w-8 h-8 text-slate-950" />
          </div>
          <h1 className="text-3xl font-extrabold text-white tracking-tight flex items-center justify-center gap-2">
            SmartShelf <span className="text-xs px-2 py-0.5 rounded-full bg-emerald-500/20 text-emerald-400 font-bold border border-emerald-500/30">AI</span>
          </h1>
          <p className="text-xs text-slate-400">
            Intelligent Grocery Shop Management & POS System
          </p>
        </div>

        {/* Login Card */}
        <div className="bg-slate-900/80 border border-slate-800 backdrop-blur-xl rounded-3xl p-8 shadow-2xl space-y-6">
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">Username</label>
              <div className="relative">
                <UserIcon className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                <input
                  type="text"
                  required
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="Enter username"
                  className="w-full pl-10 pr-4 py-2.5 bg-slate-800/80 border border-slate-700 rounded-xl text-sm text-white placeholder:text-slate-500 focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">Password</label>
              <div className="relative">
                <Lock className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                <input
                  type="password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Enter password"
                  className="w-full pl-10 pr-4 py-2.5 bg-slate-800/80 border border-slate-700 rounded-xl text-sm text-white placeholder:text-slate-500 focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500"
                />
              </div>
            </div>

            <Button
              type="submit"
              variant="primary"
              size="lg"
              className="w-full mt-2"
              isLoading={isLoading}
              rightIcon={<ArrowRight className="w-4 h-4" />}
            >
              Sign In to SmartShelf
            </Button>
          </form>

          {/* Quick Demo Login Credentials */}
          <div className="pt-4 border-t border-slate-800/80 space-y-2.5">
            <div className="text-[11px] font-semibold text-slate-400 text-center uppercase tracking-wider flex items-center justify-center gap-1.5">
              <Sparkles className="w-3.5 h-3.5 text-emerald-400" />
              1-Click Demo Login
            </div>
            <div className="grid grid-cols-2 gap-2.5">
              <button
                type="button"
                onClick={() => handleQuickDemoLogin('admin')}
                className={`p-2.5 rounded-xl border text-left transition-all ${
                  username === 'admin'
                    ? 'bg-emerald-500/10 border-emerald-500/40 text-emerald-400'
                    : 'bg-slate-800/50 border-slate-700/60 text-slate-300 hover:bg-slate-800'
                }`}
              >
                <div className="text-xs font-bold">Admin / Owner</div>
                <div className="text-[10px] text-slate-400">admin / admin123</div>
              </button>

              <button
                type="button"
                onClick={() => handleQuickDemoLogin('cashier')}
                className={`p-2.5 rounded-xl border text-left transition-all ${
                  username === 'cashier'
                    ? 'bg-emerald-500/10 border-emerald-500/40 text-emerald-400'
                    : 'bg-slate-800/50 border-slate-700/60 text-slate-300 hover:bg-slate-800'
                }`}
              >
                <div className="text-xs font-bold">Staff Cashier</div>
                <div className="text-[10px] text-slate-400">cashier / cashier123</div>
              </button>
            </div>
          </div>
        </div>

        {/* Feature Pill Footer */}
        <div className="flex items-center justify-center gap-4 text-[11px] text-slate-400">
          <span className="flex items-center gap-1">
            <ShieldCheck className="w-3.5 h-3.5 text-emerald-400" /> JWT Security
          </span>
          <span>•</span>
          <span>Offline Fallback</span>
          <span>•</span>
          <span>Thermal POS Ready</span>
        </div>
      </div>
    </div>
  );
};
