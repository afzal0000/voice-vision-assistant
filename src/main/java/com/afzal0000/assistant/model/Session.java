package com.afzal0000.assistant.model;

import java.time.Instant;

public record Session(String id, String title, String systemPrompt, Instant createdAt, Instant updatedAt) {}
