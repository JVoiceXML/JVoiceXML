package org.jvoicexml.eclipse.jst.server;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

import org.eclipse.wst.server.core.IServer;

/**
 * JVoiceXMLPingThread
 * 
 * Ping the JVoiceXML server
 * 
 * @author Aurelian Maga
 * @author Dirk Schnelle
 * @version 0.1
 * 
 */

class JVoiceXMLPingThread extends Thread {

    final int SLEEP = 5000;
    IServer server;
    private JVoiceXMLServerBehaviour behaviour;
    boolean check;
    Context context;

    public JVoiceXMLPingThread(IServer iserver, JVoiceXMLServerBehaviour jsb) {
        behaviour = jsb;
        server = iserver;
        check = false;
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.rmi.registry.RegistryContextFactory");
        env.put(Context.PROVIDER_URL, "rmi://localhost:1099");

        try {
            context = new InitialContext(env);
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void stopPinging() {
        check = false;
    }

    public void run() {
        check = true;
        while (check) {
            try {
                Thread.sleep(SLEEP);
            } catch (Exception ignore) {
            }
            check();
        }
    }

    private void check() {

        try {

            Object jvxml = context.lookup("JVoiceXml");

            behaviour.setStarted();

            jvxml = null;

        } catch (Exception ignore) {
        }
    }
}
