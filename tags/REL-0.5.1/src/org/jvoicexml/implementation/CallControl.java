/*
 * File:    $RCSfile: CallControl.java,v $
 * Version: $Revision: 1.9 $
 * Date:    $Date: 2006/05/22 07:56:00 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Call control.
 *
 * <p>
 * Objects that implement this interface are able to support making a third
 * party connection through a communications network, such as the telephone.
 * </p>
 *
 * <p>
 * The implementing object is created at the client side and transferred
 * to the the JVoiceXml server via serialization. The implementation
 * platform then calls the <code>open</code> method to connect to the
 * existing streams on the client side.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.9 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public interface CallControl
        extends ExternalResource, Serializable {
    /**
     * Retrieves the output for the <code>SystemOutput</code>.
     *
     * @return Output for the <code>SystemOutput</code>.
     *
     * @see org.jvoicexml.implementation.SystemOutput
     */
    OutputStream getOutputStream();

    /**
     * Retrieves the input for the <code>UserInput</code>.
     *
     * @return Input for the <code>UserInput</code>.
     *
     * @see org.jvoicexml.implementation.UserInput
     */
    InputStream getInputStream();

    /**
     * Retrieves the type of the platform to use.
     * @return Type of the platform.
     *
     * @since 0.5
     */
    String getPlatformType();
}
