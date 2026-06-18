package edu.eci.arsw.eciciencia;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.List;

public class ActivityGrpcServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(50051)
                .addService(new ActivityServiceImpl())
                .build();

        server.start();

        System.out.println("ActivityService started, listening on " + server.getPort());

        server.awaitTermination();
    }

    static class ActivityServiceImpl extends ActivityServiceGrpc.ActivityServiceImplBase {

        private final ActivityRepository repository = new ActivityRepository();

        @Override
        public void getActivity(
                ActivityRequest request,
                StreamObserver<ActivityResponse> responseObserver
        ) {
            ActivityResponse activity = repository.findById(request.getActivityId());

            if (activity == null) {
                responseObserver.onError(
                        io.grpc.Status.NOT_FOUND
                                .withDescription("Activity not found with id: " + request.getActivityId())
                                .asRuntimeException()
                );
                return;
            }

            responseObserver.onNext(activity);
            responseObserver.onCompleted();
        }

        @Override
        public void getAgenda(
                GetAgendaRequest request,
                StreamObserver<ActivityList> responseObserver
        ) {
            List<ActivityResponse> activities = repository.getAll();

            ActivityList response = ActivityList.newBuilder()
                    .addAllActivities(activities)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void getActivitiesByTimeSlot(
                TimeSlotRequest request,
                StreamObserver<ActivityList> responseObserver
        ) {
            try {
                List<ActivityResponse> activities = repository.findByTimeSlot(
                        request.getDate(),
                        request.getStartTime(),
                        request.getEndTime()
                );

                ActivityList response = ActivityList.newBuilder()
                        .addAllActivities(activities)
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();

            } catch (Exception e) {
                responseObserver.onError(
                        io.grpc.Status.INVALID_ARGUMENT
                                .withDescription("Invalid date or time format. Use date like 20/06/2026 and time like 10:00")
                                .asRuntimeException()
                );
            }
        }

        @Override
        public void checkAndOccupySlot(
                CapacityRequest request,
                StreamObserver<CapacityResponse> responseObserver
        ) {
            CapacityResponse response = repository.checkAndOccupySlot(request.getActivityId());

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}