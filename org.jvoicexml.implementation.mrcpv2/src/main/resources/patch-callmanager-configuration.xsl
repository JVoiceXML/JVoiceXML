<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="2.0"
    xmlns:beans="http://www.springframework.org/schema/beans">
    <xsl:param name="buildpath" />
    <xsl:param name="version" />
    <xsl:param name="address" />
    <xsl:param name="port" />
    <xsl:param name="cairoAddress" />
    <xsl:param name="cairoHost" />
    <xsl:param name="cairoPort" />
    <xsl:param name="baseReceiverRTPPort" />
    <xsl:param name="baseTransmitterRTPPort" />

    <xsl:template match="classpath">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <xsl:value-of select="$buildpath" />/<xsl:value-of select="replace(text(), '@@VERSION@@', $version)" />
            <!-- Keep current settings -->
            <xsl:apply-templates select="@*|*|comment()" />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="beans:property[@name='mySipAddress']">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <beans:value><xsl:value-of select="$address" /></beans:value>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="beans:property[@name='port']">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <beans:value><xsl:value-of select="$port" /></beans:value>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="beans:property[@name='cairoSipAddress']">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <beans:value><xsl:value-of select="$cairoAddress" /></beans:value>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="beans:property[@name='cairoSipHostName']">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <beans:value><xsl:value-of select="$cairoHost" /></beans:value>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="beans:property[@name='cairoSipPort']">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <beans:value><xsl:value-of select="$cairoPort" /></beans:value>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="beans:property[@name='baseReceiverRtpPort']">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <beans:value><xsl:value-of select="$baseReceiverRTPPort" /></beans:value>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="beans:property[@name='baseXmitRtpPort']">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <beans:value><xsl:value-of select="$baseTransmitterRTPPort" /></beans:value>
        </xsl:copy>
    </xsl:template>
    
    <!-- This template passes anything unmatched -->
    <xsl:template match="@*|*|text()|comment()">
        <xsl:copy>
            <xsl:apply-templates select="@*|*|text()|comment()" />
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
