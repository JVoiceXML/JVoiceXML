/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jvoicexml.implementation.mobicents;

import com.vnxtele.oracle.VOracleCfg;
import com.vnxtele.telnetd.VTelnetD;
import com.vnxtele.telnetd.io.BasicTerminalIO;
import java.io.*;
import java.util.*;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.mobicents.servlet.sip.catalina.rules.MatchingRuleParser;
import org.mobicents.servlet.sip.core.descriptor.MatchingRule;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vnxtele.util.*;
import javax.servlet.sip.SipServlet;

/**
 *
 * @author Shadowman
 */
public class VAppCfg implements ServletContextListener {

    private static volatile VAppCfg instance = null;
    private Properties prop = null;
    //configurations of vappctl
    public static String VAPP_HOME = "";
    //configurations for logging process
    public static int nInsts = 5;
    public static String[] instNames = {"VAppCtlr", "VAppListener", "VAppSession", "VTelnetSrv",
        "VTelnetSession"};
    /**
     * main configuration will be read from the configuration file
     */
    public static String logdir = "";
    public static String genCfgFile = "";
    public static String logCfgFile = "";
    /*********************configurations for testing local mode only **********/
    public static boolean enableRemoveInvlChars = false;
    public static HashMap<String, MatchingRule> ruleServletMap = new HashMap();
    /**
     * In this case MGW and CA are on different local host
     */
    public static String LOCAL_ADDRESS = System.getProperty(
            "jboss.bind.address", "127.0.0.1");
    public static String CA_PORT = "2921";
    public static String LOCAL_MGCP_PORT = "2727";
    public static String INBOUND_MGW_ADDRESS = System.getProperty(
            "jboss.bind.address", "127.0.0.1");
    public static String OUTBOUND_MGW_ADDRESS = System.getProperty(
            "jboss.bind.address", "127.0.0.1");
    public static String MGW_PORT = "2427";
    public static String VAS_MMS_VERSION = "VNXTele VAS-R&D MEDIA SERVER V1.0";
    public static final String CONTACT_HEADER = "Contact";
    public static boolean initConfig = false;
    /**
     * binding address and port of Tomcat HTTP Server
     */
    public static String httpServerBindAddress = "0.0.0.0";
    public static String httpServerBindPort = "8080";
    public static String recordingDir = "/tmp";
    public static VNXLog vgenLog = null;
    /**
     * sip stack address
     */
    public static String sipStackAddr = "0.0.0.0";
    public static String sipStackPort = "5000";
    
    //signal digits from the users
//    public static String digitPattern="Signal=";
    public static String digitPattern = "digit=";
    public static String digitModPattern = "Signal=";
    //endpoint name for media server
    //for mms-standalone-2.1.0.BETA1
//    public static String packetRelayEndpointNamePattern="/mobicents/media/packetrelay/$";
    //for mms-standalone-2.4.0
    public static String packetRelayEndpointNamePattern = "mobicents/media/packetrelay/$";
    public static String conferEndpointNamePattern = "mobicents/media/cnf/$";
    public static String ivrEndpointNamePattern = "mobicents/media/IVR/$";
    public static boolean enableModSDP = false;
    /**
     * 
     */
    public static boolean enableEditContactHeader = false;
    public static String INBOUND_CONTACT_IP = "192.168.146.146";
    /**
     * 
     */
    public static String defaulSipServlet = "VIVRGate";
    public static String DATE_FORMAT_NOW = "dd/MM/yyyy HH:mm:ss.SSS";
    /**
     *db interface 
     */
    public static EmbeddedJVXML embJVXML=null;
    /**
     * routing all sip messages to a specified sip proxy
     */
    public static int enableBackProxy = 0;
    public static String backSIPProxyIP = "10.50.144.74";
    public static int backSIPProxyPort = 5070;
    /**
     * customizing attribute of SIP Serverlet
     */
    public static String VCalled_CallID = "Called_CallID";
    public static String VSipFactoryImpl = "VSipFactoryImpl";
    public static String LOCAL_GEN_SIPREQUEST = "LOCAL_GEN_SIPREQUEST";
    /************supporting telnet **************/
    public static String telnUser = "vivrmng";
    public static String telnPass = "ivradmsystm";
    public static BasicTerminalIO m_IO;
    public static VTelnetD telnet = null;
    public static String telnetCfgFile = "";

    /*********************************************/
    public static String CATALINA_HOME="";
    public static String projectHome="";
    public static SipServlet sipServlet = null;
    /**************************/
    public static int INCOMING_CALL=1;
    public static int OUTGOING_CALL=2;
    
    /******************************************/
    public static int conferMngRate=60;
    public static int conferExpiredTime=60;
    ///
    public static int mgcpClientMngRate=60;
    
    public static VAppCfg getInstance() {
        if (instance == null) {
            synchronized (VAppCfg.class) {
                if (instance == null) {
                    instance = new VAppCfg();
                }
            }
        }
        return instance;
    }

    public void contextInitialized(ServletContextEvent event) {
        VAppCfg appCfg = getInstance();
        vgenLog = new VNXLog(appCfg.nInsts, appCfg.instNames, appCfg.logdir,
                appCfg.logCfgFile);
        VNXLog.info2("init context for VAppCfg:" + appCfg);
        appCfg.init();
        event.getServletContext().setAttribute("VAppCfg", appCfg);
    }

    public void initcontext() {
        VAppCfg appCfg = getInstance();
        vgenLog = new VNXLog(appCfg.nInsts, appCfg.instNames, appCfg.logdir,
                appCfg.logCfgFile);
        VNXLog.info2("init context for VAppCfg:" + appCfg);
        appCfg.init();
    }

    public void contextDestroyed(ServletContextEvent event) {
        VNXLog.info2("destroy context for VAppCfg:" + Integer.toHexString(hashCode()));
    }

    public VAppCfg() {
        VAPP_HOME = System.getProperty("VNXIVR_HOME");
        if (VAPP_HOME == null) {
            VAPP_HOME = "";
        }

        VNXLog.info2("constructor a VAppCfg hashCode:" + Integer.toHexString(hashCode()));
        genCfgFile = VAPP_HOME + "conf" + File.separatorChar + "VNXIVR.cfg";
        logdir = VAPP_HOME + "logs";
        logCfgFile = VAPP_HOME + "conf" + File.separatorChar + "VNXIVRLog.cfg";
        telnetCfgFile = VAPP_HOME + "conf" + File.separatorChar + "VNXIVR.cfg";

    }

    public void init() {
        try {
            if (initConfig == true) {
                VNXLog.info2("system is already init configurations");
                return;
            } else {
                initConfig = true;
            }
            //telnet session
//            VNXLog.info("crearte VTelnetD server...:from telnetCfgFile:" + telnetCfgFile);
//            telnet = new VTelnetD();
//            telnet.init(telnetCfgFile);
//            telnet.start();
            //loading configuration from file
            prop = new Properties();
            VNXLog.info2("reading configuration informations from file:" + genCfgFile);

            FileInputStream gencfgFile = new FileInputStream(genCfgFile);
            prop.load(gencfgFile);
            VNXLog.info2("*******print out properties from the config file: \n" + prop);
            //
            
            
                    
            sipStackAddr = prop.getProperty("VNXIVR.sipStackAddr");
            VNXLog.info2("sipStackAddr:" + sipStackAddr);
            
            sipStackPort = prop.getProperty("VNXIVR.sipStackPort");
            VNXLog.info2("sipStackPort:" + sipStackPort);
            
            LOCAL_ADDRESS = prop.getProperty("VNXIVR.local_callAgent_address");
            VNXLog.info2("LOCAL_ADDRESS:" + LOCAL_ADDRESS);

            CA_PORT = prop.getProperty("VNXIVR.local_callAgent_port");
            VNXLog.info2("CA_PORT:" + CA_PORT);
            
            

            LOCAL_MGCP_PORT = prop.getProperty("VNXIVR.local_mgcp_port");
            VNXLog.info2("LOCAL_MGCP_PORT:" + LOCAL_MGCP_PORT);

            INBOUND_MGW_ADDRESS = prop.getProperty("VNXIVR.INBOUND_MGW_ADDRESS");
            VNXLog.info2("INBOUND_MGW_ADDRESS:" + INBOUND_MGW_ADDRESS);
            OUTBOUND_MGW_ADDRESS = prop.getProperty("VNXIVR.OUTBOUND_MGW_ADDRESS");
            VNXLog.info2("OUTBOUND_MGW_ADDRESS:" + OUTBOUND_MGW_ADDRESS);

            MGW_PORT = prop.getProperty("VNXIVR.MGW_PORT");
            VNXLog.info2("MGW_PORT:" + MGW_PORT);

            enableBackProxy = Integer.parseInt(prop.getProperty("VNXIVR.enableBackProxy"));
            VNXLog.info2("enableBackProxy:" + enableBackProxy);

            backSIPProxyIP = prop.getProperty("VNXIVR.backSIPProxyIP");
            VNXLog.info2("backSIPProxyIP:" + backSIPProxyIP);

            backSIPProxyPort = Integer.parseInt(prop.getProperty("VNXIVR.backSIPProxyPort"));
            VNXLog.info2("backSIPProxyPort:" + backSIPProxyPort);


            digitPattern = prop.getProperty("VNXIVR.digitPattern");
            VNXLog.info2("digitPattern:" + digitPattern);

            packetRelayEndpointNamePattern = prop.getProperty("VNXIVR.packetRelayEndpointNamePattern");
            VNXLog.info2("packetRelayEndpointNamePattern:" + packetRelayEndpointNamePattern);

            conferEndpointNamePattern = prop.getProperty("VNXIVR.conferEndpointNamePattern");
            VNXLog.info2("conferEndpointNamePattern:" + conferEndpointNamePattern);

            ivrEndpointNamePattern = prop.getProperty("VNXIVR.ivrEndpointNamePattern");
            VNXLog.info2("ivrEndpointNamePattern:" + ivrEndpointNamePattern);
            
            INBOUND_CONTACT_IP = prop.getProperty("VNXIVR.INBOUND_CONTACT_IP");
            VNXLog.info2("INBOUND_CONTACT_IP:" + INBOUND_CONTACT_IP);
            
            
//            loadRuleSets();
            
            CATALINA_HOME= prop.getProperty("tomcat.home");
            VNXLog.info2("CATALINA_HOME:" + CATALINA_HOME);
            projectHome= prop.getProperty("project.home");
            VNXLog.info2("projectHome:" + projectHome);
            

            //
            if (prop.getProperty("VNXIVR.enableEditContactHeader").equals("true")) {
                enableEditContactHeader = true;
            }
            VNXLog.info2("enableEditContactHeader:" + enableEditContactHeader);

            if (prop.getProperty("VNXIVR.enableModSDP").equals("true")) {
                enableModSDP = true;
            }
            VNXLog.info2("enableModSDP:" + enableModSDP);


            httpServerBindAddress = prop.getProperty("VNXIVR.httpServerBindAddress");
            VNXLog.info2("httpServerBindAddress:" + httpServerBindAddress);

            httpServerBindPort = prop.getProperty("VNXIVR.httpServerBindPort");
            VNXLog.info2("httpServerBindPort:" + httpServerBindPort);

            recordingDir = prop.getProperty("VNXIVR.recordingDir");
            VNXLog.info2("recordingDir:" + recordingDir);
            
            digitPattern = prop.getProperty("VNXIVR.digitPattern");
            VNXLog.info2("digitPattern:" + digitPattern);
            
            digitModPattern = prop.getProperty("VNXIVR.digitModPattern");
            VNXLog.info2("digitModPattern:" + digitModPattern);
            
            
            


            defaulSipServlet = prop.getProperty("VNXIVR.defaulSipServlet");
            VNXLog.info2("defaulSipServlet:" + defaulSipServlet);
            
            
            conferMngRate = Integer.parseInt(prop.getProperty("VNXIVR.conferMngRate"));
            VNXLog.info2("conferMngRate:" + conferMngRate);
            
            conferExpiredTime = Integer.parseInt(prop.getProperty("VNXIVR.conferExpiredTime"));
            VNXLog.info2("conferExpiredTime:" + conferExpiredTime);
            initIntfs();


        } catch (Exception ex) {
            VNXLog.error2("error when loading configuration from file:" + genCfgFile + ex.getMessage());
            VNXLog.error2(ex);
        }
    }

    public static void initIntfs() {
        try {
            VNXLog.info2("initializing interface ...");
           
        } catch (Exception ex) {

            VNXLog.error2(ex);
        }
    }

    public static void loadRuleSets() throws Exception {
        try {
            VNXLog.info2("loading routing for SipServlet ...");
            Document xmlDoc = loadXMLFromFile("conf/VIVRRouting.xml");
            // Document xmlDoc=loadXMLFromString();
            Element template = xmlDoc.getDocumentElement();

            NodeList nodeLst = xmlDoc.getElementsByTagName("servlet-mapping");
            for (int s = 0; s < nodeLst.getLength(); s++) {
                Node fstNode = nodeLst.item(s);
                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element fstElmnt = (Element) fstNode;
                    NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("servlet-name");
                    Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
                    NodeList fstNm = fstNmElmnt.getChildNodes();
                    String servlet_name = ((Node) fstNm.item(0)).getNodeValue();
                    VNXLog.info2("servlet-name : " + servlet_name);
                    NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("pattern");
                    Element lstNmElmnt = (Element) lstNmElmntLst.item(0);
                    NodeList lstNm = lstNmElmnt.getChildNodes();
                    VNXLog.info2("patternxml:"
                            + xmlToString((Node) lstNm.item(1)));
                    //
                    MatchingRule matrule = MatchingRuleParser.buildRule((Element) lstNm.item(1));
                    ruleServletMap.put(servlet_name, matrule);
                }
            }
            VNXLog.info2("ruleServletMap:" + ruleServletMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static String xmlToString(Node node) {
        try {
            Source source = new DOMSource(node);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(source, result);
            return stringWriter.getBuffer().toString();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Document loadXMLFromString() throws Exception {
        try {
            VNXLog.debug2("loading rule patterns from String");
            String sipXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<servlet-selection>"
                    + "<servlet-mapping>"
                    + "<servlet-name>PromptAndCollectServlet</servlet-name>"
                    + "<pattern>"
                    + "<and>"
                    + "<equal>"
                    + "<var>request.method</var>"
                    + "<value>INVITE</value>"
                    + "</equal>"
                    + "<equal>"
                    + "<var>request.uri.user</var>"
                    + "<value>1338</value>"
                    + "</equal>"
                    + "</and>"
                    + "</pattern>"
                    + "</servlet-mapping>"
                    + "<servlet-mapping>"
                    + "<servlet-name>PromptAndCollectServletTTS</servlet-name>"
                    + "<pattern>"
                    + "<and>"
                    + "<equal>"
                    + "<var>request.method</var>"
                    + "<value>INVITE</value>"
                    + "</equal>"
                    + "<equal>"
                    + "<var>request.uri.user</var>"
                    + "<value>1010tts</value>"
                    + "</equal>"
                    + "</and>"
                    + "</pattern>"
                    + "</servlet-mapping>"
                    + "<servlet-mapping>"
                    + "<servlet-name>PromptAndRecordServlet</servlet-name>"
                    + "<pattern>"
                    + "<and>"
                    + "<equal>"
                    + "<var>request.method</var>"
                    + "<value>INVITE</value>"
                    + "</equal>"
                    + "<equal>"
                    + "<var>request.uri.user</var>"
                    + "<value>1011</value>"
                    + "</equal>"
                    + "</and>"
                    + "</pattern>"
                    + "</servlet-mapping>"
                    + "</servlet-selection>";


            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(sipXML.getBytes()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Document loadXMLFromFile(String filename) throws Exception {
        try {

            VNXLog.debug2("loading rule patterns from file:" + filename);
            if (filename == null || filename.isEmpty() == true) {
                VNXLog.error2("xml file is null ");
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new File(filename));
        } catch (Exception ex) {
            VNXLog.error2(ex);
        }
        return null;
    }

    public static void writeConsole(String str) {
        try {
            if (str == null || str.isEmpty() == true) {
                return;
            }
            if (m_IO != null) {
                m_IO.write(str + "\r\n");
            }
            VNXLog.info2(str);
        } catch (Exception ex) {
            VNXLog.error(ex);
        }
    }

    public static void help() {
        // ADD:  Add new function here
        writeConsole("");
        writeConsole("Commands:");
        writeConsole("reloadconfig");
        writeConsole("help");
        writeConsole("quit");

    }
}
