package staff.menu;

import java.time.LocalDate;
import java.util.UUID;

public class CourseRecovery {
    private String recoveryId;
    private String studentId;
    private String courseName;
    private String studyWeek;
    private String task;
    private String status;
    private LocalDate deadline;

    public CourseRecovery(String studentId, String courseName, String studyWeek, String task, LocalDate deadline) {
        this.recoveryId = "R" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        this.studentId = studentId;
        this.courseName = courseName;
        this.studyWeek = studyWeek;
        this.task = task;
        this.status = "Pending";
        this.deadline = deadline;
    }

    public CourseRecovery(String dataLine) {
        String[] parts = dataLine.split(",");
        if (parts.length < 6) { 
            throw new IllegalArgumentException("Invalid format");
        }
        
        this.recoveryId = parts[0].trim();
        this.studentId = parts[1].trim();
        this.courseName = parts[2].trim();
        this.studyWeek = parts[3].trim();
        this.task = parts[4].trim();
        this.status = parts[5].trim();
        
        try {
            if (parts.length > 6 && !parts[6].trim().isEmpty()) {
                this.deadline = LocalDate.parse(parts[6].trim());
            } else {
                this.deadline = null;
            }
        } catch (Exception e) {
            this.deadline = null;
        }
    }

    public String toCsvString() {
        String dl = (deadline != null) ? deadline.toString() : "";
        return String.join(",", recoveryId, studentId, courseName, studyWeek, task, status, dl);
    }

    public String getRecoveryId() { return recoveryId; }
    public String getStudentId() { return studentId; }
    public String getCourseName() { return courseName; }
    public String getStudyWeek() { return studyWeek; }
    public String getTask() { return task; }
    public String getStatus() { return status; }
    public LocalDate getDeadline() { return deadline; }

    public void setTask(String task) { this.task = task; }
    public void setStatus(String status) { this.status = status; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
}