package com.afzal0000.assistant.ui;

import com.afzal0000.assistant.config.AppConfig;
import com.afzal0000.assistant.dao.*;
import com.afzal0000.assistant.model.*;
import com.afzal0000.assistant.service.*;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

public final class ConsoleApplication {
    public static void main(String[] args) throws Exception {
        AppConfig config=new AppConfig();
        try(DatabaseManager db=new DatabaseManager(config.dbPath()); CameraService camera=new CameraService(); AudioService audio=new AudioService()){
            ChatManager chat=new ChatManager(new SessionDAO(db.connection()),new MessageDAO(db.connection()),config.maxContextMessages());
            MultimodalApiClient api=new MultimodalApiClient(config); BufferedReader input=new BufferedReader(new InputStreamReader(System.in)); byte[] image=null;
            System.out.println("FRIDAY Voice & Vision Assistant | type /help for commands");
            while(true){System.out.print("\n"+(chat.active()==null?"no-session":chat.active().title())+"> ");String line=input.readLine();if(line==null||line.equalsIgnoreCase("/quit"))break;if(line.isBlank())continue;
                if(line.startsWith("/")){String[] p=line.split(" ",3);try{switch(p[0].toLowerCase()){case "/help"->help();case "/new"->{String prompt=p.length>2?p[2]:"";Session s=chat.create(p.length>1?p[1]:"Conversation",prompt);System.out.println("Created "+s.id());}case "/list"->chat.list().forEach(s->System.out.println(s.id()+" | "+s.title()));case "/switch"->{chat.switchTo(p[1]);System.out.println("Switched.");}case "/delete"->{chat.delete(p[1]);System.out.println("Deleted.");}case "/snap"->{image=camera.snapshot().join();System.out.println("Captured "+image.length+" bytes.");}case "/record"->{int seconds=p.length>1?Integer.parseInt(p[1]):5;byte[] wav=audio.recordWav(seconds).join();Files.write(java.nio.file.Path.of("data/latest-recording.wav"),wav);System.out.println("Saved recording; configure STT_ENDPOINT for transcription.");}default->System.out.println("Unknown command.");}}catch(Exception e){System.out.println("Command failed: "+e.getMessage());}continue;}
                if(chat.active()==null){System.out.println("Create a session with /new title [custom prompt].");continue;} try{chat.saveUser(line,image==null?null:"camera://latest");String answer=api.generate(chat.active().systemPrompt(),chat.history(),line,image);chat.saveAssistant(answer);System.out.println("FRIDAY: "+answer);image=null;}catch(Exception e){System.out.println("Request failed: "+e.getMessage());}
            }
        }
    }
    private static void help(){System.out.println("/new <title> [prompt]  /list  /switch <id>  /delete <id>  /snap  /record [seconds]  /quit");}
}
