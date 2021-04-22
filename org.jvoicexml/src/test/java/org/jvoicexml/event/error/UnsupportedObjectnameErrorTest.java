package org.jvoicexml.event.error;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link UnsupportedObjectnameError}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class UnsupportedObjectnameErrorTest {
    private static String EVENT_TYPE = UnsupportedElementError.EVENT_TYPE 
            + "." + UnsupportedObjectnameError.ELEMENT;

    @Test
    public void testUnsupportedObjectnameError() {
        final UnsupportedObjectnameError error = new UnsupportedObjectnameError();
        Assert.assertEquals(EVENT_TYPE, error.getEventType());
        final String message = error.getMessage();
        Assert.assertTrue(message.indexOf(EVENT_TYPE) >= 0);
    }

    @Test
    public void testUnsupportedObjectnameErrorString() {
        final String detail = "xyz is not supported";
        final UnsupportedObjectnameError error = new UnsupportedObjectnameError(detail);
        Assert.assertEquals(EVENT_TYPE, error.getEventType());
        final String message = error.getMessage();
        Assert.assertTrue(message.indexOf(EVENT_TYPE) >= 0);
        Assert.assertTrue(message.indexOf(detail) >= 0);
    }

    @Test
    public void testUnsupportedObjectnameErrorThrowable() {
        final Throwable throwable = new NullPointerException("dummy");
        final UnsupportedObjectnameError error =
                new UnsupportedObjectnameError(throwable);
        Assert.assertEquals(EVENT_TYPE, error.getEventType());
        final String message = error.getMessage();
        Assert.assertTrue(message.indexOf(EVENT_TYPE) >= 0);
        Assert.assertEquals(throwable, error.getCause());
    }

    @Test
    public void testUnsupportedObjectnameErrorStringThrowable() {
        final Throwable throwable = new NullPointerException("dummy");
        final String detail = "xyz is not supported";
        final UnsupportedObjectnameError error =
                new UnsupportedObjectnameError(detail, throwable);
        Assert.assertEquals(EVENT_TYPE, error.getEventType());
        final String message = error.getMessage();
        Assert.assertTrue(message.indexOf(EVENT_TYPE) >= 0);
        Assert.assertTrue(message.indexOf(detail) >= 0);
        Assert.assertEquals(throwable, error.getCause());
    }
}
