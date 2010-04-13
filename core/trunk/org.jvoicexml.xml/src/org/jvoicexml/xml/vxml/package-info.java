/**
 * This package contains the classes to handle VoiceXML 2.0 and VoiceXML 2.1
 * documents and related tags.
 *
 * <p>
 * VoiceXML 2.0 is specified at
 * <a href="http://www.w3.org/TR/voicexml20/">
 * http://www.w3.org/TR/voicexml20/</a>, VoiceXML 2.1 is specified at
 * <a href="http://www.w3.org/TR/voicexml21/">
 * http://www.w3.org/TR/voicexml21/</a> as an extension to VoiceXML 2.0.
 * </p>
 * <p>
 * The document type of VoiceXML documents can be controlled via the
 * <code>jvoicexml.vxml.version</code> system property. A value
 * of <code>2.0</code> sets the document type to {@link VoiceXml20DocumentType}
 * and a value of <code>2.1</code> sets the document type to
 * {@link VoiceXml21DocumentType}. There is no document type by default.
 * </p>
 * <p>
 * It is possible to provide own document types via the method
 * {@link org.jvoicexml.xml.vxml.VoiceXmlDocument#setDocumentTypeFactory(DocumentTypeFactory)}.
 * </p>
 */

package org.jvoicexml.xml.vxml;
