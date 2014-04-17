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
public class TestTape {
    
    Tape recorder;
    
    public TestTape() {
        recorder = new Tape();
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void shouldCaptureInputAndPlaybackInput() 
            throws InterruptedException {
        final Input input = new Input("success");
        recorder.capture(input);
        Assert.assertEquals(input, recorder.playback());
        Assert.assertNull(recorder.playback()); // now empty
    }
    
    @Test
    public void shouldCaptureOutputWrong() throws InterruptedException {
        final String utterance = "success";
        recorder.capture(new Output(utterance));
        Assert.assertNotEquals(new Input(utterance), recorder.playback());
        Assert.assertNotEquals(new Input("fail"), recorder.playback());
    }
    
    @Test
    public void shouldCaptureOutputAndPlaybackOutput() 
            throws InterruptedException {
        final Input input = new Input("abc");
        recorder.capture(input);
        Statement playback = recorder.playback();
        Assert.assertEquals(input, playback);
        Assert.assertNull(recorder.playback()); // now empty
    }
    
    @Test
    public void shouldCaptureInputWrong() throws InterruptedException {
        final String utterance = "success";
        final Input i = new Input(utterance);
        recorder.capture(i);
        Assert.assertNotEquals(new Output(utterance), recorder.playback());
        recorder.capture(i);
        Assert.assertNotEquals(new Output("fail"), recorder.playback());
    }
}
