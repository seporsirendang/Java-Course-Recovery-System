/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package staff.menu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class AcademicReport extends javax.swing.JFrame {
    private javax.swing.JFrame parentFrame;
    private ArrayList<Student> students;
    private HashMap<String, CourseAssessment> courseInfo;   
    private HashMap<String, Integer> semesterMap;
    private final String[] TABLE_COLUMNS = {"Course ID", "Course Title", "Credit Hours", "Grade", "Grade Point"};
    private final String STUDENT_FILENAME = "student_information.txt";
    private final String ASSESSMENT_FILENAME = "course_assessment_information.txt";
    
    public AcademicReport(javax.swing.JFrame parent) {
        this.parentFrame = parent;
        this.students = new ArrayList<>();
        this.courseInfo = new HashMap<>();
        this.semesterMap = new HashMap<>();
        
        File studentFile = findFile(STUDENT_FILENAME);
        
        if (studentFile != null && studentFile.exists()) {
            this.students = loadStudentsWithAutoFix(studentFile);
        } else {
            JOptionPane.showMessageDialog(this, 
                "File '" + STUDENT_FILENAME + "' TIDAK DITEMUKAN!\n" +
                "Pastikan file ada di folder Project Root atau folder src.", 
                "Error File", JOptionPane.ERROR_MESSAGE);
        }
        
        try {
            File assessFile = findFile(ASSESSMENT_FILENAME);
            if (assessFile != null && assessFile.exists()) {
                CourseAssessmentLoader loader = new CourseAssessmentLoader();
                this.courseInfo = loader.loadAssessmentTable(assessFile.getPath());
            }
            
            if (!this.students.isEmpty()) {
                SemesterAssigner assigner = new SemesterAssigner();
                this.semesterMap = assigner.assignSemestersRandomly(this.students);
            }
        } catch (Exception e) {
            System.out.println("Auxiliary Data Error: " + e.getMessage());
        }
        
        this.courseInfo = normalizeMap(courseInfo);

        initComponents();
        applyAestheticFixes();
        this.setSize(665, 430); 
        this.setResizable(false);
        setupLogic(); 
    }
    
    public AcademicReport() {
        this(null);
    }
    
    private File findFile(String name) {
        // Cek 1: Folder Project (Root)
        File f = new File(name);
        if (f.exists()) return f;
        
        // Cek 2: Folder src
        f = new File("src/" + name);
        if (f.exists()) return f;
        
        // Cek 3: Folder Package
        f = new File("src/staff/menu/" + name);
        if (f.exists()) return f;
        
        return null; // Fail
    }
    
    private ArrayList<Student> loadStudentsWithAutoFix(File file) {
        ArrayList<Student> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // SKIP EMPTY ROW
                if (line.trim().isEmpty()) continue; 
                
                String lower = line.toLowerCase();
                if (lower.contains("studentid") || lower.contains("firstname")) {
                    continue;
                }
                
                String processedLine = line;
                if (line.contains(",") && !line.contains(";")) {
                    processedLine = line.replace(",", ";");
                }
                
                String[] parts = processedLine.split(";", -1);
                if (parts.length < 24) {
                    StringBuilder sb = new StringBuilder(processedLine);
                    int missing = 24 - parts.length;
                    for (int i = 0; i < missing; i++) sb.append(";");
                    if (sb.toString().endsWith(";")) sb.append("0.0");
                    processedLine = sb.toString();
                }
                
                try {
                    Student s = new Student(processedLine);
                    list.add(s);
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
        }
        return list;
    }
    
    private void applyAestheticFixes() {
        this.getContentPane().setBackground(new java.awt.Color(244, 244, 244));
        
        JButton[] buttons = {jButton1, LoadReport, ExportPDF, SendEmail};
        for(JButton btn : buttons) {
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.setBackground(new java.awt.Color(129, 182, 129));
            btn.setForeground(new java.awt.Color(242, 242, 242));
        }
        
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setColumnIdentifiers(TABLE_COLUMNS);
        
        jTable1.getTableHeader().setResizingAllowed(false);
        jTable1.getTableHeader().setReorderingAllowed(false);
        
        jScrollPane2.setPreferredSize(new Dimension(234, 224));
        jScrollPane1.setPreferredSize(new Dimension(373, 224));
        this.pack();
    }
    
    private void setupLogic() {
        DefaultComboBoxModel<String> semModel = new DefaultComboBoxModel<>();
        semModel.addElement("All Semesters");
        semModel.addElement("Semester 1");
        semModel.addElement("Semester 2");
        StudentSemester.setModel(semModel);

        updateStudentDropdown();
        
        jTextArea1.setEditable(false);
        jTextArea1.setFont(new Font("Monospaced", Font.PLAIN, 12));
        jTextArea1.setLineWrap(true); 
        jTextArea1.setWrapStyleWord(true);
    }
    
    private void updateStudentDropdown() {
        DefaultComboBoxModel<String> studentModel = new DefaultComboBoxModel<>();
        
        int selectedSemIndex = StudentSemester.getSelectedIndex(); // 0=All, 1=Sem1, 2=Sem2
        
        boolean hasStudents = false;

        if (students != null && !students.isEmpty()) {
            for (Student s : students) {
                // Get student's semester (Default to 1 if missing)
                int studentSem = semesterMap.getOrDefault(s.getStudentId(), 1);
                
                // Filter Logic: Show if "All" is selected OR semester matches
                if (selectedSemIndex == 0 || selectedSemIndex == studentSem) {
                    studentModel.addElement(s.getStudentId() + " - " + s.getFirstName() + " " + s.getLastName());
                    hasStudents = true;
                }
            }
        }

        if (!hasStudents) studentModel.addElement("-- No Students Found --");
        
        StudentName.setModel(studentModel);
        
        // Enable/Disable button
        boolean enable = hasStudents;
        LoadReport.setEnabled(enable);
        ExportPDF.setEnabled(enable);
        SendEmail.setEnabled(enable);
    }
    
    private Student getSelectedStudent() {
        if (students.isEmpty() || StudentName.getSelectedIndex() < 0) return null;
        String selectedStr = (String) StudentName.getSelectedItem();
        if (selectedStr.startsWith("--")) return null;
        
        String id = selectedStr.split(" - ")[0].trim();
        return students.stream().filter(s -> s.getStudentId().equals(id)).findFirst().orElse(null);
    }
    
    private void loadSelectedStudent() {
        Student s = getSelectedStudent();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Please select a student first.");
            return;
        }
        
        int selectedSemIndex = StudentSemester.getSelectedIndex(); // 0=All, 1=Sem1, 2=Sem2
        int studentSem = semesterMap.getOrDefault(s.getStudentId(), 1);

        if (selectedSemIndex != 0 && selectedSemIndex != studentSem) {
            JOptionPane.showMessageDialog(this,
                    "This student belongs to Semester " + studentSem,
                    "Semester Mismatch",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        StringBuilder info = new StringBuilder();
        info.append("Student Name : ").append(s.getFirstName()).append(" ").append(s.getLastName()).append("\n");
        info.append("Student ID   : ").append(s.getStudentId()).append("\n");
        info.append("Program      : ").append(s.getMajor()).append("\n");
        info.append("-------------------------------\n");
        
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        double totalPoints = 0;
        int totalCredits = 0;

        for (Student.CourseResult c : s.getCourseResults()) { 
            
            String key = normalize(c.courseName);
            CourseAssessment ca = courseInfo.get(key);

            String code = (ca != null) ? ca.getCourseID() : "N/A";
            int credits = (ca != null) ? ca.getCredits() : 3; // Default 3 if not found
            
            double gradePoint = GradeUtil.toPoint(c.grade);

            model.addRow(new Object[]{
                code,
                c.courseName,
                credits,
                c.grade,
                String.format("%.2f", gradePoint)
            });

            totalPoints += gradePoint * credits;
            totalCredits += credits;
        }

        double cgpa = (totalCredits == 0) ? 0.0 : totalPoints / totalCredits;
        info.append(String.format("Cumulative GPA (CGPA): %.2f", cgpa));
        jTextArea1.setText(info.toString());
    }
    
    private HashMap<String, CourseAssessment> normalizeMap(HashMap<String, CourseAssessment> map) {
        HashMap<String, CourseAssessment> result = new HashMap<>();
        if (map != null) {
            for (String key : map.keySet()) {
                result.put(normalize(key), map.get(key));
            }
        }
        return result;
    }
    
    private String normalize(String x) {
        return x == null ? "" : x.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        update_header = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        StudentSemester = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        StudentName = new javax.swing.JComboBox<>();
        LoadReport = new javax.swing.JButton();
        ExportPDF = new javax.swing.JButton();
        SendEmail = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        update_header.setBackground(new java.awt.Color(129, 182, 129));

        jLabel1.setFont(new java.awt.Font("MS Gothic", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(242, 242, 242));
        jLabel1.setText("Academic Performance Report");

        jButton1.setBackground(new java.awt.Color(129, 182, 129));
        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(242, 242, 242));
        jButton1.setText("Back");
        jButton1.setBorderPainted(false);
        jButton1.setOpaque(true);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout update_headerLayout = new javax.swing.GroupLayout(update_header);
        update_header.setLayout(update_headerLayout);
        update_headerLayout.setHorizontalGroup(
            update_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(update_headerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        update_headerLayout.setVerticalGroup(
            update_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, update_headerLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(update_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        StudentSemester.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        StudentSemester.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StudentSemesterActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("MS Gothic", 1, 12)); // NOI18N
        jLabel3.setText("Student:");

        jLabel2.setFont(new java.awt.Font("MS Gothic", 1, 12)); // NOI18N
        jLabel2.setText("Semester:");

        StudentName.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        StudentName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StudentNameActionPerformed(evt);
            }
        });

        LoadReport.setText("Load Report");
        LoadReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadReportActionPerformed(evt);
            }
        });

        ExportPDF.setText("Export to PDF");
        ExportPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportPDFActionPerformed(evt);
            }
        });

        SendEmail.setText("Send to Email");
        SendEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendEmailActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(update_header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(LoadReport)
                .addGap(18, 18, 18)
                .addComponent(ExportPDF)
                .addGap(18, 18, 18)
                .addComponent(SendEmail)
                .addGap(150, 150, 150))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(StudentSemester, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(StudentName, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(74, 74, 74))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(update_header, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(StudentSemester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(StudentName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LoadReport)
                    .addComponent(ExportPDF)
                    .addComponent(SendEmail))
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if (parentFrame != null) {
            parentFrame.setVisible(true);
        }
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void StudentSemesterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StudentSemesterActionPerformed
        // TODO add your handling code here:
        updateStudentDropdown();
    }//GEN-LAST:event_StudentSemesterActionPerformed

    private void StudentNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StudentNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_StudentNameActionPerformed

    private void LoadReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadReportActionPerformed
        // TODO add your handling code here:
        loadSelectedStudent();
    }//GEN-LAST:event_LoadReportActionPerformed

    private void ExportPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportPDFActionPerformed
        // TODO add your handling code here:
        Student s = getSelectedStudent();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Select a student and load report first.");
            return;
        }
        
        String fileName = s.getStudentId() + "_Report.pdf";
        AcademicReportGenerator.generatePDF(s, courseInfo, semesterMap, fileName);
        JOptionPane.showMessageDialog(this, "PDF exported successfully: " + fileName);
    }//GEN-LAST:event_ExportPDFActionPerformed

    private void SendEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendEmailActionPerformed
        // TODO add your handling code here:
        Student s = getSelectedStudent();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Select a student first.");
            return;
        }
        
        String fileName = s.getStudentId() + "_Report.pdf";
        AcademicReportGenerator.generatePDF(s, courseInfo, semesterMap, fileName);
        
        Email emailService = new Email.EmailImpl();
        emailService.sendWithAttachment(
                "clairineallegram@gmail.com", //change email
                "Academic Report - " + s.getFullName(),
                "Hi " + s.getFullName() + ",\nPlease find attached your Academic Report.",
                fileName
        );
    }//GEN-LAST:event_SendEmailActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(AcademicReport.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new AcademicReport().setVisible(true));
    }
  

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ExportPDF;
    private javax.swing.JButton LoadReport;
    private javax.swing.JButton SendEmail;
    private javax.swing.JComboBox<String> StudentName;
    private javax.swing.JComboBox<String> StudentSemester;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JPanel update_header;
    // End of variables declaration//GEN-END:variables
}
