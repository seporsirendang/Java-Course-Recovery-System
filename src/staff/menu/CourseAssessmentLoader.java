package staff.menu;

import java.io.*;
import java.util.*;

public class CourseAssessmentLoader {

    public static HashMap<String, CourseAssessment> loadAssessmentTable(String filename) {

        HashMap<String, CourseAssessment> map = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

            String header = br.readLine();
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] col = line.split("\t");

                String id = col[0].trim();
                String name = col[1].trim();
                int credits = Integer.parseInt(col[2].trim());
                String instructor = col[3].trim();
                int examW = Integer.parseInt(col[4].trim());
                int assignmentW = Integer.parseInt(col[5].trim());

                map.put(name, new CourseAssessment(id, name, credits, instructor, examW, assignmentW));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
}
