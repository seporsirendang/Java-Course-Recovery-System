package staff.menu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EligibilityCheck { 

    public static List<Student> loadStudentsFromFile(String filePath) {
        List<Student> students = new ArrayList<>();
        boolean isHeader = true;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue; 
                }
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    students.add(new Student(line));
                } catch (IllegalArgumentException e) {
                    System.err.println("Skipping malformed data line: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + filePath + ": " + e.getMessage());
        }
        return students;
    }
    
    public static List<Student> getFilteredStudents(List<Student> allStudents, String filter) {
        List<Student> filteredList = new ArrayList<>();
        
        if (allStudents == null) {
             return filteredList;
        }
        
        for (Student s : allStudents) {
            boolean isEligible = s.isEligibleToProgress();
            
            if (filter.equals("All Students")) {
                filteredList.add(s);
            } else if (filter.equals("Eligible Students") && isEligible) {
                filteredList.add(s);
            } else if (filter.equals("Ineligible Students") && !isEligible) {
                filteredList.add(s);
            }
        }
        return filteredList;
    }
}