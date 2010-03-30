package org.jvoicexml.implementation.mary;

public interface WavReaderCallback {
    /**
     * This command is lauchend by the WavReader when it has finished playing a
     * sound.
     */
    public void playingFinished();
}
