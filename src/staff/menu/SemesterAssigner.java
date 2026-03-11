/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package staff.menu;

import java.util.*;

public class SemesterAssigner {

    public static HashMap<String, Integer> assignSemestersRandomly(ArrayList<Student> students) {

        HashMap<String, Integer> map = new HashMap<>();

        ArrayList<Student> copy = new ArrayList<>(students);
        Collections.shuffle(copy);

        for (int i = 0; i < 50; i++) {
            map.put(copy.get(i).getStudentId(), 1);
        }
        for (int i = 50; i < 100; i++) {
            map.put(copy.get(i).getStudentId(), 2);
        }

        return map;
    }
}

