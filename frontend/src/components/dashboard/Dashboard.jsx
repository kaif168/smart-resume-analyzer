import React, { useState, useEffect } from 'react';
import { api } from '../../api/apiClient';
import { 
  FileText, Target, Briefcase, Bot, TrendingUp, Sparkles, 
  ArrowRight, Plus, Award, CheckCircle2, Clock, ShieldCheck 
} from 'lucide-react';
import {
  Chart as ChartJS,
  RadialLinearScale,
  PointElement,
  LineElement,
  Filler,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  BarElement,
} from 'chart.js';
import { Radar, Bar } from 'react-chartjs-2';

ChartJS.register(
  RadialLinearScale,
  PointElement,
  LineElement,
  Filler,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  BarElement
);

export default function Dashboard({ 
  user, 
  resumes, 
  activeResumeId, 
  onSelectResume, 
  openUpload, 
  goToView 
}) {
  const [history, setHistory] = useState([]);
  const [latestAnalysis, setLatestAnalysis] = useState(null);
  const [careerRecommendations, setCareerRecommendations] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadDashboardData();
  }, [activeResumeId]);

  const loadDashboardData = async () => {
    setLoading(true);
    try {
      const hist = await api.getAnalysisHistory();
      setHistory(hist || []);

      if (activeResumeId) {
        const [analysis, careers] = await Promise.all([
          api.getAnalysis(activeResumeId).catch(() => null),
          api.getCareerRecommendations(activeResumeId).catch(() => [])
        ]);
        setLatestAnalysis(analysis);
        setCareerRecommendations(careers || []);
      }
    } catch (e) {
      // Graceful fallback
    } finally {
      setLoading(false);
    }
  };

  const activeResume = resumes?.find((r) => r.id === activeResumeId) || resumes?.[0];

  // Category scores radar chart
  const radarData = {
    labels: [
      'Contact',
      'Structure',
      'Skills',
      'Keywords',
      'Experience',
      'Education',
      'Readability',
    ],
    datasets: [
      {
        label: 'Current Score',
        data: latestAnalysis?.categoryScores
          ? latestAnalysis.categoryScores.map((c) => c.percentage)
          : [80, 85, 75, 70, 80, 90, 85],
        backgroundColor: 'rgba(124, 58, 237, 0.25)',
        borderColor: '#7C3AED',
        borderWidth: 2,
        pointBackgroundColor: '#7C3AED',
      },
      {
        label: 'Target Benchmark (85%+)',
        data: [100, 95, 90, 85, 90, 95, 90],
        backgroundColor: 'rgba(16, 185, 129, 0.08)',
        borderColor: 'rgba(16, 185, 129, 0.6)',
        borderWidth: 1,
        borderDash: [4, 4],
        pointRadius: 0,
      },
    ],
  };

  const radarOptions = {
    scales: {
      r: {
        suggestedMin: 0,
        suggestedMax: 100,
        ticks: { display: false },
        pointLabels: {
          font: { size: 10, weight: 'bold' },
          color: '#475569',
        },
      },
    },
    plugins: {
      legend: {
        position: 'bottom',
        labels: { boxWidth: 12, font: { size: 10 } },
      },
    },
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Welcome Banner */}
      <div className="bg-gradient-to-r from-navy-900 via-indigo-950 to-navy-900 p-6 sm:p-8 rounded-3xl text-white shadow-xl flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
        <div>
          <div className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-purple-500/20 text-purple-300 text-xs font-semibold mb-3 border border-purple-500/30">
            <Sparkles className="w-3.5 h-3.5" />
            <span>AI Career Assistant Active</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
            Welcome back, {user?.fullName || 'Candidate'}!
          </h1>
          <p className="text-xs sm:text-sm text-slate-300 mt-1 max-w-xl">
            {activeResume
              ? `Reviewing "${activeResume.originalFileName}" targeting ${activeResume.targetRole || 'Software Engineering'}.`
              : 'Upload your resume to begin explainable ATS scoring and job matching.'}
          </p>
        </div>

        <div className="flex flex-wrap gap-2.5">
          <button
            onClick={openUpload}
            className="px-4 py-2.5 rounded-xl text-xs font-bold bg-brand-purple hover:bg-purple-600 text-white shadow-lg shadow-purple-600/30 transition-all flex items-center gap-2"
          >
            <Plus className="w-4 h-4" />
            <span>Upload Resume</span>
          </button>
          <button
            onClick={() => goToView('jobmatching')}
            className="px-4 py-2.5 rounded-xl text-xs font-semibold bg-white/10 hover:bg-white/20 text-white border border-white/20 transition-all flex items-center gap-2"
          >
            <Briefcase className="w-4 h-4 text-purple-300" />
            <span>Match Job</span>
          </button>
        </div>
      </div>

      {/* 4 Stat Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        {/* Metric 1 */}
        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <span className="text-[10px] uppercase font-bold text-slate-400 block mb-0.5">Total Resumes</span>
            <span className="text-2xl font-extrabold text-navy-950">{resumes?.length || 0}</span>
            <span className="text-[10px] text-slate-500 block mt-0.5">Active drafts</span>
          </div>
          <div className="w-11 h-11 rounded-xl bg-purple-50 text-brand-purple flex items-center justify-center">
            <FileText className="w-5 h-5" />
          </div>
        </div>

        {/* Metric 2 */}
        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <span className="text-[10px] uppercase font-bold text-slate-400 block mb-0.5">Latest ATS Score</span>
            <span className="text-2xl font-extrabold text-brand-purple">
              {latestAnalysis?.overallScore !== undefined ? `${latestAnalysis.overallScore}/100` : '--'}
            </span>
            <span className="text-[10px] text-emerald-600 font-semibold block mt-0.5">
              {latestAnalysis?.scoreRating || 'Pending analysis'}
            </span>
          </div>
          <div className="w-11 h-11 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center">
            <Target className="w-5 h-5" />
          </div>
        </div>

        {/* Metric 3 */}
        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <span className="text-[10px] uppercase font-bold text-slate-400 block mb-0.5">Total Analyses</span>
            <span className="text-2xl font-extrabold text-navy-950">{history?.length || 0}</span>
            <span className="text-[10px] text-slate-500 block mt-0.5">Audit runs completed</span>
          </div>
          <div className="w-11 h-11 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
            <Clock className="w-5 h-5" />
          </div>
        </div>

        {/* Metric 4 */}
        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <span className="text-[10px] uppercase font-bold text-slate-400 block mb-0.5">Career Match</span>
            <span className="text-2xl font-extrabold text-emerald-600">
              {careerRecommendations[0]?.compatibilityPercentage ? `${careerRecommendations[0].compatibilityPercentage}%` : '85%'}
            </span>
            <span className="text-[10px] text-slate-500 truncate block max-w-[120px] mt-0.5">
              {careerRecommendations[0]?.jobTitle || 'Java Developer'}
            </span>
          </div>
          <div className="w-11 h-11 rounded-xl bg-pink-50 text-pink-600 flex items-center justify-center">
            <TrendingUp className="w-5 h-5" />
          </div>
        </div>
      </div>

      {/* Main Grid: Radar Chart + Recommended Job Roles */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Left: 7-Pillar ATS Radar Chart */}
        <div className="lg:col-span-7 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
          <div className="flex justify-between items-center">
            <div>
              <h3 className="text-sm font-bold text-navy-950 uppercase tracking-wider">
                ATS Compatibility Pillars (Current vs Target)
              </h3>
              <p className="text-xs text-slate-500">7 categories evaluated by the Spring Boot scoring engine</p>
            </div>
            <button
              onClick={() => goToView('analysis')}
              className="text-xs font-bold text-brand-purple hover:underline flex items-center gap-1"
            >
              <span>View Breakdown</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </button>
          </div>

          <div className="h-72 flex items-center justify-center">
            <Radar data={radarData} options={radarOptions} />
          </div>
        </div>

        {/* Right: Career Recommendation Snapshot */}
        <div className="lg:col-span-5 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4 flex flex-col justify-between">
          <div>
            <div className="flex justify-between items-center mb-3">
              <h3 className="text-sm font-bold text-navy-950 uppercase tracking-wider">
                Top Job Role Compatibility
              </h3>
              <button
                onClick={() => goToView('skills')}
                className="text-xs font-bold text-brand-purple hover:underline"
              >
                Full Skill Gap
              </button>
            </div>
            <p className="text-xs text-slate-500 mb-4">
              Roles ranking highest for your current extracted technical skill profile:
            </p>

            <div className="space-y-3">
              {careerRecommendations.slice(0, 3).map((rec, idx) => (
                <div
                  key={idx}
                  className="p-3.5 rounded-xl bg-slate-50 border border-slate-200 flex items-center justify-between"
                >
                  <div>
                    <h4 className="text-xs font-bold text-navy-950">{rec.jobTitle}</h4>
                    <p className="text-[10px] text-slate-500 mt-0.5">
                      Overlap: {rec.matchingSkills?.slice(0, 3).join(', ')}
                    </p>
                  </div>
                  <span className="px-2.5 py-1 rounded-full text-xs font-extrabold bg-purple-100 text-brand-purple border border-purple-200">
                    {rec.compatibilityPercentage}%
                  </span>
                </div>
              ))}
              {careerRecommendations.length === 0 && (
                <div className="p-6 text-center text-xs text-slate-400">
                  Select or upload a resume to calculate career matches.
                </div>
              )}
            </div>
          </div>

          {/* Quick AI Prompt Card */}
          <div className="p-4 rounded-xl bg-purple-50/70 border border-purple-200 flex items-center justify-between gap-3 mt-4">
            <div className="flex items-center gap-2.5">
              <Bot className="w-5 h-5 text-brand-purple shrink-0" />
              <div>
                <h5 className="text-xs font-bold text-purple-950">Enhance Professional Summary</h5>
                <p className="text-[10px] text-purple-700">AI rephrasing tailored to your target job</p>
              </div>
            </div>
            <button
              onClick={() => goToView('ai-assistant')}
              className="px-3 py-1.5 rounded-lg text-xs font-bold bg-brand-purple text-white hover:bg-purple-600 transition-colors shrink-0"
            >
              Open AI
            </button>
          </div>
        </div>
      </div>

      {/* Recent Analysis History Table */}
      <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
        <h3 className="text-sm font-bold text-navy-950 uppercase tracking-wider">
          Recent Resume Analysis History
        </h3>

        {history && history.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 text-slate-600 uppercase font-semibold text-[10px] tracking-wider border-b border-slate-200">
                <tr>
                  <th className="p-3">Analysis Date</th>
                  <th className="p-3">Target Role</th>
                  <th className="p-3">ATS Score</th>
                  <th className="p-3">Notes &amp; Verdict</th>
                  <th className="p-3 text-right">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {history.slice(0, 5).map((h) => (
                  <tr key={h.id} className="hover:bg-slate-50/60 transition-colors">
                    <td className="p-3 text-slate-700 font-medium">
                      {new Date(h.createdAt).toLocaleDateString()} {new Date(h.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                    </td>
                    <td className="p-3 font-semibold text-navy-950">{h.roleTarget || 'Software Engineer'}</td>
                    <td className="p-3">
                      <span className={`px-2 py-0.5 rounded-full font-bold ${
                        h.overallScore >= 80 ? 'bg-emerald-100 text-emerald-800' : 'bg-purple-100 text-brand-purple'
                      }`}>
                        {h.overallScore}/100
                      </span>
                    </td>
                    <td className="p-3 text-slate-500">{h.notes || 'Full scan completed'}</td>
                    <td className="p-3 text-right">
                      <button
                        onClick={() => {
                          onSelectResume(h.resume?.id || activeResumeId);
                          goToView('analysis');
                        }}
                        className="text-brand-purple font-bold hover:underline"
                      >
                        View Report
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <p className="text-xs text-slate-400 text-center py-6">No previous analysis history recorded yet.</p>
        )}
      </div>
    </div>
  );
}
