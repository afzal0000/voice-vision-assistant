package com.afzal0000.assistant.dao;

import com.afzal0000.assistant.model.Session;
import java.sql.*;
import java.time.Instant;
import java.util.*;

public final class SessionDAO {
    private final Connection connection;
    public SessionDAO(Connection connection) { this.connection = connection; }
    public Session create(String title, String prompt) throws SQLException {
        String id = UUID.randomUUID().toString(); Instant now = Instant.now();
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO sessions VALUES (?,?,?,?,?)")) {
            ps.setString(1,id); ps.setString(2,title); ps.setString(3,prompt); ps.setString(4,now.toString()); ps.setString(5,now.toString()); ps.executeUpdate();
        }
        return new Session(id,title,prompt,now,now);
    }
    public Optional<Session> find(String id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM sessions WHERE id=?")) { ps.setString(1,id); try (ResultSet r=ps.executeQuery()) { return r.next()?Optional.of(map(r)):Optional.empty(); } }
    }
    public List<Session> findAll() throws SQLException {
        List<Session> out=new ArrayList<>(); try (PreparedStatement ps=connection.prepareStatement("SELECT * FROM sessions ORDER BY updated_at DESC"); ResultSet r=ps.executeQuery()) { while(r.next()) out.add(map(r)); } return out;
    }
    public void delete(String id) throws SQLException { try (PreparedStatement ps=connection.prepareStatement("DELETE FROM sessions WHERE id=?")){ps.setString(1,id);ps.executeUpdate();} }
    public void touch(String id) throws SQLException { try (PreparedStatement ps=connection.prepareStatement("UPDATE sessions SET updated_at=? WHERE id=?")){ps.setString(1,Instant.now().toString());ps.setString(2,id);ps.executeUpdate();} }
    private Session map(ResultSet r)throws SQLException{return new Session(r.getString("id"),r.getString("title"),r.getString("system_prompt"),Instant.parse(r.getString("created_at")),Instant.parse(r.getString("updated_at")));}
}
