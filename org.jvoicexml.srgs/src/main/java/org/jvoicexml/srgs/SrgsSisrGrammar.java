/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.srgs;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.implementation.grammar.GrammarEvaluator;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.srgs.sisr.SemanticInterpretationBlock;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Preprocessed SRGS grammar container with semantic interpretation. 
 * @author Jim Rush
 * @author Dirk Schnelle-Walka
 * @since 0.7.8
 */
public class SrgsSisrGrammar
    implements GrammarEvaluator, GrammarImplementation<SrgsXmlDocument> {
    /** Logger instance. */
    private static final Logger LOGGER = Logger
            .getLogger(SrgsSisrGrammar.class);
    private Grammar grammarNode;
    private String rootRule;
    private URI uri;
    private boolean isLiteral;

    private SemanticInterpretationBlock globalTags =
            new SemanticInterpretationBlock();
    private Map<String, SrgsRule> rules =
            new java.util.HashMap<String, SrgsRule>();

    /** A pool of grammars shared by all that were parsed together. */
    private Map<URI, SrgsSisrGrammar> grammarPool;

    public SrgsSisrGrammar(Grammar grammar, URI uri,
            Map<URI, SrgsSisrGrammar> grammarPool) {
        grammarNode = grammar;
        this.uri = uri;
        rootRule = grammar.getRoot();
        String tagFormat = grammar.getTagFormat();
        isLiteral = tagFormat != null
                && tagFormat.equals("semantics/1.0-literals");
        this.grammarPool = grammarPool;
    }

    public Grammar getGrammar() {
        return grammarNode;
    }

    public SrgsSisrGrammar getGrammarFromPool(URI uri) {
        return grammarPool.get(uri);
    }

    public void putGrammarInPool(SrgsSisrGrammar grammar) {
        grammarPool.put(grammar.getURI(), grammar);
    }

    public Map<URI, SrgsSisrGrammar> getGrammarPool() {
        return grammarPool;
    }

    public SisrRecognitionResult isValid(String[] words) {
        // TODO Auto-generated method stub
        return null;
    }

    public void addGlobalTagContent(String tagContents) {
        globalTags.append(tagContents);
    }

    public SemanticInterpretationBlock getGlobalTags() {
        return globalTags;
    }

    public void addRule(SrgsRule rule) {
        rules.put(rule.getId(), rule);
    }

    public SrgsRule getRule(String id, boolean needsToBePublic) {
        String desiredRuleId = id == null ? rootRule : id;

        final SrgsRule rule = rules.get(desiredRuleId);
        if (rule == null) {
            return null;
        }

        if (needsToBePublic && !rule.isPublic()) {
            return null;
        }

        return rule;
    }

    public Collection<SrgsRule> getRules() {
        return rules.values();
    }

    public void dump() {
        LOGGER.debug("grammar(uri=" + uri + ", root=" + rootRule + ")");
        globalTags.dump(" ");
        for (SrgsRule rule : rules.values()) {
            rule.dump(" ");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getSemanticInterpretation(final DataModel model,
            String utterance) {
        LOGGER.info("processing '" + utterance + "'");
        if (utterance == null || utterance.length() == 0) {
            return null;
        }
        final MatchConsumption mc = match(utterance);
        if (mc == null) {
            LOGGER.info("no match for '" + utterance + "'");
            return null;
        }
        if (LOGGER.isTraceEnabled()) {
            mc.dump(true);
        }
        return mc.executeSisr();
    }

    MatchConsumption match(final List<String> tokens) {
        final SrgsRule rule = rules.get(rootRule);
        if (rule == null) {
            return null;
        }
        final MatchConsumption mc = rule.match(tokens, 0);
        if (mc != null) {
            mc.setGlobalExecutableSemanticInterpretation(globalTags);
        }
        return mc;
    }

    MatchConsumption match(final String text) {
        String[] parts = text.split(" ");
        List<String> tokens = Arrays.asList(parts);
        return match(tokens);
    }

    public boolean isLiteral() {
        return isLiteral;
    }

    public void setLiteral(boolean isLiteral) {
        this.isLiteral = isLiteral;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getMediaType() {
        return GrammarType.SRGS_XML;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModeType getModeType() {
        return grammarNode.getMode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SrgsXmlDocument getGrammarDocument() {
        try {
            final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                                + grammarNode;
            final StringReader reader = new StringReader(xml);
            final InputSource source = new InputSource(reader);
            return new SrgsXmlDocument(source);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER.error("unable to create grammar document", e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getURI() {
        return uri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((globalTags == null) ? 0 : globalTags.hashCode());
        result = prime * result
                + ((grammarNode == null) ? 0 : grammarNode.hashCode());
        result = prime * result + (isLiteral ? 1231 : 1237);
        result = prime * result
                + ((rootRule == null) ? 0 : rootRule.hashCode());
        result = prime * result + ((rules == null) ? 0 : rules.hashCode());
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SrgsSisrGrammar other = (SrgsSisrGrammar) obj;
        if (globalTags == null) {
            if (other.globalTags != null) {
                return false;
            }
        } else if (!globalTags.equals(other.globalTags)) {
            return false;
        }
        if (grammarNode == null) {
            if (other.grammarNode != null) {
                return false;
            }
        } else if (!grammarNode.equals(other.grammarNode)) {
            return false;
        }
        if (grammarPool == null) {
            if (other.grammarPool != null) {
                return false;
            }
        } else if (!grammarPool.equals(other.grammarPool)) {
            return false;
        }
        if (isLiteral != other.isLiteral) {
            return false;
        }
        if (rootRule == null) {
            if (other.rootRule != null) {
                return false;
            }
        } else if (!rootRule.equals(other.rootRule)) {
            return false;
        }
        if (rules == null) {
            if (other.rules != null) {
                return false;
            }
        } else if (!rules.equals(other.rules)) {
            return false;
        }
        if (uri == null) {
            if (other.uri != null) {
                return false;
            }
        } else if (!uri.equals(other.uri)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final GrammarImplementation<SrgsXmlDocument> other) {
        return equals((Object) other);
    }

}
