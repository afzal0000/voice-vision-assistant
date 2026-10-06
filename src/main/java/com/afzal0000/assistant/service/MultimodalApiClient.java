package com.afzal0000.assistant.service;

import com.afzal0000.assistant.config.AppConfig;
import com.afzal0000.assistant.model.Message;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;

public final class MultimodalApiClient {
    private final AppConfig config; private final ObjectMapper json=new ObjectMapper(); private final HttpClient http=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();
    public MultimodalApiClient(AppConfig config){this.config=config;}
    public String generate(String systemPrompt,List<Message> history,String userText,byte[] image) throws IOException,InterruptedException {
        if(config.apiKey().isBlank()) return "API key is not configured. Set GEMINI_API_KEY, then ask again.";
        ObjectNode root=json.createObjectNode(); ArrayNode contents=root.putArray("contents");
        for(Message m:history){ObjectNode c=contents.addObject();c.put("role",m.role().equals("assistant")?"model":"user");c.putArray("parts").addObject().put("text",m.textContent());}
        ObjectNode current=contents.addObject(); ArrayNode parts=current.putArray("parts"); parts.addObject().put("text",userText);
        if(image!=null){ObjectNode data=parts.addObject().putObject("inline_data");data.put("mime_type","image/jpeg");data.put("data",Base64.getEncoder().encodeToString(image));}
        root.putObject("system_instruction").putArray("parts").addObject().put("text",systemPrompt);
        HttpRequest request=HttpRequest.newBuilder(URI.create(config.geminiEndpoint()+"?key="+config.apiKey())).header("Content-Type","application/json").timeout(Duration.ofMinutes(2)).POST(HttpRequest.BodyPublishers.ofString(root.toString())).build();
        HttpResponse<String> response=http.send(request,HttpResponse.BodyHandlers.ofString());
        if(response.statusCode()/100!=2) throw new IOException("Model API HTTP "+response.statusCode()+": "+response.body());
        JsonNode candidates=json.readTree(response.body()).path("candidates"); if(!candidates.isArray()||candidates.isEmpty())return "The model returned no response.";
        return candidates.get(0).path("content").path("parts").path(0).path("text").asText("The model returned an empty response.");
    }
}
