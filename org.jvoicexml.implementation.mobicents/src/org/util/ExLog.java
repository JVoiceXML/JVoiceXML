/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.util;

import java.sql.SQLException;
import java.text.MessageFormat;
import oracle.ucp.UniversalConnectionPoolException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author Shadowman
 */
public class ExLog 
{
    public static void exception(Logger LOGGER,Exception ex)
    {
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        String clsName=ste.getClassName();
        String pkage = clsName.substring(clsName.lastIndexOf(".",clsName.lastIndexOf(".")-1)+1, clsName.length());
        clsName=clsName.substring(clsName.lastIndexOf(".")+1, clsName.length());
        if(clsName.isEmpty()) return;
        //get a proper instance
        Logger subLog = LOGGER.getLogger(clsName);
        if(subLog==null) return;
        subLog.log(Level.ERROR, " ("+pkage+","
                +ste.getLineNumber()+","+ste.getMethodName()+" )"+stackTraceToStr(ex));
    }
    public static void exception(Logger LOGGER,Throwable throwable)
    {
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        String clsName=ste.getClassName();
        String pkage = clsName.substring(clsName.lastIndexOf(".",clsName.lastIndexOf(".")-1)+1, clsName.length());
        clsName=clsName.substring(clsName.lastIndexOf(".")+1, clsName.length());
        if(clsName.isEmpty()) return;
        //get a proper instance
        Logger subLog = LOGGER.getLogger(clsName);
        if(subLog==null) return;
        subLog.log(Level.ERROR, " ("+pkage+","
                +ste.getLineNumber()+","+ste.getMethodName()+" )"+stackTraceToStr(throwable));
    }
    public static String stackTraceToStr(Throwable ex)
    {
        try
        {
            if (ex instanceof SQLException || ex instanceof UniversalConnectionPoolException)
            {
                StackTraceElement[] trace = ex.getStackTrace();
                String temp = "";
                temp += ex.getMessage();
                if(ex instanceof SQLException)
                temp +=" ;sqlErrCode:"+((SQLException)ex).getErrorCode()+" ";
                else if(ex instanceof UniversalConnectionPoolException)
                temp +=" ;ucpErrCode:"+((UniversalConnectionPoolException)ex).getErrorCode()+" ";
                for (int i = 0; i < trace.length; i++)
                    temp += trace[i] + "\n";
                return temp;
            } else
            {
                StackTraceElement[] trace = ex.getStackTrace();
                String temp = "";
                temp += ex.getMessage();
                for (int i = 0; i < trace.length; i++) 
                    temp += trace[i] + "\n";
                return temp;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static String stackTraceToStr(Exception ex)
    {
        try
        {
            if (ex instanceof SQLException || ex instanceof UniversalConnectionPoolException)
            {
                StackTraceElement[] trace = ex.getStackTrace();
                String temp = "";
                temp += ex.getMessage();
                if(ex instanceof SQLException)
                temp +=" ;sqlErrCode:"+((SQLException)ex).getErrorCode()+" ";
                else if(ex instanceof UniversalConnectionPoolException)
                temp +=" ;ucpErrCode:"+((UniversalConnectionPoolException)ex).getErrorCode()+" ";
                for (int i = 0; i < trace.length; i++)
                    temp += trace[i] + "\n";
                return temp;
            } else
            {
                StackTraceElement[] trace = ex.getStackTrace();
                String temp = "";
                temp += ex.getMessage();
                for (int i = 0; i < trace.length; i++) 
                    temp += trace[i] + "\n";
                return temp;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static void debug(String logcontent)
    {
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        String clsName=ste.getClassName();
        String pkage = clsName.substring(clsName.lastIndexOf(".",clsName.lastIndexOf(".")-1)+1, clsName.length());
        clsName=clsName.substring(clsName.lastIndexOf(".")+1, clsName.length());
        System.out.println(" printing :"+clsName);
        if(clsName.isEmpty()) return;
        //get a proper instance
        Logger subLog = Logger.getRootLogger();
        System.out.println(" subLog :"+clsName);
        if(subLog==null) return;
        System.out.println(" subLog :"
                +" ("+pkage+","+ste.getLineNumber()+","+ste.getMethodName()+" )"+logcontent);
        subLog.log(Level.DEBUG, " ("+pkage+","+ste.getLineNumber()+","+ste.getMethodName()+" )"+logcontent);
    }
    public static void debug(String pattern, Object ... arguments)
    {
        MessageFormat temp = new MessageFormat(pattern);
        String logcontent=temp.format(arguments);
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        String clsName=ste.getClassName();
        String pkage = clsName.substring(clsName.lastIndexOf(".",clsName.lastIndexOf(".")-1)+1, clsName.length());
        clsName=clsName.substring(clsName.lastIndexOf(".")+1, clsName.length());
        if(clsName.isEmpty()) return;
        //get a proper instance
        Logger subLog = Logger.getRootLogger();
        if(subLog==null) return;
        subLog.log(Level.DEBUG, " ("+pkage+","
                +ste.getLineNumber()+","+ste.getMethodName()+" )"+logcontent);
    }
}
