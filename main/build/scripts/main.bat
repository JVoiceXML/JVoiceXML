@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  main startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and MAIN_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Djvoicexml.config=C:\Users\dwalka\Documents\Entwicklung\JVoiceXML.git\JVoiceXML\org.jvoicexml/config"

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\main-0.7.8.jar;%APP_HOME%\lib\org.jvoicexml.client-0.7.8.jar;%APP_HOME%\lib\org.jvoicexml.client-0.7.8.jar;%APP_HOME%\lib\org.jvoicexml.profile.vxml21-0.7.8.jar;%APP_HOME%\lib\org.jvoicexml.xml-0.7.8.jar;%APP_HOME%\lib\org.jvoicexml-0.7.8.jar;%APP_HOME%\lib\org.jvoicexml.config-0.7.8.jar;%APP_HOME%\lib\org.jvoicexml.srgs-0.7.8.jar;%APP_HOME%\lib\log4j-1.2.17.jar;%APP_HOME%\lib\protobuf-java-3.1.0.jar;%APP_HOME%\lib\rhino-1.7R4.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar;%APP_HOME%\lib\jetty-server-9.3.14.v20161028.jar;%APP_HOME%\lib\jchardet-1.0.jar;%APP_HOME%\lib\httpcore-4.4.5.jar;%APP_HOME%\lib\httpclient-4.5.2.jar;%APP_HOME%\lib\commons-pool-1.6.jar;%APP_HOME%\lib\commons-lang3-3.5.jar;%APP_HOME%\lib\spring-context-4.3.4.RELEASE.jar;%APP_HOME%\lib\spring-beans-4.3.4.RELEASE.jar;%APP_HOME%\lib\jetty-http-9.3.14.v20161028.jar;%APP_HOME%\lib\jetty-io-9.3.14.v20161028.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-codec-1.9.jar;%APP_HOME%\lib\spring-aop-4.3.4.RELEASE.jar;%APP_HOME%\lib\spring-core-4.3.4.RELEASE.jar;%APP_HOME%\lib\spring-expression-4.3.4.RELEASE.jar;%APP_HOME%\lib\jetty-util-9.3.14.v20161028.jar

@rem Execute main
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %MAIN_OPTS%  -classpath "%CLASSPATH%" org.jvoicexml.startup.Startup %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable MAIN_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%MAIN_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
