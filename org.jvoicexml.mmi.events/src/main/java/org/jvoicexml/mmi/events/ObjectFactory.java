/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.mmi.events;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.jvoicexml.mmi.events.xml package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
@XmlRegistry
public final class ObjectFactory {
    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.jvoicexml.mmi.events.xml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NewContextRequest }
     * 
     */
    public NewContextRequest createNewContextRequest() {
        return new NewContextRequest();
    }

    /**
     * Create an instance of {@link ClearContextResponse }
     * 
     */
    public ClearContextResponse createClearContextResponse() {
        return new ClearContextResponse();
    }

    /**
     * Create an instance of {@link CancelRequest }
     * 
     */
    public CancelRequest createCancelRequest() {
        return new CancelRequest();
    }

    /**
     * Create an instance of {@link PauseRequest }
     * 
     */
    public PauseRequest createPauseRequest() {
        return new PauseRequest();
    }

    /**
     * Create an instance of {@link NewContextResponse }
     * 
     */
    public NewContextResponse createNewContextResponse() {
        return new NewContextResponse();
    }

    /**
     * Create an instance of {@link PrepareResponse }
     * 
     */
    public PrepareResponse createPrepareResponse() {
        return new PrepareResponse();
    }

    /**
     * Create an instance of {@link ExtensionNotification }
     * 
     */
    public ExtensionNotification createExtensionNotification() {
        return new ExtensionNotification();
    }

    /**
     * Create an instance of {@link PrepareRequest }
     * 
     */
    public PrepareRequest createPrepareRequest() {
        return new PrepareRequest();
    }

    /**
     * Create an instance of {@link ContentURLType }
     * 
     */
    public ContentURLType createContentURLType() {
        return new ContentURLType();
    }

    /**
     * Create an instance of {@link Mmi }
     * 
     */
    public Mmi createMmi() {
        return new Mmi();
    }

    /**
     * Create an instance of {@link ClearContextRequest }
     * 
     */
    public ClearContextRequest createClearContextRequest() {
        return new ClearContextRequest();
    }

    /**
     * Create an instance of {@link CancelResponse }
     * 
     */
    public CancelResponse createCancelResponse() {
        return new CancelResponse();
    }

    /**
     * Create an instance of {@link DoneNotification }
     * 
     */
    public DoneNotification createDoneNotification() {
        return new DoneNotification();
    }

    /**
     * Create an instance of {@link PauseResponse }
     * 
     */
    public PauseResponse createPauseResponse() {
        return new PauseResponse();
    }

    /**
     * Create an instance of {@link ResumeRequest }
     * 
     */
    public ResumeRequest createResumeRequest() {
        return new ResumeRequest();
    }

    /**
     * Create an instance of {@link ResumeResponse }
     * 
     */
    public ResumeResponse createResumeResponse() {
        return new ResumeResponse();
    }

    /**
     * Create an instance of {@link StartRequest }
     * 
     */
    public StartRequest createStartRequest() {
        return new StartRequest();
    }

    /**
     * Create an instance of {@link StartResponse }
     * 
     */
    public StartResponse createStartResponse() {
        return new StartResponse();
    }

    /**
     * Create an instance of {@link StatusRequest }
     * 
     */
    public StatusRequest createStatusRequest() {
        return new StatusRequest();
    }

    /**
     * Create an instance of {@link StatusResponse }
     * 
     */
    public StatusResponse createStatusResponse() {
        return new StatusResponse();
    }

}
