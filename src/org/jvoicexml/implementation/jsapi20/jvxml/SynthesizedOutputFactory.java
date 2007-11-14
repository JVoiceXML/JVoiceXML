/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/jsapi10/jvxml/SynthesizedOutputFactory.java $
 * Version: $LastChangedRevision: 412 $
 * Date:    $LastChangedDate: 2007-08-31 11:10:38 +0100 (Sex, 31 Ago 2007) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.implementation.jsapi20.jvxml;

import java.beans.PropertyVetoException;

import javax.speech.EngineManager;
import javax.speech.synthesis.SynthesizerMode;

import org.jvoicexml.SynthesizedOuput;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.jsapi20.Jsapi20SynthesizedOutput;
import org.apache.log4j.Logger;
import javax.speech.EngineList;


/**
 * Demo implementation of a
 * {@link org.jvoicexml.implementation.ResourceFactory} for the
 * {@link SynthesizedOuput} based on JSAPI 1.0.
 *
 * @author Dirk Schnelle
 * @version $Revision: 412 $
 *
 * <p>
 * Copyright &copy; 2006-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.5.5
 */
public final class SynthesizedOutputFactory
    implements ResourceFactory<SynthesizedOuput> {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(SynthesizedOutputFactory.class);

    /** Number of instances that this factory will create. */
    private int instances;

    /** Name of the default voice. */
    private String voice;

    /** Type of the created resources. */
    private String type;

    private String mediaLocator;

    /**
     * Constructs a new object.
     */
    public SynthesizedOutputFactory() {
        type = "jsapi20";
    }

    static {
        try {
            SynthesizedOutputFactory.class.forName(
                    "inesc.id.l2f.protocols.ExtensionsManager");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }




    /**
     * {@inheritDoc}
     */
    public SynthesizedOuput createResource() throws NoresourceError {

        final SynthesizerMode desc = getEngineProperties();
        if (desc == null) throw new NoresourceError("Cannot find any suitable SynthesizerMode");

        final Jsapi20SynthesizedOutput output =
            new Jsapi20SynthesizedOutput(desc, mediaLocator);

        output.setType(type);

        try {
            output.setVoice(voice);
        } catch (PropertyVetoException e) {
            throw new NoresourceError("error setting voice to '" + voice + "'!",
                    e);
        }

        return output;
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
     * Sets the default voice for the synthesizers.
     * @param voiceName Name of the default voice.
     */
    public void setDefaultVoice(final String voiceName) {
        voice = voiceName;
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return type;
    }

    public String getMediaLocator() {
        return mediaLocator;
    }

    /**
     * Sets the type of the resource.
     * @param resourceType type of the resource.
     */
    public void setType(final String resourceType) {
        type = resourceType;
    }

    public void setMediaLocator(String mediaLocator) {
        this.mediaLocator = mediaLocator;
    }

    /**
     * Retrieves the required engine properties.
     *
     * @return Required engine properties or <code>null</code> for default
     * engine selection
     */
    public SynthesizerMode getEngineProperties() {
        try {
            EngineList l = EngineManager.availableEngines(new SynthesizerMode(null, null, null, null, null, null));
            if (l.size() > 0) {
                return (SynthesizerMode)l.elementAt(0);
            }
            else
                return null;
/*            return (SynthesizerMode) EngineManager.createEngine(SynthesizerMode.
                    DEFAULT);*/
        } catch (SecurityException ex) {
            ex.printStackTrace();
            return null;
       // } catch (EngineException ex) {
          //  return null;
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
