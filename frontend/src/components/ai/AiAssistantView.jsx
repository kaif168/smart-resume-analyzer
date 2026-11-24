import React, { useState, useEffect } from 'react';
import { api } from '../../api/apiClient';
import { 
  Bot, Copy, Check, Sparkles, Wand2, ShieldCheck, 
  ArrowRight, FileText, CheckCircle2, AlertCircle 
} from 'lucide-react';

export default function AiAssistantView({ resumeId, resumes, onSelectResume }) {
  const [activeTab, setActiveTab] = useState('SUMMARY'); // 'SUMMARY', 'PROJECT_ANALYZER', 'ACTION_VERBS', 'WEAK_SECTIONS'
  const [targetRole, setTargetRole] = useState('Java Developer');
  const [aiResult, setAiResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [copiedKey, setCopiedKey] = useState(null);

  // Project Analyzer State
  const [projectTitle, setProjectTitle] = useState('Smart Inventory Microservices');
  const [projectDesc, setProjectDesc] = useState('Worked on backend APIs using Java and MySQL. Helped the team fix bugs and write queries.');
  const [projectFeedback, setProjectFeedback] = useState(null);
  const [projectLoading, setProjectLoading] = useState(false);

  useEffect(() => {
    if (resumeId) {
      loadAiSuggestions();
    }
  }, [resumeId]);

  const loadAiSuggestions = async () => {
    if (!resumeId) return;
    setLoading(true);
    try {
      const data = await api.getAiImprovements({
        resumeId,
        targetRole,
        sectionType: activeTab,
      });
      setAiResult(data);
    } catch (e) {
      // Fallback handled by backend
    } finally {
      setLoading(false);
    }
  };

  const handleCopy = (text, key) => {
    navigator.clipboard.writeText(text);
    setCopiedKey(key);
    setTimeout(() => setCopiedKey(null), 2000);
  };

  const handleAnalyzeProject = async (e) => {
    e.preventDefault();
    setProjectLoading(true);
    try {
      const feedback = await api.analyzeProject({
        title: projectTitle,
        description: projectDesc,
      });
      setProjectFeedback(feedback);
    } catch (e) {
      // Error handling
    } finally {
      setProjectLoading(false);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Title Bar */}
      <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl gradient-brand text-white flex items-center justify-center">
            <Bot className="w-5 h-5" />
          </div>
          <div>
            <h2 className="text-lg font-bold text-slate-900">AI Resume Improvement Assistant</h2>
            <p className="text-xs text-slate-500">
              Powered by {aiResult?.engineUsed || 'LLM API & Intelligent Career Engine'}
            </p>
          </div>
        </div>

        <div className="flex items-center gap-3">
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
            onClick={loadAiSuggestions}
            disabled={loading}
            className="flex items-center gap-1.5 px-3.5 py-1.5 text-xs font-bold rounded-lg bg-brand-purple hover:bg-purple-600 text-white shadow-sm transition-all disabled:opacity-50"
          >
            <Wand2 className="w-3.5 h-3.5" />
            <span>Regenerate Suggestions</span>
          </button>
        </div>
      </div>

      {/* Ethical Transparency Banner */}
      <div className="p-3.5 rounded-xl bg-purple-50/70 border border-purple-200 text-purple-900 text-xs flex items-center justify-between">
        <div className="flex items-center gap-2">
          <ShieldCheck className="w-4 h-4 text-brand-purple shrink-0" />
          <span>{aiResult?.ethicalNotice || 'All recommendations preserve candidate background truthfulness without fabricating experience.'}</span>
        </div>
        <span className="text-[10px] uppercase font-bold text-brand-purple tracking-wider hidden sm:inline">
          Ethical AI Verified
        </span>
      </div>

      {/* Tabs */}
      <div className="flex border-b border-slate-200 gap-6 text-xs font-bold uppercase tracking-wider text-slate-500 overflow-x-auto">
        <button
          onClick={() => setActiveTab('SUMMARY')}
          className={`pb-3 transition-colors ${
            activeTab === 'SUMMARY'
              ? 'border-b-2 border-brand-purple text-brand-purple'
              : 'hover:text-slate-800'
          }`}
        >
          Professional Summary
        </button>
        <button
          onClick={() => setActiveTab('PROJECT_ANALYZER')}
          className={`pb-3 transition-colors ${
            activeTab === 'PROJECT_ANALYZER'
              ? 'border-b-2 border-brand-purple text-brand-purple'
              : 'hover:text-slate-800'
          }`}
        >
          STAR Project Analyzer
        </button>
        <button
          onClick={() => setActiveTab('ACTION_VERBS')}
          className={`pb-3 transition-colors ${
            activeTab === 'ACTION_VERBS'
              ? 'border-b-2 border-brand-purple text-brand-purple'
              : 'hover:text-slate-800'
          }`}
        >
          Power Action Verbs
        </button>
        <button
          onClick={() => setActiveTab('WEAK_SECTIONS')}
          className={`pb-3 transition-colors ${
            activeTab === 'WEAK_SECTIONS'
              ? 'border-b-2 border-brand-purple text-brand-purple'
              : 'hover:text-slate-800'
          }`}
        >
          Section Fixes &amp; Grammar
        </button>
      </div>

      {/* Tab 1: Professional Summary */}
      {activeTab === 'SUMMARY' && (
        <div className="space-y-6">
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
            <div className="flex justify-between items-center">
              <div>
                <h3 className="text-sm font-bold text-slate-900">Recommended Professional Summary</h3>
                <p className="text-xs text-slate-500">Tailored to your verified skills, experience level, and role</p>
              </div>

              {aiResult?.suggestedSummary && (
                <button
                  onClick={() => handleCopy(aiResult.suggestedSummary, 'summary')}
                  className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold rounded-lg bg-purple-50 text-brand-purple hover:bg-purple-100 transition-colors"
                >
                  {copiedKey === 'summary' ? (
                    <>
                      <Check className="w-3.5 h-3.5 text-emerald-600" />
                      <span className="text-emerald-700">Copied!</span>
                    </>
                  ) : (
                    <>
                      <Copy className="w-3.5 h-3.5" />
                      <span>Copy Summary</span>
                    </>
                  )}
                </button>
              )}
            </div>

            <div className="p-4 rounded-xl bg-purple-50/40 border border-purple-200/80 text-xs text-slate-800 leading-relaxed font-medium">
              {aiResult?.suggestedSummary || 'Generating tailored professional summary...'}
            </div>
          </div>

          {/* High-Impact Bullet Points */}
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-3">
            <h3 className="text-sm font-bold text-slate-900">Improved STAR-Method Bullet Points</h3>
            <p className="text-xs text-slate-500">Quantifiable impact statements ready to copy into your experience section:</p>

            <div className="space-y-2.5 pt-2">
              {aiResult?.improvedBulletPoints?.map((bullet, idx) => (
                <div
                  key={idx}
                  className="p-3 rounded-xl bg-slate-50 border border-slate-200 flex items-start justify-between gap-3 text-xs text-slate-700"
                >
                  <div className="flex items-start gap-2">
                    <span className="w-1.5 h-1.5 rounded-full bg-brand-purple mt-1.5 shrink-0" />
                    <span>{bullet}</span>
                  </div>
                  <button
                    onClick={() => handleCopy(bullet, `bullet-${idx}`)}
                    className="p-1 rounded text-slate-400 hover:text-brand-purple shrink-0"
                    title="Copy bullet"
                  >
                    {copiedKey === `bullet-${idx}` ? (
                      <Check className="w-3.5 h-3.5 text-emerald-600" />
                    ) : (
                      <Copy className="w-3.5 h-3.5" />
                    )}
                  </button>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Tab 2: Project Description Analyzer */}
      {activeTab === 'PROJECT_ANALYZER' && (
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
          <div className="lg:col-span-6 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
            <h3 className="text-sm font-bold text-navy-950 uppercase tracking-wider">
              Project Description Evaluator
            </h3>
            <p className="text-xs text-slate-500">
              Input a project title and description to benchmark its STAR alignment and quantifiable metric density.
            </p>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Project Name</label>
              <input
                type="text"
                value={projectTitle}
                onChange={(e) => setProjectTitle(e.target.value)}
                className="w-full px-3 py-2 text-xs rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-brand-purple"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Project Description / Bullets</label>
              <textarea
                rows={5}
                value={projectDesc}
                onChange={(e) => setProjectDesc(e.target.value)}
                className="w-full p-3 text-xs rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-brand-purple leading-relaxed"
              />
            </div>

            <button
              onClick={handleAnalyzeProject}
              disabled={projectLoading}
              className="w-full py-2.5 rounded-xl text-xs font-bold bg-brand-purple hover:bg-purple-600 text-white shadow-md transition-all flex items-center justify-center gap-2 disabled:opacity-50"
            >
              {projectLoading ? 'Evaluating STAR Patterns...' : 'Evaluate Project Description'}
            </button>
          </div>

          <div className="lg:col-span-6">
            {projectFeedback ? (
              <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
                <div className="flex justify-between items-center">
                  <div>
                    <span className="text-[10px] font-bold uppercase tracking-wider text-purple-600">
                      STAR Structure Rating
                    </span>
                    <h4 className="text-base font-bold text-slate-900">{projectFeedback.starStructureRating}</h4>
                  </div>
                  <div className="w-14 h-14 rounded-full bg-purple-50 border-2 border-brand-purple flex items-center justify-center font-extrabold text-sm text-brand-purple">
                    {projectFeedback.score}/100
                  </div>
                </div>

                <div>
                  <h5 className="text-xs font-bold text-slate-700 uppercase mb-2">Enhancement Suggestions</h5>
                  <ul className="space-y-1.5">
                    {projectFeedback.suggestions?.map((sug, idx) => (
                      <li key={idx} className="text-xs text-slate-600 flex items-start gap-2">
                        <span className="w-1.5 h-1.5 rounded-full bg-amber-500 mt-1.5 shrink-0" />
                        <span>{sug}</span>
                      </li>
                    ))}
                  </ul>
                </div>

                <div className="pt-2">
                  <div className="flex justify-between items-center mb-1">
                    <h5 className="text-xs font-bold text-brand-purple uppercase">Drop-in Improved Version</h5>
                    <button
                      onClick={() => handleCopy(projectFeedback.improvedVersion, 'improved-project')}
                      className="text-xs text-brand-purple font-semibold hover:underline flex items-center gap-1"
                    >
                      {copiedKey === 'improved-project' ? 'Copied!' : 'Copy Enhanced Text'}
                    </button>
                  </div>
                  <div className="p-3.5 rounded-xl bg-purple-50/60 border border-purple-200 text-xs text-slate-800 leading-relaxed font-medium">
                    {projectFeedback.improvedVersion}
                  </div>
                </div>
              </div>
            ) : (
              <div className="bg-slate-50 p-10 rounded-2xl border border-dashed border-slate-300 text-center h-full flex flex-col items-center justify-center text-xs text-slate-500">
                Click &quot;Evaluate Project Description&quot; on the left to receive instant STAR-method grading and an enhanced replacement.
              </div>
            )}
          </div>
        </div>
      )}

      {/* Tab 3: Action Verbs */}
      {activeTab === 'ACTION_VERBS' && (
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
          <h3 className="text-sm font-bold text-slate-900">Power Action Verbs Dictionary</h3>
          <p className="text-xs text-slate-500">
            Replace passive phrases like &quot;worked on&quot; or &quot;assisted in&quot; with authoritative verbs that trigger ATS scoring algorithms:
          </p>

          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-3 pt-2">
            {aiResult?.powerActionVerbs?.map((verb, idx) => (
              <div key={idx} className="p-3 rounded-xl bg-purple-50/40 border border-purple-100 text-xs">
                <span className="font-bold text-brand-purple block mb-0.5">{verb.split('(')[0]}</span>
                <span className="text-slate-500 text-[11px]">{verb.includes('(') ? '(' + verb.split('(')[1] : ''}</span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Tab 4: Weak Section Fixes */}
      {activeTab === 'WEAK_SECTIONS' && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-3">
            <h3 className="text-sm font-bold text-slate-900">Diagnostic Weak Section Fixes</h3>
            <ul className="space-y-2.5 pt-2">
              {aiResult?.weakSectionFixes?.map((fix, idx) => (
                <li key={idx} className="p-3 rounded-xl bg-amber-50/60 border border-amber-200 text-xs text-amber-900 leading-relaxed">
                  {fix}
                </li>
              ))}
            </ul>
          </div>

          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-3">
            <h3 className="text-sm font-bold text-slate-900">Grammar &amp; Formatting Guidelines</h3>
            <ul className="space-y-2.5 pt-2">
              {aiResult?.grammarAndStyleTips?.map((tip, idx) => (
                <li key={idx} className="p-3 rounded-xl bg-slate-50 border border-slate-200 text-xs text-slate-700 leading-relaxed">
                  ✓ {tip}
                </li>
              ))}
            </ul>
          </div>
        </div>
      )}
    </div>
  );
}
