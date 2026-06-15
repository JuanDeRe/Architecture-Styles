package main.classroomsHttp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import main.classrooms.Classroom;
import main.classrooms.ClassroomRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class ClassroomHttpServer {
    public static void main(String[] args) throws Exception{
        HttpServer server = HttpServer.create(new InetSocketAddress(8080),0);
        ClassroomRepository repository = new ClassroomRepository();
        server.createContext("/rooms", new ClassroomHandler(repository));
        server.createContext("/rooms/reserve", new ClassroomReserveHandler(repository));
        server.createContext("/rooms/release", new ClassroomReleaseHandler(repository));
        server.setExecutor(null);
        server.start();
        System.out.println("Classroom Server escuchando en http://localhost:8080");
    }
    static class ClassroomHandler implements HttpHandler {

        private final ClassroomRepository repository;

        public ClassroomHandler(ClassroomRepository repository) {
            this.repository = repository;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String query = exchange.getRequestURI().getQuery();
                String response = processQuery(query, exchange);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private String processQuery(String query, HttpExchange exchange) throws IOException {
            String response;
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                response = "Método no permitido";
                exchange.sendResponseHeaders(405, response.getBytes().length);
                return response;
            }
            if (query == null || query.isBlank()) {
                response = repository.getAll().toString();
                System.out.println(response);
                exchange.sendResponseHeaders(200, response.getBytes().length);
                return response;
            }
            if (!query.startsWith("id=")) {
                response = "Petición no válida";
                exchange.sendResponseHeaders(400, response.getBytes().length);
                return response;
            }
            String roomId = query.split("=")[1];
            Classroom classroom = repository.findById(roomId);
            if (classroom == null) {
                response = "Salón no encontrado";
                exchange.sendResponseHeaders(404, response.getBytes().length);
                return response;
            }
            response = classroom.toString();
            exchange.sendResponseHeaders(200, response.getBytes().length);
            return response;
        }
    }

    static class ClassroomReserveHandler implements HttpHandler {

        private final ClassroomRepository repository;

        public ClassroomReserveHandler (ClassroomRepository repository){
            this.repository = repository;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String response = processQuery(query, exchange);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String processQuery(String query, HttpExchange exchange) throws IOException {
            String response;
            if( query == null || !query.startsWith("id=") || !exchange.getRequestMethod().equalsIgnoreCase("POST")){
                response = "Petición no valida";
                exchange.sendResponseHeaders(500,response.getBytes(StandardCharsets.UTF_8).length);
                return response;
            }
            String roomId = query.split("=")[1];
            Classroom classroom = repository.findById(roomId);
            if (classroom == null){
                response = "Salón no encontrado";
                exchange.sendResponseHeaders(400,response.getBytes(StandardCharsets.UTF_8).length);
                return response;
            }
            if (classroom.isOccupied()){
                response = "Salón ya reservado";
                exchange.sendResponseHeaders(200,response.getBytes(StandardCharsets.UTF_8).length);
                return response;
            }
            classroom.setOccupied();
            response = classroom.getId() + " ahora reservado!";
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            return response;
        }
    }
    static class ClassroomReleaseHandler implements HttpHandler {

        private final ClassroomRepository repository;

        public ClassroomReleaseHandler (ClassroomRepository repository){
            this.repository = repository;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String response = processQuery(query, exchange);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String processQuery(String query, HttpExchange exchange) throws IOException {
            String response;
            if( query == null || !query.startsWith("id=") || !exchange.getRequestMethod().equalsIgnoreCase("POST")){
                response = "Petición no valida";
                exchange.sendResponseHeaders(500,response.getBytes(StandardCharsets.UTF_8).length);
                return response;
            }
            String roomId = query.split("=")[1];
            Classroom classroom = repository.findById(roomId);
            if (classroom == null){
                response = "Salón no encontrado";
                exchange.sendResponseHeaders(400,response.getBytes(StandardCharsets.UTF_8).length);
                return response;
            }
            classroom.free();
            response = classroom.getId() + " ahora libre!";
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            return response;
        }
    }
}
