package wolfgang.utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class MailUtils {

    public static void sendVerificationEmail(String recipientEmail, String token) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "false"); // false puisqu'on utilise un faux server local
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.host", "172.22.176.1");
        props.put("mail.smtp.port", "1025");

        Session session = Session.getInstance(props);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("noreply@wolfgang.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Vérification de votre compte Wolfgang");

            String link = "http://localhost:8080/wolfgang/verify?token=" + token;
            message.setText("Bienvenue ! Cliquez ici pour valider votre compte : " + link);

            Transport.send(message);
            System.out.println("Mail encoyé vers FakeSTMP");
        } catch (MessagingException e) {
            System.err.println("Erreur envoie mail : " + e.getMessage());
            e.printStackTrace();
        }
    }
}