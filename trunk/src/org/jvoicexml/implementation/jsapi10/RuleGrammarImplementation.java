/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
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

package org.jvoicexml.implementation.jsapi10;

import javax.speech.recognition.FinalRuleResult;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.GrammarListener;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.ResultListener;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleName;
import javax.speech.recognition.RuleParse;

import org.jvoicexml.GrammarImplementation;

/**
 * Implementation of a JSGF grammar.
 *
 * <p>
 * This is a bridge of JSAPI 1.0 {@link RuleGrammar} to
 * {@link GrammarImplementation}.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5.5
 */
public final class RuleGrammarImplementation
    implements GrammarImplementation, RuleGrammar {
    /** The encapsulated grammar. */
    private final RuleGrammar grammar;

    /**
     * Constructs a new object.
     * @param ruleGrammar the grammar.
     */
    public RuleGrammarImplementation(final RuleGrammar ruleGrammar) {
        grammar = ruleGrammar;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return grammar.getName();
    }

    /**
     * {@inheritDoc}
     */
    public void addImport(final RuleName rule) {
        grammar.addImport(rule);
    }

    /**
     * {@inheritDoc}
     */
    public void deleteRule(final String rule) {
        grammar.deleteRule(rule);
    }

    /**
     * {@inheritDoc}
     */
    public Rule getRule(final String rule) {
        return grammar.getRule(rule);
    }

    /**
     * {@inheritDoc}
     */
    public Rule getRuleInternal(final String rule) {
        return grammar.getRuleInternal(rule);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return grammar.isEnabled();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled(final String rule) {
        return grammar.isEnabled(rule);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRulePublic(final String rule) {
        return grammar.isRulePublic(rule);
    }

    /**
     * {@inheritDoc}
     */
    public RuleName[] listImports() {
        return grammar.listImports();
    }

    /**
     * {@inheritDoc}
     */
    public String[] listRuleNames() {
        return grammar.listRuleNames();
    }

    /**
     * {@inheritDoc}
     */
    public RuleParse parse(final String arg0, final String arg1)
        throws GrammarException {
        return grammar.parse(arg0, arg1);
    }

    /**
     * {@inheritDoc}
     */
    public RuleParse parse(final String[] arg0, final String arg1)
        throws GrammarException {
        return grammar.parse(arg0, arg1);
    }

    /**
     * {@inheritDoc}
     */
    public RuleParse parse(final FinalRuleResult result, final int arg1,
            final String arg2) throws GrammarException {
        return grammar.parse(result, arg1, arg2);
    }

    /**
     * {@inheritDoc}
     */
    public void removeImport(final RuleName rule) {
        grammar.removeImport(rule);
    }

    /**
     * {@inheritDoc}
     */
    public RuleName resolve(final RuleName rule) throws GrammarException {
        return grammar.resolve(rule);
    }

    /**
     * {@inheritDoc}
     */
    public Rule ruleForJSGF(final String rule) throws GrammarException {
        return grammar.ruleForJSGF(rule);
    }

    /**
     * {@inheritDoc}
     */
    public void setEnabled(final boolean rule) {
        grammar.setEnabled(rule);
    }

    /**
     * {@inheritDoc}
     */
    public void setEnabled(final String rule, final boolean enabled) {
        grammar.setEnabled(rule, enabled);
    }

    /**
     * {@inheritDoc}
     */
    public void setEnabled(final String[] rules, final boolean enabled) {
        grammar.setEnabled(rules, enabled);
    }

    /**
     * {@inheritDoc}
     */
    public void setRule(final String arg0, final Rule arg1,
            final boolean arg2) {
        grammar.setRule(arg0, arg1, arg2);
    }

    /**
     * {@inheritDoc}
     */
    public void addGrammarListener(final GrammarListener listener) {
        grammar.addGrammarListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void addResultListener(final ResultListener listener) {
        grammar.addResultListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    public int getActivationMode() {
        return grammar.getActivationMode();
    }

    /**
     * {@inheritDoc}
     */
    public Recognizer getRecognizer() {
        return grammar.getRecognizer();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActive() {
        return grammar.isActive();
    }

    /**
     * {@inheritDoc}
     */
    public void removeGrammarListener(final GrammarListener listener) {
        grammar.removeGrammarListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void removeResultListener(final ResultListener listener) {
        grammar.removeResultListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void setActivationMode(final int mode) {
        grammar.setActivationMode(mode);
    }
}
