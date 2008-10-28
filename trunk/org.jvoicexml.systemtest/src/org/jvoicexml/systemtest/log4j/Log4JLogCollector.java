package org.jvoicexml.systemtest.log4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.DenyAllFilter;
import org.jvoicexml.systemtest.LogCollector;

/**
 * There used log4j mechanism to collector log message.
 * 
 * @author lancer
 */
public abstract class Log4JLogCollector implements LogCollector {
    private List<String> acceptNames = new ArrayList<String>();

    private List<String> denyNames = new ArrayList<String>();

    private Appender appender = null;

    /**
     * create log4j appender with id
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

    public void setInterestName(String names) {
        acceptNames.addAll(stringToList(names));
    }

    public void setIgnoreName(List<String> names) {
        denyNames.addAll(names);
    }

    /**
     * split by ',', return Collection.
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
            return logName.startsWith(stringToMatch) ? Filter.DENY
                    : Filter.NEUTRAL;
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
            return logName.startsWith(stringToMatch) ? Filter.ACCEPT
                    : Filter.NEUTRAL;
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
