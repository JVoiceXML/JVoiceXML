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

JVOICEXML_HOME=`dirname "${JVOICEXML_BIN}"`
JVOICEXML_LIB="${JVOICEXML_HOME}/lib"

LOCAL_CLASSPATH="$JVOICEXML_HOME/config"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/commons-logging.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/commons-pool-1.3.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/commons-httpclient-3.1.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/log4j-1.2.15.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/cmulex.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/cmu_us_kal.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/en_us.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/freetts.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jsapi.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/js.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/spring-beans.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/spring-core.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/shinx4.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/WSJ_8gau_13dCep_16k_40mel_130Hz_6800Hz.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/gjtapi-1.8.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jtapi-1.3.1.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jtapi-1.3.1-mediaImpl.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/JainSipApi1.1.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/nist-sdp-1.0.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/nist-sip-1.2.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/sip-sdp.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/Stun4J.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jmf.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jlibrtp.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jsr173_1.0_api.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/sjsxp.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jvxml.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jvxml-jsapi1.0.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jvxml-jsapi1.0-impl.jar"
#LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jvxml-jsapi2.0.jar"
#LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jvxml-jsapi2.0-impl.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jvxml-text.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jvxml-xml.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jvxml-client.jar"
LOCAL_CLASSPATH=${LOCAL_CLASSPATH}:"${JVOICEXML_LIB}/jvxml-jtapi1.3.jar"

cd ${JVOICEXML_HOME}

$JAVA_CMD $VMOPTIONS -classpath $LOCAL_CLASSPATH org.jvoicexml.JVoiceXmlMain
