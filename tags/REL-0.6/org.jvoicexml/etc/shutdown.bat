@echo off 
rem Normally, editing this script should not be required.
rem Only case is to set up JAVA_HOME if it's not allready defined.
rem
rem To specify an alternative jvm, edit and uncomment the following line
rem set JAVA_HOME=C:\jdk

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if "%JAVA_CMD%" == "" set JAVA_CMD=%JAVA_HOME%\bin\java.exe
goto run

:noJavaHome
if "%JAVA_CMD%" == "" set JAVA_CMD=java.exe

:run

rem Resolve the location of the JVoiceXML installation.
rem This includes resolving any symlinks.
set JVOICEXML_HOME=%~dp0..

set JVOICEXML_LIB=%JVOICEXML_HOME%\lib

set LOCAL_CLASSPATH="%JVOICEXML_HOME%\config"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\log4j-1.2.15.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jvxml.jar"
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;"%JVOICEXML_LIB%\jvxml-client.jar"

cd %JVOICEXML_HOME%

"%JAVA_CMD%" %VMOPTIONS% -classpath %LOCAL_CLASSPATH% org.jvoicexml.RemoteShutdown
