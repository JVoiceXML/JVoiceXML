/**
 * 
 */
package org.jvoicexml.demo.jtapidemo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Dictionary;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine.Info;
import javax.telephony.ProviderUnavailableException;
import javax.telephony.media.MediaProvider;
import javax.telephony.media.MediaResourceException;
import javax.telephony.media.PlayerEvent;
import javax.telephony.media.RTC;
import javax.telephony.media.RecorderEvent;

import net.sourceforge.gjtapi.media.GenericMediaService;

/**
 * @author DS01191
 * 
 */
public final class DesktopMediaService extends GenericMediaService {

    /**
     * @param provider
     */
    public DesktopMediaService(final MediaProvider provider) {
        super(provider);
    }

    /**
     * @param peerName
     * @param provider
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ProviderUnavailableException
     */
    public DesktopMediaService(final String peerName, String provider)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, ProviderUnavailableException {
        super(peerName, provider);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecorderEvent record(String streamId, RTC[] rtc, Dictionary optargs)
            throws MediaResourceException {
        if (streamId == null) {
            throw new MediaResourceException("Unable to record a null stream!");
        }
        try {
            final URL url = new URL(streamId);
            final URLConnection connection = url.openConnection();
            final OutputStream output = connection.getOutputStream();
        } catch (MalformedURLException e) {
            throw new MediaResourceException(e.getMessage());
        } catch (IOException e) {
            throw new MediaResourceException(e.getMessage());
        }
        return super.record(streamId, rtc, optargs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlayerEvent play(String streamId, int offset, RTC[] rtcs,
            Dictionary optArgs) throws MediaResourceException {
        if (streamId == null) {
            throw new MediaResourceException("Unable to record a null stream!");
        }
        try {
            final URL url = new URL(streamId);
            final URLConnection connection = url.openConnection();
            final InputStream input = connection.getInputStream();
            play(input);
        } catch (MalformedURLException e) {
            throw new MediaResourceException(e.getMessage());
        } catch (IOException e) {
            throw new MediaResourceException(e.getMessage());
        }
        return super.play(streamId, offset, rtcs, optArgs);
    }

    /**
     * Plays the received audio to the speaker.
     * @param input current input stream.
     * @throws IOException
     *         error accessing the streams.
     */
    private void play(final InputStream input) throws IOException {
        AudioFormat receiveFormat = new AudioFormat(AudioFormat.Encoding.ULAW,
                8000, 8, 1, 1, 8000, false);

        AudioFormat playFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED, 8000, 16, 1, 2, 8000, false);

        final Info playbackLineInfo = new Info(SourceDataLine.class, playFormat,
                AudioSystem.NOT_SPECIFIED);
        final SourceDataLine line;
        try {
            line = (SourceDataLine) AudioSystem
                    .getLine(playbackLineInfo);
            line.open();
        } catch (LineUnavailableException e) {
            throw new IOException(e.getMessage());
        }
        line.start();

        final byte[] buffer = new byte[1024];
        int num;
        final AudioInputStream receiveStream = new AudioInputStream(input,
                receiveFormat, AudioSystem.NOT_SPECIFIED);
        final AudioInputStream playStream = AudioSystem.getAudioInputStream(
                playFormat, receiveStream);

        while ((num = playStream.read(buffer)) != -1) {
            line.write(buffer, 0, num);
        }
        playStream.close();
        receiveStream.close();
    }
}
