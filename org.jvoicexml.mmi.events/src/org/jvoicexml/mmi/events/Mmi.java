/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.w3.org/2008/04/mmi-arch}NewContextRequest"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.w3.org/2008/04/mmi-arch}NewContextResponse"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.w3.org/2008/04/mmi-arch}ClearContextRequest"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.w3.org/2008/04/mmi-arch}ClearContextResponse"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.w3.org/2008/04/mmi-arch}CancelRequest"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.w3.org/2008/04/mmi-arch}CancelResponse"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.w3.org/2008/04/mmi-arch}DoneNotification"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.w3.org/2008/04/mmi-arch}ExtensionNotification"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.w3.org/2008/04/mmi-arch}PauseRequest"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.w3.org/2008/04/mmi-arch}PauseResponse"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.w3.org/2008/04/mmi-arch}PrepareRequest"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.w3.org/2008/04/mmi-arch}PrepareResponse"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.w3.org/2008/04/mmi-arch}ResumeRequest"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.w3.org/2008/04/mmi-arch}ResumeResponse"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.w3.org/2008/04/mmi-arch}StartRequest"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.w3.org/2008/04/mmi-arch}StartResponse"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.w3.org/2008/04/mmi-arch}StatusRequest"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.w3.org/2008/04/mmi-arch}StatusResponse"/>
 *         &lt;/sequence>
 *       &lt;/choice>
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "newContextRequest", "newContextResponse",
        "clearContextRequest", "clearContextResponse", "cancelRequest",
        "cancelResponse", "doneNotification", "extensionNotification",
        "pauseRequest", "pauseResponse", "prepareRequest", "prepareResponse",
        "resumeRequest", "resumeResponse", "startRequest", "startResponse",
        "statusRequest", "statusResponse" })
@XmlRootElement(name = "mmi")
public final class Mmi implements Serializable {

    /** The serial version UID. */
    private static final long serialVersionUID = 5100515080041567275L;
    @XmlElement(name = "NewContextRequest")
    protected NewContextRequest newContextRequest;
    @XmlElement(name = "NewContextResponse")
    protected NewContextResponse newContextResponse;
    @XmlElement(name = "ClearContextRequest")
    protected ClearContextRequest clearContextRequest;
    @XmlElement(name = "ClearContextResponse")
    protected ClearContextResponse clearContextResponse;
    @XmlElement(name = "CancelRequest")
    protected CancelRequest cancelRequest;
    @XmlElement(name = "CancelResponse")
    protected CancelResponse cancelResponse;
    @XmlElement(name = "DoneNotification")
    protected DoneNotification doneNotification;
    @XmlElement(name = "ExtensionNotification")
    protected ExtensionNotification extensionNotification;
    @XmlElement(name = "PauseRequest")
    protected PauseRequest pauseRequest;
    @XmlElement(name = "PauseResponse")
    protected PauseResponse pauseResponse;
    @XmlElement(name = "PrepareRequest")
    protected PrepareRequest prepareRequest;
    @XmlElement(name = "PrepareResponse")
    protected PrepareResponse prepareResponse;
    @XmlElement(name = "ResumeRequest")
    protected ResumeRequest resumeRequest;
    @XmlElement(name = "ResumeResponse")
    protected ResumeResponse resumeResponse;
    @XmlElement(name = "StartRequest")
    protected StartRequest startRequest;
    @XmlElement(name = "StartResponse")
    protected StartResponse startResponse;
    @XmlElement(name = "StatusRequest")
    protected StatusRequest statusRequest;
    @XmlElement(name = "StatusResponse")
    protected StatusResponse statusResponse;
    @XmlAttribute(name = "version", required = true)
    protected BigDecimal version;

    /**
     * Gets the value of the newContextRequest property.
     * 
     * @return possible object is {@link NewContextRequest }
     * 
     */
    public NewContextRequest getNewContextRequest() {
        return newContextRequest;
    }

    /**
     * Sets the value of the newContextRequest property.
     * 
     * @param value
     *            allowed object is {@link NewContextRequest }
     * 
     */
    public void setNewContextRequest(NewContextRequest value) {
        this.newContextRequest = value;
    }

    /**
     * Gets the value of the newContextResponse property.
     * 
     * @return possible object is {@link NewContextResponse }
     * 
     */
    public NewContextResponse getNewContextResponse() {
        return newContextResponse;
    }

    /**
     * Sets the value of the newContextResponse property.
     * 
     * @param value
     *            allowed object is {@link NewContextResponse }
     * 
     */
    public void setNewContextResponse(NewContextResponse value) {
        this.newContextResponse = value;
    }

    /**
     * Gets the value of the clearContextRequest property.
     * 
     * @return possible object is {@link ClearContextRequest }
     * 
     */
    public ClearContextRequest getClearContextRequest() {
        return clearContextRequest;
    }

    /**
     * Sets the value of the clearContextRequest property.
     * 
     * @param value
     *            allowed object is {@link ClearContextRequest }
     * 
     */
    public void setClearContextRequest(ClearContextRequest value) {
        this.clearContextRequest = value;
    }

    /**
     * Gets the value of the clearContextResponse property.
     * 
     * @return possible object is {@link ClearContextResponse }
     * 
     */
    public ClearContextResponse getClearContextResponse() {
        return clearContextResponse;
    }

    /**
     * Sets the value of the clearContextResponse property.
     * 
     * @param value
     *            allowed object is {@link ClearContextResponse }
     * 
     */
    public void setClearContextResponse(ClearContextResponse value) {
        this.clearContextResponse = value;
    }

    /**
     * Gets the value of the cancelRequest property.
     * 
     * @return possible object is {@link CancelRequest }
     * 
     */
    public CancelRequest getCancelRequest() {
        return cancelRequest;
    }

    /**
     * Sets the value of the cancelRequest property.
     * 
     * @param value
     *            allowed object is {@link CancelRequest }
     * 
     */
    public void setCancelRequest(CancelRequest value) {
        this.cancelRequest = value;
    }

    /**
     * Gets the value of the cancelResponse property.
     * 
     * @return possible object is {@link CancelResponse }
     * 
     */
    public CancelResponse getCancelResponse() {
        return cancelResponse;
    }

    /**
     * Sets the value of the cancelResponse property.
     * 
     * @param value
     *            allowed object is {@link CancelResponse }
     * 
     */
    public void setCancelResponse(CancelResponse value) {
        this.cancelResponse = value;
    }

    /**
     * Gets the value of the doneNotification property.
     * 
     * @return possible object is {@link DoneNotification }
     * 
     */
    public DoneNotification getDoneNotification() {
        return doneNotification;
    }

    /**
     * Sets the value of the doneNotification property.
     * 
     * @param value
     *            allowed object is {@link DoneNotification }
     * 
     */
    public void setDoneNotification(DoneNotification value) {
        this.doneNotification = value;
    }

    /**
     * Gets the value of the extensionNotification property.
     * 
     * @return possible object is {@link ExtensionNotification }
     * 
     */
    public ExtensionNotification getExtensionNotification() {
        return extensionNotification;
    }

    /**
     * Sets the value of the extensionNotification property.
     * 
     * @param value
     *            allowed object is {@link ExtensionNotification }
     * 
     */
    public void setExtensionNotification(ExtensionNotification value) {
        this.extensionNotification = value;
    }

    /**
     * Gets the value of the pauseRequest property.
     * 
     * @return possible object is {@link PauseRequest }
     * 
     */
    public PauseRequest getPauseRequest() {
        return pauseRequest;
    }

    /**
     * Sets the value of the pauseRequest property.
     * 
     * @param value
     *            allowed object is {@link PauseRequest }
     * 
     */
    public void setPauseRequest(PauseRequest value) {
        this.pauseRequest = value;
    }

    /**
     * Gets the value of the pauseResponse property.
     * 
     * @return possible object is {@link PauseResponse }
     * 
     */
    public PauseResponse getPauseResponse() {
        return pauseResponse;
    }

    /**
     * Sets the value of the pauseResponse property.
     * 
     * @param value
     *            allowed object is {@link PauseResponse }
     * 
     */
    public void setPauseResponse(PauseResponse value) {
        this.pauseResponse = value;
    }

    /**
     * Gets the value of the prepareRequest property.
     * 
     * @return possible object is {@link PrepareRequest }
     * 
     */
    public PrepareRequest getPrepareRequest() {
        return prepareRequest;
    }

    /**
     * Sets the value of the prepareRequest property.
     * 
     * @param value
     *            allowed object is {@link PrepareRequest }
     * 
     */
    public void setPrepareRequest(PrepareRequest value) {
        this.prepareRequest = value;
    }

    /**
     * Gets the value of the prepareResponse property.
     * 
     * @return possible object is {@link PrepareResponse }
     * 
     */
    public PrepareResponse getPrepareResponse() {
        return prepareResponse;
    }

    /**
     * Sets the value of the prepareResponse property.
     * 
     * @param value
     *            allowed object is {@link PrepareResponse }
     * 
     */
    public void setPrepareResponse(PrepareResponse value) {
        this.prepareResponse = value;
    }

    /**
     * Gets the value of the resumeRequest property.
     * 
     * @return possible object is {@link ResumeRequest }
     * 
     */
    public ResumeRequest getResumeRequest() {
        return resumeRequest;
    }

    /**
     * Sets the value of the resumeRequest property.
     * 
     * @param value
     *            allowed object is {@link ResumeRequest }
     * 
     */
    public void setResumeRequest(ResumeRequest value) {
        this.resumeRequest = value;
    }

    /**
     * Gets the value of the resumeResponse property.
     * 
     * @return possible object is {@link ResumeResponse }
     * 
     */
    public ResumeResponse getResumeResponse() {
        return resumeResponse;
    }

    /**
     * Sets the value of the resumeResponse property.
     * 
     * @param value
     *            allowed object is {@link ResumeResponse }
     * 
     */
    public void setResumeResponse(ResumeResponse value) {
        this.resumeResponse = value;
    }

    /**
     * Gets the value of the startRequest property.
     * 
     * @return possible object is {@link StartRequest }
     * 
     */
    public StartRequest getStartRequest() {
        return startRequest;
    }

    /**
     * Sets the value of the startRequest property.
     * 
     * @param value
     *            allowed object is {@link StartRequest }
     * 
     */
    public void setStartRequest(StartRequest value) {
        this.startRequest = value;
    }

    /**
     * Gets the value of the startResponse property.
     * 
     * @return possible object is {@link StartResponse }
     * 
     */
    public StartResponse getStartResponse() {
        return startResponse;
    }

    /**
     * Sets the value of the startResponse property.
     * 
     * @param value
     *            allowed object is {@link StartResponse }
     * 
     */
    public void setStartResponse(StartResponse value) {
        this.startResponse = value;
    }

    /**
     * Gets the value of the statusRequest property.
     * 
     * @return possible object is {@link StatusRequest }
     * 
     */
    public StatusRequest getStatusRequest() {
        return statusRequest;
    }

    /**
     * Sets the value of the statusRequest property.
     * 
     * @param value
     *            allowed object is {@link StatusRequest }
     * 
     */
    public void setStatusRequest(StatusRequest value) {
        this.statusRequest = value;
    }

    /**
     * Gets the value of the statusResponse property.
     * 
     * @return possible object is {@link StatusResponse }
     * 
     */
    public StatusResponse getStatusResponse() {
        return statusResponse;
    }

    /**
     * Sets the value of the statusResponse property.
     * 
     * @param value
     *            allowed object is {@link StatusResponse }
     * 
     */
    public void setStatusResponse(StatusResponse value) {
        this.statusResponse = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return possible object is {@link BigDecimal }
     * 
     */
    public BigDecimal getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *            allowed object is {@link BigDecimal }
     * 
     */
    public void setVersion(BigDecimal value) {
        this.version = value;
    }

}
