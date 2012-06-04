/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.systemtest.mmi.mcspecific;

import org.jvoicexml.systemtest.mmi.NotImplementedException;

/**
 * Assertion 69: A MC MUST send a ClearContextResponse event in response to a
 * ClearContextRequest event, even if doesn't take any particular action.
 * 'success'.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public class Assert69 extends AbstractAssert {
    /**
     * Constructs a new object.
     */
    public Assert69() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getId() {
        return 69;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Exception {
        throw new NotImplementedException();
    }
}
