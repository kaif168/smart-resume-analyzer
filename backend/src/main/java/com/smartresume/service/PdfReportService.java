package com.smartresume.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.smartresume.entity.ExtractedSkill;
import com.smartresume.entity.Resume;
import com.smartresume.entity.ResumeAnalysis;
import com.smartresume.exception.ResourceNotFoundException;
import com.smartresume.repository.ExtractedSkillRepository;
import com.smartresume.repository.ResumeAnalysisRepository;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfReportService {

    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final ExtractedSkillRepository extractedSkillRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PdfReportService(ResumeAnalysisRepository resumeAnalysisRepository,
                            ExtractedSkillRepository extractedSkillRepository) {
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.extractedSkillRepository = extractedSkillRepository;
    }

    public byte[] generateAnalysisPdfReport(Long analysisId) {
        ResumeAnalysis analysis = resumeAnalysisRepository.findById(analysisId)
                .orElseThrow(() -> new ResourceNotFoundException("Analysis report not found with id: " + analysisId));

        Resume resume = analysis.getResume();
        List<ExtractedSkill> skills = extractedSkillRepository.findAllByResumeId(resume.getId());

        List<String> strengths = parseJson(analysis.getStrengthsJson());
        List<String> weaknesses = parseJson(analysis.getWeaknessesJson());
        List<String> suggestions = parseJson(analysis.getActionableSuggestionsJson());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 40, 40);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Brand Header Banner
            Color navy = new Color(15, 23, 42); // #0F172A
            Color purple = new Color(124, 58, 237); // #7C3AED
            Color lightBg = new Color(248, 250, 252);
            Color textDark = new Color(30, 41, 59);

            Font brandFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.WHITE);
            Font subBrandFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(226, 232, 240));
            Font sectionTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, purple);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, textDark);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, textDark);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.GRAY);

            // 1. Header Table
            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);

            PdfPCell bannerCell = new PdfPCell();
            bannerCell.setBackgroundColor(navy);
            bannerCell.setPadding(16);
            bannerCell.setBorder(Rectangle.NO_BORDER);

            Paragraph titlePara = new Paragraph("SMART RESUME ANALYZER", brandFont);
            Paragraph subPara = new Paragraph("AI-Powered ATS Compatibility & Career Audit Report", subBrandFont);
            bannerCell.addElement(titlePara);
            bannerCell.addElement(subPara);
            headerTable.addCell(bannerCell);
            document.add(headerTable);

            document.add(new Paragraph(" "));

            // 2. Candidate Information & Overall Score Card
            PdfPTable metaTable = new PdfPTable(2);
            metaTable.setWidthPercentage(100);
            metaTable.setWidths(new float[]{65, 35});

            PdfPCell candidateCell = new PdfPCell();
            candidateCell.setBackgroundColor(lightBg);
            candidateCell.setPadding(12);
            candidateCell.setBorderColor(new Color(226, 232, 240));

            Paragraph candidateInfo = new Paragraph();
            candidateInfo.add(new Chunk("Candidate: ", labelFont));
            candidateInfo.add(new Chunk(resume.getCandidateName() != null ? resume.getCandidateName() : "Applicant", bodyFont));
            candidateInfo.add(Chunk.NEWLINE);

            candidateInfo.add(new Chunk("Email: ", labelFont));
            candidateInfo.add(new Chunk(resume.getCandidateEmail() != null ? resume.getCandidateEmail() : "N/A", bodyFont));
            candidateInfo.add(Chunk.NEWLINE);

            candidateInfo.add(new Chunk("Target Role: ", labelFont));
            candidateInfo.add(new Chunk(resume.getTargetRole() != null ? resume.getTargetRole() : "Software Engineer", bodyFont));
            candidateInfo.add(Chunk.NEWLINE);

            candidateInfo.add(new Chunk("Date: ", labelFont));
            candidateInfo.add(new Chunk(analysis.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")), bodyFont));

            candidateCell.addElement(candidateInfo);
            metaTable.addCell(candidateCell);

            // Score Box
            PdfPCell scoreCell = new PdfPCell();
            scoreCell.setBackgroundColor(purple);
            scoreCell.setPadding(12);
            scoreCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            scoreCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            scoreCell.setBorder(Rectangle.NO_BORDER);

            Paragraph scoreLabel = new Paragraph("OVERALL ATS SCORE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE));
            scoreLabel.setAlignment(Element.ALIGN_CENTER);

            Paragraph scoreNum = new Paragraph(analysis.getOverallScore() + " / 100", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.WHITE));
            scoreNum.setAlignment(Element.ALIGN_CENTER);

            String rating = analysis.getOverallScore() >= 85 ? "EXCELLENT" : (analysis.getOverallScore() >= 70 ? "GOOD" : "NEEDS IMPROVEMENT");
            Paragraph ratingPara = new Paragraph(rating, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(243, 232, 255)));
            ratingPara.setAlignment(Element.ALIGN_CENTER);

            scoreCell.addElement(scoreLabel);
            scoreCell.addElement(scoreNum);
            scoreCell.addElement(ratingPara);
            metaTable.addCell(scoreCell);

            document.add(metaTable);
            document.add(new Paragraph(" "));

            // 3. Category Breakdown Table
            Paragraph secBreakdown = new Paragraph("CATEGORY SCORE BREAKDOWN", sectionTitleFont);
            secBreakdown.setSpacingAfter(6);
            document.add(secBreakdown);

            PdfPTable scoreTable = new PdfPTable(3);
            scoreTable.setWidthPercentage(100);
            scoreTable.setWidths(new float[]{45, 25, 30});

            addHeaderCell(scoreTable, "Category", navy);
            addHeaderCell(scoreTable, "Score Obtained", navy);
            addHeaderCell(scoreTable, "Max Possible", navy);

            addRowCell(scoreTable, "1. Contact Information", analysis.getContactScore() + " pts", "10 pts", false);
            addRowCell(scoreTable, "2. Resume Structure & Headings", analysis.getStructureScore() + " pts", "15 pts", true);
            addRowCell(scoreTable, "3. Skills Relevance", analysis.getSkillsScore() + " pts", "20 pts", false);
            addRowCell(scoreTable, "4. Job Keyword Matching", analysis.getKeywordScore() + " pts", "20 pts", true);
            addRowCell(scoreTable, "5. Work Experience & Achievements", analysis.getExperienceScore() + " pts", "15 pts", false);
            addRowCell(scoreTable, "6. Education & Certifications", analysis.getEducationScore() + " pts", "10 pts", true);
            addRowCell(scoreTable, "7. Readability & Formatting", analysis.getReadabilityScore() + " pts", "10 pts", false);

            document.add(scoreTable);
            document.add(new Paragraph(" "));

            // 4. Extracted Skills Section
            Paragraph secSkills = new Paragraph("EXTRACTED SKILLS & KEYWORDS (" + skills.size() + " DETECTED)", sectionTitleFont);
            secSkills.setSpacingAfter(6);
            document.add(secSkills);

            String skillsJoined = skills.stream()
                    .map(ExtractedSkill::getSkillName)
                    .collect(Collectors.joining(", "));
            if (skillsJoined.isEmpty()) skillsJoined = "None clearly identified.";

            PdfPCell skillsCell = new PdfPCell(new Phrase(skillsJoined, bodyFont));
            skillsCell.setBackgroundColor(lightBg);
            skillsCell.setPadding(8);
            skillsCell.setBorderColor(new Color(226, 232, 240));

            PdfPTable skillsTable = new PdfPTable(1);
            skillsTable.setWidthPercentage(100);
            skillsTable.addCell(skillsCell);
            document.add(skillsTable);

            document.add(new Paragraph(" "));

            // 5. Strengths & Actionable Recommendations
            Paragraph secAction = new Paragraph("ACTIONABLE RECOMMENDATIONS & STRENGTHS", sectionTitleFont);
            secAction.setSpacingAfter(6);
            document.add(secAction);

            PdfPTable feedbackTable = new PdfPTable(1);
            feedbackTable.setWidthPercentage(100);

            PdfPCell fbCell = new PdfPCell();
            fbCell.setBackgroundColor(Color.WHITE);
            fbCell.setPadding(10);
            fbCell.setBorderColor(new Color(203, 213, 225));

            Paragraph fbPara = new Paragraph();
            fbPara.add(new Chunk("Key Strengths:\n", labelFont));
            for (String str : strengths) {
                fbPara.add(new Chunk("  + " + str + "\n", bodyFont));
            }
            fbPara.add(Chunk.NEWLINE);

            fbPara.add(new Chunk("Areas to Improve & Action Items:\n", labelFont));
            for (String sug : suggestions) {
                fbPara.add(new Chunk("  * " + sug + "\n", bodyFont));
            }

            fbCell.addElement(fbPara);
            feedbackTable.addCell(fbCell);
            document.add(feedbackTable);

            document.add(new Paragraph(" "));

            // 6. Disclaimer Footer
            Paragraph disclaimer = new Paragraph(
                    "Disclaimer: This report provides an algorithmic estimate based on industry ATS practices. " +
                    "It is designed to assist candidates with formatting, keyword optimization, and resume structuring.",
                    smallFont
            );
            disclaimer.setAlignment(Element.ALIGN_CENTER);
            document.add(disclaimer);

            document.close();
            return out.toByteArray();

        } catch (Exception ex) {
            throw new RuntimeException("Error rendering PDF analysis report: " + ex.getMessage(), ex);
        }
    }

    private void addHeaderCell(PdfPTable table, String text, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE)));
        cell.setBackgroundColor(bg);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
    }

    private void addRowCell(PdfPTable table, String col1, String col2, String col3, boolean alt) {
        Color bg = alt ? new Color(248, 250, 252) : Color.WHITE;
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(30, 41, 59));

        PdfPCell c1 = new PdfPCell(new Phrase(col1, font));
        c1.setBackgroundColor(bg);
        c1.setPadding(5);
        table.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase(col2, font));
        c2.setBackgroundColor(bg);
        c2.setPadding(5);
        table.addCell(c2);

        PdfPCell c3 = new PdfPCell(new Phrase(col3, font));
        c3.setBackgroundColor(bg);
        c3.setPadding(5);
        table.addCell(c3);
    }

    private List<String> parseJson(String json) {
        if (json == null || json.isEmpty()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
