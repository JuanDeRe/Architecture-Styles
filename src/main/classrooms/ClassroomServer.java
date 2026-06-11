package main.classrooms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

public class ClassroomServer {

    public static void main(String[] args) throws IOException {
        ClassroomRepository classroomRepository = new ClassroomRepository();
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Server Started");
        Socket clientSocket = serverSocket.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        while (true) {
            String request = in.readLine();
            if (request.trim().equals("exit")) {
                break;
            }
            out.println(processRequest(request,classroomRepository));
        }
        in.close();
        out.close();
        clientSocket.close();
    }

    private static String processRequest(String request, ClassroomRepository classroomRepository) {
        String cleanRequest = request.trim().toUpperCase(Locale.ROOT);
        if (cleanRequest.isEmpty() || !cleanRequest.contains(",")) {
            return "ERROR_OPERACION_INVALIDA";
        }
        String operation = cleanRequest.split(",")[0];
        String parameter = cleanRequest.split(",")[1];
        if (operation == null || operation.isEmpty() || parameter == null || parameter.isEmpty()) {
            return "ERROR_OPERACION_INVALIDA";
        }
        Classroom classroom = classroomRepository.findById(parameter);
        if (classroom == null) {
            return "ERROR_SALON_NO_EXISTE";
        }
        switch (operation) {
            case "CONSULTAR_SALON":
                boolean occupied = classroom.isOccupied();
                if (occupied) {
                    return "SALON_RESERVADO";
                } else  {
                    return "SALON_DISPONIBLE";
                }
            case "RESERVAR_SALON":
                if (classroom.isOccupied()) {
                    return "SALON_RESERVADO";
                } else {
                    classroom.setOccupied();
                    return "RESERVA_EXITOSA";
                }
            case "LIBERAR_SALON":
                classroom.free();
                return "LIBERACION_EXITOSA";

            default:
                return "ERROR_OPERACION_INVALIDA";
        }
    }
}
