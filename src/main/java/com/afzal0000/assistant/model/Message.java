package com.afzal0000.assistant.model;

import java.time.Instant;

public record Message(String id, String sessionId, String role, String textContent, String imageDataRef, Instant timestamp) {}
