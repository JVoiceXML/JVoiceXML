<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="2.0" xmlns:vxml="http://www.w3.org/2001/vxml" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <xsl:param name="datapath" />

    <xsl:template match="vxml:object">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <xsl:attribute name="data">
                <xsl:value-of select="$datapath" />
            </xsl:attribute>
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
