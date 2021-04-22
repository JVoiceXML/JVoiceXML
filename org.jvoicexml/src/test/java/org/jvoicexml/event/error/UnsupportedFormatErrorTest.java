package org.jvoicexml.event.error;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link UnsupportedFormatError}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class UnsupportedFormatErrorTest {
    private static String EVENT_TYPE = UnsupportedElementError.EVENT_TYPE 
            + "." + UnsupportedFormatError.ELEMENT;

    @Test
    public void testUnsupportedFormatError() {
        final UnsupportedFormatError error = new UnsupportedFormatError();
        Assert.assertEquals(EVENT_TYPE, error.getEventType());
        final String message = error.getMessage();
        Assert.assertTrue(message.indexOf(EVENT_TYPE) >= 0);
    }

    @Test
    public void testUnsupportedFormatErrorString() {
        final String detail = "xyz is not supported";
        final UnsupportedFormatError error = new UnsupportedFormatError(detail);
        Assert.assertEquals(EVENT_TYPE, error.getEventType());
        final String message = error.getMessage();
        Assert.assertTrue(message.indexOf(EVENT_TYPE) >= 0);
        Assert.assertTrue(message.indexOf(detail) >= 0);
    }

    @Test
    public void testUnsupportedFormatErrorThrowable() {
        final Throwable throwable = new NullPointerException("dummy");
        final UnsupportedFormatError error =
                new UnsupportedFormatError(throwable);
        Assert.assertEquals(EVENT_TYPE, error.getEventType());
        final String message = error.getMessage();
        Assert.assertTrue(message.indexOf(EVENT_TYPE) >= 0);
        Assert.assertEquals(throwable, error.getCause());
    }

    @Test
    public void testUnsupportedFormatErrorStringThrowable() {
        final Throwable throwable = new NullPointerException("dummy");
        final String detail = "xyz is not supported";
        final UnsupportedFormatError error =
                new UnsupportedFormatError(detail, throwable);
        Assert.assertEquals(EVENT_TYPE, error.getEventType());
        final String message = error.getMessage();
        Assert.assertTrue(message.indexOf(EVENT_TYPE) >= 0);
        Assert.assertTrue(message.indexOf(detail) >= 0);
        Assert.assertEquals(throwable, error.getCause());
    }

}
