/*
 * File:    $HeadURL:https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/RemoteClient.java $
 * Version: $LastChangedRevision:161 $
 * Date:    $Date:2006-11-30 10:36:05 +0100 (Do, 30 Nov 2006) $
 * Author:  $LastChangedBy:schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml;

import java.io.Serializable;
import java.net.URI;

/**
 * Data container that holds all the information that is needed to connect the
 * server side resources {@link SystemOutput}, {@link UserInput}, and
 * {@link CallControl} to the client.
 *
 * <p>
 * The resources are identified using strings, e.g. <code>jsapi10</code>
 * for an implementation based on JSAPI 1.0.
 * </p>
 *
 * <p>
 * The implementing object is created at the client side and transferred
 * to the the JVoiceXml server via serialization. The implementation
 * platform then calls the {@link RemoteConnectable#connect(ConnectionInformation)}
 * method to start the communication of the client with the server side
 * resources.
 * </p>
 *
 * <p>
 * A {@link ConnectionInformation}> may also specify the server side resource it
 * wants to use. Each {@link SystemOutput}, {@link UserInput}, and
 * {@link CallControl} can be identified using a unique string. If the
 * {@link ConnectionInformation} does not specify a resource, the default resource
 * is taken.
 * </p>
 *
 * @see RemoteConnectable
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision:161 $
 * @since 0.5.5
 */
public interface ConnectionInformation extends Serializable {
    /**
     * Retrieves a unique identifier for the {@link SystemOutput} to use.
     * @return Identifier for the {@link SystemOutput}, or <code>null</code>
     * if the default resource should be used.
     */
    String getSystemOutput();

    /**
     * Retrieves a unique identifier for the {@link UserInput} to use.
     * @return Identifier for the {@link UserInput}, or <code>null</code>
     * if the default resource should be used.
     */
    String getUserInput();

    /**
     * Retrieves a unique identifier for the {@link CallControl} to use.
     * @return Identifier for the {@link CallControl}, or <code>null</code>
     * if the default resource should be used.
     */
    String getCallControl();

    /**
     * Retrieves the URI of the caller device.
     * @return URI of the caller device.
     * @since 0.7
     */
    URI getCalledDevice();

    /**
     * Retrieves the URI of the calling device.
     * @return URI of the calling device.
     * @since 0.7
     */
    URI getCallingDevice();

    /**
     * Retrieves the name of the connection protocol.
     * <p>
     * The returned URI should be a URL for telephone calls as specified in
     * <a href="http://www.ietf.org/rfc/rfc2806.txt">IETF RFC 2806</a>.
     * </p>
     * @return name of the connection protocol.
     * @since 0.7
     */
    String getProtocolName();

    /**
     * Retrieves the version of the connection protocol.
     * @return version of the connection protocol.
     * <p>
     * The returned URI should be a URL for telephone calls as specified in
     * <a href="http://www.ietf.org/rfc/rfc2806.txt">IETF RFC 2806</a>.
     * </p>
     * @since 0.7
     */
    String getProtocolVersion();
}
