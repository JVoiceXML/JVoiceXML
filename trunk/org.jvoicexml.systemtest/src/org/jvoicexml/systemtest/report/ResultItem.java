/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/systemtest/trunk/org.jvoicexml.systemtest/src/org/jvoicexml/systemtest/testcase/IRTestCaseLibrary.java $
 * Version: $LastChangedRevision: 2996 $
 * Date:    $Date: 2012-02-15 15:02:23 +0100 (Mi, 15 Feb 2012) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.systemtest.report;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.jvoicexml.systemtest.TestResult;

/**
 * result item class.
 * @author lancer
 *
 */
final class ResultItem {

    /**
     * test case id.
     */
    @XmlAttribute
    int id;

    /**
     * cost in MS of this test case.
     */
    @XmlAttribute
    long costInMS;

    /**
     * output of log tag.
     */
    @XmlElement
    String logTag = "";

    /**
     * resource log.
     */
    @XmlElement
    String resourceLog = "";

    /**
     * remote log URI.
     */
    @XmlElement
    String remoteLogURI = "";

    /**
     * 'true', if has error level in remote log.
     */
    @XmlElement
    String hasErrorLevelLog = "";

    /**
     * remote log URI.
     */
    @XmlElement
    String localLogURI = "";

    /**
     * result assert of this test.
     */
    @XmlElement
    TestResult res;

    /**
     * test comments.
     */
    @XmlElement
    String notes = "OPTIONAL-NOTES-HERE";

    /**
     * test case specification section.
     */
    @XmlElement
    String spec = "";

    /**
     * test case description.
     */
    @XmlElement
    String desc = "";
}
