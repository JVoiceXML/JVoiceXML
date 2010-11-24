package org.jvoicexml;

import java.util.Collection;

/**
 * JVoiceXML configuration.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.4
 */
public interface Configuration {
    /**
     * Loads all objects extending the <code>baseClass</code> from all
     * files with the given configuration base.
     * @param <T> type of class to loads
     * @param baseClass base class of the return type.
     * @param root name of the root element.
     * @return list of objects extending with the given root.
     * @since 0.7
     */
    <T extends Object> Collection<T> loadObjects(final Class<T> baseClass,
            final String root);

    /**
     * Loads the object with the class defined by the given key.
     *
     * @param <T>
     *        Type of the object to load.
     * @param baseClass
     *        Base class of the return type.
     * @param key
     *        Key of the object to load.
     * @return Instance of the class, <code>null</code> if the
     *         object could not be loaded.
     */
    <T extends Object> T loadObject(final Class<T> baseClass, final String key);

    /**
     * Loads the object with the class.
     *
     * @param <T>
     *        Type of the object to load.
     * @param baseClass
     *        Base class of the return type.
     * @return Instance of the class, <code>null</code> if the
     *         object could not be loaded.
     * @since 0.7
     */
    <T extends Object> T loadObject(final Class<T> baseClass);

}