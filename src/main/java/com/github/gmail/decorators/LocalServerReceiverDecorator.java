package com.github.gmail.decorators;

import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import java.io.IOException;

@Slf4j
public class LocalServerReceiverDecorator {

    @Getter
    private final LocalServerReceiver localServerReceiver;

    public LocalServerReceiverDecorator(Integer port) {
        this.localServerReceiver = new LocalServerReceiver.Builder().setPort(port).build();
    }

    public LocalServerReceiverDecorator(Integer port, String host) {
        this.localServerReceiver = new LocalServerReceiver.Builder().setHost(host).setPort(port).build();
    }

    @PreDestroy
    public void tearDown() {
        try {
            log.info("Enter: stop local server receiver");
            this.localServerReceiver.stop();
        } catch (IOException e) {
            log.error("Enter: {} ", e.getMessage());
        }
    }

}
