package jsf.util;

import javax.mail.PasswordAuthentication;

class SMTPAuthenticator extends javax.mail.Authenticator {

    private String username;
    private String password;

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }

    public SMTPAuthenticator(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }
}