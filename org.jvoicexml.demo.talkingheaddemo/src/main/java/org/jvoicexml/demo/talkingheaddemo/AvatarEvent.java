/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.demo.talkingheaddemo;

/**
 * Event, which can be triggered by the gui for show the reactions of the
 * avatar to different events.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.7
 */
public abstract class AvatarEvent {
  /**
   * Event text to display.
   */
  private String offerText;
  
  /**
   * Text to display after accept the event.
   */
  private String acceptText;
  
  /**
   * Text to display after reject the event.
   */
  private String rejectText;

  /**
   * Constructor to define the event.
   * 
   * @param theOfferText text of the event
   * @param theAcceptText text to display after accepting the event
   * @param theRejectText text to display after rejecting the event
   */
  public AvatarEvent(final String theOfferText,
                     final String theAcceptText,
                     final String theRejectText) {
    offerText = theOfferText;
    acceptText = theAcceptText;
    rejectText = theRejectText;
  }

  /**
   * Getter for the offer text of the event.
   * 
   * @return the offer text
   */
  public final String getOfferText() {
    return offerText;
  }

  /**
   * Sets the offer text.
   * 
   * @param newOfferText the new offer text
   */
  public final void setOfferText(final String newOfferText) {
    this.offerText = newOfferText;
  }

  /**
   * Getter for the accept text.
   * 
   * @return the accept text
   */
  public final String getAcceptText() {
    return acceptText;
  }

  /**
   * Sets the accept text.
   * 
   * @param newAcceptText the new accept text
   */
  public final void setAcceptText(final String newAcceptText) {
    this.acceptText = newAcceptText;
  }

  /**
   * Getter for the reject text.
   * 
   * @return the reject text
   */
  public final String getRejectText() {
    return rejectText;
  }

  /**
   * Sets the reject text.
   * 
   * @param newRejectText the new reject text
   */
  public final void setRejectText(final String newRejectText) {
    this.rejectText = newRejectText;
  }

  /**
   * Abstract Method for checking the acceptance status.
   * 
   * @return true if the event is accepted, otherwise false
   */
  public abstract boolean doAccepted();
}
