package main.classrooms;

import java.util.HashMap;
import java.util.Map;

public class ClassroomRepository {
    private final Map<String, Classroom> classrooms = new HashMap<>();

    public ClassroomRepository() {
        classrooms.put("E301", new Classroom("E301"));
        classrooms.put("E302", new Classroom("E302"));
        classrooms.put("E303", new Classroom("E303"));
        classrooms.put("E304", new Classroom("E304"));
    }
    public Classroom findById(String id) {
        return classrooms.get(id);
    }
}
