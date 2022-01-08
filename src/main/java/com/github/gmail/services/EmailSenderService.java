package com.github.gmail.services;

import com.github.gmail.exceptions.ExecuteSendEmailException;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static com.github.gmail.utils.EmailMessageUtils.ofMessage;

public class EmailSenderService {

    private final Gmail.Users.Messages messages;

    public EmailSenderService(Gmail.Users.Messages messages) {
        this.messages = messages;
    }

    public Message send(String userId, MimeMessage message) {
        try {
            return this.messages.send(userId, ofMessage(message)).execute();
        } catch (IOException e) {
            throw new ExecuteSendEmailException(e.getMessage());
        }
    }

}
