package ru.isupden.vpnbot.vpn.outline;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import ru.isupden.vpnbot.vpn.outline.dto.CreateAccessKeyRequest;
import ru.isupden.vpnbot.vpn.outline.dto.CreateAccessKeyResponse;
import ru.isupden.vpnbot.vpn.outline.dto.SetLimitRequest;

@Service
public class OutlineClient {
    private final RestClient restClient;
    private final String url;

    public OutlineClient(@Value("${outline.server}")String server, @Value("${outline.port}")int port, @Value("${outline.api-key}")String apiKey) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(@NonNull HttpURLConnection connection, @NonNull String httpMethod) throws IOException {
                if (connection instanceof HttpsURLConnection) {
                    ((HttpsURLConnection) connection).setHostnameVerifier((hostname, session) -> true);
                }
                super.prepareConnection(connection, httpMethod);
            }
        });

        this.restClient = RestClient.create(restTemplate);
        this.url = "https://%s:%s/%s".formatted(server, port, apiKey);
    }

    public CreateAccessKeyResponse createUser(CreateAccessKeyRequest request) {
        return restClient.post()
                .uri(url + "/access-keys")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(CreateAccessKeyResponse.class);
    }

    public void SetLimit(String keyId, SetLimitRequest request) {
        restClient.put()
                .uri(url + "/access-keys/" + keyId + "/data-limit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Void.class);
    }

}
