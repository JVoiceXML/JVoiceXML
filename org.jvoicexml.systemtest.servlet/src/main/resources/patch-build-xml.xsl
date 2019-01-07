<?xml version="1.0"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/TR/xhtml1/strict">
	<xsl:output method="xml" indent="yes" />
	<xsl:param name="irtestdir" />
	<xsl:template match="project/@basedir">
		<xsl:attribute name="basedir">
            <xsl:value-of select="$irtestdir" />
        </xsl:attribute>
	</xsl:template>

    <xsl:template match="property[@name='build']">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <xsl:attribute name="value">
                <xsl:value-of select="'${basedir}'" />
            </xsl:attribute>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="param[@value='${src}']">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <xsl:attribute name="value">
                <xsl:value-of select="'${basedir}/${src}'" />
            </xsl:attribute>
        </xsl:copy>
    </xsl:template>

	<!-- This template passes anything unmatched -->
	<xsl:template match="@*|*|text()|comment()">
		<xsl:copy>
			<xsl:apply-templates
				select="@*|*|text()|comment()" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
