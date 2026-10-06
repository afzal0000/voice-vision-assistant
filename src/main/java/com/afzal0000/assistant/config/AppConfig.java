package com.afzal0000.assistant.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class AppConfig {
    private final Properties properties = new Properties();
    public AppConfig() { this("application.properties"); }
    public AppConfig(String resource) {
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream(resource)) {
            if (in != null) properties.load(in);
        } catch (IOException e) { throw new IllegalStateException("Unable to load configuration", e); }
    }
    public String get(String key, String fallback) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) value = properties.getProperty(key);
        if (value == null || value.isBlank()) return fallback;
        if (value.startsWith("${") && value.endsWith("}")) return fallback;
        return value.trim();
    }
    public String apiKey() { return get("GEMINI_API_KEY", get("API_KEY", "")); }
    public String dbPath() { return get("DB_PATH", "data/assistant.db"); }
    public String geminiEndpoint() { return get("GEMINI_ENDPOINT", "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"); }
    public String sttEndpoint() { return get("STT_ENDPOINT", ""); }
    public String sttKey() { return get("STT_API_KEY", ""); }
    public String ttsEndpoint() { return get("TTS_ENDPOINT", ""); }
    public String ttsKey() { return get("TTS_API_KEY", ""); }
    public int maxContextMessages() { return Integer.parseInt(get("MAX_CONTEXT_MESSAGES", "30")); }
}
