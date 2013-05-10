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
package org.jboss.portletbridge.tools.simpleConverter.tool;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

/**
 * This class provide check of dependency on RichFaces, Facelets or Seam
 * in pom.xml from projectDir (converted project)
 * 
 * @author <a href="mailto:jjamrich@redhat.com">Jan Jamrich</a>
 * @version $Revision$
 */
public class MavenDependencyChecker {
	
	private String projectDir;
	private File xmlFile;
	private boolean pomXmlExists = false;
	
	public MavenDependencyChecker(String projectDir) {
		this.projectDir = projectDir;
		this.xmlFile = new File(projectDir + File.separator + "pom.xml");
		this.pomXmlExists = xmlFile.exists();
	}
	
	public boolean isDependencyCheckAvailable() {
		return pomXmlExists;
	}
	
	public boolean check4FaceletsDependency() {
		System.out.println(" Running check4FaceletsDependency()");		
		return checkFor(projectDir, "jsf-facelets");
	}
	
	public boolean check4RichFacesDependency() {
		System.out.println(" Running check4RichFacesDependency()");
		
		return checkFor(projectDir, "richfaces");
	}
	
	public boolean check4SeamDependency() {
		System.out.println(" Running check4SeamDependency()");
		
		return checkFor(projectDir, "jboss-seam");
	}
	
	
	private boolean checkFor(String projectDir, String dependencyName){
		SAXBuilder builder = new SAXBuilder();
		
		try {			
				System.out.println(" Path to pom.xml: " + xmlFile.getAbsolutePath());
				Document document = (Document) builder.build(xmlFile);
				Element rootNode = document.getRootElement();
				
				Namespace ns = rootNode.getNamespace();
				Element dependencies = rootNode.getChild("dependencies", ns);			
				Iterator dependenciesIt = dependencies.getChildren().iterator();
				while (dependenciesIt.hasNext()) {
					Element dependency = (Element) dependenciesIt.next();				
					if ("dependency".equals(dependency.getName())) {
						if (dependency.getChildText("artifactId", ns).contains(dependencyName)){
							return true;	
						}						
					}
				}
			
		} catch (IOException io) {
			System.out.println("Your project in directory " + projectDir + " doesn't contain pom.xml because is probably not maven based. Please use appropriate switches to instruct this tool which kind of app you want convert.");
			System.out.println(io.getMessage());
		} catch (JDOMException jdomex) {
			System.out.println(jdomex.getMessage());
		}
		
		return false;
	}
}
