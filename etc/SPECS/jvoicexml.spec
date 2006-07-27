%define name jvoicexml
%define ver 0.5

Summary: A free VoiceXML implementation.
Name: %name
Version: %ver
# This indicates changes to the spec file after last time %ver has changed.
Release: 1
License: LGPL
Group: Applications/Internet
#Source: jvxml-src-%{ver}.tar.gz
URL: http://www.jvoicexml.org/
BuildRoot: /var/tmp/%{name}-%{ver}
Packager: Dirk Schnelle <dirk.schnelle@jvoicexml.org>
Vendor: JVoiceXML group

%description
A free VoiceXML interpreter for JAVA supporting JAVA APIs such as JSAPI and
JTAPI.

JVoiceXML is an implementation of VoiceXML 2.1, the Voice Extensible
Markup Language, specified at http://www.w3.org/TR/2005/CR-voicexml21-20050613/
as an extension to VoiceXML 2.0, specified at http://www.w3.org/TR/voicexml20/.

VoiceXML is designed for creating audio dialogs that feature synthesized speech,
digitized audio, recognition of spoken and DTMF key input, recording of spoken
input, telephony, and mixed initiative conversations. Major goal is to have a
platform independent implementation that can be used for free.

%prep
rm -rf /var/tmp/%{name}-%{ver}

%install
mkdir -p /var/tmp/%{name}-%{ver}/usr/local/jvoicexml
cp -r /home/dirk/src/JVoiceXML/trunk/dist/%{ver}/* /var/tmp/%{name}-%{ver}/usr/local/jvoicexml
rm -f /var/tmp/%{name}-%{ver}/usr/local/jvoicexml/*.txt

%post
mkdir /usr/local/jvoicexml/logging
chmod o+rwx /usr/local/jvoicexml/logging

%changelog
* Thu Jul 27 2006 Dirk Schnelle <dirk.schnelle@jvoicexml.org>
  - First RPM release.

%files
/usr/local/jvoicexml
