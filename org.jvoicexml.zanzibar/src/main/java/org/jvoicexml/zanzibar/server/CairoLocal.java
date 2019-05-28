package org.jvoicexml.zanzibar.server;

import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.sip.SipException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.speechforge.cairo.server.config.CairoConfig;
import org.speechforge.cairo.server.config.ReceiverConfig;
import org.speechforge.cairo.server.config.TransmitterConfig;
import org.speechforge.cairo.server.resource.ReceiverResource;
import org.speechforge.cairo.server.resource.Resource;
import org.speechforge.cairo.server.resource.ResourceRegistry;
import org.speechforge.cairo.server.resource.ResourceRegistryImpl;
import org.speechforge.cairo.server.resource.ResourceServerImpl;
import org.speechforge.cairo.server.resource.TransmitterResource;
import org.speechforge.cairo.util.CairoUtil;


/**
 * Starts up the cairo server processes in the local JVM.  Not really ment for use in a high volume
 * deployment.  Useful for testing and simple demos.
 *
 * @author Spencer Lord {@literal <}<a href="mailto:ngodfredsen@users.sourceforge.net">salord@users.sourceforge.net</a>{@literal >}
 */
public class CairoLocal {
    
    private static Logger _logger = Logger.getLogger(CairoLocal.class);
    
    ResourceServerImpl rs;
    ResourceRegistryImpl rr;
    ReceiverResource rimpl;
    TransmitterResource timpl;
    
    //startup parmeters for the receiver
    private String cairoConfig;// = "file:../config/cairo-config.xml";
    private String receiverResourceName;// = "receiver1";
    
    
    //Startup paramters for the 
    private String transmitterResourceName;// = "transmitter1";
    
    
    //startup parameters for resource manager
    private int sipPort;
    private String sipTransport ;
    private String publicAddress;

	private String zanzibarAddress = null;


    //TODO: When Spring is used to configure Cairo, this class can be removed.  Can just configure the 3 servers to startup individually.
    
    
    public String getZanzibarAddress() {
		return this.zanzibarAddress;
	}

	public void setZanzibarAddress(String zanzibarAddress) {
		this.zanzibarAddress = zanzibarAddress;
	}

	public void startup() {
			try {
				if (zanzibarAddress == null)
					zanzibarAddress = CairoUtil.getLocalHost().getHostName();
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        try {
            startRM();
            startReceiver();
            startTransmitter();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
    public void shutdown() {
        
    }
    
    public void startRM() throws RemoteException, SipException {
    
        rr = new ResourceRegistryImpl();
        rs = new ResourceServerImpl(rr,  sipPort,  sipTransport, zanzibarAddress, publicAddress);
    
        Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        registry.rebind(ResourceRegistry.NAME, rr);
    
        _logger.info("Server and registry bound and waiting...");
    }
    
    
    public void startReceiver() throws NotBoundException, IOException, InstantiationException, ConfigurationException {
       
        URL configURL = CairoUtil.argToURL(cairoConfig);

        CairoConfig config = new CairoConfig(configURL);
        ReceiverConfig resourceConfig = config.getReceiverConfig(receiverResourceName);

        StringBuilder rmiUrl = new StringBuilder("rmi://");
        rmiUrl.append(zanzibarAddress);        
        rmiUrl.append('/').append(ResourceRegistry.NAME);

        _logger.info("looking up: " + rmiUrl);
        ResourceRegistry resourceRegistry = (ResourceRegistry) Naming.lookup(rmiUrl.toString());

         rimpl = new ReceiverResource(resourceConfig);

        _logger.info("binding receiver resource...");
        resourceRegistry.register(rimpl, Resource.Type.RECEIVER);

        _logger.info("Resource bound and waiting...");
    }
    
    
    public void startTransmitter() throws NotBoundException, IOException, InstantiationException, ConfigurationException {

        URL configURL = CairoUtil.argToURL(cairoConfig);
        
        CairoConfig config = new CairoConfig(configURL);
        TransmitterConfig resourceConfig = config.getTransmitterConfig(transmitterResourceName);

        StringBuilder rmiUrl = new StringBuilder("rmi://");
        rmiUrl.append(zanzibarAddress);
        rmiUrl.append('/').append(ResourceRegistry.NAME);

        _logger.info("looking up: " + rmiUrl);
        ResourceRegistry resourceRegistry = (ResourceRegistry) Naming.lookup(rmiUrl.toString());

         timpl = new TransmitterResource(resourceConfig);

        _logger.info("binding transmitter resource...");
        resourceRegistry.register(timpl, Resource.Type.TRANSMITTER);

        _logger.info("Resource bound and waiting...");

    }

    /**
     * @return the cairoConfig
     */
    public String getCairoConfig() {
        return cairoConfig;
    }

    /**
     * @param cairoConfig the cairoConfig to set
     */
    public void setCairoConfig(String cairoConfig) {
        this.cairoConfig = cairoConfig;
    }

    /**
     * @return the receiverResourceName
     */
    public String getReceiverResourceName() {
        return receiverResourceName;
    }

    /**
     * @param receiverResourceName the receiverResourceName to set
     */
    public void setReceiverResourceName(String receiverResourceName) {
        this.receiverResourceName = receiverResourceName;
    }

    /**
     * @return the transmitterResourceName
     */
    public String getTransmitterResourceName() {
        return transmitterResourceName;
    }

    /**
     * @param transmitterResourceName the transmitterResourceName to set
     */
    public void setTransmitterResourceName(String transmitterResourceName) {
        this.transmitterResourceName = transmitterResourceName;
    }

	/**
     * @return the publicAddress
     */
    public String getPublicAddress() {
    	return publicAddress;
    }

	/**
     * @param publicAddress the publicAddress to set
     */
    public void setPublicAddress(String publicAddress) {
    	this.publicAddress = publicAddress;
    }

	/**
     * @return the sipPort
     */
    public int getSipPort() {
    	return sipPort;
    }

	/**
     * @param sipPort the sipPort to set
     */
    public void setSipPort(int sipPort) {
    	this.sipPort = sipPort;
    }

	/**
     * @return the sipTransport
     */
    public String getSipTransport() {
    	return sipTransport;
    }

	/**
     * @param sipTransport the sipTransport to set
     */
    public void setSipTransport(String sipTransport) {
    	this.sipTransport = sipTransport;
    }
}
