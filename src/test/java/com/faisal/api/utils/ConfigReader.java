package com.faisal.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Locale;
import java.util.Properties;

public final class ConfigReader {
    private static final Properties properties = new Properties();

    private ConfigReader() {}

    static {
        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("config.properties file not found");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config.properties", e);
        }
    }

    // Precedence: -Dproperty.name, PROPERTY_NAME environment variable, classpath default.
    public static String get(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = System.getenv(key.toUpperCase(Locale.ROOT).replace('.', '_'));
        }
        if (value == null) {
            value = properties.getProperty(key);
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing or blank configuration: " + key);
        }
        return value.trim();
    }

    public static String baseUrl() {
        String value = get("base.url");
        URI uri = URI.create(value);
        if (!("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()))
                || uri.getHost() == null || uri.getUserInfo() != null
                || uri.getQuery() != null || uri.getFragment() != null) {
            throw new IllegalArgumentException("base.url must be an HTTP(S) URL without credentials, query or fragment");
        }
        return value;
    }

    public static int positiveInt(String key) {
        try {
            int value = Integer.parseInt(get(key));
            if (value <= 0) {
                throw new NumberFormatException();
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(key + " must be a positive integer in milliseconds", e);
        }
    }
}
