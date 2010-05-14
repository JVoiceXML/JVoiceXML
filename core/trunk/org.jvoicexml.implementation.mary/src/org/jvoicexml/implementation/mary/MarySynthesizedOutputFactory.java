package org.jvoicexml.implementation.mary;

import org.apache.log4j.Logger;
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

    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(MarySynthesizedOutput.class);

    /** Number of instances that this factory will create. */
    private  int instances;

    /** Type of the created resources. */
    private String type;
    
    /**Type of the output audio.*/
    private  String audioType;
   
    /**Name of the voice to use.*/
    private String voiceName;
   
    /**The used lang*/
    private String lang;
   
    /**Flag that indicates if text output is required*/
    private boolean textOutputEnabled;
    
    /**The port that will be used for text output*/
    private int textOutputPort;
    
    /**The port that will be used for text input*/
    private int textInputPort;
    
    
    @Override
    public final SynthesizedOutput createResource() throws NoresourceError {

        final MarySynthesizedOutput output = new MarySynthesizedOutput();
        output.setType(type);
        output.setAudioType(audioType);
        output.setVoiceName(voiceName);
        output.enableTextOutput(textOutputEnabled);
        output.setTextOutputPort(textOutputPort);
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
     * @param type
     *            type of the audio.
     */
    public final void setAudioType(final String type) {
        audioType = type;
      }
    
    
    public final String getAudioType() {
        return audioType;
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
    
    
    public final String getVoiceName() {
        return voiceName;
      }
    
    /**
     * Sets the textOutput.
     *
     * @param enableTextOutput
     *           
     */
    public final void setTextOutputEnabled(final boolean enableTextOutput){
        
        textOutputEnabled = enableTextOutput;
          
    }
    
    public final boolean getTextOutputEnabled(){
        
        return textOutputEnabled;
        
    }
    
    public final void setTextOutputPort(final int port){
        
        textOutputPort = port;
    }
    
    public final int getTextOutputPort(){
        
        return textOutputPort;
    }
     
    public final void setLang(final String lang){
        
        this.lang=lang;
    }
    
    public final String getLang(){
        
        return lang;
    } 
    
    
    
    
}
