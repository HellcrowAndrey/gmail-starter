package com.github.gmail.services;

import com.github.gmail.exceptions.ExecuteSendEmailException;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static com.github.gmail.utils.EmailMessageUtils.ofMessage;

public class EmailSenderService {

    private final Gmail.Users.Messages messages;

    private final String userId;

    public EmailSenderService(Gmail.Users.Messages messages, String userId) {
        this.messages = messages;
        this.userId = userId;
    }

    public Message send(MimeMessage payload) {
        return send(this.userId, payload);
    }

    public Message send(String userId, MimeMessage payload) {
        try {
            return this.messages.send(userId, ofMessage(payload)).execute();
        } catch (IOException e) {
            throw new ExecuteSendEmailException(e.getMessage());
        }
    }

}
