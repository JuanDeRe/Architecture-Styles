package edu.eci.arsw.bienestar;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class BienestarClient {

    public static void main(String[] args) {
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

        try {
            System.out.println(" Probando AppointmentService");
            System.out.println("==================================");

            AppointmentRequest appointmentRequest = AppointmentRequest.newBuilder()
                    .setStudentId("0")
                    .setServiceType("MEDICINA")
                    .setDate("20/06/2026")
                    .build();

            AppointmentResponse appointmentResponse =
                    appointmentStub.requestAppointment(appointmentRequest);

            System.out.println("Cita creada:");
            System.out.println(appointmentResponse);

            StudentRequest studentRequest = StudentRequest.newBuilder()
                    .setStudentId("0")
                    .build();

            AppointmentList appointmentList =
                    appointmentStub.getAppointments(studentRequest);

            System.out.println("Citas del estudiante 0:");
            System.out.println(appointmentList);

            CancelRequest cancelRequest = CancelRequest.newBuilder()
                    .setAppointmentId(appointmentResponse.getId())
                    .build();

            CancelResponse cancelResponse =
                    appointmentStub.cancelAppointment(cancelRequest);

            System.out.println("Cita cancelada:");
            System.out.println(cancelResponse);

            AppointmentList appointmentListAfterCancel =
                    appointmentStub.getAppointments(studentRequest);

            System.out.println("Citas del estudiante 0 después de cancelar:");
            System.out.println(appointmentListAfterCancel);


            System.out.println("==================================");
            System.out.println(" Probando GymService");
            System.out.println("==================================");

            GymReservationRequest gymRequest = GymReservationRequest.newBuilder()
                    .setStudentId("0")
                    .setDate("21/06/2026")
                    .setHour("10:00")
                    .build();

            GymReservationResponse gymResponse =
                    gymStub.reserveSession(gymRequest);

            System.out.println("Reserva de gimnasio creada:");
            System.out.println(gymResponse);

            GymStudentRequest gymStudentRequest = GymStudentRequest.newBuilder()
                    .setStudentId("0")
                    .build();

            GymReservationList gymReservationList =
                    gymStub.getReservations(gymStudentRequest);

            System.out.println("Reservas de gimnasio del estudiante 0:");
            System.out.println(gymReservationList);

            GymCancelRequest gymCancelRequest = GymCancelRequest.newBuilder()
                    .setReservationId(gymResponse.getId())
                    .build();

            GymCancelResponse gymCancelResponse =
                    gymStub.cancelReservation(gymCancelRequest);

            System.out.println("Reserva de gimnasio cancelada:");
            System.out.println(gymCancelResponse);

            GymReservationList gymReservationListAfterCancel =
                    gymStub.getReservations(gymStudentRequest);

            System.out.println("Reservas de gimnasio después de cancelar:");
            System.out.println(gymReservationListAfterCancel);

        } catch (StatusRuntimeException e) {
            System.out.println("Error gRPC:");
            System.out.println("Código: " + e.getStatus().getCode());
            System.out.println("Descripción: " + e.getStatus().getDescription());
        } finally {
            appointmentChannel.shutdown();
            gymChannel.shutdown();
        }
    }
}