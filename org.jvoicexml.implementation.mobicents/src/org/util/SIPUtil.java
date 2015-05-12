/*
 * Copyright (c) 1996-2001
 * shadowman Mobile Networks Limited
 * @author shadowman - vnxtele Mobile Networks - VAS
 * @version 1.0, 25 August 2009
 * All rights reserved.
 *
 * This software is distributed under shadowman Open Source License Version 1.0
 * ("Licence Agreement"). You shall use it and distribute only in accordance
 * with the terms of the License Agreement.
 *
 */
package org.util;

import org.apache.log4j.Logger;
import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.sip.message.Message;
import javax.sip.header.Header;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.servlet.sip.SipURI;

/**
 *
 * @author shadowman
 */
public class SIPUtil 
{
    private static final Logger LOGGER = Logger.getLogger(SIPUtil.class);
    public static AtomicInteger uniTXID = null;
    public static void initUniInteger(int value)
    {
        LOGGER.info("init AtomicInteger with value:"+value);
        uniTXID=new AtomicInteger(value);
    }
    
    public synchronized static int getUniInt()
    {
        if(uniTXID != null) return uniTXID.incrementAndGet();
        else
        {
            uniTXID=new AtomicInteger();
            return uniTXID.incrementAndGet();
        }
    }
    
     public static String uriRemovePort(String input)
    {
        try{
            if(input==null || input.isEmpty()==true) return "";
            int colonPos=input.indexOf(":");
            return input.substring(0,colonPos);
        }catch(Exception ex)
        {
            ExLog.exception(LOGGER, ex);
        }
        return "";
    }
     public static String getCaller(String sipuri)
    {
        try{
            if(sipuri==null || sipuri.isEmpty()==true) return "";
            int colonPos=sipuri.indexOf("@");
            return sipuri.substring(0,colonPos);
        }catch(Exception ex)
        {
            ExLog.exception(LOGGER, ex);
        }
        return "";
    }
     public static String getCallerNum(SipServletMessage message)
    {
        try{
            if(message==null) return "";
            SipURI from = (SipURI)message.getAddressHeader("From").getURI();
            if(from!=null) return  from.getUser();
        }catch(Exception ex)
        {
            ExLog.exception(LOGGER, ex);
        }
        return "";
    }
    public static String dumpSIPMsgHdr(SipServletMessage message)
    {
        String msgdsc="";
        try{
            if(message==null) return "";
            String fromUri = message.getFrom().getURI().toString();
            String toUri = message.getTo().getURI().toString();
            if(message instanceof SipServletRequest)
            {
                msgdsc ="SipServletRequest";
                return " method of "+msgdsc+ ": "+ message.getMethod() + " RequestURI:"
                        +((SipServletRequest)message).getRequestURI()
                    +" fromUri:"+fromUri+ " toUri:"+toUri;
            }
            else if (message instanceof SipServletResponse)
            {
                    msgdsc ="SipServletResponse";
                    return " status of "+msgdsc+ ": "+ ((SipServletResponse)message).getStatus() 
                            +" fromUri:"+fromUri+ " toUri:"+toUri;
            }
            else msgdsc ="unknow message";
        }catch(Exception ex)
        {
            ExLog.exception(LOGGER, ex);
        }
        return msgdsc;
    }
     public static String dumpSIPMsgHdr2(SipServletMessage message)
    {
        String msgdsc="";
        try{
            if(message==null) return "";
            String fromUri = message.getFrom().getURI().toString();
            String toUri = message.getTo().getURI().toString();
            if(message instanceof SipServletRequest)
            {
                msgdsc ="SipServletRequest";
                return " method of "+msgdsc+ ": "+ message.getMethod() + " RequestURI:"
                        +((SipServletRequest)message).getRequestURI()
                    +" fromUri:"+fromUri+ " toUri:"+toUri;
            }
            else if (message instanceof SipServletResponse)
            {
                    msgdsc ="SipServletResponse";
                    return " status of "+msgdsc+ ": "+ ((SipServletResponse)message).getStatus() 
                            +" fromUri:"+fromUri+ " toUri:"+toUri;
            }
            else msgdsc ="unknow message";
        }catch(Exception ex)
        {
            ExLog.exception(LOGGER, ex);
        }
        return msgdsc;
    }
    
    public static String dumpSIPMsgHdr(Message message)
    {
        String msgdsc="";
        try{
            if(message==null) return "";
            Header fromUri = message.getHeader("From");
            Header toUri = message.getHeader("To");
            if(message instanceof Request)
            {
                msgdsc ="SIP Request";
                return " method of "+msgdsc+ ": "+ ((Request)message).getMethod() + " RequestURI:"
                        +((Request)message).getRequestURI()
                    +" fromUri:"+fromUri+ " toUri:"+toUri;
            }
            else if (message instanceof Response)
            {
                    msgdsc ="SIP Response";
                    return " method of "+msgdsc+ ": statusCode: "+ ((Response)message).getStatusCode()
                            +" fromUri:"+fromUri+ " toUri:"+toUri;
            }
            else msgdsc ="unknow SIP message";
        }catch(Exception ex)
        {
            ExLog.exception(LOGGER, ex);
        }
        return msgdsc;
    }
    
    public static String dumpEventWrapper(Object event)
    {
        String msgdsc="";
        try{
            if(event==null) return "";
            if(event instanceof JainMgcpCommandEvent)
            {
                msgdsc =" JainMgcpCommandEvent";
                return msgdsc+ " txHandle:"+ ((JainMgcpCommandEvent)event).getTransactionHandle() + " ObjIden:"
                        +((JainMgcpCommandEvent)event).getObjectIdentifier();
                    
            }
            else if (event instanceof JainMgcpResponseEvent)
            {
                    msgdsc =" JainMgcpResponseEvent";
                      return msgdsc+" txHandle:"+ ((JainMgcpResponseEvent)event).getTransactionHandle() + " ObjIden:"
                        +((JainMgcpResponseEvent)event).getObjectIdentifier();
            }
            else msgdsc =" unknow MGCPEventWrapper";
        }catch(Exception ex)
        {
            ExLog.exception(LOGGER, ex);
        }
        return msgdsc;
    }
    
    public static int getMsgType(SipServletMessage message)
    {
        try{
            if(message==null) return -1;
            if(message instanceof SipServletRequest)
                return 1;
            else if (message instanceof SipServletResponse)
                return 2;
            else return -1;
        }catch(Exception ex)
        {
            ExLog.exception(LOGGER, ex);
        }
         return -1;
    }
    
    
    
     public static String getSIPMsgType(SipServletMessage message)
    {
        String msgtype="";
        try{
            if(message==null) return msgtype;
            if(message instanceof SipServletRequest)
                return message.getMethod();
            else if (message instanceof SipServletResponse)
                return Integer.toString(((SipServletResponse)message).getStatus());
            else return msgtype;
        }catch(Exception ex)
        {
            ExLog.exception(LOGGER, ex);
        }
         return msgtype;
    }
     
     
       public static void main(String[] args) 
        {
            try
            {
                System.out.println(SIPUtil.uriRemovePort("1473658@10.50.145.32:5065"));
                System.out.println(SIPUtil.getCaller("1473658@10.50.145.32:5065"));
                
            }catch(Exception ex)
            {
                ex.printStackTrace();
            }
    }
    
}
