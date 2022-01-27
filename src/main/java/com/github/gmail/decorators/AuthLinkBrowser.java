package com.github.gmail.decorators;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.util.Preconditions;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
public class AuthLinkBrowser implements AuthorizationCodeInstalledApp.Browser {

    private final Consumer<String> linkReceiver;

    public AuthLinkBrowser(Consumer<String> linkReceiver) {
        this.linkReceiver = linkReceiver;
    }

    @Override
    public void browse(String url) throws IOException {
        Preconditions.checkNotNull(url);
        log.info("Please open the following address in your browser: {}", url);
        if (Objects.nonNull(this.linkReceiver)) {
            this.linkReceiver.accept(url);
        }
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    log.info("Attempting to open that address in the default browser now...");
                    desktop.browse(URI.create(url));
                }
            }
        } catch (IOException e) {
            log.warn("Unable to open browser {}", e.getMessage());
        } catch (InternalError e) {
            log.warn("Unable to open browser: {}", e.getMessage());
        }
    }

}
