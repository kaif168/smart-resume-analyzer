import React, { useState } from 'react';
import { 
  Sparkles, Target, Briefcase, Bot, FileText, CheckCircle2, 
  ArrowRight, ShieldCheck, TrendingUp, Users, ChevronDown, 
  Award, Zap, FileCheck, Layers 
} from 'lucide-react';

export default function LandingPage({ openRegister, openLogin, openUpload, isAuthenticated, goToDashboard }) {
  const [openFaq, setOpenFaq] = useState(null);

  const toggleFaq = (index) => {
    setOpenFaq(openFaq === index ? null : index);
  };

  const handleStart = () => {
    if (isAuthenticated) {
      goToDashboard();
    } else {
      openRegister();
    }
  };

  const faqs = [
    {
      q: "Is the ATS compatibility score an actual score from an employer's ATS?",
      a: "No. The score is an explainable compatibility estimate calculated across 7 rigorous algorithmic categories (Contact info, Structure & headings, Skills relevance, Keyword density, Experience impact metrics, Education/certifications, and Readability). It prepares your resume to navigate past parsing hurdles in modern ATS systems like Workday, Taleo, and Greenhouse."
    },
    {
      q: "What file formats does the platform support?",
      a: "We support PDF (.pdf) and Microsoft Word (.docx) files. Documents are parsed directly via Apache PDFBox and Apache POI to extract full textual hierarchies safely."
    },
    {
      q: "How does the Job Description Matching work?",
      a: "You can paste any job listing or choose an industry template (Java Developer, Full Stack, Frontend, Backend, Data Analyst). Our system compares your resume's technical skills and keywords against the role, highlighting matched skills, missing skills, and recommended additions."
    },
    {
      q: "Does the AI assistant invent or fabricate qualifications?",
      a: "Never. Our AI engine adheres strictly to ethical resume guidelines. It only enhances, rephrases, and structures candidate-provided information using high-impact action verbs and the STAR methodology, without fabricating degrees, companies, or certifications."
    },
    {
      q: "How secure is my resume data?",
      a: "Your uploaded resumes are stored securely on local isolated storage and mapped exclusively to your account. Passwords are encrypted with BCrypt, and all REST endpoints are protected with stateless JWT tokens."
    }
  ];

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col">
      {/* Hero Section */}
      <section className="relative overflow-hidden bg-navy-900 text-white pt-20 pb-28">
        {/* Glow Effects */}
        <div className="absolute top-0 left-1/2 -translate-x-1/2 w-[800px] h-[400px] bg-brand-purple/20 blur-[120px] rounded-full pointer-events-none" />
        <div className="absolute bottom-0 right-0 w-[450px] h-[350px] bg-indigo-600/15 blur-[100px] rounded-full pointer-events-none" />

        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10 text-center">
          <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-slate-800/80 border border-purple-500/30 text-purple-300 text-xs font-semibold mb-6 shadow-sm">
            <Sparkles className="w-3.5 h-3.5 text-brand-purpleLight" />
            <span>AI-Powered Career Assistant &amp; ATS Resume Engine</span>
          </div>

          <h1 className="text-4xl sm:text-6xl font-extrabold tracking-tight text-white max-w-4xl mx-auto leading-tight">
            Build a Resume That <br className="hidden sm:inline" />
            <span className="bg-gradient-to-r from-purple-400 via-indigo-300 to-pink-400 bg-clip-text text-transparent">
              Gets You Noticed
            </span>
          </h1>

          <p className="mt-6 text-lg sm:text-xl text-slate-300 max-w-2xl mx-auto leading-relaxed">
            Upload your resume, unlock an explainable 0–100 ATS compatibility score, identify critical skill gaps, match against job descriptions, and download actionable audit reports.
          </p>

          {/* Action CTAs */}
          <div className="mt-10 flex flex-col sm:flex-row items-center justify-center gap-4">
            <button
              onClick={handleStart}
              className="w-full sm:w-auto px-8 py-4 rounded-xl text-base font-bold bg-brand-purple hover:bg-purple-600 text-white shadow-xl shadow-purple-600/30 transition-all hover:scale-105 flex items-center justify-center gap-2"
            >
              <span>Start Analyzing Resume</span>
              <ArrowRight className="w-5 h-5" />
            </button>

            <button
              onClick={() => {
                if (isAuthenticated) {
                  openUpload();
                } else {
                  openLogin();
                }
              }}
              className="w-full sm:w-auto px-8 py-4 rounded-xl text-base font-semibold bg-slate-800/90 hover:bg-slate-700/90 text-slate-200 border border-slate-700 transition-all flex items-center justify-center gap-2"
            >
              <FileText className="w-5 h-5 text-purple-400" />
              <span>Upload Resume</span>
            </button>
          </div>

          {/* Trust Badges */}
          <div className="mt-14 pt-8 border-t border-slate-800/80 flex flex-wrap items-center justify-center gap-8 text-xs text-slate-400">
            <div className="flex items-center gap-2">
              <ShieldCheck className="w-4 h-4 text-emerald-400" />
              <span>100% Secure &amp; Confidential</span>
            </div>
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-purple-400" />
              <span>PDF &amp; DOCX Support</span>
            </div>
            <div className="flex items-center gap-2">
              <Zap className="w-4 h-4 text-amber-400" />
              <span>Instant Explainable ATS Score</span>
            </div>
            <div className="flex items-center gap-2">
              <Award className="w-4 h-4 text-indigo-400" />
              <span>Downloadable PDF Reports</span>
            </div>
          </div>
        </div>
      </section>

      {/* Features Grid */}
      <section id="features" className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-3xl mx-auto mb-16">
            <h2 className="text-xs font-bold text-brand-purple uppercase tracking-wider">Features &amp; Capabilities</h2>
            <p className="mt-2 text-3xl font-extrabold text-navy-950 sm:text-4xl">
              Everything You Need to Beat the Resume Black Hole
            </p>
            <p className="mt-4 text-base text-slate-600">
              Built on enterprise Java Spring Boot 3 and React, providing transparent analytics rather than opaque guesses.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {/* Feature 1 */}
            <div className="p-6 rounded-2xl border border-slate-200 bg-slate-50/50 hover:bg-white hover:border-purple-300 hover:shadow-xl transition-all group">
              <div className="w-12 h-12 rounded-xl bg-purple-100 text-brand-purple flex items-center justify-center mb-5 group-hover:scale-110 transition-transform">
                <Target className="w-6 h-6" />
              </div>
              <h3 className="text-lg font-bold text-navy-950 mb-2">7-Category ATS Compatibility Score</h3>
              <p className="text-sm text-slate-600 leading-relaxed">
                Evaluates Contact Details, Structure, Skills Relevance, Keyword Density, Achievements &amp; Metrics, Education, and Readability out of 100 points.
              </p>
            </div>

            {/* Feature 2 */}
            <div className="p-6 rounded-2xl border border-slate-200 bg-slate-50/50 hover:bg-white hover:border-purple-300 hover:shadow-xl transition-all group">
              <div className="w-12 h-12 rounded-xl bg-indigo-100 text-indigo-600 flex items-center justify-center mb-5 group-hover:scale-110 transition-transform">
                <Briefcase className="w-6 h-6" />
              </div>
              <h3 className="text-lg font-bold text-navy-950 mb-2">Job Description Matcher</h3>
              <p className="text-sm text-slate-600 leading-relaxed">
                Paste any job vacancy or select preloaded industry templates. Pinpoint matched skills, missing skills, and experience alignment instantly.
              </p>
            </div>

            {/* Feature 3 */}
            <div className="p-6 rounded-2xl border border-slate-200 bg-slate-50/50 hover:bg-white hover:border-purple-300 hover:shadow-xl transition-all group">
              <div className="w-12 h-12 rounded-xl bg-pink-100 text-pink-600 flex items-center justify-center mb-5 group-hover:scale-110 transition-transform">
                <Bot className="w-6 h-6" />
              </div>
              <h3 className="text-lg font-bold text-navy-950 mb-2">AI Resume Improvement Assistant</h3>
              <p className="text-sm text-slate-600 leading-relaxed">
                Synthesizes compelling 3-sentence professional summaries, refines bullet points with the STAR method, and recommends power action verbs.
              </p>
            </div>

            {/* Feature 4 */}
            <div className="p-6 rounded-2xl border border-slate-200 bg-slate-50/50 hover:bg-white hover:border-purple-300 hover:shadow-xl transition-all group">
              <div className="w-12 h-12 rounded-xl bg-emerald-100 text-emerald-600 flex items-center justify-center mb-5 group-hover:scale-110 transition-transform">
                <TrendingUp className="w-6 h-6" />
              </div>
              <h3 className="text-lg font-bold text-navy-950 mb-2">Skill Gap &amp; Career Recommendations</h3>
              <p className="text-sm text-slate-600 leading-relaxed">
                Discover your skill coverage percentage for roles like Java Developer, Full Stack, and Data Analyst, accompanied by a step-by-step learning sequence.
              </p>
            </div>

            {/* Feature 5 */}
            <div className="p-6 rounded-2xl border border-slate-200 bg-slate-50/50 hover:bg-white hover:border-purple-300 hover:shadow-xl transition-all group">
              <div className="w-12 h-12 rounded-xl bg-amber-100 text-amber-600 flex items-center justify-center mb-5 group-hover:scale-110 transition-transform">
                <FileCheck className="w-6 h-6" />
              </div>
              <h3 className="text-lg font-bold text-navy-950 mb-2">Downloadable PDF Reports</h3>
              <p className="text-sm text-slate-600 leading-relaxed">
                Generate professional multi-page PDF audit reports containing category score graphs, extracted keyword inventories, strengths, and actionable fixes.
              </p>
            </div>

            {/* Feature 6 */}
            <div className="p-6 rounded-2xl border border-slate-200 bg-slate-50/50 hover:bg-white hover:border-purple-300 hover:shadow-xl transition-all group">
              <div className="w-12 h-12 rounded-xl bg-cyan-100 text-cyan-600 flex items-center justify-center mb-5 group-hover:scale-110 transition-transform">
                <Layers className="w-6 h-6" />
              </div>
              <h3 className="text-lg font-bold text-navy-950 mb-2">Version Comparison &amp; Checklist</h3>
              <p className="text-sm text-slate-600 leading-relaxed">
                Compare older drafts against newer versions to visualize score deltas, newly added skills, and track improvement checklists as you optimize.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* How It Works Section */}
      <section id="how" className="py-20 bg-slate-50 border-t border-slate-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-3xl mx-auto mb-16">
            <h2 className="text-xs font-bold text-brand-purple uppercase tracking-wider">Simple 4-Step Process</h2>
            <p className="mt-2 text-3xl font-extrabold text-navy-950 sm:text-4xl">
              How Smart Resume Analyzer Works
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
            <div className="text-center">
              <div className="w-14 h-14 rounded-2xl bg-navy-900 text-white font-extrabold text-xl flex items-center justify-center mx-auto mb-4 shadow-lg shadow-navy-900/20">
                1
              </div>
              <h3 className="font-bold text-base text-navy-950 mb-2">Upload Resume</h3>
              <p className="text-xs text-slate-600 leading-relaxed">
                Drop your PDF or DOCX file. Our Apache PDFBox &amp; POI engine extracts clean textual content without storing raw public files.
              </p>
            </div>

            <div className="text-center">
              <div className="w-14 h-14 rounded-2xl bg-brand-purple text-white font-extrabold text-xl flex items-center justify-center mx-auto mb-4 shadow-lg shadow-purple-600/30">
                2
              </div>
              <h3 className="font-bold text-base text-navy-950 mb-2">Parse &amp; ATS Score</h3>
              <p className="text-xs text-slate-600 leading-relaxed">
                The engine evaluates 7 distinct categories and delivers an explainable score from 0 to 100 with actionable feedback.
              </p>
            </div>

            <div className="text-center">
              <div className="w-14 h-14 rounded-2xl bg-indigo-600 text-white font-extrabold text-xl flex items-center justify-center mx-auto mb-4 shadow-lg shadow-indigo-600/30">
                3
              </div>
              <h3 className="font-bold text-base text-navy-950 mb-2">Match &amp; Optimize</h3>
              <p className="text-xs text-slate-600 leading-relaxed">
                Compare against target job listings, uncover missing keywords, and leverage AI to rephrase bullet points with STAR metrics.
              </p>
            </div>

            <div className="text-center">
              <div className="w-14 h-14 rounded-2xl bg-emerald-600 text-white font-extrabold text-xl flex items-center justify-center mx-auto mb-4 shadow-lg shadow-emerald-600/30">
                4
              </div>
              <h3 className="font-bold text-base text-navy-950 mb-2">Land Interviews</h3>
              <p className="text-xs text-slate-600 leading-relaxed">
                Download your detailed audit PDF, practice generated technical &amp; behavioral interview questions, and apply with confidence.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Benefits Section */}
      <section className="py-20 bg-white border-t border-slate-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-3xl mx-auto mb-16">
            <h2 className="text-xs font-bold text-brand-purple uppercase tracking-wider">Tailored For Every Stage</h2>
            <p className="mt-2 text-3xl font-extrabold text-navy-950 sm:text-4xl">
              Who Benefits from Smart Resume Analyzer?
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="p-8 rounded-2xl bg-gradient-to-b from-slate-50 to-white border border-slate-200">
              <div className="w-10 h-10 rounded-xl bg-purple-100 text-brand-purple flex items-center justify-center mb-4 font-bold">
                🎓
              </div>
              <h3 className="text-lg font-bold text-navy-950 mb-2">Students &amp; Freshers</h3>
              <p className="text-xs text-slate-600 leading-relaxed">
                Transition from academic projects to professional resumes. Detect missing sections, highlight technical tools, and craft strong starter project bullets.
              </p>
            </div>

            <div className="p-8 rounded-2xl bg-gradient-to-b from-slate-50 to-white border border-slate-200">
              <div className="w-10 h-10 rounded-xl bg-indigo-100 text-indigo-600 flex items-center justify-center mb-4 font-bold">
                💼
              </div>
              <h3 className="text-lg font-bold text-navy-950 mb-2">Mid-Level &amp; Senior Engineers</h3>
              <p className="text-xs text-slate-600 leading-relaxed">
                Ensure high keyword density for Senior roles. Quantify team leadership, cloud architectures, throughput improvements, and latency reductions.
              </p>
            </div>

            <div className="p-8 rounded-2xl bg-gradient-to-b from-slate-50 to-white border border-slate-200">
              <div className="w-10 h-10 rounded-xl bg-pink-100 text-pink-600 flex items-center justify-center mb-4 font-bold">
                🔄
              </div>
              <h3 className="text-lg font-bold text-navy-950 mb-2">Career Switchers</h3>
              <p className="text-xs text-slate-600 leading-relaxed">
                Identify transferable skills and unverified gaps. Follow curated skill roadmaps to pivot into Java, Full Stack, or Data Analytics domains.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* FAQ Section */}
      <section id="faq" className="py-20 bg-slate-50 border-t border-slate-200">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-xs font-bold text-brand-purple uppercase tracking-wider">Got Questions?</h2>
            <p className="mt-2 text-3xl font-extrabold text-navy-950">Frequently Asked Questions</p>
          </div>

          <div className="space-y-4">
            {faqs.map((faq, index) => (
              <div
                key={index}
                className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-sm transition-all"
              >
                <button
                  onClick={() => toggleFaq(index)}
                  className="w-full px-6 py-4 text-left flex items-center justify-between font-semibold text-navy-950 text-sm hover:text-brand-purple transition-colors"
                >
                  <span>{faq.q}</span>
                  <ChevronDown className={`w-4 h-4 text-slate-400 transition-transform ${openFaq === index ? 'rotate-180 text-brand-purple' : ''}`} />
                </button>
                {openFaq === index && (
                  <div className="px-6 pb-4 pt-1 text-xs text-slate-600 leading-relaxed border-t border-slate-100">
                    {faq.a}
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Final Call to Action */}
      <section className="py-16 gradient-brand text-white text-center">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
          <h2 className="text-3xl font-extrabold sm:text-4xl">Ready to Optimize Your Resume?</h2>
          <p className="mt-4 text-sm text-purple-200 max-w-xl mx-auto">
            Join thousands of candidates who improved their ATS scores and shortlisted interview rates.
          </p>
          <div className="mt-8 flex justify-center gap-4">
            <button
              onClick={handleStart}
              className="px-8 py-3.5 rounded-xl text-sm font-bold bg-white text-navy-900 hover:bg-slate-100 shadow-xl transition-all hover:scale-105"
            >
              Get Started Free
            </button>
          </div>
        </div>
      </section>
    </div>
  );
}
