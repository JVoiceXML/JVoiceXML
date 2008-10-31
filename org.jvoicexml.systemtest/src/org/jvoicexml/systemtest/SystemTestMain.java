package org.jvoicexml.systemtest;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * System Test application start point.
 * 
 * @author lancer
 */
public class SystemTestMain {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(SystemTestMain.class);

    /**
     * The main method.
     * 
     * @param args
     *            Command line arguments. None expected.
     */
    public static void main(final String[] args) {
        LOGGER.info("Starting SystemTest for JVoiceXML...");

        final String filename = System.getProperty("systemtestconfig.config",
                "/systemtestconfig.xml");

        SystemTestConfigLoader config = new SystemTestConfigLoader(filename);

        SystemTestCallManager cm = config.loadObject(
                SystemTestCallManager.class, "callmanager");

        JVoiceXml interpreter = findInterpreter();
        if (interpreter == null) {
            LOGGER.info("JVoiceXML not found, exit.");
            return;
        }
        cm.setJVoiceXml(interpreter);

        try {
            cm.start();
        } catch (org.jvoicexml.event.JVoiceXMLEvent e) {
            LOGGER.error("error processing the document", e);
        }
    }

    private static JVoiceXml findInterpreter() {
        JVoiceXml jvxml = null;
        Context context;
        try {
            context = new InitialContext();
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error creating initial context", ne);
            context = null;
        }

        try {
            jvxml = (JVoiceXml) context.lookup("JVoiceXml");
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error obtaining JVoiceXml", ne);
        }
        return jvxml;
    }

    /**
     * Copy from org.jvoicexml.config.JVoiceXmlConfiguration if dirk modify
     * JVoiceXmlConfiguration(String file) method to public, there need not
     * create this class.
     */
    final static class SystemTestConfigLoader {

        /** The factory to retrieve configured objects. */
        private final XmlBeanFactory factory;

        public SystemTestConfigLoader(final String filename) {
            final Resource res = new ClassPathResource(filename);

            factory = new XmlBeanFactory(res);
        }

        public <T extends Object> T loadObject(final Class<T> baseClass,
                final String key) {
            final Object object;

            try {
                object = factory.getBean(key, baseClass);
            } catch (org.springframework.beans.BeansException be) {
                be.printStackTrace();

                return null;
            }

            return baseClass.cast(object);
        }
    }
}
