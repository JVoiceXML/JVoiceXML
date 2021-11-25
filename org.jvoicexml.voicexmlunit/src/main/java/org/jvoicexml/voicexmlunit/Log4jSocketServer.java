package org.jvoicexml.voicexmlunit;

import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.server.TcpSocketServer;

/**
 * A socket server to receive logging events from JVoiceXML. This is an
 * intermediate solution to get started but should be replaced by a 
 * proper solution, once 
 * <a href="https://github.com/apache/logging-log4j-tools">https://github.com/apache/logging-log4j-tools</a>
 * is available in a Maven or Gradle repository.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class Log4jSocketServer {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager.getLogger(Log4jSocketServer.class);

    /** The port number to use. */
    private final int port;

    /** Server socket. */
    private final TcpSocketServer<InputStream> server;

    /** The server thread. */
    private Thread thread;

    /**
     * Constructs a new object at the specified host name with the specified
     * port.
     * @param hostname the host to use
     * @param portNumber the port to use
     * @throws IOException error creating the socke server
     */
    public Log4jSocketServer(final int portNumber) throws IOException {
        port = portNumber;
        server = TcpSocketServer.createJsonSocketServer(port);
    }
 
    /**
     * Starts the logging server.
     */
    public void startLoggingServer() {
        if (thread != null) {
            stopLoggingServer();
        }
        thread = server.startNewThread();
        LOGGER.info("logging server started at port '" + port + "'");
    }

    /**
     * Stops the logging server.
     */
    public void stopLoggingServer() {
        if (thread == null) {
            return;
        }
        try {
            server.shutdown();
            server.awaitTermination(thread);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            thread = null;
        }
    }
    

}
