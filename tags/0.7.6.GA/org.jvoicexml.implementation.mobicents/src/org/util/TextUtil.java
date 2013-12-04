/*
 * Copyright (c) 1996-2001
 * vnxtele Corporation - VAS Center
 * @author ShadowMan - vnxtele Mobile Networks - VAS
 * @version 1.0, 25 January 2012
 * All rights reserved.
 *
 * This software is distributed under vnxtele Open Source License Version 1.0
 * ("Licence Agreement"). You shall use it and distribute only in accordance
 * with the terms of the License Agreement.
 *
 */

package org.util;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;

import org.w3c.dom.Document;


/**
 *
 * @author shadowman
 */
public class TextUtil 
{
    private static final Logger LOGGER = Logger.getLogger(TextUtil.class);
    public static String subString(String input,String startStr,String endStr)
    {
        try{            
            int idxStart=input.indexOf(startStr);
            if(idxStart==-1) return "";
            int idxEnd=input.indexOf(endStr,idxStart+1+startStr.length());
            if(idxStart==-1 || idxEnd==-1) return "";
            return input.substring(idxStart+startStr.length(),idxEnd).trim();
        }
        catch(Exception ex){}
        return "";
    }
//    public static String subString(String linenumber,String startStr,String endStr)
//    {
//        try{            
//            int idxStart=input.indexOf(startStr);
//            if(idxStart==-1) return "";
//            int idxEnd=input.indexOf(endStr,idxStart+1+startStr.length());
//            if(idxStart==-1 || idxEnd==-1) return "";
//            return input.substring(idxStart+startStr.length(),idxEnd).trim();
//        }
//        catch(Exception ex){}
//        return "";
//    }
    
     public String xmlFilePrint(String fileName) 
     {
        try {
            File file = new File(fileName);
            DocumentBuilderFactory documentBuilderFactory =
            DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder;
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
      public static String standardPath(String path)
    {
        try
        {
            if(path.indexOf("\\")!=-1)
                path= path.replace('\\',File.separatorChar);
            if(path.indexOf("/")!=-1)
                path= path.replace('/',File.separatorChar);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            ExLog.exception(LOGGER, ex);
        }
        return path;
    }
         public static String osResPath(String path)
    {
        try
        {
            path= path.replace(File.separatorChar,'/');
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            ExLog.exception(LOGGER, ex);
        }
        return path;
    }
    
    public static String replaceSDP(String input)
    {
        String sdp="";
        sdp="v=0\n"
                +"o=- 3080742675 1 IN IP4 ?\n"
                +"s=SIPPER for phoner\n"
                +"c=IN IP4 ?\n"
                +"t=0 0\n"
                +"m=audio 7062 RTP/AVP 8 0 2 3 97 9 111 112 113 114 101\n"
                +"a=rtpmap:8 PCMA/8000\n"
                +"a=rtpmap:0 PCMU/8000";
        try{            
            //find 
            sdp=sdp.replaceAll("IP4.*", "192.168.146.146");
            System.out.println("sdp:"+sdp);
            
        }
        catch(Exception ex){}
        return sdp ;
    }
      public static void main(final String[] args)
    {
        try{
            String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                    + "<S:Body><mdiRequestResponse xmlns=\"http://mdiws/xsd\">"
                    + "<return>200</return></mdiRequestResponse></S:Body></S:Envelope>";
            System.out.println("substring:"+TextUtil.subString(xml, "<return>", "</return>"));
            System.out.println("substring:"+TextUtil.subString(xml, "mdiRequestResponse", "</mdiRequestResponse>"));
            String signal="Max-Forwards: 70"+
"\nContent-Length: 26"+
"\n"+
"\nSignal= 1"+
"\nDuration= 180";
            String sig = signal.substring(signal.indexOf("Signal=")+"Signal=".length()
                    ,signal.indexOf("Signal=")+"Signal=".length()+2).trim();
             System.out.println("got INFO request with following content " + signal 
                    + " sig:"+sig + " charAt0:"+sig.trim().charAt(0));
//            System.out.println("standardPath:"+standardPath("\\abc\\cdef\\"));
//        System.out.println("standardPath:"+standardPath("/abc/cdef/"));
            String header=
                    "GET /vn/index.aspx HTTP/1.1\r\n"+
"Host: localhost:30011\r\n"+
"User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:9.0.1) Gecko/20100101 Firefox/9.0.1\r\n"+
"Vsessionid: A59904F0A41EA7C2402B8473A4CC336652DD947BB74571289F49D7219EAD3027B6ECF8EBE0A310F0ADD5C1A942EC5EDCEB6DB9CF5C777854D807941BD332F554\r\n"+
"VHost: 192.168.146.146\r\n"+
"VX-Forwarded-For: 127.0.0.1\r\n"+
"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n"+
"Accept-Language: en-us,en;q=0.5\r\n"+
"Accept-Encoding: gzip, deflate\r\n"+
"Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\r\n"+
//                    "Referer: http://mstore.vn/vn/app/5/index.aspx\r\n"+
"\r\n"+                    
"Connection: keep-alive\r\n";
            
            String sdp="v=0\n"
                +"o=- 3080742675 1 IN IP4 ?\n"
                +"s=SIPPER for phoner\n"
                +"c=IN IP4 ?\n"
                +"t=0 0\n"
                +"m=audio 7062 RTP/AVP 8 0 2 3 97 9 111 112 113 114 101\n"
                +"a=rtpmap:8 PCMA/8000\n"
                +"a=rtpmap:0 PCMU/8000";
            sdp=sdp.replaceAll("IP4.*", "192.168.146.146");
//            System.out.println("sdp:"+sdp);
                    
//                    String header="HTTP/1.1 200 OK\r\n"+
//"Date: Tue, 21 Feb 2012 08:18:22 GMT\r\n"+
//"Server: Microsoft-IIS/6.0\r\n"+
//"X-Powered-By: ASP.NET\r\n"+
//"X-AspNet-Version: 2.0.50727\r\n"+
//"Cache-Control: private\r\n"+
//"Content-Type: text/html; charset=utf-8\r\n"+
//"Content-Length: 15509\r\n";
//            System.err.println(TextUtil.subString(header, "VX-Forwarded-For:", "\n"));
//            System.err.println(TextUtil.subString(header, "Vsessionid:", "\n"));
            
//            header = "sessionID:127001|20120109100030|fb3d12be-9d96-471b-9312-0717b309de57";
//            String regex = "<\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]>"; // matches <http://google.com>
            
//            String regex = "(?m)^\\s*$[\n\r]{1,}"; // matches <http://google.com>

//            System.out.println("header:"+header.indexOf("\\r\\n"));
//             Pattern re = Pattern.compile("^\\s*$");
//            Matcher mat = re.matcher(header);
            
//            Pattern CRLF = Pattern.compile("HTTP.*200.*OK");
            String test = "9";
//            Pattern CRLF = Pattern.compile("(99999.*)");
              Pattern CRLF = Pattern.compile("(99999.*)");
            
            Matcher m = CRLF.matcher(test);
 
            while(m.find())
            {
                System.out.println("find: start:"+m.start());
            }
            
//            if (m.find()) {
//              newString = m.replaceAll("<br>");
//            }
//            System.err.println(TextUtil.subString(header, "|", "|"));
            
//            System.out.println(TextUtil.subString(header, "Referer: http://", "/"));
//            TextUtil.
//            System.out.println("matches:"+mat.matches());
            
         }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

    }
    
}
