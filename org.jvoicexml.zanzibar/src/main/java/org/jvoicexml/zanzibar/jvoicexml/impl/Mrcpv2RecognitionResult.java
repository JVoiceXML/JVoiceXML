/*
 * File:    $RCSfile: Jsapi10RecognitionResult.java,v $
 * Version: $Revision: 1040 $
 * Date:    $Date: 2008-09-03 09:31:33 -0700 (Wed, 03 Sep 2008) $
 * Author:  $Author: davidjrodrigues $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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


package org.jvoicexml.zanzibar.jvoicexml.impl;

import org.apache.log4j.Logger;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.srgs.ModeType;

//import org.jvoicexml.xml.srgs.ModeType;


/**
 * Result of the recognition process.
 *
 * @author Spencer Lord
 * @version $Revision: 1040 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class Mrcpv2RecognitionResult implements RecognitionResult {
    
    private static final Logger _logger =  Logger.getLogger(Mrcpv2UserInput.class);

    /** The result returned by the recognizer. */
    private final org.speechforge.cairo.client.recog.RecognitionResult result;

    /** The name of the mark last executed by the SSML processor. */
    private String markname;

    /** The confidence of the result */
    private float confidenceResult = 0.0f;
    private String text = null;
    
    private String[] words = null;
    private float[] wconf = null;
    
    /**
     * Constructs a new object.
     * @param res The result returned by the recognizer.
     */
    public Mrcpv2RecognitionResult(final org.speechforge.cairo.client.recog.RecognitionResult res) {
        _logger.debug("constructing recog results "+ res);
        result = res;
        if (res!= null){
            text = result.getText();
            
            words =  result.getText().split(" ");
            wconf = new float[words.length];
    
            //TODO: get the real word confidences
            for (int i=0;i<wconf.length;i++) {
                wconf[i] = 1.0f;
            }
            
            confidenceResult = 1.0f;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getUtterance() {
        _logger.debug("getting utterance: "+text);
        return text;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAccepted() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRejected() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setMark(final String mark) {
        markname = mark;
    }

    /**
     * {@inheritDoc}
     */
    public String getMark() {
        return markname;
    }

    /**
     * {@inheritDoc}
     */
    public float getConfidence() {
        return confidenceResult;
    }

    /**
     * {@inheritDoc}
     */
    public float[] getWordsConfidence() {
       return wconf;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getWords() {
        _logger.debug("returning words: "+ words);
        return words;
    }

    public ModeType getMode() {
        // TODO Auto-generated method stub
        return ModeType.VOICE;
    }

	@Override
	public Object getSemanticInterpretation(DataModel model) throws SemanticError {
		// TODO Auto-generated method stub
		return null;
	}

}
