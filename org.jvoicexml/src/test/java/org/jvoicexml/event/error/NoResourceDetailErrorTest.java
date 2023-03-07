package org.jvoicexml.event.error;

import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for {@link NoResourceDetailError}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class NoResourceDetailErrorTest {

    @Test
    public void testNoResourceDetailErrorString() {
        final NoResourceDetailError error = new NoResourceDetailError("dummy");
        Assert.assertEquals("error.noresource.dummy", error.getEventType());
    }

    @Test
    public void testNoResourceDetailErrorStringString() {
        final NoResourceDetailError error = new NoResourceDetailError("dummy", "dummyMessage");
        Assert.assertEquals("error.noresource.dummy", error.getEventType());
        Assert.assertTrue(error.getMessage().indexOf("dummyMessage") > 0);
    }

    @Test
    public void testNoResourceDetailErrorThrowableString() {
        final Throwable throwable = new NullPointerException("dummyMessage");
        final String detail = "dummy";
        final NoResourceDetailError error =
                new NoResourceDetailError(detail, throwable);
        final String message = error.getMessage();
        Assert.assertTrue(message.indexOf(detail) >= 0);
        Assert.assertEquals(throwable, error.getCause());
    }

    @Test
    public void testNoResourceDetailErrorStringStringThrowable() {
        final Throwable throwable = new NullPointerException("dummyMessage");
        final String detail = "dummy";
        final String message = "bad stuff";
        final NoResourceDetailError error =
                new NoResourceDetailError(detail, message, throwable);
        final String errorMessage = error.getMessage();
        Assert.assertTrue(errorMessage.indexOf(detail) >= 0);
        Assert.assertEquals(throwable, error.getCause());
    }

}
