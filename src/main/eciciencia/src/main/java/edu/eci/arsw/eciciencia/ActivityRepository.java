package edu.eci.arsw.eciciencia;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ActivityRepository {

    private final Map<String, ActivityResponse> activities = new LinkedHashMap<>();

    public ActivityRepository() {
        addActivity("A1", "Opening Talk", "Welcome to ECICIENCIA",
                ActivityType.TALK, "20/06/2026", "08:00", "09:00",
                "Main Auditorium", 100);

        addActivity("A2", "gRPC Workshop", "Practical workshop about gRPC services",
                ActivityType.WORKSHOP, "20/06/2026", "10:00", "12:00",
                "Lab 1", 20);

        addActivity("A3", "Robotics Experience", "Interactive robotics demonstration",
                ActivityType.TECHNOLOGY_EXPERIENCE, "20/06/2026", "14:00", "16:00",
                "Innovation Room", 30);

        addActivity("A4", "Software Architecture Conference", "Conference about distributed systems",
                ActivityType.CONFERENCE, "21/06/2026", "09:00", "11:00",
                "Auditorium 2", 80);
    }

    private void addActivity(
            String id,
            String title,
            String description,
            ActivityType type,
            String date,
            String startTime,
            String endTime,
            String location,
            int capacity
    ) {
        ActivityResponse activity = ActivityResponse.newBuilder()
                .setId(id)
                .setTitle(title)
                .setDescription(description)
                .setType(type)
                .setDate(date)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setLocation(location)
                .setCapacity(capacity)
                .setOccupiedSlots(0)
                .build();

        activities.put(id, activity);
    }

    public List<ActivityResponse> getAll() {
        return new ArrayList<>(activities.values());
    }

    public ActivityResponse findById(String activityId) {
        return activities.get(activityId);
    }

    public List<ActivityResponse> findByTimeSlot(String date, String startTime, String endTime) {
        List<ActivityResponse> result = new ArrayList<>();

        LocalTime requestedStart = LocalTime.parse(startTime);
        LocalTime requestedEnd = LocalTime.parse(endTime);

        for (ActivityResponse activity : activities.values()) {
            if (!activity.getDate().equals(date)) {
                continue;
            }

            LocalTime activityStart = LocalTime.parse(activity.getStartTime());
            LocalTime activityEnd = LocalTime.parse(activity.getEndTime());

            boolean overlaps =
                    activityStart.isBefore(requestedEnd) &&
                            activityEnd.isAfter(requestedStart);

            if (overlaps) {
                result.add(activity);
            }
        }

        return result;
    }

    public synchronized CapacityResponse checkAndOccupySlot(String activityId) {
        ActivityResponse activity = activities.get(activityId);

        if (activity == null) {
            return CapacityResponse.newBuilder()
                    .setActivityId(activityId)
                    .setAvailable(false)
                    .setCapacity(0)
                    .setOccupiedSlots(0)
                    .setMessage("Activity not found")
                    .build();
        }

        if (activity.getOccupiedSlots() >= activity.getCapacity()) {
            return CapacityResponse.newBuilder()
                    .setActivityId(activityId)
                    .setAvailable(false)
                    .setCapacity(activity.getCapacity())
                    .setOccupiedSlots(activity.getOccupiedSlots())
                    .setMessage("Activity is full")
                    .build();
        }

        int newOccupiedSlots = activity.getOccupiedSlots() + 1;

        ActivityResponse updatedActivity = ActivityResponse.newBuilder(activity)
                .setOccupiedSlots(newOccupiedSlots)
                .build();

        activities.put(activityId, updatedActivity);

        return CapacityResponse.newBuilder()
                .setActivityId(activityId)
                .setAvailable(true)
                .setCapacity(updatedActivity.getCapacity())
                .setOccupiedSlots(updatedActivity.getOccupiedSlots())
                .setMessage("Slot occupied successfully")
                .build();
    }
}