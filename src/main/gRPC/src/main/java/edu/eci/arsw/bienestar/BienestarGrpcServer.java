package edu.eci.arsw.bienestar;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BienestarGrpcServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(50051)
                .addService(new BienestarServiceImpl())
                .build();
        server.start();
        System.out.println("gRPC server started at service 50051");
        server.awaitTermination();
    }
    static class BienestarServiceImpl extends AppointmentServiceGrpc.AppointmentServiceImplBase {
        private final Map<String,AppointmentResponse> appointments = new HashMap<>();
        private final Map<String,Student> students = new HashMap<>();
        private Integer appointmentIdCounter = 0;
        private Integer studentIdCounter = 0;
        public  BienestarServiceImpl() {
            students.put((studentIdCounter).toString(),new Student((studentIdCounter++).toString(),"Juan","juan.roa-h@mail.escuelaing.edu.co"));
        }
        @Override
        public void requestAppointment(AppointmentRequest request, StreamObserver<AppointmentResponse> responseObserver) {
            Integer id = appointmentIdCounter++;
            Student student = students.get(request.getStudentId());
            if(student == null){
                System.out.println("Student not found");
                responseObserver.onError(
                        io.grpc.Status.NOT_FOUND.asRuntimeException()
                );
                return;
            }
            AppointmentResponse appointmentResponse = AppointmentResponse.newBuilder()
                    .setId((id).toString())
                    .setDate(request.getDate())
                    .setStudentName(student.getName())
                    .setStudentId(student.getId())
                    .setStudentEmail(student.getEmail())
                    .setServiceType(request.getServiceType())
                    .setStatus(Status.REQUESTED)
                    .build();
            appointments.put(id.toString(),appointmentResponse);
            responseObserver.onNext(appointmentResponse);
            responseObserver.onCompleted();
        }
        @Override
        public void cancelAppointment(CancelRequest request, StreamObserver<CancelResponse> responseObserver) {
            AppointmentResponse appointmentResponse = appointments.remove(request.getAppointmentId());
            if(appointmentResponse == null) {
                System.out.println("Appointment not found");
                responseObserver.onError(
                        io.grpc.Status.NOT_FOUND.asRuntimeException()
                );
                return;
            }
            AppointmentResponse cancelledAppointment = AppointmentResponse.newBuilder()
                    .setId(appointmentResponse.getId())
                    .setDate(appointmentResponse.getDate())
                    .setStudentName(appointmentResponse.getStudentName())
                    .setStudentId(appointmentResponse.getStudentId())
                    .setStudentEmail(appointmentResponse.getStudentEmail())
                    .setServiceType(appointmentResponse.getServiceType())
                    .setStatus(Status.CANCELLED)
                    .build();
            appointments.put(cancelledAppointment.getId(),cancelledAppointment);
            CancelResponse cancelResponse = CancelResponse.newBuilder()
                    .setStatus(Status.CANCELLED)
                    .setAppointmentId(cancelledAppointment.getId())
                    .build();
            responseObserver.onNext(cancelResponse);
            responseObserver.onCompleted();
        }
        @Override
        public void getAppointments(StudentRequest request, StreamObserver<AppointmentList> responseObserver) {
            List<AppointmentResponse> appointmentResponses = appointments.values().stream()
                    .filter(a -> a.getStudentId().equals(request.getStudentId()))
                    .toList();
            AppointmentList appointmentList = AppointmentList.newBuilder().addAllAppointments(appointmentResponses).build();
            responseObserver.onNext(appointmentList);
            responseObserver.onCompleted();
        }
    }
}
