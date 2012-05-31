package org.jvoicexml.mmi.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Adapter for common attributes of MMI events to make them accessible.
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public class CommonAttributeAdapter {
    /** The encapsulated MMI event. */
    private final MMIEvent event;

    /**
     * Constructs a nee object.
     * @param mmiEvent the event to investigate
     */
    public CommonAttributeAdapter(final MMIEvent mmiEvent) {
        event = mmiEvent;
    }

    /**
     * Retrieves the encapsulated event.
     * @return the event
     */
    protected MMIEvent getEvent() {
        return event;
    }

    /**
     * Retrieves the context attribute.
     * @return the context attribute
     */
    public String getContext() {
        Class<?> clazz = event.getClass();
        try {
            Method method = clazz.getDeclaredMethod("getContext");
            return (String) method.invoke(event);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (SecurityException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
    }

    /**
     * Retrieves the request id attribute.
     * @return the request id attribute
     */
    public String getRequestID() {
        Class<?> clazz = event.getClass();
        try {
            Method method = clazz.getDeclaredMethod("getRequestID");
            return (String) method.invoke(event);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (SecurityException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
    }

    /**
     * Retrieves the source attribute.
     * @return the source attribute
     */
    public String getSource() {
        Class<?> clazz = event.getClass();
        try {
            Method method = clazz.getDeclaredMethod("getSource");
            return (String) method.invoke(event);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (SecurityException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
    }

    /**
     * Retrieves the source attribute.
     * @param source the source attribute
     */
    public void setSource(final String source) {
        Class<?> clazz = event.getClass();
        try {
            Method method = clazz.getDeclaredMethod("setSource", String.class);
            method.invoke(event, source);
        } catch (NoSuchMethodException e) {
        } catch (SecurityException e) {
        } catch (IllegalAccessException e) {
        } catch (IllegalArgumentException e) {
        } catch (InvocationTargetException e) {
        }
    }

    /**
     * Retrieves the target attribute.
     * @return the target attribute
     */
    public String getTarget() {
        Class<?> clazz = event.getClass();
        try {
            Method method = clazz.getDeclaredMethod("getTarget");
            return (String) method.invoke(event);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (SecurityException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
    }

    /**
     * Retrieves the target attribute.
     * @param target the target attribute
     */
    public void setTarget(final String target) {
        Class<?> clazz = event.getClass();
        try {
            Method method = clazz.getDeclaredMethod("setTarget", String.class);
            method.invoke(event, target);
        } catch (NoSuchMethodException e) {
        } catch (SecurityException e) {
        } catch (IllegalAccessException e) {
        } catch (IllegalArgumentException e) {
        } catch (InvocationTargetException e) {
        }
    }
}
