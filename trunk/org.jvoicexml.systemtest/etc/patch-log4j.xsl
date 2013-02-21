<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:log4j="http://jakarta.apache.org/log4j">
	<xsl:template match="/log4j:configuration">
		<xsl:copy>
			<appender name="SOCKET_HUB" class="org.apache.log4j.net.SocketHubAppender">
				<param name="Port" value="5920" />
				<param name="LocationInfo" value="true" />
				<filter class="org.apache.log4j.varia.LevelRangeFilter">
					<param name="LevelMin" value="debug" />
				</filter>
			</appender>

			<appender name="remote"
				class="org.apache.log4j.varia.ExternallyRolledFileAppender">
				<param name="port" value="5920" />
				<param name="maxBackupIndex" value="1000" />
				<param name="file" value="logging/interpreter.log.txt" />
				<param name="append" value="false" />
				<layout class="org.apache.log4j.PatternLayout">
					<param name="ConversionPattern" value="%6r [%-20.20t] %-5p %30.30c (%6L) - %m%n" />
				</layout>
				<filter class="org.apache.log4j.varia.LevelRangeFilter">
					<param name="LevelMin" value="debug" />
				</filter>
			</appender>

			<appender name="error.level"
				class="org.apache.log4j.varia.ExternallyRolledFileAppender">
				<param name="port" value="5930" />
				<param name="maxBackupIndex" value="1000" />
				<param name="file" value="logging/errorlevel.log.txt" />
				<param name="append" value="false" />
				<layout class="org.apache.log4j.PatternLayout">
					<param name="ConversionPattern" value="%m%n" />
				</layout>
				<filter class="org.apache.log4j.varia.LevelRangeFilter">
					<param name="LevelMin" value="error" />
				</filter>
			</appender>

			<appender name="log.tag.strategy"
				class="org.apache.log4j.varia.ExternallyRolledFileAppender">
				<param name="port" value="5940" />
				<param name="maxBackupIndex" value="1000" />
				<param name="file" value="logging/logtag.log.txt" />
				<param name="append" value="false" />
				<layout class="org.apache.log4j.PatternLayout">
					<param name="ConversionPattern" value="%m%n" />
				</layout>
				<filter class="org.apache.log4j.varia.LevelRangeFilter">
					<param name="LevelMin" value="info" />
				</filter>
			</appender>

			<appender name="keyed.resource.pool"
				class="org.apache.log4j.varia.ExternallyRolledFileAppender">
				<param name="port" value="5960" />
				<param name="maxBackupIndex" value="1000" />
				<param name="file" value="logging/resource.log.txt" />
				<param name="append" value="false" />
				<layout class="org.apache.log4j.PatternLayout">
					<param name="ConversionPattern" value="%m%n" />
				</layout>
				<filter class="org.apache.log4j.varia.LevelRangeFilter">
					<param name="LevelMin" value="debug" />
				</filter>
			</appender>

		    <logger name="org.jvoicexml.interpreter.tagstrategy.LogStrategy">
		        <level value="info"/>
		        <appender-ref ref="log.tag.strategy" />
		    </logger>
		    
		    <logger name="org.jvoicexml.implementation.KeyedResourcePool">
		        <level value="debug"/>
		        <appender-ref ref="keyed.resource.pool" />
		    </logger>
			<xsl:apply-templates select="@*|*|text()|comment()" />
		</xsl:copy>
	</xsl:template>

    <!-- xsl:template match="root">
        <xsl:copy>
            <xsl:apply-templates select="@*|*|text()|comment()" />
            <appender-ref ref="remote" />
            <appender-ref ref="error.level" />
        </xsl:copy>
    </xsl:template-->
    
	<!-- This template passes anything unmatched -->
	<xsl:template match="@*|*|text()|comment()">
		<xsl:copy>
			<xsl:apply-templates select="@*|*|text()|comment()" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
