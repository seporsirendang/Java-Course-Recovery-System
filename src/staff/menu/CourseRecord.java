/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package staff.menu;

public class CourseRecord {

    private String courseName;
    private String gradeLetter;
    private int creditHours;

    public CourseRecord(String courseName, String gradeLetter, int creditHours) {
        this.courseName = courseName;
        this.gradeLetter = gradeLetter;
        this.creditHours = creditHours;
    }

    public String getCourseName() { return courseName; }
    public String getGradeLetter() { return gradeLetter; }
    public int getCreditHours() { return creditHours; }
}

