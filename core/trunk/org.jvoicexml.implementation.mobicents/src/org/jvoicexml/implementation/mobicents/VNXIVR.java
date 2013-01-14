/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jvoicexml.implementation.mobicents;

import org.apache.log4j.Logger;
import java.io.File;
import java.util.Properties;


import org.cafesip.sipunit.SipPhone;
import org.cafesip.sipunit.SipStack;
import org.jvoicexml.JVoiceXmlMain;
import org.mobicents.servlet.sip.SipServletTestCase;
import org.mobicents.servlet.sip.catalina.SipStandardManager;
import org.mobicents.servlet.sip.startup.SipContextConfig;
import org.mobicents.servlet.sip.startup.SipStandardContext;
import org.util.TextUtil;

public class VNXIVR extends SipServletTestCase {
        private static final Logger LOGGER = Logger.getLogger(VNXIVR.class);
    private SipStack[] sipStackReceivers;
    private SipPhone[] sipPhoneReceivers;
    // Don't restart the server for this set of tests.
    private static boolean firstTime = true;
    SipStandardContext context = null;
    SipStandardManager manager = null;

    public VNXIVR(String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        LOGGER.debug("");
        if (firstTime) {
            super.setUp();
        }
        firstTime = true;
    }

    @Override
    public void tearDown() throws Exception {
        if (sipPhoneReceivers != null) {
            for (SipPhone sp : sipPhoneReceivers) {
                if (sp != null) {
                    sp.dispose();
                }
            }
        }
        if (sipStackReceivers != null) {
            for (SipStack ss : sipStackReceivers) {
                if (ss != null) {
                    ss.dispose();
                }
            }
        }
        super.tearDown();
        context = null;
        manager = null;
    }

    @Override
    public void deployApplication() {

        context = new SipStandardContext();
        context.setDocBase(TextUtil.osResPath(projectHome + "/conf/applications/VNXIVR/sipapp"));
        context.setName("VNXIVR-context");
        context.setPath("/VNXIVR");
        context.addLifecycleListener(new SipContextConfig());
        manager = new SipStandardManager();
        context.setManager(manager);
        LOGGER.debug("deploying host context:" + context
                + " getBasePath:" + context.getBasePath() + " getDocBase:" + context.getDocBase());
        assertTrue(tomcat.deployContext(context));
    }

    @Override
    protected String getDarConfigurationFile() {
        LOGGER.debug("");
        return TextUtil.osResPath("file:///"
                + projectHome
                + "/conf/resources/VNXIVR.properties");
    }

    public SipStack makeStack(String transport, int port) throws Exception {
        Properties properties = new Properties();
        String peerHostPort1 = "" + System.getProperty("org.vnxtele.vnxivr.sipaddr") + ":5090";
        properties.setProperty("javax.sip.OUTBOUND_PROXY", peerHostPort1 + "/"
                + "udp");
        properties.setProperty("javax.sip.STACK_NAME", "UAC_" + transport + "_"
                + port);
        properties.setProperty("sipunit.BINDADDR", "" + System.getProperty("org.vnxtele.vnxivr.sipaddr") + "");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",
                "logs/simplesipservlettest_debug_port" + port + ".txt");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG",
                "logs/simplesipservlettest_log_port" + port + ".xml");
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL",
                "32");
        return new SipStack(transport, port, properties);
    }

    public void init() throws Exception {
    }

    /**
     * The main method, which starts the interpreter.
     *
     * @param args Command line arguments. None expected.
     *
     * @since 0.4
     */
    public static void jvxmlmain(final String[] args) 
    {
        final JVoiceXmlMain jvxml = new JVoiceXmlMain();

        // Start the interpreter as a thread.
        jvxml.start();

        // Wait until the interpreter thread terminates.
        jvxml.waitShutdownComplete();

        System.exit(0);
    }

    public static void main(String[] args) {
        try {

            System.setProperty("javax.servlet.sip.ar.spi.SipApplicationRouterProvider",
                    "org.mobicents.servlet.sip.router.DefaultApplicationRouterProvider");
            VAppCfg appcfg = new VAppCfg();
            appcfg.initcontext();
            System.setProperty("CATALINA_HOME", TextUtil.standardPath(VAppCfg.CATALINA_HOME));
            System.setProperty("CATALINA_BASE", TextUtil.standardPath(VAppCfg.CATALINA_HOME));
            System.setProperty("org.vnxtele.vnxivr.configfile", "conf" + File.separatorChar + "VNXIVR.cfg");
            System.setProperty("org.vnxtele.vnxivr.sipstackpropertiesfile",
                    TextUtil.osResPath(VAppCfg.projectHome + "conf" + File.separatorChar + "VNXIVR.cfg"));
            System.setProperty("org.vnxtele.vnxivr.sipaddr", VAppCfg.sipStackAddr);
            System.setProperty("org.vnxtele.vnxivr.sipport", VAppCfg.sipStackPort);
            System.setProperty("org.vnxtele.vnxivr.tomcatAddress", VAppCfg.httpServerBindAddress);
            System.setProperty("org.vnxtele.vnxivr.tomcatPort", VAppCfg.httpServerBindPort);
            System.setProperty("org.vnxtele.vnxivr.inbound_mgw_address", VAppCfg.INBOUND_MGW_ADDRESS);
            System.setProperty("org.vnxtele.vnxivr.mgw_port", VAppCfg.MGW_PORT);
            //editting CONTACT address for inbound
            if (VAppCfg.enableEditContactHeader == true) {
                System.setProperty("org.vnxtele.vnxivr.INBOUND_CONTACT_IP", VAppCfg.INBOUND_CONTACT_IP);
            }
            VNXIVR c2c = new VNXIVR("VNXIVR");
            c2c.setUp();
            //
//            jvxmlmain(args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
