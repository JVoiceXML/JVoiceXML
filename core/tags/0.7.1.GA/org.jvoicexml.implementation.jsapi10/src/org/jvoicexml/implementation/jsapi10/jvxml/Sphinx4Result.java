/*
 * File:    $RCSfile: Sphinx4Result.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi10.jvxml;

import javax.speech.recognition.Grammar;

import com.sun.speech.engine.recognition.BaseResult;
import edu.cmu.sphinx.result.Result;

/**
 * JSAPI compliant recognition result.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
class Sphinx4Result
        extends BaseResult {
    /** The serial version UID. */
    private static final long serialVersionUID = -2843642475426733518L;

    /**
     * Constructs a new object.
     * @param grammar The current grammar.
     * @param result The result, returned by the sohinx4 recognizer.
     */
    public Sphinx4Result(final Grammar grammar,
                         final Result result) {
        super(grammar, result.getBestFinalResultNoFiller());
    }
}
