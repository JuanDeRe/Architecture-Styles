package edu.eci.arsw.bienestar;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class BienestarGrpcClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
        AppointmentServiceGrpc.AppointmentServiceBlockingStub stub = AppointmentServiceGrpc.newBlockingStub(channel);
        AppointmentRequest request = AppointmentRequest.newBuilder()
                .setDate("20/06/2026")
                .setStudentId("0")
                .setServiceTypeValue(1)
                .build();
        AppointmentResponse response = stub.requestAppointment(request);
        System.out.println("Respuesta recibida:");
        System.out.println("ID cita: " + response.getId());
        System.out.println("Student ID: " + response.getStudentId());
        System.out.println("Nombre: " + response.getStudentName());
        System.out.println("Email: " + response.getStudentEmail());
        System.out.println("Fecha: " + response.getDate());
        System.out.println("Servicio: " + response.getServiceType());
        System.out.println("Estado: " + response.getStatus());

        CancelRequest cancelRequest = CancelRequest.newBuilder()
                .setAppointmentId(response.getId())
                .build();
        CancelResponse cancelResponse = stub.cancelAppointment(cancelRequest);
        System.out.println("Cancelación recibida:");
        System.out.println("ID cita: " + cancelResponse.getAppointmentId());
        System.out.println("Estado: " + cancelResponse.getStatus());

        StudentRequest studentRequest = StudentRequest.newBuilder()
                        .setStudentId(request.getStudentId())
                                .build();
        AppointmentList appointmentList = stub.getAppointments(studentRequest);
        System.out.println("Recibiendo citas:");
        for(AppointmentResponse appointmentResponse : appointmentList.getAppointmentsList()){
            System.out.println("ID cita: " + appointmentResponse.getId());
            System.out.println("Nombre: " + appointmentResponse.getStudentName());
            System.out.println("Email: " + appointmentResponse.getStudentEmail());
            System.out.println("Fecha: " + appointmentResponse.getDate());
            System.out.println("Servicio: " + appointmentResponse.getServiceType());
            System.out.println("Estado: " + appointmentResponse.getStatus());
        }
        channel.shutdown();
    }
}
