/**
 * This package contains the user relevant interfaces and classes for a text
 * based interface of the client.
 *
 * <p>
 * Typically a text based client performs the following steps to establish
 * a connection:
 * </p>
 *
 * <p>
 * <code>
 * // Create a text server and start it.<br>
 * TextServer server = new TextServer(4242);<br>
 * server.start();<br>
 * <br>
 * // Add a {@link org.jvoicexml.client.text.TextListener} to receive the
 * output<br>
 * server.addTextListener(this);<br>
 * <br>
 * // Create a {@link org.jvoicexml.RemoteClient} to be passed to the<br>
 * // interpreter<br>
 * RemoteClient client = server.getRemoteClient();<br>
 * <br>
 * // Create a session.<br>
 * Session session = jvxml.createSession(client);
 * </code>
 * </p>
 *
 * <p>
 * The {@link org.jvoicexml.client.text.TextServer} also serves as an entry
 * point to send the input.
 * </p>
 * @since 0.6
 */

package org.jvoicexml.client.text;
