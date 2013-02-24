/**
 * User relevant interfaces and classes for a text based implementation
 * platform.
 * 
 * <p>
 * Main classes are {@link org.jvoicexml.implementation.text.TextTelephony}
 * as an interface to the client and
 * {@link org.jvoicexml.implementation.text.TextSynthesizedOutput} as a bridge
 * to send the SSML documents to the client and
 * {@link org.jvoicexml.implementation.text.TextSpokenInput} as a bridge
 * to receive input from the client.
 * </p>
 * 
 * <p>
 * The way, output is sent to the client is illustrated by the following
 * sequence diagram<br>
 * <img src="doc-files/SendingOutputWithTheTextPlatform.jpg"/>
 * </p>
 * 
 * <p>
 * The way, input is received from the client is illustrated by the following
 * sequence diagram<br>
 * <img src="doc-files/ReceivingInputWithTheTextPlatform.jpg"/>
 * </p>
*
 * @since 0.6
 */

package org.jvoicexml.implementation.text;
