package org.jvoicexml.systemtest.log4j;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.DenyAllFilter;
import org.jvoicexml.systemtest.LogCollector;

/**
 * do use log4j class at any class in this file. there used log4j mechanism. If
 * use it, will make dead loop.
 * 
 * @author lancer
 */
public abstract class Log4JLogCollector implements LogCollector {
    private List<String> acceptNames = new ArrayList<String>();

    private List<String> denyNames = new ArrayList<String>();

    private Appender appender = null;
    
    protected abstract Appender createAppender(String id);

    /* (non-Javadoc)
     * @see org.jvoicexml.systemtest.report.LogCollector#start(java.lang.String)
     */
    public synchronized void start(String name) {

        appender = createAppender(name);


        if (denyNames != null) {
            for (String pattern : denyNames) {
                LogNameDenyFilter f = new LogNameDenyFilter();
                f.setStringToMatch(pattern);
                appender.addFilter(f);
            }
        }
        if (acceptNames != null) {
            for (String pattern : acceptNames) {
                LogNameAcceptFilter f = new LogNameAcceptFilter();
                f.setStringToMatch(pattern);
                appender.addFilter(f);
            }
        }
        appender.addFilter(new DenyAllFilter());

        Logger logger = Logger.getRootLogger();
        logger.addAppender(appender);
    }

    /* (non-Javadoc)
     * @see org.jvoicexml.systemtest.report.LogCollector#stop()
     */
    public synchronized void stop() {

        if (appender != null) {
            Logger root = Logger.getRootLogger();
            root.removeAppender(appender);
            appender.close();
        }
    }

    public void setAcceptNames(List<String> names) {
        acceptNames.addAll(names);
    }

    public void addAcceptName(String name) {
        acceptNames.add(name);
    }

    public void setDenyNames(List<String> names) {
        denyNames.addAll(names);
    }
    
    public void addDenyName(String name) {
        denyNames.add(name);
    }
}

/**
 * @author lancer
 */
final class LogNameDenyFilter extends Filter {

    private String stringToMatch = null;

    @Override
    public int decide(LoggingEvent arg0) {
        String logName = arg0.getLoggerName();
        if (stringToMatch != null) {
            return logName.startsWith(stringToMatch) ? Filter.DENY : Filter.NEUTRAL;
        } else {
            return Filter.NEUTRAL;
        }
    }

    public void setStringToMatch(String name) {
        stringToMatch = name;
    }

    public String getStringToMatch() {
        return stringToMatch;
    }
}

/**
 * @author lancer
 */
final class LogNameAcceptFilter extends Filter {

    private String stringToMatch = null;

    @Override
    public int decide(LoggingEvent arg0) {
        String logName = arg0.getLoggerName();
        if (stringToMatch != null) {
            return logName.startsWith(stringToMatch) ? Filter.ACCEPT : Filter.NEUTRAL;
        } else {
            return Filter.NEUTRAL;
        }
    }

    public void setStringToMatch(String name) {
        stringToMatch = name;
    }

    public String getStringToMatch() {
        return stringToMatch;
    }

}
