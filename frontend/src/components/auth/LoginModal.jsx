import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { X, Lock, Mail, ArrowRight, ShieldCheck, User } from 'lucide-react';

export default function LoginModal({ isOpen, onClose, openRegister, openForgotPassword, onSuccess }) {
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await login(email, password);
      onSuccess?.();
      onClose();
    } catch (err) {
      setError(err.message || 'Login failed. Please check credentials.');
    } finally {
      setLoading(false);
    }
  };

  const fillCredentials = (userEmail, userPass) => {
    setEmail(userEmail);
    setPassword(userPass);
    setError('');
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-navy-950/70 backdrop-blur-sm animate-fade-in">
      <div className="relative w-full max-w-md bg-white rounded-2xl shadow-2xl border border-slate-100 overflow-hidden">
        {/* Header */}
        <div className="gradient-brand p-6 text-white text-center relative">
          <button
            onClick={onClose}
            className="absolute top-4 right-4 p-1.5 rounded-full bg-white/10 hover:bg-white/20 transition-colors"
          >
            <X className="w-4 h-4 text-white" />
          </button>
          <div className="w-12 h-12 rounded-xl bg-white/10 mx-auto flex items-center justify-center mb-3">
            <Lock className="w-6 h-6 text-white" />
          </div>
          <h3 className="text-xl font-bold">Welcome Back</h3>
          <p className="text-xs text-purple-200 mt-1">Sign in to access your resumes &amp; analytics</p>
        </div>

        {/* Quick Demo Credentials Banner */}
        <div className="bg-purple-50/70 p-3.5 border-b border-purple-100 flex flex-col gap-1.5 text-xs">
          <span className="font-semibold text-brand-purple flex items-center gap-1">
            <ShieldCheck className="w-3.5 h-3.5" /> Instant Demo Sign-In:
          </span>
          <div className="flex gap-2">
            <button
              type="button"
              onClick={() => fillCredentials('john.doe@example.com', 'password123')}
              className="flex-1 py-1.5 px-2.5 rounded-lg bg-white border border-purple-200 text-purple-900 font-medium hover:bg-purple-100/50 transition-colors text-left text-[11px]"
            >
              👤 <strong>Candidate</strong> (John Doe)
            </button>
            <button
              type="button"
              onClick={() => fillCredentials('admin@smartresume.com', 'password123')}
              className="flex-1 py-1.5 px-2.5 rounded-lg bg-white border border-indigo-200 text-indigo-900 font-medium hover:bg-indigo-100/50 transition-colors text-left text-[11px]"
            >
              🛡️ <strong>Admin</strong> (System Admin)
            </button>
          </div>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {error && (
            <div className="p-3 rounded-lg bg-rose-50 border border-rose-200 text-rose-700 text-xs">
              {error}
            </div>
          )}

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Email Address</label>
            <div className="relative">
              <Mail className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
              <input
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="name@example.com"
                className="w-full pl-9 pr-3 py-2 text-sm rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-brand-purple focus:border-transparent transition-all"
              />
            </div>
          </div>

          <div>
            <div className="flex justify-between items-center mb-1">
              <label className="block text-xs font-semibold text-slate-700">Password</label>
              <button
                type="button"
                onClick={() => { onClose(); openForgotPassword(); }}
                className="text-xs text-brand-purple hover:underline"
              >
                Forgot Password?
              </button>
            </div>
            <div className="relative">
              <Lock className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full pl-9 pr-3 py-2 text-sm rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-brand-purple focus:border-transparent transition-all"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-2.5 rounded-lg text-sm font-bold bg-brand-purple hover:bg-purple-600 text-white shadow-md shadow-purple-600/20 transition-all flex items-center justify-center gap-2 disabled:opacity-50"
          >
            {loading ? <span>Signing In...</span> : (
              <>
                <span>Sign In</span>
                <ArrowRight className="w-4 h-4" />
              </>
            )}
          </button>

          <p className="text-center text-xs text-slate-500 pt-2">
            Don't have an account?{' '}
            <button
              type="button"
              onClick={() => { onClose(); openRegister(); }}
              className="text-brand-purple font-semibold hover:underline"
            >
              Create Account
            </button>
          </p>
        </form>
      </div>
    </div>
  );
}
