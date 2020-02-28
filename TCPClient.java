/*
 * HW #4 SMTP 
 * Justin Huffman 
 * Ian Mutahi
 * CS3700
 */  

import java.io.*;
import java.net.*;

public class TCPClient {
    public static void main(String[] args) throws IOException {

        Socket tcpSocket = null;
        PrintWriter socketOut = null;
        BufferedReader socketIn = null;

        if (args.length != 1) {
             System.out.println("Usage: java TCPClient <hostname>");
             return;
        }
        
        try {
            tcpSocket = new Socket(args[0], 4567);
            socketOut = new PrintWriter(tcpSocket.getOutputStream(), true);
            socketIn = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: "  + args[0]);
            System.exit(1);
        }

        BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;
        String fromUser;

        while ((fromUser = sysIn.readLine()) != null) {
		      System.out.println("Client: " + fromUser);
            socketOut.println(fromUser);
				
				if ((fromServer = socketIn.readLine()) != null)
				{
					System.out.println("Server: " + fromServer);
				}
				else {
                System.out.println("Server replies nothing!");
                break;
				}
		    
			   if (fromUser.equals("Bye."))
					break;
         
        }

        socketOut.close();
        socketIn.close();
        sysIn.close();
        tcpSocket.close();
    }
}
