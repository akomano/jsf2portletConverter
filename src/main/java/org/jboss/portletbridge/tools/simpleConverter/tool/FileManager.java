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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Manager for file system based operation
 * 
 * @author <a href="mailto:jjamrich@redhat.com">Jan Jamrich</a>
 * @version $Revision$
 */
public class FileManager {
	
	private static FileManager instance;
	
	// forbidden default constructor
	private FileManager(){};
	
	public static String PORTLET_XML_PATH = 
		"/org/jboss/portletbridge/tools/simpleConverter/portlet.xml";
	
	public static FileManager getFileManager() {
		
		if (instance == null) {
			instance = new FileManager();
		}
		return instance;
	}
	
	public void copyFileFromResource(String fromFileName, File to) {
		try {
			
			if (to.getName().contains("portlet.xml")) {
				
				// create new file only for portlet.xml, this I want add
				if (!to.exists()) {
					to.createNewFile();
				}
				
				// for portlet.xml is required make copy of resource, 
				// because this file hasn't been here before.
				InputStream inStream = getClass().getResourceAsStream(fromFileName);
				// We want to copy content from resource into file in proper location
				
				OutputStream outStream = new FileOutputStream(to);
				copyStream(inStream, outStream);
			}
			
		} catch (IOException e) {
			System.err.println("Error creating input file.");
			e.printStackTrace();
		}
	}
	
	public void copyDirRecursively(File sourceDir, File targetDir){
		/* System.out.println("copyDirRecursively(" + sourceDir.getAbsolutePath()
				+ ", " + targetDir.getAbsolutePath() + ") -->"); */
		if (!sourceDir.isDirectory()) throw new IllegalArgumentException(
				"'" + sourceDir.getAbsolutePath() + "' is not directory");
		
		if (targetDir.exists()) {
			forceDelete(targetDir);
		}
			
		try {
			targetDir.mkdir();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] filesInCurrentDir = sourceDir.list();
		
		File x = null;
		for (int i=0; i<filesInCurrentDir.length; ++i){			
			
			x = new File(sourceDir.getAbsoluteFile() + File.separator + filesInCurrentDir[i]);
			
			if (x.isFile()) {
				if (!x.getName().startsWith(".")) {
					copyFile(x, new File(targetDir.getAbsolutePath() 
							+ File.separator + x.getName()));
				}
			} else {
				// don't copy hidden *nix files/folders 
				if (!x.getName().startsWith(".")) {
					copyDirRecursively(x, new File(targetDir.getAbsolutePath()
							+ File.separator + filesInCurrentDir[i]));
				}
			}
		}
		
	}
	
	public File copyFile(File source, File target) {
					
		// copy content only if source file exists
		if (source.exists() && source.isFile()) {
			
			if (target.isDirectory()){
				// if copy file to folder, modify target file path
				target = new File(target.getAbsoluteFile() 
									+ File.separator + source.getName());
			}
			
			// create new file only if not exists
			if (!target.exists()) {
			
				try {
					target.createNewFile();
					
					InputStream inStream = new FileInputStream(source);
					OutputStream outStream = new FileOutputStream(target);
					
					copyStream(inStream, outStream);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return target;
	}
	
	public void copyStream(InputStream inStream, OutputStream outStream){
		byte[] buffer = new byte[1024];
		int length;
		
		try {
			while ( ( length = inStream.read(buffer) ) > 0 ) {
				outStream.write(buffer, 0, length);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try { inStream.close(); } catch (IOException e) {}
			try { outStream.close(); } catch (IOException e) {}
		}
	}
	
	/**
	 * Function which delete file or recursively delete folder,
	 * give by param fileOrDir
	 * @param fileOrDir - path to file or directory
	 */
	public void forceDelete(File fileOrDir) {
		
		if (fileOrDir.exists()) {
			String[] childFiles = null;
			if (fileOrDir.isDirectory()){
				childFiles = fileOrDir.list();
			}
			
			if (fileOrDir.isFile() || (fileOrDir.isDirectory() && childFiles.length==0) ) {
				fileOrDir.delete();
			} else {
				for (int i=0; i<childFiles.length; ++i) {
					forceDelete(new File(
							fileOrDir.getAbsoluteFile() + File.separator + childFiles[i]));
				}
				forceDelete(fileOrDir);
			}
		} 
	}
	
	public File backupInputFile(File in) {
		
		String backupFileName = in.getParent() + File.separator + "~" + in.getName();		
		File inOld = new File(backupFileName);		
		
		copyFile(in, inOld);
		
		// returning new IN file, because base IN file is going to be overwritten
		return inOld;
	}
	
	public File findFolder(File currentDir, String folderName){
		
		if (!currentDir.isDirectory()) {
			throw new IllegalArgumentException(
					currentDir.getAbsolutePath() + " is not directory!");
		}
		
		if (folderName == null) {
			throw new IllegalArgumentException("folderName cannot be null!");
		}
		
		if (currentDir.isDirectory()) {
			String[] files = currentDir.list();
			for (int i=0; i<files.length; ++i){
				File x = new File(currentDir.getAbsoluteFile() + File.separator + files[i]);
				if (x.isDirectory()) {
					if (folderName.equals(files[i])){
						return x;
					} else {
						findFolder(x, folderName);
					}
				}
			}
		}
		
		return null;
	}
	
	public File findFileByPattern(final String[] patterns, File location){
		
		if (!location.exists() || !location.isDirectory())
			throw new IllegalArgumentException("Location " + location.getAbsolutePath() 
					+ " doesn't exists or is not directory");
		
		File path = null;
		
		FilenameFilter filter = new FilenameFilter() {			
			public boolean accept(File dir, String name) {
				return name.startsWith(patterns[0]);
			}
		};
		
		// list only files we are looking for ('portletbridge-<api|impl>')
		String[] files = location.list(filter);
		List<String> filesList = Arrays.asList(files);
		Collections.sort(filesList);
		Iterator<String> i = filesList.iterator();
		
		String fileName = null;
		while (i.hasNext()){
			fileName = i.next();
			String version = fileName.substring(patterns[0].length(), 
					patterns[0].length() + patterns[1].length());
			if (version.equals(patterns[1])) {				
				break;
			}			
		}
		if (fileName!=null){
			path = new File(location, fileName);
		}
		
		return path;
	}
	
	/**
	 * Check if file given as param is zip file
	 * @param source
	 * @return
	 */
	public boolean isFileZip(File source) {
		// TODO JJa implementation
		return false;
	}
	
	/**
	 *  Unzip file given as param and return reference to folder with content of this zip
	 * @return
	 */
	public File unzipFile(File zipFile){
		// TODO JJa implementation
		return zipFile;
	}
	
	public void createArchive(File archive, File content){
		
		if (!archive.exists()) { 
			try {
				archive.createNewFile();
				// TODO JJa: implementation
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
