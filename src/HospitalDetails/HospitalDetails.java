package HospitalDetails;
import java.sql.*;
import java.util.Scanner;
public class HospitalDetails {

        private static final String url = "jdbc:mysql://localhost:3306/hospital";
        private static final String username = "root";
        private static final String password = "__shruti12__";

        public static void main(String[] args) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            Scanner scanner = new Scanner(System.in);

            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                Patient patient = new Patient(connection, scanner);
                Doctor doctor = new Doctor(connection);
                while (true) {
                    System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
                    System.out.println("1. Add Patient");
                    System.out.println("2. View Patients");
                    System.out.println("3. View Doctors");
                    System.out.println("4. Book Appointment");
                    System.out.println("5. Exit");
                    System.out.println("Enter your choice : ");
                    int choice = scanner.nextInt();

                    switch (choice) {
                        case 1:
                            //Add Patient
                            patient.addPatient();
                            System.out.println();
                            break;
                        case 2:
                            //View Patient1
                            patient.viewPatients();
                            System.out.println();
                            break;
                        case 3:
                            //View Doctors
                            doctor.viewDoctors();
                            System.out.println();
                            break;
                        case 4:
                            //Book appointment
                            bookAppointment(patient, doctor, connection, scanner);
                            System.out.println();
                            break;
                        case 5:
                            //Exit
                            return;
                        default:
                            System.out.println("Enter valid choice !!!");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
            System.out.println("Enter Patient Id: ");
            int patientId = scanner.nextInt();
            System.out.println("Enter Doctor Id: ");
            int doctorId = scanner.nextInt();
            System.out.println("Enter appointment date (YYYY-MM-DD): ");
            String date = scanner.next();

            if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
                if (checkDoctorAvailability(doctorId, date, connection)) {
                    String appointmentQuery = "INSERT INTO appointments(patient_id,doctor_id,appointment_date) VALUES(?, ?, ?)";
                    try {
                        PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                        preparedStatement.setInt(1, patientId);
                        preparedStatement.setInt(2, doctorId);
                        preparedStatement.setString(3, date);
                        int affectedRows = preparedStatement.executeUpdate();
                        if (affectedRows > 0) {
                            System.out.println("Appointment Booked !!!");
                        } else {
                            System.out.println("Failed to book appointment !!!");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Doctor is not available on this date !!!");
                }
            } else {
                System.out.println("Either Doctor or patient is doesn't exist");
            }
        }


        public static boolean checkDoctorAvailability(int doctorID, String appointmentDate, Connection connection) {
            String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, doctorID);
                preparedStatement.setString(2, appointmentDate);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    if (count == 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
    }


