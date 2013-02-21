/**
 * The event model for VoiceXML 3.0 builds upon the DOM Level 3 Events
 * [DOM3Events] specification. DOM Level 3 Events offer a robust set of
 * interfaces for managing the listener registration, dispatching, propagation,
 * and handling of events, as well as a description of how events flow through
 * an XML tree.
 * <p>
 * The DOM 3.0 event model offers VoiceXML developers a rich set of interfaces
 * that allow them to easily add behavior to their applications. In addition,
 * conforming to the standard DOM event model enables authors to integrate their
 * Voice applications in next generation multimodal or multi-namespaced
 * frameworks such as MMI and CDF with minimal efforts. Note that the VXML 2.0
 * style events are supported through a new DOM event named 'vxmlevent', and if
 * this vxmlevent is uncanceled then the default action is to run the VXML 2.0
 * event handling.
 * </p>
 * <p>
 * Within the VoiceXML 3.0 semantic model, the DOM Level 3 Events APIs are
 * available to all Resource Controllers that have markup elements associated
 * with them. Indeed, this section covers the eventing APIs as available to
 * VoiceXML 3.0 markup elements. The following section describes how the
 * semantic model ties in with the DOM eventing model.
 */

package org.jvoicexml.processor.vxml3.event.internal;

