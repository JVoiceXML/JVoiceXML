package org.jvoicexml.voicexmlunit.demo;


import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Test;

import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.voicexmlunit.Voice;
import org.jvoicexml.xml.ssml.SsmlDocument;

public final class TestSessionDemo implements TextListener {

  private static final int PORT = 4243;

  private static final long MAX_WAIT = 1000;

  private Voice voice;

  private TextServer server;

  private String result;

  private URI dialog;

  private String input;

  private boolean failFirstOutput;

  @Before
    public void setUp() throws InterruptedException {
      voice = new Voice();
      dialog = new File("etc/input.vxml").toURI();

      server = new TextServer(PORT);
      server.addTextListener(this);
      server.start();

      synchronized (server) {
        server.wait(MAX_WAIT);
      }
    }

  @After
  public void tearDown() {
    server.stopServer();
  }

  @Test(timeout=10000)
    public void testInputYes() throws IOException {
    testInput("yes");
  }

  @Test(timeout=10000, expected=ComparisonFailure.class)
    public void testInputNo() throws IOException {
    boolean failed = false;
    testInput("no");
  }

  private void testInput(final String answer) throws IOException {
    input = answer;
    failFirstOutput = false;
    interpretDocument();

    final String expected = "You like this example.";
    Assert.assertEquals(expected, result);
  }


    private void interpretDocument() throws IOException {
      voice.call(server, dialog);
    }

  @Override
  public void started() {
    synchronized (server) {
      server.notifyAll();
    }
  }

  @Override
  public void connected(InetSocketAddress remote) {
    synchronized (server) {
      server.notifyAll();
    }
  }

  @Override
  public void outputSsml(SsmlDocument document) {
    result = document.getSpeak().getTextContent();
  }

  @Override
  public void expectingInput() {
    try {
      server.sendInput(input);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void inputClosed() {

  }

  @Override
  public void disconnected() {

  }
}
