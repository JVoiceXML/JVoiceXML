package org.jvoicexml.systemtest.testcase;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

public class IRTestCaseLibrary {
	/** Logger for this class. */
	private static final Logger LOGGER = Logger
			.getLogger(IRTestCaseLibrary.class.getName());

	private final static boolean DEBUG = false;

	private DocsRootElement rootElement = null;

	private URI docBase = null;

	IRTestCaseLibrary() {
	}

	/**
	 * create IRTestCaseLibrary from URL
	 * @param source
	 * @throws IOException
	 */
	public IRTestCaseLibrary(final URL source) throws IOException {
		rootElement = load(source.openStream());
		URI testRoot = null;
		try {
			testRoot = source.toURI().resolve(".");
			setDocBase(testRoot);
		} catch (URISyntaxException e) {
			// if URL is not current, load will throw exception first
			// need not catch exception at here.
		}
	}

	/**
	 * Load XML from InputStream
	 * @param source
	 * @return
	 */
	DocsRootElement load(final InputStream source) {
		JAXBContext jc;

		try {

			jc = JAXBContext.newInstance(DocsRootElement.class);
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

			return (DocsRootElement) um
					.unmarshal(new InputStreamReader(source));

		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int size() {
		return rootElement.testCaseList.size();
	}



	/**
	 * 
	 * @return all test cases
	 */
	public List<IRTestCase> fetchAll() {
		List<IRTestCase> list = new ArrayList<IRTestCase>();

		for (IRTestCase tc : rootElement.testCaseList) {
			list.add(tc);
		}
		return list;
	}

	/**
	 * 
	 * @param ids
	 * @return return test cases list by request id list, if it is exist.
	 */
	public List<IRTestCase> fetch(List<Integer> ids) {
		List<IRTestCase> list = new ArrayList<IRTestCase>();
		for (int id : ids) {
			IRTestCase tc = fetch(id);
			if (tc != null) {
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
		for (IRTestCase tc : rootElement.testCaseList) {
			if (id == tc.getId()) {
				return tc;
			}
		}
		return null;
	}
	
	/**
	 * for junit test
	 * @return Test case base URI
	 */
	URI getBaseUri() {
		return docBase;
	}

	private void setDocBase(URI root) {
		if (root == null || docBase != null) { // null or had set
			return;
		}
		docBase = root;
		for (IRTestCase tc : rootElement.testCaseList) {
			tc.setBaseURI(root);
		}
	}

	/**
	 * for XML file load only
	 * 
	 * @author lancer
	 * 
	 */
	@XmlRootElement(name = "tests")
	static class DocsRootElement {
		@XmlElement(name = "test")
		List<IRTestCase> testCaseList = new ArrayList<IRTestCase>();
	}

}
