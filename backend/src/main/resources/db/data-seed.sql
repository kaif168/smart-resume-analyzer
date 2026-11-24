-- Seed Data for Smart Resume Analyzer
-- BCrypt password for 'password123': $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi

INSERT INTO users (id, email, password, full_name, phone, role, bio, enabled)
VALUES 
(1, 'admin@smartresume.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'System Administrator', '+1-555-0100', 'ADMIN', 'Lead platform administrator', TRUE),
(2, 'john.doe@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'John Doe', '+1-555-0199', 'USER', 'Aspiring Full Stack Java Developer', TRUE)
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

INSERT INTO job_descriptions (id, title, role_category, department, company, description_text, required_skills_json, experience_level, is_template)
VALUES
(1, 'Senior Java Developer', 'Java Developer', 'Engineering', 'TechCorp Global', 
 'We are looking for a skilled Java Developer to build scalable enterprise cloud microservices. Must have strong core Java, Spring Boot, Hibernate/JPA, RESTful APIs, MySQL, and Docker experience.',
 '["Java", "Spring Boot", "MySQL", "REST API", "Hibernate", "Git", "Docker", "Microservices", "JUnit", "Maven"]',
 'Senior', TRUE),

(2, 'Full Stack Developer', 'Full Stack Developer', 'Product Engineering', 'InnovateSoft',
 'Seeking a Full Stack Developer proficient in React.js, Java Spring Boot, REST APIs, PostgreSQL/MySQL, Tailwind CSS, TypeScript, and CI/CD pipelines.',
 '["Java", "Spring Boot", "React", "JavaScript", "HTML", "CSS", "MySQL", "Git", "REST API", "Docker", "Tailwind CSS"]',
 'Mid-Level', TRUE),

(3, 'Frontend Developer (React)', 'Frontend Developer', 'UI/UX & Frontend', 'DesignCraft Media',
 'Looking for a passionate React Developer with modern JavaScript/TypeScript, HTML5, CSS3, Tailwind CSS, Redux/Context API, and responsive UI design expertise.',
 '["React", "JavaScript", "TypeScript", "HTML", "CSS", "Tailwind CSS", "Redux", "Git", "REST API", "Jest"]',
 'Mid-Level', TRUE),

(4, 'Backend Software Engineer', 'Backend Developer', 'Platform Infrastructure', 'CloudScale Inc',
 'Join our core platform team developing distributed backend services using Java, Spring Cloud, Kafka, Redis, SQL databases, and Kubernetes.',
 '["Java", "Spring Boot", "Microservices", "REST API", "SQL", "Redis", "Kafka", "Docker", "Git", "AWS"]',
 'Senior', TRUE),

(5, 'Python & Data Analyst', 'Data Analyst', 'Analytics & BI', 'DataVantage Labs',
 'Seeking an analytical mind skilled in Python, SQL, Pandas, NumPy, Data Visualization (Tableau/PowerBI), Statistics, and Machine Learning basics.',
 '["Python", "SQL", "Pandas", "NumPy", "Data Visualization", "Tableau", "PowerBI", "Statistics", "Machine Learning", "Git"]',
 'Entry-Level / Fresher', TRUE),

(6, 'Software Engineer', 'Software Engineer', 'Core Systems', 'Apex Systems',
 'General Software Engineer role focusing on software design, algorithms, data structures, clean code practices, testing, and team collaboration.',
 '["Java", "Python", "Data Structures", "Algorithms", "Object-Oriented Programming", "Git", "SQL", "Unit Testing", "Problem Solving"]',
 'Entry-Level', TRUE)
ON DUPLICATE KEY UPDATE title=VALUES(title);
