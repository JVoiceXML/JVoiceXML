/**
 * User relevant interfaces and classes for a text based interface of the
 * client.
 *
 * <p>
 * Typically a text based client performs the following steps to establish
 * a connection:
 * </p>
 *
 * <p>
 * <code>
 * // Create a {@link org.jvoicexml.client.text.TextServer} and start it.<br>
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
 * // Create a {@link org.jvoicexml.Session}.<br>
 * Session session = jvxml.createSession(client);
 * </code>
 * </p>
 *
 * <p>
 * Note that the user input may not be active if a field's prompt arrives.
 * </p>
 *
 * <p>
 * The {@link org.jvoicexml.client.text.TextServer} also serves as an entry
 * point to send the input.
 * </p>
 *
 * <p>
 * Any user input can be sent to the server via the
 * {@link TextServer#sendInput(String)} method.
 * </p>
 * <p>
 * <code>
 * server.sendInput("this is my input");
 * </code>
 * </p>
 * @since 0.6
 */

package org.jvoicexml.client.text;
