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
 * Creates different grammars for the jvoicexml browser
 * for the speech recognition.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.7
 */
public class GrammarCreator {

  /**
   * Head definition of a jvoicexml grammar file.
   */
  private static final String HEADER =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>%n"
          + "<grammar version=\"1.0\" root=\"%s\" xml:lang=\"de\"%n"
          + "    xmlns=\"http://www.w3.org/2001/06/grammar\" mode=\"voice\"%n"
          + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"%n"
          + "    xsi:schemaLocation=\"http://www.w3.org/2001/06/grammar%n"
          + "                        http://www.w3.org/TR/"
          + "speech-grammar/grammar.xsd\"%n"
          + "    scope = \"public\"%n"
          + "    tag-format=\"semantics/1.0\">%n";
  
  /**
   * Footer definition of a jvoicexml grammar file.
   */
  private static final String FOOTER = "</grammar>";
  
  /**
   * Open rule of the grammar file.
   */
  private static final String OPENRULE = "  <rule id=\"%s\" scope=\"public\">%n"
                                         + "%s"
                                         + "    <one-of>%n";
  
  /**
   * Close rule of the grammar file.
   */
  private static final String CLOSERULE = "    </one-of>%n"
                                          + "  </rule>%n";

  /**
   * Tag definition for the grammar file.
   */
  private static final String TAG = "\t\t<tag>%s</tag>%n";
  
  /**
   * String buffer to generate with it the grammar file.
   */
  private StringBuffer buffer;

  /**
   * Constructor to initize the grammar creator.
   */
  public GrammarCreator() {
    this.buffer = new StringBuffer();
  }

  /**
   * Adds the header to the file.
   * 
   * @param root root information
   */
  public final void addHeader(final String root) {
    buffer.append(String.format(HEADER,
        root));
  }

  /**
   * Opens a grammar rule.
   * 
   * @param name name of the rule
   */
  public final void openRule(final String name) {
    buffer.append(String.format(OPENRULE,
        name,
        ""));
  }

  /**
   * Opens a rul and sets a tag.
   * 
   * @param name the nae of the rule
   * @param tag the tag to set
   */
  public final void openRule(final String name,
      final String tag) {
    buffer.append(String.format(OPENRULE,
        name,
        String.format(TAG,
            tag)));
  }

  /**
   * Closes a rule.
   */
  public final void closeRule() {
    buffer.append(String.format(CLOSERULE));
  }

  /**
   * Adds an item to the file.
   * 
   * @param item the name of the item
   * @param tag the tag name
   */
  public final void addItem(final String item,
      final String tag) {
    buffer.append(String.format("\t\t\t<item>%s<tag>%s</tag></item>%n",
        item,
        tag));
  }

  /**
   * Adds a line to the file.
   * 
   * @param line the line
   */
  public final void addLine(final String line) {
    buffer.append(line);
  }

  /**
   * Adds the footer to the file.
   */
  public final void addFooter() {
    buffer.append(String.format(FOOTER));
  }

  /**
   * Adds a tag to the file.
   * 
   * @param tag the tag to add
   */
  public final void addTag(final String tag) {
    buffer.append(String.format(TAG,
        tag));
  }

  @Override
  public final String toString() {
    return buffer.toString();
  }

  /**
   * Resets the grammar creator.
   */
  public final void reset() {
    buffer = new StringBuffer();
  }
}
