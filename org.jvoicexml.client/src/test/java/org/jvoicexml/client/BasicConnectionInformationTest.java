package org.jvoicexml.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.SessionIdentifier;

public class BasicConnectionInformationTest {

    @Test
    public void testGetProfile() {
        final BasicConnectionInformation info = 
                new BasicConnectionInformation("call1", "output1", "input1");
        Assert.assertEquals("call1", info.getCallControl());
        Assert.assertEquals("VoiceXML21", info.getProfile());
        info.setProfile("profile1");
        Assert.assertEquals("profile1", info.getProfile());
    }

    @Test
    public void testGetCallControl() {
        final BasicConnectionInformation info = 
                new BasicConnectionInformation("call1", "output1", "input1");
        Assert.assertEquals("call1", info.getCallControl());
    }

    @Test
    public void testGetSystemOutput() {
        final BasicConnectionInformation info = 
                new BasicConnectionInformation("call1", "output1", "input1");
        Assert.assertEquals("output1", info.getSystemOutput());
    }

    @Test
    public void testGetUserInput() {
        final BasicConnectionInformation info = 
                new BasicConnectionInformation("call1", "output1", "input1");
        Assert.assertEquals("input1", info.getUserInput());
    }

    @Test
    public void testGetCalledDevice() throws URISyntaxException {
        final BasicConnectionInformation info = 
                new BasicConnectionInformation("call1", "output1", "input1");
        Assert.assertNull(info.getCalledDevice());
        final URI uri = new URI("calleddevice1");
        info.setCalledDevice(uri);
        Assert.assertEquals(uri, info.getCalledDevice());
    }

    @Test
    public void testGetCallingDevice() throws URISyntaxException {
        final BasicConnectionInformation info = 
                new BasicConnectionInformation("call1", "output1", "input1");
        Assert.assertNull(info.getCallingDevice());
        final URI uri = new URI("cakkingdevice1");
        info.setCallingDevice(uri);
        Assert.assertEquals(uri, info.getCallingDevice());
    }

    @Test
    public void testGetProtocolName() {
        final BasicConnectionInformation info = 
                new BasicConnectionInformation("call1", "output1", "input1");
        Assert.assertNull(info.getProtocolName());
        final String name = "name1";
        info.setProtocolName(name);
        Assert.assertEquals(name, info.getProtocolName());
    }

    @Test
    public void testGetProtocolVersion() {
        final BasicConnectionInformation info = 
                new BasicConnectionInformation("call1", "output1", "input1");
        Assert.assertNull(info.getProtocolVersion());
        final String version = "version1";
        info.setProtocolVersion(version);
        Assert.assertEquals(version, info.getProtocolVersion());
    }

    @Test
    public void testGetSessionIdentifier() {
        final BasicConnectionInformation info = 
                new BasicConnectionInformation("call1", "output1", "input1");
        Assert.assertNull(info.getSessionIdentifier());
        @SuppressWarnings("serial")
        final SessionIdentifier id = new SessionIdentifier() {
            
            @Override
            public String getId() {
                return "42";
            }
        };
        info.setSessionIdentifier(id);
        Assert.assertEquals(id, info.getSessionIdentifier());
    }

}
