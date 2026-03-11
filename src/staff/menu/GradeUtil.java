/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package staff.menu;

public class GradeUtil {

    public static double toPoint(String letter) {
        if (letter == null) return 0.0;
        letter = letter.trim().toUpperCase();

        switch (letter) {
            case "A": return 4.0;
            case "B": return 3.0;
            case "C": return 2.0;
            case "D": return 1.0;
            case "F": return 0.0;
            default:  return 0.0;
        }
    }

    public static boolean isFail(String letter) {
        if (letter == null) return true;
        String l = letter.trim().toUpperCase();
        return l.equals("D") || l.equals("F");
    }
}
