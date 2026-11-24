import React from 'react';
import { Sparkles, Shield, Heart, Github, Twitter, Linkedin, Mail } from 'lucide-react';

export default function Footer() {
  return (
    <footer className="bg-navy-950 text-slate-400 border-t border-slate-800 text-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
          {/* Brand */}
          <div className="space-y-4">
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 rounded-lg gradient-brand flex items-center justify-center">
                <Sparkles className="w-4 h-4 text-white" />
              </div>
              <span className="font-bold text-white text-base">SmartResume AI</span>
            </div>
            <p className="text-xs text-slate-400 leading-relaxed">
              Empowering students, freshers, and professionals to optimize their resumes with explainable ATS analytics and intelligent career guidance.
            </p>
            <div className="flex items-center gap-3 text-slate-400">
              <a href="#github" className="hover:text-white transition-colors"><Github className="w-4 h-4" /></a>
              <a href="#twitter" className="hover:text-white transition-colors"><Twitter className="w-4 h-4" /></a>
              <a href="#linkedin" className="hover:text-white transition-colors"><Linkedin className="w-4 h-4" /></a>
            </div>
          </div>

          {/* Product */}
          <div>
            <h4 className="text-xs font-semibold text-white uppercase tracking-wider mb-3">Product</h4>
            <ul className="space-y-2 text-xs">
              <li><a href="#features" className="hover:text-white transition-colors">ATS Score Analyzer</a></li>
              <li><a href="#jobmatching" className="hover:text-white transition-colors">Job Description Matcher</a></li>
              <li><a href="#ai" className="hover:text-white transition-colors">AI Resume Rewriter</a></li>
              <li><a href="#skillgap" className="hover:text-white transition-colors">Skill Gap Roadmap</a></li>
              <li><a href="#interview" className="hover:text-white transition-colors">Interview Questions Generator</a></li>
            </ul>
          </div>

          {/* Resources */}
          <div>
            <h4 className="text-xs font-semibold text-white uppercase tracking-wider mb-3">Resources</h4>
            <ul className="space-y-2 text-xs">
              <li><a href="#how" className="hover:text-white transition-colors">How ATS Works</a></li>
              <li><a href="#faq" className="hover:text-white transition-colors">Frequently Asked Questions</a></li>
              <li><a href="#templates" className="hover:text-white transition-colors">Standard Job Templates</a></li>
              <li><a href="#privacy" className="hover:text-white transition-colors">Data Privacy & Security</a></li>
            </ul>
          </div>

          {/* Security & Contact */}
          <div>
            <h4 className="text-xs font-semibold text-white uppercase tracking-wider mb-3">Enterprise Security</h4>
            <p className="text-xs text-slate-400 mb-3 flex items-center gap-1.5">
              <Shield className="w-4 h-4 text-emerald-400 shrink-0" />
              <span>JWT authentication, role authorization & encrypted storage.</span>
            </p>
            <div className="text-xs text-slate-400">
              <p className="font-semibold text-slate-300">Need Assistance?</p>
              <p className="mt-1 flex items-center gap-1"><Mail className="w-3.5 h-3.5" /> support@smartresume.ai</p>
            </div>
          </div>
        </div>

        <div className="pt-8 border-t border-slate-800/80 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs">
          <p>© {new Date().getFullYear()} Smart Resume Analyzer. Built for serious career growth.</p>
          <div className="flex gap-4">
            <a href="#privacy" className="hover:text-white transition-colors">Privacy Policy</a>
            <a href="#terms" className="hover:text-white transition-colors">Terms of Service</a>
            <a href="#security" className="hover:text-white transition-colors">Security Standards</a>
          </div>
        </div>
      </div>
    </footer>
  );
}
