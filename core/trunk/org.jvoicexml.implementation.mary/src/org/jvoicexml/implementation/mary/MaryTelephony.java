package org.jvoicexml.implementation.mary;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.sound.sampled.AudioFormat;

import org.jvoicexml.RemoteClient;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.Telephony;
import org.jvoicexml.implementation.TelephonyListener;

public class MaryTelephony implements Telephony {

    @Override
    public AudioFormat getRecordingAudioFormat() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void play(SynthesizedOutput output, Map<String, String> parameters)
            throws NoresourceError, IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void record(SpokenInput input, Map<String, String> parameters)
            throws NoresourceError, IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void startRecording(SpokenInput input, OutputStream stream,
            Map<String, String> parameters) throws NoresourceError, IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void stopPlay() throws NoresourceError {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void stopRecording() throws NoresourceError {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void transfer(String dest) throws NoresourceError {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addListener(TelephonyListener listener) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeListener(TelephonyListener listener) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void activate() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isBusy() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void open() throws NoresourceError {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void passivate() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void connect(RemoteClient client) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void disconnect(RemoteClient client) {
        // TODO Auto-generated method stub
        
    }

}
