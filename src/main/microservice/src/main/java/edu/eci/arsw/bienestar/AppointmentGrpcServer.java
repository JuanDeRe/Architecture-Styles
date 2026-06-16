package edu.eci.arsw.bienestar;


import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentGrpcServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(50051)
                .addService(new AppointmentServiceImpl())
                .build();

        server.start();
        System.out.println("AppointmentService running on port 50051");
        server.awaitTermination();
    }

    static class AppointmentServiceImpl extends AppointmentServiceGrpc.AppointmentServiceImplBase {

        private final Map<String, AppointmentResponse> appointments = new HashMap<>();
        private int appointmentIdCounter = 0;

        @Override
        public void requestAppointment(
                AppointmentRequest request,
                StreamObserver<AppointmentResponse> responseObserver
        ) {
            String id = String.valueOf(appointmentIdCounter++);

            AppointmentResponse appointment = AppointmentResponse.newBuilder()
                    .setId(id)
                    .setStudentId(request.getStudentId())
                    .setServiceType(request.getServiceType())
                    .setDate(request.getDate())
                    .setStatus(AppointmentStatus.APPOINTMENT_REQUESTED)
                    .build();

            appointments.put(id, appointment);

            responseObserver.onNext(appointment);
            responseObserver.onCompleted();
        }

        @Override
        public void cancelAppointment(
                CancelRequest request,
                StreamObserver<CancelResponse> responseObserver
        ) {
            AppointmentResponse appointment = appointments.get(request.getAppointmentId());

            if (appointment == null) {
                responseObserver.onError(
                        io.grpc.Status.NOT_FOUND
                                .withDescription("Appointment not found")
                                .asRuntimeException()
                );
                return;
            }

            AppointmentResponse cancelledAppointment = AppointmentResponse.newBuilder(appointment)
                    .setStatus(AppointmentStatus.APPOINTMENT_CANCELLED)
                    .build();

            appointments.put(cancelledAppointment.getId(), cancelledAppointment);

            CancelResponse response = CancelResponse.newBuilder()
                    .setAppointmentId(cancelledAppointment.getId())
                    .setStatus(AppointmentStatus.APPOINTMENT_CANCELLED)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void getAppointments(
                StudentRequest request,
                StreamObserver<AppointmentList> responseObserver
        ) {
            List<AppointmentResponse> result = appointments.values()
                    .stream()
                    .filter(a -> a.getStudentId().equals(request.getStudentId()))
                    .toList();

            AppointmentList response = AppointmentList.newBuilder()
                    .addAllAppointments(result)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}