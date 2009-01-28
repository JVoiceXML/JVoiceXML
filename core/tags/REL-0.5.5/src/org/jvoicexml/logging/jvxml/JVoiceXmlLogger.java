/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.logging.jvxml;

import org.jvoicexml.logging.Logger;
import org.apache.log4j.Level;


/**
 * Main class of the JVoiceXML logging. This is the default logging
 * implementation based on <a href="http://logging.apache.org/log4j">log4j</a>
 * and provides a wrapper around the logging with
 * JVoiceXML to hide the real logging implementation from the users.
 *
 * <p>The user should only have to place a logging configuration XML,
 * i.e. log4j.xml file into their <code>CLASSPATH</code>.</p>
 *
 * @author Shaun Childers
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 */
public final class JVoiceXmlLogger
        implements Logger {

    /** The encapsulated logger. */
    private org.apache.log4j.Logger log;

    /**
     * {@inheritDoc}
     */
    public void seed(final Class clazz) {
        log = org.apache.log4j.Logger.getLogger(clazz);
    }

    /**
     * {@inheritDoc}
     */
    public void info(final Object obj) {
        log.log(this.getClass().getName(), Level.INFO, obj, null);
    }

    /**
     * {@inheritDoc}
     */
    public void info(final Object obj, final Throwable throwable) {
        log.log(this.getClass().getName(), Level.INFO, obj, throwable);
    }

    /**
     * {@inheritDoc}
     */
    public void warn(final Object obj) {
        log.log(this.getClass().getName(), Level.WARN, obj, null);
    }

    /**
     * {@inheritDoc}
     */
    public void warn(final Object obj, final Throwable throwable) {
        log.log(this.getClass().getName(), Level.WARN, obj, throwable);
    }

    /**
     * {@inheritDoc}
     */
    public void debug(final Object obj) {
        log.log(this.getClass().getName(), Level.DEBUG, obj, null);
    }

    /**
     * {@inheritDoc}
     */
    public void debug(final Object obj, final Throwable throwable) {
        log.log(this.getClass().getName(), Level.DEBUG, obj, throwable);
    }

    /**
     * {@inheritDoc}
     */
    public void error(final Object obj) {
        log.log(this.getClass().getName(), Level.ERROR, obj, null);
    }

    /**
     * {@inheritDoc}
     */
    public void error(final Object obj, final Throwable throwable) {
        log.log(this.getClass().getName(), Level.ERROR, obj, throwable);
    }

    /**
     * {@inheritDoc}
     */
    public void fatal(final Object obj) {
        log.log(this.getClass().getName(), Level.FATAL, obj, null);
    }

    /**
     * {@inheritDoc}
     */
    public void fatal(final Object obj, final Throwable throwable) {
        log.log(this.getClass().getName(), Level.FATAL, obj, throwable);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }
}
