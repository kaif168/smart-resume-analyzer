import React, { useState, useEffect } from 'react';
import { api } from '../../api/apiClient';
import { GitCompare, ArrowRight, TrendingUp, CheckCircle2, AlertTriangle, Layers } from 'lucide-react';

export default function VersionCompareView({ resumes }) {
  const [oldResumeId, setOldResumeId] = useState('');
  const [newResumeId, setNewResumeId] = useState('');
  const [comparison, setComparison] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (resumes && resumes.length >= 2) {
      setOldResumeId(resumes[resumes.length - 1].id);
      setNewResumeId(resumes[0].id);
    } else if (resumes && resumes.length === 1) {
      setOldResumeId(resumes[0].id);
      setNewResumeId(resumes[0].id);
    }
  }, [resumes]);

  const handleCompare = async () => {
    if (!oldResumeId || !newResumeId) {
      setError('Please select both an older and newer resume version.');
      return;
    }

    setLoading(true);
    setError('');
    try {
      const data = await api.compareVersions(oldResumeId, newResumeId);
      setComparison(data);
    } catch (err) {
      setError(err.message || 'Failed to compare resume versions');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Title Bar */}
      <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl gradient-brand text-white flex items-center justify-center">
            <GitCompare className="w-5 h-5" />
          </div>
          <div>
            <h2 className="text-lg font-bold text-slate-900">Resume Version Comparison</h2>
            <p className="text-xs text-slate-500">Track score improvements and keyword additions across your drafts</p>
          </div>
        </div>
      </div>

      {error && (
        <div className="p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-xs">
          {error}
        </div>
      )}

      {/* Version Selector Card */}
      <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Base / Older Version
            </label>
            <select
              value={oldResumeId}
              onChange={(e) => setOldResumeId(e.target.value)}
              className="w-full px-3 py-2 text-xs font-medium rounded-lg border border-slate-300 bg-slate-50"
            >
              <option value="">Select Base Draft</option>
              {resumes?.map((r) => (
                <option key={r.id} value={r.id}>
                  {r.originalFileName} ({r.versionTag}) - Score: {r.latestScore || 'Pending'}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              New / Optimized Version
            </label>
            <select
              value={newResumeId}
              onChange={(e) => setNewResumeId(e.target.value)}
              className="w-full px-3 py-2 text-xs font-medium rounded-lg border border-slate-300 bg-slate-50"
            >
              <option value="">Select New Draft</option>
              {resumes?.map((r) => (
                <option key={r.id} value={r.id}>
                  {r.originalFileName} ({r.versionTag}) - Score: {r.latestScore || 'Pending'}
                </option>
              ))}
            </select>
          </div>
        </div>

        <button
          onClick={handleCompare}
          disabled={loading || !oldResumeId || !newResumeId}
          className="w-full py-2.5 rounded-xl text-xs font-bold bg-brand-purple hover:bg-purple-600 text-white shadow-md transition-all flex items-center justify-center gap-2 disabled:opacity-50"
        >
          {loading ? 'Analyzing Differences...' : 'Compare Resume Versions'}
        </button>
      </div>

      {/* Comparison Results */}
      {comparison && (
        <div className="space-y-6">
          {/* Delta Banner */}
          <div className="bg-gradient-to-r from-navy-900 to-indigo-950 p-6 rounded-2xl text-white shadow-lg flex flex-col sm:flex-row items-center justify-between gap-6">
            <div>
              <span className="text-[10px] font-bold uppercase tracking-wider text-purple-300">
                Performance Delta
              </span>
              <h3 className="text-xl font-bold mt-0.5">
                {comparison.oldVersionTag} vs {comparison.newVersionTag}
              </h3>
              <p className="text-xs text-slate-300 mt-1 max-w-lg">{comparison.summaryComparison}</p>
            </div>

            <div className="flex items-center gap-4">
              <div className="text-center p-3 rounded-xl bg-slate-800/80 border border-slate-700">
                <span className="text-[10px] uppercase text-slate-400 block font-semibold">Older Draft</span>
                <span className="text-xl font-bold text-slate-300">{comparison.oldOverallScore}</span>
              </div>

              <ArrowRight className="w-5 h-5 text-purple-400 shrink-0" />

              <div className="text-center p-3 rounded-xl bg-purple-900/60 border border-purple-500/40">
                <span className="text-[10px] uppercase text-purple-300 block font-semibold">New Draft</span>
                <span className="text-xl font-bold text-white">{comparison.newOverallScore}</span>
              </div>

              <div className={`text-center p-3 rounded-xl font-bold text-sm ${
                comparison.scoreDifference >= 0 ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30' : 'bg-rose-500/20 text-rose-400'
              }`}>
                <span>{comparison.scoreDifference >= 0 ? '+' : ''}{comparison.scoreDifference} pts</span>
              </div>
            </div>
          </div>

          {/* Newly Added Skills */}
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-3">
            <h4 className="text-xs font-bold text-navy-950 uppercase tracking-wider flex items-center gap-1.5">
              <CheckCircle2 className="w-4 h-4 text-emerald-500" />
              <span>Newly Incorporated Skills ({comparison.newlyAddedSkills?.length || 0})</span>
            </h4>
            <div className="flex flex-wrap gap-1.5 pt-1">
              {comparison.newlyAddedSkills?.map((skill, idx) => (
                <span key={idx} className="px-2.5 py-1 rounded-lg text-xs font-semibold bg-emerald-50 text-emerald-800 border border-emerald-200">
                  + {skill}
                </span>
              ))}
              {(!comparison.newlyAddedSkills || comparison.newlyAddedSkills.length === 0) && (
                <p className="text-xs text-slate-400">No new skills added between these versions.</p>
              )}
            </div>
          </div>

          {/* Resolved vs Remaining Gaps */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-3">
              <h4 className="text-xs font-bold text-slate-900 uppercase tracking-wider text-emerald-700">
                Resolved Weaknesses
              </h4>
              <ul className="space-y-2">
                {comparison.resolvedWeaknesses?.map((res, idx) => (
                  <li key={idx} className="text-xs text-slate-700 flex items-start gap-2">
                    <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 mt-1.5 shrink-0" />
                    <span>{res}</span>
                  </li>
                ))}
              </ul>
            </div>

            <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-3">
              <h4 className="text-xs font-bold text-slate-900 uppercase tracking-wider text-amber-700">
                Remaining Opportunities
              </h4>
              <ul className="space-y-2">
                {comparison.remainingGaps?.map((gap, idx) => (
                  <li key={idx} className="text-xs text-slate-700 flex items-start gap-2">
                    <span className="w-1.5 h-1.5 rounded-full bg-amber-500 mt-1.5 shrink-0" />
                    <span>{gap}</span>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
