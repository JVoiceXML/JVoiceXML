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

package org.jvoicexml.mmi.events.xml;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Provides access to common attributes of MMI responses.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class CommonResponseAttributeAdapter
    extends CommonAttributeAdapter {
    /**
     * Constructs a new object.
     * @param response the response.
     */
    public CommonResponseAttributeAdapter(final MMIEvent response) {
        super(response);
    }

    /**
     * Retrieves the status attribute.
     * @return the status attribute
     */
    public StatusType getStatus() {
        final MMIEvent response = getEvent();
        Class<?> clazz = response.getClass();
        try {
            Method method = clazz.getDeclaredMethod("getStatus");
            return (StatusType) method.invoke(response);
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
     * Retrieves the status info attribute.
     * @return the status info attribute
     */
    public AnyComplexType getStatusInfo() {
        final MMIEvent response = getEvent();
        Class<?> clazz = response.getClass();
        try {
            Method method = clazz.getDeclaredMethod("getStatusInfo");
            return (AnyComplexType) method.invoke(response);
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
}
