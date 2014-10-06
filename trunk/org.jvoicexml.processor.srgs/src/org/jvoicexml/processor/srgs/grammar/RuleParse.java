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

import java.util.Vector;

//Comp. 2.0.6

public class RuleParse extends RuleComponent {
    private RuleReference ruleReference;

    private RuleComponent parse;

    public RuleParse(RuleReference ruleReference, RuleComponent parse) {
        this.ruleReference = ruleReference;
        this.parse = parse;
    }

    public Object[] getTags() {
        if (parse == null) {
            return null;
        }

        final Vector parseTags = new Vector();
        addTags(parseTags, parse);

        final Object[] tags = new Object[parseTags.size()];
        parseTags.copyInto(tags);

        return tags;
    }

    @SuppressWarnings("unchecked")
    private void addTags(Vector tags, RuleComponent component) {
        if (component instanceof RuleTag) {
            final RuleTag tag = (RuleTag) component;
            final Object tagName = tag.getTag();
            tags.addElement(tagName);
        } else if (component instanceof RuleAlternatives) {
            final RuleAlternatives alternatives = (RuleAlternatives) component;
            RuleComponent[] components = alternatives.getRuleComponents();
            for (int i = 0; i < components.length; i++) {
                final RuleComponent actComponent = components[i];
                addTags(tags, actComponent);
            }
        } else if (component instanceof RuleCount) {
            final RuleCount count = (RuleCount) component;
            final RuleComponent actComponent = count.getRuleComponent();
            addTags(tags, actComponent);
        } else if (component instanceof RuleParse) {
            final RuleParse parse = (RuleParse) component;
            final RuleComponent actComponent = parse.getParse();
            addTags(tags, actComponent);
        } else if (component instanceof RuleSequence) {
            final RuleSequence sequence = (RuleSequence) component;
            RuleComponent[] components = sequence.getRuleComponents();
            for (int i = 0; i < components.length; i++) {
                final RuleComponent actComponent = components[i];
                addTags(tags, actComponent);
            }
        }
    }

    public RuleComponent getParse() {
        return parse;
    }

    public RuleReference getRuleReference() {
        return ruleReference;
    }

    public String toString() {
        if (parse == null) {
            return "";
        }

        StringBuffer str = new StringBuffer();

        if (ruleReference != null) {
            ruleReference.appendStartTag(str);
            str.append('>');
        }

        str.append(parse.toString());

        if (ruleReference != null) {
            str.append("</ruleref>");
        }

        return str.toString();
    }

}
