package app.mendnook.hub.config;

import app.mendnook.hub.material.ServiceTokenIssuer;
import app.mendnook.hub.shared.DomainRuleException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

public class FeignSecurityConfig {

    @Bean
    RequestInterceptor serviceBearerToken(ServiceTokenIssuer tokenIssuer) {
        return template -> template.header("Authorization", "Bearer " + tokenIssuer.issue());
    }

    @Bean
    ErrorDecoder materialsErrorDecoder(ObjectMapper objectMapper) {
        ErrorDecoder fallback = new ErrorDecoder.Default();
        return (methodKey, response) -> decodeError(response, objectMapper, fallback, methodKey);
    }

    private Exception decodeError(Response response,
                                  ObjectMapper objectMapper,
                                  ErrorDecoder fallback,
                                  String methodKey) {
        if (response.status() == 400 || response.status() == 404
                || response.status() == 409 || response.status() == 422) {
            return new DomainRuleException(readMessage(response, objectMapper));
        }
        if (response.status() == 401 || response.status() == 403) {
            return new DomainRuleException("The main application could not authenticate with the materials service");
        }
        return fallback.decode(methodKey, response);
    }

    private String readMessage(Response response, ObjectMapper objectMapper) {
        if (response.body() == null) {
            return "The materials service rejected this operation";
        }
        try {
            JsonNode body = objectMapper.readTree(response.body().asInputStream());
            return body.path("message").asText("The materials service rejected this operation");
        } catch (IOException exception) {
            return "The materials service rejected this operation";
        }
    }
}
