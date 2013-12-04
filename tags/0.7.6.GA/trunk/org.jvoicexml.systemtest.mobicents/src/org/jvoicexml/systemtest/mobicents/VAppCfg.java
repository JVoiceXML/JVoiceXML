/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jvoicexml.systemtest.mobicents;

import com.vnxtele.util.VNXLog;
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
    public static String MGW_ADDRESS = System.getProperty(
            "jboss.bind.address", "127.0.0.1");
    public static String MGW_PORT = "2427";
    public static final String CONTACT_HEADER = "Contact";
    public static boolean initConfig = false;
    /**
     * binding address and port of Tomcat HTTP Server
     */
    public static String httpServerBindAddress = "0.0.0.0";
    public static String httpServerBindPort = "8080";
    public static String recordingDir = "file:////home/Audio/Record/";
    public static int recordingDelayTime=30;
    public static VNXLog vgenLog = null;
    /**
     * sip stack address
     */
    public static String sipStackAddr = "0.0.0.0";
    public static int sipStackPort = 5000;
    
    //signal digits from the users
//    public static String digitPattern="Signal=";
    public static String digitPattern = "digit=";
    //endpoint name for media server
    //for mms-standalone-2.1.0.BETA1
//    public static String packetRelayEndpointNamePattern="/mobicents/media/packetrelay/$";
    //for mms-standalone-2.4.0
    public static String packetRelayEndpointNamePattern = "mobicents/media/packetrelay/$";
    public static String conferEndpointNamePattern = "mobicents/media/cnf/$";
    public static String ivrEndpointNamePattern = "mobicents/media/IVR/$";
    public static boolean enableEditContactHeader = false;
    public static boolean enableModSIP200OK = false;
    /**
     * 
     */
    public static String defaulSipServlet = "VIVRGate";
    public static int dbRate = 5000;
    /**
     *db interface 
     */
    public static VDBIntf dbIntf = null;
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
        VNXLog.info("init context for VAppCfg:" + appCfg);
        appCfg.init();
        event.getServletContext().setAttribute("VAppCfg", appCfg);
        //
//        clientServlet=(BroadcastDemoServlet)event.getServletContext().getAttribute("BroadcastDemoServlet");
//        VNXLog.info("BroadcastDemoServlet:"+clientServlet);
    }

    public void initcontext() {
        vgenLog = new VNXLog(this.nInsts, this.instNames, this.logdir,
                this.logCfgFile);
        VNXLog.info("init context for VAppCfg:" + this);
        this.init();
    }

    public void contextDestroyed(ServletContextEvent event) {
        VNXLog.info("destroy context for VAppCfg:" + Integer.toHexString(hashCode()));
    }

    public VAppCfg() {
        VAPP_HOME = System.getProperty("BroadcastDemo_HOME");
        if (VAPP_HOME == null) {
            VAPP_HOME = "";
        }

        VNXLog.info("constructor a VAppCfg hashCode:" + Integer.toHexString(hashCode()));
        genCfgFile = VAPP_HOME + "conf" + File.separatorChar + "BroadcastDemo.cfg";
        logdir = VAPP_HOME + "logs";
        logCfgFile = VAPP_HOME + "conf" + File.separatorChar + "BroadcastDemoLog.cfg";
        telnetCfgFile = VAPP_HOME + "conf" + File.separatorChar + "BroadcastDemo.cfg";

    }
    public VAppCfg(String configfile) {
        VAPP_HOME = System.getenv("BroadcastDemo_HOME");
        if (VAPP_HOME == null) {
            VAPP_HOME = "./";
            VNXLog.error("error VAPP_HOME = System.getenv(\"BroadcastDemo_HOME\");");
        }

        VNXLog.info("constructor a VAppCfg hashCode:" + Integer.toHexString(hashCode()) +  " configfile:"+configfile);
        VNXLog.info("VAPP_HOME:" + VAPP_HOME);
        genCfgFile = configfile;
        logdir = VAPP_HOME + "logs";
        logCfgFile = VAPP_HOME + "conf" + File.separatorChar + "BroadcastDemoLog.cfg";
        telnetCfgFile = configfile;

    }

    public void init() {
        try {
            if (initConfig == true) {
                VNXLog.info("system is already init configurations");
                return;
            } else {
                initConfig = true;
            }
            //telnet session
            VNXLog.info("crearte VTelnetD server...:from telnetCfgFile:" + telnetCfgFile);
//            telnet = new VTelnetD();
//            telnet.init(telnetCfgFile);
//            telnet.start();
            //loading configuration from file
            prop = new Properties();
            VNXLog.info("reading configuration informations from file:" + genCfgFile);

            FileInputStream gencfgFile = new FileInputStream(genCfgFile);
            prop.load(gencfgFile);
            VNXLog.info("*******print out properties from the config file: \n" + prop);
            //
            
            
                    
            sipStackAddr = prop.getProperty("ivrgw.sipStackAddr");
            VNXLog.info("sipStackAddr:" + sipStackAddr);
            
            sipStackPort = Integer.parseInt(prop.getProperty("ivrgw.sipStackPort"));
            VNXLog.info("sipStackPort:" + sipStackPort);
            
            LOCAL_ADDRESS = prop.getProperty("ivrgw.local_callAgent_address");
            VNXLog.info("LOCAL_ADDRESS:" + LOCAL_ADDRESS);

            CA_PORT = prop.getProperty("ivrgw.local_callAgent_port");
            VNXLog.info("CA_PORT:" + CA_PORT);
            
            

            LOCAL_MGCP_PORT = prop.getProperty("ivrgw.local_mgcp_port");
            VNXLog.info("LOCAL_MGCP_PORT:" + LOCAL_MGCP_PORT);

            MGW_ADDRESS = prop.getProperty("ivrgw.peer_MGW_address");
            VNXLog.info("MGW_ADDRESS:" + MGW_ADDRESS);

            MGW_PORT = prop.getProperty("ivrgw.peer_MGW_port");
            VNXLog.info("MGW_PORT:" + MGW_PORT);

            enableBackProxy = Integer.parseInt(prop.getProperty("ivrgw.enableBackProxy"));
            VNXLog.info("enableBackProxy:" + enableBackProxy);

            backSIPProxyIP = prop.getProperty("ivrgw.backSIPProxyIP");
            VNXLog.info("backSIPProxyIP:" + backSIPProxyIP);

            backSIPProxyPort = Integer.parseInt(prop.getProperty("ivrgw.backSIPProxyPort"));
            VNXLog.info("backSIPProxyPort:" + backSIPProxyPort);


            digitPattern = prop.getProperty("ivrgw.digitPattern");
            VNXLog.info("digitPattern:" + digitPattern);

            packetRelayEndpointNamePattern = prop.getProperty("ivrgw.packetRelayEndpointNamePattern");
            VNXLog.info("packetRelayEndpointNamePattern:" + packetRelayEndpointNamePattern);

            conferEndpointNamePattern = prop.getProperty("ivrgw.conferEndpointNamePattern");
            VNXLog.info("conferEndpointNamePattern:" + conferEndpointNamePattern);

            ivrEndpointNamePattern = prop.getProperty("ivrgw.ivrEndpointNamePattern");
            VNXLog.info("ivrEndpointNamePattern:" + ivrEndpointNamePattern);
//            loadRuleSets();
            
            CATALINA_HOME= prop.getProperty("tomcat.home");
            VNXLog.info("CATALINA_HOME:" + CATALINA_HOME);
            projectHome= prop.getProperty("project.home");
            VNXLog.info("projectHome:" + projectHome);
            

            //
            if (prop.getProperty("ivrgw.enableEditContactHeader").equals("true")) {
                enableEditContactHeader = true;
            }
            VNXLog.info("enableEditContactHeader:" + enableEditContactHeader);

            if (prop.getProperty("ivrgw.enableModSIP200OK").equals("true")) {
                enableModSIP200OK = true;
            }
            VNXLog.info("enableModSIP200OK:" + enableModSIP200OK);


            httpServerBindAddress = prop.getProperty("ivrgw.httpServerBindAddress");
            VNXLog.info("httpServerBindAddress:" + httpServerBindAddress);

            httpServerBindPort = prop.getProperty("ivrgw.httpServerBindPort");
            VNXLog.info("httpServerBindPort:" + httpServerBindPort);

            recordingDir = prop.getProperty("ivrgw.recordingDir");
            VNXLog.info("recordingDir:" + recordingDir);
            
            recordingDelayTime = 1000*Integer.parseInt(prop.getProperty("ivrgw.recordingDelayTime"));
            VNXLog.info("recordingDelayTime:" + recordingDelayTime);
            


            defaulSipServlet = prop.getProperty("ivrgw.defaulSipServlet");
            VNXLog.info("defaulSipServlet:" + defaulSipServlet);
            initDB();


        } catch (Exception ex) {
            VNXLog.error("error when loading configuration from file:" + genCfgFile + ex.getMessage());
            VNXLog.error(ex);
        }
    }

    public static void initDB() {
        try {
//            VNXLog.info("initializing db interface ...");
//            dbIntf = new VDBIntf();
//            dbIntf.init();
//            dbIntf.start();
        } catch (Exception ex) {

            VNXLog.error(ex);
        }
    }

    public static void loadRuleSets() throws Exception {
        try {
            VNXLog.info("loading routing for SipServlet ...");
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
                    VNXLog.info("servlet-name : " + servlet_name);
                    NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("pattern");
                    Element lstNmElmnt = (Element) lstNmElmntLst.item(0);
                    NodeList lstNm = lstNmElmnt.getChildNodes();
                    VNXLog.info("patternxml:"
                            + xmlToString((Node) lstNm.item(1)));
                    //
                    MatchingRule matrule = MatchingRuleParser.buildRule((Element) lstNm.item(1));
                    ruleServletMap.put(servlet_name, matrule);
                }
            }
            VNXLog.info("ruleServletMap:" + ruleServletMap);
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
            VNXLog.debug("loading rule patterns from String");
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

            VNXLog.debug("loading rule patterns from file:" + filename);
            if (filename == null || filename.isEmpty() == true) {
                VNXLog.error("xml file is null ");
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new File(filename));
        } catch (Exception ex) {
            VNXLog.error(ex);
        }
        return null;
    }

    

   
}
