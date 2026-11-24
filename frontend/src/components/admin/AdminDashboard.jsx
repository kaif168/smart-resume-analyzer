import React, { useState, useEffect } from 'react';
import { api } from '../../api/apiClient';
import { Shield, Users, FileText, Target, Plus, Trash2, CheckCircle2, XCircle, RefreshCw } from 'lucide-react';

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [users, setUsers] = useState([]);
  const [templates, setTemplates] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showAddTemplate, setShowAddTemplate] = useState(false);

  // New Template Form
  const [newTitle, setNewTitle] = useState('');
  const [newRoleCat, setNewRoleCat] = useState('');
  const [newCompany, setNewCompany] = useState('Standard Industry Template');
  const [newDesc, setNewDesc] = useState('');
  const [newSkills, setNewSkills] = useState('');

  useEffect(() => {
    loadAdminData();
  }, []);

  const loadAdminData = async () => {
    setLoading(true);
    setError('');
    try {
      const [statsData, usersData, templatesData] = await Promise.all([
        api.getAdminStats(),
        api.getAdminUsers(),
        api.getJobTemplates()
      ]);
      setStats(statsData);
      setUsers(usersData || []);
      setTemplates(templatesData || []);
    } catch (err) {
      setError(err.message || 'Failed to load administrative analytics');
    } finally {
      setLoading(false);
    }
  };

  const handleToggleUser = async (userId, currentEnabled) => {
    try {
      await api.toggleUserStatus(userId, !currentEnabled);
      setUsers(users.map((u) => (u.id === userId ? { ...u, enabled: !currentEnabled } : u)));
    } catch (err) {
      alert('Error updating user status: ' + err.message);
    }
  };

  const handleAddTemplate = async (e) => {
    e.preventDefault();
    try {
      const skillsArray = newSkills.split(',').map((s) => s.trim()).filter(Boolean);
      await api.createJobTemplate({
        title: newTitle,
        roleCategory: newRoleCat || newTitle,
        company: newCompany,
        descriptionText: newDesc,
        requiredSkillsJson: JSON.stringify(skillsArray),
        experienceLevel: 'Mid-Level',
      });
      setShowAddTemplate(false);
      setNewTitle('');
      setNewDesc('');
      setNewSkills('');
      loadAdminData();
    } catch (err) {
      alert('Error creating template: ' + err.message);
    }
  };

  const handleDeleteTemplate = async (id) => {
    if (!window.confirm('Are you sure you want to delete this job template?')) return;
    try {
      await api.deleteJobTemplate(id);
      setTemplates(templates.filter((t) => t.id !== id));
    } catch (err) {
      alert('Error deleting template: ' + err.message);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Title Bar */}
      <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-indigo-950 text-indigo-400 flex items-center justify-center">
            <Shield className="w-5 h-5" />
          </div>
          <div>
            <h2 className="text-lg font-bold text-slate-900">Platform Administration Console</h2>
            <p className="text-xs text-slate-500">System metrics, account controls, and job benchmark templates</p>
          </div>
        </div>

        <button
          onClick={loadAdminData}
          disabled={loading}
          className="p-2 rounded-lg text-slate-600 hover:text-brand-purple hover:bg-slate-100 border border-slate-200 transition-colors"
          title="Refresh Data"
        >
          <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin text-brand-purple' : ''}`} />
        </button>
      </div>

      {error && (
        <div className="p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-xs">
          {error}
        </div>
      )}

      {/* Metrics Row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
          <span className="text-[10px] uppercase font-bold text-slate-400 block mb-0.5">Registered Users</span>
          <span className="text-2xl font-extrabold text-navy-950">{stats?.totalUsers || 0}</span>
          <span className="text-[10px] text-slate-500 block mt-0.5">Active platform accounts</span>
        </div>
        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
          <span className="text-[10px] uppercase font-bold text-slate-400 block mb-0.5">Total Resumes Uploaded</span>
          <span className="text-2xl font-extrabold text-brand-purple">{stats?.totalResumes || 0}</span>
          <span className="text-[10px] text-slate-500 block mt-0.5">Documents parsed securely</span>
        </div>
        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
          <span className="text-[10px] uppercase font-bold text-slate-400 block mb-0.5">Total ATS Audits Run</span>
          <span className="text-2xl font-extrabold text-indigo-700">{stats?.totalAnalyses || 0}</span>
          <span className="text-[10px] text-slate-500 block mt-0.5">Algorithmic scoring runs</span>
        </div>
        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
          <span className="text-[10px] uppercase font-bold text-slate-400 block mb-0.5">Global Average Score</span>
          <span className="text-2xl font-extrabold text-emerald-600">{stats?.averageAtsScore || '72.5'}/100</span>
          <span className="text-[10px] text-slate-500 block mt-0.5">Platform baseline</span>
        </div>
      </div>

      {/* User Accounts Management Table */}
      <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
        <div className="flex justify-between items-center">
          <div>
            <h3 className="text-sm font-bold text-navy-950 uppercase tracking-wider">
              User Accounts Management
            </h3>
            <p className="text-xs text-slate-500">Privacy rule: Administrator cannot view private resume file contents</p>
          </div>
          <span className="text-xs font-semibold px-2.5 py-1 rounded-full bg-slate-100 text-slate-700">
            {users.length} Users
          </span>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 text-slate-600 uppercase font-semibold text-[10px] tracking-wider border-b border-slate-200">
              <tr>
                <th className="p-3">User</th>
                <th className="p-3">Role</th>
                <th className="p-3">Resumes</th>
                <th className="p-3">Joined Date</th>
                <th className="p-3">Status</th>
                <th className="p-3 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {users.map((u) => (
                <tr key={u.id} className="hover:bg-slate-50/60 transition-colors">
                  <td className="p-3">
                    <p className="font-bold text-slate-900">{u.fullName}</p>
                    <p className="text-slate-500 text-[11px]">{u.email}</p>
                  </td>
                  <td className="p-3">
                    <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                      u.role === 'ADMIN' ? 'bg-purple-100 text-brand-purple' : 'bg-slate-100 text-slate-700'
                    }`}>
                      {u.role}
                    </span>
                  </td>
                  <td className="p-3 font-semibold text-slate-700">{u.resumeCount}</td>
                  <td className="p-3 text-slate-500">{new Date(u.createdAt).toLocaleDateString()}</td>
                  <td className="p-3">
                    {u.enabled ? (
                      <span className="inline-flex items-center gap-1 text-emerald-700 font-semibold text-[11px]">
                        <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" /> Active
                      </span>
                    ) : (
                      <span className="inline-flex items-center gap-1 text-rose-700 font-semibold text-[11px]">
                        <XCircle className="w-3.5 h-3.5 text-rose-600" /> Suspended
                      </span>
                    )}
                  </td>
                  <td className="p-3 text-right">
                    {u.email !== 'admin@smartresume.com' && (
                      <button
                        onClick={() => handleToggleUser(u.id, u.enabled)}
                        className={`text-xs font-semibold px-2.5 py-1 rounded-md transition-colors ${
                          u.enabled
                            ? 'text-rose-600 hover:bg-rose-50'
                            : 'text-emerald-600 hover:bg-emerald-50'
                        }`}
                      >
                        {u.enabled ? 'Suspend' : 'Reactivate'}
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Industry Job Skill Templates Management */}
      <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
        <div className="flex justify-between items-center">
          <div>
            <h3 className="text-sm font-bold text-navy-950 uppercase tracking-wider">
              Industry Job Benchmark Templates ({templates.length})
            </h3>
            <p className="text-xs text-slate-500">Benchmark skill profiles used in job description matching</p>
          </div>
          <button
            onClick={() => setShowAddTemplate(!showAddTemplate)}
            className="flex items-center gap-1 px-3 py-1.5 text-xs font-bold rounded-lg bg-brand-purple text-white hover:bg-purple-600 transition-all"
          >
            <Plus className="w-3.5 h-3.5" />
            <span>Add Template</span>
          </button>
        </div>

        {/* Add Template Form */}
        {showAddTemplate && (
          <form onSubmit={handleAddTemplate} className="p-4 rounded-xl bg-purple-50/50 border border-purple-200 space-y-3">
            <h4 className="text-xs font-bold text-brand-purple uppercase">Create New Industry Benchmark</h4>
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <div>
                <label className="block text-[11px] font-semibold text-slate-700 mb-1">Role Title</label>
                <input
                  type="text"
                  required
                  value={newTitle}
                  onChange={(e) => setNewTitle(e.target.value)}
                  placeholder="e.g. Cloud DevOps Architect"
                  className="w-full px-3 py-1.5 text-xs rounded border border-slate-300"
                />
              </div>
              <div>
                <label className="block text-[11px] font-semibold text-slate-700 mb-1">Role Category</label>
                <input
                  type="text"
                  required
                  value={newRoleCat}
                  onChange={(e) => setNewRoleCat(e.target.value)}
                  placeholder="e.g. Cloud & DevOps"
                  className="w-full px-3 py-1.5 text-xs rounded border border-slate-300"
                />
              </div>
              <div>
                <label className="block text-[11px] font-semibold text-slate-700 mb-1">Company / Source</label>
                <input
                  type="text"
                  value={newCompany}
                  onChange={(e) => setNewCompany(e.target.value)}
                  className="w-full px-3 py-1.5 text-xs rounded border border-slate-300"
                />
              </div>
            </div>

            <div>
              <label className="block text-[11px] font-semibold text-slate-700 mb-1">Required Skills (Comma separated)</label>
              <input
                type="text"
                required
                value={newSkills}
                onChange={(e) => setNewSkills(e.target.value)}
                placeholder="AWS, Docker, Kubernetes, Terraform, CI/CD, Python, Linux"
                className="w-full px-3 py-1.5 text-xs rounded border border-slate-300"
              />
            </div>

            <div>
              <label className="block text-[11px] font-semibold text-slate-700 mb-1">Description</label>
              <textarea
                rows={3}
                required
                value={newDesc}
                onChange={(e) => setNewDesc(e.target.value)}
                placeholder="Enter standard job summary and key requirements..."
                className="w-full p-2 text-xs rounded border border-slate-300"
              />
            </div>

            <div className="flex justify-end gap-2 pt-1">
              <button
                type="button"
                onClick={() => setShowAddTemplate(false)}
                className="px-3 py-1.5 text-xs text-slate-600 hover:bg-slate-100 rounded"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-4 py-1.5 text-xs font-bold bg-brand-purple text-white hover:bg-purple-600 rounded"
              >
                Save Template
              </button>
            </div>
          </form>
        )}

        {/* Templates Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {templates.map((tmpl) => (
            <div key={tmpl.id} className="p-4 rounded-xl border border-slate-200 bg-slate-50/60 flex flex-col justify-between">
              <div>
                <div className="flex justify-between items-start">
                  <h4 className="text-xs font-bold text-navy-950">{tmpl.title}</h4>
                  <button
                    onClick={() => handleDeleteTemplate(tmpl.id)}
                    className="text-slate-400 hover:text-rose-600"
                    title="Delete template"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>
                <p className="text-[11px] text-purple-700 font-semibold">{tmpl.roleCategory}</p>
                <p className="text-xs text-slate-600 mt-2 line-clamp-3">{tmpl.descriptionText}</p>
              </div>

              <div className="pt-3 border-t border-slate-200 mt-3">
                <span className="text-[10px] text-slate-400 uppercase font-bold block mb-1">Benchmark Skills:</span>
                <p className="text-[11px] text-slate-700 font-mono truncate">{tmpl.requiredSkillsJson}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
