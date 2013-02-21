/**
 * External listeners for synthesis and recognition events.
 * 
 * <p>
 * If you wish to use this feature, have a look at the <code>jvoicexml.xml</code>-configuration file.
 * The config's defaultpath is &lt;JVOICEXML-HOME&gt;/config.
 * Near the end of the config you can see an initialisation-skeleton for your listeners.<br/>
 * </p>
 * <p>
 * It looks like
 * <code><pre>
 * &lt;!--
 *  Implementation platform to handle recognition and speech synthesis.
 * --&gt;
 * &lt;bean id=&quot;org.jvoicexml.ImplementationPlatformFactory&quot;
 *  class=&quot;org.jvoicexml.implementation.jvxml.JVoiceXmlImplementationPlatformFactory&quot;&gt;
 *    &lt;!-- 
 *    &lt;property name=&quot;externalRecognitionListener&quot;&gt;
 *        <b>&lt;bean class=&quot;YourClas&quot; /&gt;</b>
 *    &lt;/property&gt;
 *    &lt;property name=&quot;externalSynthesisListener&quot;&gt;
 *        <b>&lt;bean class=&quot;YourClas&quot; /&gt;</b>
 *    &lt;/property&gt;
 *    --&gt;
 * &lt;/bean&gt;
 * </pre></code>
 * As you can see, you can add your own classes as 
 * {@link org.jvoicexml.implementation.external.SocketExternalRecognitionListener recognizer-} and 
 * {@link org.jvoicexml.implementation.external.SocketExternalSynthesisListener synthesizerlistener}.
 * </p>
 * 
 * <p>
 * JVoiceXml also has sample implementations on board 
 * (see 
 * {@link org.jvoicexml.implementation.external.SocketExternalRecognitionListener}
 *  and
 * {@link org.jvoicexml.implementation.external.SocketExternalSynthesisListener}
 * ). 
 * In order to use these implementations the <code>jvoicexml.xml<code>-config needs to be adjusted.
 * </p>
 * 
 * <p>
 * An example-configuration could look like this
 * <code>
 * <pre>
 * <i>&lt;!--
 *  Implementation platform to handle recognition and speech synthesis.
 * --&gt;
 * &lt;bean id=&quot;org.jvoicexml.ImplementationPlatformFactory&quot;
 *  class=&quot;org.jvoicexml.implementation.jvxml.JVoiceXmlImplementationPlatformFactory&quot;&gt;</i>
 *    <b>&lt;property name=&quot;externalRecognitionListener&quot;&gt;
 *        &lt;bean class=&quot;org.jvoicexml.implementation.external.SocketExternalRecognitionListener&quot; &gt;
 *                &lt;property name=&quot;port&quot; value=&quot;5555&quot;/&gt;
 *        &lt;/bean&gt;
 *    &lt;/property&gt;
 *    &lt;property name=&quot;externalSynthesisListener&quot;&gt;
 *        &lt;bean class=&quot;org.jvoicexml.implementation.external.SocketExternalSynthesisListener&quot; &gt;
 *                &lt;property name=&quot;port&quot; value=&quot;5556&quot;/&gt;
 *        &lt;/bean&gt;
 *    &lt;/property&gt;</b>
 * <i>&lt;/bean&gt;</i>
 * </pre>
 * </code>
 * <br/>
 * <p>
 * Short summary:
 * <ol>
 * <li>
 * Uncomment the two properties <code>&lt;property name=&quot;...&quot;/&gt;</code>
 * (&quot;externalRecognitionListener&quot; and &quot;externalSynthesisListener&quot;)
 * </li>
 * <li>
 * Add the specific implementation classes as <code>&lt;bean class/&gt;</code> 
 * (<i>here 
 * {@link org.jvoicexml.implementation.external.SocketExternalRecognitionListener}
 *  and
 * {@link org.jvoicexml.implementation.external.SocketExternalSynthesisListener}
 * </i>)
 * <li>
 * Set the listening ports as <code>&lt;property name=&quot;port&quot; .../&gt;</code> 
 * (<i>here: 
 * <code>&quot;5555&quot;</code> for the recognizer 
 * and 
 * <code>&quot;5556&quot;</code> for the synthesis
 * </i>) 
 * </li>
 * <ol>
 * </p>
 */

package org.jvoicexml.implementation.external;

