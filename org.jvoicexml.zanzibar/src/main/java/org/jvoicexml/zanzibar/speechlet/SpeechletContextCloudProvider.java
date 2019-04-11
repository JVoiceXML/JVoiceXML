package org.jvoicexml.zanzibar.speechlet;

import org.speechforge.cairo.rtp.server.RTPStreamReplicator;

import com.spokentech.speechdown.client.rtp.RtpTransmitter;

public interface SpeechletContextCloudProvider {


    public RTPStreamReplicator getRtpReplicator();

    public void setRtpReplicator(RTPStreamReplicator rtpReplicator);
    
	public RtpTransmitter getRtpTransmitter();

	public void setRtpTransmitter(RtpTransmitter rtpTransmitter);
	
	public void setUrl(String url);

}