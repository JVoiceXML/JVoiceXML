/**
 * 
 */
package org.jvoicexml.demo.jtapidemo;

import javax.telephony.Address;
import javax.telephony.Call;
import javax.telephony.Connection;
import javax.telephony.InvalidArgumentException;
import javax.telephony.InvalidPartyException;
import javax.telephony.InvalidStateException;
import javax.telephony.JtapiPeer;
import javax.telephony.JtapiPeerFactory;
import javax.telephony.JtapiPeerUnavailableException;
import javax.telephony.MethodNotSupportedException;
import javax.telephony.PrivilegeViolationException;
import javax.telephony.Provider;
import javax.telephony.ResourceUnavailableException;
import javax.telephony.Terminal;
import javax.telephony.media.MediaConfigException;
import javax.telephony.media.MediaException;
import javax.telephony.media.MediaProvider;

import net.sourceforge.gjtapi.GenericJtapiPeer;
import net.sourceforge.gjtapi.media.GenericMediaService;

import org.apache.log4j.Logger;
import org.jvoicexml.callmanager.jtapi.JtapiCallManager;
import org.jvoicexml.event.error.NoresourceError;

/**
 * @author piri
 * 
 */
public class JtapiDemo {
    private static final Logger LOGGER = Logger
            .getLogger(JtapiCallManager.class);

    /** Provider. */
    private Provider provider = null;

    /**
     * Gets the provider.
     * 
     * @return the provider to use.
     * @throws JtapiPeerUnavailableException 
     * @exception NoresourceError
     *                    Error creating the provider.
     */
    private Provider getProvider() throws JtapiPeerUnavailableException {
        if (provider != null) {
            return provider;
        }

        // Get a JTAPI Peer
        JtapiPeer peer = JtapiPeerFactory.getJtapiPeer(GenericJtapiPeer.class
                .getCanonicalName());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("successfully loaded the jtapi peer");
        }
        final String providerName =
            "net.sourceforge.gjtapi.raw.sipprovider.SipProvider";
        provider = peer.getProvider(providerName);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("successfully loaded provider '" + providerName
                    + "'");
        }

        return provider;
    }

    private Terminal getTerminal(final Provider prov, final Address address) throws InvalidArgumentException, MediaException, MediaConfigException {
        final String addr = address.getName();
        final Terminal terminal = prov.getTerminal(addr);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating terminal '" + terminal.getName() + "'...");
        }

        // Create a media service
        final GenericMediaService ms = new GenericMediaService(
                (MediaProvider) provider);

        // we have only one terminal per Address
        ms.bindToTerminal(null, terminal);
        return terminal;
    }

    /**
     * @param args command line parameters
     */
    public static void main(final String[] args) {
        final JtapiDemo demo = new JtapiDemo();
        try {
            final Provider provider = demo.getProvider();
            final Address address =
                provider.getAddress("sip:jvxmlclient@192.168.67.138");
            final Terminal terminal = demo.getTerminal(provider, address);
            final Call call = provider.createCall();
            Connection[] connections =
                call.connect(terminal, address, "sip:jvoicexml@127.0.0.1:5064");
        } catch (JtapiPeerUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ResourceUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MediaConfigException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MediaException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (PrivilegeViolationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MethodNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidPartyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
