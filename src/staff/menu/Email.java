package staff.menu;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import javax.swing.JOptionPane;

// Abstract class 
public abstract class Email {

    protected final String fromEmail = "violetteviov@gmail.com";
    protected final String password = "ygdq brol bqhl cang";

    // Method abstract
    public abstract void sendText(String toEmail, String subject, String body);
    public abstract void sendWithAttachment(String toEmail, String subject, String body, String attachmentPath);

    // Method helper protected
    protected Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.enable", "false");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });
    }

    // Concrete implementation
    public static class EmailImpl extends Email {

        @Override
        public void sendText(String toEmail, String subject, String body) {
            try {
                Session session = createSession();
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject(subject);
                message.setText(body);

                Transport.send(message);
                JOptionPane.showMessageDialog(null, "Text email sent successfully to " + toEmail);
            } catch (MessagingException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            }
        }

        @Override
        public void sendWithAttachment(String toEmail, String subject, String body, String attachmentPath) {
            try {
                Session session = createSession();
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject(subject);

                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText(body);

                MimeMultipart multipart = new MimeMultipart();
                multipart.addBodyPart(textPart);

                if (attachmentPath != null) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    FileDataSource source = new FileDataSource(attachmentPath);
                    attachmentPart.setDataHandler(new DataHandler(source));
                    attachmentPart.setFileName(source.getName());
                    multipart.addBodyPart(attachmentPart);
                }

                message.setContent(multipart);
                Transport.send(message);
                JOptionPane.showMessageDialog(null, "Email with attachment sent successfully!");
            } catch (MessagingException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            }
        }
    }
}
