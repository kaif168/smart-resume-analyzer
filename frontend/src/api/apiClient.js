const API_BASE = '/api';

export async function request(endpoint, options = {}) {
  const token = localStorage.getItem('token');
  
  const headers = {
    ...options.headers,
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  // If not FormData, set Content-Type to JSON by default
  if (!(options.body instanceof FormData) && !headers['Content-Type']) {
    headers['Content-Type'] = 'application/json';
  }

  const response = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers,
  });

  // Handle 401 Unauthorized
  if (response.status === 401 && !endpoint.includes('/auth/login')) {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.dispatchEvent(new Event('auth:unauthorized'));
  }

  // If response is a file download (e.g. PDF)
  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/pdf')) {
    if (!response.ok) {
      throw new Error('Failed to download report PDF');
    }
    return response.blob();
  }

  const json = await response.json();

  if (!response.ok || (json && json.success === false)) {
    const errorMsg = json?.message || `Request failed with status ${response.status}`;
    throw new Error(errorMsg);
  }

  return json.data !== undefined ? json.data : json;
}

export const api = {
  // Auth
  login: (credentials) => request('/auth/login', { method: 'POST', body: JSON.stringify(credentials) }),
  register: (data) => request('/auth/register', { method: 'POST', body: JSON.stringify(data) }),
  forgotPassword: (email) => request('/auth/forgot-password', { method: 'POST', body: JSON.stringify({ email }) }),
  resetPassword: (payload) => request('/auth/reset-password', { method: 'POST', body: JSON.stringify(payload) }),
  getUserProfile: () => request('/user/profile'),
  updateProfile: (profile) => request('/user/profile', { method: 'PUT', body: JSON.stringify(profile) }),

  // Resumes
  uploadResume: (formData) => request('/resumes/upload', { method: 'POST', body: formData }),
  getUserResumes: () => request('/resumes'),
  getResumeById: (id) => request(`/resumes/${id}`),
  deleteResume: (id) => request(`/resumes/${id}`, { method: 'DELETE' }),
  toggleChecklist: (resumeId, itemId, completed) => request(`/resumes/${resumeId}/checklist/${itemId}/toggle`, {
    method: 'POST',
    body: JSON.stringify({ completed })
  }),
  compareVersions: (oldResumeId, newResumeId) => request(`/resumes/compare?oldResumeId=${oldResumeId}&newResumeId=${newResumeId}`),

  // Analysis
  analyzeResume: (resumeId) => request(`/analysis/${resumeId}`, { method: 'POST' }),
  getAnalysis: (resumeId) => request(`/analysis/${resumeId}`),
  getAnalysisHistory: () => request('/analysis/history'),

  // Job Matching
  matchJob: (payload) => request('/job-matching/analyze', { method: 'POST', body: JSON.stringify(payload) }),
  getJobTemplates: () => request('/job-matching/templates'),
  getMatchHistory: (resumeId) => request(`/job-matching/history/${resumeId}`),

  // AI & Career
  getAiImprovements: (payload) => request('/ai/improvements', { method: 'POST', body: JSON.stringify(payload) }),
  analyzeProject: (payload) => request('/ai/project-feedback', { method: 'POST', body: JSON.stringify(payload) }),
  getInterviewQuestions: (resumeId) => request(`/ai/interview-questions/${resumeId}`),
  getCareerRecommendations: (resumeId) => request(`/recommendations/career/${resumeId}`),
  getSkillGap: (resumeId, targetRole) => request(`/recommendations/skills/${resumeId}${targetRole ? `?targetRole=${encodeURIComponent(targetRole)}` : ''}`),

  // Reports
  downloadReport: (analysisId) => request(`/reports/${analysisId}/download`),

  // Admin
  getAdminStats: () => request('/admin/dashboard'),
  getAdminUsers: () => request('/admin/users'),
  toggleUserStatus: (userId, enabled) => request(`/admin/users/${userId}/status`, {
    method: 'PUT',
    body: JSON.stringify({ enabled })
  }),
  createJobTemplate: (template) => request('/admin/job-templates', {
    method: 'POST',
    body: JSON.stringify(template)
  }),
  deleteJobTemplate: (id) => request(`/admin/job-templates/${id}`, { method: 'DELETE' }),
};
