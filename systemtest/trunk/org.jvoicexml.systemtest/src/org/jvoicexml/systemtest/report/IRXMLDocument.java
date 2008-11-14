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

@XmlRootElement(name = "system-report")
class IRXMLDocument {

    final private static SimpleDateFormat FORMATTER = new SimpleDateFormat(
            "yyyy-MM-dd hh:mm:ss");

    @XmlAttribute
    String name = "JVoiceXML version : 0.6 / release:1084";

    @XmlElement
    String testimonial = "YOUR-WELL-FORMED-TESTIMOMIAL-CONTENT-HERE";

    @XmlElement(name = "assert")
    List<ResultItem> resultList = new ArrayList<ResultItem>();


    @XmlElement
    String testStartTime = null;

    @XmlElement
    String testEndTime = null;

    @XmlElement
    int totalOfTest = 0;
    
    @XmlElement
    long totalOfCost = 0;
    
    private Date startTime = null;
    
    private List<String> processingInstruction = new ArrayList<String>();

    public IRXMLDocument() {
        processingInstruction.add("<?xml version=\"1.0\"?>");
        startTime = new Date();

    }

    public void addProcessingInstruction(String target) {
        String ins = target.trim();
        if (ins.startsWith("<?") && ins.endsWith("?>")) {
            processingInstruction.add(target);
        }
    }

    public void add(ResultItem result) {
        if (result != null) {
            resultList.add(result);
        }
    }

    public void writeXML(OutputStream out) throws IOException {
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


class ResultItem {

    public ResultItem() {

    }

    public ResultItem(int id, String result, String notes, long cost) {
        this();
        this.id = id;
        this.res = result;
        this.notes = notes;
        this.costInMS = cost;
    }

    @XmlAttribute
    int id;

    @XmlElement
    String res;

    @XmlAttribute
    long costInMS = 0;

    @XmlElement
    String notes = "OPTIONAL-NOTES-HERE";

    @XmlElement
    List<Object> logURIs = new ArrayList<Object>();

}