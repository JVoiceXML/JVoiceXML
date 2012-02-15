<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0" >
  <xsl:template match="/web-app">
<web-app xmlns="http://java.sun.com/xml/ns/javaee"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
  version="3.0">
  <xsl:apply-templates select="@*|*|text()|comment()" />
    <servlet-mapping>
        <servlet-name>jsp</servlet-name>
        <url-pattern>*.jsp</url-pattern>
        <url-pattern>*.ircgi</url-pattern>
    </servlet-mapping>
</web-app>
  </xsl:template>

  <xsl:template match="servlet">
  </xsl:template>

  <xsl:template match="servlet-mapping">
  </xsl:template>
  
  <!-- This template passes anything unmatched -->
  <xsl:template match="@*|*|text()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|comment()" />
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
