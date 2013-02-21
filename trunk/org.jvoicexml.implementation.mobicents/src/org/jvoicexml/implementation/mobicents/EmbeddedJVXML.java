/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/demo/tags/0.7.5.GA/org.jvoicexml.demo.embedded/src/org/jvoicexml/demo/embedded/EmbeddedJVoiceXML.java $
 * Version: $LastChangedRevision: 2771 $
 * Date:    $Date: 2011-08-26 10:37:28 +0200 (Fr, 26 Aug 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.implementation.mobicents;

import org.apache.log4j.Logger;
import java.io.File;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.sip.SipServletRequest;
import org.jvoicexml.CharacterInput;

import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlMain;
import org.jvoicexml.JVoiceXmlMainListener;
import org.jvoicexml.Session;
import org.jvoicexml.client.BasicConnectionInformation;
import org.jvoicexml.config.JVoiceXmlConfiguration;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.implementation.jvxml.JVoiceXmlSystemOutput;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.implementation.mobicents.callmanager.MobicentsConnectionInformation;
import org.mobicents.servlet.sip.restcomm.callmanager.mgcp.MgcpCallTerminal;
import org.util.ExLog;

/**
 * Demo to show how JVoiceXML can be launched by other applications.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2771 $
 * @since 0.7.5
 */
public final class EmbeddedJVXML implements JVoiceXmlMainListener 
{
    private static final Logger LOGGER =
            Logger.getLogger(EmbeddedJVXML.class);

    /** Reference to JVoiceXML. */
    private JVoiceXmlMain jvxml;
    private ConnectionInformation jndiClient = null;
    private ConcurrentHashMap<String, Session> listCurrentCalls = new ConcurrentHashMap();

    /**
     * Do not create from outside.
     */
    public EmbeddedJVXML() {
        // Specify the location of the config folder.
//        System.setProperty("jvoicexml.config", "../org.jvoicexml/config");
        System.setProperty("jvoicexml.config", "conf/jvxml");
    }

    /**
     * Calls the VoiceXML interpreter context to process the given XML document.
     * 
     * @param uri
     *            URI of the first document to load
     * @exception JVoiceXMLEvent
     *                error processing the call.
     * @throws InterruptedException
     *                error waiting for JVoiceXML
     */
    public void init() {
        try {
            LOGGER.info("initializing JVoiceXML interface ......");
            JVoiceXmlConfiguration config = new JVoiceXmlConfiguration();
            jvxml = new JVoiceXmlMain(config);
            jvxml.addListener(this);
            jvxml.start();
//            wait();
        } catch (Exception ex) {
            ExLog.exception(LOGGER, ex);
        }
    }

    public Session createSession(MgcpCallTerminal call) {
        try {
            //get JNDI client connection;
            jndiClient = new MobicentsConnectionInformation(call, "mobicents", "mobicents");
            final Session session = jvxml.createSession(jndiClient);
            return session;
        } catch (JVoiceXMLEvent ev) {
            ExLog.exception(LOGGER, ev);
        } catch (Exception ex) {
            ExLog.exception(LOGGER, ex);
        }
        return null;
    }

    public void deleteSession(MgcpCallTerminal call) 
    {
        try {
            final Session session = listCurrentCalls.remove(call.getSIPCallID());
            if (session == null) {
                LOGGER.error("error when delete a jvoice session with call:" + call);
                return;
            }
            ImplementationPlatform implPlatform = ((JVoiceXmlSession) session).getImplementationPlatform();
            LOGGER.info("terminating a jvoice session:" + session + " with call:" + call
                    + " ImplementationPlatform:" + implPlatform);
            JVoiceXmlSystemOutput sysout=(JVoiceXmlSystemOutput)implPlatform.getSystemOutput();
            ((MobicentsSynthesizedOutput)sysout.getSynthesizedOutput()).cancelAllSpeakers();
//            if(implPlatform!=null) 
//            {
//                ((JVoiceXmlImplementationPlatform)implPlatform).telephonyCallHungup(null);
//                implPlatform.close();
//            }
//            ((JVoiceXmlSession) session).cleanup();
        } catch (ConnectionDisconnectHangupEvent evnt) {
            ExLog.exception(LOGGER, evnt);
        } catch (NoresourceError err) {
            ExLog.exception(LOGGER, err);
        } catch (Exception ex) {
            ExLog.exception(LOGGER, ex);
        }
    }

    public void execute(MgcpCallTerminal call) {
        try {
            LOGGER.debug("execute vxml script for a MgcpCallTerminal:" + call);
//            File dialog = new File("conf/jvxml/vxml/hello.vxml");
            //play wav and collect DTMF input
//            File dialog = new File("conf/jvxml/vxml/hellodtmf.vxml");
            File dialog = new File("conf/jvxml/vxml/vb_sms_talk.vxml");
            final URI vxmlURI = dialog.toURI();
            //get JNDI client connection;
            jndiClient = new MobicentsConnectionInformation(call, "mobicents", "mobicents");
            final Session session = jvxml.createSession(jndiClient);
            listCurrentCalls.put(call.getSIPCallID(), session);
            LOGGER.debug("call jxmlsession:" + session + " and initiate a call at JVoiceXML:vxmlURI:" + vxmlURI);
            session.call(vxmlURI);
//            session.waitSessionEnd();
//            session.hangup();
        } catch (JVoiceXMLEvent ev) {
            ExLog.exception(LOGGER, ev);
        } catch (Exception ex) {
            ExLog.exception(LOGGER, ex);
        }
    }

    public void procSIPInfo(SipServletRequest request) {
        try {
            Session session = listCurrentCalls.get(request.getCallId());
            LOGGER.debug("get jvxml session for sip callid:" + request.getCallId() + " jxmlsession:" + session);
            if (session == null) {
                LOGGER.debug(" null jxmlsession with call id:" + request.getCallId());
                return;
            }
            String messageContent = new String((byte[]) request.getContent());
            int idexdigit = messageContent.indexOf(VAppCfg.digitPattern);
            String signal = messageContent.substring(idexdigit + VAppCfg.digitPattern.length(), idexdigit + VAppCfg.digitPattern.length() + 2).trim();
            LOGGER.info("got INFO request with following content " + messageContent
                    + " signal:" + signal + " charAt0:" + signal.trim().charAt(0));
            CharacterInput input = session.getCharacterInput();
            if (input == null) {
                LOGGER.error("CharacterInput is null:" + input);
            } else {
                input.addCharacter(signal.trim().charAt(0));
            }
        } catch (JVoiceXMLEvent ev) {
            ExLog.exception(LOGGER, ev);
        } catch (Exception ex) {
            LOGGER.error(" error when processing sip info with" + request.toString());
            ExLog.exception(LOGGER, ex);
        }
        
    }

    /**
     * Calls the VoiceXML interpreter context to process the given XML document.
     * 
     * @param uri
     *            URI of the first document to load
     * @exception JVoiceXMLEvent
     *                error processing the call.
     * @throws InterruptedException
     *                error waiting for JVoiceXML
     */
    private synchronized void interpretDocument(final URI uri)
            throws JVoiceXMLEvent, InterruptedException {
        JVoiceXmlConfiguration config = new JVoiceXmlConfiguration();
        jvxml = new JVoiceXmlMain(config);
        jvxml.addListener(this);
        jvxml.start();

        wait();

        final ConnectionInformation client = new BasicConnectionInformation(
                "dummy", "jsapi10", "jsapi10");
        final Session session = jvxml.createSession(client);

        session.call(uri);
        session.waitSessionEnd();
        session.hangup();
    }

    /**
     * The main method.
     * 
     * @param args
     *            Command line arguments. None expected.
     */
    public static void main(final String[] args) {
        final EmbeddedJVXML demo = new EmbeddedJVXML();

        try {
            File dialog = new File("config/vxml/hello.vxml");
            final URI uri = dialog.toURI();
            demo.interpretDocument(uri);
        } catch (org.jvoicexml.event.JVoiceXMLEvent e) {
            LOGGER.error("error processing the document", e);
        } catch (InterruptedException e) {
            LOGGER.error("error processing the document", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void jvxmlStarted() {
        notifyAll();
    }

    @Override
    public void jvxmlTerminated() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void jvxmlStartupError(final Throwable exception) {
        LOGGER.error("error starting JVoiceML", exception);
    }
}
