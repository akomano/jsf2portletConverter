<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output encoding="UTF-8" indent="yes" omit-xml-declaration="yes" />
  
  <xsl:template match="/">  	
	<xsl:apply-templates select="node()" />
  </xsl:template>
   
  <xsl:template match="node()|@*" priority="3" >
	<xsl:copy>
  		<xsl:apply-templates select="@*" />
  		<xsl:if test="not(name()='dependencies') and not(name()='properties')" >
  			<xsl:apply-templates select="node()" />
  		</xsl:if>
  		<xsl:if test="name()='dependencies'">
  			<xsl:call-template name="dependencies" />
  		</xsl:if>
  		<xsl:if test="name()='properties'">
  			<xsl:call-template name="properties" />
  		</xsl:if>
  	</xsl:copy>
  </xsl:template>
  
  <xsl:template name="properties">
  	<xsl:apply-templates select="child::node()" />
  	<xsl:if test="count(//properties/portletbridge.version)=0">
  		<xsl:variable name="portletbridge.version">2.0.0.FINAL</xsl:variable>
  		<xsl:text>	</xsl:text>
  		<portletbridge.version>
  			<xsl:text>2.0.0.FINAL</xsl:text>
  		</portletbridge.version>
  	</xsl:if>
  </xsl:template>

  
  <xsl:template match="dependencies" name="dependencies" exclude-result-prefixes="#all" >
  	<xsl:apply-templates select="child::node()" />
  	
  	<xsl:text>
  	</xsl:text>  	
  	<xsl:element name="dependency" namespace="http://maven.apache.org/POM/4.0.0" >
  		<xsl:text>
  		</xsl:text><groupId>javax.portlet</groupId>
	<xsl:text>
		</xsl:text><artifactId>portlet-api</artifactId>
	<xsl:text>
		</xsl:text><version>1.0</version>
	<xsl:text>
		</xsl:text><scope>provided</scope>
	<xsl:text>	
	</xsl:text>
  	</xsl:element>
  	
	
	<xsl:text>
	</xsl:text>
	<xsl:element name="dependency" namespace="http://maven.apache.org/POM/4.0.0" >
		<xsl:text>
		</xsl:text><groupId>org.jboss.portletbridge</groupId>
	<xsl:text>
		</xsl:text><artifactId>portletbridge-api</artifactId>
	<xsl:text>
		</xsl:text><version>${portletbridge.version}</version>
	<xsl:text>
		</xsl:text><exclusions>
			<xsl:text>
			</xsl:text><exclusion>
				<xsl:text>
				</xsl:text><groupId>javax.faces</groupId>
				<xsl:text>
				</xsl:text><artifactId>jsf-api</artifactId>
			<xsl:text>
			</xsl:text></exclusion>
			<xsl:text>
			</xsl:text><exclusion>
				<xsl:text>
				</xsl:text><groupId>javax.faces</groupId>
				<xsl:text>
				</xsl:text><artifactId>jsf-impl</artifactId>
			<xsl:text>
			</xsl:text></exclusion>
		<xsl:text>
		</xsl:text></exclusions>			
	<xsl:text>
	</xsl:text>
  	</xsl:element>
		
	<xsl:text>
	</xsl:text>
	<xsl:element name="dependency" namespace="http://maven.apache.org/POM/4.0.0" >
		<xsl:text>
		</xsl:text><groupId>org.jboss.portletbridge</groupId>
		<xsl:text>
		</xsl:text><artifactId>portletbridge-impl</artifactId>
		<xsl:text>
		</xsl:text><version>${portletbridge.version}</version>
		<xsl:text>
		</xsl:text><exclusions>
			<xsl:text>
			</xsl:text><exclusion>
				<xsl:text>
				</xsl:text><groupId>javax.faces</groupId>
				<xsl:text>
				</xsl:text><artifactId>jsf-api</artifactId>
			<xsl:text>
			</xsl:text></exclusion>
			<xsl:text>
			</xsl:text><exclusion>
				<xsl:text>
				</xsl:text><groupId>javax.faces</groupId>
				<xsl:text>
				</xsl:text><artifactId>jsf-impl</artifactId>
			<xsl:text>
			</xsl:text></exclusion>
		<xsl:text>
		</xsl:text></exclusions>
	<xsl:text>
		</xsl:text>
  	</xsl:element>
  	  	
  </xsl:template>
    
</xsl:stylesheet>

