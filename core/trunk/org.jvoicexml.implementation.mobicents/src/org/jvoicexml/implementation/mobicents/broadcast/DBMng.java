/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jvoicexml.implementation.mobicents.broadcast;

import com.timesten.jdbc.TimesTenCallableStatement;
import com.timesten.jdbc.TimesTenTypes;
import com.vnxtele.oracle.DBCfg;
import com.vnxtele.oracle.DBMemRAC;
import com.vnxtele.util.VNXLog;
import com.vnxtele.util.VProcThrd;
import java.sql.*;
import java.util.Enumeration;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.sip.SipURI;
import org.jvoicexml.implementation.mobicents.VAppCfg;

/**
 *
 * @author Shadowman
 */
public class DBMng extends VProcThrd {

    private DBMemRAC dbRAC = null;
    private Connection oraCon = null;
    private Connection ttCon = null;
    private Statement stmt = null;
    private String sqlCmd = "";
    private PreparedStatement pstmtChkCon = null;
    private CallableStatement  csGetBroadcastObj=null;
    private CallableStatement updateBroadcastObjStatus=null;
    private CallableStatement updateBroadcastObjFromToURI=null;
    private int rate = 10000;
    private String oracfgFile = "conf/DBMemRAC.cfg";
    private int numBatchBrcast = 0;
    //
    public static ConcurrentLinkedQueue tempQueue= new ConcurrentLinkedQueue();
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

            VNXLog.info("starting db interface:" + getThreadName() + " rate:"
                    + rate + "; ttUsed:" + DBCfg.ttUsed);
            scheduleAtFixedRate(rate);
            VNXLog.info(getThreadName() + "  starting gensys db connection pool");
            dbRAC = new DBMemRAC("DBMemRACPOOL", oracfgFile);
            dbRAC.startPool();
            VNXLog.info("database connection pool is established");
            connect();
            connectTimesten();
            loadCalledRouting();
//            testTimesten();
            loadBroadCastObject();
            procTempQueue();
//            loadBroadCastList();
        } catch (Exception ex) {
            VNXLog.error2(ex);
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
            loadBroadCastObject() ;
            procTempQueue();
            retry();
        } catch (Exception ex) {
            VNXLog.error(getThreadName() + ":" + ex.toString());
        }
    }

    public void loadBroadCastList() {
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT caller, called,from_uri,to_uri, vxml_uri, language, schedule_time,"
                + "start_time, duration, error_code, answer_time,hangup_time  FROM broadcast"
                + " where fun_permit_voicebroadcast(caller, called,error_code,schedule_time) = 0 and rownum < "
                + VAppCfg.vbcNumbBatchRecords;
        VNXLog.info2("sql command:" + sql);
        try {
            if (oraCon == null || DBCfg.dbUsed == 0 || !isConnected(oraCon)) {
                return;
            }
            stmt = oraCon.createStatement();
            rs = stmt.executeQuery(sql);
            VoiceBroadCastMng.broadcastQueue.clear();
            while (rs.next()) {
                BroadcastObj broadcst = new BroadcastObj();
                broadcst.caller = rs.getString("caller");
                broadcst.called = rs.getString("called");
                broadcst.vxml_uri = rs.getString("vxml_uri");
                broadcst.schedule_time = rs.getTimestamp("schedule_time");
                broadcst.error_code = rs.getInt("error_code");
                if (broadcst.isValid()) 
                {
                    VNXLog.info2("putting BroadcastObj the list:" + broadcst 
                            +" size: "+VoiceBroadCastMng.broadcastQueue.size());
                    VoiceBroadCastMng.broadcastQueue.add(broadcst);
                }
            }
            //
            VNXLog.info2("dumping all calledRouters:"+VoiceBroadCastMng.calledPatternRouters);
        } catch (SQLException ex) {
            VNXLog.error2(ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    VNXLog.error2(ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    VNXLog.error2(ex);
                }
            }
        }
    }
    
    
    public void loadBroadCastObject() 
    {
        ResultSet cursor = null;
        VNXLog.info2("sql command:" + VAppCfg.vbcSqlBroadcastCMD);
        try {
            if (ttCon == null || DBCfg.ttUsed == 0 || !isConnected(ttCon)) 
            {
                return;
            }
            if (csGetBroadcastObj == null || csGetBroadcastObj.isClosed() == true) {
                csGetBroadcastObj =
                        ttCon.prepareCall(VAppCfg.vbcSqlBroadcastCMD);
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
                    brdcast.status = BrcastObjState.valueOf(cursor.getString("status"));
                    VNXLog.info2(" adding a brdcast object to the temp queue: "+brdcast );
                    brdcast.status = BrcastObjState.SELECT;
                    tempQueue.add(brdcast);
                    
                }
            }
        } catch (SQLException ex) {
            VNXLog.error2(ex);
        }
        catch (Exception ex) {
            VNXLog.error2(ex);
        }
    }
    
    
    public void procTempQueue() 
    {
        if (tempQueue.size() == 0) 
        {
            return;
        }
        VNXLog.info2("size of tempQueue:" + tempQueue.size());
        try 
        {
            BroadcastObj brdcast = (BroadcastObj) tempQueue.poll();
            if (updateBrcastObjStatus(BrcastObjState.SELECT, brdcast.broadcast_id) == 0) 
            {
                brdcast.status = BrcastObjState.QUEUE;
                VoiceBroadCastMng.broadcastQueue.add(brdcast);
                VNXLog.info2(" queued a brdcast object: " + brdcast);
            } else {
                VNXLog.error2(" cannot queue a brdcast object: " + brdcast);
            }
        } catch (Exception ex) {
            VNXLog.error2(ex);
        }
    }
    
    
    public void testTimesten() 
    {
        try{
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
            System.out.println("xxxxxxxxxxxxx tables: "+cursor.getString(1) + " timestamp:"
                    +cursor.getTimestamp("schedule_time"));
        }
        } catch (SQLException ex) {
            VNXLog.error2(ex);
        }
        catch (Exception ex) {
            VNXLog.error2(ex);
        }
    }
    
    
     public int updateBrcastObjStatus(BrcastObjState status,int broadcastid) 
    {
        int result=-1;
        String sql=" begin updateBrcastObjStatus(:status,:BROADCAST_ID,:RESULT); end;";
        VNXLog.info2("sql command:" + sql);
        try {
            if (ttCon == null || DBCfg.ttUsed == 0 || !isConnected(ttCon)) {
                return -1;
            }
            if (updateBroadcastObjStatus == null || updateBroadcastObjStatus.isClosed() == true) {
                updateBroadcastObjStatus =
                        ttCon.prepareCall(sql);
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
            VNXLog.error2(ex);
        }
        catch (Exception ex) {
            VNXLog.error2(ex);
        }
        return result;
    }
     
    
     
      public int updateBrcastObjSessidInvTime(BroadcastObj brdobj) 
    {
        int result=-1;
        String sql=" begin updateBrcastObjSessidInvTime(:broadcast_id,:callid,:from_uri,:to_uri,:status,:RESULT); end;";
        VNXLog.info2("sql command:" + sql);
        try {
            if (ttCon == null || DBCfg.ttUsed == 0 || !isConnected(ttCon)) {
                return -1;
            }
            if (updateBroadcastObjFromToURI == null || updateBroadcastObjFromToURI.isClosed() == true) {
                updateBroadcastObjFromToURI =
                        ttCon.prepareCall(sql);
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
            VNXLog.error2(ex);
        }
        catch (Exception ex) {
            VNXLog.error2(ex);
        }
        return result;
    }

    public void loadCalledRouting() {
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT called_number_pattern, subcps_addr_router, cp_name,comments  FROM called_router where enable_router=1";
        VNXLog.info2("sql command:" + sql);
        try {
            if (oraCon == null || DBCfg.dbUsed == 0 || !isConnected(oraCon)) {
                return;
            }
            stmt = oraCon.createStatement();
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
                        VNXLog.info2("putting called_number_pattern the routing list:" + called_number_pattern
                                + " subcps_addr_router:" + subcps_addr_router);
                        //for a list of address
                        if (subcps_addr_router.indexOf(";") != -1) {
                            String[] addrs = subcps_addr_router.split(";");
                            for (String adr : addrs) {
                                if (subcps_addr_router.indexOf(":") != -1) {
                                    subcp.addresses.add(adr);
                                } else {
                                    VNXLog.error2("wrong format of address, please try with 'host:port'");
                                }
                            }
                        }//for single address
                        else {
                            if (subcps_addr_router.indexOf(":") != -1) {
                                subcp.addresses.add(subcps_addr_router);
                            } else {
                                VNXLog.error2("wrong format of address, please try with 'host:port'");
                            }
                        }
                        //
                        VoiceBroadCastMng.calledPatternRouters.put(called_number_pattern, subcp);
                    } else {
                        SubCpsAddress subcp = VoiceBroadCastMng.calledPatternRouters.get(called_number_pattern);
                        if (subcp != null && subcps_addr_router.indexOf(":") != -1) {
                            VNXLog.info2("adding  subcps_addr_router to the list:" + subcps_addr_router);
                            subcp.addresses.add(subcps_addr_router);
                        }
                    }
                }
            }
            //
            VNXLog.info2("dumping all calledRouters:"+VoiceBroadCastMng.calledPatternRouters);
        } catch (SQLException ex) {
            VNXLog.error2(ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    VNXLog.error2(ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    VNXLog.error2(ex);
                }
            }
        }
    }

    public boolean getCalledRoute(BroadcastObj brdobj) {
        try {
            if (DBCfg.dbUsed == 0) {
                return false;
            }
            VNXLog.info2("  get Address of IVR sub cps for :fromUri:"+brdobj.from_uri +" toUri:" +brdobj.to_uri 
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
                            VNXLog.info2("  routing for called:" + brdobj.to_uri .getUser() + " to " + address);
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
            VNXLog.error2("  error when get Address of IVR sub cps for :fromUri:" + 
                    brdobj.from_uri + " toUri:" + brdobj.to_uri 
                    + "  calledPatternRouters:"+VoiceBroadCastMng.calledPatternRouters);

        } catch (Exception ex) {
            VNXLog.error2(ex);
        }
        return false;
    }
}
