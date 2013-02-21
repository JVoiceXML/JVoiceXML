/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/demo/tags/0.7.5.GA/org.jvoicexml.demo.embedded/src/org/jvoicexml/demo/embedded/EmbeddedJVoiceXML.java $
 * Version: $LastChangedRevision: 2771 $
 * Date:    $Date: 2011-08-26 10:37:28 +0200 (Fr, 26 Aug 2011) $
 * Author:  $LastChangedBy: Shadowman $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */package org.jvoicexml.implementation.mobicents.broadcast;

import com.vnxtele.util.VNXLog;
import com.vnxtele.util.VProcThrd;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.jvoicexml.implementation.mobicents.VAppCfg;
import org.mobicents.servlet.sip.restcomm.ServiceLocator;
import org.mobicents.servlet.sip.restcomm.media.api.Call;
import org.mobicents.servlet.sip.restcomm.media.api.CallManager;

/**
 *
 * @author Shadowman
 */
public class VoiceBroadCastMng extends VProcThrd 
{
    private final CallManager callManager;
    public static ConcurrentLinkedQueue broadcastQueue= new ConcurrentLinkedQueue();
    /**
     * routing out calls to the GMSCs of providers
     */
    public static final ConcurrentHashMap<String,SubCpsAddress> calledPatternRouters = new ConcurrentHashMap();
    protected Executor broadcastExec;
    public VoiceBroadCastMng()
    {
        setName("VoiceBroadCastMng");
        final ServiceLocator services = ServiceLocator.getInstance();
        callManager = services.get(CallManager.class);

    }
    public void init()
    {
        try
        {
           Thread.sleep(VAppCfg.vbcDelayStartTime);
           VNXLog.info(getThreadName()+"  starting Voice Broadcast Manager ....rate:"+VAppCfg.vbcRate
                   + " vbcNumbThreadPool:"+VAppCfg.vbcNumbThreadPool);
           scheduleAtFixedRate(VAppCfg.vbcRate);
           VNXLog.info("start the threads for processing voice broad cast scenariors...");
           broadcastExec = Executors.newFixedThreadPool(VAppCfg.vbcNumbThreadPool);
           broadcastExec.execute(new VBCExe());
        } catch (Exception ex) {
            VNXLog.error2(ex);
        }
    }
    
    public void process() 
    {
        try
        {
            
        }
        catch(Exception ex)
        {
            VNXLog.error2(ex);
        }
    }
    
     private class VBCExe implements Runnable 
    {
        public void run() {
            try {
                while (true) {
                    try 
                    {
                        if (broadcastQueue.size() > 0) {
                            VNXLog.info2("broadcastQueue size:" + broadcastQueue.size());
                            if (VAppCfg.vbcEnable == 1) 
                            {
                                long polltime=System.currentTimeMillis();
                                BroadcastObj brdobj = (BroadcastObj)broadcastQueue.poll();
                                if(brdobj==null || brdobj.status != BrcastObjState.QUEUE) continue;
                                VNXLog.info2("polltime:" + (System.currentTimeMillis()-polltime));
                                Call call = null;
                                try 
                                {
                                    if(callManager==null)
                                    {
                                        VNXLog.error("callManager is not initialized");
                                        return;
                                    }
                                    call = callManager.createOutCall(brdobj); 
                                }
                                catch(final Exception exception) 
                                {
                                    VNXLog.error2(exception);
                                  
                                }
                            } else 
                            {
                                VNXLog.warn2("disable voice broadcast mode, update device config message");
                                Thread.sleep(600000);

                            }
                            Thread.sleep(VAppCfg.vbcBrdcstRate);
                        }
                    } 
                    catch (Exception ex) {
                        VNXLog.error2(ex);
                    }
                }
            } catch (Exception ex) {
                VNXLog.error2(ex);
            }
        }
    }
}
