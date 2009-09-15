<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:srgs="http://www.w3.org/2001/06/grammar">

<!--
    SRGS 1.0 Stylesheet to convert grammars from XML to JSGF.
    
    Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
 
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Library General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or (at your option) any later version.
 
    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Library General Public License for more details.
 
    You should have received a copy of the GNU Library General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Based on
    SRGS 1.0 Stylesheet to convert grammars from XML to ABNF Form (20020524)

    Copyright 1998-2002 W3C (MIT, INRIA, Keio), All Rights Reserved. 

    Permission to use, copy, modify and distribute the SRGS XML to 
    ABNF stylesheet and its accompanying documentation
    for any purpose and without fee is hereby granted in
    perpetuity, provided that the above copyright notice and this
    paragraph appear in all copies.  The copyright holders make no
    representation about the suitability of the schema for any purpose. 
    It is provided "as is" without expressed or implied warranty.
-->

<xsl:strip-space 
elements="srgs:rule srgs:example srgs:item srgs:one-of 
srgs:token"/>

<xsl:output method="text"/>

<xsl:template name="addweight">
<xsl:if test="string(@weight)!=''">/<xsl:value-of select="@weight"/>/ </xsl:if>
</xsl:template>

<xsl:template name="addlang">
  <xsl:if test="string(@xml:lang)!=''">!<xsl:value-of select="@xml:lang"/> </xsl:if>
</xsl:template>


<xsl:template name="addexamples">
<xsl:if test="count(srgs:example)>0">
/** <xsl:for-each select="srgs:example">
* @example <xsl:value-of select="text()"/>
    </xsl:for-each>
*/
</xsl:if>
</xsl:template>

<xsl:template name="groupingstart">
<xsl:if test="string(@repeat)!=''">
  <xsl:choose>
    <xsl:when test="string(@repeat)='0-1'">[</xsl:when>
    <xsl:otherwise>(</xsl:otherwise> 
    </xsl:choose>
  </xsl:if>
</xsl:template>

<xsl:template name="groupingend">
<xsl:if test="string(@repeat)!=''">
  <xsl:choose>
    <xsl:when test="string(@repeat)='0-1'">]</xsl:when>
    <xsl:otherwise>)</xsl:otherwise> 
    </xsl:choose>
  </xsl:if>
</xsl:template>

<xsl:template name="addrepeat">
<xsl:if test="string(@repeat)!=''">
  <xsl:choose>
    <xsl:when test="string(@repeat)='0-'"> * </xsl:when>
    <xsl:when test="string(@repeat)='1-'"> + </xsl:when>
    </xsl:choose>
  </xsl:if>
</xsl:template>

<xsl:template match="srgs:grammar">#JSGF V1.0<xsl:if test="string(@xml:lang)!=''"><xsl:value-of select="concat(' ', @xml:lang)"/></xsl:if>; 
<xsl:if test="string(@xml:base)!=''">base &lt;<xsl:value-of select="@xml:base"/>&gt;;
</xsl:if>
<xsl:if test="string(@root)!=''">grammar <xsl:value-of select="@root"/>;
</xsl:if>
<xsl:if test="string(@tag-format)!=''">tag-format &lt;<xsl:value-of select="@tag-format"/>&gt;;
</xsl:if>
<xsl:apply-templates select="srgs:lexicon"/>
<xsl:apply-templates select="srgs:meta"/>
<xsl:apply-templates select="srgs:rule"/>
</xsl:template>

<!-- Tags are different in JSGF. Needs more thinking. -->
<!-- <xsl:template match="srgs:tag"> {<xsl:value-of select="."/>} </xsl:template>-->
<xsl:template match="srgs:tag"> </xsl:template>

<xsl:template match="srgs:lexicon">
<xsl:choose>
<xsl:when test="string(@type)!=''">lexicon &lt;<xsl:value-of select="@uri"/>&gt;~&lt;<xsl:value-of select="@type"/>&gt;;
</xsl:when>
<xsl:otherwise>lexicon &lt;<xsl:value-of select="@uri"/>&gt;;
</xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="srgs:meta">
<xsl:if test="string(@content)!=''">
<xsl:choose>
<xsl:when test="string(@http-equiv)!=''">
  http-equiv '<xsl:value-of select="@http-equiv"/>' is '<xsl:value-of select="@content"/>';
</xsl:when>
<xsl:when test="string(@name)!=''">
//  meta '<xsl:value-of select="@name"/>' is '<xsl:value-of select="@content"/>';
</xsl:when>
</xsl:choose>
</xsl:if>
</xsl:template>

<xsl:template match="srgs:rule">
<xsl:call-template name="addexamples"/>
<xsl:value-of select="@scope"/> &lt;<xsl:value-of select="@id"/>&gt; = 
<xsl:apply-templates/>;

</xsl:template>

<xsl:template match="srgs:example"/>

<xsl:template match="srgs:token"> 
  ("<xsl:value-of select="text()"/>")<xsl:call-template name="addlang"/>
</xsl:template>

<xsl:template match="srgs:ruleref">
<xsl:choose>
<xsl:when test="string(@special)!=''"> &lt;<xsl:value-of select="@special"/>&gt; </xsl:when>
<xsl:otherwise>
<xsl:choose>
<xsl:when test="starts-with(string(@uri),'#')"> 
  &lt;<xsl:value-of select="substring-after(@uri,'#')"/>&gt; 
</xsl:when>
<xsl:otherwise>
<xsl:choose>
<xsl:when test="string(@type)!=''"> 
  $&lt;<xsl:value-of select="@uri"/>&gt;~&lt;<xsl:value-of select="@type"/>&gt; 
</xsl:when>
<xsl:otherwise> $&lt;<xsl:value-of select="@uri"/>&gt; </xsl:otherwise>
</xsl:choose>
</xsl:otherwise>
</xsl:choose>
</xsl:otherwise>
</xsl:choose> <xsl:call-template name="addlang"/>
</xsl:template>

<xsl:template match="srgs:one-of">  
(<xsl:apply-templates/>) <xsl:call-template name="addlang"/>
</xsl:template>

<xsl:template match="srgs:one-of/srgs:item">
<xsl:call-template name="addweight"/> <xsl:call-template name="groupingstart"/><xsl:apply-templates/><xsl:call-template name="groupingend"/> 
<xsl:call-template name="addlang"/> 
<xsl:call-template name="addrepeat"/>
<xsl:if test="not(position()=last())">      |
</xsl:if>
</xsl:template>

<xsl:template match="srgs:item"> 
  <xsl:call-template name="groupingstart"/><xsl:apply-templates/><xsl:call-template name="groupingend"/> <xsl:call-template name="addlang"/>
  <xsl:call-template name="addrepeat"/>
</xsl:template>

</xsl:stylesheet>
