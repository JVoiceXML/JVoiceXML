<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:beans="http://www.springframework.org/schema/beans"
  version="1.0">

  <xsl:param name="terminal"/>
  <xsl:param name="port"/>

  <xsl:template match="beans:property[@name='terminal']">
    <property name="terminal">
        <xsl:attribute name="value">
            <xsl:value-of select="$terminal"/>
        </xsl:attribute>
    </property>
  </xsl:template>

  <xsl:template match="beans:property[@name='port']">
    <property name="port">
        <xsl:attribute name="value">
            <xsl:value-of select="$port"/>
        </xsl:attribute>
    </property>
  </xsl:template>

  <!-- This template passes anything unmatched -->
  <xsl:template match="@*|*|text()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|comment()"/>
    </xsl:copy>
  </xsl:template>  

</xsl:stylesheet>
