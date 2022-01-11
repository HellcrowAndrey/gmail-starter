package com.github.gmail.config;

import com.github.gmail.services.EmailSenderService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.List;

@Configuration
public class EmailConfig {

    @Value(value = "${emails.app.name}")
    private String applicationName;

    @Value(value = "${emails.app.credentials}")
    private String credentials;

    @Value(value = "${emails.app.accessType}")
    private String accessType;

    @Value(value = "${emails.app.token.directory.path}")
    private String tokenDirectoryPath;

    @Value(value = "${emails.app.userId}")
    private String userId;

    @Value(value = "${emails.app.port}")
    private Integer port;

    @Bean
    public Gmail gmail(JsonFactory jsonFactory,
                       Credential credential,
                       NetHttpTransport netHttpTransport) {
        return new Gmail.Builder(netHttpTransport, jsonFactory, credential)
                .setApplicationName(this.applicationName)
                .build();
    }

    @Bean
    @ConditionalOnExpression("${emails.app.default.enabled:true}")
    public NetHttpTransport netHttpTransport() {
        try {
            return GoogleNetHttpTransport.newTrustedTransport();
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException("Can't create http transport, message: " + e.getMessage());
        }
    }

    @Bean
    @ConditionalOnExpression("${emails.app.default.enabled:true}")
    public JsonFactory gsonJsonFactory() {
        return GsonFactory.getDefaultInstance();
    }

    @Bean
    public Credential gmailCredential(GoogleAuthorizationCodeFlow flow) {
        try {
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(this.port).build();
            return new AuthorizationCodeInstalledApp(flow, receiver).authorize(this.userId);
        } catch (IOException e) {
            throw new RuntimeException("Can't load credentials, message: " + e.getMessage());
        }
    }

    @Bean
    @ConditionalOnExpression("${emails.app.default.enabled:true}")
    public List<String> defaultScopes() {
        return List.of(GmailScopes.MAIL_GOOGLE_COM);
    }

    @Bean
    @ConditionalOnExpression("${emails.app.default.enabled:true}")
    public GoogleAuthorizationCodeFlow defaultGoogleAuthorizationCodeFlow(NetHttpTransport httpTransport, JsonFactory jsonFactory, List<String> scopes) {
        try {
            InputStream in;
            ClassPathResource resource = new ClassPathResource(this.credentials);
            if (resource.exists()) {
                in = resource.getInputStream();
            } else {
                in = new FileInputStream(this.credentials);
            }
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));
            return new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, jsonFactory, clientSecrets, scopes)
                    .setDataStoreFactory(new FileDataStoreFactory(new File(this.tokenDirectoryPath)))
                    .setAccessType(this.accessType)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Can't create google auth code flow, message: " + e.getMessage());
        }
    }

    @Bean
    public EmailSenderService emailSenderService(Gmail gmail) {
        return new EmailSenderService(gmail.users().messages());
    }

}
