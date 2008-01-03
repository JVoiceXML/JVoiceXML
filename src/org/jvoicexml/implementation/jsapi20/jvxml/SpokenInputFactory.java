/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/jsapi10/jvxml/SpokenInputFactory.java $
 * Version: $LastChangedRevision: 392 $
 * Date:    $LastChangedDate: 2007-08-13 08:23:39 +0100 (Seg, 13 Ago 2007) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.speech.EngineManager;
import javax.speech.recognition.RecognizerMode;

import org.jvoicexml.SpokenInput;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.jsapi20.Jsapi20SpokenInput;
import org.apache.log4j.Logger;
import javax.speech.EngineList;

/**
 * Demo implementation of a
 * {@link org.jvoicexml.implementation.ResourceFactory} for the
 * {@link SpokenInput} based on JSAPI 2.0.
 *
 * @author Dirk Schnelle
 * @version $Revision: 392 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.5.5
 */
public final class SpokenInputFactory implements ResourceFactory<SpokenInput> {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(SpokenInputFactory.class);

    /** Number of instances that this factory will create. */
    private int instances;

    private String mediaLocator;

    /**
     * Constructs a new object.
     */
    public SpokenInputFactory() {
    }

    /**
     * {@inheritDoc}
     */
    public SpokenInput createResource() throws NoresourceError {

        final RecognizerMode desc = getEngineProperties();
        if (desc == null) throw new NoresourceError("Cannot find any suitable RecognizerMode");

        final Jsapi20SpokenInput input = new Jsapi20SpokenInput(desc, mediaLocator);

        return input;
    }

    /**
     * Sets the number of instances that this factory will create.
     * @param number Number of instances to create.
     */
    public void setInstances(final int number) {
        instances = number;
    }

    public void setMediaLocator(String mediaLocator) {
        this.mediaLocator = mediaLocator;
    }

    /**
     * {@inheritDoc}
     */
    public int getInstances() {
        return instances;
    }

    public String getMediaLocator() {
        return mediaLocator;
    }

    /**
     * Get the required engine properties.
     *
     * @return Required engine properties or <code>null</code> for default
     *   engine selection
     *
     * @todo This is more or less a bogus implementation and has to be replaced,
     * if  sphinx4 is more JSAPI compliant.
     */
    public RecognizerMode getEngineProperties() {
        try {
            EngineList l = EngineManager.availableEngines(new RecognizerMode(null));
            if (l.size() > 0) {
                return (RecognizerMode)l.elementAt(0);
            }
            else
                return null;


          //  return (RecognizerMode) EngineManager.createEngine(RecognizerMode.DEFAULT);
        } catch (SecurityException ex) {
            ex.printStackTrace();
            return null;
//        } catch (EngineException ex) {
//            return null;
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "jsapi20";
    }
}
