import React, { useState } from 'react';
import { api } from '../../api/apiClient';
import { FileText, Eye, Target, Trash2, Download, Plus, Sparkles, Layers } from 'lucide-react';

export default function ResumeList({ 
  resumes, 
  onSelectResume, 
  onInspectResume, 
  openUpload, 
  onDeleted, 
  onGoToAnalysis 
}) {
  const [deletingId, setDeletingId] = useState(null);

  const handleDelete = async (id, e) => {
    e.stopPropagation();
    if (!window.confirm('Are you sure you want to delete this resume? All associated analysis reports will also be removed.')) {
      return;
    }

    setDeletingId(id);
    try {
      await api.deleteResume(id);
      onDeleted?.(id);
    } catch (err) {
      alert('Failed to delete resume: ' + err.message);
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Header Bar */}
      <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl gradient-brand text-white flex items-center justify-center">
            <FileText className="w-5 h-5" />
          </div>
          <div>
            <h2 className="text-lg font-bold text-slate-900">My Resumes &amp; Drafts</h2>
            <p className="text-xs text-slate-500">Manage multiple resumes tailored for different target roles</p>
          </div>
        </div>

        <button
          onClick={openUpload}
          className="flex items-center gap-1.5 px-4 py-2 text-xs font-bold rounded-xl bg-brand-purple hover:bg-purple-600 text-white shadow-md shadow-purple-600/30 transition-all"
        >
          <Plus className="w-4 h-4" />
          <span>Upload New Resume</span>
        </button>
      </div>

      {/* Grid of Resumes */}
      {resumes && resumes.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {resumes.map((resume) => (
            <div
              key={resume.id}
              className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm hover:border-purple-300 hover:shadow-md transition-all flex flex-col justify-between group"
            >
              <div className="space-y-3">
                <div className="flex justify-between items-start">
                  <div className="w-10 h-10 rounded-xl bg-purple-50 text-brand-purple flex items-center justify-center shrink-0">
                    <FileText className="w-5 h-5" />
                  </div>

                  {resume.latestScore !== undefined && resume.latestScore !== null ? (
                    <div className="text-right">
                      <span className="text-[10px] uppercase font-bold text-slate-400 block">ATS Score</span>
                      <span className={`text-sm font-extrabold px-2 py-0.5 rounded-full inline-block ${
                        resume.latestScore >= 80 
                          ? 'bg-emerald-100 text-emerald-800' 
                          : resume.latestScore >= 60 
                          ? 'bg-purple-100 text-brand-purple' 
                          : 'bg-amber-100 text-amber-800'
                      }`}>
                        {resume.latestScore}/100
                      </span>
                    </div>
                  ) : (
                    <span className="text-[10px] text-slate-400 font-semibold px-2 py-0.5 rounded bg-slate-100">
                      Unscored
                    </span>
                  )}
                </div>

                <div>
                  <h3 className="text-sm font-bold text-slate-900 truncate" title={resume.originalFileName}>
                    {resume.originalFileName}
                  </h3>
                  <div className="flex items-center gap-2 mt-1">
                    <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-slate-100 text-slate-600 border border-slate-200">
                      {resume.versionTag || 'v1.0'}
                    </span>
                    <span className="text-[11px] text-indigo-700 font-semibold truncate">
                      {resume.targetRole || 'Software Engineer'}
                    </span>
                  </div>
                </div>

                <div className="pt-2 border-t border-slate-100 text-[11px] text-slate-400 space-y-1">
                  <p>Uploaded: {new Date(resume.createdAt).toLocaleDateString()}</p>
                  <p>Size: {(resume.fileSize / 1024).toFixed(1)} KB</p>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="pt-4 border-t border-slate-100 mt-4 flex items-center justify-between gap-2">
                <div className="flex items-center gap-1.5">
                  <button
                    onClick={() => onInspectResume(resume.id)}
                    className="p-1.5 rounded-lg text-slate-500 hover:text-brand-purple hover:bg-purple-50 transition-colors"
                    title="Inspect Extracted Text & Checklist"
                  >
                    <Eye className="w-4 h-4" />
                  </button>

                  <a
                    href={`/api/resumes/download/${resume.fileName}`}
                    download
                    className="p-1.5 rounded-lg text-slate-500 hover:text-indigo-600 hover:bg-indigo-50 transition-colors"
                    title="Download Original File"
                  >
                    <Download className="w-4 h-4" />
                  </a>

                  <button
                    onClick={(e) => handleDelete(resume.id, e)}
                    disabled={deletingId === resume.id}
                    className="p-1.5 rounded-lg text-slate-400 hover:text-rose-600 hover:bg-rose-50 transition-colors"
                    title="Delete Resume"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>

                <button
                  onClick={() => {
                    onSelectResume(resume.id);
                    onGoToAnalysis();
                  }}
                  className="flex items-center gap-1 px-3 py-1.5 rounded-lg text-xs font-bold bg-brand-purple text-white hover:bg-purple-600 shadow-sm transition-all"
                >
                  <Target className="w-3.5 h-3.5" />
                  <span>View ATS Score</span>
                </button>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="bg-white p-16 rounded-2xl border border-slate-200 text-center max-w-xl mx-auto shadow-sm space-y-4">
          <div className="w-14 h-14 rounded-2xl bg-purple-50 text-brand-purple flex items-center justify-center mx-auto">
            <FileText className="w-7 h-7" />
          </div>
          <h3 className="text-base font-bold text-slate-800">No Resumes Uploaded Yet</h3>
          <p className="text-xs text-slate-500 leading-relaxed">
            Upload your PDF or Word document to unlock explainable ATS scores, job description matching, and AI suggestions.
          </p>
          <button
            onClick={openUpload}
            className="px-6 py-2.5 rounded-xl text-xs font-bold bg-brand-purple hover:bg-purple-600 text-white shadow-md transition-all inline-flex items-center gap-2"
          >
            <Plus className="w-4 h-4" />
            <span>Upload Your First Resume</span>
          </button>
        </div>
      )}
    </div>
  );
}
