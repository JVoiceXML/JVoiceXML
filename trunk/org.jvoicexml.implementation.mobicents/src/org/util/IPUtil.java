/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.util;

/**
 *
 * @author Shadowman
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class IPUtil 
{
    
    private static final Logger LOGGER = Logger.getLogger(IPUtil.class);

    private static final String IP_SEPERATOR = "(\\p{Space}|\\p{Punct})";
    private static final String IP_COMPONENT = "(1?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";
    private static final Pattern IP_PATTERN = Pattern.compile("(?<=(^|[" + IP_SEPERATOR
            + "]))(" + IP_COMPONENT + "\\.){3}" + IP_COMPONENT + "(?=([" + IP_SEPERATOR + "]|$))");
    private List<String> ips;

    /**
     * Constructs a new IPFinder for the given string
     * @param s
     */
    public IPUtil(String s) {
        ips = findIPAddresses(s);
    }

    /**
     * @return the list of IPs found in the String supplied during construction
     */
    public List<String> getIPs() {
        return ips;
    }

    /**
     * @return the list of unique IPs found in the String supplied during construction
     */
    public Set<String> getUniqueIPs() {
        return new TreeSet<String>(ips);
    }

    /**
     * Utility method to extract IP addresses, non unique
     * @param s
     */
    public static Set<String> findUniqueIPAddresses(String s) {
        return new TreeSet<String>(findIPAddresses(s));
    }

    /**
     * Utility method to extract IP addresses, non unique
     * @param s
     */
    public static List<String> findIPAddresses(String s) {
        List<String> ips = new ArrayList<String>();

        Matcher m = IP_PATTERN.matcher(s);
        while (m.find()) {
            String ip = m.group();
            ips.add(ip);
        }

        return ips;
    }
     /**
     * Utility method to extract IP addresses, non unique
     * @param s
     */
    public static String findFirstIPAddr(String s) 
    {
        Matcher m = IP_PATTERN.matcher(s);
        while (m.find()) {
            String ip = m.group();
            if(ip!=null && ip.isEmpty()==false)
                return ip;
        }
        return "";
    }

    /**
     * Validates an IP address
     * @param ip
     * @return true if it's a valid IP address
     */
    public static boolean isValidIP(String ip) {
        return IP_PATTERN.matcher(ip).matches();
    }
    
    public static String getPort(String url) 
    {
        if (url == null || url.isEmpty() ) {
            return "";
        }
        String hostport = TextUtil.subString(url, "http://", "/");
        if(hostport.indexOf(":")==-1)
            return "80";
        else
            return hostport.split(":")[1];
    }
    public static String getHost(String url) 
    {
        if (url == null || url.isEmpty() ) {
            return "";
        }
        String hostport = TextUtil.subString(url, "http://", "/");
        return hostport.split(":")[0];
    }

    /**
     * Test
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        /* This string contains the IPs that should be matched:
         *   10.85.0.1
         *   182.54.233.31
         *   192.168.0.1
         *   41.43.132.23
         *   0.0.0.0
         *   255.255.255.255
         *   127.0.0.0
         *   127.0.0.1
         *   127.0.0.2
         *   10.0.0.0
         *   10.255.255.255
         *   172.16.0.0
         *   172.31.255.255
         *   192.168.0.0
         *   192.168.255.255
         *   196.38.192.23
         *
         * And one string in the format of an IP that shouldn't be matched:
         *   918.343.54.123
         *   32.256.241.23
         */
        String s =
                "An IP address consists of 4 bytes, which when represented in a readable form\n"
                + "is done so by the numeric value of each byte, separated by a dot (.).\n"
                + "An few examples would be: 10.85.0.1, 182.54.233.31, 192.168.0.1\n"
                + "and 41.43.132.23. Since each component is a byte, their ranges would be from 0\n"
                + "to 255. So valid IPs are in the range 0.0.0.0 to 255.255.255.255. Though neither of\n"
                + "these 2 is valid themselves. So if you see an IP address on the CSI show like\n"
                + "918.343.54.123, then you know it's not valid because it's individual components\n"
                + "contain numbers that are too large for bytes. Another example which is very close\n"
                + "to but still not a match would be: 32.256.241.23.\n"
                + "\n"
                + "The IP address for your local machine is 127.0.0.1.\n"
                + "127.0.0.1 is called the loopback ip, and is associated with virtual interface\n"
                + "interface to allow you to do network type communications without having\n"
                + "to involve your physical network device. Most operating systems allow\n"
                + "any communicatations to 127.0.0.0 IPs to be treated as loopback IPs, and thus\n"
                + "you can run software bound to different loopback IPs as if you are running\n"
                + "the on different machines. So if you want to have 2 different programs run\n"
                + "on the same port and communicte to them via loopback, you can have the first\n"
                + "listen on the port for IP 127.0.0.1 and the other on the same port for 127.0.0.2\n"
                + "and then just communicate to them on these IPs.\n"
                + "\n"
                + "Further, you get private network IPs like the class A 10.0.0.0 to 10.255.255.255,\n"
                + "the class B 172.16.0.0 to 127.31.255.255, and the class C 192.168.0.0 to 192.168.255.255.\n"
                + "All other IPs like 196.38.192.23 is public internet IPs assigned by an authority like\n"
                + "AfriNic.";

        System.out.println("In the paragraph:");
        System.out.println("---------------------------------------------------------------------------");
        System.out.println(s);
        System.out.println("---------------------------------------------------------------------------");
        System.out.println();
        IPUtil finder = new IPUtil(s);
        Set<String> ips = finder.getUniqueIPs();
        System.out.println("We found these " + ips.size() + " ips:");
        for (String ip : ips) {
            System.out.println(" -> " + ip);
        }

        System.out.println();
        String[] checkIPs = new String[]{
            "32.256.241.23",
            "918.343.54.123",
            "192.168.10.5"
        };
        for (String ip : checkIPs) {
            System.out.println(ip + " is a valid IP: " + isValidIP(ip));
        }
        
        //
        String via="Via: SIP/2.0/UDP 10.50.144.66;branch=z9hG4bKf7b1.1909fc2.0";
        System.out.println(" the ip is a valid IP: " + IPUtil.findFirstIPAddr(via));
    }
}
