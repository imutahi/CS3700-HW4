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

        TCPClient client = new TCPClient();

        int state = 0;
        Socket tcpSocket = null;
        PrintWriter socketOut = null;
        BufferedReader socketIn = null;
        BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
        String fromUser;
        String fromServer;
        Mail mail = new Mail();

        while(state != -1) {
            while (state == 0) {
                ConnectionResponse response = client.connectToServer(sysIn);
                state = response.newState;
                if(state != 0) {
                    tcpSocket = response.tcpSocket;
                    socketOut = response.socketOut;
                    socketIn = response.socketIn;
                }
            }
            if(state == 1) {
                mail = getMailFromUser(sysIn);

                System.out.println("Sender: " + mail.sender);
                System.out.println("Reciever: " + mail.reciever);
                System.out.println("Subject: " + mail.subject);
                System.out.println("Body: " + mail.body);

                state = 2;
            }
            if(state == 2) {
                state = sendMail(state,mail,socketOut,socketIn);
            }
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

    private static int sendMail(int currentState, Mail mail, PrintWriter socketOut, BufferedReader socketIn) {
        int newState = currentState;

        newState = sendHello(newState, socketOut, socketIn);
        if (newState == -1) {
            return -1;
        }
        return newState;
    }

    private static int sendHello(int currentState, PrintWriter socketOut, BufferedReader socketIn){
        int newState = currentState;
        String response;
        socketOut.println("HELO");
        try {
            if((response = socketIn.readLine()) != null) {
                String[] words = response.split(" ", 5);
                if(words[0].equals("220")) {
                    newState++;
                } else {
                    newState = -1;
                    System.out.println("Error sending hello.");
                }
            }
        } catch (IOException e) {
            newState = -1;
            System.out.println("I/O exception while sending hello.");
        }
        return newState;
    }

    private ConnectionResponse connectToServer(BufferedReader sysIn) {

        String fromUser = new String();
        ConnectionResponse response = new ConnectionResponse();
        response.setState(0);

        System.out.println("Enter hostname of mail server: ");

        try {
            if((fromUser = sysIn.readLine()) != null) {

                Socket socket = new Socket(fromUser, 4567);
                PrintWriter socketOut = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                response.setSocket(socket);
                response.setSocketOut(socketOut);
                response.setSocketIn(socketIn);
                response.setState(1);

            }
        } catch (UnknownHostException e) {

            System.err.println("Don't know about host: " + fromUser);
            response.setState(0);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: "  + fromUser);
            response.setState(0);
        }

        return response;
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
        System.out.println("Please input the e-mail body. End the message body with \".\" on a line by itself.");
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

    private class ConnectionResponse {
        public int newState = 0;
        public Socket tcpSocket = null;
        public PrintWriter socketOut = null;
        public BufferedReader socketIn = null;

        public ConnectionResponse() {

        }

        public void setState(int state) {
            this.newState = state;
        }

        public void setSocket(Socket socket) {
            this.tcpSocket = tcpSocket;
        }

        public void setSocketOut(PrintWriter socketOut) {
            this.socketOut = socketOut;
        }

        public void setSocketIn(BufferedReader socketIn) {
            this.socketIn = socketIn;
        }

        public int getState() {
            return this.newState;
        }

        public Socket getSocket() {
            return this.tcpSocket;
        }

        public PrintWriter getSocketOut() {
            return this.socketOut;
        }

        public BufferedReader getSocketIn() {
            return this.socketIn;
        }

    }
}
