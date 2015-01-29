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
package org.jvoicexml.implementation.lightweightbml.parser;

import java.io.IOException;
import java.io.StringReader;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jvoicexml.implementation.lightweightbml.xmltags.After;
import org.jvoicexml.implementation.lightweightbml.xmltags.BML;
import org.jvoicexml.implementation.lightweightbml.xmltags.BlockProgress;
import org.jvoicexml.implementation.lightweightbml.xmltags.Constraint;
import org.jvoicexml.implementation.lightweightbml.xmltags.Gaze;
import org.jvoicexml.implementation.lightweightbml.xmltags.Gesture;
import org.jvoicexml.implementation.lightweightbml.xmltags.Pointing;
import org.jvoicexml.implementation.lightweightbml.xmltags.Speech;
import org.jvoicexml.implementation.lightweightbml.xmltags.Sync;
import org.jvoicexml.implementation.lightweightbml.xmltags.SyncPoint;
import org.jvoicexml.implementation.lightweightbml.xmltags.Synchronize;
import org.jvoicexml.implementation.lightweightbml.xmltags.Wait;
import org.jvoicexml.implementation.lightweightbml.xmltags.Warning;
import org.xml.sax.SAXException;

/**
 * Class to parse a string, which represents a bml-file,
 * to generate a bml-structure.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.7
 */
public class LightweightBMLParser {
//  /**
//   * JDOM specific classes to generate a xml-parser with xsd-validation.
//   */
//  private SchemaFactory schemaFactory;
//
//  /**
//   * JDOM specific classes to generate a xml-parser with xsd-validation.
//   */
//  private XMLReaderJDOMFactory xmlReaderFactory;
//
//  /**
//   * XML Schema declaration.
//   */
//  private Schema xmlSchema;

  /**
   * XML-Tree builder.
   */
  private SAXBuilder xmlBuilder;

  /**
   * BML Structure, which will be created.
   */
  private BML bmlTree;

  /**
   * Constructor to initize the interpreter.
   * 
   * @param xsdFileName
   *          filename to the bml xsd file
   * @throws SAXException
   *           occurs, when xsd schema cannot be loaded
   */
//  @SuppressWarnings("static-access")
  public LightweightBMLParser(final String xsdFileName) throws SAXException {
//    // Generate XSD Factory
//    schemaFactory =
//        schemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//
//    // Generate Schema
//    xmlSchema = schemaFactory.newSchema(new File(xsdFileName));
//
//    // Generate XML Reader Factory
//    xmlReaderFactory = new XMLReaderSchemaFactory(xmlSchema);

    // Generate XMLBuilder
    // XMLBuilder = new SAXBuilder(XMLReaderFactory);
    xmlBuilder = new SAXBuilder();
  }

  /**
   * scans the incoming stream for bml-string and interprets
   * the xml-tree for bml-data.
   * 
   * @param bmlString the string for parsing the bml tree
   * @return BML-Message with a list of bml information
   * @throws JDOMException xml-string is corrupted
   * @throws IOException cannot read from stream
   */
  public final BML generateBML(final String bmlString) throws JDOMException,
      IOException {
    // Build XML-Tree
    Document doc = xmlBuilder.build(new StringReader(bmlString.trim()));
    Element root = doc.getRootElement();

    // Generate BML From Document
    return generateBML(root);
  }

  /**
   * Generates a BML Structure from given xml root-element.
   * 
   * @param xmlRootElement
   *          bml/root tag/element of the xml-file
   * @return BML structure
   */
  protected final BML generateBML(final Element xmlRootElement) {
    // Generate Structure
    String character = xmlRootElement.getAttributeValue("character", "");
    String bmlID = xmlRootElement.getAttributeValue("id",
        "");
    bmlTree = new BML(character, bmlID);

    // Interpret BML-Tree
    for (Element element : xmlRootElement.getChildren()) {
      // Get the name of the element
      String elementName = element.getName();

      // Generate Elements
      if (elementName.compareTo("required") == 0) {
        generateRequired(element);
      } else if (elementName.compareTo("constraint") == 0) {
        generateConstraint(element, false);
      } else if (elementName.compareTo("warningFeedback") == 0) {
        generateWarning(element);
      } else if (elementName.compareTo("syncPointProgress") == 0) {
        generateSyncPoint(element);
      } else if (elementName.compareTo("blockProgress") == 0) {
        generateBlockProgress(element);
      } else {
        generateCommand(element, false);
      }
    }

    // return message
    return bmlTree;
  }

  /**
   * Reads the elements of the required element and
   * stores it in the bml structure.
   * 
   * @param requiredElement
   *          element with the name required
   */
  protected final void generateRequired(final Element requiredElement) {
    for (Element requiredSubelement : requiredElement.getChildren()) {
      // Get the name of the element
      String requiredElementName = requiredSubelement.getName();

      // Generate Elements
      if (requiredElementName.compareTo("constraint") == 0) {
        generateConstraint(requiredSubelement,
            true);
      } else {
        generateCommand(requiredSubelement, true);
      }
    }
  }

  /**
   * Reads the xml structures and extracts the information about the
   * constraints of the bml document.
   * 
   * @param xmlConstraintElement
   *            the element, which represents the constraints element
   * @param required is the execution of the constraint is required
   */
  protected final void generateConstraint(final Element xmlConstraintElement,
                                          final boolean required) {
    Constraint c = new Constraint(required);

    for (Element s : xmlConstraintElement.getChildren("synchronize")) {
      Synchronize synchronize = new Synchronize();

      for (Element sync : s.getChildren("sync")) {
        String ref = sync.getAttributeValue("ref", "");

        if (ref.compareTo("") != 0) {
          synchronize.getSyncPoints().add(new Sync(ref));
        }

      }

      c.getSynchronizes().add(synchronize);
    }

    for (Element a : xmlConstraintElement.getChildren("after")) {
      String ref = a.getAttributeValue("ref",
          "");

      After after = new After(ref);

      for (Element sync : a.getChildren("sync")) {
        String sref = sync.getAttributeValue("ref",
            "");

        if (ref.compareTo("") != 0) {
          after.getSyncPoints().add(new Sync(sref));
        }

      }

      c.getAfters().add(after);
    }

    bmlTree.getConstraints().add(c);
  }

  /**
   * Generates a warning elements, when during the parsing
   * process occurs some errors.
   * 
   * @param xmlWarningElement the element, which generates the error
   */
  protected final void generateWarning(final Element xmlWarningElement) {
    String id = xmlWarningElement.getAttributeValue("id", "");
    String character = xmlWarningElement.getAttributeValue("characterId", "");
    String type = xmlWarningElement.getAttributeValue("type", "");

    bmlTree.getWarnings().add(new Warning(id, character, type));
  }

  /**
   * Generates a sync poing element.
   * 
   * @param xmlSyncPointElement
   *                      the xml element, which represents a bml sync point
   */
  protected final void generateSyncPoint(final Element xmlSyncPointElement) {
    String id = xmlSyncPointElement.getAttributeValue("id", "");
    String time = xmlSyncPointElement.getAttributeValue("time", "");
    String globalTime =
        xmlSyncPointElement.getAttributeValue("globalTime", "");
    String character =
        xmlSyncPointElement.getAttributeValue("characterId", "");

    bmlTree.getSyncPoints().add(new SyncPoint(id, time, globalTime, character));
  }

  /**
   * Parses a xml tag to an block progress object.
   * 
   * @param xmlBlockProgressElement
   *                          the xml tag, which represents a block progress
   */
  protected final void generateBlockProgress(
      final Element xmlBlockProgressElement) {
    String id = xmlBlockProgressElement.getAttributeValue("id",
        "");
    String globalTime = xmlBlockProgressElement.getAttributeValue("globalTime",
        "");
    String character = xmlBlockProgressElement.getAttributeValue("characterId",
        "");

    BlockProgress bp = new BlockProgress(id, globalTime, character);
    bmlTree.getBlockProgresses().add(bp);
  }

  /**
   * reads the attributes of an xml-tag and creates a bml command.
   * 
   * @param xmlCommandElement
   *          bml-command element of the xml-tree
   * @param required
   *          flag, about the bml-command is required for execution
   */
  protected final void generateCommand(final Element xmlCommandElement,
                                       final boolean required) {
    // Read Basic Information
    String name = xmlCommandElement.getName();
    String id = xmlCommandElement.getAttributeValue("id",
        "");
    String start = xmlCommandElement.getAttributeValue("start",
        "-not set-");
    String end = xmlCommandElement.getAttributeValue("end",
        "-not set-");

    if (name.compareTo("wait") == 0) {
      // Read specific Data
      String duration = xmlCommandElement.getAttributeValue("duration",
          "-not set-");

      // Generate Command
      bmlTree.getCommands().add(new Wait(id, required, start, end, duration));
    } else if (name.compareTo("gesture") == 0) {
      // Read Specific Data
      String lexeme = xmlCommandElement.getAttributeValue("lexeme",
          "-unknown-");

      // Generate Command
      bmlTree.getCommands().add(new Gesture(id, required, start, end, lexeme));
    } else if (name.compareTo("speech") == 0) {
      // Read Specific Data
      String text = xmlCommandElement.getChild("text").getTextTrim();

      // Generate Command
      bmlTree.getCommands().add(new Speech(id, required, start, end, text));
    } else if (name.compareTo("pointing") == 0) {
      // Read specific data
      String mode = xmlCommandElement.getAttributeValue("mode",
          "");
      String target = xmlCommandElement.getAttributeValue("target",
          "");

      // Generate Command
      Pointing p = new Pointing(id, required, start, end, mode, target);
      bmlTree.getCommands().add(p);
    } else if (name.compareTo("gaze") == 0) {
      // Read specific data
      String mode = xmlCommandElement.getAttributeValue("mode",
          "");
      String target = xmlCommandElement.getAttributeValue("target",
          "");

      // Generate Command
      Gaze g = new Gaze(id, required, start, end, mode, target);
      bmlTree.getCommands().add(g);
    }
  }
}
