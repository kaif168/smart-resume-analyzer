import React, { useState, useEffect } from 'react';
import { api } from '../../api/apiClient';
import { 
  Target, Download, Sparkles, CheckCircle2, AlertTriangle, 
  ArrowRight, ShieldCheck, RefreshCw, Briefcase, Bot 
} from 'lucide-react';
import confetti from 'canvas-confetti';

export default function AtsScoreView({ resumeId, resumes, onSelectResume, onGoToJobMatch, onGoToAi }) {
  const [analysis, setAnalysis] = useState(null);
  const [loading, setLoading] = useState(false);
  const [downloading, setDownloading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (resumeId) {
      loadAnalysis(resumeId);
    }
  }, [resumeId]);

  const loadAnalysis = async (id, forceRefresh = false) => {
    setLoading(true);
    setError('');
    try {
      const data = forceRefresh 
        ? await api.analyzeResume(id)
        : await api.getAnalysis(id);
      setAnalysis(data);

      if (data.overallScore >= 80) {
        confetti({
          particleCount: 50,
          spread: 60,
          origin: { y: 0.7 }
        });
      }
    } catch (err) {
      setError(err.message || 'Failed to load analysis for resume');
    } finally {
      setLoading(false);
    }
  };

  const handleDownloadPdf = async () => {
    if (!analysis?.id) return;
    setDownloading(true);
    try {
      const blob = await api.downloadReport(analysis.id);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `SmartResume_Analysis_${analysis.candidateName || 'Candidate'}.pdf`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (err) {
      alert('Error downloading report: ' + err.message);
    } finally {
      setDownloading(false);
    }
  };

  if (!resumeId && (!resumes || resumes.length === 0)) {
    return (
      <div className="max-w-4xl mx-auto py-16 px-4 text-center">
        <div className="w-16 h-16 rounded-2xl bg-purple-100 text-brand-purple flex items-center justify-center mx-auto mb-4">
          <Target className="w-8 h-8" />
        </div>
        <h3 className="text-xl font-bold text-slate-800">No Resumes Found</h3>
        <p className="text-sm text-slate-500 mt-2">Upload a resume document first to view your ATS compatibility score.</p>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Top Selector Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-4 rounded-2xl border border-slate-200 shadow-sm">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl gradient-brand text-white flex items-center justify-center">
            <Target className="w-5 h-5" />
          </div>
          <div>
            <h2 className="text-lg font-bold text-slate-900">ATS Resume Compatibility Score</h2>
            <p className="text-xs text-slate-500">Explainable multi-category algorithmic evaluation</p>
          </div>
        </div>

        <div className="flex items-center gap-2">
          {resumes && resumes.length > 1 && (
            <select
              value={resumeId}
              onChange={(e) => onSelectResume(Number(e.target.value))}
              className="px-3 py-1.5 text-xs font-semibold rounded-lg border border-slate-300 bg-slate-50 focus:outline-none focus:ring-2 focus:ring-brand-purple"
            >
              {resumes.map((r) => (
                <option key={r.id} value={r.id}>
                  {r.originalFileName} ({r.versionTag})
                </option>
              ))}
            </select>
          )}

          <button
            onClick={() => loadAnalysis(resumeId, true)}
            disabled={loading}
            className="p-2 rounded-lg text-slate-600 hover:text-brand-purple hover:bg-slate-100 border border-slate-200 transition-colors"
            title="Re-run ATS Analysis"
          >
            <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin text-brand-purple' : ''}`} />
          </button>

          <button
            onClick={handleDownloadPdf}
            disabled={downloading || !analysis}
            className="flex items-center gap-1.5 px-3.5 py-2 text-xs font-bold rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white shadow-sm transition-all disabled:opacity-50"
          >
            <Download className="w-4 h-4" />
            <span>{downloading ? 'Generating PDF...' : 'Download Report'}</span>
          </button>
        </div>
      </div>

      {error && (
        <div className="p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-xs">
          {error}
        </div>
      )}

      {loading && !analysis ? (
        <div className="py-20 text-center">
          <div className="w-12 h-12 rounded-full border-4 border-purple-200 border-t-brand-purple animate-spin mx-auto mb-4" />
          <p className="text-sm font-semibold text-slate-700">Evaluating 7 ATS categories with Apache PDFBox &amp; POI...</p>
        </div>
      ) : analysis ? (
        <div className="space-y-6">
          {/* Hero Score Card */}
          <div className="bg-gradient-to-r from-navy-900 via-indigo-950 to-navy-900 rounded-3xl p-6 sm:p-8 text-white shadow-xl relative overflow-hidden">
            <div className="relative z-10 flex flex-col md:flex-row items-center justify-between gap-8">
              {/* Score Display */}
              <div className="flex flex-col sm:flex-row items-center gap-6 text-center sm:text-left">
                <div className="relative w-32 h-32 flex items-center justify-center rounded-full bg-slate-800/80 border-4 border-purple-500/50 shadow-inner">
                  <div className="text-center">
                    <span className="text-4xl font-extrabold text-white tracking-tight">{analysis.overallScore}</span>
                    <span className="text-xs text-purple-300 block font-medium">/ 100</span>
                  </div>
                </div>

                <div>
                  <div className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-purple-500/20 text-purple-300 text-xs font-semibold mb-2 border border-purple-500/30">
                    <Sparkles className="w-3.5 h-3.5" />
                    <span>Rating: {analysis.scoreRating}</span>
                  </div>
                  <h3 className="text-xl sm:text-2xl font-bold text-white">
                    {analysis.candidateName || 'Candidate'}&apos;s ATS Compatibility
                  </h3>
                  <p className="text-xs text-slate-300 mt-1 max-w-md">
                    Target Role: <strong className="text-purple-300">{analysis.targetRole || 'Software Engineer'}</strong>
                  </p>
                </div>
              </div>

              {/* Quick Actions */}
              <div className="flex flex-col sm:flex-row gap-3 w-full md:w-auto">
                <button
                  onClick={onGoToJobMatch}
                  className="px-4 py-2.5 rounded-xl text-xs font-bold bg-white/10 hover:bg-white/20 text-white border border-white/20 transition-all flex items-center justify-center gap-2"
                >
                  <Briefcase className="w-4 h-4 text-purple-300" />
                  <span>Match Against Job Description</span>
                </button>
                <button
                  onClick={onGoToAi}
                  className="px-4 py-2.5 rounded-xl text-xs font-bold bg-brand-purple hover:bg-purple-600 text-white shadow-lg shadow-purple-600/30 transition-all flex items-center justify-center gap-2"
                >
                  <Bot className="w-4 h-4" />
                  <span>Get AI Suggestions</span>
                </button>
              </div>
            </div>
          </div>

          {/* Missing Sections Alert */}
          {analysis.missingSections && analysis.missingSections.length > 0 && (
            <div className="p-4 rounded-2xl bg-amber-50 border border-amber-200 text-amber-900 flex items-start gap-3">
              <AlertTriangle className="w-5 h-5 text-amber-600 shrink-0 mt-0.5" />
              <div>
                <h4 className="text-xs font-bold">Missing Standard Sections Detected</h4>
                <p className="text-xs text-amber-800 mt-0.5">
                  Your resume is missing standard headers: <strong className="underline">{analysis.missingSections.join(', ')}</strong>. Adding these sections will significantly improve ATS parsing accuracy.
                </p>
              </div>
            </div>
          )}

          {/* 7 Category Progress Bars Grid */}
          <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm">
            <h3 className="text-sm font-bold text-navy-950 uppercase tracking-wider mb-4">
              Category Score Breakdown (7 Pillars)
            </h3>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {analysis.categoryScores?.map((cat, idx) => (
                <div key={idx} className="p-3.5 rounded-xl bg-slate-50 border border-slate-200">
                  <div className="flex justify-between items-center mb-1 text-xs">
                    <span className="font-bold text-slate-800">{cat.name}</span>
                    <span className="font-extrabold text-brand-purple">
                      {cat.score} <span className="text-slate-400 font-normal">/ {cat.maxScore} pts</span>
                    </span>
                  </div>

                  {/* Progress Track */}
                  <div className="w-full h-2 rounded-full bg-slate-200 overflow-hidden mb-2">
                    <div
                      className={`h-full rounded-full transition-all duration-500 ${
                        cat.percentage >= 80 ? 'bg-emerald-500' : cat.percentage >= 60 ? 'bg-indigo-500' : 'bg-amber-500'
                      }`}
                      style={{ width: `${cat.percentage}%` }}
                    />
                  </div>

                  <p className="text-[11px] text-slate-500 leading-tight">{cat.feedback}</p>
                </div>
              ))}
            </div>
          </div>

          {/* Strengths & Weaknesses 2-Column Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Strengths */}
            <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm">
              <div className="flex items-center gap-2 mb-4">
                <CheckCircle2 className="w-5 h-5 text-emerald-500" />
                <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wider">Identified Strengths</h3>
              </div>
              <ul className="space-y-2.5">
                {analysis.strengths?.map((str, idx) => (
                  <li key={idx} className="text-xs text-slate-700 flex items-start gap-2">
                    <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 mt-1.5 shrink-0" />
                    <span>{str}</span>
                  </li>
                ))}
              </ul>
            </div>

            {/* Weaknesses / Opportunities */}
            <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm">
              <div className="flex items-center gap-2 mb-4">
                <AlertTriangle className="w-5 h-5 text-amber-500" />
                <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wider">Improvement Areas</h3>
              </div>
              <ul className="space-y-2.5">
                {analysis.weaknesses?.map((weak, idx) => (
                  <li key={idx} className="text-xs text-slate-700 flex items-start gap-2">
                    <span className="w-1.5 h-1.5 rounded-full bg-amber-500 mt-1.5 shrink-0" />
                    <span>{weak}</span>
                  </li>
                ))}
              </ul>
            </div>
          </div>

          {/* Actionable Suggestions */}
          <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm">
            <h3 className="text-sm font-bold text-navy-950 uppercase tracking-wider mb-3">
              Actionable Optimization Steps
            </h3>
            <div className="space-y-2.5">
              {analysis.actionableSuggestions?.map((sug, idx) => (
                <div key={idx} className="p-3 rounded-xl bg-purple-50/50 border border-purple-100 flex items-start gap-3">
                  <span className="w-5 h-5 rounded-full bg-brand-purple text-white text-[10px] font-bold flex items-center justify-center shrink-0 mt-0.5">
                    {idx + 1}
                  </span>
                  <p className="text-xs text-purple-950 leading-relaxed">{sug}</p>
                </div>
              ))}
            </div>
          </div>

          {/* Legal / Explanatory Disclaimer */}
          <p className="text-[11px] text-slate-400 text-center italic">
            {analysis.disclaimer}
          </p>
        </div>
      ) : null}
    </div>
  );
}
