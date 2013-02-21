/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.util;

import org.apache.log4j.Logger;
import javax.xml.soap.SOAPBodyElement;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import org.w3c.dom.Element;

/**
 *
 * @author Shadowman
 */
public class WebService 
{
    private static final Logger LOGGER = Logger.getLogger(WebService.class);
    public static String URL="url";
    public static String REQUEST_METHOD="request_method";
    public static String RESPONSE_METHOD="response_method";
    public static String RESPONSE_RESULT="response_result";
    String remoteURL="",remoteHost="",remotePort="",remoteRequestMethod="";
    String remoteResponseMethod="",remoteResponseResult="";
    
    class WSParam{
        public String paramname;
        public String paramvalue;
    }

    public final static String getElementValue(Node elem) {
        org.w3c.dom.Node kid;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (kid = elem.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
                    if (kid.getNodeType() == Node.TEXT_NODE) {
                        return kid.getNodeValue();
                    }
                }
            }
        }
        return "";
    }
    
     public static int test(String arguments) 
    {
        int errorcode = -1;
        try {
            System.out.println("test arguments is: "+arguments);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return errorcode;
    }
    
    public static int POSTTest(Object ... arguments) 
    {
        int errorcode = -1;
        try {
            String temp="POST Params:";
            for(Object arg: arguments) 
            {
                temp+=", " + arg;
            }
            System.out.println("all arguments are: "+temp);
            

        } catch (Exception ex) {
            ex.printStackTrace();
        } 
        return errorcode;
    }
    
    public boolean getHostPort(Map<String, Object> map) 
    {
        try {
            Set set= map.entrySet();
            Iterator iter = set.iterator(); 
            while(iter.hasNext()) 
            {
                Map.Entry entry = (Map.Entry)iter.next(); 
                String key=(String)entry.getKey();
                if (key.compareToIgnoreCase(WebService.URL)==0)
                {
                    remoteURL=(String)entry.getValue();
                    remoteHost=IPUtil.getHost(remoteURL);
                    remotePort=IPUtil.getPort(remoteURL);
                    return true;
                }
            }
        } catch (Exception ex) 
        {
            ExLog.exception(LOGGER, ex);
        } 
        return false;
    }
    
    public boolean getRequestMethod(Map<String, Object> map) 
    {
        try {
            Set set= map.entrySet();
            Iterator iter = set.iterator(); 
            while(iter.hasNext()) 
            {
                Map.Entry entry = (Map.Entry)iter.next(); 
                String key=(String)entry.getKey();
                if (key.compareToIgnoreCase(WebService.REQUEST_METHOD)==0)
                {
                    remoteRequestMethod=(String)entry.getValue();
                    return true;
                }
            }
        } catch (Exception ex) 
        {
            ExLog.exception(LOGGER, ex);
        } 
        return false;
    }
    public boolean getResponseMethod(Map<String, Object> map) 
    {
        try {
            Set set= map.entrySet();
            Iterator iter = set.iterator(); 
            while(iter.hasNext()) 
            {
                Map.Entry entry = (Map.Entry)iter.next(); 
                String key=(String)entry.getKey();
                if (key.compareToIgnoreCase(WebService.RESPONSE_METHOD)==0)
                {
                    remoteResponseMethod=(String)entry.getValue();
                    return true;
                }
            }
        } catch (Exception ex) 
        {
            ExLog.exception(LOGGER, ex);
        } 
        return false;
    }
     public boolean getResponseResult(Map<String, Object> map) 
    {
        try {
            Set set= map.entrySet();
            Iterator iter = set.iterator(); 
            while(iter.hasNext()) 
            {
                Map.Entry entry = (Map.Entry)iter.next(); 
                String key=(String)entry.getKey();
                if (key.compareToIgnoreCase(WebService.RESPONSE_RESULT)==0)
                {
                    remoteResponseResult=(String)entry.getValue();
                    return true;
                }
            }
        } catch (Exception ex) 
        {
            ExLog.exception(LOGGER, ex);
        } 
        return false;
    }
    
    public boolean getParam(Map<String, Object> map,WSParam wsparam) 
    {
        try {
            Set set= map.entrySet();
            Iterator iter = set.iterator(); 
            while(iter.hasNext()) 
            {
                Map.Entry entry = (Map.Entry)iter.next(); 
                String key=(String)entry.getKey();
                if (key.compareToIgnoreCase(WebService.REQUEST_METHOD)!=0
                        &&key.compareToIgnoreCase(WebService.RESPONSE_METHOD)!=0
                        && key.compareToIgnoreCase(WebService.URL)!=0
                        && key.compareToIgnoreCase(WebService.RESPONSE_RESULT)!=0
                        )
                {
                    wsparam.paramname=key;
                    wsparam.paramvalue=(String)entry.getValue();
                    return true;
                }
            }
        } catch (Exception ex) 
        {
            ExLog.exception(LOGGER, ex);
        } 
        return false;
    }
    
    
    public  int POST(Object ... arguments) 
    {
        int errorcode = -1;
        int timeout=20000;
        SOAPConnectionImpl connection = null;
        try {
            
            for(Object arg: arguments) 
            {
                if(arg instanceof Map)
                {
                    getHostPort((Map)arg);
                    getRequestMethod((Map)arg);
                    getResponseMethod((Map)arg);
                    getResponseResult((Map)arg);
                }
            }
            System.out.println("remoteURL:"+remoteURL + " remoteHost:"+
                    remoteHost+ " remotePort:"+remotePort+ " remoteRequestMethod:"+remoteRequestMethod
                    + " remoteResponseMethod:"+remoteResponseMethod
                    + " remoteResponseResult:"+remoteResponseResult
                    );
            long starttime = System.currentTimeMillis();
            connection = new SOAPConnectionImpl();
            SOAPFactory soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            MessageFactory factory = new org.apache.axis.soap.MessageFactoryImpl();
            SOAPMessage message = factory.createMessage();
            SOAPHeader header = message.getSOAPHeader();
            SOAPBody body = message.getSOAPBody();
            header.detachNode();
            Name bodyName = soapFactory.createName(remoteRequestMethod, "m",
                    "http://" + remoteHost + ":" + remotePort);
            SOAPBodyElement bodyElement =
                    body.addBodyElement(bodyName);
            //get parameters for web services
            for(Object arg: arguments) 
            {
                if(arg instanceof Map)
                {
                    WSParam param= new WSParam();
                    if(getParam((Map)arg,param)==true)
                    {
                        bodyElement.addChildElement(soapFactory.createName(param.paramname))
                                .addTextNode(param.paramvalue);
                    }
                }
            }
            URL endpoint = new URL(remoteURL);
            connection.setTimeout(timeout);
            System.out.println("***************************sending message:");
            message.writeTo(System.out);
            System.out.println();
            SOAPMessage response = connection.call(message, endpoint);
            SOAPPart sp = response.getSOAPPart();
            System.out.println("*************************** get response message: elapsedTime:"
                    + (System.currentTimeMillis() - starttime));
            response.writeTo(System.out);
            System.out.println();
            SOAPEnvelope se = sp.getEnvelope();
            SOAPBody sb = se.getBody();
            Element element = (Element) sb.getElementsByTagName(remoteResponseMethod).item(0);
            Element child = (Element) element.getElementsByTagName(remoteResponseResult).item(0);
            errorcode = Integer.parseInt(getElementValue((Node) child));

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return errorcode;
    }

   
    public static Map createMap(String key, String value)
    {
        if(key==null || key.isEmpty()==true || value==null || value.isEmpty()==true)
            return null;
        Map map = new Hashtable();
        map.put(key, value);
        return map;
    }

    
}
