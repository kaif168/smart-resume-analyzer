# Smart Resume Analyzer – AI-Powered Career Assistant 🚀📄

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Security-Spring%20Security%206%20+%20JWT-blue.svg)](https://spring.io/projects/spring-security)
[![React](https://img.shields.io/badge/Frontend-React%2018%20+%20Vite-61DAFB.svg)](https://react.dev/)
[![Tailwind CSS](https://img.shields.io/badge/Styling-Tailwind%20CSS-38B2AC.svg)](https://tailwindcss.com/)
[![Database](https://img.shields.io/badge/Database-MySQL%208%20%2F%20H2-4479A1.svg)](https://www.mysql.com/)
[![Apache PDFBox](https://img.shields.io/badge/Parser-Apache%20PDFBox%203.0-red.svg)](https://pdfbox.apache.org/)
[![Apache POI](https://img.shields.io/badge/Parser-Apache%20POI%205.2-blueviolet.svg)](https://poi.apache.org/)

> **Smart Resume Analyzer** is an enterprise-grade, responsive full-stack platform that helps students, freshers, and experienced professionals optimize their resumes, achieve higher ATS compatibility scores, bridge skill gaps, and prepare for technical interviews.

---

## 🌟 Key Features

### 1. 🎯 Explainable 7-Category ATS Compatibility Scoring (0–100)
Rather than an opaque number, our engine calculates a transparent score across 7 distinct pillars:
1. **Contact Information (10 pts)**: Email, phone, location, LinkedIn/GitHub links.
2. **Resume Structure & Headings (15 pts)**: Recognizes standard headings (Summary, Experience, Education, Skills, Projects).
3. **Skills Relevance & Density (20 pts)**: Evaluates technical keywords against a 180+ technology dictionary.
4. **Job Keyword Matching (20 pts)**: Density of industry architectural and engineering terms.
5. **Work Experience & Achievements (15 pts)**: Power action verbs and quantifiable metrics (`%` latency reduction, scale, numbers).
6. **Education & Certifications (10 pts)**: Academic degrees, university accreditations, and industry credentials (AWS, Oracle, etc.).
7. **Readability & Formatting (10 pts)**: Textual density, word count (350–1000 words), and single-column layout parsing.

### 2. 📑 Dual-Format Text Extraction (PDF & DOCX)
- **Apache PDFBox 3.x**: Extracts text with position sorting, handles password detection and image-only warnings.
- **Apache POI 5.x**: Parses Microsoft Word `.docx` documents via `XWPFDocument` and `XWPFWordExtractor`.
- **Structured Categorization**: Extracts candidate name, email, phone, location, summary, skills, experience years, education, projects, certifications, and languages.
- **Missing Section Warnings**: Flags missing critical sections before recruiters see them.

### 3. 💼 Job Description Matching Engine
- Match resumes against any pasted job posting or choose from pre-configured industry benchmark templates:
  - *Senior Java Developer*
  - *Full Stack Developer*
  - *Frontend Developer (React)*
  - *Backend Software Engineer*
  - *Python & Data Analyst*
  - *General Software Engineer*
- Displays:
  - Overall match percentage (0–100%) and verdict (Strong, Moderate, Low Match).
  - Matched skills pills (green) vs Missing skills pills (amber/red).
  - Relevant keywords and experience alignment notes.
  - *Explicit ethical guideline:* Missing keywords indicate tailoring opportunities, not lack of ability.

### 4. 🤖 AI Resume Assistant & STAR Project Optimizer
- **Tailored Professional Summary**: Generates 3-sentence summaries based on candidate skills and target roles.
- **STAR Method Project Evaluator**: Grades project descriptions, verifies action verbs and metrics, and outputs drop-in enhanced versions.
- **Power Action Verbs Dictionary**: Replaces passive phrases (*"worked on"*, *"helped with"*) with high-impact verbs (*"Architected"*, *"Engineered"*, *"Optimized"*).
- **Hybrid AI Architecture**: Reads `AI_API_KEY` environment variable. Automatically falls back to an **Intelligent Rule-Based Deterministic Engine** when offline or unconfigured.
- *Strict Rule:* Never fabricates qualifications, degrees, or employers.

### 5. 🚀 Skill Gap Analysis & Career Recommendations
- Visual skill coverage doughnut chart (Chart.js).
- 3-Phase step-by-step learning sequence roadmap (Foundations ➔ Architecture ➔ Production Readiness).
- Top 5 career role compatibility ranking based purely on actual resume skills.

### 6. 💬 Tailored Interview Preparation Questions
- Generates tailored **Technical**, **System Design**, and **Behavioral** interview questions based on the candidate's actual extracted skills and projects.
- Includes expandable sample answers with STAR frameworks.

### 7. 📄 Downloadable PDF Audit Reports
- Generates vector-quality, multi-page executive PDF reports using **OpenPDF**.
- Includes branding, score badges, category breakdown tables, strengths, weaknesses, and actionable next steps.

### 8. 🔄 Version Comparison & Optimization Checklist
- Compare old drafts against new revisions to visualize score improvements (`+15 pts`), newly incorporated skills, and remaining gaps.
- Interactive optimization checklist with real-time toggleable verification.

### 9. 🛡️ Enterprise Security & Administration Console
- **Spring Security 6 & Stateless JWT**: Bearer tokens, BCrypt password hashing, and role-based access (`ROLE_USER`, `ROLE_ADMIN`).
- **Data Privacy**: Users only access their own resumes and reports.
- **Admin Console**: Platform metrics, user account management (suspend/reactivate), and industry job template management.

---

## 🏗️ Technology Stack

| Layer | Technologies |
| :--- | :--- |
| **Backend** | Java 21, Spring Boot 3.2.4, Spring MVC, Spring Data JPA, Hibernate, Maven |
| **Security** | Spring Security 6, JJWT 0.12.5, BCrypt Password Encoder, CORS Filter |
| **Document Processing**| Apache PDFBox 3.0.2, Apache POI 5.2.5, OpenPDF 1.3.39 |
| **Database** | MySQL 8.0 (Production) / Embedded H2 (Instant Local Dev) |
| **Frontend** | React 18, Vite 5, Tailwind CSS 3.4, Lucide React, Chart.js, react-chartjs-2, canvas-confetti |

---

## ⚡ Quickstart Guide

### Prerequisites
- **Java 17 or Java 21**
- **Maven 3.8+**
- **Node.js 18+ & npm**
- *(Optional)* MySQL 8.0 (Embedded H2 runs automatically if MySQL is not running!)

---

### 1. Run the Spring Boot Backend

```bash
cd backend
mvn spring-boot:run
```
*The backend starts at `http://localhost:8080`. By default, it initializes an embedded H2 database with pre-seeded test data, initial admin/user accounts, and standard job templates.*

To run with **MySQL**:
```bash
# Ensure MySQL is running on port 3306 and create the database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS smart_resume_db;"

# Start with MySQL profile
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

---

### 2. Run the React Frontend

```bash
cd frontend
npm install
npm run dev
```
*The Vite frontend starts at `http://localhost:5173` with automated proxying to the Spring Boot backend (`http://localhost:8080/api`).*

---

## 🔑 Pre-Seeded Demo Credentials

| Role | Email | Password | Access Level |
| :--- | :--- | :--- | :--- |
| **Candidate / User** | `john.doe@example.com` | `password123` | Full resume upload, ATS scoring, Job matching, AI assistant |
| **Administrator** | `admin@smartresume.com` | `password123` | Platform analytics, user account management, job templates |

*(New user registration and password recovery workflows are also fully functional on the frontend!)*

---

## 📡 REST API Reference

### Authentication (`/api/auth`)
- `POST /api/auth/register` - Create new candidate account.
- `POST /api/auth/login` - Authenticate & obtain JWT bearer token.
- `POST /api/auth/forgot-password` - Request password reset token.
- `POST /api/auth/reset-password` - Update password via reset token.

### Resume Management (`/api/resumes`)
- `POST /api/resumes/upload` - Upload PDF/DOCX file with target role and version tag.
- `GET /api/resumes` - List resumes owned by current authenticated user.
- `GET /api/resumes/{id}` - Retrieve structured overview, skills, and checklist.
- `DELETE /api/resumes/{id}` - Delete resume and associated analyses.
- `GET /api/resumes/download/{fileName}` - Download uploaded file securely.
- `POST /api/resumes/{id}/checklist/{itemId}/toggle` - Check/uncheck optimization item.
- `GET /api/resumes/compare?oldResumeId={id}&newResumeId={id}` - Compare drafts.

### ATS Scoring & Analysis (`/api/analysis`)
- `POST /api/analysis/{resumeId}` - Trigger 7-pillar algorithmic evaluation.
- `GET /api/analysis/{resumeId}` - Retrieve latest analysis breakdown.
- `GET /api/analysis/history` - User's historical score tracking.

### Job Matching (`/api/job-matching`)
- `POST /api/job-matching/analyze` - Match resume against job description text.
- `GET /api/job-matching/templates` - Retrieve standard industry templates.
- `GET /api/job-matching/history/{resumeId}` - Previous match runs.

### AI Assistant & Career (`/api/ai`, `/api/recommendations`)
- `POST /api/ai/improvements` - Summary rewrite, STAR bullets, action verbs.
- `POST /api/ai/project-feedback` - STAR-method project grader and rewriter.
- `GET /api/ai/interview-questions/{resumeId}` - Tailored interview prep.
- `GET /api/recommendations/career/{resumeId}` - Role compatibility ranking.
- `GET /api/recommendations/skills/{resumeId}?targetRole={role}` - Skill gap roadmap.

### Report Generation (`/api/reports`)
- `GET /api/reports/{analysisId}/download` - Download professional multi-page PDF report.

### Admin Console (`/api/admin`) - *(Requires `ROLE_ADMIN`)*
- `GET /api/admin/dashboard` - Platform usage statistics and distributions.
- `GET /api/admin/users` - Registered users list (preserving resume privacy).
- `PUT /api/admin/users/{userId}/status` - Suspend or reactivate user account.
- `POST /api/admin/job-templates` - Add new industry benchmark template.
- `DELETE /api/admin/job-templates/{id}` - Delete job template.

---

## 📄 License
This project is open-source under the MIT License.
