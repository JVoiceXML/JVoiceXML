/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 63 $
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

//Comp. 2.0.6

public class RuleSequence extends RuleComponent {
    private RuleComponent[] ruleComponents;

    public RuleSequence(RuleComponent[] ruleComponents)
            throws IllegalArgumentException {
        if (ruleComponents == null) {
            throw new IllegalArgumentException(
                    "Rule components must not be null!");
        }
        this.ruleComponents = ruleComponents;
    }

    public RuleSequence(String[] tokens) throws IllegalArgumentException {
        if (tokens == null) {
            throw new IllegalArgumentException("Tokens must not be null!");
        }
        ruleComponents = new RuleComponent[tokens.length];

        for (int i = 0; i < tokens.length; i++) {
            final String token = tokens[i];
            ruleComponents[i] = new RuleToken(token);
        }
    }

    public RuleComponent[] getRuleComponents() {
        return ruleComponents;
    }

    public String toString() {
        if (ruleComponents == null) {
            return "";
        }

        final StringBuffer str = new StringBuffer();
        for (int i = 0; i < ruleComponents.length; i++) {
            final RuleComponent component = ruleComponents[i];
            if (component == null) {
                str.append(RuleSpecial.NULL.toString());
            } else {
                str.append(component.toString());
            }
        }

        return str.toString();
    }

}
