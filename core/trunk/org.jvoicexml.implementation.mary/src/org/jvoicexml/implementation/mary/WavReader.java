package org.jvoicexml.implementation.mary;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.apache.log4j.Logger;

/**
 * The wav reader enables easy reading of wav files. If a file is already being read when a play
 * or repeat method is called, the reading will stop to proceed to the new file.
 * @author Pascal Perez (pascal.perez@epfl.ch)
 * @version 1.0
 */
public class WavReader {
    /**
     * The buffer size in bytes.
     */
    private static final int WAVREADER_BUFFERSIZE = 8;
    public static Logger logger = Logger.getLogger(WavReader.class);

    // wav reader's variable
    private File wavFile = null;
    private Reader reader = null;
    private WavReaderCallback callback = null;

    private InputStream in = null;
    // neu
    boolean test = false;

    /**
     * Constructs a wav reader.
     */
    public WavReader() { }

    /**
     * Constructs a wav reader which uses the callback to inform of it's on going processing.
     * @param callback the callback to use
     */
    public WavReader(WavReaderCallback callback) {
        this.callback = callback;
    }

    /**
     * Play the specified wav file. This method is non-blocking.
     * @param wavFile the wav file to read
     */
    public void play(String wavFile) {
        this.wavFile = new File(wavFile);
        this.in = null;
        play();
    }
    
    public void play(File file){
        this.wavFile = file;
        this.in = null;
        play();
        
    }

    public void play(InputStream in){
        this.in = in;
        this.wavFile = null;
    }

    /**
     * Play the wav file. This method is non-blocking.
     */
    private void play() {
        try {
            logger.debug("Playing file:"+ this.wavFile.getCanonicalPath());
        } catch (IOException e) {
            logger.error("File not found");
            e.printStackTrace();
        }
        stop();

        if (wavFile != null && in == null)
            reader = new Reader(wavFile);
        else
            reader = new Reader(in);

        reader.start();
    }

    /**
     * Repeat the last wav file played. This method is non-blocking.
     */
    public void repeat() {
        if (wavFile != null) {
            play();
        }
    }

    /**
     * Stop reading.
     */
    public void stop() {
        if (reader != null)
            reader.end();
    }
        
        private class Reader extends Thread {
            private boolean run = true;
            private boolean isRunning = false;
            private File wavFile = null;
            private InputStream in = null;

            /**
             * Create a new reader.
             * @param wavFile the wav file to read
             */
            public Reader(String wavFile) {
                this.wavFile = new File(wavFile);
            }
            
            public Reader(File file){
                    this.wavFile = file;
            }

            public Reader(InputStream in){
                    this.in = in;
            }

            /**
             * Read the wav file.
             */
            @Override
            public void run() {
                isRunning = true;
                try {
                    
                    AudioInputStream audioInputStream = null;
                    
                    if (this.wavFile != null)
                        audioInputStream = AudioSystem.getAudioInputStream(wavFile);
                    else 
                        audioInputStream = AudioSystem.getAudioInputStream(in);

                    AudioFormat audioFormat = audioInputStream.getFormat();

                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                    SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                    line.open(audioFormat);
                    line.start();
                    int nBytesRead = 0;
                    byte[] abData = new byte[WAVREADER_BUFFERSIZE];
                    while (nBytesRead != -1 && run) {
                        nBytesRead = audioInputStream.read(abData, 0, abData.length);
                        if (nBytesRead >= 0) {
                            line.write(abData, 0, nBytesRead);
                        }
                    }
                    line.drain();
                    line.close();

                    audioInputStream.close();


                } catch (Exception e) { 
                    System.out.println("WavReader.run> exception");
                }
                if (callback != null)
                    callback.playingFinished();
                isRunning = false;
                reader = null;

            }

            /**
             * Stop reading the wav file.
             */
            public void end() {
                run = false;
                while (isRunning); // waiting for the thread to finish
            }
        }

    }


