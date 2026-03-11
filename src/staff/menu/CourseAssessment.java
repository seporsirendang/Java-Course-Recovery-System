/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package staff.menu;
public class CourseAssessment {

    private String courseID;
    private String courseName;
    private int credits;
    private String instructor;
    private int examWeight;
    private int assignmentWeight;

    public CourseAssessment(String courseID, String courseName, int credits,
                            String instructor, int examWeight, int assignmentWeight) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.credits = credits;
        this.instructor = instructor;
        this.examWeight = examWeight;
        this.assignmentWeight = assignmentWeight;
    }

    public String getCourseID() { return courseID; }
    public String getCourseName() { return courseName; }
    public int getCredits() { return credits; }
    public String getInstructor() { return instructor; }
    public int getExamWeight() { return examWeight; }
    public int getAssignmentWeight() { return assignmentWeight; }
}

