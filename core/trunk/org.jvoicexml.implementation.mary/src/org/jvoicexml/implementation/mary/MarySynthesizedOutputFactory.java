package org.jvoicexml.implementation.mary;

import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SynthesizedOutput;

/**implementation of a.
* {@link org.jvoicexml.implementation.ResourceFactory} for the
* {@link SynthesizedOutput} based on MaryTTS
* @author Dirk Schnelle-Walka
* @author Giannis Assiouras
**/

public class MarySynthesizedOutputFactory implements
    ResourceFactory<SynthesizedOutput> {
    /** Number of instances that this factory will create. */
    private int instances;

    /** Type of the created resources. */
    private String type;
    
    /**Type of the output audio.*/
    private String audioType;
   
    /**Name of the voice to use.*/
    private String voiceName;

    /**The used language. */
    private String lang;
   
    /**
     * {@inheritDoc}
     */
    @Override
    public final SynthesizedOutput createResource() throws NoresourceError {

        final MarySynthesizedOutput output = new MarySynthesizedOutput();
        output.setType(type);
        output.setAudioType(audioType);
        output.setVoiceName(voiceName);
        output.setLang(lang);
        return output;
    }

    @Override
    public final int getInstances() {
        // TODO Auto-generated method stub
        return instances;
    }

    @Override
    public final Class<SynthesizedOutput> getResourceType() {
        return  SynthesizedOutput.class;
    }

    @Override
    public final String getType() {
        return  type;
    }

    /**
     * Sets the number of instances that this factory will create.
     *
     * @param number
     *            Number of instances to create.
     */
    public final void setInstances(final int number) {
        instances = number;
    }

    /**
     * Sets the type of the resource.
     *
     * @param resourceType
     *            type of the resource.
     */
    public final void setType(final String resourceType) {
        type = resourceType;
      }

    /**
     * Sets the type of the audio.
     *
     * @param value
     *            type of the audio.
     */
    public final void setAudioType(final String value) {
        audioType = value;
      }
    

    /**
     * Sets the voice that will be used from Mary server.
     *
     * @param name
     *            voice name.
     */
    public final void setVoiceName(final String name) {
        voiceName = name;
      }
    
    /**
     * Sets the language.
     * @param value the new language
     */
    public final void setLang(final String value){
        lang=value;
    }
}
