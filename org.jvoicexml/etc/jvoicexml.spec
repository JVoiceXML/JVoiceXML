%global ver     @jvxml.version.raw@

Name:           jvoicexml
Version:        %{ver}.1
Summary:        A free VoiceXML implementation
Release:        2
License:        GPLv2
Source0:        http://downloads.sf.net/project/%{name}/%{name}/@jvxml.version@/jvxml-@jvxml.version@.zip
Source1:        jvoicexml.service
#Source2:        http://downloads.sf.net/project/%{name}/userguide/%{ver}.GA/jvxml-userguide-%{ver}.GA.pdf
URL:            http://%{name}.sf.net/
BuildArch:      noarch

BuildRequires:  java
Requires:       java ant

%description
A free VoiceXML interpreter for JAVA with an open architecture for custom
extensions.
Demo implementation platforms are supporting JAVA APIs such as JSAPI and JTAPI.

JVoiceXML is an implementation of VoiceXML 2.1, the Voice Extensible
Markup Language, specified at http://www.w3.org/TR/voicexml21/
as an extension to VoiceXML 2.0, specified at http://www.w3.org/TR/voicexml20/.

VoiceXML is designed for creating audio dialogs that feature synthesized speech,
digitized audio, recognition of spoken and DTMF key input, recording of spoken
input, telephony, and mixed initiative conversations. Major goal is to have a
platform independent implementation that can be used for free.

%prep
%autosetup -c %{name}-%{ver}

%install
cd %{buildroot}

%post
chmod o+rwx /var/log/jvoicexml
chmod oug+x /usr/share/jvoicexml/bin/startup.sh
chmod oug+x /usr/share/jvoicexml/bin/shutdown.sh

%changelog
* Sun Oct 05 2014 Raphael Groner <projects.rg [AT] smart.ms>  - 0.7.6.1-2
- cleanup for Fedora 20+
- add systemd service
* Fri Mar 13 2009 Dirk Schnelle-Walka <dirk.schnelle [AT] jvoicexml.org>
- Adaption to 0.7
* Mon Jun 2 2008 Dirk Schnelle <dirk.schnelle [AT] jvoicexml.org>
- Added support for tags enumerate, option, foreach, record
* Thu Jul 27 2006 Dirk Schnelle <dirk.schnelle [AT]jvoicexml.org>
- First RPM release.

