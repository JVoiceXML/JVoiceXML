/*
 * Zanzibar - Open source speech application server.
 *
 * Copyright (C) 2008-2009 Spencer Lord 
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Contact: salord@users.sourceforge.net
 *
 */
package org.jvoicexml.zanzibar.sip;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.sdp.MediaDescription;
import javax.sdp.SdpException;
import javax.sdp.SdpParseException;
import javax.sip.ObjectInUseException;
import javax.sip.SipException;
import javax.sip.TimeoutEvent;

import org.apache.log4j.Logger;
import org.jvoicexml.zanzibar.speechlet.SessionProcessor;
import org.jvoicexml.zanzibar.speechlet.SpeechletContext;
import org.jvoicexml.zanzibar.speechlet.SpeechletContextImpl;
import org.jvoicexml.zanzibar.speechlet.SpeechletService;
import org.mrcp4j.MrcpResourceType;
import org.mrcp4j.client.MrcpChannel;
import org.mrcp4j.client.MrcpFactory;
import org.mrcp4j.client.MrcpProvider;
import org.mrcp4j.message.header.IllegalValueException;
import org.speechforge.cairo.sip.ResourceUnavailableException;
import org.speechforge.cairo.sip.SdpMessage;
import org.speechforge.cairo.sip.SessionListener;
import org.speechforge.cairo.sip.SipAgent;
import org.speechforge.cairo.sip.SipSession;

/**
 * SipServer is the sip agent for the speech client.  It implements SessionListner, so it
 * is notified with Significant SIP events.  It also provides methods to initiate SIP 
 * requets (i.e. invite and bye).
 * 
 * Before using this agent, the following properties need to be set either
 * by calling setters and then startup()(perhaps with Spring) 
 * OR use the 4 parameter constructor and the setDaultCairoServer method
 *    mySipAddress     - the sip address of the agent
 *    stackName        - a name for the agents sip stack
 *    port             - the port used by this sip agent
 *    transport        - transport too use (udp or tcp)
 *    cairoSipHostName - the host name of the speech servers sip agent (RM in cairo)
 *    cairoSipPort     - the port used by the speech servers sip agent
 *    cairoSipAddress  - the sip address of the speech server
 * 
 * @author Spencer Lord {@literal <}<a href="mailto:salord@users.sourceforge.net">salord@users.sourceforge.net</a>{@literal >}
 */
public class SipServer implements SessionListener {
        private static Logger _logger = Logger.getLogger(SipServer.class);
        
        // properties need to be set either
        //   - call setters and then the no arg constructor then startup()(perhaps with SPring) OR
        //   - use the 4 parameter constructor and the setDaultCairoServer method
        private String mySipAddress;
        private String stackName;
        private int port;
        private String transport;
        private String cairoSipHostName;
        private int cairoSipPort;
        private String cairoSipAddress;
        
        private String mrcpClientHost;
        
        private SipAgent _sipAgent;
        
        boolean responded = false;
        SdpMessage _response;
        SipSession _session;

        //private int _retryCount = 0;
        //private static final int  MAXRETRIES = 3;
        
        private SpeechletService dialogService;

        private SdpMessage _message;

        Map<String, SessionPair> waitingList;
         
        public SipServer() {
            super();
        }

        public SipServer(String mySipAddress, String stackName, int port, String transport) throws SipException {

            this.mySipAddress = mySipAddress;
            this.stackName = stackName;
            this.port = port;
            this.transport = transport;
            
            
            // Construct a SIP agent to be used to send a SIP Invitation to the ciaro server
            _sipAgent =new SipAgent(this, mySipAddress, stackName, port, transport);
            waitingList = new HashMap<String, SessionPair>();
        }
        
        
        public void startup() throws SipException {
            _sipAgent = new SipAgent(this, mySipAddress, stackName, port, transport);
            waitingList = new HashMap<String, SessionPair>();
            
            try {
                InetAddress addr = InetAddress.getLocalHost();
                mrcpClientHost = addr.getHostAddress();
                _logger.info("***: "+mrcpClientHost);
                //host = addr.getCanonicalHostName();
            } catch (UnknownHostException e) {
            	mrcpClientHost = "127.0.0.1";
                //host = "localhost";
                _logger.debug(e, e);
                _logger.warn("using localhost for mrcp client host name in sdp messages");
            }
            
        }
        
        
        public void setDefaultCairoServer(String cairoSipAddress, String cairoSipHostName, int cairoSipPort) {
            this.cairoSipAddress = cairoSipAddress;
            this.cairoSipHostName = cairoSipHostName;
            this.cairoSipPort = cairoSipPort;
            
        }
        

        public synchronized SdpMessage sendInviteWithoutProxy(String to, SdpMessage message, String peerAddress, int peerPort) throws SipException {
            //_retryCount = 0;
 
            //save the parameters in case a timeout occurs in which case a retry is needed.
            //_to = to;
            //_message = message;
            //_peerAddress = peerAddress;
            //_peerPort = peerPort;
            
            // Send the sip invitation
            SipSession session = _sipAgent.sendInviteWithoutProxy(to, message, peerAddress, peerPort);

            responded = false;
            //synchronized (this) {
                while (!responded) {
                    responded = false;
                   try {
                       this.wait(1000);
                   } catch (InterruptedException e) {
                       // TODO Auto-generated catch block
                       e.printStackTrace();
                   }
                }
            //}
            return _response;
        }
        
        public void shutdown() throws ObjectInUseException {
            _sipAgent.dispose();
        }
        

        public synchronized SdpMessage processInviteResponse(boolean ok, SdpMessage response,SipSession session) {
            _logger.debug("Gotta invite response, ok is: "+ok);
            SdpMessage pbxResponse = null;
            if (ok) {
                //System.out.println(":::: "+session.getId()+" :::::::");
                SessionPair pair = waitingList.get(session.getCtx().toString());
                waitingList.remove(session.getCtx().toString());

                if (pair != null) {
                    // Get the MRCP media channels (need the port number and the channelID that are sent
                    // back from the server in the response in order to setup the MRCP channel)
                    String remoteHostName = null;
                    InetAddress remoteHostAdress = null;
                    try {
                        remoteHostName = response.getSessionDescription().getConnection().getAddress();
                        remoteHostAdress = InetAddress.getByName(remoteHostName);

                        List <MediaDescription> xmitterChans = response.getMrcpTransmitterChannels();
                        int xmitterPort = xmitterChans.get(0).getMedia().getMediaPort();
                        String xmitterChannelId = xmitterChans.get(0).getAttribute(SdpMessage.SDP_CHANNEL_ATTR_NAME);

                        List <MediaDescription> receiverChans = response.getMrcpReceiverChannels();
                        MediaDescription controlChan = receiverChans.get(0);
                        int receiverPort = controlChan.getMedia().getMediaPort();
                        String receiverChannelId = receiverChans.get(0).getAttribute(SdpMessage.SDP_CHANNEL_ATTR_NAME);

                        List <MediaDescription> rtpChans = response.getAudioChansForThisControlChan(controlChan);
                        int remoteRtpPort = -1;
                        Vector supportedFormats = null;
                        if (rtpChans.size() > 0) {
                            //TODO: What if there is more than 1 media channels?
                            //TODO: check if there is an override for the host attribute in the m block
                            //InetAddress remoteHost = InetAddress.getByName(rtpmd.get(1).getAttribute();
                            remoteRtpPort =  rtpChans.get(0).getMedia().getMediaPort();
                            //rtpmd.get(1).getMedia().setMediaPort(localPort);
                            supportedFormats = rtpChans.get(0).getMedia().getMediaFormats(true);    
                        } else {
                            _logger.warn("No Media channel specified in the invite request");
                            //TODO:  handle no media channel in the response corresponding tp the mrcp channel (sip/sdp error)
                        } 

                        

                        //Construct the MRCP Channels
                        String protocol = MrcpProvider.PROTOCOL_TCP_MRCPv2;
                        MrcpFactory factory = MrcpFactory.newInstance();
                        MrcpProvider provider = factory.createProvider();
                        
                        _logger.debug("New Xmitter chan: "+xmitterChannelId+" "+remoteHostAdress+" "+xmitterPort+" "+ protocol);
                        _logger.debug("New Receiver chan: "+receiverChannelId+" "+remoteHostAdress+" "+receiverPort+" "+protocol);
                        
                        MrcpChannel ttsChannel = provider.createChannel(xmitterChannelId, remoteHostAdress, xmitterPort, protocol);
                        MrcpChannel recogChannel = provider.createChannel(receiverChannelId, remoteHostAdress, receiverPort, protocol);
                        session.setTtsChannel(ttsChannel);
                        session.setRecogChannel(recogChannel);

                        pbxResponse = constructInviteResponseToPbx(remoteRtpPort,remoteHostName,supportedFormats);
                        
                       _sipAgent.sendResponse(pair.getExternal(), pbxResponse);
                       
                       //setup the context (for speechlet to communicate back to container and access to container services)
                       SpeechletContext c = new SpeechletContextImpl();
                       
                       //The context needs a reference to the conatiner
                       ((SpeechletContextImpl) c).setContainer(dialogService);
                       
                       //The context needs both the internal and external sessions
                       ((SpeechletContext) c).setExternalSession(pair.getExternal()); //**** This is the original
                       ((SpeechletContext) c).setInternalSession(pair.getInternal());
                                              
                       
                        //create the actual speechlet (running in a thread within the session processor)
                        SessionProcessor d = dialogService.startNewDialog(c);
                        
                        //the sessionprocessor needs a referenece to the context
                        d.setContext(c);
                        
                        //the context also needs a reference to the speechlet
                        ((SpeechletContextImpl) c).setSpeechlet(d);
            
                        //System.out.println(">>>>Here is the invite response:");
                        //System.out.println(pbxResponse.getSessionDescription().toString());

                    } catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (SdpParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalValueException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (SdpException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    // TODO:  Need to keep track of the "forwarded" Session.  the one that wa created to get the resourses for MRCPv2.  The original
                    // Session came from the pbx
                    //session.setForward(forward);

                } else {
                    _logger.info("Could not find corresponding external request in waiting list");
                }
            } else {
                _logger.info("Invite Response not ok");
            }

            return pbxResponse;
        }
                        

        public synchronized void processTimeout(TimeoutEvent event) {
            _logger.debug("Timeout occurred");
           // _retryCount = _retryCount + 1;
           // _logger.info("Timeout # "+ _retryCount+" occured for SIP invite request.");
           // if (_retryCount < MAXRETRIES) {
           //     // Re-Send the sip invitation
           //     try {
           //         SipSession session = _sipAgent.sendInviteWithoutProxy(_to, _message, _peerAddress, _peerPort);
           //     } catch (SipException e) {
           //         // TODO Auto-generated catch block
           //         e.printStackTrace();
           //     }
           // } else {   
               _response = null;
               responded = true;
               this.notify();
           // }
        }
   


        public void processByeRequest(SipSession session) throws RemoteException, InterruptedException {
            //TODO:  This is certainly not corrrect.  There are no resources in the proxy
            //       this is a cut and paste error.  Should  send bye on to cairo so that it can clean up 
            //       - Also need to check take a careful look at what happens to the application here in the proxy when a bye is received
            //         and the special case of when the call was forwarded on -- should it be able to return to the voice app?
            //for (Resource r : session.getResources()) {
             //   r.bye(session.getId());
            //}
            _logger.info("Got a bye request");
            
            try {
                dialogService.StopDialog(session);
            } catch (SipException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public SdpMessage processInviteRequest(SdpMessage request, SipSession session) throws SdpException, ResourceUnavailableException, RemoteException {
            
            //get the rtp channels from the invitation
            Vector pbxFormats = null;
            int pbxRtpPort = 0;
            String pbxHost = request.getSessionDescription().getConnection().getAddress();
            String pbxSessionName = request.getSessionDescription().getSessionName().getValue();
            SdpMessage pbxResponse = null;
            SipSession internalSession = null;
            
            _logger.info("Got an invite request");
            try {
                for (MediaDescription md : request.getRtpChannels()) {
                    pbxRtpPort = md.getMedia().getMediaPort();
                    pbxFormats = md.getMedia().getMediaFormats(true);
                    //System.out.println("Individual Media connection address: "+ md.getConnection().getAddress());
                }
            } catch (SdpException e) {
                _logger.debug(e, e);
                throw e;
            }

            //construct the invite request and send it to the cairo resource server to get the resources for the session
            //TODO: check which resource needed in the original invite (from pbx).  the construct method below blindly gets a tts and recog resoruce
            SdpMessage message = null;
            SdpMessage inviteResponse = null;
            try {
                message = constructInviteRequestToCairo(pbxRtpPort,pbxHost,pbxSessionName,pbxFormats);
                internalSession = _sipAgent.sendInviteWithoutProxy(cairoSipAddress, message, cairoSipHostName, cairoSipPort);
            } catch (UnknownHostException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (SipException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            //these sessions are linked thru a stateful proxy.  
            //the forward attribute is not good enough (one side is the backwrds lookup)
            //TODO: Session rediesign.  It has become a bucket of things that are sometmes needed in the client and sometimes in teh server
            // and the proxy relationship is messy with the single reference "forward"
            session.setForward(internalSession);
            internalSession.setForward(session);
            SessionPair sessionPair = new SessionPair(internalSession,session);
            //System.out.println(":::: ADDING  Internal "+internalSession.getId()+" :::::::");
            //System.out.println(":::: AND THE External "+session.getId()+" :::::::");
            waitingList.put(internalSession.getCtx().toString(),sessionPair);
            return null;
        }
            
   
        
        private  SdpMessage constructInviteRequestToCairo(int pbxRtpPort,String pbxHost,String pbxSessionName,Vector pbxFormats) throws UnknownHostException, SdpException {
            SdpMessage sdpMessage = SdpMessage.createNewSdpSessionMessage(mySipAddress, mrcpClientHost, pbxSessionName);
            MediaDescription rtpChannel = SdpMessage.createRtpChannelRequest(pbxRtpPort,pbxFormats,pbxHost);
            //rtpChannel.getMedia().setMediaFormats(pbxFormats);
            MediaDescription synthControlChannel = SdpMessage.createMrcpChannelRequest(MrcpResourceType.SPEECHSYNTH);
            MediaDescription recogControlChannel = SdpMessage.createMrcpChannelRequest(MrcpResourceType.SPEECHRECOG);
            Vector v = new Vector();
            v.add(synthControlChannel);
            v.add(recogControlChannel);
            v.add(rtpChannel);
            sdpMessage.getSessionDescription().setMediaDescriptions(v);
            return sdpMessage;
        }
        
        private  SdpMessage constructInviteResponseToPbx(int cairoRtpPort, String cairoHost, Vector formats) throws UnknownHostException, SdpException {
            SdpMessage sdpMessage = SdpMessage.createNewSdpSessionMessage(mySipAddress, cairoHost, "The session Name");
            MediaDescription rtpChannel = SdpMessage.createRtpChannelRequest(cairoRtpPort,formats);
            Vector v = new Vector();
            //rtpChannel.setAttribute("ptime", "60");
            v.add(rtpChannel);
            sdpMessage.getSessionDescription().setMediaDescriptions(v);
            return sdpMessage;
        }

        /**
         * @return the cairoSipAddress
         */
        public String getCairoSipAddress() {
            return cairoSipAddress;
        }

        /**
         * @param cairoSipAddress the cairoSipAddress to set
         */
        public void setCairoSipAddress(String cairoSipAddress) {
            this.cairoSipAddress = cairoSipAddress;
        }

        /**
         * @return the cairoSipHostName
         */
        public String getCairoSipHostName() {
            return cairoSipHostName;
        }

        /**
         * @param cairoSipHostName the cairoSipHostName to set
         */
        public void setCairoSipHostName(String cairoSipHostName) {
            this.cairoSipHostName = cairoSipHostName;
        }

        /**
         * @return the cairoSipPort
         */
        public int getCairoSipPort() {
            return cairoSipPort;
        }

        /**
         * @param cairoSipPort the cairoSipPort to set
         */
        public void setCairoSipPort(int cairoSipPort) {
            this.cairoSipPort = cairoSipPort;
        }

        /**
         * @return the mySipAddress
         */
        public String getMySipAddress() {
            return mySipAddress;
        }

        /**
         * @param mySipAddress the mySipAddress to set
         */
        public void setMySipAddress(String mySipAddress) {
            this.mySipAddress = mySipAddress;
        }

        /**
         * @return the port
         */
        public int getPort() {
            return port;
        }

        /**
         * @param port the port to set
         */
        public void setPort(int port) {
            this.port = port;
        }

        /**
         * @return the stackName
         */
        public String getStackName() {
            return stackName;
        }

        /**
         * @param stackName the stackName to set
         */
        public void setStackName(String stackName) {
            this.stackName = stackName;
        }

        /**
         * @return the transport
         */
        public String getTransport() {
            return transport;
        }

        /**
         * @param transport the transport to set
         */
        public void setTransport(String transport) {
            this.transport = transport;
        }

        /**
         * @return the dialogService
         */
        public SpeechletService getDialogService() {
            return dialogService;
        }

        /**
         * @param dialogService the dialogService to set
         */
        public void setDialogService(SpeechletService dialogService) {
            this.dialogService = dialogService;
        }
        
        private class SessionPair {
            private SipSession internal;
            private SipSession external;
            
            public SessionPair(SipSession i, SipSession e) {
                internal = i;
                external = e;
            }
            
            /**
             * @return the external
             */
            public SipSession getExternal() {
                return external;
            }
            /**
             * @param external the external to set
             */
            public void setExternal(SipSession external) {
                this.external = external;
            }
            /**
             * @return the internal
             */
            public SipSession getInternal() {
                return internal;
            }
            /**
             * @param internal the internal to set
             */
            public void setInternal(SipSession internal) {
                this.internal = internal;
            }
            
        }

        public void processInfoRequest(SipSession session, String contentType, String contentSubType, String content) {
            
            _logger.debug("SIP INFO request: "+contentType+"/"+contentSubType+"\n"+content);
           
            String code = null;
            int duration = 0 ;
            
            //if dtmf signal     
            if ((contentType.trim().equals("application")) &&(contentSubType.trim().equals("dtmf-relay"))) {

                //Handle the client side dtmf signaling
                if (content == null) {
                    _logger.warn("sip info request with a dtmf-relay content type with no content.");
                } else {

                    String lines[] = content.toString().split("\n");
                    for (int i=0; i<lines.length;i++) {
                        String parse[] = lines[i].toString().split("=");
                        if (parse[0].equals("Signal")) {
                            code = parse[1];
                        }
                        if (parse[0].equals("Duration")) {
                            duration = Integer.parseInt(parse[1].trim());
                        }
                    }
                    _logger.debug("The DTMF code : "+code);
                    _logger.debug("The duration: "+ duration);

                    dialogService.dtmf(session,code.charAt(0));


                    //TODO: Forward the info request on to the speech server
                    //_sipAgent.sendInfoRequest(session, contentType,contentSubType,content);
                }

            } else {
                _logger.warn("Unhandled SIP INFO request content type: "+contentType+"/"+contentSubType+"\n"+content);
            }

        }
}