package main.labRPC;

import java.io.Serializable;

public class LabEquipment implements Serializable {
    private final String id;
    private final String name;
    private final String laboratory;
    private boolean isAvailable;
    public LabEquipment(String id, String name, String laboratory){
        this.id = id;
        this.name = name;
        this.laboratory = laboratory;
        this.isAvailable = true;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getLaboratory() {
        return laboratory;
    }
    public boolean isAvailable() {
        return isAvailable;
    }
    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return getId() + " " + getName() + " " + getLaboratory() + " -> Available:" + isAvailable;
    }
}
