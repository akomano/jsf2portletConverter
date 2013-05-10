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
import java.util.Iterator;
import java.util.List;

import org.jboss.portletbridge.tools.simpleConverter.Main;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * Convertor for pom.xml 
 * 
 * @author <a href="mailto:jjamrich@redhat.com">Jan Jamrich</a>
 * @version $Revision$
 */
public class PortletXmlConvertor extends SimpleJDOMConvertor {
	
	private String defaultViewStatePage;
	public static final String INIT_PARAM_KEY = "javax.portlet.faces.defaultViewId.view";
	
	public PortletXmlConvertor(File xmlFile, String defaultViewStatePage) throws JDOMException, IOException {
		super(xmlFile);
		this.defaultViewStatePage = defaultViewStatePage;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void transform() {
		Iterator<Element> itPortlets = rootNode.getChildren("portlet", ns).iterator();
		
		int portletCounter = 0;
		String portletNameSuffix = "Portlet";
		while(itPortlets.hasNext()){			
			
			if (portletCounter > 0) {
				portletNameSuffix = "Portlet" + portletCounter;
			}
			
			Element portlet = itPortlets.next();
			List<Element> initParams = portlet.getChildren("init-param", ns);
			if (initParams != null) {
				Iterator<Element> initParamsIt = initParams.iterator();			
				while (initParamsIt.hasNext()) {
					Element initParam = initParamsIt.next();
					Element name = initParam.getChild("name", ns);
					Element value = initParam.getChild("value", ns);
					
					if (INIT_PARAM_KEY.equals(name.getText())){
						if (defaultViewStatePage != null && !defaultViewStatePage.equals(value.getText())){
							// replace value only if really need
							System.out.println(" Replacing value in init-param in portlet.xml");
							value.setText(defaultViewStatePage);
						}	
						// don't need traverse remain init-params
						break;
					}
				}
			}
			
			portlet.getChild("portlet-info", ns).getChild("title", ns).setText(Main.getProjectName() + " " + portletNameSuffix);
			portlet.getChild("portlet-name", ns).setText(Main.getProjectName() + portletNameSuffix);			
			portlet.getChild("display-name", ns).setText(Main.getProjectName() + " " + portletNameSuffix);			
			
			++portletCounter;
		}
		saveDocument();		
	}

}
