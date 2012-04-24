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
package org.jvoicexml.interpreter.formitem;

import java.util.Collection;
import java.util.Locale;

import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.vxml.Option;

/**
 * An option converter for SRGS XML grammars.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 */
public final class SrgsXmlOptionConverter implements OptionConverter {

    /**
     * {@inheritDoc}
     */
    @Override
    public Grammar createVoiceGrammar(final Collection<Option> options,
            final Locale language) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Grammar createDtmfGrammar(final Collection<Option> options,
            final Locale language) {
        // TODO Auto-generated method stub
        return null;
    }

}
