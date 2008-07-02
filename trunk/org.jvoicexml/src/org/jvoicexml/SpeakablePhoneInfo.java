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
public final class SpeakablePhoneInfo {

  private final String phoneme;

  private final int duration;

  public SpeakablePhoneInfo(String phoneme, int duration) {
    this.phoneme = phoneme;
    this.duration = duration;
  }

  public String getPhoneme() {
    return phoneme;
  }

  public int getDuration() {
    return duration;
  }
}
