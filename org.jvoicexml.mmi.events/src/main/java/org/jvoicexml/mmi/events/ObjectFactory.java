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
     * Create a new ObjectFactory that can be used to create new instances of
     * schema derived classes for package: org.jvoicexml.mmi.events.xml.
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NewContextRequest}.
     * @return created request
     */
    public NewContextRequest createNewContextRequest() {
        return new NewContextRequest();
    }

    /**
     * Create an instance of {@link ClearContextResponse}.
     * @return created response
     */
    public ClearContextResponse createClearContextResponse() {
        return new ClearContextResponse();
    }

    /**
     * Create an instance of {@link CancelRequest}.
     * @return created request
     */
    public CancelRequest createCancelRequest() {
        return new CancelRequest();
    }

    /**
     * Create an instance of {@link PauseRequest}.
     * @return created request
     */
    public PauseRequest createPauseRequest() {
        return new PauseRequest();
    }

    /**
     * Create an instance of {@link NewContextResponse}.
     * @return created response
     */
    public NewContextResponse createNewContextResponse() {
        return new NewContextResponse();
    }

    /**
     * Create an instance of {@link PrepareResponse}.
     * @return created response
     */
    public PrepareResponse createPrepareResponse() {
        return new PrepareResponse();
    }

    /**
     * Create an instance of {@link ExtensionNotification}.
     * @return created notification
     */
    public ExtensionNotification createExtensionNotification() {
        return new ExtensionNotification();
    }

    /**
     * Create an instance of {@link PrepareRequest}.
     * @return created request
     */
    public PrepareRequest createPrepareRequest() {
        return new PrepareRequest();
    }

    /**
     * Create an instance of {@link ContentURLType}.
     * @return created content URL type
     */
    public ContentURLType createContentURLType() {
        return new ContentURLType();
    }

    /**
     * Create an instance of {@link Mmi}.
     * @return created MMI.
     */
    public Mmi createMmi() {
        return new Mmi();
    }

    /**
     * Create an instance of {@link ClearContextRequest}.
     * @return created request
     */
    public ClearContextRequest createClearContextRequest() {
        return new ClearContextRequest();
    }

    /**
     * Create an instance of {@link CancelResponse}.
     * @return created response
     */
    public CancelResponse createCancelResponse() {
        return new CancelResponse();
    }

    /**
     * Create an instance of {@link DoneNotification}.
     * @return created notification
     */
    public DoneNotification createDoneNotification() {
        return new DoneNotification();
    }

    /**
     * Create an instance of {@link PauseResponse}.
     * @return created response
     */
    public PauseResponse createPauseResponse() {
        return new PauseResponse();
    }

    /**
     * Create an instance of {@link ResumeRequest}.
     * @return created request
     */
    public ResumeRequest createResumeRequest() {
        return new ResumeRequest();
    }

    /**
     * Create an instance of {@link ResumeResponse}.
     * @return created response
     */
    public ResumeResponse createResumeResponse() {
        return new ResumeResponse();
    }

    /**
     * Create an instance of {@link StartRequest}.
     * @return created request
     */
    public StartRequest createStartRequest() {
        return new StartRequest();
    }

    /**
     * Create an instance of {@link StartResponse}.
     * @return created response
     */
    public StartResponse createStartResponse() {
        return new StartResponse();
    }

    /**
     * Create an instance of {@link StatusRequest}.
     * @return created request
     */
    public StatusRequest createStatusRequest() {
        return new StatusRequest();
    }

    /**
     * Create an instance of {@link StatusResponse}.
     * @return created response
     */
    public StatusResponse createStatusResponse() {
        return new StatusResponse();
    }

}
