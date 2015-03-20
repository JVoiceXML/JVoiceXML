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
package org.jvoicexml.implementation.lightweightbml.xmltags;

/**
 * Represents a command, which lets the avatar speak
 * 
 * @author Matthias Mettel
 *
 */
/**
 * Represents a xml-tag, which commands the avatar to speak some text.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.7
 */
public class Speech
    extends ITag {
  /**
   * Text of the avatar speech.
   */
  private String text;

  /**
   * Constructor to set the attributes.
   * 
   * @param id
   *          the id of command
   * @param required
   *          is the command required for execution
   * @param start
   *          start time of the command
   * @param end
   *          end time of the command
   * @param theText
   *          of the speech
   */
  public Speech(final String id,
      final boolean required,
      final String start,
      final String end,
      final String theText) {
    super(ITag.TYPE_SPEECH, id, required, start, end);

    text = theText;
  }

  /**
   * Access to the speech text.
   * 
   * @return the text of the speech
   */
  public final String getText() {
    return text;
  }
  
  /**
   * Sets the text, which should the avatar speak.
   * 
   * @param newText the new text for the avatar output
   */
  public final void setText(final String newText) {
    text = newText;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String toString() {
    StringBuilder builder = new StringBuilder();

    builder.append("<speech id=\""
                   + getID()
                   + "\" start=\""
                   + getStart()
                   + "\" end=\""
                   + getEnd()
                   + "\">");
    builder.append("<text>");
    builder.append(text);
    builder.append("</text>");
    builder.append("</speech>");

    return builder.toString();
  }
}
