/*
 * Zanzibar - Open source speech application server.
 *
 * Copyright (C) 2008-2009 Spencer Lord 
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Contact: salord@users.sourceforge.net
 *
 */
package org.jvoicexml.zanzibar.jvoicexml.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.sound.sampled.AudioFormat;

import org.apache.log4j.Logger;
import org.jvoicexml.CallControl;
import org.jvoicexml.CallControlProperties;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.event.error.NoresourceError;

public final class DummyCallControl implements CallControl {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(DummyCallControl.class);

    /**
     * {@inheritDoc}
     */
    public void activate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("activating call...");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "dummy";
    }

    /**
     * {@inheritDoc}
     */
    public void open() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("passivating call...");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final ConnectionInformation client)
        throws IOException {
    }

    public AudioFormat getRecordingAudioFormat() {
        // TODO Auto-generated method stub
        return null;
    }

    public void play(SystemOutput arg0, Map<String, String> arg1) throws NoresourceError, IOException {
        // TODO Auto-generated method stub
        
    }

    public void record(UserInput arg0, Map<String, String> arg1) throws NoresourceError, IOException {
        // TODO Auto-generated method stub
        
    }

    public void startRecording(UserInput arg0, OutputStream arg1, Map<String, String> arg2) throws NoresourceError, IOException {
        // TODO Auto-generated method stub
        
    }

    public void stopPlay() throws NoresourceError {
        // TODO Auto-generated method stub
        
    }

    public void stopRecord() throws NoresourceError {
        // TODO Auto-generated method stub
        
    }

    public void transfer(String arg0) throws NoresourceError {
        // TODO Auto-generated method stub
        
    }

	@Override
	public void play(SystemOutput output, CallControlProperties props) throws NoresourceError, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void record(UserInput input, CallControlProperties props) throws NoresourceError, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startRecording(UserInput input, OutputStream stream, CallControlProperties props)
			throws NoresourceError, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isCallActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void hangup() {
		// TODO Auto-generated method stub
		
	}

    
}
