/**
 * MMI lifecycle events.
 * 
 * <p>
 * This library provides container classes to handle MMI lifecycle events
 * as specified in {@link https://www.w3.org/TR/mmi-arch/}. This library is
 * independent to the JVoiceXML project and can also be used separately.
 * </p>
 * The current implementations supports the following formats
 * <ul>
 * <li>XML</li>
 * <li>JSON</li>
 * <li>protobuf</li>
 * </ul>
 */

@javax.xml.bind.annotation.XmlSchema(elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
    namespace = "http://www.w3.org/2008/04/mmi-arch",
    xmlns = { @XmlNs(prefix = "mmi", namespaceURI = "http://www.w3.org/2008/04/mmi-arch"),
        @XmlNs(prefix = "", namespaceURI = "http://www.w3.org/2008/04/mmi-arch")})
package org.jvoicexml.mmi.events;

import javax.xml.bind.annotation.XmlNs;

