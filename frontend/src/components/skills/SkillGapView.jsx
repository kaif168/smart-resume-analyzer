import React, { useState, useEffect } from 'react';
import { api } from '../../api/apiClient';
import { Sparkles, CheckCircle2, AlertTriangle, TrendingUp, BookOpen, Layers, ArrowRight } from 'lucide-react';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { Doughnut } from 'react-chartjs-2';

ChartJS.register(ArcElement, Tooltip, Legend);

export default function SkillGapView({ resumeId, resumes, onSelectResume }) {
  const [targetRole, setTargetRole] = useState('Java Developer');
  const [skillGap, setSkillGap] = useState(null);
  const [careerRecommendations, setCareerRecommendations] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (resumeId) {
      loadData(resumeId, targetRole);
    }
  }, [resumeId, targetRole]);

  const loadData = async (resId, role) => {
    setLoading(true);
    try {
      const [gap, careers] = await Promise.all([
        api.getSkillGap(resId, role),
        api.getCareerRecommendations(resId)
      ]);
      setSkillGap(gap);
      setCareerRecommendations(careers || []);
    } catch (e) {
      // Error
    } finally {
      setLoading(false);
    }
  };

  const chartData = {
    labels: ['Existing Skills', 'Skill Gaps'],
    datasets: [
      {
        data: [skillGap?.coveragePercentage || 50, 100 - (skillGap?.coveragePercentage || 50)],
        backgroundColor: ['#7C3AED', '#E2E8F0'],
        borderWidth: 0,
      },
    ],
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Header Bar */}
      <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl gradient-brand text-white flex items-center justify-center">
            <Sparkles className="w-5 h-5" />
          </div>
          <div>
            <h2 className="text-lg font-bold text-slate-900">Skill Gap Analysis &amp; Career Recommendations</h2>
            <p className="text-xs text-slate-500">Benchmark your current skill inventory against standard industry roles</p>
          </div>
        </div>

        <div className="flex items-center gap-3">
          {resumes && resumes.length > 1 && (
            <select
              value={resumeId}
              onChange={(e) => onSelectResume(Number(e.target.value))}
              className="px-3 py-1.5 text-xs font-semibold rounded-lg border border-slate-300 bg-slate-50"
            >
              {resumes.map((r) => (
                <option key={r.id} value={r.id}>
                  {r.originalFileName} ({r.versionTag})
                </option>
              ))}
            </select>
          )}

          <select
            value={targetRole}
            onChange={(e) => setTargetRole(e.target.value)}
            className="px-3 py-1.5 text-xs font-bold rounded-lg border border-purple-300 bg-purple-50 text-brand-purple"
          >
            <option value="Java Developer">Target: Java Developer</option>
            <option value="Full Stack Developer">Target: Full Stack Developer</option>
            <option value="Frontend Developer">Target: Frontend Developer</option>
            <option value="Backend Developer">Target: Backend Developer</option>
            <option value="Python & Data Analyst">Target: Python &amp; Data Analyst</option>
            <option value="Software Engineer">Target: Software Engineer</option>
          </select>
        </div>
      </div>

      {/* Main 2-Column Section */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Left Column: Visual Coverage & Skills */}
        <div className="lg:col-span-5 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-6">
          <h3 className="text-sm font-bold text-navy-950 uppercase tracking-wider">
            Current Skill Coverage
          </h3>

          {/* Doughnut Chart */}
          <div className="relative w-44 h-44 mx-auto flex items-center justify-center">
            <Doughnut data={chartData} options={{ cutout: '78%', plugins: { legend: { display: false } } }} />
            <div className="absolute inset-0 flex flex-col items-center justify-center">
              <span className="text-2xl font-extrabold text-navy-900">{skillGap?.coveragePercentage || 0}%</span>
              <span className="text-[10px] text-slate-400 font-semibold uppercase">Covered</span>
            </div>
          </div>

          {/* Existing Skills */}
          <div>
            <h4 className="text-xs font-bold text-slate-800 uppercase tracking-wider mb-2 flex items-center gap-1.5">
              <CheckCircle2 className="w-3.5 h-3.5 text-emerald-500" />
              <span>Verified Existing Skills ({skillGap?.existingSkills?.length || 0})</span>
            </h4>
            <div className="flex flex-wrap gap-1.5">
              {skillGap?.existingSkills?.map((s, idx) => (
                <span
                  key={idx}
                  className="px-2.5 py-1 rounded-lg text-xs font-semibold bg-emerald-50 text-emerald-800 border border-emerald-200"
                >
                  {s}
                </span>
              ))}
            </div>
          </div>

          {/* Missing Skills */}
          <div>
            <h4 className="text-xs font-bold text-slate-800 uppercase tracking-wider mb-2 flex items-center gap-1.5">
              <AlertTriangle className="w-3.5 h-3.5 text-amber-500" />
              <span>Recommended Skills to Learn ({skillGap?.missingSkills?.length || 0})</span>
            </h4>
            <div className="flex flex-wrap gap-1.5">
              {skillGap?.missingSkills?.map((s, idx) => (
                <span
                  key={idx}
                  className="px-2.5 py-1 rounded-lg text-xs font-semibold bg-amber-50 text-amber-900 border border-amber-200"
                >
                  + {s}
                </span>
              ))}
              {(!skillGap?.missingSkills || skillGap.missingSkills.length === 0) && (
                <p className="text-xs text-emerald-600 font-medium">100% benchmark coverage achieved!</p>
              )}
            </div>
          </div>
        </div>

        {/* Right Column: Suggested Learning Sequence Roadmap */}
        <div className="lg:col-span-7 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-6">
          <div className="flex items-center gap-2">
            <BookOpen className="w-5 h-5 text-brand-purple" />
            <h3 className="text-sm font-bold text-navy-950 uppercase tracking-wider">
              Suggested Learning Sequence Roadmap
            </h3>
          </div>

          <div className="space-y-3">
            {skillGap?.suggestedLearningSequence?.map((step, idx) => (
              <div
                key={idx}
                className="p-4 rounded-xl bg-gradient-to-r from-purple-50/50 to-slate-50 border border-purple-100 flex items-start gap-3"
              >
                <div className="w-6 h-6 rounded-full bg-brand-purple text-white text-xs font-bold flex items-center justify-center shrink-0 mt-0.5 shadow-sm">
                  {idx + 1}
                </div>
                <div>
                  <p className="text-xs font-bold text-slate-800 leading-relaxed">{step}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Career Role Recommendations */}
      <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
        <div className="flex items-center gap-2">
          <TrendingUp className="w-5 h-5 text-brand-purple" />
          <h3 className="text-sm font-bold text-navy-950 uppercase tracking-wider">
            Personalized Career Role Compatibility Ranking
          </h3>
        </div>
        <p className="text-xs text-slate-500">
          Ranked purely on your extracted resume technical assets, not arbitrary predictions.
        </p>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 pt-2">
          {careerRecommendations.map((career, idx) => (
            <div
              key={idx}
              className="p-5 rounded-2xl border border-slate-200 bg-slate-50/50 hover:bg-white hover:border-purple-300 hover:shadow-md transition-all space-y-3"
            >
              <div className="flex justify-between items-start">
                <h4 className="text-sm font-bold text-navy-950">{career.jobTitle}</h4>
                <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-purple-100 text-brand-purple border border-purple-200">
                  {career.compatibilityPercentage}% Match
                </span>
              </div>

              {/* Progress bar */}
              <div className="w-full h-1.5 rounded-full bg-slate-200 overflow-hidden">
                <div
                  className="h-full bg-brand-purple rounded-full"
                  style={{ width: `${career.compatibilityPercentage}%` }}
                />
              </div>

              <p className="text-[11px] text-slate-600 leading-tight">
                {career.reason}
              </p>

              <div className="pt-1">
                <span className="text-[10px] uppercase font-bold text-slate-400 block mb-1">Top Matching Skills:</span>
                <div className="flex flex-wrap gap-1">
                  {career.matchingSkills?.slice(0, 4).map((s, sIdx) => (
                    <span key={sIdx} className="text-[10px] px-2 py-0.5 rounded bg-slate-200/70 text-slate-700 font-medium">
                      {s}
                    </span>
                  ))}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
