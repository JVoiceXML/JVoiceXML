<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 version="1.0">

 <xsl:param name="distpath" />

 <!-- Adapt the classpath -->
 <xsl:template match="path[@id='run.classpath']">
  <xsl:copy>
   <!-- Keep current path -->
   <xsl:apply-templates select="@*|*|text()|comment()" />
   <!-- Append component path -->
   <fileset id="jvxml.text.lib">
    <xsl:attribute name="dir">
            <xsl:value-of select="$distpath" />
        </xsl:attribute>
    <include name="jvxml-text.jar" />
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
