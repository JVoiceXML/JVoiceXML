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

rem Resolve the location of the JVoiceXML installation.
rem This includes resolving any symlinks.
set JVOICEXML_HOME=%~dp0..

set JVOICEXML_LIB=%JVOICEXML_HOME%\lib

set LOCAL_CLASSPATH="%JVOICEXML_HOME%\config"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\commons-logging.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\commons-pool-1.3.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\commons-httpclient-3.1.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\log4j-1.2.15.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\cmulex.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\cmu_us_kal.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\en_us.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\freetts.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jsapi.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\js.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\spring-beans.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\spring-core.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\shinx4.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\WSJ_8gau_13dCep_16k_40mel_130Hz_6800Hz.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\gjtapi-1.8.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jtapi-1.3.1.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jtapi-1.3.1-mediaImpl.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\JainSipApi1.1.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\nist-sdp-1.0.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\nist-sip-1.2.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\sip-sdp.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\Stun4J.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jmf.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jlibrtp.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jsr173_1.0_api.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\sjsxp.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jvxml.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jvxml-jsapi1.0.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jvxml-jsapi1.0-impl.jar"
rem set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jvxml-jsapi2.0.jar"
rem set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jvxml-jsapi2.0-impl.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jvxml-text.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jvxml-xml.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jvxml-client.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jvxml-jtapi1.3.jar"

cd %JVOICEXML_HOME%

"%JAVA_CMD%" %VMOPTIONS% -classpath %LOCAL_CLASSPATH% org.jvoicexml.JVoiceXmlMain
