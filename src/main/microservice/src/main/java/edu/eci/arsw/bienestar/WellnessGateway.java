package edu.eci.arsw.bienestar;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Scanner;

public class WellnessGateway {

    public static void main(String args[]) {
        ManagedChannel appointmentChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        ManagedChannel gymChannel = ManagedChannelBuilder
                .forAddress("localhost", 50052)
                .usePlaintext()
                .build();

        AppointmentServiceGrpc.AppointmentServiceBlockingStub appointmentStub =
                AppointmentServiceGrpc.newBlockingStub(appointmentChannel);

        GymServiceGrpc.GymServiceBlockingStub gymStub =
                GymServiceGrpc.newBlockingStub(gymChannel);

        Scanner scanner = new Scanner(System.in);

        System.out.println("Wellness gateway started");

        boolean end = false;

        while (!end) {
            System.out.println("\nEscoja su operación: ");
            System.out.println("1. Agendar cita");
            System.out.println("2. Obtener resumen médico");
            System.out.println("3. Reservar sesión de gimnasio");
            System.out.println("4. Reservar recurso de recreación");
            System.out.println("5. Salir");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        requestAppointment(appointmentStub, scanner);
                        break;

                    case "2":
                        getStudentWellnessSummary();
                        break;

                    case "3":
                        reserveGymSession(gymStub, scanner);
                        break;

                    case "4":
                        reserveRecreationResource();
                        break;

                    case "5":
                        end = true;
                        System.out.println("Saliendo del Wellness Gateway...");
                        break;

                    default:
                        System.out.println("Opción inválida");
                        break;
                }
            } catch (StatusRuntimeException e) {
                System.out.println("Error gRPC:");
                System.out.println("Código: " + e.getStatus().getCode());
                System.out.println("Descripción: " + e.getStatus().getDescription());
            } catch (Exception e) {
                System.out.println("Error inesperado: " + e.getMessage());
            }
        }

        appointmentChannel.shutdown();
        gymChannel.shutdown();
        scanner.close();
    }

    private static void reserveRecreationResource() {
        System.out.println("Falta implementación de RecreationService.");
    }

    private static void getStudentWellnessSummary() {
        System.out.println("Falta implementación del resumen médico / Wellness Summary.");
    }

    private static void reserveGymSession(
            GymServiceGrpc.GymServiceBlockingStub gymStub,
            Scanner scanner
    ) {
        System.out.println("Ingresa el ID del estudiante: ");
        String studentId = scanner.nextLine();

        System.out.println("Ingresa la fecha de la sesión de gimnasio: ");
        String date = scanner.nextLine();

        System.out.println("Ingresa la hora de la sesión de gimnasio: ");
        String hour = scanner.nextLine();

        GymReservationRequest gymRequest = GymReservationRequest.newBuilder()
                .setStudentId(studentId)
                .setDate(date)
                .setHour(hour)
                .build();

        GymReservationResponse gymResponse =
                gymStub.reserveSession(gymRequest);

        System.out.println("Reserva de gimnasio creada:");
        System.out.println(gymResponse);
    }

    private static void requestAppointment(
            AppointmentServiceGrpc.AppointmentServiceBlockingStub appointmentStub,
            Scanner scanner
    ) {
        System.out.println("Ingresa el ID del estudiante: ");
        String studentId = scanner.nextLine();

        System.out.println("Ingrese el tipo de servicio (MEDICINE, PSYCHOLOGY, DENTISTRY): ");
        String service = scanner.nextLine();

        System.out.println("Ingresa la fecha de la cita: ");
        String cita = scanner.nextLine();

        AppointmentRequest appointmentRequest = AppointmentRequest.newBuilder()
                .setStudentId(studentId)
                .setServiceType(service.toUpperCase())
                .setDate(cita)
                .build();

        AppointmentResponse appointmentResponse =
                appointmentStub.requestAppointment(appointmentRequest);

        System.out.println("Cita creada:");
        System.out.println(appointmentResponse);
    }
}