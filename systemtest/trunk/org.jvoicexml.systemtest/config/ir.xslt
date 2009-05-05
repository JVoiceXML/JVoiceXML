<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">

  <xsl:template name="bgColor">
    <xsl:param name="string" />
    <xsl:param name="redString" />
    <xsl:param name="greenString" />
    <xsl:choose>
      <xsl:when test="$string = $greenString">#84B951</xsl:when>
      <xsl:when test="$string = $redString">#ED6D10</xsl:when>
      <xsl:otherwise>white</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="ref">
    <xsl:param name="uri" />
    <xsl:param name="name" />
    <xsl:choose>
      <xsl:when test="starts-with($uri, 'file:')">
        <a>
          <xsl:attribute name="href">
            <xsl:value-of select="substring($uri, 6)" />
          </xsl:attribute>
          <xsl:value-of select="$name" />
        </a>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$uri" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="replaceAll">
    <xsl:param name="input" />
    <xsl:param name="from" />
    <xsl:param name="to" />
    <xsl:if test="$input">
      <xsl:choose>
        <xsl:when test="contains($input,$from)">
          <xsl:value-of select="substring-before($input,$from)" />
          <xsl:value-of select="$to" />
          <xsl:call-template name="replaceAll">
            <xsl:with-param name="input"
              select="substring-after($input,$from)" />
            <xsl:with-param name="from" select="$from" />
            <xsl:with-param name="to" select="$to" />
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$input" />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
  </xsl:template>

  <xsl:key name="key1" match="assert" use="concat(res,notes,hasErrorLevelLog)" />
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
              <td><h3> Test Time :</h3></td>
              <td>
                <h3>
                  <xsl:value-of select="system-report/testStartTime" />
                </h3>
              </td>
              <td><h3> - </h3></td>
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
              <th>
                <font color="#ED6D10">Total Of Fail</font>
              </th>
              <th>
                <font color="#84B951">Total Of Pass</font>
              </th>
              <th>
                <font color="#888637">Total Of Skip</font>
              </th>
              <th>
                <font color="black">Total Of ALL</font>
              </th>
              <th>
                <font color="black">Total Of Cost</font>
              </th>
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
                <xsl:value-of
                  select="number(system-report/totalOfCost) div 1000" />
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
              <th>
                <font color="black">Has Error Level Log</font>
              </th>
              <th align="center" width="15%">Total</th>
            </tr>
            <xsl:for-each
              select="//assert[generate-id(.)=generate-id(key('key1',concat(res,notes,hasErrorLevelLog)))]">
              <xsl:sort select="concat(res,notes)" order="ascending" />
              <tr>
                <td align="center">
                  <xsl:value-of select="position()" />
                </td>
                <td align="center">
                  <xsl:attribute name="bgcolor">
                    <xsl:call-template name="bgColor">
                      <xsl:with-param name="string" select="res" />
                      <xsl:with-param name="redString" select="'fail'" />
                      <xsl:with-param name="greenString" select="'pass'" />
                    </xsl:call-template>
                  </xsl:attribute>
                  <xsl:value-of select="res" />
                </td>
                <td align="left">
                  <xsl:value-of select="notes" />
                </td>
                <td align="center">
                  <xsl:attribute name="bgcolor">
                    <xsl:call-template name="bgColor">
                      <xsl:with-param name="string" select="./hasErrorLevelLog" />
                      <xsl:with-param name="redString" select="'true'" />
                      <xsl:with-param name="greenString" select="'false'" />
                    </xsl:call-template>
                  </xsl:attribute>
                  <xsl:if test="string-length(hasErrorLevelLog) = 0">
                    <xsl:text>-</xsl:text>
                  </xsl:if>
                  <xsl:value-of select="hasErrorLevelLog" />
                </td>
                <td align="center">
                  <xsl:value-of
                    select="count(key('key1',concat(res,notes,hasErrorLevelLog)))" />
                </td>
              </tr>
            </xsl:for-each>
          </table>
          <p />
        </center>
        <p />
        
        <center>
        Records:
        <table border="1" width="95%">
          <tr>
            <th align="left" width="5%">Assert ID</th>
            <th align="left" width="35%">Description</th>
            <th align="left" width="15%">LogTag</th>
            <th align="left" width="10%">Resource Borrow and Return</th>
            <th align="left" width="5%"><center>Report ERROR <br/>/<br /> Remote Log</center></th>
            <th align="left" width="10%"><center>Test Result <br/>/<br /> Local Log</center></th>
            <th align="left" width="15%">Notes</th>
            <th align="left" width="5%">Test Time Cost</th>
          </tr>
          <xsl:apply-templates />
        </table>
        </center>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="testimonial" />

  <xsl:template
    match="totalOfTest | testStartTime | testEndTime | totalOfCost | totalOfCost" />

  <xsl:template match="assert">
    <tr>
      <td>
        <xsl:value-of select="@id" />
      </td>
      <td>
        <xsl:value-of select="concat('[', spec, '] ', desc)" />
      </td>
      <td>
        <xsl:apply-templates select="logTag" />
      </td>
      <td>
        <xsl:apply-templates select="resourceLog" />
      </td>
      <td align="center">
        <xsl:attribute name="bgcolor">
          <xsl:call-template name="bgColor">
            <xsl:with-param name="string" select="./hasErrorLevelLog" />
            <xsl:with-param name="redString" select="'true'" />
            <xsl:with-param name="greenString" select="'false'" />
          </xsl:call-template>
        </xsl:attribute>
        <xsl:apply-templates select="hasErrorLevelLog" />
      </td>
      <td align="center">
        <xsl:attribute name="bgcolor">
          <xsl:call-template name="bgColor">
            <xsl:with-param name="string" select="res" />
            <xsl:with-param name="redString" select="'fail'" />
            <xsl:with-param name="greenString" select="'pass'" />
          </xsl:call-template>
        </xsl:attribute>
        <xsl:apply-templates select="res" />
      </td>
      <td>
        <xsl:apply-templates select="notes" />
      </td>
      <td>
        <xsl:value-of select="@costInMS" />
        (ms)
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="notes">
    <xsl:value-of select="." />
  </xsl:template>

  <xsl:template match="logTag">
    <xsl:if test="string-length(.) = 0" >
      <xsl:text>-</xsl:text>
    </xsl:if>
    <xsl:value-of select="." />
  </xsl:template>

  <xsl:template match="hasErrorLevelLog">
    <xsl:if test="string-length(.) = 0" >
      <xsl:text>-</xsl:text>
    </xsl:if>
    <xsl:call-template name="ref">
      <xsl:with-param name="uri" select="../remoteLogURI" />
      <xsl:with-param name="name" select="." />
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="res">
    <xsl:if test=". = 'skip'">
      <xsl:value-of select="." />
    </xsl:if>
    <xsl:call-template name="ref">
      <xsl:with-param name="uri" select="../localLogURI" />
      <xsl:with-param name="name" select="." />
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="resourceLog">
    <xsl:if test="string-length(.) = 0">
      <xsl:text>-</xsl:text>
    </xsl:if>
    <pre>
      <font size="1">
      <xsl:call-template name="replaceAll">
        <xsl:with-param name="input">
          <xsl:call-template name="replaceAll">
            <xsl:with-param name="input" select="."/>
            <xsl:with-param name="from">pool has now </xsl:with-param>
            <xsl:with-param name="to"/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="from" >for key 'text' (org.jvoicexml.implementation.text.</xsl:with-param>
        <xsl:with-param name="to">(</xsl:with-param>
      </xsl:call-template>
      </font>
    </pre>
  </xsl:template>
</xsl:stylesheet>