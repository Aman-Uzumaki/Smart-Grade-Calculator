import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class SGC {

    static final String DB_URL = "jdbc:mysql://localhost:3306/SGC";
    static final String USER = "root";
    static final String PASS = "moon1234";

    static int choice;
    static int option;
    static String username;
    static String pass;
    static String pass2;
    static String sql;
    static int exams;
    static int i;
    static int j;
    static String examName;
    static int examMarks;
    static double examWeightage;
    static int numSubjects;
    static String rollno;
    static double marks;
    static int cont = 1;

    public static void main(String[] args) {

        Scanner scn = new Scanner(System.in);

        System.out.println("Welcome to Smart Grade Calculator!\n");

        //Establishing connection

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stat = conn.createStatement()) {

            //Login as teacher/student

            System.out.println("Who are you?");
            System.out.println("1. Teacher");
            System.out.println("2. Student");
            System.out.print("Your choice: ");
            choice = scn.nextInt();
            scn.nextLine();
            if (choice == 1) {
                System.out.println("\nChoose an option: ");
                System.out.println("1. Create a new account");
                System.out.println("2. Log in to an existing account");
                System.out.print("Your choice: ");
                option = scn.nextInt();
                scn.nextLine();
                if (option == 1) {

                    // For new teacher: Creation of account

                    System.out.print("\nCreate your username: ");
                    username = scn.nextLine();

                    // Loop to check if the username already exists
                    while (usernameExists(username, stat)) {
                        System.out.println("Username already exists. Please choose another username.");
                        System.out.print("Create your username: ");
                        username = scn.nextLine();
                    }

                    // Loop to ask for password and confirmation until they match
                    do {
                        System.out.print("\nCreate your password: ");
                        pass = scn.nextLine();
                        System.out.print("Confirm your password: ");
                        pass2 = scn.nextLine();

                        if (!pass.equals(pass2)) {
                            System.out.println("Passwords do not match. Please try again.");
                        }
                    } while (!pass.equals(pass2));

                    // Add code to insert the data into the table
                    sql = "Insert into teacher (username, password) values('" + username + "','" + pass + "');";
                    stat.executeUpdate(sql);

                    System.out.println("\nAccount created successfully.");

                    System.out.print("Enter the number of exams: ");
                    exams = scn.nextInt();
                    scn.nextLine();

                    // Creating a table having information about exams and full marks and weightage
                    sql = "CREATE TABLE exam_" + username + " (Name varchar(30), Marks decimal(3), Weightage decimal(5,2));";
                    stat.executeUpdate(sql);

                    for (i = 1; i <= exams; i++) {
                        System.out.print("Enter the name of Exam " + i + " : ");
                        examName = scn.nextLine();
                        System.out.print("Enter the total marks of " + examName + " : ");
                        examMarks = scn.nextInt();
                        scn.nextLine();
                        System.out.print("Enter the weightage of " + examName + " : ");
                        examWeightage = scn.nextDouble();
                        scn.nextLine();
                        System.out.println("");
                        sql = "INSERT INTO exam_" + username + " (name, marks, weightage) VALUES ('" + examName + "','" + examMarks + "','" + examWeightage + "');";
                        stat.executeUpdate(sql);
                    }

                    System.out.println("Exam details set up successfully.");

                    // Creating a table having information about subjects and exams in which they appear
                    sql = "CREATE TABLE subject_details_" + username + " (subname VARCHAR(30));";
                    stat.executeUpdate(sql);

                    System.out.print("Enter the number of subjects: ");
                    numSubjects = scn.nextInt();
                    scn.nextLine(); // Consume the newline character

                    for (int j = 1; j <= numSubjects; j++) {
                        System.out.print("Enter the name of Subject " + j + " : ");
                        String subjectName = scn.nextLine();

                        // Insert each subject name into the 'subname' attribute of 'subject_details' table
                        sql = "INSERT INTO subject_details_" + username + " (subname) VALUES ('" + subjectName + "');";
                        stat.executeUpdate(sql);
                    }
                    
                    // Creating arrays for exam names and subject names
                    String[] examNames = new String[exams];
                    String[] subjects = new String[numSubjects];

                    sql = "Select Name from exam_" + username;
                    ResultSet resultSet = stat.executeQuery(sql);
                    int i = 0;
                    while(resultSet.next()){
                        examNames[i] = resultSet.getString(1);
                        i++;
                    }

                    sql = "Select subname from subject_details_" + username;
                    resultSet = stat.executeQuery(sql);
                    i = 0;
                    while(resultSet.next()){
                        subjects[i] = resultSet.getString(1);
                        i++;
                    }

                    for (String name : examNames) {
                        sql = "CREATE table " + name + "_" + username + " (rollno varchar(10) primary key ";
                        for (String subname : subjects) {
                            sql += ", " + subname + " decimal(5,3)";
                        }
                        sql += ");";
                        stat.executeUpdate(sql);
                    }

                    System.out.println("Restart the program to login and enter marks.");
                }
                else if(option == 2) {
                    System.err.println("Enter your username: ");
                    username = scn.nextLine();
                    System.err.println("Enter the password: ");
                    pass = scn.nextLine();

                    sql = "Select password from teacher where username ='" + username + "'";
                    ResultSet resultSet = stat.executeQuery(sql);
                    while(resultSet.next()){
                        pass2 = resultSet.getString(1);
                    }
                    if(pass != pass2) {
                        while(cont == 1) {
                            System.out.println("Enter the rollno of student: ");
                            rollno = scn.nextLine();
                            sql = "Select name from exam_" + username + ";";
                            ResultSet fetchmarks = stat.executeQuery(sql);
                            i = 0;
                            while(fetchmarks.next()){
                                i++;
                            }
                            j = i;
                            String[] fmarks = new String[i];

                            fetchmarks = stat.executeQuery(sql);
                            
                            i = 0;
                            while(fetchmarks.next()){
                                fmarks[i] = fetchmarks.getString(1);
                                i++;
                            }
                            for(i=0;i<j;i++){
                                sql = "INSERT INTO " + fmarks[i] + "_" + username + " VALUES (" + rollno ; 
                                    ResultSet rs = stat.executeQuery("Select subname from subject_details_" + username + ";");
                                    while(rs.next()){
                                        String sname = rs.getString(1);
                                        System.out.print("Enter the marks for " + sname + ": ");
                                        marks = scn.nextInt();
                                        scn.nextLine();
                                        sql += "," + marks;
                                    }
                                    sql += ");";
                                    stat.executeUpdate(sql);
                                }
                                cont = 0;
                            }
                        }
            }
            else{
                System.out.println("Invalid option");
            }
        }
            else {
                System.out.print("Enter your teacher's username: ");
                username = scn.nextLine();
                System.out.print("Enter your rollno: ");
                rollno = scn.nextLine();
                sql = "Select name from exam_" + username;

                ResultSet fetchmarks = stat.executeQuery(sql);
                i = 0;
                while(fetchmarks.next()){
                    i++;
                }
                j = i;
                String[] fmarks = new String[i];

                fetchmarks = stat.executeQuery(sql);
                
                i = 0;
                while(fetchmarks.next()){
                    fmarks[i] = fetchmarks.getString(1);
                    i++;
                }

                ResultSet subdata = stat.executeQuery("Select subname from subject_details_" + username);
                i = 0;
                while(subdata.next()){
                    i++;
                }
                String[] sdata = new String[i];

                subdata = stat.executeQuery("Select subname from subject_details_" + username);
                    
                i = 0;
                while(subdata.next()){
                    sdata[i] = subdata.getString(1);
                    i++;
                }

                for (String string : fmarks) {
                    System.out.println("Marks report in exam: " + string);

                    for (String string2 : sdata) {
                        System.out.print(string2 + ": ");
                        sql = "Select " + string2 + " from " + string + "_" + username + " where rollno = " + rollno;

                        subdata = stat.executeQuery(sql);
                        while(subdata.next()){
                            System.out.println(subdata.getInt(1));
                        }
                    }                    
                }
                
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }

        
        
        // Creating a table for storing data of students
        // For existing teachers: Allowing adding, updating, or deleting marks
        // For students login: Entering roll no and DOB to view marks

        scn.close();
    }

    // Method to check if the username already exists in the teacher table
    private static boolean usernameExists(String username, Statement stat) throws SQLException {
        String checkUsernameQuery = "SELECT COUNT(*) FROM teacher WHERE username = '" + username + "'";
        try (var resultSet = stat.executeQuery(checkUsernameQuery)) {
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;
        }
    }
}