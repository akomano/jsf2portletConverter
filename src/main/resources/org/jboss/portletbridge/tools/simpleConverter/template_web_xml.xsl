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
  		<xsl:if test="not(name()='web-app')" >
  			<xsl:apply-templates select="node()" />
  		</xsl:if>
  		<xsl:if test="name()='web-app'">
  			<xsl:call-template name="own_template" />
  		</xsl:if>
  	</xsl:copy>
  </xsl:template>

  
  <xsl:template match="web-app" name="own_template" >
  	<xsl:apply-templates select="child::node()" />
  	
  	<!-- this variables are retrieved from runtime context, should be changed by params on start -->
  	<xsl:variable name="facelets">
  		<xsl:value-of select="pbr:checkFacelets()" />
  	</xsl:variable>
  	<xsl:variable name="richfaces">
  		<xsl:value-of select="pbr:checkRichFaces()" />
  	</xsl:variable>
  	<xsl:variable name="seam">
  		<xsl:value-of select="pbr:checkSeam()" />
  	</xsl:variable>
  	
  	<!-- JSF -->
  	<xsl:text>
   </xsl:text>
		<xsl:comment>Params added for JSF</xsl:comment>
		<xsl:text>
   </xsl:text>
   		<xsl:element name="context-param" namespace="http://java.sun.com/xml/ns/javaee">
   			<xsl:text>
   	</xsl:text>
   			<xsl:element name="param-name" namespace="http://java.sun.com/xml/ns/javaee">javax.portlet.faces.renderPolicy</xsl:element>
   			<xsl:text>
   	</xsl:text>
   			<xsl:element name="param-value" namespace="http://java.sun.com/xml/ns/javaee">NEVER_DELEGATE</xsl:element>
   			<xsl:text>
   </xsl:text>   			
   		</xsl:element>
   		
    <xsl:if test="$facelets='true'">
    <xsl:text>
    </xsl:text>
  	<xsl:comment>This is optional parameters for a facelets based application</xsl:comment>
  	<xsl:text>
  	</xsl:text>
  	
  	<xsl:element name="context-param" namespace="http://java.sun.com/xml/ns/javaee">
  			<xsl:text>
   	</xsl:text>
   			<xsl:element name="param-name" namespace="http://java.sun.com/xml/ns/javaee">org.ajax4jsf.VIEW_HANDLERS</xsl:element>
   			<xsl:text>
   	</xsl:text>
   			<xsl:element name="param-value" namespace="http://java.sun.com/xml/ns/javaee">org.jboss.portletbridge.application.FaceletPortletViewHandler</xsl:element>
   			<xsl:text>
   </xsl:text>   			
   	</xsl:element>
  	
    <xsl:text>
  	</xsl:text>
  		<xsl:element name="context-param" namespace="http://java.sun.com/xml/ns/javaee">
  			<xsl:text>
   	</xsl:text>
   			<xsl:element name="param-name" namespace="http://java.sun.com/xml/ns/javaee">javax.portlet.faces.RENDER_POLICY</xsl:element>
   			<xsl:text>
   	</xsl:text>
   			<xsl:element name="param-value" namespace="http://java.sun.com/xml/ns/javaee">ALWAYS_DELEGATE</xsl:element>
   			<xsl:text>
   </xsl:text>   			
   		</xsl:element>	    
	</xsl:if>
	    
	<xsl:if test="$richfaces='true'">
		<xsl:text>
		
   </xsl:text>
		<xsl:comment>Params added for RichFaces</xsl:comment>
		<xsl:text>
   </xsl:text>
   		<xsl:element name="context-param" namespace="http://java.sun.com/xml/ns/javaee">
   			<xsl:text>
   	</xsl:text>
   			<xsl:element name="param-name" namespace="http://java.sun.com/xml/ns/javaee">org.richfaces.LoadStyleStrategy</xsl:element>
   			<xsl:text>
   	</xsl:text>
   			<xsl:element name="param-value" namespace="http://java.sun.com/xml/ns/javaee">ALL</xsl:element>
   			<xsl:text>
   </xsl:text>   			
   		</xsl:element>		
		<xsl:text>
   </xsl:text>
   		<xsl:element name="context-param" namespace="http://java.sun.com/xml/ns/javaee">
   			<xsl:text>
   	</xsl:text>
   			<xsl:element name="param-name" namespace="http://java.sun.com/xml/ns/javaee">org.richfaces.LoadScriptStrategy</xsl:element>
   			<xsl:text>
   	</xsl:text>
   			<xsl:element name="param-value" namespace="http://java.sun.com/xml/ns/javaee">ALL</xsl:element>
   			<xsl:text>
   </xsl:text>   			
   		</xsl:element>
		<xsl:text>
   </xsl:text>
		<xsl:element name="context-param" namespace="http://java.sun.com/xml/ns/javaee">
			<xsl:text>
   	</xsl:text>
   			<xsl:element name="param-name" namespace="http://java.sun.com/xml/ns/javaee">org.ajax4jsf.RESOURCE_URI_PREFIX</xsl:element>
   			<xsl:text>
   	</xsl:text>
   			<xsl:element name="param-value" namespace="http://java.sun.com/xml/ns/javaee">rfRes</xsl:element>
   			<xsl:text>
   </xsl:text>   			
   		</xsl:element>
		<xsl:text>
   </xsl:text>						
	</xsl:if>
	
	<xsl:if test="$seam='true'">
		<xsl:text>
		
   </xsl:text>
		<xsl:comment>Params added for Seam</xsl:comment>
		<xsl:text>
   </xsl:text>
   		<xsl:element name="context-param" namespace="http://java.sun.com/xml/ns/javaee">
   			<xsl:text>
   	</xsl:text>
   			<xsl:element name="param-name" namespace="http://java.sun.com/xml/ns/javaee">org.jboss.portletbridge.ExceptionHandler</xsl:element>
   			<xsl:text>
   	</xsl:text>
   			<xsl:element name="param-value" namespace="http://java.sun.com/xml/ns/javaee">org.jboss.portletbridge.SeamExceptionHandlerImpl</xsl:element>
   			<xsl:text>
   </xsl:text>   			
   		</xsl:element>
		<xsl:text>
   </xsl:text>		
	</xsl:if>
  	  	
  </xsl:template>
  
  <xsl:template match="web-app/*" name="own_template2">
  	<xsl:copy>
  		<xsl:value-of select="node()" />
  	</xsl:copy>
  	
  </xsl:template>
  
</xsl:stylesheet>

