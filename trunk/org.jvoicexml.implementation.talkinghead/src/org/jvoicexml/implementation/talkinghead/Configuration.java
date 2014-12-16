/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.talkinghead;

import java.io.File;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * Configuration class to load configurable data from a xml-file.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class Configuration {
  /**
   * Config of the Talking Head.
   */
  private Document configDocument;

  /**
   * Config XML-File Root Element.
   */
  private Element configRootElement;

  /**
   * Constructor, which loads the configuration from xml-file.
   * 
   * @param filename
   *          name & path of the config file
   * @throws JDOMException
   *           parsing error of the xml-file
   * @throws IOException
   *           cannot find file
   */
  public Configuration(final String filename)
      throws JDOMException, IOException {
    // Load the config xml
    configDocument = new SAXBuilder().build(new File(filename));
    configRootElement = configDocument.getRootElement();
  }

  /**
   * Getter for the avatar description in the config file.
   * 
   * @return the avatar element of the config-file
   */
  public final Element getAvatar() {
    return configRootElement.getChild("avatar");
  }

  /**
   * reads the input host-address information.
   * 
   * @param def
   *          defining default value, for non existing informations.
   * @return the input host address for incoming data
   */
  public final String getInputHost(final String def) {
    return getElementAttributeDefault(configRootElement, "input", "host", def);
  }

  /**
   * reads the output host-address information.
   * 
   * @param def
   *          defining default value, for non existing informations.
   * @return the output host address for feedback data
   */
  public final String getOutputHost(final String def) {
    return getElementAttributeDefault(configRootElement, "output", "host", def);
  }

  /**
   * reads the port value for the input host.
   * 
   * @param def
   *          default value
   * @return the port information for income host
   */
  public final int getInputPort(final int def) {
    String strPort =
        getElementAttributeDefault(configRootElement,
                                   "input",
                                   "port",
                                   new Integer(def).toString());

    try {
      return Integer.parseInt(strPort);
    } catch (NumberFormatException exc) {
      return def;
    }
  }

  /**
   * reads the port value for the output.
   * 
   * @param def
   *          default value
   * @return the port information for output connection
   */
  public final int getOutputPort(final int def) {
    String strPort =
        getElementAttributeDefault(configRootElement,
                                   "output",
                                   "port",
                                   new Integer(def).toString());

    try {
      return Integer.parseInt(strPort);
    } catch (NumberFormatException exc) {
      return def;
    }
  }

  /**
   * reads the buffer size for incoming messages.
   * 
   * @param def
   *          defined default value for the information
   * @return
   *          the buffer size, if the data isn't defined
   *          in the file, it returns def
   */
  public final int getInputBuffer(final int def) {
    String strPort =
        getElementAttributeDefault(configRootElement,
                                   "input",
                                   "buffer",
                                   new Integer(def).toString());

    try {
      return Integer.parseInt(strPort);
    } catch (NumberFormatException exc) {
      return def;
    }
  }

  /**
   * reads the buffer size for outgoing messages.
   * 
   * @param def
   *          defined default value for the information
   * @return the buffer size, if the data isn't defined
   *         in the file, it returns def
   */
  public final int getOutputBuffer(final int def) {
    String strPort =
        getElementAttributeDefault(configRootElement,
                                   "output",
                                   "buffer",
                                   new Integer(def).toString());

    try {
      return Integer.parseInt(strPort);
    } catch (NumberFormatException exc) {
      return def;
    }
  }

  /**
   * reads the path & filename for the xsd file.
   * 
   * @param def
   *          defined default value for the information
   * @return the path & filename of the xsd file
   */
  public final String getXSDFilename(final String def) {
    return getElementAttributeDefault(configRootElement,
                                      "xsddef",
                                      "filename",
                                      def);
  }

  /**
   * reads the MaryTTS server ip from file.
   * 
   * @param def
   *          default value for the ip
   * @return the address to the MaryTTS server
   */
  public final String getTTSIP(final String def) {
    return getElementAttributeDefault(configRootElement, "tts", "host", def);
  }

  /**
   * reads the MaryTTS server port from file.
   * 
   * @param def
   *          default value for the port
   * @return the port of the MaryTTS server
   */
  public final int getTTSPort(final int def) {
    String strPort =
        getElementAttributeDefault(configRootElement,
                                   "tts",
                                   "port",
                                   new Integer(def).toString());

    try {
      return Integer.parseInt(strPort);
    } catch (NumberFormatException exc) {
      return def;
    }
  }

  /**
   * reads the locale for speech generation by MaryTTS.
   * 
   * @param def
   *          default value
   * @return the locale definition for MaryTTS
   */
  public final String getTTSLocale(final String def) {
    return getElementAttributeDefault(configRootElement, "tts", "locale", def);
  }

  /**
   * reads the name of the voice, which should MaryTTS use.
   * 
   * @param def
   *          default value
   * @return the name of the voice, which should MaryTTS use
   */
  public final String getTTSVoice(final String def) {
    return getElementAttributeDefault(configRootElement, "tts", "voice", def);
  }

  /**
   * reads the a specific attribute from a specific
   * element or returns the default value.
   * 
   * @param root
   *          root element of the xml-tree
   * @param elementName
   *          name of the specific element
   * @param attributeName
   *          name of the attribute of the element
   * @param def
   *          default value, when the attribute or element isn't existing
   * @return the value of the attribute
   */
  private String getElementAttributeDefault(final Element root,
                                            final String elementName,
                                            final String attributeName,
                                            final String def) {
    Element element = root.getChild(elementName);
    if (element == null) {
      return def;
    }

    return element.getAttributeValue(attributeName, def);
  }

}
