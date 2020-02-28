/*
 * HW #4 SMTP 
 * Justin Huffman 
 * Ian Mutahi
 * CS3700
 */  

import java.net.*;
import java.io.*;

public class TCPMultiServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverTCPSocket = null;
        boolean listening = true;
        String hostname = InetAddress.getLocalHost().getCanonicalHostName();

        try {
            serverTCPSocket = new ServerSocket(4567);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4567.");
            System.exit(-1);
        }

        while (listening){
            new TCPMultiServerThread(serverTCPSocket.accept(), hostname).start();
        }
			
        serverTCPSocket.close();
    }
}
