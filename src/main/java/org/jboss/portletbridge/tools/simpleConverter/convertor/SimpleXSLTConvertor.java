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

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import static org.jboss.portletbridge.tools.simpleConverter.tool.FileManager.getFileManager;

/**
 * Class providing basic convert functionality based on XSLT
 * 
 * @author <a href="mailto:jjamrich@redhat.com">Jan Jamrich</a>
 * @version $Revision$
 */
public class SimpleXSLTConvertor implements SimpleConvertor {
	
	File sourceXML;
	String xslTemplate;
	File output;
	
	TransformerFactory tFactory = TransformerFactory.newInstance();
	// FileManager fileManager = FileManager.getInstance();
	
	public SimpleXSLTConvertor(File sourceXML, String xslTemplate){

		this.xslTemplate = xslTemplate;
		
		// sourceXML file is going to be overwritten, make backup copy
		this.sourceXML = getFileManager().backupInputFile(sourceXML);
		this.output = sourceXML;
	}
	
	public void transform() throws FileNotFoundException, TransformerException {
		Transformer transformer = tFactory.newTransformer(new StreamSource(getClass().getResourceAsStream(xslTemplate)));
		if (sourceXML.exists()) {
			// don't convert non existing files
			transformer.transform(new StreamSource(sourceXML), new StreamResult(new FileOutputStream(output)));
		}
	}
	
}
