/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jvoicexml.voicexmlunit;

import org.jvoicexml.voicexmlunit.processor.Adapter;
import java.net.InetSocketAddress;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.voicexmlunit.processor.Connection;
import org.jvoicexml.voicexmlunit.processor.Recorder;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 *
 * @author raphael
 */
public class IVR implements Adapter, TextListener {
    
    private final Connection connection;
    
    /**
     * Constructor.
     * @param connection
     */
    public IVR(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public void prompt(Comparable pattern) {
        hears(pattern);
    }

    @Override
    public void hears(Comparable pattern) {
        plays(pattern);
    }

    public void plays(Comparable pattern) {
        hears(pattern);
    }

    public void input(char digit) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void input(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void input(char[] digits) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void says(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void enters(char[] digits) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void press(char digit) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void select(char digit) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void select(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void noinput() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void nomatch() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void hangup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void started() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void connected(InetSocketAddress remote) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void outputSsml(SsmlDocument document) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void expectingInput() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void inputClosed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void disconnected() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
