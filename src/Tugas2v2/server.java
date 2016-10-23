/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tugas2v2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 *
 * @author saptarga
 */
public class server {
    static Vector clients;
    static Socket clientSocket;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        clients = new Vector();
        clientSocket = null;
        ServerSocket serverSocket = null;
        
       try {
            serverSocket = new ServerSocket(2222);
            //Socket clientSocket = serverSocket.accept();
            System.out.println("Server Running...");
            //System.out.println("Server : "+clientSocket.getInetAddress());
           // System.out.println("Port : "+clientSocket.getPort());
        } catch (IOException e) {
            System.out.println("IO" + e);
        }
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                cThread s = new cThread(clientSocket);
                clients.add(s);
                s.start();
            } catch (IOException e) {
                System.out.println("IOaccept" + e);
            }
        }
    }
    
}
