package edu.eci.arsw.eciciencia;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RegistrationRepository {

    private final Map<String, AttendeeResponse> attendees = new LinkedHashMap<>();
    private final Map<String, BookActivityResponse> registrations = new LinkedHashMap<>();

    private int attendeeCounter = 0;
    private int registrationCounter = 0;

    public synchronized AttendeeResponse registerAttendee(
            String name,
            String email,
            String institutionalCode
    ) {
        String attendeeId = "S" + attendeeCounter++;

        AttendeeResponse attendee = AttendeeResponse.newBuilder()
                .setAttendeeId(attendeeId)
                .setName(name)
                .setEmail(email)
                .setInstitutionalCode(institutionalCode)
                .setRegistered(true)
                .setMessage("Attendee registered successfully")
                .build();

        attendees.put(attendeeId, attendee);

        return attendee;
    }

    public synchronized AttendeeResponse findAttendeeById(String attendeeId) {
        return attendees.get(attendeeId);
    }

    public synchronized boolean alreadyBooked(String attendeeId, String activityId) {
        return registrations.values()
                .stream()
                .anyMatch(r ->
                        r.getAttendeeId().equals(attendeeId)
                                && r.getActivityId().equals(activityId)
                                && r.getStatus() == RegistrationStatus.RESERVED
                );
    }

    public synchronized BookActivityResponse createRegistration(
            String attendeeId,
            String activityId
    ) {
        String registrationId = "R" + registrationCounter++;

        BookActivityResponse registration = BookActivityResponse.newBuilder()
                .setRegistrationId(registrationId)
                .setAttendeeId(attendeeId)
                .setActivityId(activityId)
                .setStatus(RegistrationStatus.RESERVED)
                .setMessage("Activity booked successfully")
                .build();

        registrations.put(registrationId, registration);

        return registration;
    }

    public synchronized List<BookActivityResponse> findRegistrationsByAttendee(String attendeeId) {
        List<BookActivityResponse> result = new ArrayList<>();

        for (BookActivityResponse registration : registrations.values()) {
            if (registration.getAttendeeId().equals(attendeeId)) {
                result.add(registration);
            }
        }

        return result;
    }
}