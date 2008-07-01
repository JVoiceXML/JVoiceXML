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
 version="1.0">

 <xsl:param name="distpath" />
 <xsl:param name="thirdparty" />

 <!-- Adapt the classpath -->
 <xsl:template match="path[@id='run.classpath']">
  <xsl:copy>
   <!-- Keep current path -->
   <xsl:apply-templates select="@*|*|text()|comment()" />
   <!-- Append component path -->
   <xsl:comment>JTAPI libraries</xsl:comment>
   <fileset id="jvxml.jtapi.lib">
    <xsl:attribute name="dir">
        <xsl:value-of select="$distpath" />
    </xsl:attribute>
    <include name="jvxml-jtapi.jar" />
   </fileset>
   <fileset id="jtapi1.3.lib">
     <xsl:attribute name="dir">
       <xsl:value-of select="concat($thirdparty, '/jtapi1.3/lib')" />
     </xsl:attribute>
     <include name="*.jar"/>
   </fileset>
   <fileset id="gjtapi1.8.lib">
     <xsl:attribute name="dir">
       <xsl:value-of select="concat($thirdparty, '/gjtapi1.8/lib')" />
     </xsl:attribute>
     <include name="*.jar"/>
   </fileset>
   <fileset id="jainsip1.1.lib">
     <xsl:attribute name="dir">
       <xsl:value-of select="concat($thirdparty, '/jainsip1.1/lib')" />
     </xsl:attribute>
     <include name="*.jar"/>
   </fileset>
   <fileset id="stun4j.lib">
     <xsl:attribute name="dir">
       <xsl:value-of select="concat($thirdparty, '/stun4j/lib')" />
     </xsl:attribute>
     <include name="*.jar"/>
   </fileset>
  </xsl:copy>
 </xsl:template>

 <!-- This template passes anything unmatched -->
 <xsl:template match="@*|*|text()|comment()">
  <xsl:copy>
   <xsl:apply-templates select="@*|*|text()|comment()" />
  </xsl:copy>
 </xsl:template>

</xsl:stylesheet>
