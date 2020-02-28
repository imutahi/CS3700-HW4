/*
 * Server App upon TCP
 * A thread is started to handle every client TCP connection to this server
 * Weiying Zhu
 */ 

import java.net.*;
import java.io.*;

public class TCPMultiServerThread extends Thread {
    private Socket clientTCPSocket = null;
    private InetAddress hostAddress;
    private InetAddress clientAddress;
    private int state = 0;
    private String hostname;
    
    public TCPMultiServerThread(Socket socket, String hostname) {
		super("TCPMultiServerThread");
		clientTCPSocket = socket;
        this.hostname = hostname;
    }
    
    public void run() {

		try {
			this.clientAddress = clientTCPSocket.getInetAddress();
			this.hostAddress = clientTCPSocket.getLocalAddress();
	 	    PrintWriter cSocketOut = new PrintWriter(clientTCPSocket.getOutputStream(), true);
            BufferedReader cSocketIn = new BufferedReader(
                new InputStreamReader(
                    clientTCPSocket.getInputStream()));
			  
            while (this.state != -1) {
                if (this.state == 0) {
                    this.state = initializeConnection(cSocketOut);
                } else if(this.state == 1) {
                    String clientMessage = cSocketIn.readLine();
                    ParseResponse response = parseRequest(this.state, clientMessage);
                    this.state = response.newState;
                    if (response.response != null) {cSocketOut.print(response.response);}
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

    ParseResponse parseRequest(int currentState, String clientMessage) {
         ParseResponse response = new ParseResponse();

    	 if(currentState == 1) { 
    		 String[] words = clientMessage.split(" ", 2);

    		 if(words[0].equals("HELO")) {
    			 response.newState = 2;
    			 response.response = "250 " + this.hostAddress.getHostAddress() + " Hello " + this.clientAddress.getHostAddress() + "\r\n";
    		 } else if (words[0].equals("QUIT")) {
                 response.newState = -1;
                 response.response = "221 " + this.hostAddress.getHostAddress() + " closing connection\r\n";
             } else {
                 response.newState = 1;
                 response.response = "503 5.5.2 Send hello first\r\n";
             }
    	 }

         return response;
    }

    private int initializeConnection(PrintWriter cSocketOut) {
        String toClient = "220 " + this.hostname;
        cSocketOut.println(toClient);
        return 1;
    }
    
}
class ParseResponse {
	  String response;
	  int newState;	  
}
