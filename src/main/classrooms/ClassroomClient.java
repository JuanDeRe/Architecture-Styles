package main.classrooms;

import jdk.jfr.Frequency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClassroomClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1",5000);
        Scanner scanner = new Scanner(System.in);
        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while (true){
            System.out.print("Escriba una consulta: ");
            String request = scanner.nextLine();
            out.println(request);
            if(request.trim().equals("exit")){
                break;
            }
            System.out.println(in.readLine());
        }
        in.close();
        out.close();
        socket.close();
    }
}
