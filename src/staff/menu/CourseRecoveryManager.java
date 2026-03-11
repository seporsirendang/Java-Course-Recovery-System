package staff.menu;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CourseRecoveryManager {
    private static final String FILE_PATH = "course_recovery_plan.txt";
    private static final int MIN_PASSING_SCORE = 50;
    
    private List<CourseRecovery> recoveryPlans;

    public CourseRecoveryManager() {
        this.recoveryPlans = loadPlans();
    }

    private List<CourseRecovery> loadPlans() {
        List<CourseRecovery> plans = new ArrayList<>();
        boolean isHeader = true;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }
                if (line.trim().isEmpty()) continue;
                try {
                    plans.add(new CourseRecovery(line));
                } catch (Exception e) {
                    System.err.println("Skipping bad record: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("No existing file, creating new list.");
        }
        return plans;
    }

    public void savePlans() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            pw.println("RecoveryID,StudentID,CourseName,StudyWeek,Task,Status,Deadline");
            for (CourseRecovery plan : recoveryPlans) {
                pw.println(plan.toCsvString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addMilestone(String studentId, String courseName, String studyWeek, String task, LocalDate deadline) {
        CourseRecovery newPlan = new CourseRecovery(studentId, courseName, studyWeek, task, deadline);
        this.recoveryPlans.add(newPlan);
        savePlans(); 
    }

    public void generatePlansForFailedCourses(List<Student> studentList) {
        LocalDate baseDate = LocalDate.now();
        for (Student student : studentList) {
            String sId = student.getStudentId();
            for (Student.CourseResult result : student.getCourseResults()) {
                if (result.score < MIN_PASSING_SCORE) {
                    String cName = result.courseName;
                    boolean exists = recoveryPlans.stream().anyMatch(p -> 
                        p.getStudentId().equals(sId) && p.getCourseName().equals(cName)
                    );

                    if (!exists) {
                        addMilestone(sId, cName, "Week 1-2", "Review Topics", baseDate.plusWeeks(2));
                        addMilestone(sId, cName, "Week 3", "Lecturer Meeting", baseDate.plusWeeks(3));
                        addMilestone(sId, cName, "Week 4", "Recovery Exam", baseDate.plusWeeks(4));
                    }
                }
            }
        }
        savePlans();
    }

    public List<CourseRecovery> getPlansByStudent(String studentId) {
        return recoveryPlans.stream()
                .filter(p -> p.getStudentId().equalsIgnoreCase(studentId))
                .collect(Collectors.toList());
    }
    
    public List<CourseRecovery> getAllPlans() {
        return new ArrayList<>(recoveryPlans);
    }

    public boolean updatePlan(String planId, String newStatus, String newTask, LocalDate newDeadline) {
        for (CourseRecovery plan : recoveryPlans) {
            if (plan.getRecoveryId().equalsIgnoreCase(planId)) {
                if (newStatus != null) plan.setStatus(newStatus);
                if (newTask != null) plan.setTask(newTask);
                if (newDeadline != null) plan.setDeadline(newDeadline);
                savePlans();
                return true;
            }
        }
        return false;
    }

    public boolean removePlan(String planId) {
        boolean removed = recoveryPlans.removeIf(p -> p.getRecoveryId().equalsIgnoreCase(planId));
        if (removed) savePlans();
        return removed;
    }
}