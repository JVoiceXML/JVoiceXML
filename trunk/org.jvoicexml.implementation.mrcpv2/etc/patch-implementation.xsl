<?xml version="1.0"?>
  <!--
    - Configuration file for the JVoiceXML VoiceXML interpreter. -
    Copyright (C) 2009 JVoiceXML group -
    http://jvoicexml.sourceforge.net - - This library is free software;
    you can redistribute it and/or - modify it under the terms of the
    GNU Library General Public - License as published by the Free
    Software Foundation; either - version 2 of the License, or (at your
    option) any later version. - - This library is distributed in the
    hope that it will be useful, - but WITHOUT ANY WARRANTY; without
    even the implied warranty of - MERCHANTABILITY or FITNESS FOR A
    PARTICULAR PURPOSE. See the GNU - Library General Public License for
    more details. - - You should have received a copy of the GNU Library
    General Public - License along with this library; if not, write to
    the Free Software - Foundation, Inc., 59 Temple Place, Suite 330,
    Boston, MA 02111-1307 USA
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:beans="http://www.springframework.org/schema/beans"
  exclude-result-prefixes="beans" version="1.0">

  <xsl:template match="beans:bean[@id='implementationplatform']">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|comment()" />
    </xsl:copy>
    <xsl:comment>MRCPv2 SIP Server configuration</xsl:comment>
    <bean id="sipService" class="org.speechforge.zanzibar.sip.SipServer"
      init-method="startup" destroy-method="shutdown">
      <property name="mySipAddress">
        <value>sip:cairogate@speechforge.org</value>
      </property>
      <property name="stackName">
        <value>Mrcpv2SessionManager</value>
      </property>
      <property name="port">
        <value>5090</value>
      </property>
      <property name="transport">
        <value>UDP</value>
      </property>
      <property name="cairoSipAddress">
        <value>sip:cairo@speechforge.org</value>
      </property>
      <property name="cairoSipHostName">
        <value>localhost</value>
      </property>
      <property name="cairoSipPort">
        <value>5050</value>
      </property>
    </bean>
  </xsl:template>

  <!-- Adapt resource factories -->
  <xsl:template match="beans:property[@name='synthesizedoutput']">
    <xsl:copy>
      <xsl:apply-templates select="@*" />
      <list>
        <!--  Copy all existing beans. -->
        <xsl:copy-of select="beans:list/beans:bean" />
        <xsl:comment>MRCPv2 synthesized output</xsl:comment>
        <bean
          class="org.jvoicexml.implementation.mrcpv2.jvxml.SynthesizedOutputFactory">
          <property name="instances" value="1" />
        </bean>
      </list>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="beans:property[@name='spokeninput']">
    <xsl:copy>
      <xsl:apply-templates select="@*" />
      <list>
        <!--  Copy all existing beans. -->
        <xsl:copy-of select="beans:list/beans:bean" />
        <xsl:comment>MRCPv2 spoken input</xsl:comment>
        <bean
          class="org.jvoicexml.implementation.mrcpv2.jvxml.SpokenInputFactory">
          <property name="instances" value="1" />
        </bean>
      </list>
    </xsl:copy>
  </xsl:template>

  <!-- This template passes anything unmatched -->
  <xsl:template match="@*|*|text()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|comment()" />
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
