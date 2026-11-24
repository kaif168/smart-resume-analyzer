import React, { useState, useEffect } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import { api } from './api/apiClient';

// Components
import Navbar from './components/common/Navbar';
import Footer from './components/common/Footer';
import Notification from './components/common/Notification';

import LandingPage from './components/landing/LandingPage';
import Dashboard from './components/dashboard/Dashboard';
import ResumeList from './components/resume/ResumeList';
import ResumeUploadModal from './components/resume/ResumeUploadModal';
import ResumePreviewModal from './components/resume/ResumePreviewModal';
import AtsScoreView from './components/analysis/AtsScoreView';
import JobMatchView from './components/jobmatching/JobMatchView';
import AiAssistantView from './components/ai/AiAssistantView';
import SkillGapView from './components/skills/SkillGapView';
import InterviewPrepView from './components/interview/InterviewPrepView';
import VersionCompareView from './components/version/VersionCompareView';
import AdminDashboard from './components/admin/AdminDashboard';

// Modals
import LoginModal from './components/auth/LoginModal';
import RegisterModal from './components/auth/RegisterModal';
import ForgotPasswordModal from './components/auth/ForgotPasswordModal';

function MainApp() {
  const { user, isAuthenticated, isAdmin } = useAuth();
  const [currentView, setCurrentView] = useState('landing');
  const [resumes, setResumes] = useState([]);
  const [activeResumeId, setActiveResumeId] = useState(null);

  // Modals
  const [isLoginOpen, setIsLoginOpen] = useState(false);
  const [isRegisterOpen, setIsRegisterOpen] = useState(false);
  const [isForgotOpen, setIsForgotOpen] = useState(false);
  const [isUploadOpen, setIsUploadOpen] = useState(false);
  const [previewResumeDetail, setPreviewResumeDetail] = useState(null);

  // Toast Notification
  const [notification, setNotification] = useState(null);

  const showToast = (message, type = 'info') => {
    setNotification({ message, type });
  };

  useEffect(() => {
    if (isAuthenticated) {
      loadResumes();
      if (currentView === 'landing') {
        setCurrentView('dashboard');
      }
    } else {
      setResumes([]);
      setActiveResumeId(null);
      setCurrentView('landing');
    }
  }, [isAuthenticated]);

  const loadResumes = async () => {
    try {
      const data = await api.getUserResumes();
      setResumes(data || []);
      if (data && data.length > 0 && !activeResumeId) {
        setActiveResumeId(data[0].id);
      }
    } catch (e) {
      // Error handling
    }
  };

  const handleUploadSuccess = (uploadedResume) => {
    showToast('Resume uploaded and extracted successfully!', 'success');
    loadResumes();
    setActiveResumeId(uploadedResume.id);
    setCurrentView('analysis');
  };

  const handleInspectResume = async (resumeId) => {
    try {
      const detail = await api.getResumeById(resumeId);
      setPreviewResumeDetail(detail);
    } catch (err) {
      showToast('Failed to load resume details: ' + err.message, 'error');
    }
  };

  const handleResumeDeleted = (deletedId) => {
    showToast('Resume deleted successfully', 'info');
    const filtered = resumes.filter((r) => r.id !== deletedId);
    setResumes(filtered);
    if (activeResumeId === deletedId) {
      setActiveResumeId(filtered[0]?.id || null);
    }
  };

  return (
    <div className="min-h-screen flex flex-col bg-slate-50 font-sans">
      {/* Top Navigation */}
      <Navbar
        currentView={currentView}
        setCurrentView={setCurrentView}
        openLogin={() => setIsLoginOpen(true)}
        openRegister={() => setIsRegisterOpen(true)}
        openUpload={() => setIsUploadOpen(true)}
      />

      {/* Main View Router */}
      <main className="flex-1">
        {currentView === 'landing' && (
          <LandingPage
            openRegister={() => setIsRegisterOpen(true)}
            openLogin={() => setIsLoginOpen(true)}
            openUpload={() => setIsUploadOpen(true)}
            isAuthenticated={isAuthenticated}
            goToDashboard={() => setCurrentView('dashboard')}
          />
        )}

        {currentView === 'dashboard' && (
          <Dashboard
            user={user}
            resumes={resumes}
            activeResumeId={activeResumeId}
            onSelectResume={(id) => setActiveResumeId(id)}
            openUpload={() => setIsUploadOpen(true)}
            goToView={(view) => setCurrentView(view)}
          />
        )}

        {currentView === 'resumes' && (
          <ResumeList
            resumes={resumes}
            onSelectResume={(id) => setActiveResumeId(id)}
            onInspectResume={handleInspectResume}
            openUpload={() => setIsUploadOpen(true)}
            onDeleted={handleResumeDeleted}
            onGoToAnalysis={() => setCurrentView('analysis')}
          />
        )}

        {currentView === 'analysis' && (
          <AtsScoreView
            resumeId={activeResumeId}
            resumes={resumes}
            onSelectResume={(id) => setActiveResumeId(id)}
            onGoToJobMatch={() => setCurrentView('jobmatching')}
            onGoToAi={() => setCurrentView('ai-assistant')}
          />
        )}

        {currentView === 'jobmatching' && (
          <JobMatchView
            resumeId={activeResumeId}
            resumes={resumes}
            onSelectResume={(id) => setActiveResumeId(id)}
          />
        )}

        {currentView === 'ai-assistant' && (
          <AiAssistantView
            resumeId={activeResumeId}
            resumes={resumes}
            onSelectResume={(id) => setActiveResumeId(id)}
          />
        )}

        {currentView === 'skills' && (
          <SkillGapView
            resumeId={activeResumeId}
            resumes={resumes}
            onSelectResume={(id) => setActiveResumeId(id)}
          />
        )}

        {currentView === 'interview' && (
          <InterviewPrepView
            resumeId={activeResumeId}
            resumes={resumes}
            onSelectResume={(id) => setActiveResumeId(id)}
          />
        )}

        {currentView === 'compare' && (
          <VersionCompareView resumes={resumes} />
        )}

        {currentView === 'admin' && isAdmin && (
          <AdminDashboard />
        )}
      </main>

      {/* Footer */}
      <Footer />

      {/* Modals */}
      <LoginModal
        isOpen={isLoginOpen}
        onClose={() => setIsLoginOpen(false)}
        openRegister={() => setIsRegisterOpen(true)}
        openForgotPassword={() => setIsForgotOpen(true)}
        onSuccess={() => {
          showToast('Signed in successfully!', 'success');
          setCurrentView('dashboard');
        }}
      />

      <RegisterModal
        isOpen={isRegisterOpen}
        onClose={() => setIsRegisterOpen(false)}
        openLogin={() => setIsLoginOpen(true)}
        onSuccess={() => {
          showToast('Account created successfully!', 'success');
          setCurrentView('dashboard');
        }}
      />

      <ForgotPasswordModal
        isOpen={isForgotOpen}
        onClose={() => setIsForgotOpen(false)}
        openLogin={() => setIsLoginOpen(true)}
      />

      <ResumeUploadModal
        isOpen={isUploadOpen}
        onClose={() => setIsUploadOpen(false)}
        onUploadSuccess={handleUploadSuccess}
      />

      <ResumePreviewModal
        isOpen={!!previewResumeDetail}
        onClose={() => setPreviewResumeDetail(null)}
        resumeDetail={previewResumeDetail}
        onChecklistUpdated={(updatedList) => {
          setPreviewResumeDetail((prev) => prev ? { ...prev, checklist: updatedList } : null);
        }}
      />

      {/* Toast Notification */}
      {notification && (
        <Notification
          type={notification.type}
          message={notification.message}
          onClose={() => setNotification(null)}
        />
      )}
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <MainApp />
    </AuthProvider>
  );
}
