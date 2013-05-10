<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output encoding="UTF-8" indent="yes" />
  
  <xsl:template match="/">  	
	<xsl:apply-templates select="node()" />
  </xsl:template>
   
  <xsl:template match="node()|@*" priority="3" >
	<xsl:copy>
  		<xsl:apply-templates select="@*" />
  		<xsl:if test="not(name()='application')" >
  			<xsl:apply-templates select="node()" />
  		</xsl:if>
  		<xsl:if test="name()='application'">
  			<xsl:call-template name="own_template" />
  		</xsl:if>  		
  	</xsl:copy>
  </xsl:template>

  <xsl:template match="application" priority="50" name="own_template" exclude-result-prefixes="#all" >
  	<xsl:apply-templates select="node()" />
  	<xsl:text>
  		</xsl:text>
	<xsl:element name="view-handler" namespace="http://java.sun.com/xml/ns/javaee">
		<xsl:text>org.jboss.portletbridge.application.PortletViewHandler</xsl:text>
	</xsl:element>
	
	<xsl:text>
		</xsl:text>
	<xsl:element name="state-manager" namespace="http://java.sun.com/xml/ns/javaee">
		<xsl:text>org.jboss.portletbridge.application.PortletStateManager</xsl:text>
	</xsl:element>
	
	<xsl:text>
  	</xsl:text>
  </xsl:template>
  
</xsl:stylesheet>

