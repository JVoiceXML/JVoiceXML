package org.jvoicexml.interpreter.variables;

import org.mozilla.javascript.NativeArray;

/**
 * The last result.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
@SuppressWarnings("serial")
public class LastResultArray extends NativeArray {
    /**
     * Constructs a new object.
     * 
     * @param length
     *            length of this array
     */
    public LastResultArray(long length) {
        super(length);

        // define shortcut properties
        defineProperty(this, "utterance", LastResultArray.class, READONLY);
        defineProperty("confidence", LastResultArray.class, READONLY);
        defineProperty("inputmode", LastResultArray.class, READONLY);
        defineProperty("interpretation", LastResultArray.class, READONLY);
        defineProperty("words", LastResultArray.class, READONLY);
    }

    /**
     * Constructs a new object with initial values of the array
     * @param array the initial array
     */
    public LastResultArray(LastResultShadowVarContainer[] array) {
        super(array);

        // define shortcut properties
        defineProperty("utterance", LastResultArray.class, READONLY);
        defineProperty("confidence", LastResultArray.class, READONLY);
        defineProperty("inputmode", LastResultArray.class, READONLY);
        defineProperty("interpretation", LastResultArray.class, READONLY);
        defineProperty("words", LastResultArray.class, READONLY);
    }

    /**
     * Retrieves the first element of the array.
     * @return first element of the array, {@code null} if there is no array
     */
    private LastResultShadowVarContainer getLastResult() {
        if (isEmpty()) {
            return null;
        }
        return (LastResultShadowVarContainer) get(0);
    }

    /**
     * Retrieves the utterance.
     * 
     * @return the utterance.
     */
    public String getUtterance() {
        final LastResultShadowVarContainer lastresult = getLastResult();
        if (lastresult == null) {
            return null;
        }
        return lastresult.getUtterance();
    }

    /**
     * Retrieves the utterance.
     * 
     * @return the utterance.
     */
    public float getConfidence() {
        final LastResultShadowVarContainer lastresult = getLastResult();
        if (lastresult == null) {
            return 0.0f;
        }
        return lastresult.getConfidence();
    }

    /**
     * Retrieves the utterance.
     * 
     * @return the utterance.
     */
    public String getInputmode() {
        final LastResultShadowVarContainer lastresult = getLastResult();
        if (lastresult == null) {
            return null;
        }
        return lastresult.getInputmode();
    }

    /**
     * Retrieves the words.
     * 
     * @return the vector of words.
     * @since 0.7
     */
    public WordVarContainer[] getWords() {
        final LastResultShadowVarContainer lastresult = getLastResult();
        if (lastresult == null) {
            return null;
        }
        return lastresult.getWords();
    }

    /**
     * Retrieves the semantic interpretation.
     * 
     * @return the semantic interpretation
     * @since 0.7.2
     */
    public Object getInterpretation() {
        final LastResultShadowVarContainer lastresult = getLastResult();
        if (lastresult == null) {
            return null;
        }
        return lastresult.getInterpretation();
    }
}
