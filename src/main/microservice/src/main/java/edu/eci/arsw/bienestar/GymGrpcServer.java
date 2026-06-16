package edu.eci.arsw.bienestar;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GymGrpcServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(50052)
                .addService(new GymServiceImpl())
                .build();

        server.start();
        System.out.println("GymService running on port 50052");
        server.awaitTermination();
    }

    static class GymServiceImpl extends GymServiceGrpc.GymServiceImplBase {

        private final Map<String, GymReservationResponse> reservations = new HashMap<>();
        private int reservationIdCounter = 0;

        @Override
        public void reserveSession(
                GymReservationRequest request,
                StreamObserver<GymReservationResponse> responseObserver
        ) {
            String id = String.valueOf(reservationIdCounter++);

            GymReservationResponse reservation = GymReservationResponse.newBuilder()
                    .setId(id)
                    .setStudentId(request.getStudentId())
                    .setDate(request.getDate())
                    .setHour(request.getHour())
                    .setStatus(GymReservationStatus.GYM_RESERVED)
                    .build();

            reservations.put(id, reservation);

            responseObserver.onNext(reservation);
            responseObserver.onCompleted();
        }

        @Override
        public void cancelReservation(
                GymCancelRequest request,
                StreamObserver<GymCancelResponse> responseObserver
        ) {
            GymReservationResponse reservation = reservations.get(request.getReservationId());

            if (reservation == null) {
                responseObserver.onError(
                        io.grpc.Status.NOT_FOUND
                                .withDescription("Gym reservation not found")
                                .asRuntimeException()
                );
                return;
            }

            GymReservationResponse cancelledReservation = GymReservationResponse.newBuilder(reservation)
                    .setStatus(GymReservationStatus.GYM_CANCELLED)
                    .build();

            reservations.put(cancelledReservation.getId(), cancelledReservation);

            GymCancelResponse response = GymCancelResponse.newBuilder()
                    .setReservationId(cancelledReservation.getId())
                    .setStatus(GymReservationStatus.GYM_CANCELLED)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void getReservations(
                GymStudentRequest request,
                StreamObserver<GymReservationList> responseObserver
        ) {
            List<GymReservationResponse> result = reservations.values()
                    .stream()
                    .filter(r -> r.getStudentId().equals(request.getStudentId()))
                    .toList();

            GymReservationList response = GymReservationList.newBuilder()
                    .addAllReservations(result)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}