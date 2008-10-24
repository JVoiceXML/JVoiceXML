package org.jvoicexml.systemtest.report;

import org.apache.log4j.Category;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Test;

public class LogNameFilterTest {

    @Test
    public void testDenyName() {
        LogNameDenyFilter filter = new LogNameDenyFilter();

        filter.setStringToMatch("org.jvoicexml.systemtest");

        LoggingEvent event = createLoggingEvent("org.jvoicexml.systemtest");

        Assert.assertEquals(Filter.DENY, filter.decide(event));

        event = createLoggingEvent("org.jvoicexml");

        Assert.assertEquals(Filter.NEUTRAL, filter.decide(event));
    }

    @Test
    public void testAcceptName() {
        LogNameAcceptFilter filter = new LogNameAcceptFilter();

        filter.setStringToMatch("org.jvoicexml");

        LoggingEvent event = createLoggingEvent("org.jvoicexml.interpreter");

        Assert.assertEquals(Filter.ACCEPT, filter.decide(event));

        event = createLoggingEvent("org.jvoicexml");

        Assert.assertEquals(Filter.ACCEPT, filter.decide(event));
    }

    LoggingEvent createLoggingEvent(String logName) {
        Category category = LogManager.getLogger(logName);
        LoggingEvent event = new LoggingEvent(logName, category, null, "", null);
        return event;
    }
}
