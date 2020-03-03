/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager.mmi;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.CallManager;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.UuidSessionIdentifier;
import org.jvoicexml.client.ConnectionInformationCallMetadataModifiable;
import org.jvoicexml.client.ConnectionInformationController;
import org.jvoicexml.client.ConnectionInformationFactory;
import org.jvoicexml.client.UnsupportedResourceIdentifierException;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.profile.mmi.MmiProfile;

/**
 * A callmanager for MMI integration.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public final class MMICallManager implements CallManager {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(MMICallManager.class);

    /** Reference to JVoiceXML. */
    private JVoiceXml jvxml;

    /** The adapter for the used ETL protocol. */
    private ETLProtocolAdapter adapter;

    /** Reference to the voice modality component. */
    private VoiceModalityComponent mc;

    /** A factory for connection information objects. */
    private ConnectionInformationFactory factory;

    /** Created sessions. */
    private final Map<Session, ConnectionInformationController> sessions;

    /** Identifier for the call control to use. */
    private String call;

    /** Identifier for the spoken input to use. */
    private String input;

    /** Identifier for the system output to use. */
    private String output;

    /** The basic URI of the MMI servlet. */
    private String servletBaseUri;

    /** The extension notification converter. */
    private ExtensionNotificationDataConverter converter;

    /** The extension notification data extractor. */
    private ExtensionNotificationDataExtractor extractor;

    /**
     * Constructs a new object.
     */
    public MMICallManager() {
        sessions =
            new java.util.HashMap<Session,
                ConnectionInformationController>();
    }

    /**
     * Sets the identifier for the call control.
     * 
     * @param value
     *            identifier for the call control
     */
    public void setCall(final String value) {
        call = value;
    }

    /**
     * Sets the identifier for the spoken input.
     * 
     * @param value
     *            identifier for the spoken input
     */
    public void setInput(final String value) {
        input = value;
    }

    /**
     * Sets the identifier for the system output.
     * 
     * @param value
     *            identifier for the system output
     */
    public void setOutput(final String value) {
        output = value;
    }

    /**
     * Sets the base URI where to find the MMI servlet.
     * 
     * @param uri
     *            base URI of the servlet
     */
    public void setServletBaseUri(final String uri) {
        servletBaseUri = uri;
    }

    /**
     * Sets the adapter for the ETL specific protocol.
     * 
     * @param protocolAdapter
     *            the adapter to use.
     */
    public void setProtocolAdapter(final ETLProtocolAdapter protocolAdapter) {
        adapter = protocolAdapter;
    }

    /**
     * Sets the extension notification data converter.
     * 
     * @param conv
     *            the converter
     * @since 0.7.7
     */
    public void setExtensionNotificationDataConverter(
            final ExtensionNotificationDataConverter conv) {
        converter = conv;
    }

    /**
     * Sets the extension notification data extractor.
     * 
     * @param ext
     *            the data extractor
     * @since 0.7.7
     */
    public void setExtensionNotificationDataExtractor(
            final ExtensionNotificationDataExtractor ext) {
        extractor = ext;
    }

    /**
     * Retrieves the extension notification converter.
     * 
     * @return the converter
     * @since 0.7.7
     */
    public ExtensionNotificationDataConverter
        getExtensionNotificationDataConverter() {
        return converter;
    }

    /**
     * Sets the connection information factory.
     * 
     * @param connectionInformationFactory
     *            the connection information factory
     * 
     */
    public void setConnectionInformationFactory(
            final ConnectionInformationFactory connectionInformationFactory) {
        factory = connectionInformationFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setJVoiceXml(final JVoiceXmlCore jvoicexml) {
        jvxml = jvoicexml;

    }

    /**
     * Retrieves the voice modality component.
     * 
     * @return the voice modality component.
     */
    VoiceModalityComponent getVoiceModalityComponent() {
        return mc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws NoresourceError, IOException {
        if (adapter == null) {
            throw new IOException(
                    "Unable to hook to the ETL without a protocol adapter!");
        }
        mc = new VoiceModalityComponent(this, converter, extractor,
                servletBaseUri);
        mc.startAcceptingLifecyleEvents(adapter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStarted() {
        return mc != null;
    }

    /**
     * Creates a session. Created sessions must be cleaned up after the session
     * has ended using {@link #cleanupSession(Session)}.
     * 
     * @param data call meta data
     * @return created session
     * @throws ErrorEvent
     *             error creating the session
     * @throws UnsupportedResourceIdentifierException
     *             error in the URI scheme
     */
    public Session createSession(final CallMetadata data) throws ErrorEvent,
            UnsupportedResourceIdentifierException {
        final ConnectionInformationController controller = factory
                .createConnectionInformation(call, output, input);
        final ConnectionInformation info = controller
                .getConnectionInformation();
        if (info instanceof ConnectionInformationCallMetadataModifiable) {
            final ConnectionInformationCallMetadataModifiable modifiable =
                    (ConnectionInformationCallMetadataModifiable) info;
            modifiable.setProfile(MmiProfile.NAME);
            modifiable.setCalledDevice(data.getCalledDevice());
            modifiable.setCallingDevice(data.getCallingDevice());
            modifiable.setProtocolName(data.getProtocolName());
            modifiable.setProtocolVersion(data.getProtocolVersion());
        }
        final SessionIdentifier id = new UuidSessionIdentifier();
        final Session session = jvxml.createSession(info, id);
        sessions.put(session, controller);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("session '" + session.getSessionId() + "' created");
        }
        return session;
    }

    /**
     * Cleanup of the resources when creating the session.
     * 
     * @param session
     *            the session
     */
    public void cleanupSession(final Session session) {
        final ConnectionInformationController controller = sessions
                .get(session);
        if (controller == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("no controller known for session '"
                        + session.getSessionId() + "'");
            }
            return;
        }
        try {
            controller.cleanup();
        } finally {
            sessions.remove(session);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (mc == null) {
            return;
        }
        try {
            mc.stopAcceptingLifecycleEvents();
        } finally {
            mc = null;
        }

    }
}
