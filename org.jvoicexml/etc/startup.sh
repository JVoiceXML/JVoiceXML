#!/bin/bash
#
# Normally, editing this script should not be required.
# Only case is to set up JAVA_HOME if it's not allready defined.
#
# To specify an alternative jvm, edit and uncomment the following line
# JAVA_HOME=/usr/lib/java

JAVA_CMD="java"
if [ -n $JAVA_HOME ] ; then
    _TMP="$JAVA_HOME/bin/java"
    if [ -f "$_TMP" ] ; then
        if [ -x "$_TMP" ] ; then
            JAVA_CMD="$_TMP"
        else
            echo "Warning: $_TMP is not executable"
        fi
    else
        echo "Warning: $_TMP does not exist"
    fi
fi
if ! which "$JAVA_CMD" >/dev/null ; then
    echo "Error: No java environment found"
    exit 1
fi

VMOPTIONS=-mx256m

#
# Resolve the location of the JVoiceXML installation.
# This includes resolving any symlinks.
PRG=$0
while [ -h "$PRG" ]; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '^.*-> \(.*\)$' 2>/dev/null`
    if expr "$link" : '^/' 2> /dev/null >/dev/null; then
        PRG="$link"
    else
        PRG="`dirname "$PRG"`/$link"
    fi
done

JVOICEXML_BIN=`dirname "$PRG"`

# absolutize dir
oldpwd=`pwd`
cd "${JVOICEXML_BIN}"; JVOICEXML_BIN=`pwd`
cd "${oldpwd}"; unset oldpwd

VMOPTIONS=-mx256m
VMOPTIONS=%VMOPTIONS% -Djava.util.logging.config.file=config/logging.properties
VMOPTIONS=%VMOPTIONS% -Djava.security.policy=config/jvoicexml.policy

# GJTAPI settings
VMOPTIONS=${VMOPTIONS} -Dgjtapi.sip.properties=/gjtapi-provider.properties

# Enable RTP streaming via jlibrtp
VMOPTIONS=${VMOPTIONS} -Djava.protocol.handler.pkgs=org.jlibrtp.protocols

 Disallow FreeTTS to add jars to the classpath
VMOPTIONS=${VMOPTIONS} -Dfreetts.nocpexpansion=true

JVOICEXML_HOME=`dirname "${JVOICEXML_BIN}"`
JVOICEXML_LIB="${JVOICEXML_HOME}/lib"

LOCAL_CLASSPATH="$JVOICEXML_HOME/config"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/log4j-1.2.15.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/js.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/spring-core.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/spring-beans.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/commons-pool-1.3.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/commons-logging.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/commons-codec-1.3.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/commons-httpclient-3.1.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jmf.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jlibrtp.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jvxml.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jvxml-xml.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jvxml-client.jar"

cd ${JVOICEXML_HOME}

$JAVA_CMD $VMOPTIONS -classpath $LOCAL_CLASSPATH org.jvoicexml.JVoiceXmlMain
