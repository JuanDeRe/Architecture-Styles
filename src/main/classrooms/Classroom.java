package main.classrooms;

public class Classroom {
    private String id;
    private boolean occupied;
    public Classroom(String id) {
        this.id = id;
        occupied = false;
    }
    public String getId() {
        return id;
    }
    public boolean isOccupied() {
        return occupied;
    }
    public void setOccupied() {
        this.occupied = true;
    }

    public void free() {
        this.occupied = false;
    }
}
