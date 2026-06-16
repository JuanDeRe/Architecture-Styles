package main.labRPC;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class LabEquipmentServiceImpl extends UnicastRemoteObject implements LabEquipmentService{

    private final LabEquipmentRepository repository;

    protected LabEquipmentServiceImpl() throws RemoteException {
        super();
        this.repository = new LabEquipmentRepository();
    }

    @Override
    public List<String> getLabEquipments() throws RemoteException {
        List<String> list = new ArrayList<>();
        for(LabEquipment labEquipment : repository.getAll()){
            list.add(labEquipment.toString());
        }
        return list;
    }

    @Override
    public String getLabEquipment(String id) throws RemoteException {
        return repository.getById(id).toString();
    }

    @Override
    public boolean reserveLabEquipment(String id) throws RemoteException {
        LabEquipment labEquipment = repository.getById(id);
        if(labEquipment == null){
            return false;
        }
        if(!labEquipment.isAvailable()){
            return false;
        }
        labEquipment.setAvailable(false);
        return true;
    }

    @Override
    public boolean releaseLabEquipment(String id) throws RemoteException {
        LabEquipment labEquipment = repository.getById(id);
        if(labEquipment == null){
            return false;
        }
        labEquipment.setAvailable(true);
        return true;
    }
    }
