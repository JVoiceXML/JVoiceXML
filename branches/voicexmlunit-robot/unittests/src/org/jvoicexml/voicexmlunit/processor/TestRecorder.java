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
import org.jvoicexml.voicexmlunit.io.Statement;

/**
 *
 * @author raphael
 */
public class TestRecorder {
    
    Recorder recorder;
    
    public TestRecorder() {
        recorder = new Recorder();
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
    public void testPlaybackInput() throws InterruptedException {
        final Input input = new Input("abc");
        recorder.capture(input);
        Statement playback = recorder.playback();
        Assert.assertEquals(input, playback);
        Assert.assertFalse(recorder.validate(input));
    }
    
    @Test
    public void testPlaybackOutputWrong() throws InterruptedException {
        final Output output = new Output("xyz");
        recorder.capture(output);
        boolean failed = false;
        try {
            Statement playback = recorder.playback();
        } catch (AssertionError e) {
            failed = true;
        }
        Assert.assertTrue(failed);
    }
}
