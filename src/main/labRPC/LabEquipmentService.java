package main.labRPC;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface LabEquipmentService extends Remote {
    List<String> getLabEquipments() throws RemoteException;
    String getLabEquipment(String id) throws RemoteException;
    boolean reserveLabEquipment(String id) throws RemoteException;
    boolean releaseLabEquipment(String id) throws RemoteException;
}
