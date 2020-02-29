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

        int state = 0;
        Socket tcpSocket = null;
        PrintWriter socketOut = null;
        BufferedReader socketIn = null;
        BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
        String fromUser;
        String fromServer;

        while (state == 0) {
            System.out.println("Enter hostname of mail server: ");
            if((fromUser = sysIn.readLine()) != null) {
                try {
                    tcpSocket = new Socket(fromUser, 4567);
                    socketOut = new PrintWriter(tcpSocket.getOutputStream(), true);
                    socketIn = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
                    state = 1;
                } catch (UnknownHostException e) {
                    System.err.println("Don't know about host: " + fromUser);
                } catch (IOException e) {
                    System.err.println("Couldn't get I/O for the connection to: "  + fromUser);
                }
            }

        }


        if ((fromServer = socketIn.readLine()) != null) {
            System.out.println("Server: " + fromServer);
        }

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
