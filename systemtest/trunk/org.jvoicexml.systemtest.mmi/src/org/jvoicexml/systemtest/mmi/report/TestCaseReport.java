/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.systemtest.mmi.report;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * Result of a single assertion.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
@XmlType(name = "assert")
@XmlAccessorType(XmlAccessType.FIELD)
public final class TestCaseReport {
    /** The id of this test case. */
    @XmlAttribute(name = "id")
    private final int id;

    /** Result of this test case. */
    @XmlAttribute(name = "res")
    private TestResult result;

    /** Optional notes. */
    @XmlValue
    private String notes;

    /** Constructs a new object. */
    public TestCaseReport() {
        this(-1);
    }

    /**
     * Constructs a new object.
     * @param assertionId the id of this test case
     */
    public TestCaseReport(final int assertionId) {
        id = assertionId;
    }

    /**
     * Retrieves the test result.
     * @return the result
     */
    public TestResult getResult() {
        return result;
    }

    /**
     * Sets the test result.
     * @param rslt the result to set
     */
    public void setResult(final TestResult rslt) {
        result = rslt;
    }

    /**
     * Retrieves the optional notes.
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the optional notes.
     * @param value the notes to set
     */
    public void setNotes(final String value) {
        notes = value;
    }

    /**
     * Retrieves the id of this test case.
     * @return the id
     */
    public int getId() {
        return id;
    }
}
