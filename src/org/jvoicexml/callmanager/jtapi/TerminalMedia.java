/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision: $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.callmanager.jtapi;

import net.sourceforge.gjtapi.media.GenericMediaService;
import javax.telephony.media.MediaResourceException;
import javax.telephony.media.RTC;
import java.util.Map;
import java.util.Dictionary;
import java.util.LinkedHashMap;
import java.net.URI;
import org.apache.log4j.Logger;

/**
 * Thread to process a media stream from a given URI.
 *
 * @author lyncher
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public abstract class TerminalMedia implements Runnable {

    /** Logger instance. */
    private static final Logger LOGGER = Logger.getLogger(TerminalMedia.class);

   /** Media service to stream the audio. */
   protected final GenericMediaService mediaService;

   private Thread thread = null;

   private boolean started = false;

   private boolean shouldProcess = false;

   private Object actionLock = new Object();

   private final LinkedHashMap<URI, Dictionary> uris;

   private boolean busy;

   /**
    * Constructs a new object.
    * @param service media service to stream the audio.
    * @param rtpUri the URI to play.
    */
   public TerminalMedia(final GenericMediaService service) {
       mediaService = service;
       busy = false;
       uris = new LinkedHashMap<URI, Dictionary>();
   }

   public void start() {
       thread = new Thread(this, "TerminalMedia");
       thread.setDaemon(true);
       started = true;
       startProcessing();
       thread.start();
   }

   public void stop() {
       started = false;
       synchronized (uris) {
           uris.notify();
       }
       thread = null;
   }

   /**
    *
    * @param uri URI
    * @param parameters Map
    *
    * @todo What happens if same URI is inserted twice? TRASH!
    */
   @SuppressWarnings("unchecked")
   public void processURI(URI uri, Map<String, String> parameters) {
       uris.put(uri, (Dictionary)parameters);
       synchronized (uris) {
           uris.notify();
       }

       if (started && shouldProcess)
           busy = true;
   }

   public void stopProcessing() {
       shouldProcess = false;
   }

   public void startProcessing() {
       shouldProcess = true;
       synchronized (actionLock) {
           actionLock.notify();
       }
   }

   public boolean isBusy() {
       return busy;
   }

   public abstract void process(URI uri, RTC[] rtc, Dictionary optargs) throws MediaResourceException;

   public abstract void onPostProcess();

   public abstract void onPreProcess();

   /**
    * {@inheritDoc}
    */
   public void run() {
       URI uri;
       Dictionary parameters;
       while (started) {
           busy = false;

           //Checks if processing should be done
           if (shouldProcess == false) {
               synchronized (actionLock) {
                   try {
                       actionLock.wait();
                   } catch (InterruptedException ex) {
                   }
               }
           }

           //Wait for an available MSC
           while ((started == true) && (uris.size() < 1)) {
               synchronized (uris) {
                   try {
                       uris.wait();
                   } catch (InterruptedException ex) {
                   }
               }
           }

           //Will do something now...
           busy = true;

           onPreProcess();

           //Get next URI and parameters
           uri = uris.keySet().iterator().next();
           parameters = uris.get(uri);
           uris.remove(uri);

           if (LOGGER.isDebugEnabled()) {
               LOGGER.debug("processing uri '" + uri + "'");
           }
           try {
               process(uri, null, parameters);
               busy = false;
           } catch (MediaResourceException ex) {
               LOGGER.error("error processing media from URI '" + uri + "'", ex);

               busy = false;
               onPostProcess();
               stopProcessing();
               stop();

               return;
           }

           onPostProcess();

           if (LOGGER.isDebugEnabled()) {
               LOGGER.debug("...done processing media from uri '" + uri + "'");
           }
       }
   }

}
