/**
 * A {@link org.jvoicexml.CallManager} implementation for the
 * <a href="http://www.w3.org/TR/mmi-arch">MMI architectural pattern</a>.
 * It enables JVoiceXML to be used as a modality component.
 *
 * <p>
 * The event and transport layer can be customized by implementing the
 * {@link org.jvoicexml.callmanager.mmi.ETLProtocolAdapter}. The 
 * {@link org.jvoicexml.callmanager.mmi.VoiceModalityComponent} registers
 * itself as a listener to consume MMI events. It also employs the adapter
 * to send MMI messages.
 * </p>
 * 
 * <img src="doc-files/Voice-Modality-Component.png" />
 * 
 * <p>
 * For now it is possible to start a new JVoiceXML session using the
 * <code>StartRequest</code> and terminate it with a <code>StopRequest</code>.
 * If the JVoiceXML session terminates, a <code>DoneNotification</code> is
 * sent. The VoiceXML application can be identified via the
 * <code>ContentURL</code> attribute of the <code>PrepareRequest</code> or
 * <code>StartRequest</code>. Alternatively, it is possible to send
 * VoiceXML snippets in the <code>Content</code> attribute in one of the
 * previously mentioned messages. The following snippets are currently
 * supported:
 * <ul>
 * <li>strings to have the given text spoken in a prompt</li>
 * <li>prompt to have the given prompt spoken</li>
 * <li>field to get input from a VoiceXML document with the given field</li>
 * </ul>
 * In order to have this working it is required to deploy
 * <code>org.jvoicexml.callmanager.mmi.servlet.war</code> to the servlet
 * container of your choice.
 * </p>
 */
package org.jvoicexml.callmanager.mmi;

