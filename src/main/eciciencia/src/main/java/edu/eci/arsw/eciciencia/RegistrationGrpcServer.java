package edu.eci.arsw.eciciencia;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.List;

public class RegistrationGrpcServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(50052)
                .addService(new RegistrationServiceImpl())
                .build();

        server.start();

        System.out.println("RegistrationService started, listening on " + 50052);

        server.awaitTermination();
    }

    static class RegistrationServiceImpl extends RegistrationServiceGrpc.RegistrationServiceImplBase {

        private final RegistrationRepository repository = new RegistrationRepository();

        private final ManagedChannel activityChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        private final ActivityServiceGrpc.ActivityServiceBlockingStub activityStub =
                ActivityServiceGrpc.newBlockingStub(activityChannel);

        @Override
        public void registerAttendee(
                RegisterAttendeeRequest request,
                StreamObserver<AttendeeResponse> responseObserver
        ) {
            if (request.getName().isBlank()
                    || request.getEmail().isBlank()
                    || request.getInstitutionalCode().isBlank()) {

                responseObserver.onError(
                        io.grpc.Status.INVALID_ARGUMENT
                                .withDescription("Name, email and institutional code are required")
                                .asRuntimeException()
                );
                return;
            }

            AttendeeResponse response = repository.registerAttendee(
                    request.getName(),
                    request.getEmail(),
                    request.getInstitutionalCode()
            );

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void bookActivity(
                BookActivityRequest request,
                StreamObserver<BookActivityResponse> responseObserver
        ) {
            String attendeeId = request.getAttendeeId();
            String activityId = request.getActivityId();

            AttendeeResponse attendee = repository.findAttendeeById(attendeeId);

            if (attendee == null) {
                BookActivityResponse response = BookActivityResponse.newBuilder()
                        .setAttendeeId(attendeeId)
                        .setActivityId(activityId)
                        .setStatus(RegistrationStatus.REJECTED)
                        .setMessage("Attendee not found")
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }

            if (repository.alreadyBooked(attendeeId, activityId)) {
                BookActivityResponse response = BookActivityResponse.newBuilder()
                        .setAttendeeId(attendeeId)
                        .setActivityId(activityId)
                        .setStatus(RegistrationStatus.REJECTED)
                        .setMessage("Attendee already booked this activity")
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }

            CapacityRequest capacityRequest = CapacityRequest.newBuilder()
                    .setActivityId(activityId)
                    .build();

            CapacityResponse capacityResponse = activityStub.checkAndOccupySlot(capacityRequest);

            if (!capacityResponse.getAvailable()) {
                BookActivityResponse response = BookActivityResponse.newBuilder()
                        .setAttendeeId(attendeeId)
                        .setActivityId(activityId)
                        .setStatus(RegistrationStatus.REJECTED)
                        .setMessage(capacityResponse.getMessage())
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }

            BookActivityResponse response = repository.createRegistration(attendeeId, activityId);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void getAttendeeRegistrations(
                AttendeeRequest request,
                StreamObserver<RegistrationList> responseObserver
        ) {
            String attendeeId = request.getAttendeeId();

            AttendeeResponse attendee = repository.findAttendeeById(attendeeId);

            if (attendee == null) {
                responseObserver.onError(
                        io.grpc.Status.NOT_FOUND
                                .withDescription("Attendee not found with id: " + attendeeId)
                                .asRuntimeException()
                );
                return;
            }

            List<BookActivityResponse> registrations =
                    repository.findRegistrationsByAttendee(attendeeId);

            RegistrationList response = RegistrationList.newBuilder()
                    .addAllRegistrations(registrations)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
