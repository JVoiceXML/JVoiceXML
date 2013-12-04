/*
 * Copyright (c) 1996-2001
 * VNXTele Corporation - VAS Center
 * @author Shadowman - VNXTele Mobile Networks - VAS
 * @version 1.0, 25 August 2009
 * All rights reserved.
 *
 * This software is distributed under VNXTele Open Source License Version 1.0
 * ("Licence Agreement"). You shall use it and distribute only in accordance
 * with the terms of the License Agreement.
 *
 */
package org.jvoicexml.systemtest.mobicents;

import java.sql.*;

import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import com.vnxtele.oracle.DBCfg;
import com.vnxtele.oracle.DBManager;
import com.vnxtele.util.VDate;
import com.vnxtele.util.VNXLog;
import com.vnxtele.util.VProcThrd;
import java.util.Hashtable;

import javax.servlet.sip.SipServletRequest;
public class VDBIntf extends VProcThrd
{
    private DBManager dbRAC = null;
    private Connection oraCon = null;
    private Connection ttCon = null;
    CallableStatement csChkPPaid=null;
    private Statement stmt = null;
    private String sqlCmd = "";
    public static final Hashtable hisbillQueue = new Hashtable();

    public VDBIntf()
    {
    	VNXLog.info("constructor db interface");
    }
    public void init()
    {
        try
        {
            VNXLog.info("starting db interface:"+getThreadName()+" rate:"+
                    VAppCfg.dbRate+"; ttUsed:"+DBCfg.ttUsed);
            scheduleAtFixedRate(VAppCfg.dbRate);
            VNXLog.info(getThreadName()+"  starting Broadcast Demo db connection pool");
            dbRAC = new DBManager("IVRGWPOOL","conf/VOracleRAC.cfg");
            dbRAC.startPool();
            VNXLog.info("Broadcast Demo db connection pool is established,user:"+dbRAC.getUser());
            connect();
            connectTimesten();
        } catch (Exception ex) {
            VNXLog.error(getThreadName()+":"+ex.toString());
        }
    }
     public void connect()
    {
        try {
        	if (DBCfg.dbUsed==0) return;
            oraCon = dbRAC.getConNoRetry();
            if(oraCon!=null)
                VNXLog.info(getThreadName()+"  get connection successful; "
                        +oraCon.getMetaData().getURL());
            else
            {
                VNXLog.error("lost connections to the database");
            }
            Thread.sleep(2000);
        } catch (Exception ex)
        {
            VNXLog.error(ex);
        }
    }
      public void connectTimesten()
    {
        try {
            if(DBCfg.ttUsed==0) return;
            ttCon = dbRAC.getTimeStenCon();
            if(ttCon!=null)
                VNXLog.info(getThreadName()+"  get timesten connection successful; "
                        +ttCon.getMetaData().getURL());
            else
            {
                VNXLog.error("lost connections to the timesten database");
            }
            Thread.sleep(2000);
        } catch (Exception ex)
        {
            VNXLog.error(ex);
        }
    }
    /**
   * This method connect to host,port and SID declared in oracle client configuration
   * See ORA_HOME\network\admin\tnsnames
   * @param host String
   * @param port int
   * @param SID String
   * @return Connection
   */
    public Connection connect(String host, int port, String SID, String OraUsername, String OraPassword)
    {
        Connection conn = null;
        String cnString = "";
        String csDump="";
        try
        {
            VNXLog.info(getThreadName()+":Connecting to the database:"+host+" ...");
            DriverManager.setLoginTimeout(60);
            VNXLog.info(getThreadName()+":LoginTimeOut:"+DriverManager.getLoginTimeout());
            java.util.Properties props = System.getProperties();
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            cnString = "jdbc:oracle:thin:@(description=(address=(host=" + host
                    + ")(protocol=tcp)(port=" + port + "))(connect_data=(sid=" + SID + ")))";
            csDump = "Connecting... -- \r\n" + cnString;

            VNXLog.debug(getThreadName()+":"+csDump);
            conn = DriverManager.getConnection(cnString, OraUsername, OraPassword);
            VNXLog.info(getThreadName()+":Connected to the database:"+host);

            
        } catch (Exception err)
        {
            csDump = "Failed to connect to oracle -- " + cnString;
            VNXLog.debug(getThreadName()+":"+csDump);
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
    protected boolean isConnected(Connection conn)
    {
    	if(DBCfg.dbUsed==0) 
    		{
    		VNXLog.warn("db is disable");
    		return true;
    		}
        if (conn == null )
        {
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
                VNXLog.info( "connection is connected...conState:" + conState);
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
        }
          finally
         {
             try
             {
                 if(stmt !=null) stmt.close();
             }
             catch (Exception ex) { VNXLog.error(ex);}
         }
        return conState;
    }

    

   

    

     public boolean isExistCDRFileName(String filename)
     {
         Statement stmt = null;
         boolean uniqueex = true;
         try {
             //
             if (oraCon != null)
             {
                 stmt = oraCon.createStatement();
                 sqlCmd = " INSERT INTO cdrfilenames(filename)  VALUES('" + filename + "')";
                 VNXLog.info( sqlCmd);
                 stmt.executeQuery(sqlCmd);
                 oraCon.commit();
                 uniqueex = false;
             }
         } catch (SQLException ex) 
         {
             VNXLog.warn(getThreadName() + ":exists cdr file:"+filename);
             VNXLog.warn(ex.getMessage());
         } catch (Exception ex) {
             VNXLog.error(ex);
         }
         finally
         {
             try {if(stmt !=null) stmt.close();}
             catch (Exception ex) { VNXLog.error( ex.toString());}
         }
         return uniqueex;
    }

      public void instInviteFromCaller(SipServletRequest orgrequest,String conferKey)
     {
         Statement stmt = null;
         try {
             //
             if (oraCon != null || DBCfg.dbUsed==1)
             {
            	 SipURI from = (SipURI)orgrequest.getAddressHeader("From").getURI();
            	 SipURI to = (SipURI)orgrequest.getAddressHeader("To").getURI();
                 stmt = oraCon.createStatement();
                 sqlCmd = " INSERT INTO conferences(confer_key,caller_callid,fromuri,touri,callernumber" +
                 		",callednumber,invitetime) VALUES('" + conferKey + 
                 		"','" + orgrequest.getCallId()+ 
                 		"','" +  from.toString()+ "','"+to.toString()+ 
                 		"','" + from.getUser()+ "','" + to.getUser()+ "',sysdate)";
                 VNXLog.info( "sqlCmd:"+sqlCmd);
                 stmt.executeQuery(sqlCmd);
                 oraCon.commit();
             }
         } catch (SQLException ex)
         {
             VNXLog.error(ex);
         } catch (Exception ex) {
             VNXLog.error( ex);
         }
         finally
         {
             try {if(stmt !=null) stmt.close();}
             catch (Exception ex) { VNXLog.error( ex);}
         }
    }
      public void updtOn200OKFromCPs(SipServletResponse response,String callerID)
      {
          Statement stmt = null;
          try {
              //
        	  if (oraCon != null || DBCfg.dbUsed==1)
              {
                  stmt = oraCon.createStatement();
                  sqlCmd = "update conferences set called_callid='"+response.getCallId()
                  +"',starttime=sysdate where caller_callid='"+callerID+"'";
                  VNXLog.info( "sqlCmd:"+sqlCmd);
                  stmt.executeQuery(sqlCmd);
                  oraCon.commit();
              }
          } catch (SQLException ex)
          {
              VNXLog.error(ex);
          } catch (Exception ex) {
              VNXLog.error( ex);
          }
          finally
          {
              try {if(stmt !=null) stmt.close();}
              catch (Exception ex) { VNXLog.error( ex);}
          }
     }
      
      public String getCPsSideCaller_CallId(SipServletRequest request)
      {
    	  String called_callid="";
          try 
          {
              if (oraCon != null)
              {
                  Statement stmt = oraCon.createStatement();
                  //load msisdn prefix form ocs zte
                  sqlCmd = "select called_callid from  conferences  where caller_callid='"+
                  			request.getCallId()+"'";
                  VNXLog.debug("sqlCmd:"+sqlCmd);
                  ResultSet rset = stmt.executeQuery(sqlCmd);
                  
                  while (rset.next()) {
                      called_callid=rset.getString("called_callid");
                  }
                  //
                  rset.close();
                  stmt.close();
                  return called_callid;
              }
          } catch (SQLException ex)
          {
              VNXLog.error(ex);
          }
          catch(Exception ex)
          {
              VNXLog.error(ex);
              
          }
          return called_callid;
      }
      
   

/**
 *
 * @param logmessage ORA-12801: error signaled in parallel query server P000
 * @param logcomment exception:chkPostPaidSub
 */
       public void insertError(String logmessage,String logcomment)
     {
         Statement stmt = null;
         try {
             //
             if (oraCon != null)
             {
                 stmt = oraCon.createStatement();
                 sqlCmd = "begin VWriteLog('"
                         +logmessage+"','"+logcomment+"'); END;";
                 VNXLog.info( sqlCmd);
                 stmt.executeQuery(sqlCmd);
             }
         } catch (SQLException ex)
         {
             VNXLog.error( ex.toString());
         } catch (Exception ex) {
             ex.printStackTrace();
             VNXLog.error( ex.toString());
         }
         finally
         {
             try {if(stmt !=null) stmt.close();}
             catch (Exception ex) { VNXLog.error( ex.toString());}
         }
    }


    
    public void retry()
    {
        try
        {
            if(!isConnected(oraCon))
            {
                VNXLog.error(getThreadName()+":"+"losting connection to the database"+" url= "+dbRAC.getUrl());
                VNXLog.info(getThreadName()+":"+"system is retrying now...");
                connect();
            }
            if(dbRAC.isTTEnable()==false) return;
            if(DBCfg.ttUsed == 0) return;
            if(!isConnected(ttCon))
            {
                VNXLog.error(getThreadName()+":"+"losting connection to the timesten database"+" url= "+dbRAC.getTTUrl());
                VNXLog.info(getThreadName()+":"+"system is retrying now...");
                connectTimesten();
            }

        }
        catch(Exception ex)
        {
            VNXLog.error(ex);
        }

    }

    public void process() 
    {
        try
        {
            retry();
        }
        catch(Exception ex)
        {
            VNXLog.error(getThreadName()+":"+ex.toString());
        }
    }
    
   

    public void insertAlarm(int level,int cate,String content, String desc)
    {
        ResultSet rset=null;
        Statement stmt =null;
        try
        {
            try
            {
                if (oraCon != null)
                {
                    stmt = oraCon.createStatement();
                    String cmd="begin pr_ins_alarm_sms("+level+","+cate+",'"+content+"','"+desc+"'); END;";
                    VNXLog.debug(getThreadName()+":"+"cmd:" + cmd);
                    rset = stmt.executeQuery(cmd);
                    rset.close();
                    stmt.close();
                }
            }
            catch  (SQLException ex)
            {
                ex.printStackTrace();
                VNXLog.error(getThreadName()+":"+ex.toString());
            }
            if(stmt!=null) stmt.close();
            if(rset!=null) rset.close();

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            VNXLog.error(getThreadName()+":"+ex.toString());
        }

    }

   

}

