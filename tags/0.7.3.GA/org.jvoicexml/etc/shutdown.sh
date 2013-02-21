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

JVOICEXML_HOME=`dirname "${JVOICEXML_BIN}"`
JVOICEXML_LIB="${JVOICEXML_HOME}/lib"

VMOPTIONS="-Djava.security.policy=config/jvoicexml.policy"
VMOPTIONS="${VMOPTIONS} -Djava.rmi.server.codebase=file://${JVOICEXML_HOME}/lib/jvxml.jar"

LOCAL_CLASSPATH="$JVOICEXML_HOME/config"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/log4j-1.2.15.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jvxml.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/org.jvoicexml.jndi.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jvxml-client.jar"

cd ${JVOICEXML_HOME}

$JAVA_CMD $VMOPTIONS -classpath $LOCAL_CLASSPATH org.jvoicexml.RemoteShutdown
