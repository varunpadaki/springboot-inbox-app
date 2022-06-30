package com.springboot.inbox.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.inbox.domain.AuthClaims;
import com.springboot.inbox.exception.InboxAuthException;
import com.springboot.inbox.utils.InboxAppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class InboxAppHelper {

    @Value("${inboxapp.token.validate.uri}")
    private String tokenValidateUri;

    private Logger logger = LoggerFactory.getLogger(InboxAppHelper.class);

    public AuthClaims validateTokenAndReturnClaims(String authToken) throws InboxAuthException {
        String errorMessage = "";
        try {
            Map<String, Object> pathVariableMap = new HashMap<String, Object>();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.setBearerAuth(authToken.split(InboxAppConstants.SPACE_DELIMITER)[1]);
            HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(tokenValidateUri, HttpMethod.POST, httpEntity,
                    String.class, pathVariableMap);
            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                logger.info("Auth token validated successfully, returning claims.");
                String authClaimStr = response.getBody();
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(authClaimStr, AuthClaims.class);
            }
            errorMessage = "Failed to validate auth token for better reads.";
        } catch (JsonProcessingException e) {
            logger.error("JsonProcessing exception occurred,failed to validate auth token", e);
            errorMessage = e.getMessage();
        } catch (Exception e) {
            logger.error("Error occurred,failed to validate auth token", e);
            errorMessage = e.getMessage();
        }
        throw new InboxAuthException(errorMessage);
    }
}
