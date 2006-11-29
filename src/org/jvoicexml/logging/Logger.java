/*
 * File:    $RCSfile: Logger.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.logging;


/**
 * Allows for users to implement their own logging mechanism.
 *
 * @author Shaun Childers
 *
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public interface Logger {
    /**
     * Seed the Logger implementation class with the class doing the
     * logging.
     * @param clazz The class to attach the logger to.
     */
    void seed(final Class clazz);

    /**
     * Log INFO messages.
     * @param obj The object to log, typically a java.lang.String.
     */
    void info(final Object obj);

    /**
     * Log INFO messages with the Throwable cause.
     * @param obj The object to log, typically a java.lang.String.
     * @param t The java.lang.Throwable object representing this exception.
     */
    void info(final Object obj, final Throwable t);

    /**
     * Log WARN messages.
     * @param obj The object to log, typically a java.lang.String.
     */
    void warn(final Object obj);

    /**
     * Log WARN messages with the Throwable cause.
     * @param obj The object to log, typically a java.lang.String.
     * @param t The java.lang.Throwable object representing this exception.
     */
    void warn(final Object obj, final Throwable t);

    /**
     * Log DEBUG messages.
     * @param obj The object to log, typically a java.lang.String.
     */
    void debug(final Object obj);

    /**
     * Log DEBUG messages with the Throwable cause.
     * @param obj The object to log, typically a java.lang.String.
     * @param t The java.lang.Throwable object representing this exception.
     */
    void debug(final Object obj, final Throwable t);

    /**
     * Log ERROR messages.
     * @param obj The object to log, typically a java.lang.String.
     */
    void error(final Object obj);

    /**
     * Log ERROR messages with the Throwable cause.
     * @param obj The object to log, typically a java.lang.String.
     * @param t The java.lang.Throwable object representing this exception.
     */
    void error(final Object obj, final Throwable t);

    /**
     * Log FATAL messages.
     * @param obj The object to log, typically a java.lang.String.
     */
    void fatal(final Object obj);

    /**
     * Log FATAL messages with the Throwable cause.
     * @param obj The object to log, typically a java.lang.String.
     * @param t The java.lang.Throwable object representing this exception.
     */
    void fatal(final Object obj, final Throwable t);

    /**
     * Inform the caller whether the level of logging is DEBUG.
     * @return True or Fals if debug is enabled.
     */
    boolean isDebugEnabled();

    /**
     * Inform the caller whether the level of logging is INFO.
     * @return True or Fals if INFO is enabled.
     */
    boolean isInfoEnabled();
}
