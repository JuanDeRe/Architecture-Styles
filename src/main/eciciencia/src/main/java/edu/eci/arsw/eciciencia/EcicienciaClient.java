package edu.eci.arsw.eciciencia;

import io.grpc.StatusRuntimeException;

import java.util.Scanner;

public class EcicienciaClient {

    public static void main(String[] args) {
        Gateway gateway = new Gateway();
        Scanner scanner = new Scanner(System.in);

        boolean end = false;

        System.out.println("ECICIENCIA Client started");

        while (!end) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Register attendee");
            System.out.println("2. Get full agenda");
            System.out.println("3. Get activities by time slot");
            System.out.println("4. Get activity by ID");
            System.out.println("5. Book activity / workshop");
            System.out.println("6. Get attendee registrations");
            System.out.println("7. Exit");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        registerAttendee(gateway, scanner);
                        break;

                    case "2":
                        getAgenda(gateway);
                        break;

                    case "3":
                        getActivitiesByTimeSlot(gateway, scanner);
                        break;

                    case "4":
                        getActivity(gateway, scanner);
                        break;

                    case "5":
                        bookActivity(gateway, scanner);
                        break;

                    case "6":
                        getAttendeeRegistrations(gateway, scanner);
                        break;

                    case "7":
                        end = true;
                        System.out.println("Closing ECICIENCIA Client...");
                        break;

                    default:
                        System.out.println("Invalid option");
                        break;
                }
            } catch (StatusRuntimeException e) {
                System.out.println("gRPC error:");
                System.out.println("Code: " + e.getStatus().getCode());
                System.out.println("Description: " + e.getStatus().getDescription());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }

        gateway.shutdown();
        scanner.close();
    }

    private static void registerAttendee(
            Gateway gateway,
            Scanner scanner
    ) {
        System.out.println("Enter attendee name:");
        String name = scanner.nextLine();

        System.out.println("Enter attendee email:");
        String email = scanner.nextLine();

        System.out.println("Enter institutional Id:");
        String institutionalCode = scanner.nextLine();

        AttendeeResponse response = gateway.registerAttendee(
                name,
                email,
                institutionalCode
        );

        System.out.println("Attendee registration response:");
        System.out.println(response);
    }

    private static void getAgenda(Gateway gateway) {
        ActivityList response = gateway.getAgenda();

        System.out.println("ECICIENCIA agenda:");
        System.out.println(response);
    }

    private static void getActivitiesByTimeSlot(
            Gateway gateway,
            Scanner scanner
    ) {
        System.out.println("Enter date (DD/MM/YYYY): ");
        String date = scanner.nextLine();

        System.out.println("Enter start time (HH:MM): ");
        String startTime = scanner.nextLine();

        System.out.println("Enter end time (HH:MM): ");
        String endTime = scanner.nextLine();

        ActivityList response = gateway.getActivitiesByTimeSlot(
                date,
                startTime,
                endTime
        );

        System.out.println("Activities in selected time slot:");
        System.out.println(response);
    }

    private static void getActivity(
            Gateway gateway,
            Scanner scanner
    ) {
        System.out.println("Enter activity ID:");
        String activityId = scanner.nextLine();

        ActivityResponse response = gateway.getActivity(activityId);

        System.out.println("Activity found:");
        System.out.println(response);
    }

    private static void bookActivity(
            Gateway gateway,
            Scanner scanner
    ) {
        System.out.println("Enter attendee ID:");
        String attendeeId = scanner.nextLine();

        System.out.println("Enter activity ID:");
        String activityId = scanner.nextLine();

        BookActivityResponse response = gateway.bookActivity(
                attendeeId,
                activityId
        );

        System.out.println("Booking response:");
        System.out.println(response);
    }

    private static void getAttendeeRegistrations(
            Gateway gateway,
            Scanner scanner
    ) {
        System.out.println("Enter attendee ID:");
        String attendeeId = scanner.nextLine();

        RegistrationList response = gateway.getAttendeeRegistrations(attendeeId);

        System.out.println("Attendee registrations:");
        System.out.println(response);
    }
}
