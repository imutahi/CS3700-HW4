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

        while (state == 1) {
            Mail mail = getMailFromUser(sysIn);

            System.out.println("Sender: " + mail.sender);
            System.out.println("Reciever: " + mail.reciever);
            System.out.println("Subject: " + mail.subject);
            System.out.println("Body: " + mail.body);

            state = 2;
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

    private static Mail getMailFromUser(BufferedReader sysIn) {
        Mail mail = new Mail();
        mail.sender = getSender(sysIn);
        mail.reciever = getReciever(sysIn);
        mail.subject = getSubject(sysIn);
        mail.body = getBody(sysIn);
        return mail;
    }

    private static String getSender(BufferedReader sysIn) {
        String sender = null;
        System.out.println("Please input sender's email address: ");
        try {
            sender = sysIn.readLine();
        } catch (IOException e) {
            System.err.println("Couldn't read sender's address.");
        }
        return sender;
    }

    private static String getReciever(BufferedReader sysIn) {
        String reciever = null;
        System.out.println("Please input reciever's email address: ");
        try {
            reciever = sysIn.readLine();
        } catch (IOException e) {
            System.err.println("Couldn't read reciever's address.");
        }
        return reciever;
    }

    private static String getSubject(BufferedReader sysIn) {
        String subject = null;
        System.out.println("Please input the Subject: ");
        try {
            subject = sysIn.readLine();
        } catch (IOException e) {
            System.err.println("Couldn't read the Subject.");
        }
        return subject;
    }

    private static String getBody(BufferedReader sysIn) {
        String bodyLine;
        System.out.println("Please input the e-mail body. End the message body with /'./' on a line by itself.");
        StringBuilder mailBodyLines = new StringBuilder();
        try {
            while((bodyLine = sysIn.readLine()) != null) {
                mailBodyLines.append(bodyLine + "\r\n");
                if(bodyLine.equals(".")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Couldn't read the Subject.");
        }
        return mailBodyLines.toString();
    } 
}
