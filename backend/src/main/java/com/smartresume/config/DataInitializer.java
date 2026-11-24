package com.smartresume.config;

import com.smartresume.entity.JobDescription;
import com.smartresume.entity.Role;
import com.smartresume.entity.User;
import com.smartresume.repository.JobDescriptionRepository;
import com.smartresume.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           JobDescriptionRepository jobDescriptionRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Seed default Admin if not present
        if (!userRepository.existsByEmail("admin@smartresume.com")) {
            User admin = new User(
                    "admin@smartresume.com",
                    passwordEncoder.encode("password123"),
                    "System Administrator",
                    Role.ADMIN
            );
            admin.setPhone("+1-555-0100");
            admin.setBio("Platform Chief Administrator and System Auditor");
            userRepository.save(admin);
        }

        // Seed default Demo User if not present
        if (!userRepository.existsByEmail("john.doe@example.com")) {
            User user = new User(
                    "john.doe@example.com",
                    passwordEncoder.encode("password123"),
                    "John Doe",
                    Role.USER
            );
            user.setPhone("+1-555-0199");
            user.setBio("Full Stack Java & Cloud Developer");
            userRepository.save(user);
        }

        // Seed Industry Standard Job Roles if empty
        if (jobDescriptionRepository.count() == 0) {
            seedJobTemplates();
        }
    }

    private void seedJobTemplates() {
        List<JobDescription> templates = Arrays.asList(
            createJob("Senior Java Developer", "Java Developer", "Engineering", "TechCorp Global",
                "We are seeking an experienced Java Developer to engineer resilient enterprise cloud microservices. Required skills include Java 17+, Spring Boot, Hibernate/JPA, RESTful APIs, MySQL, and Docker.",
                "[\"Java\", \"Spring Boot\", \"MySQL\", \"REST API\", \"Hibernate\", \"Git\", \"Docker\", \"Microservices\", \"JUnit\", \"Maven\"]",
                "Senior"),

            createJob("Full Stack Developer", "Full Stack Developer", "Product Engineering", "InnovateSoft",
                "Looking for a Full Stack Developer adept in React.js, Java Spring Boot, REST APIs, SQL, Tailwind CSS, TypeScript, and CI/CD pipelines.",
                "[\"Java\", \"Spring Boot\", \"React\", \"JavaScript\", \"HTML\", \"CSS\", \"MySQL\", \"Git\", \"REST API\", \"Docker\", \"Tailwind CSS\"]",
                "Mid-Level"),

            createJob("Frontend Developer (React)", "Frontend Developer", "UI/UX & Frontend", "DesignCraft Media",
                "Looking for a passionate React Developer with modern JavaScript/TypeScript, HTML5, CSS3, Tailwind CSS, Redux, and responsive UI design expertise.",
                "[\"React\", \"JavaScript\", \"TypeScript\", \"HTML\", \"CSS\", \"Tailwind CSS\", \"Redux\", \"Git\", \"REST API\", \"Jest\"]",
                "Mid-Level"),

            createJob("Backend Software Engineer", "Backend Developer", "Platform Infrastructure", "CloudScale Inc",
                "Join our platform team developing distributed backend services using Java, Spring Cloud, Kafka, Redis, SQL databases, and Kubernetes.",
                "[\"Java\", \"Spring Boot\", \"Microservices\", \"REST API\", \"SQL\", \"Redis\", \"Kafka\", \"Docker\", \"Git\", \"AWS\"]",
                "Senior"),

            createJob("Python & Data Analyst", "Data Analyst", "Analytics & BI", "DataVantage Labs",
                "Seeking an analytical professional skilled in Python, SQL, Pandas, NumPy, Data Visualization (Tableau/PowerBI), Statistics, and Machine Learning.",
                "[\"Python\", \"SQL\", \"Pandas\", \"NumPy\", \"Data Visualization\", \"Tableau\", \"PowerBI\", \"Statistics\", \"Machine Learning\", \"Git\"]",
                "Entry-Level / Fresher"),

            createJob("Software Engineer", "Software Engineer", "Core Systems", "Apex Systems",
                "General Software Engineer role focusing on software design, algorithms, data structures, clean code practices, testing, and team collaboration.",
                "[\"Java\", \"Python\", \"Data Structures\", \"Algorithms\", \"Object-Oriented Programming\", \"Git\", \"SQL\", \"Unit Testing\", \"Problem Solving\"]",
                "Entry-Level")
        );

        jobDescriptionRepository.saveAll(templates);
    }

    private JobDescription createJob(String title, String roleCategory, String department, String company,
                                     String desc, String requiredSkillsJson, String experienceLevel) {
        JobDescription jd = new JobDescription();
        jd.setTitle(title);
        jd.setRoleCategory(roleCategory);
        jd.setDepartment(department);
        jd.setCompany(company);
        jd.setDescriptionText(desc);
        jd.setRequiredSkillsJson(requiredSkillsJson);
        jd.setExperienceLevel(experienceLevel);
        jd.setTemplate(true);
        return jd;
    }
}
