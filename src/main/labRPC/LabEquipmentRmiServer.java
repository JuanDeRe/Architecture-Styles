package main.labRPC;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LabEquipmentRmiServer {
    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException, InterruptedException {
        LabEquipmentService labEquipmentService = new LabEquipmentServiceImpl();
        Registry registry = LocateRegistry.createRegistry(23000);
        registry.rebind("LabEquipmentService", labEquipmentService);
        System.out.println("Lab Equipment RMI Server ready, port 23000");
    }
}
