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
import org.jvoicexml.implementation.jtapi.JtapiCallControl;

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
public final class JtapiCallManager implements CallManager {

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

    /**
     * Provider initialization and properties for the terminals
     */
    public JtapiCallManager() {
        console.logEntry();

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
                    "Usage: java net.sourceforge.gjtapi.test.CallManagerIMP <Provider> ");
            //"net.sourceforge.gjtapi.raw.sipprovider.SipProvider";
            System.exit(1);
        }

        _providerName = args[0];
        JtapiCallManager _callManager = null;
        _callManager = new JtapiCallManager();
        _callManager.start();
        console.logExit();
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public boolean addTerminal(final String terminal, final URI application) {
        console.logEntry();
        _associateTerminalToVxml.put(terminal, application);
        console.logExit();
        return true;
    }

}
