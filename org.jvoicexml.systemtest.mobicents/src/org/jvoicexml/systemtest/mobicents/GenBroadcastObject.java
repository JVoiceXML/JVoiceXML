/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jvoicexml.systemtest.mobicents;

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
public class GenBroadcastObject extends VProcThrd {

    private DBManager dbRAC = null;
    private Connection oraCon = null;
    private Connection ttCon = null;
    private Statement stmt = null;
    private String sqlCmd = "";
    private PreparedStatement pstmtChkCon = null;
    private CallableStatement csGetBroadcastObj = null;
    private CallableStatement insertBroadcastObj = null;
    private CallableStatement updateBroadcastObjFromToURI = null;
    private CallableStatement updateBroadcastObjComplete = null;
    private int rate = 10000;
    private String oracfgFile = "conf/DBManager.cfg";
    private int numBatchBrcast = 0;
    //
    public static ConcurrentLinkedQueue tempQueue = new ConcurrentLinkedQueue();
    //storing insert batch
    public static final ConcurrentLinkedQueue sipIncommingMesgsQueue = new ConcurrentLinkedQueue();
    public static final ConcurrentLinkedQueue sipOutgoingMesgsQueue = new ConcurrentLinkedQueue();
    //singleton pattern

    private static final class SingletonHolder {

        private static final GenBroadcastObject instance = new GenBroadcastObject();
    }

    public static GenBroadcastObject getInstance() {
        return SingletonHolder.instance;
    }

    public GenBroadcastObject() {
        init();
        start();
    }

    public void init() {
        try {

            VNXLog.info("starting db interface:" + getThreadName() + " rate:"
                    + rate + "; ttUsed:" + DBCfg.ttUsed);
            scheduleAtFixedRate(rate);
            VNXLog.info(getThreadName() + "  starting gensys db connection pool");
            dbRAC = new DBManager("DBMemRACPOOL", oracfgFile);
            dbRAC.startPool();
            VNXLog.info("database connection pool is established");
//            connect();
            connectTimesten();
        } catch (Exception ex) {
            VNXLog.error(ex);
        }
    }

    public void connect() {
        try {
            oraCon = dbRAC.getConNoRetry();
            if (oraCon != null) {
                VNXLog.info(getThreadName() + "  get connection successful; "
                        + oraCon.getMetaData().getURL());
            } else {
                VNXLog.error("lost connections to the database");
            }
            Thread.sleep(2000);
        } catch (Exception ex) {
            VNXLog.error(ex);
        }
    }

    public void connectTimesten() {
        try {
            if (DBCfg.ttUsed == 0) {
                return;
            }
            ttCon = dbRAC.getTimeStenCon();
            if (ttCon != null) {
                VNXLog.info(getThreadName() + "  get timesten connection successful; "
                        + ttCon.getMetaData().getURL());
            } else {
                VNXLog.error("lost connections to the timesten database");
            }
            Thread.sleep(2000);
        } catch (Exception ex) {
            VNXLog.error(ex);
        }
    }

    public Connection connect(String host, int port, String SID, String OraUsername, String OraPassword) {
        Connection conn = null;
        String cnString = "";
        String csDump = "";
        try {
            VNXLog.info(getThreadName() + ":Connecting to the database:" + host + " ...");
            DriverManager.setLoginTimeout(60);
            VNXLog.info(getThreadName() + ":LoginTimeOut:" + DriverManager.getLoginTimeout());
            java.util.Properties props = System.getProperties();
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            cnString = "jdbc:oracle:thin:@(description=(address=(host=" + host
                    + ")(protocol=tcp)(port=" + port + "))(connect_data=(sid=" + SID + ")))";
            csDump = "Connecting... -- \r\n" + cnString;

            VNXLog.debug(getThreadName() + ":" + csDump);
            conn = DriverManager.getConnection(cnString, OraUsername, OraPassword);
            VNXLog.info(getThreadName() + ":Connected to the database:" + host);


        } catch (Exception err) {
            csDump = "Failed to connect to oracle -- " + cnString;
            VNXLog.debug(getThreadName() + ":" + csDump);
            err.printStackTrace();
            conn = null;
        }
        return conn;
    }

    /**
     * Check if the connection is alive
     * @param conn Connection
     * @return boolean
     */
    protected boolean isConnected(Connection conn) {
        if (conn == null) {
            return false;
        }
        PreparedStatement stmt = null;
        boolean conState = false;
        try {
            try {
                VNXLog.info(getThreadName() + ":--------------checking connection");
                stmt = conn.prepareStatement("select 1 from dual");
                conState = stmt.execute();
                stmt.close();
                VNXLog.info(getThreadName() + ":" + "connection is connected...conState:" + conState);
            } catch (SQLException ex) {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException sx) {
                    VNXLog.error(ex);
                }
            }
        } catch (Exception ex) {
            VNXLog.error(ex);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception ex) {
                VNXLog.error(ex);
            }
        }
        return conState;
    }

    /**
     * inserting a sip message to the db 
     * @param direction :1 for incoming 2: for outgoing
     */
    public void insrSIPMsgs(SipServletMessage msg, int direction) {
        try {
            //setting stored time
            msg.setAttribute("stored_time", new Timestamp(System.currentTimeMillis()));
            VNXLog.info("storing the message" + VIVRUtil.dumpSIPMsgHdr(msg));
            if (direction == VAppCfg.SIP_MSG_INCOMING) {
                sipIncommingMesgsQueue.add(msg);
            } else {
                sipOutgoingMesgsQueue.add(msg);
            }
        } catch (Exception ex) {
            VNXLog.error(ex);
        }

    }

    public void procSIPMsgQueues(ConcurrentLinkedQueue linkqueue, int direction) {
        PreparedStatement pstmt = null;
        if (linkqueue == null || linkqueue.size() == 0) {
            return;
        } else {
            VNXLog.info("Insert batch SIP Messages:size of linkqueue:" + linkqueue.size());
        }
        String tablename = "SIP_MESSAGES";

        String csSql = "INSERT INTO " + tablename + " (message_type, sip_message_type, callid, fromuri, touri,"
                + "contact, raw_sip_message, stored_time, callernumber,callednumber,local_addr,remote_addr) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        String currentCallid = "";
        Vector<SipServletMessage> vTemp = new Vector();
        try {
            if (oraCon == null) {
                return;
            }
            oraCon.setAutoCommit(false);
            pstmt = oraCon.prepareStatement(csSql);
            int cnt = 0;
            SipServletMessage sipmsg = null;
            while (!linkqueue.isEmpty()) {
                if (cnt > DBCfg.dbBatNumRecords) {
                    break;
                } else {
                    cnt++;
                }
                sipmsg = (SipServletMessage) linkqueue.poll();
                //adding temp vector to remove later;
                vTemp.add(sipmsg);
                SipURI from = (SipURI) sipmsg.getAddressHeader("From").getURI();
                SipURI to = (SipURI) sipmsg.getAddressHeader("To").getURI();
                Address contact = sipmsg.getAddressHeader(VAppCfg.CONTACT_HEADER);
                pstmt.setInt(1, direction);
                pstmt.setString(2, VIVRUtil.getSIPMsgType(sipmsg));
                pstmt.setString(3, sipmsg.getCallId());
                pstmt.setString(4, from.toString());
                pstmt.setString(5, to.toString());
                pstmt.setString(6, contact == null ? "" : contact.toString());
                pstmt.setString(7, sipmsg.toString());
//                    pstmt.setString(8, (String)sipmsg.getAttribute("stored_time"));
                pstmt.setTimestamp(8, (java.sql.Timestamp) sipmsg.getAttribute("stored_time"));
                pstmt.setString(9, from.getUser());
                pstmt.setString(10, to.getUser());
                pstmt.setString(11, sipmsg.getLocalAddr() + ":" + sipmsg.getLocalPort());
                pstmt.setString(12, sipmsg.getInitialRemoteAddr() + ":" + sipmsg.getInitialRemotePort());
                currentCallid = sipmsg.getCallId();
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            oraCon.commit();
            pstmt.clearBatch();
            pstmt.close();
            vTemp.clear();
            VNXLog.info("Insert batch sucessully request into table " + tablename
                    + " :size of linkqueue:" + linkqueue.size());
        } catch (BatchUpdateException e) {
            try {
                oraCon.rollback();
                insertBackupTbl(vTemp);
                VNXLog.error(e);
                insertError(e.getMessage(), "exception:on batchUpdate incoming_messages currentCallid:" + currentCallid);
            } catch (Exception ex) {
                VNXLog.error(ex);
            }
        } catch (Exception ex) {
            VNXLog.error(ex);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    VNXLog.error(ex);
                }
            }

        }
    }

    public void insertBackupTbl(Vector vt) {
        VNXLog.fatal("Insert backup table here, don't support yet : vt:" + vt);
        vt.clear();
    }

    /**
     *
     * @param logmessage ORA-12801: error signaled in parallel query server P000
     * @param logcomment exception:chkPostPaidSub
     */
    public void insertError(String logmessage, String logcomment) {
        Statement stmt = null;
        try {
            //
            if (oraCon != null) {
                stmt = oraCon.createStatement();
                sqlCmd = "begin VWriteLog('"
                        + logmessage + "','" + logcomment + "'); END;";
                VNXLog.info(sqlCmd);
                stmt.executeQuery(sqlCmd);
            }
        } catch (SQLException ex) {
            VNXLog.error(ex.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            VNXLog.error(ex.toString());
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception ex) {
                VNXLog.error(ex.toString());
            }
        }
    }

    public void retry() {
        try {
            if (!isConnected(oraCon)) {
                VNXLog.error(getThreadName() + ":" + "losting connection to the database");
                VNXLog.info(getThreadName() + ":" + "system is retrying now...");
                connect();
            }
            if (dbRAC.isTTEnable() == false) {
                return;
            }
            if (DBCfg.ttUsed == 0) {
                return;
            }
            if (!isConnected(ttCon)) {
                VNXLog.error(getThreadName() + ":" + "losting connection to the timesten database");
                VNXLog.info(getThreadName() + ":" + "system is retrying now...");
                connectTimesten();
            }

        } catch (Exception ex) {
            VNXLog.error(ex);
        }

    }

    public void process() {
        try {
            //other functions
            retry();
        } catch (Exception ex) {
            VNXLog.error(getThreadName() + ":" + ex.toString());
        }
    }

   
    public void testTimesten() {
        try {
            CallableStatement cstmt;
            ResultSet cursor;
// Use a PL/SQL block to open the cursor.
            cstmt = ttCon.prepareCall(" begin open :x for SELECT * FROM VOICEBRCAST.broadcast where rownum <=25 "
                    + " and error_code = -1 and status = 'INITIAL' "
                    + " and (sysdate - schedule_time) > NUMTODSINTERVAL(1,'MINUTE'); end;");
            cstmt.registerOutParameter(1, TimesTenTypes.CURSOR);
            cstmt.execute();
            cursor = ((TimesTenCallableStatement) cstmt).getCursor(1);

// Use the cursor as you would any other ResultSet object.
            while (cursor.next()) {
                System.out.println("xxxxxxxxxxxxx tables: " + cursor.getString(1) + " timestamp:"
                        + cursor.getTimestamp("schedule_time"));
            }
        } catch (SQLException ex) {
            VNXLog.error(ex);
        } catch (Exception ex) {
            VNXLog.error(ex);
        }
    }

    public int insertBrcastObj(BroadcastObj broadcastobj) 
    {
        int result = -1;
        String sql = " begin insertBrcastObj(:BROADCAST_ID,:caller,:called,:vxml_uri,:LANGUAGE"
                + ",:schedule_time,:error_code,:status, :RESULT,:ERRORMSG); end;";
        VNXLog.info("sql command:" + sql);
        try {
            if (ttCon == null || DBCfg.ttUsed == 0 || !isConnected(ttCon)) {
                return -1;
            }
            if (insertBroadcastObj == null || insertBroadcastObj.isClosed() == true) {
                insertBroadcastObj =
                        ttCon.prepareCall(sql);
            }
            synchronized (insertBroadcastObj) {
                insertBroadcastObj.setInt(1, broadcastobj.broadcast_id);
                insertBroadcastObj.setString(2, broadcastobj.caller);
                insertBroadcastObj.setString(3, broadcastobj.called);
                insertBroadcastObj.setString(4, broadcastobj.vxml_uri);
                insertBroadcastObj.setString(5, broadcastobj.language);
                insertBroadcastObj.setTimestamp(6, broadcastobj.schedule_time);
                insertBroadcastObj.setInt(7, broadcastobj.error_code);
                insertBroadcastObj.setString(8, broadcastobj.status.name());
                insertBroadcastObj.registerOutParameter(9, java.sql.Types.INTEGER);
                insertBroadcastObj.registerOutParameter(10, java.sql.Types.VARCHAR);
                insertBroadcastObj.execute();
                result = insertBroadcastObj.getInt(9);
                VNXLog.info("error sql message:" + insertBroadcastObj.getString(10));
                return result;
            }
        } catch (SQLException ex) {
            VNXLog.error(ex);
        } catch (Exception ex) {
            VNXLog.error(ex);
        }
        return result;
    }

    
    public int genBroadcastList(int number_of_objects, int start_broadcastid, int start_caller_number,
            int start_called_number, String vxml_uri, Timestamp scheduletime) 
    {
        try 
        {
            VNXLog.info("<<<<<<<<<<<<<<< starting generating broadcast list with number_of_objects:"+number_of_objects);
            VNXLog.info("number_of_objects:"+number_of_objects);
            VNXLog.info("start_broadcastid:"+start_broadcastid);
            VNXLog.info("start_caller_number:"+start_caller_number);
            VNXLog.info("start_called_number:"+start_called_number);
            VNXLog.info("vxml_uri:"+vxml_uri);
            VNXLog.info("scheduletime:"+scheduletime);
            long startTime=System.currentTimeMillis();
            int insertedNumber=0;
            for(int i=1;i<number_of_objects;i++)
            {
                BroadcastObj broadcastobj = new BroadcastObj();
                broadcastobj.broadcast_id  = start_broadcastid + i;
                broadcastobj.caller =   Integer.toString(start_caller_number+i+start_broadcastid);    
                broadcastobj.called =   Integer.toString(start_called_number+i+start_broadcastid);           
                broadcastobj.vxml_uri = vxml_uri;     
                broadcastobj.language = "VIETNAM";     
                broadcastobj.schedule_time = scheduletime;
                broadcastobj.error_code = 0;    
                broadcastobj.status =  BrcastObjState.INITIAL;      
                int result=insertBrcastObj(broadcastobj);
                if(result==0) insertedNumber++;
                VNXLog.info("inserting broadcastobj:"+broadcastobj + " result: "+result);
            }
            VNXLog.info("insertedNumber:"+insertedNumber);
            VNXLog.info("total generated time:"+(System.currentTimeMillis()-startTime) 
                    + " for number_of_objects: "+number_of_objects);
            
            
        } catch (Exception ex) {
            VNXLog.error(ex);
        }
        return -1;
    }

    public static void main(final String[] args) {


        try {
            VNXLog vnxlog = new VNXLog("conf/GenBroadcastListLog.cfg");
            GenBroadcastObject genbroadcast = new GenBroadcastObject();
            genbroadcast.genBroadcastList(10000-1143, 1143, 999999, 456789999,
                    "file://///home/vbcast/V1.0/conf/jvxml/vxml/CRBTPromotion.vxml",
                    new Timestamp(System.currentTimeMillis()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
