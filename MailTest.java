class MailTest {
    public static void main(String[] args) {
        Mail mail = new Mail();
        mail.sender = "blah@blah.edu";
        mail.reciever = "hi@hi.net";
        mail.subject = "This is my subject line.";
        mail.body = "Hi hi,\r\n" +
                    "\r\n" +
                    "This is blah.\r\n" +
                    "How are you doing?\r\n" +
                    "\r\n" +
                    "\r\n" +
                    "\r\n" +
                    "\r\n" +
                    "\r\n.\r\n";


        System.out.println(mail.getBody());
    }
}
