/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jvoicexml.implementation.mobicents;

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

import javax.servlet.sip.SipServlet;
import org.apache.log4j.Logger;
import org.util.ExLog;

/**
 *
 * @author Shadowman
 */
public class VAppCfg implements ServletContextListener {
        private static final Logger LOGGER = Logger.getLogger(VAppCfg.class);
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
      
        LOGGER.info("init context for VAppCfg:" + appCfg);
        appCfg.init();
        event.getServletContext().setAttribute("VAppCfg", appCfg);
    }

    public void initcontext() {
        VAppCfg appCfg = getInstance();
        LOGGER.info("init context for VAppCfg:" + appCfg);
        appCfg.init();
    }

    public void contextDestroyed(ServletContextEvent event) {
        LOGGER.info("destroy context for VAppCfg:" + Integer.toHexString(hashCode()));
    }

    public VAppCfg() {
        VAPP_HOME = System.getProperty("VNXIVR_HOME");
        if (VAPP_HOME == null) {
            VAPP_HOME = "";
        }

        LOGGER.info("constructor a VAppCfg hashCode:" + Integer.toHexString(hashCode()));
        genCfgFile = VAPP_HOME + "conf" + File.separatorChar + "VNXIVR.cfg";
        logdir = VAPP_HOME + "logs";
        logCfgFile = VAPP_HOME + "conf" + File.separatorChar + "VNXIVRLog.cfg";
        telnetCfgFile = VAPP_HOME + "conf" + File.separatorChar + "VNXIVR.cfg";

    }

    public void init() {
        try {
            if (initConfig == true) {
                LOGGER.info("system is already init configurations");
                return;
            } else {
                initConfig = true;
            }
            //telnet session
//            LOGGER.info("crearte VTelnetD server...:from telnetCfgFile:" + telnetCfgFile);
//            telnet = new VTelnetD();
//            telnet.init(telnetCfgFile);
//            telnet.start();
            //loading configuration from file
            prop = new Properties();
            LOGGER.info("reading configuration informations from file:" + genCfgFile);

            FileInputStream gencfgFile = new FileInputStream(genCfgFile);
            prop.load(gencfgFile);
            LOGGER.info("*******print out properties from the config file: \n" + prop);
            //
            
            
                    
            sipStackAddr = prop.getProperty("VNXIVR.sipStackAddr");
            LOGGER.info("sipStackAddr:" + sipStackAddr);
            
            sipStackPort = prop.getProperty("VNXIVR.sipStackPort");
            LOGGER.info("sipStackPort:" + sipStackPort);
            
            LOCAL_ADDRESS = prop.getProperty("VNXIVR.local_callAgent_address");
            LOGGER.info("LOCAL_ADDRESS:" + LOCAL_ADDRESS);

            CA_PORT = prop.getProperty("VNXIVR.local_callAgent_port");
            LOGGER.info("CA_PORT:" + CA_PORT);
            
            

            LOCAL_MGCP_PORT = prop.getProperty("VNXIVR.local_mgcp_port");
            LOGGER.info("LOCAL_MGCP_PORT:" + LOCAL_MGCP_PORT);

            INBOUND_MGW_ADDRESS = prop.getProperty("VNXIVR.INBOUND_MGW_ADDRESS");
            LOGGER.info("INBOUND_MGW_ADDRESS:" + INBOUND_MGW_ADDRESS);
            OUTBOUND_MGW_ADDRESS = prop.getProperty("VNXIVR.OUTBOUND_MGW_ADDRESS");
            LOGGER.info("OUTBOUND_MGW_ADDRESS:" + OUTBOUND_MGW_ADDRESS);

            MGW_PORT = prop.getProperty("VNXIVR.MGW_PORT");
            LOGGER.info("MGW_PORT:" + MGW_PORT);

            enableBackProxy = Integer.parseInt(prop.getProperty("VNXIVR.enableBackProxy"));
            LOGGER.info("enableBackProxy:" + enableBackProxy);

            backSIPProxyIP = prop.getProperty("VNXIVR.backSIPProxyIP");
            LOGGER.info("backSIPProxyIP:" + backSIPProxyIP);

            backSIPProxyPort = Integer.parseInt(prop.getProperty("VNXIVR.backSIPProxyPort"));
            LOGGER.info("backSIPProxyPort:" + backSIPProxyPort);


            digitPattern = prop.getProperty("VNXIVR.digitPattern");
            LOGGER.info("digitPattern:" + digitPattern);

            packetRelayEndpointNamePattern = prop.getProperty("VNXIVR.packetRelayEndpointNamePattern");
            LOGGER.info("packetRelayEndpointNamePattern:" + packetRelayEndpointNamePattern);

            conferEndpointNamePattern = prop.getProperty("VNXIVR.conferEndpointNamePattern");
            LOGGER.info("conferEndpointNamePattern:" + conferEndpointNamePattern);

            ivrEndpointNamePattern = prop.getProperty("VNXIVR.ivrEndpointNamePattern");
            LOGGER.info("ivrEndpointNamePattern:" + ivrEndpointNamePattern);
            
            INBOUND_CONTACT_IP = prop.getProperty("VNXIVR.INBOUND_CONTACT_IP");
            LOGGER.info("INBOUND_CONTACT_IP:" + INBOUND_CONTACT_IP);
            
            
//            loadRuleSets();
            
            CATALINA_HOME= prop.getProperty("tomcat.home");
            LOGGER.info("CATALINA_HOME:" + CATALINA_HOME);
            projectHome= prop.getProperty("project.home");
            LOGGER.info("projectHome:" + projectHome);
            

            //
            if (prop.getProperty("VNXIVR.enableEditContactHeader").equals("true")) {
                enableEditContactHeader = true;
            }
            LOGGER.info("enableEditContactHeader:" + enableEditContactHeader);

            if (prop.getProperty("VNXIVR.enableModSDP").equals("true")) {
                enableModSDP = true;
            }
            LOGGER.info("enableModSDP:" + enableModSDP);


            httpServerBindAddress = prop.getProperty("VNXIVR.httpServerBindAddress");
            LOGGER.info("httpServerBindAddress:" + httpServerBindAddress);

            httpServerBindPort = prop.getProperty("VNXIVR.httpServerBindPort");
            LOGGER.info("httpServerBindPort:" + httpServerBindPort);

            recordingDir = prop.getProperty("VNXIVR.recordingDir");
            LOGGER.info("recordingDir:" + recordingDir);
            
            digitPattern = prop.getProperty("VNXIVR.digitPattern");
            LOGGER.info("digitPattern:" + digitPattern);
            
            digitModPattern = prop.getProperty("VNXIVR.digitModPattern");
            LOGGER.info("digitModPattern:" + digitModPattern);
            
            
            


            defaulSipServlet = prop.getProperty("VNXIVR.defaulSipServlet");
            LOGGER.info("defaulSipServlet:" + defaulSipServlet);
            
            
            conferMngRate = Integer.parseInt(prop.getProperty("VNXIVR.conferMngRate"));
            LOGGER.info("conferMngRate:" + conferMngRate);
            
            conferExpiredTime = Integer.parseInt(prop.getProperty("VNXIVR.conferExpiredTime"));
            LOGGER.info("conferExpiredTime:" + conferExpiredTime);
            initIntfs();


        } catch (Exception ex) {
            LOGGER.error("error when loading configuration from file:" + genCfgFile + ex.getMessage());
            ExLog.exception(LOGGER, ex);
        }
    }

    public static void initIntfs() {
        try {
            LOGGER.info("initializing interface ...");
           
        } catch (Exception ex) {

            ExLog.exception(LOGGER, ex);
        }
    }

    public static void loadRuleSets() throws Exception {
        try {
            LOGGER.info("loading routing for SipServlet ...");
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
                    LOGGER.info("servlet-name : " + servlet_name);
                    NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("pattern");
                    Element lstNmElmnt = (Element) lstNmElmntLst.item(0);
                    NodeList lstNm = lstNmElmnt.getChildNodes();
                    LOGGER.info("patternxml:"
                            + xmlToString((Node) lstNm.item(1)));
                    //
                    MatchingRule matrule = MatchingRuleParser.buildRule((Element) lstNm.item(1));
                    ruleServletMap.put(servlet_name, matrule);
                }
            }
            LOGGER.info("ruleServletMap:" + ruleServletMap);
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
            LOGGER.debug("loading rule patterns from String");
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

            LOGGER.debug("loading rule patterns from file:" + filename);
            if (filename == null || filename.isEmpty() == true) {
                LOGGER.error("xml file is null ");
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new File(filename));
        } catch (Exception ex) {
            ExLog.exception(LOGGER, ex);
        }
        return null;
    }

    public static void writeConsole(String str) {
        try {
            if (str == null || str.isEmpty() == true) {
                return;
            }
//            if (m_IO != null) {
//                m_IO.write(str + "\r\n");
//            }
            LOGGER.info(str);
        } catch (Exception ex) {
            ExLog.exception(LOGGER, ex);
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
