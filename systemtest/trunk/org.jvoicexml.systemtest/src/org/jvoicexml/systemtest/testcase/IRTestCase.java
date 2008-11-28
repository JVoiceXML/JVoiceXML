/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jvoicexml.systemtest.testcase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

import org.apache.log4j.Logger;
import org.jvoicexml.systemtest.TestCase;

/**
 * implementation test case. from w3.org .
 *
 * @author lancer
 *
 */
public class IRTestCase implements TestCase {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(IRTestCase.class
            .getName());

    private static final String OPTIONAL = "optional";

    private static final boolean DEBUG = false;

    @XmlAttribute
    int id;

    @XmlElement(name = "assert")
    Description description;

    @XmlElement
    Start start;

    @XmlElement(name = "dep")
    List<Dep> dependences = new ArrayList<Dep>();

    /**
     * reason of ignore.
     */
    private String ignoreReason = null;

    /**
     * base URI.
     */
    private URI baseURI = null;

    /**
     * @param base URI of document.
     */
    final void setBaseURI(final URI base) {
        baseURI = base;
    }

    /**
     * {@inheritDoc}
     */
    public final URI getStartURI() {
        try {
            if (baseURI == null) {
                return new URI(start.uri);
            } else {
                return baseURI.resolve(start.uri);
            }
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * @return section of specification.
     */
    public final String getSpec() {
        return description.spec.trim();
    }

    /**
     * {@inheritDoc}
     */
    public final int getId() {
        return description.id;
    }

    /**
     *
     * @return true if there have multi-page in one test case, else false.
     */
    public final boolean isSinglePage() {
        return !(dependences.size() > 0);
    }

    /**
     * {@inheritDoc}
     */
    public final String getIgnoreReason() {
        return ignoreReason;
    }

    /**
     * @param reason reason of ignore.
     */
    public final void setIgnoreReason(final String reason) {
        this.ignoreReason = reason;
    }

    /**
     * @return false if this test case is optional, else true.
     */
    public final boolean isRequest() {
        return !(OPTIONAL.equalsIgnoreCase(description.confLevel));
    }

    /**
     * @return true if execManual is not 1, else false.
     */
    public final boolean canAutoExec() {
        return !(description.execManual == 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean completenessCheck() {
        URI checkedURI = null;
        try {
            String startPage = start.uri;
            checkedURI = baseURI.resolve(startPage.trim());
            readTextStream(checkedURI);
            for (Dep d : dependences) {
                String u = d.uri;
                checkedURI = baseURI.resolve(u.trim());
                if (isText(d.type)) {
                    readTextStream(checkedURI);
                } else {
                    // TODO read other resource.
                }
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("the uri " + checkedURI
                    + " can not read. ignore this test case.", e);
            ignoreReason = "can not read. ignore this test case";
            return false;
        }
    }

    /**
     * read from URI.
     * @param startUri
     * @throws IOException
     * @throws MalformedURLException
     */
    private void readTextStream(final URI startUri)
            throws IOException, MalformedURLException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                startUri.toURL().openStream()));
        String line = null;
        do {
            line = reader.readLine();
            if (DEBUG && LOGGER.isDebugEnabled()) {
                LOGGER.debug(line);
            }
        } while (line != null);
    }

    /**
     * @param type
     * @return
     */
    private boolean isText(final String type) {
        if (type.startsWith("text")) {
            return true;
        }
        if (type.endsWith("ircgi")) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append(getId());
        buff.append(" [");
        buff.append(description.spec);
        buff.append("] ");
        buff.append(" start=");
        buff.append(start.uri);
        buff.append(" deps=");
        buff.append(dependences.size());
        buff.append(" \"");
        buff.append(description.text.trim());
        buff.append("\"");
        return buff.toString();
    }

    /**
     * IR test case dependence attribute.
     *
     * @author lancer
     *
     */
    static class Dep {

        @XmlAttribute
        int id;

        @XmlAttribute
        String uri;

        @XmlAttribute
        String type;
    }

    /**
     * IR test case start URI attribute.
     *
     * @author lancer
     *
     */
    static class Start {

        @XmlAttribute
        String uri;

        @XmlAttribute
        String type;
    }

    /**
     * IR test case description attribute.
     *
     * @author lancer
     *
     */
    static class Description {

        @XmlAttribute
        int id;

        @XmlAttribute(name = "conf_level")
        String confLevel;

        @XmlAttribute(name = "exec_manual")
        int execManual = 0;

        @XmlAttribute(name = "abs_uri")
        int absUri = 0;

        @XmlAttribute
        String spec;

        @XmlValue
        String text;
    }

}
