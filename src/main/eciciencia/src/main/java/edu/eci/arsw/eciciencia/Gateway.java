package edu.eci.arsw.eciciencia;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class Gateway {

    private final ManagedChannel activityChannel;
    private final ManagedChannel registrationChannel;

    private final ActivityServiceGrpc.ActivityServiceBlockingStub activityStub;
    private final RegistrationServiceGrpc.RegistrationServiceBlockingStub registrationStub;

    public Gateway() {
        this.activityChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        this.registrationChannel = ManagedChannelBuilder
                .forAddress("localhost", 50052)
                .usePlaintext()
                .build();

        this.activityStub = ActivityServiceGrpc.newBlockingStub(activityChannel);
        this.registrationStub = RegistrationServiceGrpc.newBlockingStub(registrationChannel);
    }

    public AttendeeResponse registerAttendee(
            String name,
            String email,
            String institutionalCode
    ) {
        RegisterAttendeeRequest request = RegisterAttendeeRequest.newBuilder()
                .setName(name)
                .setEmail(email)
                .setInstitutionalCode(institutionalCode)
                .build();

        return registrationStub.registerAttendee(request);
    }

    public ActivityList getAgenda() {
        GetAgendaRequest request = GetAgendaRequest.newBuilder().build();

        return activityStub.getAgenda(request);
    }

    public ActivityList getActivitiesByTimeSlot(
            String date,
            String startTime,
            String endTime
    ) {
        TimeSlotRequest request = TimeSlotRequest.newBuilder()
                .setDate(date)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .build();

        return activityStub.getActivitiesByTimeSlot(request);
    }

    public ActivityResponse getActivity(String activityId) {
        ActivityRequest request = ActivityRequest.newBuilder()
                .setActivityId(activityId)
                .build();

        return activityStub.getActivity(request);
    }

    public BookActivityResponse bookActivity(
            String attendeeId,
            String activityId
    ) {
        BookActivityRequest request = BookActivityRequest.newBuilder()
                .setAttendeeId(attendeeId)
                .setActivityId(activityId)
                .build();

        return registrationStub.bookActivity(request);
    }

    public RegistrationList getAttendeeRegistrations(String attendeeId) {
        AttendeeRequest request = AttendeeRequest.newBuilder()
                .setAttendeeId(attendeeId)
                .build();

        return registrationStub.getAttendeeRegistrations(request);
    }

    public void shutdown() {
        activityChannel.shutdown();
        registrationChannel.shutdown();
    }
}
