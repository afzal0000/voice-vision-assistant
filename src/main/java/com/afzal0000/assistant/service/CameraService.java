package com.afzal0000.assistant.service;

import com.github.sarxos.webcam.Webcam;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.*;
import javax.imageio.ImageIO;

public final class CameraService implements AutoCloseable {
    private Webcam webcam; private final ExecutorService executor=Executors.newSingleThreadExecutor();
    public void open(){webcam=Webcam.getDefault();if(webcam==null)throw new IllegalStateException("No webcam detected");webcam.open();}
    public CompletableFuture<byte[]> snapshot(){return CompletableFuture.supplyAsync(()->{try{if(webcam==null||!webcam.isOpen())open();BufferedImage image=webcam.getImage();ByteArrayOutputStream out=new ByteArrayOutputStream();ImageIO.write(image,"jpg",out);return out.toByteArray();}catch(Exception e){throw new CompletionException(e);}},executor);}
    @Override public void close(){if(webcam!=null&&webcam.isOpen())webcam.close();executor.shutdownNow();}
}
