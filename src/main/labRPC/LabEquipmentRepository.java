package main.labRPC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LabEquipmentRepository {
    private final Map<String, LabEquipment> equipments = new HashMap<>();
    public LabEquipmentRepository(){
        equipments.put("L101",new LabEquipment("L101","Gafas","E303"));
        equipments.put("L102",new LabEquipment("L102","Microscopio","E303"));
        equipments.put("L103",new LabEquipment("L103","Acelerador de partículas","E304"));
    }

    public LabEquipment getById(String id){
        return equipments.get(id);
    }
    public List<LabEquipment> getAll(){
        return new ArrayList<>(equipments.values());
    }
}
