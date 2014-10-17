package org.jvoicexml.mock;

import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.jvoicexml.Profile;
import org.jvoicexml.interpreter.SsmlParsingStrategyFactory;
import org.jvoicexml.interpreter.TagStrategyFactory;
import org.jvoicexml.test.interpreter.tagstrategy.MockInitializationTagStrategyFactory;
import org.jvoicexml.test.interpreter.tagstrategy.MockTagStrategyFactory;

public class MockProfile implements Profile {

    public MockProfile() {
    }

    @Override
    public String getName() {
        return "VoiceXML21";
    }

    @Override
    public TagStrategyFactory getInitializationTagStrategyFactory() {
        try {
            return new MockInitializationTagStrategyFactory();
        } catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException | SecurityException
                | NoSuchMethodException | IllegalArgumentException
                | InvocationTargetException e) {
            Assert.fail(e.getMessage());
            return null;
        }
    }

    @Override
    public TagStrategyFactory getTagStrategyFactory() {
        try {
            return new MockTagStrategyFactory();
        } catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException | SecurityException
                | NoSuchMethodException | IllegalArgumentException
                | InvocationTargetException e) {
            Assert.fail(e.getMessage());
            return null;
        }
    }

    @Override
    public SsmlParsingStrategyFactory getSsmlParsingStrategyFactory() {
        // TODO Auto-generated method stub
        return null;
    }
}
