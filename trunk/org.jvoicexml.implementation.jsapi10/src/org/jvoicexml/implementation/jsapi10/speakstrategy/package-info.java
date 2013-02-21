/**
 * This package contains
 * {@link org.jvoicexml.implementation.jsapi10.SSMLSpeakStrategy} 
 * implementations to support SSML with JSAPI 1.0.
 * <p>
 * JSAPI 1.0 offers only support for JSML which is a subset of SSML. The
 * implementations of this package serve to bridge this gap by providing
 * suitable strategies for each of the SSML tags. This may result in lower
 * quality of the prosodic features since the spoken text is fragmented into
 * pieces that are handed to the synthesizer. In some cases it may even lead
 * to unwanted breaks in the synthesized output. Hence, it is not suitable to
 * be used in commercial settings that aim at high quality.
 * </p>
 */

package org.jvoicexml.implementation.jsapi10.speakstrategy;

