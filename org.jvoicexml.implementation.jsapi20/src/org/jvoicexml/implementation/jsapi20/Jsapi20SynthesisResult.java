package org.jvoicexml.implementation.jsapi20;

import org.jvoicexml.SpeakablePhoneInfo;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SynthesisResult;

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
public class Jsapi20SynthesisResult implements SynthesisResult {

  private final SpeakableText speakableText;
  private final SpeakablePhoneInfo[] speakablePhoneInfos;

  public Jsapi20SynthesisResult(SpeakableText speakableText, SpeakablePhoneInfo[] speakablePhoneInfos) {
    this.speakableText = speakableText;
    this.speakablePhoneInfos = speakablePhoneInfos;
  }

  /**
   * Returns the phones generated
   *
   * @return PhoneInfo[]
   */
  public SpeakablePhoneInfo[] getPhonesInfo() {
    return speakablePhoneInfos;
  }

  /**
   * Returns the speakable that produced this result
   *
   * @return SpeakableText
   */
  public SpeakableText getSpeakable() {
    return speakableText;
  }


}
