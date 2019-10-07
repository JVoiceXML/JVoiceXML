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

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Basic container of the MMI events.
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
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "newContextRequest", "newContextResponse",
        "clearContextRequest", "clearContextResponse", "cancelRequest",
        "cancelResponse", "doneNotification", "extensionNotification",
        "pauseRequest", "pauseResponse", "prepareRequest", "prepareResponse",
        "resumeRequest", "resumeResponse", "startRequest", "startResponse",
        "statusRequest", "statusResponse" })
@XmlRootElement(name = "mmi", namespace = "http://www.w3.org/2008/04/mmi-arch")
public final class Mmi implements Serializable {

    /** The serial version UID. */
    private static final long serialVersionUID = 5100515080041567275L;
    @XmlElement(name = "NewContextRequest", namespace = "http://www.w3.org/2008/04/mmi-arch")
    protected NewContextRequest newContextRequest;
    @XmlElement(name = "NewContextResponse", namespace = "http://www.w3.org/2008/04/mmi-arch")
    protected NewContextResponse newContextResponse;
    @XmlElement(name = "ClearContextRequest", namespace = "http://www.w3.org/2008/04/mmi-arch")
    protected ClearContextRequest clearContextRequest;
    @XmlElement(name = "ClearContextResponse", namespace = "http://www.w3.org/2008/04/mmi-arch")
    protected ClearContextResponse clearContextResponse;
    @XmlElement(name = "CancelRequest", namespace = "http://www.w3.org/2008/04/mmi-arch")
    protected CancelRequest cancelRequest;
    @XmlElement(name = "CancelResponse", namespace = "http://www.w3.org/2008/04/mmi-arch")
    protected CancelResponse cancelResponse;
    @XmlElement(name = "DoneNotification", namespace = "http://www.w3.org/2008/04/mmi-arch")
    protected DoneNotification doneNotification;
    @XmlElement(name = "ExtensionNotification", namespace = "http://www.w3.org/2008/04/mmi-arch")
    protected ExtensionNotification extensionNotification;
    @XmlElement(name = "PauseRequest", namespace = "http://www.w3.org/2008/04/mmi-arch")
    protected PauseRequest pauseRequest;
    @XmlElement(name = "PauseResponse", namespace = "http://www.w3.org/2008/04/mmi-arch")
    protected PauseResponse pauseResponse;
    @XmlElement(name = "PrepareRequest", namespace = "http://www.w3.org/2008/04/mmi-arch")
    protected PrepareRequest prepareRequest;
    @XmlElement(name = "PrepareResponse", namespace = "http://www.w3.org/2008/04/mmi-arch")
    protected PrepareResponse prepareResponse;
    @XmlElement(name = "ResumeRequest", namespace = "http://www.w3.org/2008/04/mmi-arch")
    protected ResumeRequest resumeRequest;
    @XmlElement(name = "ResumeResponse", namespace = "http://www.w3.org/2008/04/mmi-arch")
    protected ResumeResponse resumeResponse;
    @XmlElement(name = "StartRequest", namespace = "http://www.w3.org/2008/04/mmi-arch")
    protected StartRequest startRequest;
    @XmlElement(name = "StartResponse", namespace = "http://www.w3.org/2008/04/mmi-arch")
    protected StartResponse startResponse;
    @XmlElement(name = "StatusRequest", namespace = "http://www.w3.org/2008/04/mmi-arch")
    protected StatusRequest statusRequest;
    @XmlElement(name = "StatusResponse", namespace = "http://www.w3.org/2008/04/mmi-arch")
    protected StatusResponse statusResponse;
    @XmlAttribute(name = "version", namespace = "http://www.w3.org/2008/04/mmi-arch", required = true)
    protected BigDecimal version;

    /**
     * Retrieves the nested {@link LifeCycleEvent}.
     * 
     * @return the nested {@link LifeCycleEvent}
     */
    public LifeCycleEvent getLifeCycleEvent() {
        if (newContextRequest != null) {
            return newContextRequest;
        } else if (newContextResponse != null) {
            return newContextResponse;
        } else if (clearContextRequest != null) {
            return clearContextRequest;
        } else if (clearContextResponse != null) {
            return clearContextResponse;
        } else if (cancelRequest != null) {
            return cancelRequest;
        } else if (cancelResponse != null) {
            return cancelResponse;
        } else if (doneNotification != null) {
            return doneNotification;
        } else if (extensionNotification != null) {
            return extensionNotification;
        } else if (pauseRequest != null) {
            return pauseRequest;
        } else if (pauseResponse != null) {
            return pauseResponse;
        } else if (prepareRequest != null) {
            return prepareRequest;
        } else if (prepareResponse != null) {
            return prepareResponse;
        } else if (resumeRequest != null) {
            return resumeRequest;
        } else if (resumeResponse != null) {
            return prepareResponse;
        } else if (startRequest != null) {
            return startRequest;
        } else if (startResponse != null) {
            return startResponse;
        } else if (statusRequest != null) {
            return statusRequest;
        } else if (statusResponse != null) {
            return statusResponse;
        } else {
            return null;
        }
    }

    /**
     * Sets the nested {@link LifeCycleEvent}.
     * 
     * @param event
     *            the event to set
     */
    public void setLifeCycleEvent(final LifeCycleEvent event) {
        if (event == null) {
            return;
        }
        if (event instanceof NewContextRequest) {
            setNewContextRequest((NewContextRequest) event);
        } else if (event instanceof NewContextResponse) {
            setNewContextResponse((NewContextResponse) event);
        } else if (event instanceof ClearContextRequest) {
            setClearContextRequest((ClearContextRequest) event);
        } else if (event instanceof ClearContextResponse) {
            setClearContextResponse((ClearContextResponse) event);
        } else if (event instanceof CancelRequest) {
            setCancelRequest((CancelRequest) event);
        } else if (event instanceof CancelResponse) {
            setCancelResponse((CancelResponse) event);
        } else if (event instanceof DoneNotification) {
            setDoneNotification((DoneNotification) event);
        } else if (event instanceof ExtensionNotification) {
            setExtensionNotification((ExtensionNotification) event);
        } else if (event instanceof PauseRequest) {
            setPauseRequest((PauseRequest) event);
        } else if (event instanceof PauseResponse) {
            setPauseResponse((PauseResponse) event);
        } else if (event instanceof PrepareRequest) {
            setPrepareRequest((PrepareRequest) event);
        } else if (event instanceof PrepareResponse) {
            setPauseResponse((PauseResponse) event);
        } else if (event instanceof ResumeRequest) {
            setResumeRequest((ResumeRequest) event);
        } else if (event instanceof ResumeResponse) {
            setResumeResponse((ResumeResponse) event);
        } else if (event instanceof StartRequest) {
            setStartRequest((StartRequest) event);
        } else if (event instanceof StartResponse) {
            setStartResponse((StartResponse) event);
        } else if (event instanceof StatusRequest) {
            setStatusRequest((StatusRequest) event);
        } else if (event instanceof StatusResponse) {
            setStatusResponse((StatusResponse) event);
        }
    }

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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        try {
            JAXBContext ctx = JAXBContext.newInstance(Mmi.class);
            final Marshaller marshaller = ctx.createMarshaller();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            marshaller.marshal(this, out);
            return out.toString();
        } catch (JAXBException e) {
            return super.toString();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(cancelRequest, cancelResponse, clearContextRequest,
                clearContextResponse, doneNotification, extensionNotification,
                newContextRequest, newContextResponse, pauseRequest,
                pauseResponse, prepareRequest, prepareResponse, resumeRequest,
                resumeResponse, startRequest, startResponse, statusRequest,
                statusResponse, version);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Mmi)) {
            return false;
        }
        Mmi other = (Mmi) obj;
        return Objects.equals(cancelRequest, other.cancelRequest)
                && Objects.equals(cancelResponse, other.cancelResponse)
                && Objects.equals(clearContextRequest,
                        other.clearContextRequest)
                && Objects.equals(clearContextResponse,
                        other.clearContextResponse)
                && Objects.equals(doneNotification, other.doneNotification)
                && Objects.equals(extensionNotification,
                        other.extensionNotification)
                && Objects.equals(newContextRequest, other.newContextRequest)
                && Objects.equals(newContextResponse, other.newContextResponse)
                && Objects.equals(pauseRequest, other.pauseRequest)
                && Objects.equals(pauseResponse, other.pauseResponse)
                && Objects.equals(prepareRequest, other.prepareRequest)
                && Objects.equals(prepareResponse, other.prepareResponse)
                && Objects.equals(resumeRequest, other.resumeRequest)
                && Objects.equals(resumeResponse, other.resumeResponse)
                && Objects.equals(startRequest, other.startRequest)
                && Objects.equals(startResponse, other.startResponse)
                && Objects.equals(statusRequest, other.statusRequest)
                && Objects.equals(statusResponse, other.statusResponse)
                && Objects.equals(version, other.version);
    }
}
