/*******************************************************************************
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *******************************************************************************/
package org.jboss.portletbridge.tools.simpleConverter.convertor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.portletbridge.tools.simpleConverter.Main;
import org.jdom.Comment;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * Convertor for web.xml
 *  
 * @author <a href="mailto:jjamrich@redhat.com">Jan Jamrich</a>
 * @version $Revision$
 */
public class WebXmlConvertor extends SimpleJDOMConvertor {
	
	Map<String, String> contextParamsMap;
	
	public WebXmlConvertor(File xmlFile) throws JDOMException, IOException {
		super(xmlFile);
		contextParamsMap = initContextParamMap();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void transform() {
		
		List<Element> contextParams = rootNode.getChildren("context-param", ns);
		Iterator<Element> it = contextParams.iterator();
		Set<String> keySet = contextParamsMap.keySet();
		while (it.hasNext()) {
			String key = checkAndUpdateContextParam(it.next(), keySet);
			if (key != null) {
				keySet.remove(key);
			}
		}
		
		rootNode.addContent(new Comment("Portlet related params added by jsf2PortletConvert tool"));
		for (String key:keySet){
			addContextParams(rootNode, contextParamsMap, key);
		}		
		
		saveDocument();
	}
	
	private String checkAndUpdateContextParam(Element contextParam, Set<String> keys){
		
		Element paramName = contextParam.getChild("param-name", ns);
		
		if (paramName != null) {
			if (keys.contains(paramName.getText())) {
				contextParam.getChild("param-value", ns).setText(contextParamsMap.get(paramName.getText()));
				return paramName.getText();
			}
		}
		return null;
	}
	
	private void addContextParams(Element rootNode, Map<String, String> paramsMap, String key2Add ) {
		
		Element contextParam = new Element("context-param", ns);
		List<Element> paramsKeysVal = new ArrayList<Element>();
		
		paramsKeysVal.add(new Element("param-name", ns).setText(key2Add));
		paramsKeysVal.add(new Element("param-value", ns).setText(paramsMap.get(key2Add)));
		
		contextParam.setContent(paramsKeysVal);
		rootNode.addContent(contextParam);
	}

	private Map<String, String> initContextParamMap(){
		
		Map<String, String> m = new HashMap<String, String>();
		
		if (Main.checkFacelets()) {
			m.put("org.ajax4jsf.VIEW_HANDLERS", "org.jboss.portletbridge.application.FaceletPortletViewHandler");
			m.put("javax.portlet.faces.RENDER_POLICY", "ALWAYS_DELEGATE");
		}
		
		if (Main.checkJSP()) {
			m.put("javax.portlet.faces.renderPolicy", "NEVER_DELEGATE");				
		}
		
		if (Main.checkRichFaces()){
			m.put("org.richfaces.LoadStyleStrategy", "ALL");
			m.put("org.richfaces.LoadScriptStrategy", "ALL");
			m.put("org.ajax4jsf.RESOURCE_URI_PREFIX", "rfRes");
		}
		
		if (Main.checkSeam()){
			m.put("org.jboss.portletbridge.ExceptionHandler", "org.jboss.portletbridge.SeamExceptionHandlerImpl");
		}
		
		return m;
	}	

}
