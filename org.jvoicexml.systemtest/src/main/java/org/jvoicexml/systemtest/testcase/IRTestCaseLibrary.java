/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;
import org.jvoicexml.systemtest.TestCase;
import org.jvoicexml.systemtest.TestCaseLibrary;

/**
 * IR test case library.
 *
 * @author lancer
 * @author Dirk Schnelle-Walka
 *
 */
public final class IRTestCaseLibrary implements TestCaseLibrary {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(IRTestCaseLibrary.class.getName());
    /** The base URL of the system test servlet. */
    private String baseURL;
    
    /** The test suit to use, e.g. vxml20_1.0.1d, vxml21_0.0.5. */
    private String testsuite;
    
    /**
     * ignore list updated flag.
     */
    private boolean ignoresUpdated = false;

    /**
     * ignore list.
     */
    private final List<Ignore> ignoreList = new java.util.ArrayList<Ignore>();

    /**
     * test cases list.
     */
    private final List<IRTestCase> testCaseList = new java.util.ArrayList<IRTestCase>();

    /**
     * ignore case string.
     */
    private String tempIgnores;

    /**
     * @param ignores
     *            string.
     */
    public final void setIgnores(final String ignores) {
        tempIgnores = ignores;
        ignoresUpdated = false;
    }

    /**
     * ignore list file path.
     * 
     * @param ignoresFile
     *            path
     */
    public void setIgnoreList(final String ignoresFile) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ignoresFile = " + ignoresFile);
        }
        final InputStream in = IgnoresRootElement.class
                .getResourceAsStream(ignoresFile);
        IgnoresRootElement rootElment = loadObject(IgnoresRootElement.class,
                in);
        ignoreList.addAll(rootElment.ignoreList);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("total " + ignoreList.size() + " ignores loaded.");
        }
        ignoresUpdated = false;
    }

    /**
     * Sets the bas URL of the sytem test servlet. 
     * @param url
     * @since 0.7.9
     */
    public void setBaseURL(final String url) {
        baseURL = url;
    }
    
    /**
     * Sets the test suite to use.
     * @param suite
     * @since 0.7.9
     */
    public void setTestsuite(final String suite) {
        testsuite = suite;
    }
    
    /**
     * Loads all test cases
     * 
     * @throws IOException if the manifest could not be loaded
     */
    public void loadTestcases() throws IOException {
        URL manifest = null;
        try {
            manifest = new URL(baseURL + "/" + testsuite 
                    + "/manifest.xml"); 
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("loading manifest '" + manifest + "'");
            }
            final InputStream in = manifest.openStream();
            final TestsRootElement rootElement = loadObject(
                    TestsRootElement.class, in);
            if (rootElement != null) {
                testCaseList.addAll(rootElement.testCaseList);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("total " + testCaseList.size()
                            + " testcase loaded.");
                }
                
                final String testRoot = baseURL + "/" + testsuite
                        + "/vxml-srgs";
                for (IRTestCase tc : testCaseList) {
                    tc.setBaseURI(testRoot);
                }
            }
        } catch (IOException e) {
            LOGGER.error(
                    "can not load test case library with " + manifest 
                        + ". Is the servlet deployed?", e);
            throw new IOException("can not load test case library with "
                        + manifest
                        + ". Is the servlet deployed?", e);
        }
        ignoresUpdated = false;
    }

    /**
     * Load XML from InputStream.
     *
     * @param clazz
     *            Object class.
     * @param source
     *            configuration stream
     * @return object of load
     * @param <T>
     *            type of the object to load
     */
    private <T extends Object> T loadObject(final Class<T> clazz,
            final InputStream source) {
        try {
            final JAXBContext jc = JAXBContext.newInstance(clazz);
            final Unmarshaller um = jc.createUnmarshaller();

            if (LOGGER.isDebugEnabled()) {
                um.setListener(new Unmarshaller.Listener() {
                    @Override
                    public void afterUnmarshal(final Object arg0,
                            final Object arg1) {
                        super.afterUnmarshal(arg0, arg1);
                        LOGGER.debug("Object1 : " + arg0);
                        LOGGER.debug("Object2 : " + arg1);
                    }

                    @Override
                    public void beforeUnmarshal(final Object arg0,
                            final Object arg1) {
                        super.beforeUnmarshal(arg0, arg1);
                    }
                });
            }

            final Reader reader = new InputStreamReader(source);
            final Object o = um.unmarshal(reader);
            return clazz.cast(o);
        } catch (JAXBException e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        } catch (NullPointerException e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public final int size() {
        return testCaseList.size();
    }

    /**
     * Update ignores.
     * 
     * @throws IOException
     *         error fetching the test cases
     */
    private void updateIgnores() throws IOException {
        ignoresUpdated = true;
        for (Ignore ignore : ignoreList) {
            for (IRTestCase tc : testCaseList) {
                if (tc.getId() == ignore.id) {
                    tc.setIgnoreReason(ignore.reason);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("tc.getId()  = " + tc.getId()
                                + " ignore.id" + ignore.id);

                        LOGGER.debug("tc.getIgnoreReason()  = "
                                + tc.getIgnoreReason());
                    }
                }
            }
        }
        if (tempIgnores != null) {
            Set<TestCase> ignores = fetch(tempIgnores);
            for (TestCase tcCase : ignores) {
                IRTestCase irtc = (IRTestCase) tcCase;
                irtc.setIgnoreReason("temporary ignore by configuration");
            }
        }
        for (IRTestCase tcCase : testCaseList) {
            if (!tcCase.canAutoExec()) {
                tcCase.setIgnoreReason("test case must be run manual.");
            }
        }
        for (IRTestCase tcCase : testCaseList) {
            if (!tcCase.isRequest()) {
                tcCase.setIgnoreReason("test case was optional, skip.");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public final Set<TestCase> fetch(final String testcases) throws IOException {
        if (testCaseList.isEmpty()) {
            loadTestcases();
        }
        if (testcases.equalsIgnoreCase("ALL")) {
            Set<TestCase> fetched = new LinkedHashSet<TestCase>();
            fetched.addAll(fetchAll());
            return fetched;
        }

        Set<TestCase> fetched = new LinkedHashSet<TestCase>();
        String[] words = testcases.split(",");
        for (String s : words) {
            String cleanedString = s.trim().toUpperCase();
            if (cleanedString.matches("[0-9]+")) {
                int id = Integer.parseInt(s.trim());
                TestCase tc = fetch(id);
                if (tc != null) {
                    fetched.add(fetch(id));
                }
                continue;
            }
            if (cleanedString.matches("[0-9]+ *- *[ 0-9]+")) {
                String[] seq = cleanedString.split("-");
                int first = Integer.parseInt(seq[0].trim());
                int last = Integer.parseInt(seq[1].trim());
                for (int id = first; id <= last; id++) {
                    TestCase tc = fetch(id);
                    if (tc != null) {
                        fetched.add(fetch(id));
                    }
                }
                continue;
            }
            if (cleanedString.startsWith("SPEC=")) {
                String section = cleanedString.substring("SPEC=".length())
                        .trim();
                fetched.addAll(fetchSection(section));
                continue;
            }

            LOGGER.info("unknown testcases '" + testcases + "' at '" + s + "'");

        }
        return fetched;

    }

    /**
     * @return all test cases
     * @throws IOException
     *          error fetching the test cases
     */
    public List<IRTestCase> fetchAll() throws IOException {
        if (!ignoresUpdated) {
            updateIgnores();
        }
        List<IRTestCase> list = new java.util.ArrayList<IRTestCase>();

        for (IRTestCase tc : testCaseList) {
            list.add(tc);
        }
        return list;
    }

    /**
     * @param section
     * @return return test cases list by request section, if it is exist.
     * @throws IOException
     *          error fetching the test cases
     */
    public final List<IRTestCase> fetchSection(final String section)
                throws IOException{
        if (!ignoresUpdated) {
            updateIgnores();
        }
        List<IRTestCase> list = new java.util.ArrayList<IRTestCase>();
        for (IRTestCase tc : testCaseList) {
            if (tc.getSpec().startsWith(section)) {
                list.add(tc);
            }
        }
        return list;
    }

    /**
     * fetch test case by id.
     *
     * @param id
     *            of test case.
     * @return null if not such id test case
     * @throws IOException
     *          error ethcing the test cases
     */
    public final IRTestCase fetch(final int id) throws IOException {
        if (!ignoresUpdated) {
            updateIgnores();
        }

        for (IRTestCase tc : testCaseList) {
            if (id == tc.getId()) {
                return tc;
            }
        }
        return null;
    }

    /**
     * for XML file load only.
     *
     * @author lancer
     */
    @XmlRootElement(name = "tests")
    static class TestsRootElement {
        /* test case list. */
        @XmlElement(name = "test")
        List<IRTestCase> testCaseList = new java.util.ArrayList<IRTestCase>();
    }

    /**
     * for XML file load only.
     *
     * @author lancer
     */
    @XmlRootElement(name = "ignores")
    static class IgnoresRootElement {
        /* ignore list. */
        @XmlElement(name = "ignore")
        List<Ignore> ignoreList = new java.util.ArrayList<Ignore>();
    }

}
