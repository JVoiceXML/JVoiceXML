Short description:

1. Get four project to your workspace.
   a. org.jvoicexml
   b. org.jvoicexml.implementation.text
   c. org.jvoicexml.systemtest (this project)
   d. org.jvoicexml.systemtest.servlet

2. Prepare servlet service. Tomcat as a example. 
   Modify org.jvoicexml/config-props/ant.properties follow line:
   servlet.lib.dir=MUST BE SUPLIED DIRECTORY to to fit your tomcat environment. 
   It look like servlet.lib.dir=/usr/local/tomcat/common/lib
   
   and org.jvoicexml.systemtest.servlet/build.xml tomcat.home property to fit your tomcat environment.
   
3. run "ant -f night.xml run" in org.jvoicexml.systemtest project.

4. Patch JVoiceXmlImplementationPlatform.patch to org.jvoicexml/src/org/jvoicexml/implementation/JVoiceXmlImplementationPlatform.java 
   for high pass rate.
   About the reason, see "http://sourceforge.net/mailarchive/message.php?msg_id=1225506454.7205.45.camel%40ubuntu-asus", please.
   