package com.github.gmail.utils;

import com.github.gmail.exceptions.CreateEmailException;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.codec.binary.Base64;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

public class EmailMessageUtils {

    public static final String SUBTYPE_HTML = "html";

    public static MimeMessage ofEmailWithText(String to, String from, String subject, String bodyText) {
        return ofEmailWithText(to, from, subject, bodyText, null, "plain");
    }

    public static MimeMessage ofEmailWithText(String to, String from, String subject, String bodyText, String charset, String subtype) {
        try {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props);
            MimeMessage email = new MimeMessage(session);
            email.setFrom(new InternetAddress(from));
            email.addRecipient(javax.mail.Message.RecipientType.TO,
                    new InternetAddress(to));
            email.setSubject(subject);
            email.setText(bodyText, charset, subtype);
            return email;
        } catch (MessagingException e) {
            throw new CreateEmailException(e.getMessage());
        }
    }

    public static Message ofMessage(MimeMessage emailContent) {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            emailContent.writeTo(buffer);
            byte[] bytes = buffer.toByteArray();
            String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
            Message message = new Message();
            message.setRaw(encodedEmail);
            return message;
        } catch (IOException | MessagingException e) {
            throw new CreateEmailException(e.getMessage());
        }
    }

}
