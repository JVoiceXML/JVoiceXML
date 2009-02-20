/**
 * 
 */
package org.jvoicexml.callmanager.jtapi;

import java.net.UnknownHostException;
import java.util.Map;

import org.jvoicexml.RemoteClient;
import org.jvoicexml.callmanager.CallManager;
import org.jvoicexml.callmanager.ConfiguredApplication;
import org.jvoicexml.callmanager.RemoteClientCreationException;
import org.jvoicexml.callmanager.RemoteClientFactory;

/**
 * A factory for the {@link JtapiRemoteClient}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class JtapiRemoteClientFactory implements RemoteClientFactory {
    /** The name of the terminal parameter. */
    public static final String TERMINAL = "terminal";

    /**
     * {@inheritDoc}
     */
    public RemoteClient createRemoteClient(final CallManager callManager,
            final ConfiguredApplication application,
            final Map<String, Object> parameters)
        throws RemoteClientCreationException {
        final JVoiceXmlTerminal term =
            (JVoiceXmlTerminal) parameters.get(TERMINAL);
        final String output = application.getOutputType();
        final String input = application.getInputType();
        try {
            return new JtapiRemoteClient(term, output, input);
        } catch (UnknownHostException e) {
            throw new RemoteClientCreationException(e.getMessage(), e);
        }
    }

}
