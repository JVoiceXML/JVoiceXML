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

    final private static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @XmlAttribute
    String name = "JVoiceXML version : 0.6 / release:1084";

    @XmlElement
    Summary summary = new Summary();

    @XmlElement
    String testimonial = "YOUR-WELL-FORMED-TESTIMOMIAL-CONTENT-HERE";

    @XmlElement(name = "assert")
    List<TestResult> resultList = new ArrayList<TestResult>();

    List<String> processingInstruction = new ArrayList<String>();

    public IRXMLDocument() {
        processingInstruction.add("<?xml version=\"1.0\"?>");
        summary.testStartTime = FORMATTER.format(new Date());
    }

    public void addProcessingInstruction(String target) {
        String ins = target.trim();
        if (ins.startsWith("<?") && ins.endsWith("?>")) {
            processingInstruction.add(target);
        }
    }

    public void add(TestResult result) {
        if (result != null) {
            resultList.add(result);
        }
    }

    public void writeXML(OutputStream out) throws IOException {
        collectStatistics();
        summary.testEndTime = FORMATTER.format(new Date());

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

    void collectStatistics() {

        final int one = 1;

        List<String> types = new ArrayList<String>();
        List<Integer> counts = new ArrayList<Integer>();

        for (int i = 0; i < resultList.size(); i++) {
            TestResult item = resultList.get(i);
            String type = item.res.trim();
            boolean hasThisType = false;
            for (int j = 0; j < types.size(); j++) {
                if (type.equals(types.get(j))) {
                    hasThisType = true;
                    int count = counts.get(j) + 1;
                    counts.set(j, count);
                    break;
                }
            }
            if (!hasThisType) {
                types.add(type);
                counts.add(one);
            }
        }
        types.add("total");
        counts.add(resultList.size());

        summary.types = types;
        summary.countOfTypes = counts;

    }

}

class Summary {
    @XmlElement
    String testStartTime = null;

    @XmlElement
    String testEndTime = null;

    @XmlElement(name = "type")
    List<String> types = new ArrayList<String>();

    @XmlElement(name = "count")
    List<Integer> countOfTypes = new ArrayList<Integer>();
}

class TestResult {

    public TestResult() {

    }

    public TestResult(int id, String result, String notes, long cost) {
        this();
        this.id = id;
        this.res = result;
        this.notes = notes;
        this.costInMS = cost;
    }

    @XmlAttribute
    int id;

    @XmlAttribute
    String res;

    @XmlAttribute
    long costInMS = 0;

    @XmlElement
    String notes = "OPTIONAL-NOTES-HERE";

    @XmlElement
    List<Object> logURIs = new ArrayList<Object>();

}