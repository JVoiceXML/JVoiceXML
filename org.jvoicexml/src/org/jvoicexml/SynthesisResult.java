package org.jvoicexml;

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
public interface SynthesisResult {

  /**
   * Returns the speakable that produced this result
   *
   * @return SpeakableText
   */
  public SpeakableText getSpeakable();

  /**
   * Returns the phones generated
   *
   * @return PhoneInfo[]
   */
  public SpeakablePhoneInfo[] getPhonesInfo();
}
