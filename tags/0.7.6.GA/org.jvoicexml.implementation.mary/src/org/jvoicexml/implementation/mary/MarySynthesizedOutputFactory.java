/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

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
    public final void setLang(final String value) {
        lang = value;
    }
}
