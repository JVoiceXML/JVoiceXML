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

import org.apache.log4j.Logger;
import org.jvoicexml.CallControl;
import org.jvoicexml.CallControlProperties;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.DtmfInput;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.Session;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.EventBus;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.event.plain.implementation.NomatchEvent;
import org.jvoicexml.event.plain.implementation.RecognitionEvent;
import org.jvoicexml.xml.vxml.BargeInType;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.client.SpeechEventListener;


public final class MrcpImplementationPlatform implements SpeechEventListener, ImplementationPlatform {
    //SystemOutputListener, ,UserInputListener

    private static final Logger _logger = Logger.getLogger(MrcpImplementationPlatform.class);

    /** The system output device. */
    private SystemOutput output;

    /** Support for audio input. */
    private UserInput input;

    /** The calling device. */
    private CallControl call;

    /** The event observer to communicate events back to the interpreter. */
    private EventBus eventObserver;


    /** The name of the mark last executed by the SSML processor. */
    private String markname;

    /** Number of active output message, i.e. synthesized text. */
    private int activeOutputCount;
    
    /** Number of active input requests */
    private int activeInputCount;

    /** the speech client object that uses the speech server via mrcp */
    SpeechClient mrcpClient;



    public  SystemOutput getSystemOutput() throws NoresourceError {
        return output;
    }

    
    public UserInput getUserInput() throws NoresourceError {
        return input;
    }


    public  CallControl getCallControl() throws NoresourceError {
        return call;
    }


    public void close() {
        _logger.debug("MrcpIImplementationPlatform.close called.  Not implemented.");
    }

    /**
     * @return the mrcpClient
     */
    public SpeechClient getMrcpClient() {
        return mrcpClient;
    }

    /**
     * @param mrcpClient the mrcpClient to set
     */
    public void setMrcpClient(SpeechClient mrcpClient) {
        this.mrcpClient = mrcpClient;
        mrcpClient.addListener(this);
        output = new Mrcpv2SystemOutput(mrcpClient);
        ((Mrcpv2SystemOutput) output).setImplementationPlatform(this);
        input = new Mrcpv2UserInput(mrcpClient);
        ((Mrcpv2UserInput)input).setImplementationPlatform(this);
        call = new DummyCallControl();
        activeInputCount=0;
        activeOutputCount=0;
    }   

    public void setEventHandler(final EventBus observer) {
        eventObserver = observer;
    }
    
    
    //Not going to implement these event handlers.  Will do events with mrcpEvents.  Will do same processing though
    public void speechStarted(final BargeInType type) {
        _logger.debug("MrcpIImplementationPlatform.speechstarted() called.  Not implemented.");
    }
    
    public void resultAccepted(final RecognitionResult result) {
        _logger.debug("accepted recognition '" + result.getUtterance() + "'");
        if (eventObserver != null) {
            result.setMark(markname);
            final RecognitionEvent recognitionEvent = new RecognitionEvent(null, markname, result);
            eventObserver.publish(recognitionEvent);
        }
        markname = null;
    }

    
    public void resultRejected(final RecognitionResult result) {
        _logger.debug("rejected recognition'" + result.getUtterance() + "'");
        if (eventObserver != null) {
            result.setMark(markname);
            final NomatchEvent noMatchEvent = new NomatchEvent(null, markname, result);
            eventObserver.publish(noMatchEvent);
        }
    }

    
    public void inputStarted() {
        ++activeInputCount;
        _logger.debug("input started: active input count: "+ activeInputCount);
    }


    public void inputEnded() {
        --activeInputCount;
        _logger.debug("input ended: active input count: "+ activeInputCount);
    }
    
    
    public void outputStarted() {
        ++activeOutputCount;
        _logger.debug("output started: active output count: "+ activeOutputCount);
    }


    public void outputEnded() {
        --activeOutputCount;
        _logger.debug("output ended: active output count: "+ activeOutputCount);
    }


    public void markerReached(final String mark) {
         _logger.debug("Reached mark '" + mark + "'");
         markname = mark;
    }

    
    //MRCP speech client event handlers


    public void speechSynthEventReceived(SpeechEventType event) {
        _logger.debug("got a synth event: "+event.toString());
       if (event == SpeechEventType.SPEAK_COMPLETE) {
           
            //keep track of the pending outputs
            outputEnded();
            //TODO: check if bargein is enabled
          /*  if (activeInputCount > 0) {
                try {
                    mrcpClient.sendStartInputTimersRequest();
                } catch (MrcpInvocationException e) {
                    _logger.warn("MRCPv2 Status Code "+ e.getResponse().getStatusCode());
                    _logger.warn(e, e);
                } catch (IOException e) {
                    _logger.warn(e, e);
                } catch (InterruptedException e) {
                    _logger.warn(e, e);
                }
            } */
        } 
        
    }


 
    public void recognitionEventReceived(SpeechEventType event, org.speechforge.cairo.client.recog.RecognitionResult r) {
        
        _logger.debug("got a recog event: "+event.toString());


        if (event == SpeechEventType.START_OF_INPUT) {    
            //TODO: check if bargein is enabled
            if (activeOutputCount > 0) {
                try {
                	//cancel any active outputs being synthesized now or in the servers queue)
                    output.cancelOutput(null);
                    
                    //reset the active count
                    activeOutputCount = 0;
                } catch (NoresourceError e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } 
            }

        } else if (event == SpeechEventType.RECOGNITION_COMPLETE) {
            
            //keep track of the pending inputs
            inputEnded();
            
            //TODO: get the cause, need to pass it in event as well...now that mrcp events are no longer passed this far up the stack
            //MrcpHeader completionCauseHeader = event.getHeader(MrcpHeaderName.COMPLETION_CAUSE);
            //CompletionCause completionCause = null;
            //try {
            //    completionCause = (CompletionCause) completionCauseHeader.getValueObject();
            //} catch (IllegalValueException e) {
            //    // TODO Auto-generated catch block
            //    e.printStackTrace();
            //}
            RecognitionResult result = new Mrcpv2RecognitionResult(r);
            //if (completionCause.getCauseCode() != 0 || r.isOutOfGrammar()) {
            if (r.isOutOfGrammar()) {
                resultRejected(result);
            } else {
               resultAccepted(result);
            }

        }
    }



    public void characterEventReceived(String c, DtmfEventType status) {
        // TODO Auto-generated method stub
        _logger.debug("Character Event! status= "+ status+" code= "+c);        
    }


    public CallControl borrowCallControl() throws NoresourceError {
        _logger.debug("borrowCallControl not implemented");
        return call;
    }


    public SystemOutput borrowSystemOutput() throws NoresourceError {
        // TODO Auto-generated method stub
        _logger.debug("borrowSystemOutput not implemented");
        return output;
    }


    public UserInput borrowUserInput() throws NoresourceError {
        // TODO Auto-generated method stub
        _logger.debug("borrowUserInput not implemented");
        return input;
    }


    public CallControl getBorrowedCallControl() {
        // TODO Auto-generated method stub
        _logger.debug("getBorrowedCallControl not implemented");
        return call;
    }


    public UserInput getBorrowedUserInput() {
        // TODO Auto-generated method stub
        _logger.debug("getBorrowedUserInput not implemented");
        return input;
    }


    public void returnCallControl(CallControl arg0) {
        _logger.debug("returnCallControl not implemented");
        // TODO Auto-generated method stub
        
    }


    public void returnSystemOutput(SystemOutput arg0) {
        _logger.debug("returnSystemOutput not implemented");
        // TODO Auto-generated method stub
        
    }


    public void returnUserInput(UserInput arg0) {
        _logger.debug("returnUserInput not implemented");
        // TODO Auto-generated method stub
        
    }


    public void setSession(Session arg0) {
        _logger.debug("setSession not implemented");
        // TODO Auto-generated method stub
        
    }


    public void waitOutputQueueEmpty() {
        _logger.warn("waitOutputQueueEmpty not implemented");
        // TODO Auto-generated method stub
        
    }


	public boolean hasUserInput() {
	    // TODO Auto-generated method stub
	    return false;
    }


	public void waitNonBargeInPlayed() {
	    // TODO Auto-generated method stub
	    
    }

	public boolean isInputBusy() {
		if (activeInputCount >0) {
			   return true;
		   } else {
			   return false;
		   }
	    }

	public boolean isOutputBusy() {
	   if (activeOutputCount >0) {
		   return true;
	   } else {
		   return false;
	   }
    }


	public void queuePrompt(SpeakableText arg0) {
		// TODO Auto-generated method stub
		
	}


	public void renderPrompts(DocumentServer arg0) throws BadFetchError,
			NoresourceError, ConnectionDisconnectHangupEvent {
		// TODO Auto-generated method stub
		
	}


	public void setPromptTimeout(long arg0) {
		// TODO Auto-generated method stub
		
	}


	public void renderPrompts(String arg0, DocumentServer arg1)
			throws BadFetchError, NoresourceError,
			ConnectionDisconnectHangupEvent {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void renderPrompts(String sessionId, DocumentServer server, CallControlProperties callProps)
			throws BadFetchError, NoresourceError, ConnectionDisconnectHangupEvent {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean isUserInputActive() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public DtmfInput getCharacterInput() throws NoresourceError, ConnectionDisconnectHangupEvent {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setEventBus(EventBus bus) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void startPromptQueuing(long timeout) {
		// TODO Auto-generated method stub
		
	}

}
