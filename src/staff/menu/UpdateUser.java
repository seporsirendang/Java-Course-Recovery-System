package staff.menu;

import config.AppConfig;
import java.io.*;
import java.util.*;


/**
 * UpdateUser.java to UPDATE user into the CSV
 * @author pasta
 */

public class UpdateUser {

    public String readFile(String user, String newPassword, String newStatus, String newEmail) { //reading user first to check if it exists or not
        boolean found = false;
        List<String[]> allRows = new ArrayList<>();
        
        String alert = "";

        try {
            Scanner scan = new Scanner(new File(AppConfig.CSV_PATH));

            // read header
            if (scan.hasNextLine()) {
                allRows.add(scan.nextLine().split(","));
            }

            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                String[] data = line.split(",");
                allRows.add(data);
            }
            
            scan.close();
          
            for (String[] data : allRows) {
                
                if (data[0].equalsIgnoreCase("StaffID")) continue; //skip header
                if (data.length < 10) continue; // skip invalid rows

                
                //Lets start with update first
                if(data[0].equalsIgnoreCase(user)){
                    found =true;
                    
                    //overwrites if input is not NULL only
                    if (newPassword != null && !newPassword.isBlank()) {
                        data[3] = newPassword;
                    }

                    if (newStatus != null && !newStatus.isBlank()) {
                        data[5] = newStatus;
                    }

                    if (newEmail != null && !newEmail.isBlank()) {
                        data[6] = newEmail;
                    }
                    
                    writeBack(allRows);

                    System.out.println("Update successful! For user " + data[1] + " (" + data[4] + ")");
                    alert = "success";

                }
           
            }
            
            //Case - no matching data found
            if (!found) {
               alert = "Invalid username or not found.";
               System.out.println("Username not found");
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return alert;
    }
    
    private void writeBack(List<String[]> allRows) throws IOException {
        FileWriter writer = new FileWriter(AppConfig.CSV_PATH);
        for (String[] row : allRows) {
            writer.write(String.join(",", row));
            writer.write("\n");
        }
        writer.close();
    }
}