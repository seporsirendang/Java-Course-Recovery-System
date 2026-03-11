/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package staff.menu;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.util.HashMap;

public class AcademicReportGenerator {

    public static void generatePDF(Student s,
                                   HashMap<String, CourseAssessment> courseInfo,
                                   HashMap<String, Integer> semesterMap,
                                   String outputPath) {

        try {
            Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, new FileOutputStream(outputPath));
            doc.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Academic Performance Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);
            doc.add(new Paragraph("\n"));

            int sem = semesterMap.getOrDefault(s.getStudentId(), 1);

            Font labelBold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normal = new Font(Font.FontFamily.HELVETICA, 12);

            PdfPTable info = new PdfPTable(3);
            info.setWidthPercentage(50);
            info.setSpacingAfter(20);
            info.setHorizontalAlignment(Element.ALIGN_LEFT);
            info.setWidths(new float[]{2.5f, 0.5f, 4f});

            addInfoRow(info, "Student Name", s.getFirstName() + " " + s.getLastName(), labelBold, normal);
            addInfoRow(info, "Student ID", s.getStudentId(), labelBold, normal);
            addInfoRow(info, "Program", s.getMajor(), labelBold, normal);
            addInfoRow(info, "Semester", "Semester " + sem, labelBold, normal);

            doc.add(info);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.0f, 4.2f, 1.6f, 1.4f, 1.8f});

            addHeader(table, "Course ID");
            addHeader(table, "Course Title");
            addHeader(table, "Credit Hours");
            addHeader(table, "Grade");
            addHeader(table, "Grade Point");

            double totalPoints = 0;
            int totalCredits = 0;

            for (Student.CourseResult c : s.getCourseResults()) {

                // Normalize name for lookup
                String key = normalize(c.courseName);
                CourseAssessment ca = courseInfo.get(key);

                // Default values if not found in Map
                String code = (ca != null) ? ca.getCourseID() : "N/A";
                int credits = (ca != null) ? ca.getCredits() : 3; // Default to 3
                
                double gp = GradeUtil.toPoint(c.grade);

                table.addCell(center(code));
                table.addCell(c.courseName);
                table.addCell(center(String.valueOf(credits)));
                table.addCell(center(c.grade));
                table.addCell(center(String.format("%.2f", gp)));

                totalPoints += gp * credits;
                totalCredits += credits;
            }

            doc.add(table);

            double cgpa = (totalCredits == 0) ? 0.0 : totalPoints / totalCredits;
            Paragraph cgpaLine = new Paragraph("\nCumulative GPA (CGPA): " + String.format("%.2f", cgpa), labelBold);
            doc.add(cgpaLine);

            doc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addInfoRow(PdfPTable t, String label, String value, Font bold, Font normal) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, bold));
        c1.setBorder(Rectangle.NO_BORDER);
        t.addCell(c1);

        PdfPCell colon = new PdfPCell(new Phrase(":"));
        colon.setBorder(Rectangle.NO_BORDER);
        t.addCell(colon);

        PdfPCell c2 = new PdfPCell(new Phrase(value, normal));
        c2.setBorder(Rectangle.NO_BORDER);
        t.addCell(c2);
    }

    private static void addHeader(PdfPTable table, String txt) {
        Font f = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        PdfPCell h = new PdfPCell(new Phrase(txt, f));
        h.setHorizontalAlignment(Element.ALIGN_CENTER);
        h.setBackgroundColor(BaseColor.LIGHT_GRAY);
        h.setPadding(6);
        table.addCell(h);
    }

    private static PdfPCell center(String text) {
        PdfPCell c = new PdfPCell(new Phrase(text));
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        return c;
    }

    private static String normalize(String x) {
        return x == null ? "" : x.trim().replaceAll("\\s+", " ").toLowerCase();
    }
}