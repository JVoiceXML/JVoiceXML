/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.profile.vxml21.tagstrategy;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.mock.TestAppender;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Log;
import org.mockito.Mockito;

/**
 * This class provides a test case for the {@link LogStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public final class TestLogStrategy extends TagStrategyTestBase {
    /**
     * Initialize this test.
     */
    @BeforeClass
    public static void init() {
        final LoggerContext context = LoggerContext.getContext(false);
        final Configuration config = context.getConfiguration();
        final Appender appender = new TestAppender();
        appender.start();
        config.addAppender(appender);
        final Level level = null;
        final Filter filter = null;
        for (final LoggerConfig loggerConfig : config.getLoggers().values()) {
            loggerConfig.addAppender(appender, level, filter);
        }
        config.getRootLogger().addAppender(appender, level, filter);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.LogStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                test failed
     * @throws JVoiceXMLEvent
     *             test failed
     */
    @Test
    public void testExecute() throws Exception, JVoiceXMLEvent {
        final Block block = createBlock();
        final Log log = block.appendChild(Log.class);
        final String message = TestAppender.TEST_PREFIX
                + "this is a simple log test";
        log.addText(message);

        LogStrategy strategy = new LogStrategy();
        executeTagStrategy(log, strategy);
        Assert.assertTrue("message not found in appender",
                TestAppender.containsMessage(message));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.LogStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                test failed
     * @throws JVoiceXMLEvent
     *             test failed
     */
    @Test
    public void testExecuteExpr() throws Exception, JVoiceXMLEvent {
        final String var = "test";

        final Block block = createBlock();
        final Log log = block.appendChild(Log.class);
        final String expr = "'" + TestAppender.TEST_PREFIX + "actor is + "
                + var;
        log.setExpr(expr);

        final DataModel model = getDataModel();
        final String message = TestAppender.TEST_PREFIX
                + "actor is Horst Buchholz";
        Mockito.when(model.evaluateExpression(expr, Object.class)).thenReturn(
                message);
        LogStrategy strategy = new LogStrategy();
        executeTagStrategy(log, strategy);

        Assert.assertTrue("message not found in appender",
                TestAppender.containsMessage(message));
    }
}
