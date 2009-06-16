%define name jvoicexml
%define ver 0.7.0.EA

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
Packager: Dirk Schnelle-Walka <dirk.schnelle@jvoicexml.org>
Vendor: JVoiceXML group

%description
A free VoiceXML interpreter for JAVA with an open architecture for custom
extensions.
Demo implementation platforms are supporting JAVA APIs such as JSAPI and JTAPI.

JVoiceXML is an implementation of VoiceXML 2.1, the Voice Extensible
Markup Language, specified at http://www.w3.org/TR/2005/CR-voicexml21-20050613/
as an extension to VoiceXML 2.0, specified at http://www.w3.org/TR/voicexml20/.

VoiceXML is designed for creating audio dialogs that feature synthesized speech,
digitized audio, recognition of spoken and DTMF key input, recording of spoken
input, telephony, and mixed initiative conversations. Major goal is to have a
platform independent implementation that can be used for free.

%prep

%install

%post
if [[ ! -e /usr/local/jvoicexml/logging ]]; then
    mkdir /usr/local/jvoicexml/logging
fi
chmod o+rwx /usr/local/jvoicexml/logging
chmod oug+x /usr/local/jvoicexml/bin/startup.sh
chmod oug+x /usr/local/jvoicexml/bin/shutdown.sh

%changelog
* Fri Mar 13 2009 Dirk Schnelle-Walka <dirk.schnelle@jvoicexml.org>
  - Adaption to 0.7
* Mon Jun 2 2008 Dirk Schnelle <dirk.schnelle@jvoicexml.org>
  - Added support for tags enumerate, option, foreach, record
* Thu Jul 27 2006 Dirk Schnelle <dirk.schnelle@jvoicexml.org>
  - First RPM release.

%files
/usr/local/jvoicexml

