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

public interface RuleGrammar extends Grammar {
    boolean isActivatable(String ruleName);

    void setActivatable(String ruleName, boolean activatable);
    
    void addElement(String element) throws GrammarException;

    void removeElement(String element);

    Rule getRule(String ruleName);

    void addRule(Rule rule);

    void addRule(String ruleText) throws GrammarException;

    void addRules(Rule[] rules);

    void removeRule(String ruleName) throws IllegalArgumentException;

    String[] listRuleNames();

    void setAttribute(String attribute, String value)
        throws IllegalArgumentException;

    String getAttribute(String attribute) throws IllegalArgumentException;

    String[] getElements();

    RuleParse parse(String[] tokens, String ruleName)
        throws IllegalArgumentException, GrammarException;

    RuleParse parse(String text, String ruleName)
        throws IllegalArgumentException, GrammarException;

    RuleReference resolve(RuleReference ruleReference) throws GrammarException;

    void setRoot(String rootName);

    String getRoot();

    String toString();
}
