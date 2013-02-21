package org.jvoicexml.implementation.mobicents;
import javax.media.mscontrol.MediaEventListener;
import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.join.JoinEvent;
import javax.media.mscontrol.join.JoinEventListener;
import javax.media.mscontrol.join.Joinable.Direction;
import javax.media.mscontrol.mediagroup.MediaGroup;

import org.apache.log4j.Logger;

 public class MMSJoinEventListener implements JoinEventListener 
 {
     private static final Logger LOGGER = Logger.getLogger(MMSJoinEventListener.class);

	public void onEvent(javax.media.mscontrol.join.JoinEvent event) 
	{
		LOGGER.info("event:"+event+ " eventType:"+event.getEventType());
		MediaGroup mg = (MediaGroup) event.getThisJoinable();
		if (event.isSuccessful()) {

			if (JoinEvent.JOINED == event.getEventType()) {
				// NC Joined to MG

				
			} else if (JoinEvent.UNJOINED == event.getEventType()) {
				
			}

		} else {
			LOGGER.error("Joining of MG and NC failed");
		}
	}

}
