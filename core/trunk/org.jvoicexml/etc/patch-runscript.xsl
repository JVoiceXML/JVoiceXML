<?xml version="1.0"?>
  <!--
    - Platform configurations for the JVoiceXML VoiceXML interpreter.
    - Copyright (C) 2011 JVoiceXML group
    - http://jvoicexml.sourceforge.net
    -
    - This library is free software; you can redistribute it and/or
    - modify it under the terms of the GNU Library General Public
    - License as published by the Free Software Foundation; either
    - version 2 of the License, or (at your option) any later version.
    -
    - This library is distributed in the hope that it will be useful,
    - but WITHOUT ANY WARRANTY; without even the implied warranty of
    - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    - Library General Public License for more details.
    -
    - You should have received a copy of the GNU Library General Public
    - License along with this library; if not, write to the Free Software
    - Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">
  <xsl:template match="platforms">
    <project name="JVoiceXML Platforms">
      <description>
        Generated build file to integrate the specified implementation
        platforms into the overall build process.
        Do not edit!
      </description>
      <target name="-buildPlatforms">
        <xsl:apply-templates select="platform">
          <xsl:with-param name="target">jar</xsl:with-param>
          <xsl:with-param name="action">building</xsl:with-param>
        </xsl:apply-templates>
      </target>
      <target name="-cleanPlatforms">
        <xsl:apply-templates select="platform">
          <xsl:with-param name="target">clean</xsl:with-param>
          <xsl:with-param name="action">cleaning</xsl:with-param>
        </xsl:apply-templates>
      </target>
      <target name="-apidocPlatforms">
        <xsl:apply-templates select="platform">
          <xsl:with-param name="target">apidoc</xsl:with-param>
          <xsl:with-param name="action">creating apidoc for</xsl:with-param>
        </xsl:apply-templates>
      </target>
      <target name="configurePlatforms">
        <xsl:apply-templates select="platform">
          <xsl:with-param name="target">configuration</xsl:with-param>
          <xsl:with-param name="action">configuring</xsl:with-param>
        </xsl:apply-templates>
      </target>
    </project>
  </xsl:template>
  
  <!-- Adapt the jvmarg values -->
  <xsl:template match="platform">
    <xsl:param name="action"/>
    <xsl:param name="target"/>
    <echo>
      <xsl:value-of select="$action"/><xsl:value-of select="concat(' ', text())"/>
    </echo>
    <ant target="{$target}"
        inheritall="false" inheritrefs="false">
       <xsl:attribute name="dir">
         <xsl:value-of select="concat('../', text())"></xsl:value-of>
       </xsl:attribute>
    </ant>
  </xsl:template>

  <!-- This template passes anything unmatched -->
  <xsl:template match="@*|*|text()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|comment()" />
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
