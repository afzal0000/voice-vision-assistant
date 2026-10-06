package com.afzal0000.assistant.service;

import javax.sound.sampled.*;
import java.io.*;
import java.util.concurrent.*;

public final class AudioService implements AutoCloseable {
    private final ExecutorService executor=Executors.newCachedThreadPool();
    public CompletableFuture<byte[]> recordWav(int seconds){return CompletableFuture.supplyAsync(()->{AudioFormat format=new AudioFormat(16000,16,1,true,false);try(TargetDataLine line=AudioSystem.getTargetDataLine(format)){line.open(format);line.start();ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] buffer=new byte[4096];long end=System.nanoTime()+seconds*1_000_000_000L;while(System.nanoTime()<end){int n=line.read(buffer,0,buffer.length);if(n>0)out.write(buffer,0,n);}return wav(format,out.toByteArray());}catch(LineUnavailableException|IOException e){throw new CompletionException(e);}},executor);}
    private byte[] wav(AudioFormat f,byte[] data)throws IOException{ByteArrayOutputStream o=new ByteArrayOutputStream();try(DataOutputStream d=new DataOutputStream(o)){d.writeBytes("RIFF");d.writeInt(Integer.reverseBytes(36+data.length));d.writeBytes("WAVEfmt ");d.writeInt(Integer.reverseBytes(16));d.writeShort(Short.reverseBytes((short)1));d.writeShort(Short.reverseBytes((short)f.getChannels()));d.writeInt(Integer.reverseBytes((int)f.getSampleRate()));d.writeInt(Integer.reverseBytes((int)(f.getSampleRate()*f.getFrameSize())));d.writeShort(Short.reverseBytes((short)f.getFrameSize()));d.writeShort(Short.reverseBytes((short)f.getSampleSizeInBits()));d.writeBytes("data");d.writeInt(Integer.reverseBytes(data.length));d.write(data);}return o.toByteArray();}
    public void playWav(byte[] wav)throws Exception{try(AudioInputStream in=AudioSystem.getAudioInputStream(new ByteArrayInputStream(wav));Clip clip=AudioSystem.getClip()){clip.open(in);clip.start();while(clip.isRunning())Thread.sleep(50);}}
    @Override public void close(){executor.shutdownNow();}
}
