/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.systemtest.testcase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
public final class IRTestCase implements TestCase {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(IRTestCase.class
            .getName());

    /** Size of the read buffer when reading in completeness tests. */
    private static final int READ_BUFFER_SIZE = 1024;

    /**
     * optional string.
     */
    private static final String OPTIONAL = "optional";

    /**
     * id attribute.
     */
    @XmlAttribute
    int id;

    /**
     * description Element.
     */
    @XmlElement(name = "assert")
    Description description;

    /**
     * start Element.
     */
    @XmlElement
    Start start;

    /**
     * dependence Element.
     */
    @XmlElement(name = "dep")
    List<Dep> dependencies = new ArrayList<Dep>();

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
    void setBaseURI(final URI base) {
        baseURI = base;
    }

    /**
     * {@inheritDoc}
     */
    public URI getStartURI() {
        String str = start.uri;
        int idx = str.indexOf(".txml");
        if (idx != -1) {
            str = str.substring(0, idx) + ".vxml";
        }
        
        try {
            if (baseURI == null) {
                return new URI(str);
            } else {
                return baseURI.resolve(str);
            }
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * @return section of specification.
     */
    public String getSpec() {
        return description.spec.trim();
    }

    /**
     * {@inheritDoc}
     */
    public int getId() {
        if (description == null) {
            return -1;
        }
        return description.id;
    }

    /**
     *
     * @return true if there have multi-page in one test case, else false.
     */
    public boolean isSinglePage() {
        return !(dependencies.size() > 0);
    }

    /**
     * {@inheritDoc}
     */
    public String getIgnoreReason() {
        return ignoreReason;
    }

    /**
     * @param reason reason of ignore.
     */
    public void setIgnoreReason(final String reason) {
        this.ignoreReason = reason;
    }

    /**
     * @return false if this test case is optional, else true.
     */
    public boolean isRequest() {
        return !(OPTIONAL.equalsIgnoreCase(description.confLevel));
    }

    /**
     * @return true if execManual is not 1, else false.
     */
    public boolean canAutoExec() {
        return !(description.execManual == 1);
    }

    /**
     * @return test case specification section.
     */
    public String getSpecSection(){
        return description.spec;
    }

    /**
     * @return test case description
     */
    public String getDescription(){
        return description.text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean completenessCheck() {
        URI checkedURI = null;
        try {
            checkedURI = getStartURI();
            readTextStream(checkedURI);
            for (Dep dependeny : dependencies) {
                String uri = dependeny.uri;
                int idx = uri.indexOf(".txml");
                if (idx != -1) {
                    uri = uri.substring(0, idx) + ".vxml";
                }
                checkedURI = baseURI.resolve(uri.trim());
                if (isText(dependeny.type)) {
                    readTextStream(checkedURI);
                } else {
                    // TODO read other resource.
                }
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("the uri '" + checkedURI
                    + "' can not read. ignore this test case.", e);
            ignoreReason = "can not read. ignore this test case";
            return false;
        }
    }

    /**
     * Perform a full read of the resource form the given uri.
     * @param uri start URI.
     * @throws IOException
     *         error reading from the given uri
     */
    private void readTextStream(final URI uri)
            throws IOException {
        final URL url = uri.toURL();
        final InputStream in = url.openStream();
        final byte[] buffer = new byte[READ_BUFFER_SIZE];
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        int num;
        do {
            num = in.read(buffer);
            if (num >= 0) {
                out.write(buffer, 0, num);
            }
        } while(num >= 0);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(out.toString());
        }
    }

    /**
     * @param type string.
     * @return true if type is based test, like text or cgi.
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
    public String toString() {
        final StringBuffer buff = new StringBuffer();
        buff.append(getId());
        if (description != null) {
            buff.append(" [");
            buff.append(description.spec);
            buff.append("] ");
        }
        buff.append(" start=");
        if (start == null) {
            buff.append("null");
        } else {
            buff.append(start.uri);
        }
        buff.append(" deps=");
        if (dependencies == null) {
            buff.append("null");
        } else {
            buff.append(dependencies.size());
        }
        if (description != null) {
            buff.append(" \"");
            buff.append(description.text.trim());
            buff.append("\"");
        }
        return buff.toString();
    }

    /**
     * IR test case dependence attribute.
     *
     * @author lancer
     *
     */
    static class Dep {

        /**
         * id Attribute.
         */
        @XmlAttribute
        int id;

        /**
         * URI Attribute.
         */
        @XmlAttribute
        String uri;

        /**
         * type Attribute.
         */
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

        /**
         * URI Attribute.
         */
        @XmlAttribute
        String uri;

        /**
         * type Attribute.
         */
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

        /**
         * type Attribute.
         */
        @XmlAttribute
        int id;

        /**
         * 'optional' if the test case is optional.
         */
        @XmlAttribute(name = "conf_level")
        String confLevel;

        /**
         * '1' if need execute by manual.
         */
        @XmlAttribute(name = "exec_manual")
        int execManual = 0;

        /**
         * URI Attribute.
         */
        @XmlAttribute(name = "abs_uri")
        int absUri = 0;

        /**
         * specification section Attribute.
         */
        @XmlAttribute
        String spec;

        /**
         * content of description.
         */
        @XmlValue
        String text;
    }

}
