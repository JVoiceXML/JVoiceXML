package org.jvoicexml.implementation.jsapi20;

import org.jvoicexml.SpeakableText;
import javax.speech.synthesis.SpeakableEvent;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author Renato Cassaca
 * @version 1.0
 */
public class Jsapi20SpeakableText implements SpeakableText {

  //Synthersized text
  private final String text;

  public Jsapi20SpeakableText(SpeakableEvent speakableEvent) {
    text = speakableEvent.getTextInfo();
  }

  /**
   * Appends the given text to this speakable.
   *
   * @param text Text to be appended.
   * @return This object.
   * @todo Implement this org.jvoicexml.SpeakableText method
   */
  public SpeakableText appendSpeakableText(String text) {
    return this;
  }

  /**
   * Retrieves the text to be passed to the TTS Engine.
   *
   * @return Text to be spoken.
   * @todo Implement this org.jvoicexml.SpeakableText method
   */
  public String getSpeakableText() {
    return text;
  }

  /**
   * Checks if this speakable contains any text to be passed to the TTS engine.
   *
   * @return <code>true</code> if this speakable contains text.
   */
  public boolean isSpeakableTextEmpty() {
    return ((text == null || text.length() < 1) ? true : false);
  }
}
