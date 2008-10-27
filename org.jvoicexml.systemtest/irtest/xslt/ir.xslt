<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:template match="/">
		<html>
			<head>
				<title>JVoiceXML interpreter implementation test report
				</title>
			</head>
			<body>
				<center>
					<h2>JVoiceXML interpreter implementation test report</h2>
					<table border="0">
						<tr>
							<td> Test Start Time :</td>
							<td>
								<xsl:value-of select="system-report/summary/testStartTime" />
							</td>
							<td> Test End Time :</td>
							<td>
								<xsl:value-of select="system-report/summary/testEndTime" />
							</td>
						</tr>
					</table>
					<p />
					<table border="1" width="60%">
						<tr>
							<xsl:for-each select="system-report/summary/type">
								<th>
									<xsl:value-of select="." />
								</th>
							</xsl:for-each>
						</tr>
						<tr>
							<xsl:for-each select="system-report/summary/count">
								<th>
									<xsl:value-of select="." />
								</th>
							</xsl:for-each>
						</tr>
					</table>
				</center>
				<xsl:value-of select="system-report/testimonial" />
				<table border="1" width="95%">
					<tr>
						<th align="left">Assert ID</th>
						<th align="left">Test Result</th>
						<th align="left">Notes</th>
						<th align="left">LogTag</th>
						<th align="left">Remote Log</th>
						<th align="left">Local Log</th>
						<th align="left">Test Time Cost</th>
					</tr>
					<xsl:apply-templates />
				</table>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="testimonial" />
	<xsl:template match="summary" />
	<xsl:template match="assert">
		<tr>
			<td>
				<xsl:value-of select="@id" />
			</td>
			<td>
				<font>
					<xsl:choose>
						<xsl:when test="@res = 'pass'">
							<xsl:attribute name="color">green</xsl:attribute>
						</xsl:when>
						<xsl:when test="@res = 'fail'">
							<xsl:attribute name="color">red</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="color">black</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:value-of select="@res" />
				</font>
			</td>
			<xsl:apply-templates />
			<td>
				<xsl:value-of select="@costInMS" />
				(ms)
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="notes">
		<td>
			<xsl:value-of select="." />
		</td>
	</xsl:template>
	<xsl:template match="logURIs">
		<td>
			<xsl:choose>
				<xsl:when test=". = '-'">
					<xsl:value-of select="." />
				</xsl:when>
				<xsl:when test="starts-with(., 'file:')">
					<a>
						<xsl:attribute name="href">
                            <xsl:value-of select="substring(., 6)" />
                        </xsl:attribute>
						<xsl:value-of select="substring(., 6)" />
					</a>
				</xsl:when>
				<xsl:when test="starts-with(., 'string:')">
					<xsl:value-of select="substring(., 8)" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="." />
				</xsl:otherwise>
			</xsl:choose>
		</td>
	</xsl:template>
</xsl:stylesheet>