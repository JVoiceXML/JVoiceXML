package org.jvoicexml.processor.vxml3.resources.promptqueue;

/**
 * The underlying player.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @updated 07-Jun-2011 08:39:06
 */
public interface PromptDevice {
    /**
     * Sent to platform to cause a single prompt to be played.
     * 
     * @param prompt the prompt to play
     */
    void play(Object prompt);

    /**
     * Sent at start of prompt play and whenever a new prompt or fetch audio is
     * played whose bargeinType differs from the preceding one.
     * 
     * @param paramName name of the parameter
     * @param value new value of the parameter.
     */
    public void setParameter(String paramName, Object value);

	/**
	 * 
	 * @param listener    The listener to add.
	 */
	public void addDeviceListener(PromptDeviceListener listener);

	/**
	 * 
	 * @param listener    the listener to remove
	 */
	public void removeDeviceListener(PromptDeviceListener listener);

}