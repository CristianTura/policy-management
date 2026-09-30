package com.api.policy_management.client;

import com.api.policy_management.dto.CoreEventRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CoreClient {

    private static final Logger log = LoggerFactory.getLogger(CoreClient.class);

    private final RestClient restClient;
    private final String apiKey;

    public CoreClient(
            @Value("${app.core-mock-url}") String coreMockUrl,
            @Value("${app.api-key}") String apiKey
    ) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder().baseUrl(coreMockUrl).build();
    }

    public void notifyUpdate(Long policyId) {
        try {
            restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-api-key", apiKey)
                    .body(new CoreEventRequest("ACTUALIZACION", policyId))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            log.warn("No se pudo notificar al CORE mock para polizaId={}: {}", policyId, ex.getMessage());
        }
    }
}
