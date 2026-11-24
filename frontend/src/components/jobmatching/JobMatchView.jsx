import React, { useState, useEffect } from 'react';
import { api } from '../../api/apiClient';
import { Briefcase, CheckCircle2, XCircle, Sparkles, ArrowRight, BookOpen, AlertCircle } from 'lucide-react';

export default function JobMatchView({ resumeId, resumes, onSelectResume }) {
  const [templates, setTemplates] = useState([]);
  const [selectedTemplateId, setSelectedTemplateId] = useState('');
  const [targetTitle, setTargetTitle] = useState('Senior Java Developer');
  const [jdText, setJdText] = useState('');
  const [matchResult, setMatchResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    loadTemplates();
  }, []);

  const loadTemplates = async () => {
    try {
      const data = await api.getJobTemplates();
      setTemplates(data || []);
      if (data && data.length > 0) {
        setSelectedTemplateId(data[0].id);
        setTargetTitle(data[0].title);
        setJdText(data[0].descriptionText);
      }
    } catch (e) {
      // Fallback
    }
  };

  const handleTemplateChange = (templateId) => {
    setSelectedTemplateId(templateId);
    const tmpl = templates.find((t) => String(t.id) === String(templateId));
    if (tmpl) {
      setTargetTitle(tmpl.title);
      setJdText(tmpl.descriptionText);
    }
  };

  const handleRunMatch = async (e) => {
    e.preventDefault();
    if (!resumeId) {
      setError('Please select a resume to analyze.');
      return;
    }
    if (!jdText.trim()) {
      setError('Please provide a job description.');
      return;
    }

    setLoading(true);
    setError('');
    try {
      const result = await api.matchJob({
        resumeId,
        jobTemplateId: selectedTemplateId ? Number(selectedTemplateId) : null,
        targetJobTitle: targetTitle,
        jobDescriptionText: jdText,
      });
      setMatchResult(result);
    } catch (err) {
      setError(err.message || 'Failed to match against job description');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Title Card */}
      <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl gradient-brand text-white flex items-center justify-center">
            <Briefcase className="w-5 h-5" />
          </div>
          <div>
            <h2 className="text-lg font-bold text-slate-900">Job Description Matching Engine</h2>
            <p className="text-xs text-slate-500">Compare your resume against any role vacancy or industry template</p>
          </div>
        </div>

        {resumes && resumes.length > 1 && (
          <div className="flex items-center gap-2">
            <span className="text-xs font-semibold text-slate-600">Active Resume:</span>
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
          </div>
        )}
      </div>

      {error && (
        <div className="p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-xs">
          {error}
        </div>
      )}

      {/* Input & Form Split Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Left Form: Job Description Input */}
        <div className="lg:col-span-5 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
          <h3 className="text-sm font-bold text-navy-950 uppercase tracking-wider">
            Job Target Specifications
          </h3>

          {/* Quick Industry Templates */}
          {templates.length > 0 && (
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">
                Choose Standard Template (or Paste Custom)
              </label>
              <select
                value={selectedTemplateId}
                onChange={(e) => handleTemplateChange(e.target.value)}
                className="w-full px-3 py-2 text-xs font-medium rounded-lg border border-slate-300 bg-slate-50 focus:outline-none focus:ring-2 focus:ring-brand-purple"
              >
                {templates.map((t) => (
                  <option key={t.id} value={t.id}>
                    {t.title} ({t.company})
                  </option>
                ))}
              </select>
            </div>
          )}

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Target Job Title</label>
            <input
              type="text"
              value={targetTitle}
              onChange={(e) => setTargetTitle(e.target.value)}
              placeholder="e.g. Senior Java Developer"
              className="w-full px-3 py-2 text-xs rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-brand-purple"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Job Description / Required Skills
            </label>
            <textarea
              rows={8}
              value={jdText}
              onChange={(e) => setJdText(e.target.value)}
              placeholder="Paste job description requirements, responsibilities, and qualifications..."
              className="w-full p-3 text-xs font-mono rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-brand-purple leading-relaxed"
            />
          </div>

          <button
            onClick={handleRunMatch}
            disabled={loading}
            className="w-full py-2.5 rounded-xl text-xs font-bold bg-brand-purple hover:bg-purple-600 text-white shadow-md shadow-purple-600/30 transition-all flex items-center justify-center gap-2 disabled:opacity-50"
          >
            {loading ? (
              <span>Comparing Resume Against JD...</span>
            ) : (
              <>
                <span>Analyze Job Compatibility</span>
                <ArrowRight className="w-4 h-4" />
              </>
            )}
          </button>
        </div>

        {/* Right Panel: Match Results */}
        <div className="lg:col-span-7">
          {matchResult ? (
            <div className="space-y-6">
              {/* Match Header Gauge */}
              <div className="p-6 rounded-2xl bg-gradient-to-r from-navy-900 to-indigo-950 text-white shadow-lg flex items-center justify-between">
                <div>
                  <span className="text-[10px] font-bold uppercase tracking-wider text-purple-300 block">
                    Match Verdict
                  </span>
                  <h3 className="text-xl font-bold text-white mt-0.5">{matchResult.matchVerdict}</h3>
                  <p className="text-xs text-slate-300 mt-1">{matchResult.targetRole}</p>
                </div>

                <div className="w-24 h-24 rounded-full bg-slate-800/80 border-4 border-purple-500/40 flex items-center justify-center shrink-0">
                  <div className="text-center">
                    <span className="text-2xl font-extrabold text-white">{matchResult.matchPercentage}%</span>
                    <span className="text-[9px] text-purple-300 block">Alignment</span>
                  </div>
                </div>
              </div>

              {/* Skills Grid: Matched vs Missing */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {/* Matched Skills */}
                <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
                  <div className="flex items-center gap-2 mb-3">
                    <CheckCircle2 className="w-4 h-4 text-emerald-500" />
                    <h4 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
                      Matching Skills ({matchResult.matchedSkills?.length || 0})
                    </h4>
                  </div>
                  <div className="flex flex-wrap gap-1.5">
                    {matchResult.matchedSkills?.map((skill, idx) => (
                      <span
                        key={idx}
                        className="px-2.5 py-1 rounded-lg text-xs font-semibold bg-emerald-50 text-emerald-800 border border-emerald-200"
                      >
                        ✓ {skill}
                      </span>
                    ))}
                    {(!matchResult.matchedSkills || matchResult.matchedSkills.length === 0) && (
                      <p className="text-xs text-slate-400">No direct skill matches detected.</p>
                    )}
                  </div>
                </div>

                {/* Missing Skills */}
                <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
                  <div className="flex items-center gap-2 mb-3">
                    <XCircle className="w-4 h-4 text-rose-500" />
                    <h4 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
                      Missing Skills ({matchResult.missingSkills?.length || 0})
                    </h4>
                  </div>
                  <div className="flex flex-wrap gap-1.5">
                    {matchResult.missingSkills?.map((skill, idx) => (
                      <span
                        key={idx}
                        className="px-2.5 py-1 rounded-lg text-xs font-semibold bg-rose-50 text-rose-800 border border-rose-200"
                      >
                        + {skill}
                      </span>
                    ))}
                    {(!matchResult.missingSkills || matchResult.missingSkills.length === 0) && (
                      <p className="text-xs text-emerald-600 font-medium">Full skill overlap achieved!</p>
                    )}
                  </div>
                </div>
              </div>

              {/* Experience Alignment */}
              <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
                <h4 className="text-xs font-bold text-slate-800 uppercase tracking-wider mb-2">
                  Experience Level Alignment
                </h4>
                <p className="text-xs text-slate-700 leading-relaxed font-medium">
                  {matchResult.experienceAlignment}
                </p>
              </div>

              {/* Suggested Tailoring Improvements */}
              <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
                <h4 className="text-xs font-bold text-navy-950 uppercase tracking-wider mb-3">
                  Recommended Tailoring Steps
                </h4>
                <ul className="space-y-2">
                  {matchResult.suggestedImprovements?.map((tip, idx) => (
                    <li key={idx} className="text-xs text-slate-700 flex items-start gap-2">
                      <span className="w-1.5 h-1.5 rounded-full bg-brand-purple mt-1.5 shrink-0" />
                      <span>{tip}</span>
                    </li>
                  ))}
                </ul>
              </div>

              {/* Rule reminder */}
              <div className="p-3.5 rounded-xl bg-purple-50/70 border border-purple-200 text-purple-950 text-xs flex items-center gap-2">
                <AlertCircle className="w-4 h-4 text-brand-purple shrink-0" />
                <span>{matchResult.note}</span>
              </div>
            </div>
          ) : (
            <div className="bg-white p-12 rounded-2xl border border-slate-200 shadow-sm text-center h-full flex flex-col items-center justify-center">
              <div className="w-14 h-14 rounded-2xl bg-slate-100 text-slate-400 flex items-center justify-center mb-3">
                <Briefcase className="w-7 h-7" />
              </div>
              <h4 className="text-base font-bold text-slate-800">No Job Comparison Executed Yet</h4>
              <p className="text-xs text-slate-500 mt-1 max-w-sm">
                Select an industry template or paste your target job description on the left to see matched vs missing skills and alignment percentage.
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
