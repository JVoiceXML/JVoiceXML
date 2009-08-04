/*
 * File:    $RCSfile: RuleNameCache.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter.grammar;

/**
 * The <code>RuleNameCache</code> provides some kinf of name
 * mapping.
 *
 * If there is a grammar document A with some rules a,b and c. Rule c
 * references to another grammar document B containing several ruls
 * c,d and f. All referenced rules have to be aggregated into one
 * instance of the RuleGrammar class.
 *
 * A Rule is identified by its name. If there are two rules from
 * different grammar documents with the same name, there will be some
 * kind of collision. Thatswhy there is a need for some class which
 * keeps track of the rulenames. If there is a collision, this class
 * will rename the grammar and replace it's references.
 *
 * @author Christoph Buente
 *
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class RuleNameCache {

    /**
     * Checks, if the given name already exists in the cache.
     *
     * @param rulename
     *        the given name
     * @return true, if name exists, alse false
     * @todo impelementation needs to be done.
     */
    public boolean ruleExists(final String rulename) {
        /** @todo implementation needs to be done. */
        return false;
    }

}
