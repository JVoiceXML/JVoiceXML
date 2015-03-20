/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.jvoicexml.implementation.mobicents.VAppCfg;
import org.mobicents.servlet.sip.restcomm.ServiceLocator;
import org.mobicents.servlet.sip.restcomm.callmanager.mgcp.MgcpCallManager;
import org.mobicents.servlet.sip.restcomm.callmanager.mgcp.MgcpCallTerminal;
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
    public static AtomicInteger totalOutcalls = new AtomicInteger(0);
    public static AtomicInteger failOutcalls = new AtomicInteger(0);
    public static AtomicInteger numberOutcalls = new AtomicInteger(0);
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
            VNXLog.error(ex);
        }
    }
    
    public void process() 
    {
        try
        {
            VNXLog.info("******************** broadcast statistics*************\n"
                    +" size of broadcastQueue:"+ broadcastQueue.size() + " \n"
                    +" totalOutcalls:"+ totalOutcalls.get() + " \n"
                    +" failOutcalls:"+ failOutcalls.get() + " \n"
                    +" listCurrentOutCalls:"+ MgcpCallManager.listCurrentOutCalls.size() + " \n"
                    +" outcall Rate:"+  numberOutcalls.getAndSet(0)*1000/VAppCfg.vbcRate
                    + " outcalls per second\n");
            checkExpiredCalls() ;
        }
        catch(Exception ex)
        {
            VNXLog.error(ex);
        }
    }
    
     public void checkExpiredCalls() 
    {
        Enumeration keys=null;
        try
        {
            if(MgcpCallManager.listCurrentOutCalls.size()==0 ) return;
            
            keys=MgcpCallManager.listCurrentOutCalls.elements();
            while(keys!=null &&keys.hasMoreElements())
            {
                MgcpCallTerminal call = (MgcpCallTerminal)keys.nextElement();
                if(call == null || call.brcastObj ==null || call.brcastObj.start_time==null) continue;
                //check time expired for error calls
                if(call.brcastObj.start_time.after
                        (new Timestamp(System.currentTimeMillis()+VAppCfg.expiredTime*1000)))
                {
                    VNXLog.error("** the expired call **"+ call);
                    MgcpCallManager.listCurrentOutCalls.remove(call);
                }
                
            }
        }
        catch(Exception ex)
        {
            VNXLog.error("with MgcpCallManager.listCurrentOutCalls:"+MgcpCallManager.listCurrentOutCalls
                    +" keys:"+keys);
            VNXLog.error(ex);
        }
    }
    
     private class VBCExe implements Runnable 
    {
         
        public void run() {
            try {
                while (true) {
                    try 
                    {
                        BroadcastObj brdobj =null;
                        
                        if (broadcastQueue.size() > 0) {
                            VNXLog.info("broadcastQueue size:" + broadcastQueue.size());
                            if (VAppCfg.vbcEnable == 1) 
                            {
                                if(VAppCfg.vbcEnaLimitConOutcalls==1 &&
                                        MgcpCallManager.listCurrentOutCalls .size()>VAppCfg.vbcConcurrentOutcallRate)
                                {
                                    VNXLog.warn("outcall rate is limited: vbcConcurrentOutcallRate:" 
                                            + VAppCfg.vbcConcurrentOutcallRate
                                            +" current outcall rate: "+MgcpCallManager.listCurrentOutCalls.size());
                                    Thread.sleep(VAppCfg.vbcRate);
                                    continue;
                                }
                                long polltime=System.currentTimeMillis();
                                brdobj = (BroadcastObj)broadcastQueue.poll();
                                if(brdobj==null || brdobj.status != BrcastObjState.QUEUE 
                                        || brdobj.error_code !=-1) continue;
                                VNXLog.info("polltime:" + (System.currentTimeMillis()-polltime)
                                        + " broadcastID:"+brdobj.broadcast_id);
                                Call call = null;
                                try 
                                {
                                    if (callManager == null) {
                                        VNXLog.error("callManager is not initialized");
                                        return;
                                    }
                                    call = callManager.createOutCall(brdobj);
                                    brdobj.start_time=new Timestamp(System.currentTimeMillis());
                                    numberOutcalls.incrementAndGet();
                                    if (call == null) {
                                        failOutcalls.incrementAndGet();
                                    } else {
                                        totalOutcalls.incrementAndGet();
                                    }
                                }
                                catch(final Exception exception) 
                                {
                                    VNXLog.error(" with "+brdobj);
                                    VNXLog.error(exception);
                                  
                                }
                            } else 
                            {
                                VNXLog.warn("disable voice broadcast mode, update device config message");
                                Thread.sleep(600000);
                            }
                            Thread.sleep(VAppCfg.vbcBrdcstRate);
                        }
                        Thread.sleep(VAppCfg.vbcBrdcstRate);
                    } 
                    catch (Exception ex) {
                        VNXLog.error(ex);
                    }
                }
            } catch (Exception ex) {
                VNXLog.error(ex);
            }
        }
    }
}
