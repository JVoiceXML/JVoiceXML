<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">
  <xsl:template name="fontColor">
    <xsl:param name="resultText" />
    <font>
      <xsl:choose>
        <xsl:when test="$resultText = 'pass'">
          <xsl:attribute name="color">green</xsl:attribute>
        </xsl:when>
        <xsl:when test="$resultText = 'fail'">
          <xsl:attribute name="color">red</xsl:attribute>
        </xsl:when>
        <xsl:when test="$resultText = 'true'">
          <xsl:attribute name="color">red</xsl:attribute>
        </xsl:when>
        <xsl:when test="$resultText = 'false'">
          <xsl:attribute name="color">green</xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="color">black</xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="$resultText" />
    </font>
  </xsl:template>
  <xsl:key name="key1" match="assert" use="concat(res,notes,logURIs[3])" />
  <xsl:key name="key2" match="assert" use="res" />
  <xsl:template match="/">
    <html>
      <head>
        <title>JVoiceXML interpreter implementation test report
        </title>
      </head>
      <body>
        <center>
          <h1>JVoiceXML interpreter implementation test report
          </h1>
          <table border="0">
            <tr>
              <td> Test Time :</td>
              <td>
                <h3>
                  <xsl:value-of select="system-report/testStartTime" />
                </h3>
              </td>
              <td> -</td>
              <td>
                <h3>
                  <xsl:value-of select="system-report/testEndTime" />
                </h3>
              </td>
            </tr>
          </table>
          <p />
          <table border="1" width="80%">
            <tr>
              <th><font color="red">Total Of Fail</font></th>
              <th><font color="green">Total Of Pass</font></th>
              <th><font color="gray">Total Of Skip</font></th>
              <th><font color="black">Total Of ALL</font></th>
              <th><font color="black">Total Of Cost</font></th>
            </tr>
            <tr>
              <xsl:for-each
                select="//assert[generate-id(.)=generate-id(key('key2',res))]">
                <xsl:sort select="res" order="ascending" />
                <td align="center">
                  <xsl:value-of select="count(key('key2',res))" />
                </td>
              </xsl:for-each>
              <td align="center">
                <xsl:value-of select="system-report/totalOfTest" />
              </td>
              <td align="center">
                <xsl:value-of select="number(system-report/totalOfCost) div 1000" />
                (s)
              </td>
            </tr>
          </table>
          <p />
          Reason Statistics:
          <table width="80%" border="1">
            <tr>
              <th align="center" width="5%">Type Id</th>
              <th align="center" width="15%">Result Type</th>
              <th align="center" width="70%">Reason Notes</th>
              <th><font color="black">Has Error Level Log</font></th>
              <th align="center" width="15%">Total</th>
            </tr>
            <xsl:for-each
              select="//assert[generate-id(.)=generate-id(key('key1',concat(res,notes,logURIs[3])))]">
              <xsl:sort select="concat(res,notes)" order="ascending" />
              <tr>
                <td align="center">
                  <xsl:value-of select="position()" />
                </td>
                <td align="center">
                  <xsl:call-template name="fontColor">
                    <xsl:with-param name="resultText" select="res" />
                  </xsl:call-template>
                </td>
                <td align="left">
                  <xsl:value-of select="notes" />
                </td>
                <td align="center">
                  <xsl:call-template name="fontColor">
                  <xsl:with-param name="resultText" select="logURIs[3]" />
                    <xsl:value-of select="logURIs[3]" />
                  </xsl:call-template>
                </td>
                <td align="center">
                  <xsl:value-of select="count(key('key1',concat(res,notes,logURIs[3])))" />
                </td>
              </tr>
            </xsl:for-each>
          </table>
          <p />
        </center>
        Records:
        <table border="1" width="95%">
          <tr>
            <th align="left" width="5%">Assert ID</th>
            <th align="left" width="10%">Test Result</th>
            <th align="left" width="20%">Notes</th>
            <th align="left" width="20%">LogTag</th>
            <th align="left" width="20%">Remote Log</th>
            <th align="left" width="20%">Report ERROR</th>
            <th align="left" width="20%">Local Log</th>
            <th align="left" width="5%">Test Time Cost</th>
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
        <xsl:value-of select="@id" />
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
  <xsl:template match="res">
    <td>
      <xsl:call-template name="fontColor">
        <xsl:with-param name="resultText" select="." />
      </xsl:call-template>
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