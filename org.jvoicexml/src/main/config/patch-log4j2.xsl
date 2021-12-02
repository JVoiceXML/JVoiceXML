<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="2.0">
    <xsl:preserve-space elements="*" />
    <xsl:output method="xml" indent="yes"/>
    <xsl:template match="Root">
        <xsl:copy>
            <!-- Keep current settings -->
            <xsl:apply-templates />
            <xsl:comment>Use the system test log4j configuration</xsl:comment><xsl:text>&#xa;</xsl:text>
            <AppenderRef ref="remote" level="info"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="Appenders">
        <xsl:copy>
            <!-- Keep current settings -->
            <xsl:apply-templates />
            <xsl:comment>Use the system test log4j configuration</xsl:comment><xsl:text>&#xa;</xsl:text>
            <Socket name="remote" host="localhost" port="14712" reconnectionDelay="1000">
                <JsonLayout properties="true"/>
            </Socket>
        </xsl:copy>
    </xsl:template>

    <!-- This template passes anything unmatched -->
    <xsl:template match="@*|*|text()|comment()">
        <xsl:copy>
            <xsl:apply-templates select="@*|*|text()|comment()" />
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
