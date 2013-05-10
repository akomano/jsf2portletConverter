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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Simple class providing support to recursive search for JSF pages,
 * 	to provide list of available pages or select one if only one found
 * when creating web.xml for portlet app
 * 
 * @author <a href="mailto:jjamrich@redhat.com">Jan Jamrich</a>
 * @version $Revision$
 */
public class JsfPagesFinder {
	String contextRoot;
	boolean verbose = false;
	List<String> jsfFiles = new ArrayList<String>();
	
	public JsfPagesFinder(String path2WebInf) {
		System.out.println("JsfPagesFinder(" + path2WebInf + ")");
		contextRoot = path2WebInf;			
	}
	
	public List<String> listJsfFiles() {
		try {
			findAllJsfFiles(contextRoot, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (verbose) {
			
			if (jsfFiles.size()==0) {
				System.out.println(" ### No files to suggest as portlet's default VIEW state found");
			}
			
			for (Iterator<String> i=jsfFiles.iterator(); i.hasNext(); ) {
				System.out.println(" ### > " + i.next());
			}
		}
		
		return jsfFiles;
	}
	
	/**
	 * Find all files in given directory recursively.
	 * @param path2WebInf
	 * @param currDir
	 * @throws IOException
	 */
	private void findAllJsfFiles(String path2WebInf, String currDir) throws IOException {
		
		File path = new File( currDir==null ? path2WebInf : path2WebInf + File.separator + currDir);
		if (verbose) System.out.println(" ### path = " + path.getAbsolutePath());
		
		String[] files = path.list();
		if (files == null) {
			if (verbose) System.out.println(" ### No files found in " + path.getPath());
			return;
		}
		
		for (int i = 0; i < files.length; i++ ) {
			File f = new File(path + File.separator + files[i]);
			
			if (f.isDirectory() && !f.getName().startsWith(".")) {
				if (verbose) System.out.println(" Dir: " + f.getPath());
				findAllJsfFiles(path2WebInf, files[i]);
			} 
			
			if (f.isFile()) {
				if (verbose) System.out.println(" File: " + f.getPath());
				// when get value of "javax.faces.DEFAULT_SUFFIX" context param from web.xml
				// we should be able to check only this file extension
				if ( f.getName().contains(".xhtml") || f.getName().contains(".jsf") || f.getName().contains(".jsp")) {
					String fileName = currDir!= null ? currDir + File.separator + f.getName() : f.getName();
					jsfFiles.add(fileName);				
				} 
			}
		}
	}
}
