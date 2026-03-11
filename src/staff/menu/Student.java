package staff.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Student {
    // Minimum passing score for a module
    private static final int PASSING_SCORE = 50; 
    
    Student(String id, String first, String last, String major, String email) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static class CourseResult {
        public String courseName;
        public int score;
        public String grade;

        public CourseResult(String courseName, int score, String grade) {
            this.courseName = courseName;
            this.score = score;
            this.grade = grade;
        }
    }

    private String studentId;
    private String firstName;
    private String lastName;
    private String major;
    private String email;
    private List<CourseResult> courseResults;
    private double fileCgpa;

    private final int BASE_COLS = 5;      // ID, FN, LN, Major, Email (Indices 0-4)
    private final int COURSE_SLOTS = 6;
    private final int TOTAL_PARTS = BASE_COLS + (COURSE_SLOTS * 3) + 1; // 5 + 18 + 1 = 24

    
    
    public Student(String dataLine) {
        String[] parts = Arrays.stream(dataLine.split(";"))
                               .map(String::trim)
                               .toArray(String[]::new);

        if (parts.length < TOTAL_PARTS) {
            throw new IllegalArgumentException("Data line is incomplete for a full 6-course record: " + dataLine);
        }

        this.studentId = parts[0];
        this.firstName = parts[1];
        this.lastName = parts[2];
        this.major = parts[3];
        this.email = parts[4];
        this.courseResults = new ArrayList<>();
        
        int courseNameStart = BASE_COLS;       // Index 5
        int scoreGradeStart = courseNameStart + COURSE_SLOTS; // Index 11

        for (int i = 0; i < COURSE_SLOTS; i++) {
            String courseName = parts[courseNameStart + i];
            
            // Check for Score and Grade indices
            int scoreIndex = scoreGradeStart + (i * 2);
            int gradeIndex = scoreGradeStart + (i * 2) + 1;
            
            if (!courseName.isEmpty()) { 
                try {
                    int score = Integer.parseInt(parts[scoreIndex]);
                    String grade = parts[gradeIndex];
                    this.courseResults.add(new CourseResult(courseName, score, grade));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Warning: Invalid score/grade data for " + courseName + " in student " + studentId);
                }
            }
        }
        
        // CGPA from file (the last element)
        try {
            this.fileCgpa = Double.parseDouble(parts[TOTAL_PARTS - 1]);
        } catch (NumberFormatException e) {
            this.fileCgpa = 0.0;
        }
    }

    public int countFailedCourses() {
        int failedCount = 0;
        for (CourseResult result : courseResults) {
            if (result.score < PASSING_SCORE) {
                failedCount++;
            }
        }
        return failedCount;
    }

    public boolean isEligibleToProgress() {
        // Check 1: CGPA >= 2.0 (using the provided file CGPA)
        boolean meetsCgpaReq = this.fileCgpa >= 2.0;

        // Check 2: Not more than three failed courses (score < 50)
        int failedCoursesCount = countFailedCourses();
        boolean meetsFailedCourseReq = failedCoursesCount <= 3;

        return meetsCgpaReq && meetsFailedCourseReq;
    }
    
    private ArrayList<CourseRecord> courses = new ArrayList<>();
    
    public void addCourse(CourseRecord c) {
        courses.add(c);
    }

    public ArrayList<CourseRecord> getCourses() {
        return courses;
    }

    public String getStudentId() { return studentId; }
    public String getFullName() { return firstName + " " + lastName; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getMajor() { return major; }
    public double getCgpa() { return fileCgpa; }
    public String getEmail() { return email; }
    public List<CourseResult> getCourseResults() { return courseResults; }
}