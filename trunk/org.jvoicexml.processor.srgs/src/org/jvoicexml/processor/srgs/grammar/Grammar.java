/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 58 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.processor.srgs.grammar;

import java.net.URI;


//Comp. 2.0.6

public interface Grammar {
    int ACTIVATION_INDIRECT = 0x384;

    int ACTIVATION_FOCUS = 0x385;

    int ACTIVATION_MODAL = 0x386;

    int ACTIVATION_GLOBAL = 0x387;

    int getActivationMode();

    GrammarManager getGrammarManager();

    URI getReference();

    void setActivationMode(int mode) throws IllegalArgumentException;

    boolean isActive();

    boolean isActivatable();

    void setActivatable(boolean activatable);
}
