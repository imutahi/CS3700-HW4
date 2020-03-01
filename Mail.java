public class Mail {
    public String sender;
    public String reciever;
    public String subject;
    public String body;

    public String getBody() {
        StringBuilder response = new StringBuilder();
        response.append("To: " + this.reciever + "\r\n");
        response.append("From: " + this.sender + "\r\n");
        response.append("Subject: " + this.subject + "\r\n");
        response.append("\r\n");
        response.append(this.body);
        return response.toString();
    }
}
