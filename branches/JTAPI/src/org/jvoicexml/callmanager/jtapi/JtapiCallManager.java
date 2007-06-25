package org.jvoicexml.callmanager.jtapi;

import java.net.URI;
import java.util.HashMap;

import javax.telephony.Address;
import javax.telephony.JtapiPeer;
import javax.telephony.JtapiPeerFactory;
import javax.telephony.JtapiPeerUnavailableException;
import javax.telephony.Provider;
import javax.telephony.ProviderUnavailableException;
import javax.telephony.ResourceUnavailableException;
import javax.telephony.Terminal;
import javax.telephony.media.MediaBindException;
import javax.telephony.media.MediaConfigException;
import javax.telephony.media.MediaProvider;

import net.sourceforge.gjtapi.media.GenericMediaService;
import net.sourceforge.gjtapi.raw.sipprovider.common.Console;

import org.jvoicexml.callmanager.CallManager;
import org.jvoicexml.demo.helloworlddemo.HelloWorldDemoTelephony;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;

/**
 * <p>Title: Call Manager</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: L2f,Inesc-id</p>
 *
 * @author
 * @version 1.0
 */
public class JtapiCallManager implements CallManager {

    //log
    protected static Console console = Console.getConsole(JtapiCallManager.class);

    //peer
    protected static String JtapiPeer =
            "net.sourceforge.gjtapi.GenericJtapiPeer";

    //provider
    private static Provider _provider = null;

    //name of the provider used ex:net.sourceforge.gjtapi.raw.sipprovider.SipProvider
    private static String _providerName = null;

    //Map of terminals associated to an Application
    HashMap<String, URI> _associateTerminalToVxml = new HashMap<String, URI>();

    //
    HashMap<String,
            JtapiCallControl> _jtapiCallControlList = new HashMap<String,
            JtapiCallControl>();

    private static JtapiCallManager _jtapiCallManager;

    /**
     *
     * @param terminalName String
     * @return JtapiCallControl
     */
    public static JtapiCallControl getTerminal(String terminalName) {

        if (_jtapiCallManager == null) {
            //launch provider
            _jtapiCallManager = new JtapiCallManager(_providerName);
            //load and register terminals
            _jtapiCallManager.start();
        }
        //get controlo and media terminal
        JtapiCallControl _callControl = _jtapiCallManager.getControlTerminal(
                terminalName);
        return _callControl;
    }


    /**
     * Provider initialization and properties for the terminals
     */
    public JtapiCallManager(String provName) {
        console.logEntry();

        _providerName = provName;
        // Get a JTAPI Peer
        JtapiPeer peer = null;
        try {
            peer = JtapiPeerFactory.getJtapiPeer(JtapiPeer);
            console.debug("1.1: Successfully loaded the JTapi Peer");
        } catch (JtapiPeerUnavailableException jpue) {
            console.debug("1.1: Failed to locate Peer with the factory");
            jpue.printStackTrace();
        }

        // initialize and load properties
        try {
            if (_providerName == null) { //default provider: SipProvider
                _providerName =
                        "net.sourceforge.gjtapi.raw.sipprovider.SipProvider";
            }
            _provider = peer.getProvider(_providerName);
            console.debug("1.2: Successfully loaded Provider");
        } catch (ProviderUnavailableException pue) {
            console.debug("1.2: Failed to load Provider");
            pue.printStackTrace();
        }
        console.logExit();
    }

    public static void main(String[] args) {
        console.logEntry();
        if (args.length < 1) {
            System.err.println(
                    "Usage: java net.sourceforge.gjtapi.raw.CallManagerIMP <Provider> ");
            //"net.sourceforge.gjtapi.raw.sipprovider.SipProvider";
            System.exit(1);
        }

        //_providerName = args[0];
        JtapiCallManager _callManager = null;
        _callManager = new JtapiCallManager(args[0]);
        _callManager.start();
        console.logExit();
    }

    /**
     * start to listening, i.e., associate a Listener and an observer to
     * each terminal of the provider
     */
    public void start() {
        console.logEntry();

        try {
            Address[] address = _provider.getAddresses();

            for (int i = 0; i < address.length; i++) {

                //address
                String addr = address[i].getName();
                console.debug("address name : " + addr);
                //terminal
                Terminal terminal = _provider.getTerminal(addr);
                console.debug("terminal name: " + terminal.getName());

                //Create a media service
                console.debug("Attempting to create a Media Service...");
                GenericMediaService ms = new GenericMediaService((MediaProvider)
                        _provider);

                //we have only one terminal per Address
                ms.bindToTerminal(null, terminal);

                JtapiCallControl callControl = new JtapiCallControl(ms);
                _jtapiCallControlList.put(terminal.getName(), callControl);

            }
        } catch (ResourceUnavailableException ex) {
            console.error("", ex);
            ex.printStackTrace();
        } catch (MediaConfigException ex) {
            console.error("", ex);
            ex.printStackTrace();
        } catch (MediaBindException ex) {
            ex.printStackTrace();
            console.error("", ex);
        } catch (javax.telephony.InvalidArgumentException ex) {
            console.error("", ex);
            ex.printStackTrace();
        }

    }

    /**
     *
     * @param terminalName String
     * @return JtapiCallControl
     */
    public JtapiCallControl getControlTerminal(String terminalName) {
        return _jtapiCallControlList.get(terminalName);
    }

    /**
     *
     * @return HelloWorldDemoTelephony
     */
    public HelloWorldDemoTelephony initAppVXML() {

        final HelloWorldDemoTelephony demo = new HelloWorldDemoTelephony();

        final VoiceXmlDocument document = demo.createHelloWorld();
        if (document == null) {
            return null;
        }

        final String xml = demo.printDocument(document);
        if (xml == null) {
            return null;
        }

        final URI uri = demo.addDocument(document);
        if (uri == null) {
            return null;
        }

        /*try {
            demo.interpretDocument(uri);
                 } catch (org.jvoicexml.event.JVoiceXMLEvent e) {
            //LOGGER.error("error processing the document", e);
            e.printStackTrace();
                 }*/
        return demo;

    }

    /**
     * Stop the provider
     */
    public void stop() {
        console.logEntry();
        /**
         * @todo may be it is necessary to stop all the listerners
         */

        console.debug("ShutingDown the provider");
        _provider.shutdown();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            console.error("", ex);
        }
        console.logExit();
    }

    /**
     * Add a given application (URI) to a terminal
     * @param application URI
     * @param terminal String
     * @return boolean
     */
    public boolean addTerminal(URI application, String terminal) {
        console.logEntry();
        _associateTerminalToVxml.put(terminal, application);
        console.logExit();
        return true;
    }

}
