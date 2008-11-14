/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jvoicexml.systemtest.log4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.DenyAllFilter;
import org.apache.log4j.varia.LevelRangeFilter;
import org.jvoicexml.systemtest.LogSnoop;

/**
 * There used log4j mechanism to collector log message.
 *
 * @author lancer
 */
public abstract class Log4JSnoop implements LogSnoop {
    /**
     * DEBUG level name.
     */
    private static final String DEBUG = "debug";
    /**
     * INFO level name.
     */
    private static final String INFO = "info";
    /**
     * WARN level name.
     */
    private static final String WARN = "warn";
    /**
     * ERROR level name.
     */
    private static final String ERROR = "error";
    
    /**
     * FATAL level name.
     */
    private static final String FATAL = "fatal";

    /**
     * DEBUG level name.
     */
    private List<String> acceptNames = new ArrayList<String>();

    /**
     * DEBUG level name
     */
    private List< String > denyNames = new ArrayList<String>();

    /**
     * log level or high to collect.
     */
    private String logLevel = null;

    /**
     * the log appender.
     */
    private Appender appender = null;

    /**
     * create log4j appender with id
     * 
     * @param id
     * @return
     */
    protected abstract Appender createAppender(String id);

    /*
     * (non-Javadoc)
     * 
     * @see org.jvoicexml.systemtest.report.LogCollector#start(java.lang.String)
     */
    public synchronized void start(String name) {

        appender = createAppender(name);

        /* add level filter. */
        if (logLevel != null) {
            Level minLevel = null;
            if (DEBUG.equals(logLevel)) {
                minLevel = Level.DEBUG;
            } else if (INFO.equals(logLevel)) {
                minLevel = Level.INFO;
            } else if (WARN.equals(logLevel)) {
                minLevel = Level.WARN;
            } else if (ERROR.equals(logLevel)) {
                minLevel = Level.ERROR;
            } else if (FATAL.equals(logLevel)) {
                minLevel = Level.FATAL;
            } else {
                minLevel = Level.ERROR;
            }

            System.out.println("log level = " + minLevel);

            LevelRangeFilter levelFilter = new LevelRangeFilter();
            levelFilter.setLevelMax(Level.OFF);
            levelFilter.setLevelMin(minLevel);
            levelFilter.setAcceptOnMatch(false);
            appender.addFilter(levelFilter);
        }

        /* add deny filter. */
        for (String pattern : denyNames) {
            LogNameDenyFilter f = new LogNameDenyFilter();
            f.setStringToMatch(pattern);
            appender.addFilter(f);
        }

        /* add accept filter. */
        for (String pattern : acceptNames) {
            LogNameAcceptFilter f = new LogNameAcceptFilter();
            f.setStringToMatch(pattern);
            appender.addFilter(f);
        }

        /* deny other. */
        appender.addFilter(new DenyAllFilter());

        Logger logger = Logger.getRootLogger();
        logger.addAppender(appender);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jvoicexml.systemtest.report.LogCollector#stop()
     */
    public synchronized void stop() {

        if (appender != null) {
            Logger root = Logger.getRootLogger();
            root.removeAppender(appender);
            appender.close();
        }
    }

    /**
     * the log name you want to collect.
     * @param names
     */
    public void setInterestName(String names) {
        acceptNames.addAll(stringToList(names));
    }

    /**
     * the log name you want not to collect.
     * @param names
     */
    public void setIgnoreName(List<String> names) {
        denyNames.addAll(names);
    }

    /**
     * names split by ',', return Collection of name.
     *
     * @param names
     * @return
     */
    private Collection<String> stringToList(String names) {
        List<String> list = new ArrayList<String>();
        String[] words = names.split(",");
        for (String s : words) {
            list.add(s.trim());
        }
        return list;
    }

    /**
     * the log level name, you want to collect from it to high level.
     * @param level
     */
    public void setLogLevel(String level) {
        this.logLevel = level.toLowerCase().trim();
    }
}

/**
 * Name Deny Filter.
 * @author lancer
 */
final class LogNameDenyFilter extends Filter {

    /**
     * name to match.
     */
    private String stringToMatch = null;

    /**
     *  (non-Javadoc)
     * @see org.apache.log4j.spi.Filter#decide(LoggingEvent)
     */
    @Override
    public int decide(final LoggingEvent arg0) {
        String logName = arg0.getLoggerName();
        if (stringToMatch != null && logName.startsWith(stringToMatch)) {
            return  Filter.DENY;
        } else {
            return Filter.NEUTRAL;
        }
    }

    /**
     * set name to match.
     * @param name
     */
    public void setStringToMatch(final String name) {
        stringToMatch = name;
    }
}

/**
 * Name Accept Filter.
 * @author lancer
 */
final class LogNameAcceptFilter extends Filter {

    /**
     * name to match.
     */
    private String stringToMatch = null;

    /**
     *  (non-Javadoc)
     * @see org.apache.log4j.spi.Filter#decide(LoggingEvent)
     */
    @Override
    public int decide(final LoggingEvent arg0) {
        String logName = arg0.getLoggerName();
        if (stringToMatch != null && logName.startsWith(stringToMatch)) {
            return Filter.ACCEPT;
        } else {
            return Filter.NEUTRAL;
        }
    }

    /**
     * set name to match.
     * @param name
     */
    public void setStringToMatch(final String name) {
        stringToMatch = name;
    }
}
