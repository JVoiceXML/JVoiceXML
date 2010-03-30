package org.jvoicexml.implementation.mary;

import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.AudioFileOutput;
import org.jvoicexml.implementation.ResourceFactory;

/**
 * Demo implementation of a
 * {@link org.jvoicexml.implementation.ResourceFactory} for the
 * {@link AudioFileOutput} based on JSAPI 1.0.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 1427 $
 * @since 0.5.5
 */
public final class MaryAudioFileOutputFactory
    implements ResourceFactory<AudioFileOutput> {
    /** Number of instances that this factory will create. */
    private int instances;

    /**
     * Constructs a new object.
     */
    public MaryAudioFileOutputFactory() {
    }

    /**
     * {@inheritDoc}
     */
    public AudioFileOutput createResource()
        throws NoresourceError {

        return new MaryAudioFileOutput();
    }

    /**
     * Sets the number of instances that this factory will create.
     * @param number Number of instances to create.
     */
    public void setInstances(final int number) {
        instances = number;
    }

    /**
     * {@inheritDoc}
     */
    public int getInstances() {
        return instances;
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "maryTTS";
    }

    /**
     * {@inheritDoc}
     */
    public Class<AudioFileOutput> getResourceType() {
        return AudioFileOutput.class;
    }
}
