package com.ticketing.storage;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonStorageService {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {

        // Support LocalDateTime
        mapper.registerModule(new JavaTimeModule());

        // Save dates as readable text
        mapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );

        // Pretty-print JSON
        mapper.enable(
                SerializationFeature.INDENT_OUTPUT
        );

        // Ignore unknown fields when importing
        mapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false
        );
    }

    private JsonStorageService() {
        // Prevent instantiation
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }
}