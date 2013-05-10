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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.TransformerFactoryConfigurationError;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import static org.jboss.portletbridge.tools.simpleConverter.tool.FileManager.getFileManager;

/**
 * Class providing basic convert functionality based on JDOM
 * (Using JDOM instead of XSLT is a bit more useful)
 * 
 * @author <a href="mailto:jjamrich@redhat.com">Jan Jamrich</a>
 * @version $Revision$
 */
public abstract class SimpleJDOMConvertor implements SimpleConvertor {
	
	Document document;
	Element rootNode;			
	Namespace ns;
	File xmlFile;
	// FileManager fileManager = FileManager.getInstance();
	
	public SimpleJDOMConvertor(File xmlFile) throws JDOMException, IOException {
		getFileManager().backupInputFile(xmlFile);
		this.xmlFile = xmlFile;
		document = getDocument(xmlFile);
		rootNode = document.getRootElement();			
		ns = rootNode.getNamespace();
	}
	
	private Document getDocument(File xmlFile) throws JDOMException, IOException{
		SAXBuilder builder = new SAXBuilder();	
		return (Document) builder.build(xmlFile);		
	}
	
	public void saveDocument() {
		try {			
			FileOutputStream outputStream = new FileOutputStream(xmlFile);
			 XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			 out.output(document, outputStream);
			
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * This method provide converting functionality
	 *  according to file given in ctor.
	 */
	public abstract void transform();
	
}