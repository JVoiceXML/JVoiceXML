package org.jvoicexml.implementation.jsapi20;

import org.jvoicexml.SpeakablePhoneInfo;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SynthesisResult;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author Renato Cassaca
 * @version $Revision: 1375 $
 * @since 0.6
 */
public final class Jsapi20SynthesisResult implements SynthesisResult {

    private final SpeakableText speakableText;
    private final SpeakablePhoneInfo[] speakablePhoneInfos;

    /**
     * Constructs a new object.
     * @param speakable the speakable text.
     * @param phoneInfos related phone infos.
     */
    public Jsapi20SynthesisResult(final SpeakableText speakable,
            final SpeakablePhoneInfo[] phoneInfos) {
        speakableText = speakable;
        speakablePhoneInfos = phoneInfos;
    }

    /**
     * Returns the phones generated.
     *
     * @return PhoneInfo[]
     */
    public SpeakablePhoneInfo[] getPhonesInfo() {
        return speakablePhoneInfos;
    }

    /**
     * Returns the speakable that produced this result.
     *
     * @return SpeakableText
     */
    public SpeakableText getSpeakable() {
        return speakableText;
    }
}
