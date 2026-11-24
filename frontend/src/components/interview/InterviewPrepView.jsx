import React, { useState, useEffect } from 'react';
import { api } from '../../api/apiClient';
import { MessageSquare, ChevronDown, Sparkles, HelpCircle, BookOpen, Layers } from 'lucide-react';

export default function InterviewPrepView({ resumeId, resumes, onSelectResume }) {
  const [questions, setQuestions] = useState([]);
  const [filter, setFilter] = useState('ALL');
  const [expandedIndex, setExpandedIndex] = useState(0);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (resumeId) {
      loadQuestions(resumeId);
    }
  }, [resumeId]);

  const loadQuestions = async (id) => {
    setLoading(true);
    try {
      const data = await api.getInterviewQuestions(id);
      setQuestions(data || []);
    } catch (e) {
      // Error
    } finally {
      setLoading(false);
    }
  };

  const filteredQuestions = filter === 'ALL'
    ? questions
    : questions.filter((q) => q.category === filter);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Title Bar */}
      <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl gradient-brand text-white flex items-center justify-center">
            <MessageSquare className="w-5 h-5" />
          </div>
          <div>
            <h2 className="text-lg font-bold text-slate-900">Tailored Interview Preparation</h2>
            <p className="text-xs text-slate-500">
              Generated from the technologies and accomplishments listed on your resume
            </p>
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

          <div className="flex bg-slate-100 p-1 rounded-lg text-xs font-semibold">
            {['ALL', 'TECHNICAL', 'SYSTEM_DESIGN', 'BEHAVIORAL'].map((cat) => (
              <button
                key={cat}
                onClick={() => setFilter(cat)}
                className={`px-2.5 py-1 rounded-md transition-colors ${
                  filter === cat ? 'bg-white text-brand-purple shadow-sm' : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                {cat.replace('_', ' ')}
              </button>
            ))}
          </div>
        </div>
      </div>

      {loading ? (
        <div className="py-20 text-center">
          <div className="w-10 h-10 rounded-full border-4 border-purple-200 border-t-brand-purple animate-spin mx-auto mb-3" />
          <p className="text-xs text-slate-500 font-medium">Generating questions based on your skills...</p>
        </div>
      ) : (
        <div className="space-y-4">
          {filteredQuestions.map((q, idx) => (
            <div
              key={idx}
              className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden transition-all"
            >
              <button
                onClick={() => setExpandedIndex(expandedIndex === idx ? null : idx)}
                className="w-full p-5 text-left flex items-start justify-between gap-4 hover:bg-slate-50/50 transition-colors"
              >
                <div className="space-y-1.5">
                  <div className="flex items-center gap-2">
                    <span className="px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider bg-purple-100 text-brand-purple">
                      {q.category}
                    </span>
                    <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                      q.difficulty === 'Hard' ? 'bg-rose-100 text-rose-700' : 'bg-indigo-100 text-indigo-700'
                    }`}>
                      {q.difficulty}
                    </span>
                    {q.relatedSkill && (
                      <span className="text-[11px] text-slate-400 font-medium">
                        Focus: <strong className="text-slate-600">{q.relatedSkill}</strong>
                      </span>
                    )}
                  </div>
                  <h4 className="text-sm font-bold text-slate-900 leading-snug">{q.question}</h4>
                </div>
                <ChevronDown
                  className={`w-5 h-5 text-slate-400 shrink-0 transition-transform ${
                    expandedIndex === idx ? 'rotate-180 text-brand-purple' : ''
                  }`}
                />
              </button>

              {expandedIndex === idx && (
                <div className="px-5 pb-5 pt-1 border-t border-slate-100 bg-purple-50/20 space-y-2">
                  <div className="flex items-center gap-1.5 text-xs font-bold text-brand-purple pt-2">
                    <BookOpen className="w-4 h-4" />
                    <span>Suggested Answer Framework &amp; Technical Talking Points:</span>
                  </div>
                  <p className="text-xs text-slate-700 leading-relaxed bg-white p-4 rounded-xl border border-purple-100/80">
                    {q.sampleAnswer}
                  </p>
                </div>
              )}
            </div>
          ))}

          {filteredQuestions.length === 0 && (
            <div className="p-10 text-center bg-white rounded-2xl border border-slate-200 text-xs text-slate-400">
              No interview questions found under this filter category.
            </div>
          )}
        </div>
      )}
    </div>
  );
}
