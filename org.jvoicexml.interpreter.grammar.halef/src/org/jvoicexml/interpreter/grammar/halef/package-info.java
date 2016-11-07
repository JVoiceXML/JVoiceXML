/**
 * Demo implementation of a grammar to demonstrate how to enhance JVoiceXML
 * by own grammar formats. This implementation supports grammars that are built
 * via regular expressions as mentioned, e.g., at
 * <a href="http://www.cisco.com/c/en/us/td/docs/ios/voice/vxml/developer/guide/vxmlprg/refgde1.html">http://www.cisco.com/c/en/us/td/docs/ios/voice/vxml/developer/guide/vxmlprg/refgde1.html</a>.
 * 
 * <p>
 * Extensions to grammars usually feature the following parts
 * <ol>
 * <li>a type definition to introduce the new type</li>
 * <li>an implementation as a dedicated storage for the grammar</li> 
 * <li>a parser and evaluator for the grammar</li>
 * </ol>
 * </p>
 * 
 * <p>
 * Here, the type definition comprises
 * <ul>
 * <li>{@link org.jvoicexml.interpreter.grammar.regex.RegexGrammarTypeFactory}</li>
 * <li>{@link org.jvoicexml.interpreter.grammar.regex.RegexGrammarType}</li>
 * </ul>
 * 
 * Grammar extensions are loaded via the Java service interface from
 * {@link org.jvoicexml.xml.srgs.Grammar}. Hence, this jar must be loaded in the
 * same classloader as org.jvoicexml.xml.jar. Therefore, new grammars must
 * provide a {@link org.jvoicexml.xml.srgs.GrammarTypeFactory}. In this case,
 * this is the
 * {@link org.jvoicexml.interpreter.grammar.regex.RegexGrammarTypeFactory}.
 * </p>
 * 
 * <p>
 * Grammar implementations must provide implementations of
 * {@link org.jvoicexml.implementation.GrammarImplementation}. Here, the
 * implementation comprises
 * <ul>
 * <li>{@link org.jvoicexml.interpreter.grammar.regex.RegexGrammarImplementation}</li>
 * </ul> 
 * </p>
 *
 * <p>
 * Grammar parsers and evaluators are able to parse utterances into
 * semantic interpretations. Here, it comprises
 * <ul>
 * <li>{@link org.jvoicexml.interpreter.grammar.regex.RegexGrammarParser}</li>
 * <li>{@link org.jvoicexml.interpreter.grammar.regex.RegexGrammarEvaluator}</li>
 * </ul>
 * They must made be available to the implementation platform. For instance, the
 * text implementation platform comprises a configuration part to set the
 * parsers to use.
 * <pre>
&lt;beans:bean class="org.jvoicexml.implementation.text.TextPlatformFactory"&gt;
    &lt;beans:property name="instances" value="1" /&gt;
        &lt;property name="grammarParsers"&gt;
            &lt;list&gt;
                &lt;bean class="org.jvoicexml.srgs.SrgsSisrXmlGrammarParser" /&gt;
                &lt;bean class="org.jvoicexml.interpreter.grammar.regex.RegexGrammarParser" /&gt;
            &lt;/list&gt;
        &lt;/property&gt;
    &lt;/beans:bean&gt;
&lt;/beans:bean&gt;
 * </pre>
 * Do not forget to add the corresponding jar to the classpath.
 * </p>
 *  
 * @since 0.7.8
 */

package org.jvoicexml.interpreter.grammar.regex;

