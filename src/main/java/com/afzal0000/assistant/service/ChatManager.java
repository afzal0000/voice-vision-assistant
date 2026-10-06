package com.afzal0000.assistant.service;

import com.afzal0000.assistant.dao.*;
import com.afzal0000.assistant.model.*;
import java.sql.SQLException;
import java.util.*;

public final class ChatManager {
    public static final String FRIDAY_PROMPT="You are FRIDAY, a calm, highly capable, polite and slightly witty Irish-technological AI assistant. Be concise, helpful, safety-conscious, and transparent about uncertainty. Do not claim to be a real person or reproduce a copyrighted voice; use this persona only as a tone and conversational style.";
    private final SessionDAO sessions; private final MessageDAO messages; private final int max; private Session active;
    public ChatManager(SessionDAO s,MessageDAO m,int max){sessions=s;messages=m;this.max=max;}
    public Session active(){return active;}
    public Session create(String title,String customPrompt)throws SQLException{String p=FRIDAY_PROMPT+(customPrompt==null||customPrompt.isBlank()?"":"\nAdditional user instructions:\n"+customPrompt);active=sessions.create(title,p);return active;}
    public List<Session> list()throws SQLException{return sessions.findAll();}
    public void switchTo(String id)throws SQLException{active=sessions.find(id).orElseThrow(()->new IllegalArgumentException("Session not found: "+id));}
    public void delete(String id)throws SQLException{sessions.delete(id);if(active!=null&&active.id().equals(id))active=null;}
    public List<Message> history()throws SQLException{if(active==null)throw new IllegalStateException("Create or switch to a session first");return messages.findRecent(active.id(),max);}
    public Message saveUser(String text,String imageRef)throws SQLException{return messages.save(active.id(),"user",text,imageRef);}
    public Message saveAssistant(String text)throws SQLException{return messages.save(active.id(),"assistant",text,null);}
}
