package main.labRPC;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class LabEquipmentClient {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 23000);
        LabEquipmentService service = (LabEquipmentService) registry.lookup("LabEquipmentService");
        List<String> labEquipments = service.getLabEquipments();
        System.out.println(labEquipments);
        System.out.println("Reservando equipo " + labEquipments.get(0).substring(0,4));
        boolean result = service.reserveLabEquipment(labEquipments.get(0).substring(0,4));
        System.out.println("Resultado: " + (result ? "Reserva exitosa" : "Ya esta reservado"));
        System.out.println(service.getLabEquipments());
        System.out.println("Liberando equipo " + labEquipments.get(0).substring(0,4));
        result = service.releaseLabEquipment(labEquipments.get(0).substring(0,4));
        System.out.println("Resultado: " + (result ? "Equipo liberado" : "No se pudo liberar"));
        System.out.println(service.getLabEquipments());
    }
}
