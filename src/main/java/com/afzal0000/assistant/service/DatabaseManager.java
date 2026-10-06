package com.afzal0000.assistant.service;

import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.util.stream.Collectors;

public final class DatabaseManager implements AutoCloseable {
    private final Connection connection;
    public DatabaseManager(String dbPath) throws SQLException, IOException {
        Path p=Path.of(dbPath); if(p.getParent()!=null)Files.createDirectories(p.getParent());
        connection=DriverManager.getConnection("jdbc:sqlite:"+dbPath); try(Statement s=connection.createStatement()){s.execute("PRAGMA foreign_keys=ON");}
        String schema; try(var in=DatabaseManager.class.getClassLoader().getResourceAsStream("schema.sql")){if(in==null)throw new IOException("schema.sql missing"); schema=new String(in.readAllBytes());}
        for(String statement:schema.split(";")){if(!statement.isBlank())try(Statement s=connection.createStatement()){s.execute(statement);}}
    }
    public Connection connection(){return connection;}
    @Override public void close() throws SQLException {connection.close();}
}
