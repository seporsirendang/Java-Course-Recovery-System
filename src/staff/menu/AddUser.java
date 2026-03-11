package staff.menu;

import config.AppConfig;
import java.io.*;
import java.util.*;

/**
 * AddUser.java processes ADDing new user into the CSV including generating new ID
 * @author pasta
 */

public class AddUser {

    // Generate new staff ID based on the last ID in CSV
    private String generateID(List<String[]> allRows) {
        int maxID = 0;

        for (int i = 1; i < allRows.size(); i++) { // skips header
            String[] row = allRows.get(i);
            if (row.length > 0) {
                try {
                    int idNum = Integer.parseInt(row[0].replace("A", ""));
                    if (idNum > maxID) maxID = idNum;
                } catch (NumberFormatException e) {
                }
            }
        }

        maxID++;
        return "A" + String.format("%03d", maxID);
    }

    // Checks if input name exists already. Only add if it does not exist.
    public String addUser(String newFirstName, String newLastName, String newPassword, String newEmail, String newRole) {
        List<String[]> allRows = new ArrayList<>();
        String alert = "";

        try (Scanner scan = new Scanner(new File(AppConfig.CSV_PATH))) {
            // read header
            if (scan.hasNextLine()) {
                allRows.add(scan.nextLine().split(","));
            }

            // read existing users
            while (scan.hasNextLine()) {
                allRows.add(scan.nextLine().split(","));
            }

            // check if user already exists
            boolean userExists = false;
            for (String[] data : allRows) {
                if (data.length < 3) continue; // skip invalid rows
                if (data[1].equalsIgnoreCase(newFirstName) && data[2].equalsIgnoreCase(newLastName)) {
                    userExists = true;
                    break;
                }
            }

            if (userExists) {
                alert = "User already exists.";
                System.out.println(alert);
                return alert;
            } else {
                // create new user
                String newStaffID = generateID(allRows);
                String[] newUser = {
                    newStaffID,
                    newFirstName,
                    newLastName,
                    newPassword,
                    newRole,
                    "active",
                    newEmail,
                    "-",
                    "-",
                    "0"
                };

                allRows.add(newUser);
                writeBack(allRows);

                alert = "success: new user " + newStaffID + " - " + newFirstName + " " + newLastName;
                System.out.println("Registration successful! " + alert);
                return alert;
            }

        } catch (FileNotFoundException e) {
            alert = "Error: File not found.";
            e.printStackTrace();
        } catch (IOException e) {
            alert = "Error writing file: " + e.getMessage();
            e.printStackTrace();
        }

        return alert;
    }

    // Write all rows back to CSV
    private void writeBack(List<String[]> allRows) throws IOException {
        try (FileWriter writer = new FileWriter(AppConfig.CSV_PATH)) {
            for (String[] row : allRows) {
                writer.write(String.join(",", row));
                writer.write("\n");
            }
        }
    }
}
