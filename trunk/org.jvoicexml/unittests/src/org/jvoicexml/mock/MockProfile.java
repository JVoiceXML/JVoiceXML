/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/unittests/src/org/jvoicexml/mock/MockRecognitionResult.java $
 * Version: $LastChangedRevision: 4080 $
 * Date:    $Date: 2013-12-17 09:46:17 +0100 (Tue, 17 Dec 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.mock;

import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.jvoicexml.Profile;
import org.jvoicexml.interpreter.SsmlParsingStrategyFactory;
import org.jvoicexml.interpreter.TagStrategyFactory;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.test.interpreter.tagstrategy.MockInitializationTagStrategyFactory;
import org.jvoicexml.test.interpreter.tagstrategy.MockTagStrategyFactory;

/**
 * A profile for test purposes.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
public class MockProfile implements Profile {

    public MockProfile() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "VoiceXML21";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(VoiceXmlInterpreterContext context) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminate(VoiceXmlInterpreterContext context) {
    }
    
    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public SsmlParsingStrategyFactory getSsmlParsingStrategyFactory() {
        // TODO Auto-generated method stub
        return null;
    }
}
