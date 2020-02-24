/*
 * Server App upon TCP
 * A thread is started to handle every client TCP connection to this server
 * Weiying Zhu
 */ 

import java.net.*;
import java.io.*;

public class TCPMultiServerThread extends Thread {
    private Socket clientTCPSocket = null;
    private int state = 0;

    public TCPMultiServerThread(Socket socket) {
		super("TCPMultiServerThread");
		clientTCPSocket = socket;
    }

    public void run() {

		try {
	 	    PrintWriter cSocketOut = new PrintWriter(clientTCPSocket.getOutputStream(), true);
            BufferedReader cSocketIn = new BufferedReader(
                new InputStreamReader(
                    clientTCPSocket.getInputStream()));
			  
            while (this.state != -1) {
                if (this.state == 0) {
                    this.state = initializeConnection(cSocketOut);
                } else {
                    this.state = -1;
                }
				
				// toClient = fromClient.toUpperCase();
				// cSocketOut.println(toClient);
				
				// if (fromClient.equals("Bye"))
				//    break;
            }
			
            cSocketOut.close();
            cSocketIn.close();
            clientTCPSocket.close();

		} catch (IOException e) {
		    e.printStackTrace();
		}
    }

    private int initializeConnection(PrintWriter cSocketOut) {
        String hostname = java.net.InetAddress.getHostName();
        String toClient = "220 " + hostname;
        cSocketOut.println(toClient);
        return 1;
    }
}
