//package com.example.firebaseapp;
//
//import android.os.AsyncTask;
//
//import java.util.Properties;
//import javax.mail.Authenticator;
//import javax.mail.Message;
//import javax.mail.MessagingException;
//import javax.mail.PasswordAuthentication;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//
//public class EmailSender extends AsyncTask<Void, Void, Void> {
//
//    private String email;
//    private String clubName;
//
//    public EmailSender(String email, String clubName) {
//        this.email = email;
//        this.clubName = clubName;
//    }
//
//    @Override
//    protected Void doInBackground(Void... voids) {
//        Properties props = new Properties();
//        props.put("mail.smtp.host", "smtp.gmail.com"); // Adres serwera SMTP
//        props.put("mail.smtp.port", "587"); // Port
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//
//        Session session = Session.getInstance(props, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication("kacperadamczyk2015@gmail.com", "5102repcaK"); // Podaj swoje dane
//            }
//        });
//
//        try {
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress("kacperadamczyk2015@gmail.com")); // Adres e-mail nadawcy
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email)); // Adres e-mail odbiorcy
//            message.setSubject("Dołączenie do " + clubName);
//            message.setText("Gratulacje! Zostałeś członkiem koła naukowego: " + clubName);
//
//            Transport.send(message);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}
