/**
 * 
 */
package org.jvoicexml.mmi.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Provides access to common attributes of MMI responses.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6

 */
public class CommonResponseAttributeAdapter extends CommonAttributeAdapter {
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
