/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/formitem/FieldShadowVarContainer.java $
 * Version: $LastChangedRevision: 4080 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager.mmi.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.jvoicexml.RecognitionResult;
import org.jvoicexml.callmanager.mmi.ConversionException;
import org.jvoicexml.callmanager.mmi.ExtensionNotificationDataExtractor;
import org.jvoicexml.mmi.events.ExtensionNotification;
import org.jvoicexml.mmi.events.Mmi;

/**
 * Extracts XML formatted data from received {@link ExtensionNotification}s.
 * Standards like <a href="http://www.w3.org/TR/emma/">EMMA</a> are used where
 * possible.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
public class XmlExtensionNotificationDataExtractor
        implements ExtensionNotificationDataExtractor {
    /**
     * {@inheritDoc}
     */
    @Override
    public RecognitionResult getRecognitionResult(final Mmi mmi,
            final ExtensionNotification ext) throws ConversionException {
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(Mmi.class);
            Marshaller marshaller = context.createMarshaller();
            final EmmaSemanticInterpretationExtractor extractor = new EmmaSemanticInterpretationExtractor();
            marshaller.marshal(mmi, extractor);
            return extractor.getRecognitonResult();
        } catch (JAXBException e) {
            throw new ConversionException(e.getMessage(), e);
        }
    }
}
