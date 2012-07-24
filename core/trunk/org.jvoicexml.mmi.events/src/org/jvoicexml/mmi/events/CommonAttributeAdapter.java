/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

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
    protected final MMIEvent getEvent() {
        return event;
    }

    /**
     * Retrieves the context attribute.
     * @return the context attribute
     */
    public final String getContext() {
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
    public final String getRequestID() {
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
    public final String getSource() {
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
    public final void setSource(final String source) {
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
    public final String getTarget() {
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
    public final void setTarget(final String target) {
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
