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
package org.jvoicexml.implementation.lightweightbml.utilities;

/**
 * Utility class for parsing methods.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public final class ParseUtils {
  /**
   * Error return value for the int parsing method, to signal the
   * value is not defined.
   */
  public static final int PARSE_ERROR_NOT_SET = -1;

  /**
   * Error return value for the integer parsing method, to signal the
   * string doesn't contain a integer value.
   */
  public static final int PARSE_ERROR_NO_INTEGER = -2;

  /**
   * Private declaration of the constructor for helper classes.
   */
  private ParseUtils() {
  }

  /**
   * Method to parse an int-value from string, for time reading in the executor.
   * 
   * @param str
   *          with int information
   * @return -1 for no time information, the value of the integer
   */
  public static int parseInt(final String str) {
    if (str.compareTo("-not set-") == 0) {
      return PARSE_ERROR_NOT_SET;
    } else {
      try {
        return Integer.parseInt(str);
      } catch (NumberFormatException exc) {
        return PARSE_ERROR_NO_INTEGER;
      }
    }
  }
}
