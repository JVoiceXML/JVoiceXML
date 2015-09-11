/**
 * Demo implementation of a grammar to demonstrate how to enhance JVoiceXML
 * by own grammar formats. This implementation supports grammars that are built
 * via regular expressions as mentioned, e.g., at
 * <a href="http://www.cisco.com/c/en/us/td/docs/ios/voice/vxml/developer/guide/vxmlprg/refgde1.html">http://www.cisco.com/c/en/us/td/docs/ios/voice/vxml/developer/guide/vxmlprg/refgde1.html</a>.
 * 
 * <p>
 * Grammar extensions are loaded via the Java service interface from
 * {@link org.jvoicexml.xml.srgs.Grammar}. Hence, this jar must be loaded in the
 * same classloader as org.jvoicexml.xml.jar. Therefore, new grammars must
 * provide a {@link org.jvoicexml.xml.srgs.GrammarTypeFactory}. In this case,
 * this is the
 * {@link org.jvoicexml.interpreter.grammar.regex.RegexGrammarTypeFactory}.
 * </p>
 *
 * @since 0.7.8
 */

package org.jvoicexml.interpreter.grammar.regex;

