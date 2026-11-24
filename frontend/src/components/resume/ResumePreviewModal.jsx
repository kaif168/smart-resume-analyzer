import React, { useState } from 'react';
import { api } from '../../api/apiClient';
import { X, FileText, CheckCircle2, CheckSquare, Square, Mail, Phone, MapPin, Tag, Briefcase } from 'lucide-react';

export default function ResumePreviewModal({ isOpen, onClose, resumeDetail, onChecklistUpdated }) {
  const [activeTab, setActiveTab] = useState('DETAILS'); // 'DETAILS' or 'RAW_TEXT' or 'CHECKLIST'
  const [checklist, setChecklist] = useState(resumeDetail?.checklist || []);

  if (!isOpen || !resumeDetail) return null;

  const handleToggleChecklist = async (itemId, currentCompleted) => {
    try {
      const updatedItem = await api.toggleChecklist(resumeDetail.id, itemId, !currentCompleted);
      const nextList = checklist.map((item) =>
        item.id === itemId ? { ...item, completed: !currentCompleted } : item
      );
      setChecklist(nextList);
      onChecklistUpdated?.(nextList);
    } catch (e) {
      // Toggle locally on error
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-navy-950/75 backdrop-blur-sm animate-fade-in">
      <div className="relative w-full max-w-3xl bg-white rounded-2xl shadow-2xl border border-slate-100 overflow-hidden flex flex-col max-h-[90vh]">
        {/* Header */}
        <div className="gradient-brand p-5 text-white flex items-center justify-between shrink-0">
          <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-xl bg-white/10 flex items-center justify-center">
              <FileText className="w-5 h-5 text-white" />
            </div>
            <div>
              <h3 className="text-base font-bold truncate max-w-md">{resumeDetail.originalFileName}</h3>
              <p className="text-xs text-purple-200">
                {resumeDetail.targetRole} • {resumeDetail.versionTag} • {new Date(resumeDetail.createdAt).toLocaleDateString()}
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg bg-white/10 hover:bg-white/20 transition-colors"
          >
            <X className="w-4 h-4 text-white" />
          </button>
        </div>

        {/* Tab navigation */}
        <div className="flex border-b border-slate-200 px-6 pt-3 gap-6 bg-slate-50 shrink-0 text-sm font-semibold">
          <button
            onClick={() => setActiveTab('DETAILS')}
            className={`pb-3 transition-colors ${
              activeTab === 'DETAILS'
                ? 'border-b-2 border-brand-purple text-brand-purple'
                : 'text-slate-500 hover:text-slate-800'
            }`}
          >
            Structured Overview
          </button>
          <button
            onClick={() => setActiveTab('CHECKLIST')}
            className={`pb-3 transition-colors flex items-center gap-1.5 ${
              activeTab === 'CHECKLIST'
                ? 'border-b-2 border-brand-purple text-brand-purple'
                : 'text-slate-500 hover:text-slate-800'
            }`}
          >
            <span>Optimization Checklist</span>
            <span className="px-1.5 py-0.2 rounded-full bg-purple-100 text-brand-purple text-[10px]">
              {checklist.filter((c) => c.completed).length}/{checklist.length}
            </span>
          </button>
          <button
            onClick={() => setActiveTab('RAW_TEXT')}
            className={`pb-3 transition-colors ${
              activeTab === 'RAW_TEXT'
                ? 'border-b-2 border-brand-purple text-brand-purple'
                : 'text-slate-500 hover:text-slate-800'
            }`}
          >
            Extracted Document Text
          </button>
        </div>

        {/* Content Body */}
        <div className="p-6 overflow-y-auto space-y-6">
          {activeTab === 'DETAILS' && (
            <>
              {/* Candidate Metadata Grid */}
              <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-3">
                <div className="p-3 rounded-xl bg-slate-50 border border-slate-200">
                  <span className="text-[10px] uppercase font-bold text-slate-400 block mb-0.5">Candidate Name</span>
                  <p className="text-sm font-bold text-slate-900">{resumeDetail.candidateName || 'Not Detected'}</p>
                </div>
                <div className="p-3 rounded-xl bg-slate-50 border border-slate-200">
                  <span className="text-[10px] uppercase font-bold text-slate-400 block mb-0.5">Email</span>
                  <p className="text-sm font-medium text-slate-800 truncate">{resumeDetail.candidateEmail || 'N/A'}</p>
                </div>
                <div className="p-3 rounded-xl bg-slate-50 border border-slate-200">
                  <span className="text-[10px] uppercase font-bold text-slate-400 block mb-0.5">Phone</span>
                  <p className="text-sm font-medium text-slate-800">{resumeDetail.candidatePhone || 'N/A'}</p>
                </div>
                <div className="p-3 rounded-xl bg-slate-50 border border-slate-200">
                  <span className="text-[10px] uppercase font-bold text-slate-400 block mb-0.5">Location</span>
                  <p className="text-sm font-medium text-slate-800">{resumeDetail.candidateLocation || 'Not Specified'}</p>
                </div>
                <div className="p-3 rounded-xl bg-slate-50 border border-slate-200">
                  <span className="text-[10px] uppercase font-bold text-slate-400 block mb-0.5">Estimated Experience</span>
                  <p className="text-sm font-bold text-brand-purple">~{resumeDetail.experienceYears || 0} Years</p>
                </div>
                <div className="p-3 rounded-xl bg-slate-50 border border-slate-200">
                  <span className="text-[10px] uppercase font-bold text-slate-400 block mb-0.5">Target Role</span>
                  <p className="text-sm font-bold text-indigo-700">{resumeDetail.targetRole || 'General'}</p>
                </div>
              </div>

              {/* Professional Summary */}
              {resumeDetail.professionalSummary && (
                <div className="p-4 rounded-xl bg-purple-50/50 border border-purple-200">
                  <h4 className="text-xs font-bold text-brand-purple uppercase tracking-wider mb-1.5">
                    Extracted Professional Summary
                  </h4>
                  <p className="text-xs text-slate-700 leading-relaxed italic">
                    "{resumeDetail.professionalSummary}"
                  </p>
                </div>
              )}

              {/* Extracted Skills List */}
              <div>
                <h4 className="text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
                  Extracted Skills ({resumeDetail.extractedSkills?.length || 0})
                </h4>
                <div className="flex flex-wrap gap-1.5">
                  {resumeDetail.extractedSkills?.map((skill, idx) => (
                    <span
                      key={idx}
                      className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg text-xs font-medium bg-slate-100 text-slate-800 border border-slate-200"
                    >
                      <CheckCircle2 className="w-3 h-3 text-emerald-500" />
                      <span>{skill.skillName}</span>
                    </span>
                  ))}
                  {(!resumeDetail.extractedSkills || resumeDetail.extractedSkills.length === 0) && (
                    <p className="text-xs text-slate-400">No structured skills detected yet.</p>
                  )}
                </div>
              </div>
            </>
          )}

          {activeTab === 'CHECKLIST' && (
            <div className="space-y-2.5">
              <p className="text-xs text-slate-500 mb-3">
                Track and verify recommended optimizations to increase your ATS match probability before submitting applications.
              </p>
              {checklist.map((item) => (
                <div
                  key={item.id}
                  onClick={() => handleToggleChecklist(item.id, item.completed)}
                  className={`p-3.5 rounded-xl border flex items-center gap-3 cursor-pointer transition-all ${
                    item.completed
                      ? 'bg-emerald-50/60 border-emerald-200 text-emerald-900'
                      : 'bg-white border-slate-200 hover:border-purple-300'
                  }`}
                >
                  <button type="button" className="shrink-0 text-slate-400">
                    {item.completed ? (
                      <CheckSquare className="w-5 h-5 text-emerald-600" />
                    ) : (
                      <Square className="w-5 h-5 text-slate-400" />
                    )}
                  </button>
                  <div className="flex-1">
                    <p className={`text-xs font-bold ${item.completed ? 'line-through text-emerald-800' : 'text-slate-800'}`}>
                      {item.title}
                    </p>
                    <span className="text-[10px] text-slate-400 font-medium uppercase tracking-wider">
                      {item.category}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}

          {activeTab === 'RAW_TEXT' && (
            <div>
              <p className="text-xs text-slate-500 mb-2">
                This is the raw text parsed by the backend engine (Apache PDFBox / Apache POI) exactly as an ATS parser reads it:
              </p>
              <pre className="p-4 rounded-xl bg-slate-900 text-slate-100 font-mono text-xs whitespace-pre-wrap leading-relaxed max-h-96 overflow-y-auto">
                {resumeDetail.extractedText || 'No text extracted.'}
              </pre>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
