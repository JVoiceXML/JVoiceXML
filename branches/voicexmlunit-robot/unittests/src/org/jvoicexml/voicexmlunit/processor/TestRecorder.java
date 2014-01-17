/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jvoicexml.voicexmlunit.processor;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.voicexmlunit.io.Input;
import org.jvoicexml.voicexmlunit.io.Output;

/**
 *
 * @author raphael
 */
public class TestRecorder {
    
    Recorder recorder;
    
    public TestRecorder() {
        recorder = new Recorder(new Recording(null, null)); //null means mock
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testCaptureInput() throws InterruptedException {
        final Input input = new Input("success");
        recorder.capture(input);
        Assert.assertTrue(recorder.validate(input));
    }
    
    @Test
    public void testCaptureOutputWrong() throws InterruptedException {
        recorder.capture(new Output("success"));
        Assert.assertFalse(recorder.validate(new Output("fail")));
    }
    
    @Test
    public void testReplayInput() throws InterruptedException {
        final Input input = new Input("abc");
        recorder.capture(input);
        recorder.replay();
        Assert.assertFalse(recorder.validate(input));
    }
    
    @Test
    public void testReplayOutputWrong() throws InterruptedException {
        final Output output = new Output("xyz");
        recorder.capture(output);
        boolean failed = false;
        try {
            recorder.replay();
        } catch (AssertionError e) {
            failed = true;
        }
        Assert.assertTrue(failed);
    }
}
