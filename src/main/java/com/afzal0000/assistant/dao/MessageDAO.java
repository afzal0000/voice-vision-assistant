package com.afzal0000.assistant.dao;

import com.afzal0000.assistant.model.Message;
import java.sql.*;
import java.time.Instant;
import java.util.*;

public final class MessageDAO {
    private final Connection connection;
    public MessageDAO(Connection connection) { this.connection=connection; }
    public Message save(String sessionId,String role,String text,String imageRef)throws SQLException{
        Message m=new Message(UUID.randomUUID().toString(),sessionId,role,text,imageRef,Instant.now());
        try(PreparedStatement ps=connection.prepareStatement("INSERT INTO messages VALUES (?,?,?,?,?,?)")){ps.setString(1,m.id());ps.setString(2,sessionId);ps.setString(3,role);ps.setString(4,text);ps.setString(5,imageRef);ps.setString(6,m.timestamp().toString());ps.executeUpdate();} return m;
    }
    public List<Message> findRecent(String sessionId,int limit)throws SQLException{
        List<Message> out=new ArrayList<>(); String q="SELECT * FROM (SELECT * FROM messages WHERE session_id=? ORDER BY timestamp DESC LIMIT ?) ORDER BY timestamp ASC";
        try(PreparedStatement ps=connection.prepareStatement(q)){ps.setString(1,sessionId);ps.setInt(2,limit);try(ResultSet r=ps.executeQuery()){while(r.next())out.add(new Message(r.getString("id"),r.getString("session_id"),r.getString("role"),r.getString("text_content"),r.getString("image_data_ref"),Instant.parse(r.getString("timestamp"))));}} return out;
    }
}
