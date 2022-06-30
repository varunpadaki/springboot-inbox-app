package com.springboot.inbox.utils;

public interface InboxAppConstants {
    String ACTUATOR_URI = "/actuator";
    String AUTHORIZATION = "authorization";
    String BEARER = "Bearer";
    String SPACE_DELIMITER = " ";
    String JWT_TOKEN_VALIDATION_FAILURE_MSG = "Invalid JWT token, cannot access API.";
    String CONTENT_TYPE_JSON = "application/json";

    String OPTIONS = "options";
    String SENT_LABEL = "Sent";
}
