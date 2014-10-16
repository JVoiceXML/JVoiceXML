package org.jvoicexml.profile.vxml21;

import org.jvoicexml.Profile;
import org.jvoicexml.interpreter.TagStrategyFactory;

public class VoiceXml21Profile implements Profile {
    /** The initialization tag strategy factory. */
    private TagStrategyFactory initializationTagStrategyFactory;

    /** The tag strategy factory. */
    private TagStrategyFactory tagStrategyFactory;

    /**
     * Constructs a new object.
     */
    public VoiceXml21Profile() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "VoiceXML21";
    }

    /**
     * Sets the tag strategy factory.
     * 
     * @param factory
     *            the tag strategy factory
     */
    public void setInitializationTagStrategyFactory(
            final TagStrategyFactory factory) {
        initializationTagStrategyFactory = factory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TagStrategyFactory getInitializationTagStrategyFactory() {
        return initializationTagStrategyFactory;
    }

    /**
     * Sets the tag strategy factory.
     * 
     * @param factory
     *            the tag strategy factory
     */
    public void setTagStrategyFactory(final TagStrategyFactory factory) {
        tagStrategyFactory = factory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TagStrategyFactory getTagStrategyFactory() {
        return tagStrategyFactory;
    }

}
