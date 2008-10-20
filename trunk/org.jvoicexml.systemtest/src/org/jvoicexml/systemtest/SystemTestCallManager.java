package org.jvoicexml.systemtest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.callmanager.CallManager;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.systemtest.testcase.IRTestCase;
import org.jvoicexml.systemtest.testcase.IRTestCaseLibrary;

/**
 * 
 * System Test config. For fit two scenery, it can be call as CallManager in 
 * jovicexml, or start as stand alone from SystemTestMain. 
 * 
 * @author Zhang Nan
 * @version $Revision$
 * @since 0.7
 */
public class SystemTestCallManager implements CallManager {
	/** Logger for this class. */
	private static final Logger LOGGER = Logger
			.getLogger(SystemTestCallManager.class);

	private int port = 5900;

	private JVoiceXml _jvxml = null;

	private String testManifest = null;

	private boolean autoTest = true;

	private String testcases = null;

	@Override
	public void start() throws NoresourceError {
		LOGGER.debug("start()");
		IRTestCaseLibrary lib = null;

		try {
			URL url = new URL(testManifest);
			lib = new IRTestCaseLibrary(url);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("can not load test case library.", e);
			return;
		}

		List<IRTestCase> jobs = getJobs(lib, testcases);
		LOGGER.info("There have " + jobs.size() + " test case(s).");

		Thread testThread = null;
		if (autoTest) {
			testThread = new AutoTestThread(_jvxml, port, jobs);
			testThread.start();
		} else {
			LOGGER.info("not implemetns yet.");
		}
	}

	List<IRTestCase> getJobs(IRTestCaseLibrary lib, String testcases) {
		List<IRTestCase> jobs;
		if (testcases.equalsIgnoreCase("ALL")) {
			jobs = lib.fetchAll();
		} else {
			List<Integer> ids = new ArrayList<Integer>();
			String[] words = testcases.split(",");
			for (String s : words) {
				int id = -1;
				try {
					id = Integer.parseInt(s.trim());
				} catch (NumberFormatException e) {
					LOGGER.error("ids = '" + testcases + "', exception at : '"
							+ s + "' is not integer");
					continue;
				}
				ids.add(id);
			}
			jobs = lib.fetch(ids);
		}
		return jobs;
	}

	@Override
	public void stop() {
		LOGGER.debug("stop()");
	}

	@Override
	public void setJVoiceXml(JVoiceXml jvxml) {
		_jvxml = jvxml;
	}

	public void setTestManifest(String manifest) {
		testManifest = manifest;
	}

	public void setAutoTest(boolean autoTest) {
		this.autoTest = autoTest;
	}

	public void setTestcases(String cases) {
		testcases = cases;
	}

	public void setTextServerPort(int port) {
		this.port = port;
	}
}
