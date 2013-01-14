/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.util;

import org.apache.axis.Message;
import org.apache.axis.attachments.Attachments;
import org.apache.axis.client.Call;
import org.apache.axis.utils.Messages;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.utils.Options;
import org.apache.axis.utils.XMLUtils;

import javax.xml.soap.SOAPException;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.List;
import java.util.Vector;
import javax.xml.soap.Node;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.server.AxisServer;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;



/**
 * SOAP Connection implementation
 *
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class SOAPConnectionImpl extends javax.xml.soap.SOAPConnection
{
    private boolean closed = false;
    private Integer timeout = null;

    /**
     * get the timeout value
     * @return
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * set the timeout value
     * @param timeout
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * Sends the given message to the specified endpoint and
     * blocks until it has returned the response.
     * @param   request the <CODE>SOAPMessage</CODE>
     *     object to be sent
     * @param   endpoint a <CODE>URLEndpoint</CODE>
     *     object giving the URL to which the message should be
     *     sent
     * @return the <CODE>SOAPMessage</CODE> object that is the
     *     response to the message that was sent
     * @throws  SOAPException if there is a SOAP error
     */
    public SOAPMessage call(SOAPMessage request, Object endpoint)
        throws SOAPException {
        if(closed){
            throw new SOAPException(Messages.getMessage("connectionClosed00"));
        }
        try {
            Call call = new Call(endpoint.toString());
            //call.setSOAPVersion(SOAPConstants.SOAP12_CONSTANTS);
            org.apache.axis.client.AxisClient axisengine =
                new org.apache.axis.client.AxisClient();


        // need to set it not null , if not nullpointer in sp.getEnvelope()
        ((org.apache.axis.Message) request).setMessageContext(
                new org.apache.axis.MessageContext(axisengine));
//            ((org.apache.axis.Message)request).setMessageContext(call.getMessageContext());
            Attachments attachments = ((org.apache.axis.Message)
                    request).getAttachmentsImpl();
            if (attachments != null) {
                Iterator iterator = attachments.getAttachments().iterator();
                while (iterator.hasNext()) {
                    Object attachment = iterator.next();
                    call.addAttachmentPart(attachment);
                }
            }

            String soapActionURI = checkForSOAPActionHeader(request);
            if (soapActionURI != null)
                call.setSOAPActionURI(soapActionURI);
            call.setTimeout(timeout);
            call.setReturnClass(SOAPMessage.class);
            call.setProperty(Call.CHECK_MUST_UNDERSTAND,Boolean.FALSE);
            call.invoke((Message) request);

            return call.getResponseMessage();
        } catch (java.net.MalformedURLException mue){
            throw new SOAPException(mue);
        } catch (org.apache.axis.AxisFault af){
            return new Message(af);
        }
    }

    /**
     * Checks whether the request has an associated SOAPAction MIME header
     * and returns its value.
     * @param request the message to check
     * @return the value of any associated SOAPAction MIME header or null
     * if there is no such header.
     */
    private String checkForSOAPActionHeader(SOAPMessage request) {
        MimeHeaders hdrs = request.getMimeHeaders();
        if (hdrs != null) {
            String[] saHdrs = hdrs.getHeader("SOAPAction");
            if (saHdrs != null && saHdrs.length > 0)
                return saHdrs[0];
        }
        return null;
    }

    /**
     * Closes this <CODE>SOAPConnection</CODE> object.
     * @throws  SOAPException if there is a SOAP error
     */
    public void close() throws SOAPException {
        if(closed){
            throw new SOAPException(Messages.getMessage("connectionClosed00"));
        }
        closed = true;
    }
   
     public final static String getElementValue( Node elem )
     {
         org.w3c.dom.Node kid;
         if( elem != null){
             if (elem.hasChildNodes()){
                 for( kid = elem.getFirstChild(); kid != null; kid = kid.getNextSibling() ){
                     if( kid.getNodeType() == Node.TEXT_NODE  ){
                         return kid.getNodeValue();
                     }
                 }
             }
         }
         return "";
     }
    public String doit(String[] args) throws Exception
    {
        Options opts = new Options(args);
        opts.setDefaultURL("http://localhost:8080/axis/services/MessageService");

        Service  service = new Service();
        Call     call    = (Call) service.createCall();

        call.setTargetEndpointAddress( new URL(opts.getURL()) );
        SOAPBodyElement[] input = new SOAPBodyElement[3];

        input[0] = new SOAPBodyElement(XMLUtils.StringToElement("urn:foo",
                                                                "e1", "Hello"));
        input[1] = new SOAPBodyElement(XMLUtils.StringToElement("urn:foo",
                                                                "e1", "World"));

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc            = builder.newDocument();
        Element cdataElem       = doc.createElementNS("urn:foo", "e3");
        CDATASection cdata      = doc.createCDATASection("Text with\n\tImportant  <b>  whitespace </b> and tags! ");
        cdataElem.appendChild(cdata);

        input[2] = new SOAPBodyElement(cdataElem);

        Vector          elems = (Vector) call.invoke( input );
        SOAPBodyElement elem  = null ;
        Element         e     = null ;

        elem = (SOAPBodyElement) elems.get(0);
        e    = elem.getAsDOM();

        String str = "Res elem[0]=" + XMLUtils.ElementToString(e);

        elem = (SOAPBodyElement) elems.get(1);
        e    = elem.getAsDOM();
        str = str + "Res elem[1]=" + XMLUtils.ElementToString(e);

        elem = (SOAPBodyElement) elems.get(2);
        e    = elem.getAsDOM();
        str = str + "Res elem[2]=" + XMLUtils.ElementToString(e);

        return( str );
    }

    public static void main(String[] args) throws Exception {
        SOAPConnectionImpl testMsg = new SOAPConnectionImpl();
        //testMsg.doit(args);
//        testMsg.testEnvelope(args);
    }
}
