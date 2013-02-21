/**
 * User relevant interfaces and classes for telephony connections.
 *
 * <p>
 * The basic implementation of a {@link org.jvoicexml.CallManager} maintains
 * active connections as {@link org.jvoicexml.callmanager.Terminal}s.
 * </p>
 * <p>
 * The {@link org.jvoicexml.CallManager} has several tasks
 * <ol>
 * <li>
 * manage a mapping of terminals to an URI of the starting document of an
 * application through the
 * {@link org.jvoicexml.callmanager.ConfiguredApplication}
 * </li>
 * <li>
 * maintain a list of {@link org.jvoicexml.callmanager.Terminal}s as an
 * interface to the telephony environment
 * </li>
 * <li>
 * initiate calls in JVoiceXML and call the configured URI for the terminal.
 * </li>
 * </ol>
 * </p>
 */

package org.jvoicexml.callmanager;

