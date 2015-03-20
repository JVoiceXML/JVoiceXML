<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:template name="bgColor">
        <xsl:param name="string" />
        <xsl:choose>
            <xsl:when test="$string = 'PASS'">
                #84B951
            </xsl:when>
            <xsl:when test="$string = 'FAIL'">
                #ED6D10
            </xsl:when>
            <xsl:otherwise>
                white
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="/">
        <html>
            <head>
                <title>JVoiceXML interpreter implementation test report
                </title>
            </head>
            <body>
                <h1>JVoiceXML interpreter implementation test report</h1>
                <p>
                    Test Time:
                    <xsl:value-of select="//system-report/testStartTime" />
                    -
                    <xsl:value-of select="//system-report/testEndTime" />
                </p>
                <h2>Summary</h2>
                <table border="1" width="100%">
                    <tr>
                        <th>
                            <font color="#ED6D10">#failed tests</font>
                        </th>
                        <th>
                            <font color="#84B951">#passed tests</font>
                        </th>
                        <th>
                            <font color="#888637">#skipped tests</font>
                        </th>
                        <th>
                            <font color="black">#tests run</font>
                        </th>
                        <th>
                            <font color="black">duration</font>
                        </th>
                    </tr>
                    <tr>
                        <td align="center">
                            <xsl:value-of select="count(//system-report/assert/res[text()='FAIL'])" />
                        </td>
                        <td align="center">
                            <xsl:value-of select="count(//system-report/assert/res[text()='PASS'])" />
                        </td>
                        <td align="center">
                            <xsl:value-of select="count(//system-report/assert/res[text()='SKIP'])" />
                        </td>
                        <td align="center">
                            <xsl:value-of select="//system-report/totalOfTest" />
                        </td>
                        <td align="center">
                            <xsl:value-of select="number(//system-report/totalOfCost) div 1000" />
                            (s)
                        </td>
                    </tr>
                </table>

                <h2>Overview</h2>
                <table width="100%" border="1">
                    <tr>
                        <th align="center" width="5%">Id</th>
                        <th align="center" width="10%">Result</th>
                        <th align="center" width="85%">Notes</th>
                    </tr>
                    <xsl:for-each select="//system-report/assert">
                        <tr>
                            <td>
                                <a>
                                    <xsl:attribute name="href"><xsl:value-of select="concat('#detail',@id)" /></xsl:attribute>
                                    <xsl:value-of select="@id" />
                                </a>
                            </td>
                            <td align="center">
                                <xsl:attribute name="bgcolor">
                                    <xsl:call-template name="bgColor">
                                        <xsl:with-param name="string" select="res" />
                                    </xsl:call-template>
                                </xsl:attribute>
                                <xsl:value-of select="res" />
                            </td>
                            <td>
                                <xsl:value-of select="notes" />
                            </td>
                        </tr>
                    </xsl:for-each>
                </table>

                <h2>Records</h2>
                <table border="1" width="100%">
                    <tr>
                        <th align="left" width="5%">Id</th>
                        <th align="left" width="30%">Description</th>
                        <th align="left" width="5%">Result</th>
                        <th align="left" width="35%">Notes</th>
                        <th align="left" width="10%">Resources</th>
                        <th align="left" width="10%">Logs</th>
                        <th align="left" width="5%">Duration</th>
                    </tr>
                    <xsl:apply-templates />
                </table>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="testimonial" />

    <xsl:template match="totalOfTest | testStartTime | testEndTime | totalOfCost | totalOfCost" />

    <xsl:template match="assert">
        <tr>
            <td>
                <a>
                    <xsl:attribute name="name"><xsl:value-of select="concat('detail',@id)" /></xsl:attribute>
                </a>
                <xsl:value-of select="@id" />
            </td>
            <td>
                <xsl:value-of select="desc" />
            </td>
            <td align="center">
                <xsl:attribute name="bgcolor">
                    <xsl:call-template name="bgColor">
                        <xsl:with-param name="string" select="res" />
                    </xsl:call-template>
                </xsl:attribute>
                <xsl:value-of select="res" />
            </td>
            <td>
                <xsl:value-of select="notes" />
            </td>
            <td>
                <pre><xsl:apply-templates select="resourceLog" /></pre>
            </td>
            <td>
                <a>
                    <xsl:attribute name="href"><xsl:value-of select="localLogURI" /></xsl:attribute>
                    local log
                </a><br/>
                <a>
                    <xsl:attribute name="href"><xsl:value-of select="remoteLogURI" /></xsl:attribute>
                    remote log
                </a>
            </td>
            <td>
                <xsl:value-of select="@costInMS" />
                (ms)
            </td>
        </tr>
    </xsl:template>

</xsl:stylesheet>