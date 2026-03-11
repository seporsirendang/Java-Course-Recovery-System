package staff.menu;
import config.AppConfig;

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ReadCSV.java handles log IN and log OUT, into the CSV.
 * @author pasta
 */

public class ReadCSV {
    
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String readFile(String user, String pass) {
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

                String staffID = data[0];
                String firstName = data[1];
                String lastName = data[2];
                String password = data[3];
                String role = data[4];
                String status = data[5];
                String email = data[6];
                String lastLogin = data[7];
                String lastLogout = data[8];
                String loginStatus = data[9];
                
                //Case - account is DEACTIVATED
                if ((user.equalsIgnoreCase(email) || user.equalsIgnoreCase(staffID)) 
                        && status.equalsIgnoreCase("deactivated")) {
                    
                    alert = "Account is deactivated. Please contact admin.";
                    System.out.println("Account is deactivated. Please contact admin.");
                    return alert;
                }
                
                //Case - correct login
                if ((user.equalsIgnoreCase(email) || user.equalsIgnoreCase(staffID))
                        && pass.equals(password)) {

                    found = true;
                    
                    data[7] = LocalDateTime.now().format(format);
                    data[9] = "1";
                    
                    writeBack(allRows);

                    System.out.println("Login successful! Welcome " + firstName + " (" + role + ")");
                    System.out.println("Opening " + role + " menu...");
                    return role;
                }
            }
            
            //Case - no matching data found
            if (!found) {
               alert = "Invalid username or password";
               System.out.println("Invalid username or password");
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
    
    //LOGOUT function
    public void logoutUser(String staffID) {
        List<String[]> allRows = new ArrayList<>();

        try {
            Scanner scan = new Scanner(new File(AppConfig.CSV_PATH));
            
            if (scan.hasNextLine()) {
                allRows.add(scan.nextLine().split(","));
            }

            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                allRows.add(line.split(","));
            }
            scan.close();

            for (String[] data : allRows) {
                if (data[0].equalsIgnoreCase(staffID)) {
                    data[8] = LocalDateTime.now().format(format);
                    data[9] = "0";
                    break;
                }
            }

            writeBack(allRows);

        } catch (IOException e) {
            System.out.println("Error updating logout: " + e.getMessage());
        }
    }

    private void writeBack(List<String[]> allRows) throws IOException {
        FileWriter writer = new FileWriter(AppConfig.CSV_PATH);
        for (String[] row : allRows) {
            writer.write(String.join(",", row));
            writer.write("\n");
        }
        writer.close();
    }
    
    public String[] getUserDetails(String username) {
        String line = "";
        String splitBy = ",";

        try {
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("academic_staff_information.csv"));
            while ((line = br.readLine()) != null) {
                String[] data = line.split(splitBy);
                if (data[0].equals(username)) {
                    return data; 
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}