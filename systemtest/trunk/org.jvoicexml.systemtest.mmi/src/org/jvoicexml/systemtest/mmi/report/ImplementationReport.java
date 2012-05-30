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

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Report of this MMI test.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
@XmlRootElement(name = "system-report")
public final class ImplementationReport {
    /** Name of the tested system. */
    @XmlAttribute(name = "name")
    private final String name;

    /** The testimonal. */
    @XmlElement(name = "testimonial")
    private final String testimonial;

    /** Reports. */
    @XmlElement
    private final List<TestCaseReport> reports;

    /**
     * Constructs a new object.
     */
    public ImplementationReport() {
        this("");
    }

    /**
     * Constructs a new object.
     * @param systemName name of the system under test
     */
    public ImplementationReport(final String systemName) {
        reports = new java.util.ArrayList<TestCaseReport>();
        name = systemName;
        final DateFormat format =
                DateFormat.getDateInstance(DateFormat.FULL, Locale.US);
        final Date now = new Date();
        testimonial = "MMI implementation report of " + name + " from "
                + format.format(now);
    }

    /**
     * Adds the given report.
     * @param report teh report to add.
     */
    public void addReport(final TestCaseReport report) {
        reports.add(report);
    }

    /**
     * Retrieves the name of the tested system.
     * @return
     */
    public String getName() {
        return name;
    }
}
