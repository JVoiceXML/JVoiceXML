package org.jvoicexml.processor.vxml3.resources.promptqueue;

import java.util.Map;

import org.jvoicexml.processor.vxml3.resources.Resource;

/**
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @created 07-Jun-2011 08:08:32
 */
public interface PromptQueueResource extends Resource, PromptDeviceListener {

    /**
     * Immediately cancels any prompt or fetch audio that is playing and clears
     * the queue.
     */
    void cancel();

    /**
     * Deletes any queued fetch audio. Also cancels any fetch audio that is
     * already playing, unless fetchAudioMin has been specified and not yet
     * reached.
     */
    void cancelFetchAudio();

    /**
     * Sets the value of paramName to newValue, which may be either an absolute
     * or relative value. The new setting takes effect immediately, even if
     * there is already a prompt playing.
     * 
     * @param paramName
     *            name of the parameter
     * @param newValue
     *            new value for the parameter
     */
    void changeParameter(final String paramName, final Object newValue);

    /**
     * Causes any queued prompts or fetch audio to be played.
     */
    void play();

    /**
     * Adds fetch audio to queue, removing any existing fetch audio from queue.
     * Does not cause it to be played.
     * 
     * @param prompt
     *            prompt to be played
     */
    void queueFetchAudio(final Object prompt);

    /**
     * Adds prompt to queue, but does not cause it to be played.
     * 
     * @param prompt
     *            prompt to be queued
     * @param properties
     *            properties
     */
    void queuePrompt(final Object prompt, final Map<?, ?> properties);

	/**
	 * Sent whenever a single prompt or piece of fetch audio finishes playing.
	 */
	public void playerDone();

}