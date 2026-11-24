import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { 
  FileText, Sparkles, BarChart2, Briefcase, Bot, 
  Target, MessageSquare, GitCompare, Shield, LogOut, 
  Menu, X, User as UserIcon 
} from 'lucide-react';

export default function Navbar({ currentView, setCurrentView, openLogin, openRegister, openUpload }) {
  const { user, isAuthenticated, isAdmin, logout } = useAuth();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const navItems = [
    { id: 'dashboard', label: 'Dashboard', icon: BarChart2, authRequired: true },
    { id: 'resumes', label: 'My Resumes', icon: FileText, authRequired: true },
    { id: 'analysis', label: 'ATS Score', icon: Target, authRequired: true },
    { id: 'jobmatching', label: 'Job Matcher', icon: Briefcase, authRequired: true },
    { id: 'ai-assistant', label: 'AI Assistant', icon: Bot, authRequired: true },
    { id: 'skills', label: 'Skill Gap', icon: Sparkles, authRequired: true },
    { id: 'interview', label: 'Interview Prep', icon: MessageSquare, authRequired: true },
    { id: 'compare', label: 'Compare Versions', icon: GitCompare, authRequired: true },
  ];

  if (isAdmin) {
    navItems.push({ id: 'admin', label: 'Admin Portal', icon: Shield, authRequired: true });
  }

  const handleNavClick = (viewId) => {
    setCurrentView(viewId);
    setMobileMenuOpen(false);
  };

  return (
    <nav className="sticky top-0 z-40 bg-navy-900/95 backdrop-blur-md border-b border-slate-800 text-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <div 
            onClick={() => handleNavClick(isAuthenticated ? 'dashboard' : 'landing')} 
            className="flex items-center gap-2 cursor-pointer group"
          >
            <div className="w-10 h-10 rounded-xl gradient-brand flex items-center justify-center shadow-lg shadow-purple-900/30 group-hover:scale-105 transition-transform">
              <Sparkles className="w-5 h-5 text-white" />
            </div>
            <div>
              <span className="font-extrabold text-lg tracking-tight bg-gradient-to-r from-white via-slate-100 to-purple-300 bg-clip-text text-transparent">
                SmartResume
              </span>
              <span className="text-xs ml-1.5 px-1.5 py-0.5 rounded bg-brand-purple/30 text-purple-300 font-medium border border-purple-500/30">
                AI
              </span>
            </div>
          </div>

          {/* Desktop Navigation Links */}
          {isAuthenticated && (
            <div className="hidden lg:flex items-center gap-1">
              {navItems.map((item) => {
                const Icon = item.icon;
                const isActive = currentView === item.id;
                return (
                  <button
                    key={item.id}
                    onClick={() => handleNavClick(item.id)}
                    className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-sm font-medium transition-all ${
                      isActive
                        ? 'bg-brand-purple text-white shadow-md shadow-purple-600/30'
                        : 'text-slate-300 hover:text-white hover:bg-slate-800/60'
                    }`}
                  >
                    <Icon className="w-4 h-4" />
                    <span>{item.label}</span>
                  </button>
                );
              })}
            </div>
          )}

          {/* Action & User Section */}
          <div className="hidden md:flex items-center gap-3">
            {isAuthenticated ? (
              <>
                <button
                  onClick={openUpload}
                  className="flex items-center gap-1.5 px-3.5 py-1.5 text-xs font-semibold rounded-lg bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-500 hover:to-indigo-500 text-white shadow-sm transition-all"
                >
                  <FileText className="w-3.5 h-3.5" />
                  Upload Resume
                </button>

                <div className="flex items-center gap-2 pl-3 border-l border-slate-700">
                  <div className="text-right">
                    <p className="text-xs font-semibold text-white leading-tight">{user?.fullName}</p>
                    <p className="text-[10px] text-purple-400 font-medium">{user?.role === 'ADMIN' ? 'Administrator' : 'Job Seeker'}</p>
                  </div>
                  <button
                    onClick={logout}
                    title="Sign Out"
                    className="p-1.5 rounded-lg text-slate-400 hover:text-red-400 hover:bg-slate-800 transition-colors"
                  >
                    <LogOut className="w-4 h-4" />
                  </button>
                </div>
              </>
            ) : (
              <div className="flex items-center gap-2">
                <button
                  onClick={openLogin}
                  className="px-4 py-2 rounded-lg text-sm font-medium text-slate-200 hover:text-white hover:bg-slate-800/80 transition-colors"
                >
                  Sign In
                </button>
                <button
                  onClick={openRegister}
                  className="px-4 py-2 rounded-lg text-sm font-semibold bg-brand-purple hover:bg-purple-600 text-white shadow-md shadow-purple-700/30 transition-all hover:scale-105"
                >
                  Get Started Free
                </button>
              </div>
            )}
          </div>

          {/* Mobile menu button */}
          <div className="flex lg:hidden items-center gap-2">
            {isAuthenticated && (
              <button
                onClick={openUpload}
                className="px-2.5 py-1 text-xs font-semibold rounded bg-brand-purple text-white"
              >
                Upload
              </button>
            )}
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="p-2 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800"
            >
              {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Drawer */}
      {mobileMenuOpen && (
        <div className="lg:hidden bg-navy-950 border-b border-slate-800 px-4 pt-2 pb-4 space-y-1">
          {isAuthenticated ? (
            <>
              {navItems.map((item) => {
                const Icon = item.icon;
                const isActive = currentView === item.id;
                return (
                  <button
                    key={item.id}
                    onClick={() => handleNavClick(item.id)}
                    className={`flex items-center gap-2.5 w-full px-3 py-2 rounded-lg text-sm font-medium ${
                      isActive ? 'bg-brand-purple text-white' : 'text-slate-300 hover:bg-slate-800'
                    }`}
                  >
                    <Icon className="w-4 h-4" />
                    <span>{item.label}</span>
                  </button>
                );
              })}
              <div className="pt-3 border-t border-slate-800 flex justify-between items-center text-xs text-slate-400 px-2">
                <span>Signed in as <strong className="text-white">{user?.fullName}</strong></span>
                <button onClick={logout} className="text-red-400 hover:underline">Sign Out</button>
              </div>
            </>
          ) : (
            <div className="flex flex-col gap-2 pt-2">
              <button
                onClick={() => { openLogin(); setMobileMenuOpen(false); }}
                className="w-full py-2.5 rounded-lg text-center text-sm font-medium bg-slate-800 text-white"
              >
                Sign In
              </button>
              <button
                onClick={() => { openRegister(); setMobileMenuOpen(false); }}
                className="w-full py-2.5 rounded-lg text-center text-sm font-semibold bg-brand-purple text-white"
              >
                Get Started Free
              </button>
            </div>
          )}
        </div>
      )}
    </nav>
  );
}
