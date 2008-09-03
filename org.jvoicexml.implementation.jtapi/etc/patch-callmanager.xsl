<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:beans="http://www.springframework.org/schema/beans"
  exclude-result-prefixes="beans"
  version="1.0">

  <xsl:param name="providername"/>
  <xsl:param name="terminal"/>
  <xsl:param name="port"/>
  <xsl:param name="inputType"/>
  <xsl:param name="outputType"/>

  <xsl:template match="beans:beans">
  <xsl:copy>
   <xsl:apply-templates select="@*" />
    <bean id="callmanager"
          class="org.jvoicexml.callmanager.jtapi.JtapiCallManager">
    <property name="providername">
      <xsl:attribute name="value">
        <xsl:value-of select="$providername"/>
      </xsl:attribute>
    </property>
     <property name="applications">
       <list>
         <bean
           class="org.jvoicexml.callmanager.jtapi.JtapiConfiguredApplication">
           <property name="terminal">
             <xsl:attribute name="value">
               <xsl:value-of select="$terminal"/>
             </xsl:attribute>
           </property>
           <property name="uri"
                 value="http://127.0.0.1:8080/helloworldservletdemo/JVoiceXML" />
           <property name="port">
             <xsl:attribute name="value">
               <xsl:value-of select="$port"/>
             </xsl:attribute>
           </property>
           <property name="inputType">
             <xsl:attribute name="value">
               <xsl:value-of select="$inputType"/>
             </xsl:attribute>
           </property>
           <property name="outputType">
             <xsl:attribute name="value">
               <xsl:value-of select="$outputType"/>
             </xsl:attribute>
           </property>
         </bean>
       </list>
      </property>
    </bean>
    </xsl:copy>
  </xsl:template>

  <!-- This template passes anything unmatched -->
  <xsl:template match="@*|*|text()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|comment()"/>
    </xsl:copy>
  </xsl:template>  

</xsl:stylesheet>
