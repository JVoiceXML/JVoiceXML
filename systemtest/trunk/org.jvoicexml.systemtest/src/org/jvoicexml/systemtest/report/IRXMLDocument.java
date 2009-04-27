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
package org.jvoicexml.systemtest.report;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * report XML root element.
 * @author lancer
 *
 */
@XmlRootElement(name = "system-report")
class IRXMLDocument {

    /**
     * date formatter.
     */
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(
            "yyyy-MM-dd hh:mm:ss");

    /**
     * report name attribute.
     */
    @XmlAttribute
    String name = "JVoiceXML version : 0.6 / release:1084";

    /**
     * report comment attribute.
     */
    @XmlElement
    String testimonial = "YOUR-WELL-FORMED-TESTIMOMIAL-CONTENT-HERE";

    /**
     * time String of report create.
     */
    @XmlElement
    String testStartTime = null;

    /**
     * time String of tests finished.
     */
    @XmlElement
    String testEndTime = null;

    /**
     * total of tests.
     */
    @XmlElement
    int totalOfTest = 0;

    /**
     * total cost in MS of all test cases.
     */
    @XmlElement
    long totalOfCost = 0;

    /**
     * result list.
     */
    @XmlElement(name = "assert")
    List<ResultItem> resultList = new ArrayList<ResultItem>();

    /**
     * XML document processing Instruction list.
     */
    private List<String> processingInstruction = new ArrayList<String>();

    /**
     * time of report create.
     */
    private Date startTime = null;

    /**
     * Construct a new object.
     */
    public IRXMLDocument() {
        processingInstruction.add("<?xml version=\"1.0\"?>");
        startTime = new Date();

    }

    /**
     * add processing Instruction.
     * @param target content of processing Instruction.
     */
    public final void addProcessingInstruction(final String target) {
        String ins = target.trim();
        if (ins.startsWith("<?") && ins.endsWith("?>")) {
            processingInstruction.add(target);
        }
    }

    /**
     * add new test result.
     * @param arg0 result of test.
     */
    public final void add(final ResultItem arg0) {
        if (arg0 != null) {
            resultList.add(arg0);
        }
    }

    /**
     * write XML document to the output stream.
     * @param out output stream.
     * @throws IOException .
     */
    public final void writeXML(final OutputStream out) throws IOException {
        totalOfTest = resultList.size();
        Date now = new Date();
        totalOfCost = now.getTime() - startTime.getTime();
        testStartTime = FORMATTER.format(startTime);
        testEndTime = FORMATTER.format(now);

        Writer writer = new OutputStreamWriter(out);
        for (String instruction : processingInstruction) {
            writer.write(instruction);
            writer.write("\n");
        }

        try {
            JAXBContext jc = JAXBContext.newInstance(IRXMLDocument.class);
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.setProperty(Marshaller.JAXB_FRAGMENT, true);
            m.marshal(this, writer);
        } catch (JAXBException e) {
            throw new IOException(e);
        }
        writer.close();
    }
}


/**
 * result item class.
 * @author lancer
 *
 */
class ResultItem {

    /**
     * test case id.
     */
    @XmlAttribute
    int id;

    /**
     * result assert of this test.
     */
    @XmlElement
    String res;

    /**
     * cost in MS of this test case.
     */
    @XmlAttribute
    long costInMS = 0;

    /**
     * test comments.
     */
    @XmlElement
    String notes = "OPTIONAL-NOTES-HERE";

    /**
     * output of log tag.
     */
    @XmlElement
    String logTag = "";

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
     * resource log.
     */
    @XmlElement
    String resourceLog = "";

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
