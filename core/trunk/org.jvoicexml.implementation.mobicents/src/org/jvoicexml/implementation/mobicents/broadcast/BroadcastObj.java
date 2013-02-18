/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jvoicexml.implementation.mobicents.broadcast;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.Timestamp;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipURI;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author Shadowman
 */
public class BroadcastObj 
{
    public Integer broadcast_id;
    public String caller;
    public String called;
    public SipURI from_uri;
    public SipURI to_uri;
    public String vxml_uri;
    public String language;
    public Timestamp schedule_time;
    public Timestamp start_time;
    public Long duration;
    public Integer error_code;
    public BrcastObjState status;
    public Timestamp answer_time;
    public Timestamp hangup_time;
    public SipServletRequest invite;

    public String toString4() {
        return " BroadcastObj [[ broadcast_id:" + broadcast_id + " caller:" + caller + "  called: " + called
                + " from_uri: " + from_uri
                + " to_uri: " + to_uri + "  vxml_uri: " + vxml_uri + "  language: " + language
                + "  schedule_time: " + schedule_time + " start_time: " + start_time
                + "  duration: " + duration + "  error_code: " + error_code
                + "  answer_time: " + answer_time + " hangup_time:" + hangup_time;
    }

    public void setData(ResultSet cursor) {
    }

    public String toString2(StringBuilder strappend, Object obj) {
        if (strappend == null || obj == null) {
            return "";
        } else {
            return "";
        }
    }

    public String toString2() {
        final StringBuilder str = new StringBuilder();
        str.append(BroadcastObj.class.getCanonicalName());
        str.append('[');
        if (caller != null) {
            str.append(" caller:" + caller);
        }
        str.append(']');
        return str.toString();
    }

    public String toString3() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        result.append("BroadcastObj");
        result.append(" Object {");
        result.append(newLine);
        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();
        //print field names paired with their values
        for (Field field : fields) 
        {
            result.append("  ");
            try {
                Object value=field.get(this);
                if(value==null) continue;
                result.append(field.getName());
                result.append(": ");
                //requires access to private field:
                result.append(value);
            } catch (IllegalAccessException ex) {
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }

    public boolean isValid() {
        return true;
    }

    public static void main(final String[] args) {
        System.out.println("BroadcastObj.class.getCanonicalName():" + BroadcastObj.class.getCanonicalName());
        BroadcastObj obj = new BroadcastObj();
        obj.broadcast_id=1;
        System.out.println("reflectionToString3:" + obj.toString3());
        System.out.println("reflectionToString:" + obj);

    }
}
