/*
 * HW #4 SMTP 
 * Justin Huffman 
 * Ian Mutahi
 * CS3700
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
                ParseResponse response = new ParseResponse();

                if (this.state == 0) {
                    this.state = initializeConnection(cSocketOut);
                } else if(this.state != 5) {
                    String clientMessage = cSocketIn.readLine();
                    if (clientMessage == null) {
                        System.out.println("Connection closed prematurely by client: " + this.clientAddress.getHostAddress() + ".");
                        this.state = -1;
                        break;
                    }
                    response = parseRequest(this.state, clientMessage);

                    System.out.println("Next State: " + String.valueOf(response.newState));
                    System.out.println("Response: " + response.response);

                    this.state = response.newState;
                    if (response.response != null) {
                        cSocketOut.println(response.response);
                    }

                } else if(this.state == 5) {
                    StringBuilder mailBodyLines = new StringBuilder();
                    String fromClient;
                    while ((fromClient = cSocketIn.readLine()) != null){
                        

                        mailBodyLines.append(fromClient + "\r\n");

                        if(fromClient.equals(".")){
                            break;
                        }
                    }

                    String mailBody = mailBodyLines.toString();
                    response = parseRequest(this.state,mailBody);
                    this.state = response.newState;

                    if (response.response != null) {
                        cSocketOut.println(response.response);
                    }
                } else {
                    this.state = -1;
                    break;
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
         System.out.println("Recieved message: " + clientMessage);

    	 if(currentState == 1) { 
    		 String[] words = clientMessage.split(" ", 2);

    		 if(words[0].equals("HELO")) {
    			 response.newState = 2;
    			 response.response = "250 " + this.hostAddress.getHostAddress() + " Hello " + this.clientAddress.getHostAddress();
    		 } else if (words[0].equals("QUIT")) {
                 response.newState = -1;
                 response.response = "221 " + this.hostAddress.getHostAddress() + " closing connection";
             } else {
                 response.newState = 1;
                 response.response = "503 5.5.2 Send hello first";
             }
    	 } else if(currentState == 2) { 
    		 String[] words = clientMessage.split(" ", 3);
             if (words[0].equals("MAIL") && words[1].equals("FROM:")) {
                 response.newState = 3;
                 response.response = "250 2.1.0 Sender OK";
             } else {
                 response.newState = 2;
                 response.response = "503 5.5.2 Need mail command";
             }

         } else if(currentState == 3) { 
             String[] words = clientMessage.split(" ", 3);
             if (words[0].equals("RCPT") && words[1].equals("TO:")) {
                 response.newState = 4;
                 response.response = "250 2.1.5 Recipient OK";
             } else {
                 response.newState = 3;
                 response.response = "503 5.5.2 Need rcpt command";
             }
         } else if(currentState == 4) { 
             String[] words = clientMessage.split(" ", 3);
             if (words[0].equals("DATA")) {
                 response.newState = 5;
                 response.response = "354 Start mail input; end with <CRLF>.<CRLF>";
             } else {
                 response.newState = 4;
                 response.response = "503 5.5.2 Need data command";
             }
         } else if (currentState == 5){
            response.newState = 1;
            response.response = "250 Message received and to be delivered";
            System.out.println("Recieved Mail:\r\n" + clientMessage);
         } else {
             response.newState = -1;
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
