/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jvoicexml.implementation.mobicents.broadcast;

import com.timesten.jdbc.TimesTenCallableStatement;
import com.timesten.jdbc.TimesTenTypes;
import com.vnxtele.oracle.DBCfg;
import com.vnxtele.oracle.DBManager;
import com.vnxtele.util.VDate;
import com.vnxtele.util.VNXLog;
import com.vnxtele.util.VProcThrd;
import java.sql.*;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipURI;
import org.jvoicexml.implementation.mobicents.VAppCfg;
import org.vnxtele.ivrgw.sip.utils.VIVRUtil;

/**
 *
 * @author Shadowman
 */
public class DBMng extends VProcThrd {

    private DBManager dbRAC = null;
    private String sqlCmd = "";
    private CallableStatement  csGetBroadcastObj=null;
    private CallableStatement updateBroadcastObjStatus=null;
    private CallableStatement updateBroadcastObjFromToURI=null;
    private CallableStatement updateBroadcastObjAnswer=null;
    private CallableStatement updateBroadcastObjComplete=null;
    private String oracfgFile = "conf/DBMemRAC.cfg";
    //
    public static ConcurrentLinkedQueue tempQueue= new ConcurrentLinkedQueue();
    //storing insert batch
    public static final ConcurrentLinkedQueue sipIncommingMesgsQueue = new ConcurrentLinkedQueue();
    public static final ConcurrentLinkedQueue sipOutgoingMesgsQueue = new ConcurrentLinkedQueue();
    //singleton pattern
    private static final class SingletonHolder 
    {
        private static final DBMng instance = new DBMng();
    }
    
    public static DBMng getInstance() {
        return SingletonHolder.instance;
    }
    public DBMng()
    {
        init();
        start();
    }

    public void init() {
        try {

            
            VNXLog.info(getThreadName() + "  starting db manager ");
            dbRAC = new DBManager("DBMemRACPOOL", oracfgFile);
            dbRAC.startPool();
            VNXLog.info("starting db interface:" + getThreadName() + " rate:"
                    + DBCfg.dbRate + "; ttUsed:" + DBCfg.ttUsed);
            scheduleAtFixedRate(DBCfg.dbRate);
            VNXLog.info("database connection pool is established");
            loadCalledRouting();
            loadBroadCastObject();
            procTempQueue();
        } catch (Exception ex) {
            VNXLog.error(ex);
        }
    }
     /**
     * inserting a sip message to the db 
     * @param direction :1 for incoming 2: for outgoing
     */
    public void insrSIPMsgs(SipServletMessage msg,int direction)
    {
        try{
            //setting stored time
            msg.setAttribute("stored_time",new Timestamp(System.currentTimeMillis()));
            VNXLog.info("storing the message"+VIVRUtil.dumpSIPMsgHdr(msg) );
            if(direction==VAppCfg.SIP_MSG_INCOMING)
                sipIncommingMesgsQueue.add(msg);
            else sipOutgoingMesgsQueue.add(msg);
        } catch (Exception ex)
        {
               VNXLog.error(ex);
        }
        
    }
    
    public void procSIPMsgQueues(ConcurrentLinkedQueue linkqueue,int direction)
    {
        PreparedStatement pstmt = null;
        if (linkqueue == null || linkqueue.size() == 0) 
        {
            return ;
        }
        else VNXLog.info("Insert batch SIP Messages:size of linkqueue:"+linkqueue.size() );
        String tablename="SIP_MESSAGES";
        
        String csSql = "INSERT INTO "+tablename+" (message_type, sip_message_type, callid, fromuri, touri,"+
                        "contact, raw_sip_message, stored_time, callernumber,callednumber,local_addr,remote_addr) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        String currentCallid="";
        Vector<SipServletMessage> vTemp=new Vector();
        Connection oracleConn=null;
        try
        {
            oracleConn=dbRAC.getOracleConNoRetry(new String("oracleConn"));
                if(oracleConn==null) return;
                oracleConn.setAutoCommit(false);
                pstmt = oracleConn.prepareStatement(csSql);
                int cnt=0;
                SipServletMessage sipmsg=null;
                while (!linkqueue.isEmpty())
                {
                    if(cnt>DBCfg.dbBatNumRecords) break;
                    else cnt++;
                    sipmsg = (SipServletMessage) linkqueue.poll();
                    //adding temp vector to remove later;
                    vTemp.add(sipmsg);
                    SipURI from = (SipURI)sipmsg.getAddressHeader("From").getURI();
                    SipURI to = (SipURI)sipmsg.getAddressHeader("To").getURI();
                    Address contact=sipmsg.getAddressHeader(VAppCfg.CONTACT_HEADER);
                    pstmt.setInt(1, direction);
                    pstmt.setString(2, VIVRUtil.getSIPMsgType(sipmsg));
                    pstmt.setString(3, sipmsg.getCallId());
                    pstmt.setString(4, from.toString());
                    pstmt.setString(5, to .toString());
                    pstmt.setString(6, contact==null?"":contact.toString());
                    pstmt.setString(7, sipmsg.toString());
                    pstmt.setTimestamp(8, (java.sql.Timestamp)sipmsg.getAttribute("stored_time"));
                    pstmt.setString(9,  from.getUser());
                    pstmt.setString(10, to.getUser());
                    pstmt.setString(11, sipmsg.getLocalAddr()+":"+sipmsg.getLocalPort());
                    pstmt.setString(12, sipmsg.getInitialRemoteAddr()+":"+sipmsg.getInitialRemotePort());
                    currentCallid=sipmsg.getCallId();
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                oracleConn.commit();
                pstmt.clearBatch();
                pstmt.close();
                vTemp.clear();
                VNXLog.info("Insert batch sucessully request into table "+tablename
                    + " :size of linkqueue:"+linkqueue.size());
        } catch (BatchUpdateException e)
        {
               try {
                   if(oracleConn!=null) oracleConn.rollback();
                   insertBackupTbl(vTemp);
                   VNXLog.error(e);
                   insertError(e.getMessage(),"exception:on batchUpdate incoming_messages currentCallid:"+currentCallid);
               } catch (Exception ex) {
                   VNXLog.error(ex);
               }
        } catch (Exception ex)
        {
               VNXLog.error(ex);
        }finally {
            if (pstmt != null) 
            {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    VNXLog.error(ex);
                }
            }

        }
    }
    
    public void insertBackupTbl(Vector vt) 
    {
        VNXLog.fatal("Insert backup table here, don't support yet : vt:"+vt);
        vt.clear();
    }
    
    
/**
 *
 * @param logmessage ORA-12801: error signaled in parallel query server P000
 * @param logcomment exception:chkPostPaidSub
 */
       public void insertError(String logmessage,String logcomment)
     {
         Statement stmt = null;
         Connection conError=null;
         try {
             //
             conError = dbRAC.getOracleConNoRetry("oracleConn");
             if (conError != null)
             {
                 stmt = conError.createStatement();
                 sqlCmd = "begin VWriteLog('"
                         +logmessage+"','"+logcomment+"'); END;";
                 VNXLog.info( sqlCmd);
                 stmt.executeQuery(sqlCmd);
             }
         } catch (SQLException ex)
         {
             VNXLog.error( ex);
         } catch (Exception ex) {
             VNXLog.error( ex);
         }
         finally
         {
             try {if(stmt !=null) stmt.close();}
             catch (Exception ex) { VNXLog.error( ex);}
         }
    }

   

    public void process() {
        try {
            //other functions
            loadBroadCastObject() ;
            procTempQueue();
            procSIPMsgQueues(sipIncommingMesgsQueue,VAppCfg.SIP_MSG_INCOMING);
            procSIPMsgQueues(sipOutgoingMesgsQueue,VAppCfg.SIP_MSG_OUTGOING);
            dbRAC.validateConnections();
        } catch (Exception ex) {
            VNXLog.error(getThreadName() + ":" + ex.toString());
        }
    }

    
    
    
    public void loadBroadCastObject() 
    {
        ResultSet cursor = null;
        VNXLog.info("sql command:" + VAppCfg.vbcSqlBroadcastCMD);
        Connection ttConLoadBroadcastObj=null;
        try 
        {
            //check queue is lower than limitation
            if(VoiceBroadCastMng.broadcastQueue.size()> VAppCfg.vbcLimitBroadcastQueueSize)
            {
                VNXLog.warn("remaining a lot of broadcast objects in the list. broadcast objects are no loaded..."
                        + " broadcastQueue:"+VoiceBroadCastMng.broadcastQueue.size()
                        + " vbcLimitBroadcastQueueSize:"+VAppCfg.vbcLimitBroadcastQueueSize
                        );
                return;
            }
            ttConLoadBroadcastObj = dbRAC.getTimeStenCon("ttConLoadBroadcastObj");
            if (ttConLoadBroadcastObj == null || DBCfg.ttUsed == 0 ) 
            {
                return;
            }
            if (csGetBroadcastObj == null || csGetBroadcastObj.isClosed() == true) {
                csGetBroadcastObj =
                        ttConLoadBroadcastObj.prepareCall(VAppCfg.vbcSqlBroadcastCMD);
            }
            synchronized (csGetBroadcastObj) 
            {
                // Step-5: register output parameters ...
                csGetBroadcastObj.registerOutParameter(1, TimesTenTypes.CURSOR);
                // Step-6: execute the stored procedures: proc3
                csGetBroadcastObj.execute();
                // Step-7: extract the output parameters
                // get parameter 2 as output
                cursor = ((TimesTenCallableStatement) csGetBroadcastObj).getCursor(1);
                // Use the cursor as you would any other ResultSet object.
                while (cursor != null && cursor.next()) 
                {
                    BroadcastObj brdcast= new BroadcastObj();
                    brdcast.broadcast_id=cursor.getInt("broadcast_id");
                    brdcast.caller = cursor.getString("caller");
                    brdcast.called = cursor.getString("called");
                    brdcast.schedule_time = cursor.getTimestamp("schedule_time");
                    brdcast.error_code = cursor.getInt("error_code");
                    brdcast.vxml_uri = cursor.getString("vxml_uri");
                    brdcast.status = BrcastObjState.valueOf(cursor.getString("status"));
                    brdcast.status = BrcastObjState.SELECT;
                    if(tempQueue.contains(brdcast)==false)
                    {
                        VNXLog.info(" adding a brdcast object to the temp queue: "+brdcast );
                        tempQueue.add(brdcast);
                    }
                    else
                        VNXLog.error(" the brdcast object is already loaded: "+brdcast );
                }
                if(cursor.getFetchSize()>0)
                {
                    VNXLog.info(" the temp queue size: "+tempQueue.size() +" getFetchSize:"+cursor.getFetchSize());
                }
            }
            csGetBroadcastObj.clearBatch();
            csGetBroadcastObj.clearParameters();
            cursor.close();
        } catch (SQLException ex) {
            VNXLog.error(ex);
        }
        catch (Exception ex) {
            VNXLog.error(ex);
        }
    }
    
    
    public void procTempQueue() {
        if (tempQueue.size() == 0) {
            return;
        }
        VNXLog.info("size of tempQueue:" + tempQueue.size());
        try {
            while (tempQueue.size() > 0) 
            {
                BroadcastObj brdcast = (BroadcastObj) tempQueue.poll();
                if (updateBrcastObjStatus(BrcastObjState.SELECT, brdcast.broadcast_id) == 0) {
                    brdcast.status = BrcastObjState.QUEUE;
                    VoiceBroadCastMng.broadcastQueue.add(brdcast);
                    VNXLog.info(" queued a brdcast object: " + brdcast);
                } else {
                    VNXLog.error(" cannot queue a brdcast object: " + brdcast);
                }
            }
        } catch (Exception ex) {
            VNXLog.error(ex);
        }
    }
    
    
    
    
     public int updateBrcastObjStatus(BrcastObjState status,int broadcastid) 
    {
        int result=-1;
        String sql=" begin updateBrcastObjStatus(:status,:BROADCAST_ID,:RESULT); end;";
        VNXLog.info("sql command:" + sql);
        Connection updateBrcastStatus = null;
        try {
            updateBrcastStatus= dbRAC.getTimeStenCon(new String("updateBrcastStatus"));
            if (updateBrcastStatus == null || DBCfg.ttUsed == 0) {
                return -1;
            }
            if (updateBroadcastObjStatus == null || updateBroadcastObjStatus.isClosed() == true) {
                updateBroadcastObjStatus =
                        updateBrcastStatus.prepareCall(sql);
            }
            synchronized (updateBroadcastObjStatus) {
                updateBroadcastObjStatus.setString(1,status.name());
                updateBroadcastObjStatus.setInt(2, broadcastid);
                updateBroadcastObjStatus.registerOutParameter(3, java.sql.Types.INTEGER);
                updateBroadcastObjStatus.execute();
                result=updateBroadcastObjStatus.getInt(3);
                return result;
            }
        } catch (SQLException ex) {
            VNXLog.error(ex);
        }
        catch (Exception ex) {
            VNXLog.error(ex);
        }
        return result;
    }
     
     public int updateBrcastObjAnswer(BrcastObjState status,int broadcastid,int errorcode) 
    {
        int result=-1;
        String sql=" begin UPDATE_BroadCAST_Answer(:BROADCAST_ID,:error_code,:status,:RESULT); end;";
        VNXLog.info("sql command:" + sql);
        Connection updateBrcastStatus = null;
        try {
            updateBrcastStatus= dbRAC.getTimeStenCon("updateBrcastStatus");
            if (updateBrcastStatus == null || DBCfg.ttUsed == 0) {
                return -1;
            }
            if (updateBroadcastObjAnswer == null || updateBroadcastObjAnswer.isClosed() == true) {
                updateBroadcastObjAnswer =
                        updateBrcastStatus.prepareCall(sql);
            }
            synchronized (updateBroadcastObjAnswer) {
                updateBroadcastObjAnswer.setInt(1, broadcastid);
                updateBroadcastObjAnswer.setInt(2, errorcode);
                updateBroadcastObjAnswer.setString(3,status.name());
                updateBroadcastObjAnswer.registerOutParameter(4, java.sql.Types.INTEGER);
                updateBroadcastObjAnswer.execute();
                result=updateBroadcastObjAnswer.getInt(4);
                return result;
            }
        } catch (SQLException ex) {
            VNXLog.error(ex);
        }
        catch (Exception ex) {
            VNXLog.error(ex);
        }
        return result;
    }
     
     
     public int updateBrcastObjComplete(BrcastObjState status,int broadcastid,int errorcode) 
    {
        int result=-1;
        String sql=" begin UPDATE_BroadCAST_complete(:BROADCAST_ID,:error_code,:status,:RESULT); end;";
        VNXLog.info("sql command:" + sql);
        Connection updateBrcastStatus = null;
        try {
            updateBrcastStatus= dbRAC.getTimeStenCon("updateBrcastStatus");
            if (updateBrcastStatus == null || DBCfg.ttUsed == 0) {
                return -1;
            }
            if (updateBroadcastObjComplete == null || updateBroadcastObjComplete.isClosed() == true) {
                updateBroadcastObjComplete =
                        updateBrcastStatus.prepareCall(sql);
            }
            synchronized (updateBroadcastObjComplete) {
                
                updateBroadcastObjComplete.setInt(1, broadcastid);
                updateBroadcastObjComplete.setInt(2, errorcode);
                updateBroadcastObjComplete.setString(3,status.name());
                updateBroadcastObjComplete.registerOutParameter(4, java.sql.Types.INTEGER);
                updateBroadcastObjComplete.execute();
                result=updateBroadcastObjComplete.getInt(4);
                return result;
            }
        } catch (SQLException ex) {
            VNXLog.error(ex);
        }
        catch (Exception ex) {
            VNXLog.error(ex);
        }
        return result;
    }
     
    
     
      public int updateBrcastObjSessidInvTime(BroadcastObj brdobj) 
    {
        int result=-1;
        String sql=" begin updateBrcastObjSessidInvTime(:broadcast_id,:callid,:from_uri,:to_uri,:status,:RESULT); end;";
        VNXLog.info("sql command:" + sql);
        Connection updateBrcastStatus = null;
        try {
            updateBrcastStatus= dbRAC.getTimeStenCon("updateBrcastStatus");
            if (updateBrcastStatus == null || DBCfg.ttUsed == 0) {
                return -1;
            }
            if (updateBroadcastObjFromToURI == null || updateBroadcastObjFromToURI.isClosed() == true) {
                updateBroadcastObjFromToURI =
                        updateBrcastStatus.prepareCall(sql);
            }
            synchronized (updateBroadcastObjFromToURI) {
                updateBroadcastObjFromToURI.setInt(1, brdobj.broadcast_id);
                updateBroadcastObjFromToURI.setString(2,brdobj.invite.getCallId());
                updateBroadcastObjFromToURI.setString(3,brdobj.from_uri.toString());
                updateBroadcastObjFromToURI.setString(4, brdobj.to_uri.toString());
                updateBroadcastObjFromToURI.setString(5, brdobj.status.name());
                updateBroadcastObjFromToURI.registerOutParameter(6, java.sql.Types.INTEGER);
                updateBroadcastObjFromToURI.execute();
                result=updateBroadcastObjFromToURI.getInt(6);
                return result;
            }
        } catch (SQLException ex) {
            VNXLog.error(ex);
        }
        catch (Exception ex) {
            VNXLog.error(ex);
        }
        return result;
    }

    public void loadCalledRouting() {
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT called_number_pattern, subcps_addr_router, cp_name,comments  FROM called_router where enable_router=1";
        VNXLog.info("sql command:" + sql);
        Connection oracleConn=null;
        try 
        {
            oracleConn=dbRAC.getOracleConNoRetry("oracleConn");
            if (oracleConn == null || DBCfg.dbUsed == 0 ) {
                return;
            }
            stmt = oracleConn.createStatement();
            rs = stmt.executeQuery(sql);
            VoiceBroadCastMng.calledPatternRouters.clear();
            while (rs.next()) 
            {
                String called_number_pattern = null;
                String subcps_addr_router = null;
                called_number_pattern = rs.getString("called_number_pattern");
                subcps_addr_router = rs.getString("subcps_addr_router");
                if (called_number_pattern != null && called_number_pattern.isEmpty() == false && subcps_addr_router != null) {
                    if (VoiceBroadCastMng.calledPatternRouters.containsKey(called_number_pattern) == false) {
                        SubCpsAddress subcp = new SubCpsAddress();
                        VNXLog.info("putting called_number_pattern the routing list:" + called_number_pattern
                                + " subcps_addr_router:" + subcps_addr_router);
                        //for a list of address
                        if (subcps_addr_router.indexOf(";") != -1) {
                            String[] addrs = subcps_addr_router.split(";");
                            for (String adr : addrs) {
                                if (subcps_addr_router.indexOf(":") != -1) {
                                    subcp.addresses.add(adr);
                                } else {
                                    VNXLog.error("wrong format of address, please try with 'host:port'");
                                }
                            }
                        }//for single address
                        else {
                            if (subcps_addr_router.indexOf(":") != -1) {
                                subcp.addresses.add(subcps_addr_router);
                            } else {
                                VNXLog.error("wrong format of address, please try with 'host:port'");
                            }
                        }
                        //
                        VoiceBroadCastMng.calledPatternRouters.put(called_number_pattern, subcp);
                    } else {
                        SubCpsAddress subcp = VoiceBroadCastMng.calledPatternRouters.get(called_number_pattern);
                        if (subcp != null && subcps_addr_router.indexOf(":") != -1) {
                            VNXLog.info("adding  subcps_addr_router to the list:" + subcps_addr_router);
                            subcp.addresses.add(subcps_addr_router);
                        }
                    }
                }
            }
            //
            VNXLog.info("dumping all calledRouters:"+VoiceBroadCastMng.calledPatternRouters);
        } catch (SQLException ex) {
            VNXLog.error(ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    VNXLog.error(ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    VNXLog.error(ex);
                }
            }
        }
    }

    public boolean getCalledRoute(BroadcastObj brdobj) {
        try {
            if (DBCfg.dbUsed == 0) {
                return false;
            }
            VNXLog.info("  get Address of IVR sub cps for :fromUri:"+brdobj.from_uri +" toUri:" +brdobj.to_uri 
                    + " called:" + brdobj.to_uri .getUser());
            if (VoiceBroadCastMng.calledPatternRouters.size() != 0) {
                Enumeration keys = VoiceBroadCastMng.calledPatternRouters.keys();
                Object pattern;
                while (keys.hasMoreElements()) {
                    pattern = keys.nextElement();
                    //
                    Pattern pattern1 = Pattern.compile((String) pattern);
                    Matcher matcher1 = pattern1.matcher(brdobj.to_uri .getUser());
                    if (matcher1.find() == true) {
                        SubCpsAddress subcp = (SubCpsAddress) VoiceBroadCastMng.calledPatternRouters.get(pattern);
                        if (subcp != null && subcp.addresses.size() > 0) {
                            Random random = new Random();
                            String address = subcp.addresses.get(random.nextInt(subcp.addresses.size()));
                            VNXLog.info("  routing for called:" + brdobj.to_uri .getUser() + " to " + address);
                            if (address != null && address.isEmpty() == false) 
                            {
                                brdobj.to_uri .setHost(address.split(":")[0]);
                                brdobj.to_uri .setPort(Integer.parseInt(address.split(":")[1]));
                                return true;
                            }
                        }
                    }
                }
            }
            VNXLog.error("  error when get Address of IVR sub cps for :fromUri:" + 
                    brdobj.from_uri + " toUri:" + brdobj.to_uri 
                    + "  calledPatternRouters:"+VoiceBroadCastMng.calledPatternRouters);

        } catch (Exception ex) {
            VNXLog.error(ex);
        }
        return false;
    }
}
