package org.jvoicexml.voicexmlunit.demo;


import java.lang.AssertionError;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.jvoicexml.voicexmlunit.Call;
import org.jvoicexml.voicexmlunit.Conversation;
import org.jvoicexml.voicexmlunit.Supervisor;
import org.jvoicexml.voicexmlunit.Voice;

public class TestDtmfDemo {
  private Call call;
  private Supervisor supervisor;

  @Before
  public void setUp() throws Exception {
    call = new Call("etc/dtmf.vxml");
    supervisor = new Supervisor();
  }

  @Test
  public void testInputYes() {
    createConversation('1');
    supervisor.process();
  }

  @Test(expected=AssertionError.class)
  public void testInputNoFail() {
    createConversation('2');
    supervisor.process();
  }

  private void createConversation(char answer) {
    Conversation conversation = supervisor.init(call);
    conversation.addOutput("Do you like this example? Please enter 1 for yes or 2 for no");
    conversation.addDtmf(answer);
    conversation.addOutput("You like this example.");
  }
}
