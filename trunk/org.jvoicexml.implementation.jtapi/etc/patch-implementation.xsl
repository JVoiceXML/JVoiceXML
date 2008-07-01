<?xml version="1.0"?>
<!--
 - Configuration file for the JVoiceXML VoiceXML interpreter. 
 - Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
 -
 - This library is free software; you can redistribute it and/or
 - modify it under the terms of the GNU Library General Public
 -  License as published by the Free Software Foundation; either
 - version 2 of the License, or (at your option) any later version.
 -
 - This library is distributed in the hope that it will be useful,
 - but WITHOUT ANY WARRANTY; without even the implied warranty of
 - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 - Library General Public License for more details.
 -
 - You should have received a copy of the GNU Library General Public
 - License along with this library; if not, write to the Free Software
 - Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:beans="http://www.springframework.org/schema/beans"
 exclude-result-prefixes="beans"
 version="1.0">

 <!-- Adapt resource factories -->
 <xsl:template match="beans:property[@name='telephony']">
  <xsl:copy>
   <xsl:apply-templates select="@*" />
   <list>
     <xsl:for-each select="beans:list">
       <xsl:apply-templates select="@*|*|text()|comment()" />
     </xsl:for-each>
    <bean
     class="org.jvoicexml.implementation.jtapi.JtapiTelephonyFactory">
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
