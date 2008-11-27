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

 <xsl:template match="beans:property[@name='identifier']">
  <xsl:copy>
   <xsl:apply-templates select="@*" />
   <list>
    <!--  Copy all existing beans. -->
    <xsl:copy-of select="beans:list/beans:bean" />
    <xsl:comment>JSAPI 1.0 grammar identifier</xsl:comment>
    <bean
     class="org.jvoicexml.implementation.jsapi10.grammar.JsgfGrammarIdentifier"/>
   </list>
  </xsl:copy>
 </xsl:template>

 <xsl:template match="beans:property[@name='transformer']">
  <xsl:copy>
   <xsl:apply-templates select="@*" />
   <list>
    <!--  Copy all existing beans. -->
    <xsl:copy-of select="beans:list/beans:bean" />
    <xsl:comment>JSAPI 1.0 grammar transformer</xsl:comment>
    <bean
     class="org.jvoicexml.implementation.jsapi10.grammar.Jsgf2JsgfGrammarTransformer"/>
    <bean
     class="org.jvoicexml.implementation.jsapi10.grammar.SrgsXml2JsgfGrammarTransformer"/>
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
