<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
	xmlns:pbr="xalan://org.jboss.portletbridge.tools.simpleConverter.Main"
	exclude-result-prefixes="pbr">
<xsl:output encoding="UTF-8" indent="yes" omit-xml-declaration="yes" />
  
  <xsl:template match="/">  	
	<xsl:apply-templates select="node()" />
  </xsl:template>
   
  <xsl:template match="node()|@*" priority="3" >
	<xsl:copy>
  		<xsl:apply-templates select="@*" />
  		
  		<xsl:choose>
  			<xsl:when test="name()='portlet-name'">
  				<xsl:call-template name="own_template_portlet_name" />
  			</xsl:when>
  			<xsl:when test="name()='display-name'">
  				<xsl:call-template name="own_template_display_name" />
  			</xsl:when>
  			<xsl:when test="name()='init-param'">
  				<xsl:call-template name="own_template_init_param" />
  			</xsl:when>
  			<xsl:when test="name()='portlet-info'">
  				<xsl:call-template name="own_template_portlet_info" />
  			</xsl:when>
  			<xsl:otherwise>
  				<xsl:apply-templates select="node()" />
  			</xsl:otherwise>
  		</xsl:choose>
  		  		
  	</xsl:copy>
  </xsl:template>

  <xsl:template match="portlet-name" name="own_template_portlet_name" >
  	<xsl:variable name="projectName" select="pbr:getProjectName()" />
  	<xsl:value-of select="$projectName" />Portlet
  </xsl:template>
  
  <xsl:template match="display-name" name="own_template_display_name" >
  	<xsl:variable name="projectName" select="pbr:getProjectName()" />
  	<xsl:value-of select="$projectName" /> Portlet
  </xsl:template>
  
  <xsl:template match="portlet-info" name="own_template_portlet_info" >
  	<xsl:variable name="projectName" select="pbr:getProjectName()" />
  	<xsl:text>
  	</xsl:text>
  	<title><xsl:value-of select="$projectName" /> Portlet</title>  			
  </xsl:template>
  
  <xsl:template match="init-param" name="own_template_init_param" >
  	<xsl:variable name="initParamKey">
  		<xsl:value-of select="descendant::node()" />
  	</xsl:variable>
  	
  	<xsl:choose>
  		<xsl:when test="$initParamKey='javax.portlet.faces.defaultViewId.view'">
  			<xsl:text>
  		</xsl:text>
  			<name>javax.portlet.faces.defaultViewId.view</name>
  			<xsl:text>
  		</xsl:text>
  			<value><xsl:value-of select="pbr:getDefaultViewStatePage()" /></value>
  			<xsl:text>
  		</xsl:text>
  		</xsl:when>  		
  		<xsl:otherwise>
  			<xsl:copy-of select="node()" />
  		</xsl:otherwise>
  	</xsl:choose>
  	  	
  </xsl:template>
  
  <xsl:template match="portlet-app/*" priority="200" >
  	<xsl:copy>
  		<xsl:value-of select="node()" />
  	</xsl:copy>
  </xsl:template>
    
</xsl:stylesheet>

