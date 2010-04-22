/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.implementation.text/src/org/jvoicexml/implementation/text/TextSpokenInputFactory.java $
 * Version: $LastChangedRevision: 1403 $
 * Date:    $LastChangedDate: 2009-01-30 12:41:19 +0200 (Παρ, 30 Ιαν 2009) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.mary;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SpokenInput;

/**
 * Demo implementation of a
 * {@link org.jvoicexml.implementation.ResourceFactory} for the
 * {@link SpokenInput} based on a simple text interface.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 1403 $
 * @since 0.6
 */
public final class TextSpokenInputFactory
    implements ResourceFactory<SpokenInput> {
    /** Number of instances that this factory will create. */
    private int instances;

    
    private int textInputPort;
    
    /**
     * Constructs a new object.
     */
    public TextSpokenInputFactory() {
    }

    /**
     * {@inheritDoc}
     */
    public SpokenInput createResource()
        throws NoresourceError {
        final TextSpokenInput input = new TextSpokenInput(); 
        input.setTextInputPort(textInputPort);
        return input;
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
        return "text";
    }

    /**
     * {@inheritDoc}
     */
    public Class<SpokenInput> getResourceType() {
        return SpokenInput.class;
    }
    
    
    public void setTextInputPort(final int port){
        
        textInputPort=port;
        
    }
    
    public int getTextInputPort(){
        
        return textInputPort;
        
    }
    
    
    
}
