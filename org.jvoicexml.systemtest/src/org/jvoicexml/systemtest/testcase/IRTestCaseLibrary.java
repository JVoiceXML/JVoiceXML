package org.jvoicexml.systemtest.testcase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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

public class IRTestCaseLibrary implements TestCaseLibrary {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(IRTestCaseLibrary.class.getName());

    private final static boolean DEBUG = false;

    private boolean ignoresUpdated = false;

    private List<Ignore> ignoreList = new ArrayList<Ignore>();

    private List<IRTestCase> testCaseList = new ArrayList<IRTestCase>();

    private String tempIgnores;

    public void setIgnores(String ignores) {
        tempIgnores = ignores;
        ignoresUpdated = false;
    }

    public void setIgnoreList(String ignoresFile) {
        LOGGER.debug("ignoresFile = " + ignoresFile);
        try {
            URI uri = guessURI(ignoresFile);
            IgnoresRootElement rootElment = loadObject(
                    IgnoresRootElement.class, uri.toURL().openStream());
            ignoreList.addAll(rootElment.ignoreList);
            LOGGER.debug("total " + ignoreList.size() + " ignores loaded.");
        } catch (URISyntaxException e) {
            LOGGER.error("unknown uri format, check config file.", e);
        } catch (IOException e) {
            LOGGER.error("can not load ignore list with " + ignoresFile + ".",
                    e);
        }
        ignoresUpdated = false;
    }

    private URI guessURI(String location) throws URISyntaxException {
        URI uri = new URI(location);
        if (uri.getScheme() == null) {// default is file
            File f = new File(location);
            uri = f.toURI();
        }
        return uri;
    }

    public void setTestManifest(String manifest) {
        LOGGER.debug("manifest = " + manifest);
        try {
            URI uri = guessURI(manifest);
            TestsRootElement rootElement = loadObject(TestsRootElement.class,
                    uri.toURL().openStream());
            testCaseList.addAll(rootElement.testCaseList);
            LOGGER.debug("total " + testCaseList.size() + " testcase loaded.");
            URI testRoot = uri.resolve(".");
            setDocBase(testRoot);
        } catch (URISyntaxException e) {
            LOGGER.error("unknown uri format, check config file.", e);
        } catch (IOException e) {
            LOGGER.error("can not load test case library with " + manifest
                    + ".", e);
        }
        ignoresUpdated = false;
    }

    /**
     * Load XML from InputStream
     * 
     * @param source
     * @return
     */
    private <T extends Object> T loadObject(Class<T> clazz,
            final InputStream source) {
        JAXBContext jc;

        try {

            jc = JAXBContext.newInstance(clazz);
            Unmarshaller um = jc.createUnmarshaller();

            if (DEBUG) {
                um.setListener(new Unmarshaller.Listener() {
                    @Override
                    public void afterUnmarshal(Object arg0, Object arg1) {
                        super.afterUnmarshal(arg0, arg1);
                        LOGGER.debug("Object1 : " + arg0);
                        LOGGER.debug("Object2 : " + arg1);
                    }

                    @Override
                    public void beforeUnmarshal(Object arg0, Object arg1) {
                        // LOGGER.debug("Object1 : " + arg0);
                        // LOGGER.debug("Object2 : " + arg1);
                        super.beforeUnmarshal(arg0, arg1);
                    }
                });
            }

            return clazz.cast(um.unmarshal(new InputStreamReader(source)));
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.jvoicexml.systemtest.testcase.TestCaseLibrary#size()
     */
    public int size() {
        return testCaseList.size();
    }

    private void updateIgnores() {
        ignoresUpdated = true;
        for (Ignore ignore : ignoreList) {
            for (IRTestCase tc : testCaseList) {
                if (tc.getId() == ignore.id) {
                    LOGGER.debug("tc.getId()  = " + tc.getId() + " ignore.id"
                            + ignore.id);
                    tc.setIgnoreReason(ignore.reason);
                    LOGGER.debug("tc.getIgnoreReason()  = "
                            + tc.getIgnoreReason());
                }
            }
        }
        if (tempIgnores != null) {
            Set<TestCase> ignores = fetch(tempIgnores);
            for (TestCase tcCase : ignores) {
                IRTestCase irtc = (IRTestCase)tcCase;
                irtc.setIgnoreReason("temporary ignore by configuration");
            }
        }
        for (IRTestCase tcCase : testCaseList) {
            if(!tcCase.canAutoExec()){
                tcCase.setIgnoreReason("test case must be run manual.");
            }
        }
        for (IRTestCase tcCase : testCaseList) {
            if(!tcCase.isRequest()){
                tcCase.setIgnoreReason("test case was optional, skip.");
            }
        }
    }

    /* (non-Javadoc)
     * @see org.jvoicexml.systemtest.testcase.TestCaseLibrary#fetch(java.lang.String)
     */
    public Set<TestCase> fetch(String testcases) {

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
            LOGGER
                    .debug("unknown testcases '" + testcases + "' at '" + s
                            + "'");

        }
        return fetched;

    }

    /**
     * @return all test cases
     */
    public List<IRTestCase> fetchAll() {
        if (!ignoresUpdated) {
            updateIgnores();
        }
        List<IRTestCase> list = new ArrayList<IRTestCase>();

        for (IRTestCase tc : testCaseList) {
            list.add(tc);
        }
        return list;
    }

    /**
     * @param section
     * @return return test cases list by request section, if it is exist.
     */
    public List<IRTestCase> fetchSection(final String section) {
        if (!ignoresUpdated) {
            updateIgnores();
        }
        List<IRTestCase> list = new ArrayList<IRTestCase>();
        for (IRTestCase tc : testCaseList) {
            if (tc.getSpec().startsWith(section)) {
                list.add(tc);
            }
        }
        return list;
    }

    /**
     * fetch test case by id
     * 
     * @param id
     * @return null if not such id test case
     */
    public IRTestCase fetch(int id) {
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

    private void setDocBase(URI root) {
        if (root == null) { // null or had set
            return;
        }
        for (IRTestCase tc : testCaseList) {
            tc.setBaseURI(root);
        }
    }

    /**
     * for XML file load only
     * 
     * @author lancer
     */
    @XmlRootElement(name = "tests")
    static class TestsRootElement {
        @XmlElement(name = "test")
        List<IRTestCase> testCaseList = new ArrayList<IRTestCase>();
    }

    /**
     * for XML file load only
     * 
     * @author lancer
     */
    @XmlRootElement(name = "ignores")
    static class IgnoresRootElement {
        @XmlElement(name = "ignore")
        List<Ignore> ignoreList = new ArrayList<Ignore>();
        boolean updated = false;
    }

}
