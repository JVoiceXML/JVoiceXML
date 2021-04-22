package org.jvoicexml.event.error;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link UnsupportedLanguageError}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class UnsupportedLanguageErrorTest {
    private static String EVENT_TYPE = UnsupportedElementError.EVENT_TYPE 
            + "." + UnsupportedLanguageError.ELEMENT;

    @Test
    public void testUnsupportedLanguageError() {
        final UnsupportedLanguageError error = new UnsupportedLanguageError();
        Assert.assertEquals(EVENT_TYPE, error.getEventType());
        final String message = error.getMessage();
        Assert.assertTrue(message.indexOf(EVENT_TYPE) >= 0);
    }

    @Test
    public void testUnsupportedLanguageErrorString() {
        final String detail = "xyz is not supported";
        final UnsupportedLanguageError error = new UnsupportedLanguageError(detail);
        Assert.assertEquals(EVENT_TYPE, error.getEventType());
        final String message = error.getMessage();
        Assert.assertTrue(message.indexOf(EVENT_TYPE) >= 0);
        Assert.assertTrue(message.indexOf(detail) >= 0);
    }

    @Test
    public void testUnsupportedLanguageErrorThrowable() {
        final Throwable throwable = new NullPointerException("dummy");
        final UnsupportedLanguageError error =
                new UnsupportedLanguageError(throwable);
        Assert.assertEquals(EVENT_TYPE, error.getEventType());
        final String message = error.getMessage();
        Assert.assertTrue(message.indexOf(EVENT_TYPE) >= 0);
        Assert.assertEquals(throwable, error.getCause());
    }

    @Test
    public void testUnsupportedLanguageErrorStringThrowable() {
        final Throwable throwable = new NullPointerException("dummy");
        final String detail = "xyz is not supported";
        final UnsupportedLanguageError error =
                new UnsupportedLanguageError(detail, throwable);
        Assert.assertEquals(EVENT_TYPE, error.getEventType());
        final String message = error.getMessage();
        Assert.assertTrue(message.indexOf(EVENT_TYPE) >= 0);
        Assert.assertTrue(message.indexOf(detail) >= 0);
        Assert.assertEquals(throwable, error.getCause());
    }

}
