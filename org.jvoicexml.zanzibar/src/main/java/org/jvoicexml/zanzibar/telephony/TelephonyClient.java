/*
 * Zanzibar - Open source speech application server.
 *
 * Copyright (C) 2008-2009 Spencer Lord 
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Contact: salord@users.sourceforge.net
 *
 */
package org.jvoicexml.zanzibar.telephony;

import java.io.IOException;

import org.asteriskjava.manager.TimeoutException;


// TODO: Auto-generated Javadoc
/**
 * The interface to access telephony capabilities.
 * 
 * @author Spencer Lord {@literal <}<a href="mailto:salord@users.sourceforge.net">salord@users.sourceforge.net</a>{@literal >}
 */
public interface TelephonyClient {
    
    /**
     * Redirect blocking.
     * 
     * @param channel the channel
     * @param connectContext the connect context
     * @param connectTo the connect to
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws TimeoutException the timeout exception
     */
    public void redirectBlocking(String channel, String connectContext, String connectTo) throws  IOException, TimeoutException ;

    /**
     * Redirect.
     * 
     * @param channel the channel
     * @param connectContext the connect context
     * @param connectTo the connect to
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws TimeoutException the timeout exception
     */
    public void redirect(String channel, String connectContext, String connectTo) throws  IOException, TimeoutException ;

}
