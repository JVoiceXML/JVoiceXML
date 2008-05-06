<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:beans="http://www.springframework.org/schema/beans"
 exclude-result-prefixes="beans"
 version="1.0">

 <!-- Adapt resource factories -->
 <xsl:template match="beans:property[@name='synthesizedoutput']">
  <xsl:copy>
   <xsl:apply-templates select="@*" />
   <list>
     <xsl:copy-of select="beans:list/beans:bean"/>
     <bean
      class="org.jvoicexml.implementation.text.TextSynthesizedOutputFactory">
      <property name="instances" value="1" />
     </bean>
   </list>
  </xsl:copy>
 </xsl:template>

 <xsl:template match="beans:property[@name='fileoutput']">
  <xsl:copy>
   <xsl:apply-templates select="@*" />
   <list>
    <xsl:copy-of select="beans:list/beans:bean"/>
    <bean
     class="org.jvoicexml.implementation.text.TextAudioFileOutputFactory">
     <property name="instances" value="1" />
    </bean>
   </list>
  </xsl:copy>
 </xsl:template>

 <xsl:template match="beans:property[@name='spokeninput']">
  <xsl:copy>
   <xsl:apply-templates select="@*" />
   <list>
    <xsl:copy-of select="beans:list/beans:bean"/>
    <bean
     class="org.jvoicexml.implementation.text.TextSpokenInputFactory">
     <property name="instances" value="1" />
    </bean>
   </list>
  </xsl:copy>
 </xsl:template>

 <xsl:template match="beans:property[@name='telephony']">
  <xsl:copy>
   <xsl:apply-templates select="@*" />
   <list>
     <xsl:for-each select="beans:list/beans:bean">
      <xsl:copy>
       <xsl:apply-templates select="@*|*|text()|comment()" />
      </xsl:copy>
     </xsl:for-each>
    <bean
     class="org.jvoicexml.implementation.text.TextTelephonyFactory">
     <property name="instances" value="1" />
    </bean>
   </list>
  </xsl:copy>
 </xsl:template>

 <!-- This template passes anything unmatched -->
 <xsl:template match="@*|*|text()|comment()">
  <xsl:copy>
   <xsl:apply-templates select="@*|*|text()|comment()" />
  </xsl:copy>
 </xsl:template>

</xsl:stylesheet>
