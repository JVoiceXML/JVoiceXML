<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">
  <xsl:param name="kinectrecognizerpath"/>
  <!-- Adapt the jvmarg values -->
  <xsl:template match="java[@classname='org.jvoicexml.startup.Startup']">
    <xsl:copy>
      <!-- Keep current path -->
      <xsl:apply-templates select="@*" />
      <xsl:comment>Set the library path for the Kinect Recognizer</xsl:comment>
      <jvmarg value="-Djava.library.path={$kinectrecognizerpath}" />
      <xsl:apply-templates select="@*|*|text()|comment()" />
    </xsl:copy>
  </xsl:template>

  <!-- This template passes anything unmatched -->
  <xsl:template match="@*|*|text()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|comment()" />
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
