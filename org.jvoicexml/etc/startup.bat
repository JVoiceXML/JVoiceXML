@echo off 
rem Normally, editing this script should not be required.
rem Only case is to set up JAVA_HOME if it's not already defined.
rem
rem To specify an alternative jvm, edit and uncomment the following line
rem set JAVA_HOME=C:\jdk

title JVoiceXML

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if "%JAVA_CMD%" == "" set JAVA_CMD=%JAVA_HOME%\bin\java.exe
goto run

:noJavaHome
if "%JAVA_CMD%" == "" set JAVA_CMD=java.exe

:run

set VMOPTIONS=-mx256m
set VMOPTIONS=%VMOPTIONS% -Djava.util.logging.config.file=config/logging.properties
set VMOPTIONS=%VMOPTIONS% -Djava.security.policy=config/jvoicexml.policy

rem Allow external XML-ENTITIES
rem set VMOPTIONS=%VMOPTIONS% -Dorg.jvoicexml.xml.resolveEntities=true

rem GJTAPI settings
set VMOPTIONS=%VMOPTIONS% -Dgjtapi.sip.properties=/gjtapi-provider.properties

rem Enable RTP streaming via jlibrtp
set VMOPTIONS=%VMOPTIONS% -Djava.protocol.handler.pkgs=org.jlibrtp.protocols

rem Disallow FreeTTS to add jars to the classpath
set VMOPTIONS=%VMOPTIONS% -Dfreetts.nocpexpansion=true

rem Resolve the location of the JVoiceXML installation.
rem This includes resolving any symlinks.
set JVOICEXML_HOME=%~dp0..
set VMOPTIONS=%VMOPTIONS% -Djava.library.path="%JVOICEXML_HOME%\lib"

set JVOICEXML_LIB=%JVOICEXML_HOME%\lib

set LOCAL_CLASSPATH="%JVOICEXML_HOME%\config"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\log4j-1.2.17.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\spring-core-4.2.0.RELEASE.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\spring-beans-4.2.0.RELEASE.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\spring-context-4.2.0.RELEASE.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\spring-expression-4.2.0.RELEASE.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\commons-pool-1.5.5.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\httpcore-4.2.3.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\httpcore-nio-4.2.3.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\httpcore-ab-4.2.3.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\httpclient-4.2.3.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\httpclient-cache-4.2.3.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\fluent-hc-4.2.3.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\httpmime-4.2.3.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\chardet.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jmf.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jlibrtp.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\commons-logging-1.1.1.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\commons-codec-1.4.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\commons-lang3-3.3.2.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jetty-http-9.2.2.v20140723.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jetty-io-9.2.2.v20140723.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jetty-server-9.2.2.v20140723.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jetty-util-9.2.2.v20140723.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\servlet-api-3.1.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\org.jvoicexml.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\org.jvoicexml.xml.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\org.jvoicexml.config.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\org.jvoicexml.srgs.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\org.jvoicexml.client.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\org.jvoicexml.jndi.jar"

cd "%JVOICEXML_HOME%"

"%JAVA_CMD%" %VMOPTIONS% -classpath %LOCAL_CLASSPATH% org.jvoicexml.startup.Startup
