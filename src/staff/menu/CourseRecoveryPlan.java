/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package staff.menu;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class CourseRecoveryPlan extends javax.swing.JFrame {
    private javax.swing.JFrame parentFrame; 
    private List<Student> allStudents; 
    private CourseRecoveryManager recoveryManager;
    private List<Student> studentsNeedingRecovery; 
    private Student selectedStudent = null;
    private List<String> currentStudentFailedCourses;
    private final String FILE_PATH = "student_information.txt";
    private final String[] TABLE_HEADERS = {"Plan ID", "Course", "Study Week", "Task", "Status", "Deadline"};
   
    private class AddPlanDialog extends JDialog {
        private boolean confirmed = false;
        private String selectedCourse, studyWeek, task, deadlineStr;
        private JComboBox<String> courseComboBox;
        private JTextField weekField, taskField, deadlineField;

        public AddPlanDialog(JFrame parent, List<String> availableCourses) {
            super(parent, "Add New Recovery Plan", true);
            setLayout(new BorderLayout());

            JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
            courseComboBox = new JComboBox<>(availableCourses.toArray(new String[0]));
            weekField = new JTextField();
            taskField = new JTextField();
            deadlineField = new JTextField("YYYY-MM-DD");

            panel.add(new JLabel("Failed Course:")); panel.add(courseComboBox);
            panel.add(new JLabel("Study Week (e.g. Week 1):")); panel.add(weekField);
            panel.add(new JLabel("Task Description:")); panel.add(taskField);
            panel.add(new JLabel("Deadline (YYYY-MM-DD):")); panel.add(deadlineField);

            javax.swing.JButton okButton = new javax.swing.JButton("Add Milestone");
            javax.swing.JButton cancelButton = new javax.swing.JButton("Cancel");

            okButton.addActionListener(e -> {
                if (weekField.getText().trim().isEmpty() || taskField.getText().trim().isEmpty() || deadlineField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields must be filled.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                selectedCourse = (String) courseComboBox.getSelectedItem();
                studyWeek = weekField.getText().trim();
                task = taskField.getText().trim();
                deadlineStr = deadlineField.getText().trim();
                confirmed = true;
                dispose();
            });
            cancelButton.addActionListener(e -> dispose());

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(okButton); buttonPanel.add(cancelButton);
            add(panel, BorderLayout.CENTER); add(buttonPanel, BorderLayout.SOUTH);
            pack(); setLocationRelativeTo(parent);
        }
        public boolean isConfirmed() { return confirmed; }
        public String getSelectedCourse() { return selectedCourse; }
        public String getStudyWeek() { return studyWeek; }
        public String getTask() { return task; }
        public String getDeadlineStr() { return deadlineStr; }
    }

    private class UpdatePlanDialog extends JDialog {
        private boolean confirmed = false;
        private String newStatus, newTask, newDeadlineStr;
        private JTextField taskField, deadlineField;
        private JComboBox<String> statusComboBox;

        public UpdatePlanDialog(JFrame parent, String id, String currentTask, String currentStatus, String currentDeadline) {
            super(parent, "Update Plan ID: " + id, true);
            setLayout(new BorderLayout());

            JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
            taskField = new JTextField(currentTask);
            deadlineField = new JTextField(currentDeadline);
            String[] statuses = {"Pending", "Completed", "In Progress"};
            statusComboBox = new JComboBox<>(statuses);
            statusComboBox.setSelectedItem(currentStatus);

            panel.add(new JLabel("Recovery ID:")); panel.add(new JLabel(id));
            panel.add(new JLabel("Edit Task:")); panel.add(taskField);
            panel.add(new JLabel("Edit Status:")); panel.add(statusComboBox);
            panel.add(new JLabel("Edit Deadline:")); panel.add(deadlineField);

            javax.swing.JButton okButton = new javax.swing.JButton("Save Update");
            javax.swing.JButton cancelButton = new javax.swing.JButton("Cancel");

            okButton.addActionListener(e -> {
                newTask = taskField.getText().trim();
                newStatus = (String) statusComboBox.getSelectedItem();
                newDeadlineStr = deadlineField.getText().trim();
                if (newTask.isEmpty() || newStatus.isEmpty() || newDeadlineStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                confirmed = true;
                dispose();
            });
            cancelButton.addActionListener(e -> dispose());

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(okButton); buttonPanel.add(cancelButton);
            add(panel, BorderLayout.CENTER); add(buttonPanel, BorderLayout.SOUTH);
            pack(); setLocationRelativeTo(parent);
        }
        public boolean isConfirmed() { return confirmed; }
        public String getNewTask() { return newTask; }
        public String getNewStatus() { return newStatus; }
        public String getNewDeadlineStr() { return newDeadlineStr; }
    }
    
    public CourseRecoveryPlan(javax.swing.JFrame parent) {
        this.parentFrame = parent;
        this.allStudents = EligibilityCheck.loadStudentsFromFile(FILE_PATH); 
        if (this.allStudents == null) this.allStudents = new ArrayList<>();
        
        this.recoveryManager = new CourseRecoveryManager();
        this.recoveryManager.generatePlansForFailedCourses(allStudents);
        
        initComponents(); 
        applyAestheticFixes();
        setupCourseRecoveryTab();
    }
    
    public CourseRecoveryPlan() {
        this(null);
    }

    private void applyAestheticFixes() {
        this.getContentPane().setBackground(new java.awt.Color(244, 244, 244)); 
        
        forceButtonColor(jButton1); // Back
        forceButtonColor(AddButton); // Add
        forceButtonColor(UpdateButton); // Update
        forceButtonColor(RemoveButton); // Remove
        forceButtonColor(EmailButton); // Email
    }
    
    private void forceButtonColor(javax.swing.JButton button) {
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true); 
        button.setBackground(new java.awt.Color(129, 182, 129)); 
        button.setForeground(new java.awt.Color(242, 242, 242));
    }
    
    private void setupCourseRecoveryTab() {
        this.studentsNeedingRecovery = this.allStudents.stream()
            .filter(s -> s.countFailedCourses() > 0)
            .collect(Collectors.toList());

        String[] studentNames;
        if (this.studentsNeedingRecovery.isEmpty()) {
            studentNames = new String[]{"-- No students need recovery --"};
            EligibilityDropDown.setEnabled(false);
        } else {
            studentNames = this.studentsNeedingRecovery.stream()
                .map(s -> s.getStudentId() + " - " + s.getFullName())
                .toArray(String[]::new);
            EligibilityDropDown.setEnabled(true);
        }

        EligibilityDropDown.setModel(new DefaultComboBoxModel<>(studentNames));
        
        for(ActionListener al : EligibilityDropDown.getActionListeners()) EligibilityDropDown.removeActionListener(al);
        EligibilityDropDown.addActionListener(this::EligibilityDropDownActionPerformed);
        
        for(ActionListener al : AddButton.getActionListeners()) AddButton.removeActionListener(al);
        AddButton.addActionListener(e -> AddButtonActionPerformed(e));

        for(ActionListener al : UpdateButton.getActionListeners()) UpdateButton.removeActionListener(al);
        UpdateButton.addActionListener(e -> UpdateButtonActionPerformed(e));

        for(ActionListener al : RemoveButton.getActionListeners()) RemoveButton.removeActionListener(al);
        RemoveButton.addActionListener(e -> RemoveButtonActionPerformed(e));
        
        for(ActionListener al : EmailButton.getActionListeners()) EmailButton.removeActionListener(al);
        EmailButton.addActionListener(e -> EmailButtonActionPerformed(e));

        for(ActionListener al : jButton1.getActionListeners()) jButton1.removeActionListener(al);
        jButton1.addActionListener(e -> jButton1ActionPerformed(e));

        if (!studentsNeedingRecovery.isEmpty()) {
            EligibilityDropDown.setSelectedIndex(0);
        } else {
            updateRecoveryTable(new ArrayList<>(), new ArrayList<>());
        }
    }
    
    private void StudentNameActionPerformed(java.awt.event.ActionEvent evt) {
        if (EligibilityDropDown.getSelectedItem() == null || studentsNeedingRecovery.isEmpty()) return;
        
        String fullSelection = (String) EligibilityDropDown.getSelectedItem();
        if (fullSelection.startsWith("--")) return; 

        String studentId = fullSelection.split(" - ")[0].trim();
        
        selectedStudent = studentsNeedingRecovery.stream()
            .filter(s -> s.getStudentId().equals(studentId))
            .findFirst().orElse(null);

        if (selectedStudent != null) {
            List<CourseRecovery> plans = recoveryManager.getPlansByStudent(studentId);
            
            List<?> results = selectedStudent.getCourseResults(); 
            currentStudentFailedCourses = results.stream()
                 .filter(obj -> obj instanceof Student.CourseResult && ((Student.CourseResult) obj).score < 50) 
                 .map(obj -> ((Student.CourseResult) obj).courseName) 
                 .collect(Collectors.toCollection(ArrayList::new));

            updateRecoveryTable(plans, currentStudentFailedCourses);
        }
    }

    private void updateRecoveryTable(List<CourseRecovery> plans, List<String> allFailedCourses) {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setColumnIdentifiers(TABLE_HEADERS);
        model.setRowCount(0);

        for (CourseRecovery plan : plans) {
            model.addRow(new Object[]{
                plan.getRecoveryId(),
                plan.getCourseName(),
                plan.getStudyWeek(),
                plan.getTask(),
                plan.getStatus(),
                plan.getDeadline() != null ? plan.getDeadline().toString() : ""
            });
        }
        
        boolean hasData = !plans.isEmpty();
        UpdateButton.setEnabled(hasData);
        RemoveButton.setEnabled(hasData);
        EmailButton.setEnabled(hasData);
        AddButton.setEnabled(selectedStudent != null);
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
        jDialog1 = new javax.swing.JDialog();
        jDialog2 = new javax.swing.JDialog();
        jDialog3 = new javax.swing.JDialog();
        jDialog4 = new javax.swing.JDialog();
        update_header = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        EligibilityDropDown = new javax.swing.JComboBox<>();
        AddButton = new javax.swing.JButton();
        UpdateButton = new javax.swing.JButton();
        RemoveButton = new javax.swing.JButton();
        EmailButton = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jDialog2Layout = new javax.swing.GroupLayout(jDialog2.getContentPane());
        jDialog2.getContentPane().setLayout(jDialog2Layout);
        jDialog2Layout.setHorizontalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog2Layout.setVerticalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jDialog3Layout = new javax.swing.GroupLayout(jDialog3.getContentPane());
        jDialog3.getContentPane().setLayout(jDialog3Layout);
        jDialog3Layout.setHorizontalGroup(
            jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog3Layout.setVerticalGroup(
            jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jDialog4Layout = new javax.swing.GroupLayout(jDialog4.getContentPane());
        jDialog4.getContentPane().setLayout(jDialog4Layout);
        jDialog4Layout.setHorizontalGroup(
            jDialog4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog4Layout.setVerticalGroup(
            jDialog4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        update_header.setBackground(new java.awt.Color(129, 182, 129));

        jLabel1.setFont(new java.awt.Font("MS Gothic", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(242, 242, 242));
        jLabel1.setText("Course Recovery Plan");

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 235, Short.MAX_VALUE)
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

        EligibilityDropDown.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        EligibilityDropDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EligibilityDropDownActionPerformed(evt);
            }
        });

        AddButton.setText("Add");
        AddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddButtonActionPerformed(evt);
            }
        });

        UpdateButton.setText("Update");
        UpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UpdateButtonActionPerformed(evt);
            }
        });

        RemoveButton.setText("Remove");
        RemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveButtonActionPerformed(evt);
            }
        });

        EmailButton.setText("Email");
        EmailButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EmailButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(update_header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(141, 141, 141)
                .addComponent(AddButton)
                .addGap(27, 27, 27)
                .addComponent(UpdateButton)
                .addGap(27, 27, 27)
                .addComponent(RemoveButton)
                .addGap(27, 27, 27)
                .addComponent(EmailButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(140, 140, 140)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(EligibilityDropDown, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE))
                    .addContainerGap(140, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(update_header, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 317, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddButton)
                    .addComponent(UpdateButton)
                    .addComponent(RemoveButton)
                    .addComponent(EmailButton))
                .addGap(22, 22, 22))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(58, 58, 58)
                    .addComponent(EligibilityDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(58, Short.MAX_VALUE)))
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

    private void EligibilityDropDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EligibilityDropDownActionPerformed
        // TODO add your handling code here:
        if (EligibilityDropDown.getSelectedItem() == null || studentsNeedingRecovery.isEmpty()) return;
        
        String fullSelection = (String) EligibilityDropDown.getSelectedItem();
        if (fullSelection.startsWith("--")) return; 

        String studentId = fullSelection.split(" - ")[0].trim();
        
        selectedStudent = studentsNeedingRecovery.stream()
            .filter(s -> s.getStudentId().equals(studentId))
            .findFirst().orElse(null);

        if (selectedStudent != null) {
            List<CourseRecovery> plans = recoveryManager.getPlansByStudent(studentId);
            
            List<?> results = selectedStudent.getCourseResults(); 
            currentStudentFailedCourses = results.stream()
                 .filter(obj -> obj instanceof Student.CourseResult && ((Student.CourseResult) obj).score < 50) 
                 .map(obj -> ((Student.CourseResult) obj).courseName) 
                 .collect(Collectors.toCollection(ArrayList::new));

            updateRecoveryTable(plans, currentStudentFailedCourses);
        }
    }//GEN-LAST:event_EligibilityDropDownActionPerformed

    private void UpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateButtonActionPerformed
        // TODO add your handling code here:
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String planId = (String) jTable1.getValueAt(selectedRow, 0);
        String currentTask = (String) jTable1.getValueAt(selectedRow, 3);
        String currentStatus = (String) jTable1.getValueAt(selectedRow, 4);
        String currentDeadline = (String) jTable1.getValueAt(selectedRow, 5);

        UpdatePlanDialog dialog = new UpdatePlanDialog(this, planId, currentTask, currentStatus, currentDeadline);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                LocalDate newDeadline = LocalDate.parse(dialog.getNewDeadlineStr());
                boolean success = recoveryManager.updatePlan(planId, dialog.getNewStatus(), dialog.getNewTask(), newDeadline);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    EligibilityDropDownActionPerformed(null);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_UpdateButtonActionPerformed

    private void AddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddButtonActionPerformed
        // TODO add your handling code here:
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(this, "Please select a student first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (currentStudentFailedCourses == null || currentStudentFailedCourses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "This student has no failed courses.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        AddPlanDialog dialog = new AddPlanDialog(this, currentStudentFailedCourses);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            try {
                LocalDate deadline = LocalDate.parse(dialog.getDeadlineStr());
                recoveryManager.addMilestone(selectedStudent.getStudentId(), 
                        dialog.getSelectedCourse(), dialog.getStudyWeek(), dialog.getTask(), deadline);
                
                JOptionPane.showMessageDialog(this, "Plan added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                EligibilityDropDownActionPerformed(null); // Refresh tabel
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_AddButtonActionPerformed

    private void RemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveButtonActionPerformed
        // TODO add your handling code here:
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to remove.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String planId = (String) jTable1.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete plan " + planId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (recoveryManager.removePlan(planId)) {
                JOptionPane.showMessageDialog(this, "Removed.", "Success", JOptionPane.INFORMATION_MESSAGE);
                EligibilityDropDownActionPerformed(null);
            }
        }
    }//GEN-LAST:event_RemoveButtonActionPerformed

    private void EmailButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EmailButtonActionPerformed
        // TODO add your handling code here:
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(this, "Please select a student first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<CourseRecovery> studentPlans = recoveryManager.getPlansByStudent(selectedStudent.getStudentId());
        
        if (studentPlans.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No recovery plans to send for this student.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder body = new StringBuilder();
        body.append("Hi, ").append(selectedStudent.getFullName()).append("! This is your Course Recovery Plan:\n\n");
        body.append("--------------------------------------------------\n");

        for (CourseRecovery plan : studentPlans) {
            body.append("Course:   ").append(plan.getCourseName()).append("\n");
            body.append("Week:     ").append(plan.getStudyWeek()).append("\n");
            body.append("Task:     ").append(plan.getTask()).append("\n");
            body.append("Status:   ").append(plan.getStatus()).append("\n");
            body.append("Deadline: ").append(plan.getDeadline()).append("\n");
            body.append("--------------------------------------------------\n");
        }
        
        body.append("\nPlease check your dashboard for more details.\n");
        body.append("Regards,\nAcademic Officer");
        
        String recipient = "clairineallegram@gmail.com"; //change email here
        
        Email emailService = new Email.EmailImpl();
        emailService.sendText(recipient, "Course Recovery Plan Update", body.toString());
    }//GEN-LAST:event_EmailButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CourseRecoveryPlan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CourseRecoveryPlan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CourseRecoveryPlan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CourseRecoveryPlan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CourseRecoveryPlan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddButton;
    private javax.swing.JComboBox<String> EligibilityDropDown;
    private javax.swing.JButton EmailButton;
    private javax.swing.JButton RemoveButton;
    private javax.swing.JButton UpdateButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JDialog jDialog2;
    private javax.swing.JDialog jDialog3;
    private javax.swing.JDialog jDialog4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel update_header;
    // End of variables declaration//GEN-END:variables
}
