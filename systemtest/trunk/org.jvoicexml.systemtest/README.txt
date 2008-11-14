Short description:

1.To get irtests.zip from w3c.org(http://www.w3.org/Voice/2004/vxml-ir/irtests.zip). 
    Unzip, and put to irtest/irtests directory.

2.Configure web server for translating template to VXML file which include by irtests.zip.
  Requested xslt file included also. There are a tomcat solution that be put irtest/WEB-INF directory, 
  source and WEB-INF.xml.

3.Modify org.jvoicexml log4j.xml file to add SOCKET_HUB appender. It used for collecting log.
  An example was at org.jvoicexml directory.
  There has a small issue. When use RemoteShutdown.class, will throw a exception, but it work fine still.
 
4.Start jvoicexml interpreter first. Then call SystemTestMain.class, test will auto execute according to 
  systemtestconfig.xml. Or use ant -f run.xml run .
  
5. About result, see irtest/results/ir-report.xml file.

