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

package org.jvoicexml.systemtest.mobicents;


import com.vnxtele.util.VNXLog;
import com.vnxtele.util.VText;
import java.io.File;
import java.util.Properties;


import org.cafesip.sipunit.SipPhone;
import org.cafesip.sipunit.SipStack;
import org.mobicents.servlet.sip.SipServletTestCase;
import org.mobicents.servlet.sip.catalina.SipStandardManager;
import org.mobicents.servlet.sip.startup.SipContextConfig;
import org.mobicents.servlet.sip.startup.SipStandardContext;

public class BroadcastDemo extends SipServletTestCase {


	private SipStack[] sipStackReceivers;

	private SipPhone[] sipPhoneReceivers;


	// Don't restart the server for this set of tests.
	private static boolean firstTime = true;

	SipStandardContext context = null;
	SipStandardManager manager = null; 
		
	public BroadcastDemo(String name) {
		super(name);
	}

	@Override
	public void setUp() throws Exception 
        {
            VNXLog.debug("");
		if (firstTime) 
                {
			super.setUp();
		}
		firstTime = true;
	}

	@Override
	public void tearDown() throws Exception {
		if(sipPhoneReceivers != null) {
			for (SipPhone sp : sipPhoneReceivers) {
				if(sp != null) {
					sp.dispose();
				}
			}
		}
		if(sipStackReceivers != null) {
			for (SipStack ss : sipStackReceivers) {
				if(ss != null) {
					ss.dispose();
				}
			}
		}
		super.tearDown();
		context = null;
		manager = null;
	}

	@Override
	public void deployApplication() 
        {
            
		context = new SipStandardContext();
		context.setDocBase(VText.osResPath(projectHome +  "/conf/applications/BroadcastDemo/sipapp"));
		context.setName("BroadcastDemo-context");
		context.setPath("/BroadcastDemo");		
		context.addLifecycleListener(new SipContextConfig());
		manager = new SipStandardManager();
		context.setManager(manager);
                VNXLog.debug("deploying host context:"+context
                        + " getBasePath:"+context.getBasePath() + " getDocBase:"+context.getDocBase());
		assertTrue(tomcat.deployContext(context));		
	}

	@Override
	protected String getDarConfigurationFile() 
        {
            VNXLog.debug("");
            return VText.osResPath("file:///"
				+ projectHome
				+ "/conf/resources/org/mobicents/servlet/sip/testsuite/"
                        + "BroadcastDemo/BroadcastDemo.properties");
	}

	public SipStack makeStack(String transport, int port) throws Exception 
        {
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

        public static void init(String absoluteConfigFileName) {
        // TODO code application logic here
            try
            {
                
                System.setProperty("javax.servlet.sip.ar.spi.SipApplicationRouterProvider",
                        "org.mobicents.servlet.sip.router.DefaultApplicationRouterProvider");
                VNXLog genlog = new VNXLog("conf"+File.separatorChar+"BroadcastDemo.cfg");
                VNXLog.info("generating configuration file:"+absoluteConfigFileName);
                VAppCfg appcfg = new VAppCfg(absoluteConfigFileName);
                appcfg.initcontext();
                System.setProperty("CATALINA_HOME", VText.standardPath(VAppCfg.CATALINA_HOME));
		System.setProperty("CATALINA_BASE", VText.standardPath(VAppCfg.CATALINA_HOME));
                System.setProperty("org.vnxtele.vnxivr.configfile",absoluteConfigFileName);
                System.setProperty("org.vnxtele.vnxivr.sipstackpropertiesfile",absoluteConfigFileName);
                System.setProperty("org.vnxtele.vnxivr.sipaddr",VAppCfg.sipStackAddr);
                System.setProperty("org.vnxtele.vnxivr.sipport",Integer.toString(VAppCfg.sipStackPort));
                System.setProperty("org.vnxtele.vnxivr.tomcatAddress",VAppCfg.httpServerBindAddress);
                System.setProperty("org.vnxtele.vnxivr.tomcatPort",VAppCfg.httpServerBindPort);
                BroadcastDemo c2c = new BroadcastDemo("BroadcastDemo");
                c2c.setUp();
                while(true)
                    Thread.sleep(5000);
            }catch(Exception ex)
            {
                ex.printStackTrace();
            }
    }
        
         public static void main(String[] args) 
          {
            try
            {
                if(args==null || args.length<2)
                    System.err.println("trying java -jar BroadcastDemo.jar "
                            + "-config F:\\WorkStation\\Java\\Projects\\VNXIVR\\BroadcastDemo\\conf\\BroadcastDemo.cfg");
                else 
                {
                    for(int i=0;i<args.length;i++)
                        System.out.println("arg:["+i+"]="+args[i]);
                    BroadcastDemo.init(args[1]);
                }
            }catch(Exception ex)
            {
                ex.printStackTrace();
            }
    }
}
