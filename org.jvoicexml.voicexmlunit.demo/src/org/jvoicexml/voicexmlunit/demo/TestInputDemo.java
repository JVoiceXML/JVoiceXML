package org.jvoicexml.voicexmlunit.demo;


import java.lang.AssertionError;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.jvoicexml.voicexmlunit.Call;
import org.jvoicexml.voicexmlunit.Conversation;
import org.jvoicexml.voicexmlunit.Supervisor;
import org.jvoicexml.voicexmlunit.Voice;
import org.jvoicexml.voicexmlunit.io.Output;
import org.jvoicexml.voicexmlunit.io.Input;

public class TestInputDemo {
  private Call call;
  private Supervisor supervisor;

  @Before
  public void setUp() throws Exception {
    call = new Call("etc/input.vxml");
    supervisor = new Supervisor();
  }

  @Test
  public void testInputYes() {
    createConversation("yes");
    supervisor.process();
  }

  @Test(expected=AssertionError.class)
  public void testInputNoFail() {
    createConversation("no");
    boolean failed = false;
    supervisor.process();
  }

  private void createConversation(String answer) {
    Conversation conversation = supervisor.init(call);
    conversation.add(new Output("Do you like this example?"));
    conversation.add(new Input(answer));
    conversation.add(new Output("You like this example."));
  }
}
