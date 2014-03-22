package jsf.util;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;


public class ServicioCorreo {

    /*
    public static synchronized void postMail(String recipients[], String subject, String message) throws MessagingException {
        boolean debug = false;

        //Set the host smtp address
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtpin.csic.es");
        props.put("mail.smtp.auth", "true");

        Authenticator auth = new SMTPAuthenticator("iact_avisos", "acd136Mb9");
        Session session = Session.getInstance(props, auth);
        session.setDebug(debug);

        // create a message
        Message msg = new MimeMessage(session);

        // set the from and to address
        InternetAddress addressFrom = new InternetAddress("iact_avisos@iact.ugr-csic.es");
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            addressTo[i] = new InternetAddress(recipients[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);

        // Optional : You can also set your custom headers in the Email if you Want
        msg.addHeader("MyHeaderName", "myHeaderValue");

        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setContent(message, "UTF8");
        Transport.send(msg);
    }
    */
    
    
    public static synchronized void postMail(String recipients[], String subject, String message) throws MessagingException {
        boolean debug = false;

        //Set the host smtp address
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtpin.csic.es");
        props.put("mail.smtp.auth", "true");

        Authenticator auth = new SMTPAuthenticator("iact_avisos", "acd136Mb9");
        Session session = Session.getInstance(props, auth);
        session.setDebug(debug);

        // create a message
        Message msg = new MimeMessage(session);

        // set the from and to address
        InternetAddress addressFrom = new InternetAddress("iact_avisos@iact.ugr-csic.es");
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            addressTo[i] = new InternetAddress(recipients[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);

        // Optional : You can also set your custom headers in the Email if you Want
        msg.addHeader("charset", "UTF-8");

        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setContent(message, "text/plain");
        Transport.send(msg);
    }
    
}
