<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="2.0"
    xmlns:beans="http://www.springframework.org/schema/beans">
    <xsl:param name="buildpath" />
    <xsl:param name="version" />

    <xsl:template match="classpath">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <xsl:value-of select="$buildpath" />/<xsl:value-of select="replace(text(), '@@VERSION@@', $version)" />
            <!-- Keep current settings -->
            <xsl:apply-templates select="@*|*|comment()" />
        </xsl:copy>
    </xsl:template>

    <!-- This template passes anything unmatched -->
    <xsl:template match="@*|*|text()|comment()">
        <xsl:copy>
            <xsl:apply-templates select="@*|*|text()|comment()" />
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
