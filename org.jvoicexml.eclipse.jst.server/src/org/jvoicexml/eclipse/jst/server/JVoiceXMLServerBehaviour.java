package org.jvoicexml.eclipse.jst.server;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.jst.server.generic.core.internal.GenericServerBehaviour;

/**
 * Eclipse JST Server Behaviour for JVoiceXML 
 *
 * Basically just extends the Generic to allow for the pinging of the server
 * during start-up 
 * 
 * @author Aurelian Maga
 * @version 0.1
 *
 */

public class JVoiceXMLServerBehaviour extends GenericServerBehaviour{
	
	private JVoiceXMLPingThread ping;
	
	
	/**
	 * method used to stop the server.
	 * @param force : boolean to determine if the
	 * server must be force to shutdown or not
	 * @see org.eclipse.wst.server.core.model.
	 * ServerBehaviourDelegate#stop(boolean)
	 */
	public final void stop(final boolean force) {
		try {
			if (ping != null) {
				ping.stop();
				ping = null;
			}
			//always force the stop
			super.stop(true);
		} catch (Exception e) {
		}
	}
	protected void setupLaunch(ILaunch launch, String launchMode,
			IProgressMonitor monitor) throws CoreException {
		

		setServerState(IServer.STATE_STARTING);
		setMode(launchMode);
		ping = new JVoiceXMLPingThread(getServer(),this);
		
		ping.start();
		
	}
	
	public void setStarted() {
		setServerState(IServer.STATE_STARTED);
	}
}