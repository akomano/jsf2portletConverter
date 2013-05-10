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
package org.jboss.portletbridge.tools.simpleConverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import org.jboss.portletbridge.tools.simpleConverter.convertor.FacesConfigConvertor;
import org.jboss.portletbridge.tools.simpleConverter.convertor.POMXmlConvertor;
import org.jboss.portletbridge.tools.simpleConverter.convertor.PortletXmlConvertor;
import org.jboss.portletbridge.tools.simpleConverter.convertor.SimpleConvertor;
import org.jboss.portletbridge.tools.simpleConverter.convertor.WebXmlConvertor;
import org.jboss.portletbridge.tools.simpleConverter.tool.MavenDependencyChecker;
import org.jboss.portletbridge.tools.simpleConverter.tool.FileManager;
import org.jboss.portletbridge.tools.simpleConverter.tool.JsfPagesFinder;
import org.jdom.JDOMException;

import static org.jboss.portletbridge.tools.simpleConverter.tool.FileManager.getFileManager;

/**
 * Simple class providing support to recursive search for JSF pages,
 * 	to provide list of available pages or select one if only one found
 * when creating web.xml for portlet app
 * 
 * @author <a href="mailto:jjamrich@redhat.com">Jan Jamrich</a>
 * @version $Revision$
 */
public class Main {

	public static String USAGE = "Usage: java -jar simpleConvertor.jar PATH_TO_PROJECT <OPTS>"
		+ "\n PATH_TO_PROJECT"
		+ "\n\t path to project dir you want to convert to portlet. This tool doesn't touch content of this folder,"
		+ " but create copy of this dir with name sufixed by 'Portlet' string."
		+ "\n   OPTS:"
		+ "\n\t--relativePath2WEB-INF <path> use to tell where is WEB-INF placed. Required only if path is different from " +
				"src/main/webapp (usual in maven based projects)"
		+ "\n\t[--portletName <newPortletName>] use to rename portlet.  Usefull in case when project name is too long, " +
				"or you don't want name of your new portlet based on source project name"
		+ "\n\t[--pbrVersion <pbrVersion>] is usefull only for maven based project. By this option you can force PBR's version " +
				"you want to depend on in project you are converting to portlet. If you give this option for non maven based project," +
				"it will impact nothing."
		+ "\n\t[--path2bridgeBinaries <path/to/pbr/binaries>] use if convert non maven based app (or archive). " +
				"This option let know this tool where look for portletbridge binaries. " +
				"Please make sure that binaries are not renamed, and match folowing pattern 'portletbridge-<impl|api>-*.jar, where '*' is ussually version. " +
				"If you specify --pbrVersion, this version will be prefered if this folder contains more versions of portletbridge binaries"
		+ "\n\t[--verbose] provide a bit more verbose output"
		+ "\n\t--richfaces use if want convert RichFaces app (for maven based project is this option retrieved from pom.xml, and override this option)"
		+ "\n\t--seam use if you want convert Seam app (for maven based project is this option retrieved from pom.xml, and override this option)"
		+ "\n\t--facelets use only if you want convert facelet app and don't have maven based project " +
				"(for maven based project is this option retrieved from pom.xml, and override this option)"
		+ "\n\t--jsp use if you want convert app using JSP (instead of xhtml/jsf)"		
		;
	public static final String DEFAULT_PBR_VERSION = "2.1.0.FINAL";
	
	public static boolean richfaces = false;
	public static boolean facelets = false;
	public static boolean seam = false;
	public static boolean jsp = false;
	
	private static boolean debug = false;
	private static boolean experimentalMode = false;
	
	private static boolean sourceProjectInZip = false;
	
	private static String projectName;
	private static String sourceProjectPath;
	private static String targetProjectPath;
	private static String defaultViewStatePage = null;
	private static String pbrVersion = null;
	private static String path2bridgeBinaries = null;
	
	private static String relPath2WebInf = "src" + File.separator + "main" + File.separator + "webapp";

	public static void main(String[] args) {
		
		if (args.length < 1) {
			System.out.println(USAGE);
			return;
		}
		
		for (int i=0; i<args.length; ++i) {
			if (args[i].equals("--help") || args[i].equals("-h")){		
				System.out.println(USAGE);
				return;
			}
		}
		
		applyAppParams(args);
		
		File sourceProject = new File(sourceProjectPath);
		if (getFileManager().isFileZip(sourceProject)){			
			sourceProjectInZip = true;			
			targetProjectPath = generateTargetProjectPath(
					getFileManager().unzipFile(sourceProject).getAbsolutePath());
		}
		
		if (sourceProject.isDirectory()) {
			targetProjectPath = generateTargetProjectPath(sourceProjectPath);
			// create copy of source project dir and make changes there
			getFileManager().copyDirRecursively(
					new File(sourceProjectPath), new File(targetProjectPath));
		}
		
		String path2WebInf = targetProjectPath + File.separator + relPath2WebInf;
		
		if (!new File(path2WebInf).exists()) {
			System.out.println("\nERROR! Path to WEB-INF directory is not valid. " +
					"Please give correct relative path to WEB-INF directory using --relativePath2WEB-INF option");
			return;
		}
		
		findJsfPagesAndChooseDefaultStates(path2WebInf);
		
		MavenDependencyChecker depChecker = new MavenDependencyChecker(targetProjectPath);		
		if (depChecker.isDependencyCheckAvailable()) {
			facelets = depChecker.check4FaceletsDependency();
			richfaces = depChecker.check4RichFacesDependency();
			seam = depChecker.check4SeamDependency();
		} else {
			System.out.println("\nWARNING! Automatic dependency detection feature is not available. " +
					"\n\tProbably reason is that your app is not maven based." +
					"\n - Your project will be converted according switches you have provided (--richfaces, --seam)" +
					"\n\t (If you haven't provided correct switches, do it again with correct switches, please)" +
					"\n\n - You can download last PortletBridge JARs (in ZIP format) from " +
					"http://sourceforge.net/projects/jboss/files/JBoss%20Portal/" +
					"\n - If you have given --path2bridgeBinaries, binaries from this location will be copied into lib dir.");
			
			if (path2bridgeBinaries!=null){
				
				File libFolder = getFileManager().findFolder(new File(targetProjectPath), "lib");
				
				System.out.println(" Trying to copy portletbridge libraries from " + path2bridgeBinaries
						+ " into " + libFolder.getAbsolutePath() + " folder");
				
				File pbApi = getFileManager().findFileByPattern(
						new String[]{"portletbridge-api-", pbrVersion}, new File(path2bridgeBinaries));
				
				File pbImpl = getFileManager().findFileByPattern(
						new String[]{"portletbridge-impl-", pbrVersion}, new File(path2bridgeBinaries));

				System.out.println("Copying file " + pbApi.getAbsolutePath() 
						+ " into " + libFolder.getAbsolutePath());
				getFileManager().copyFile(pbApi, libFolder);
				System.out.println("Copying file " + pbImpl.getAbsolutePath() 
						+ " into " + libFolder.getAbsolutePath());
				getFileManager().copyFile(pbImpl, libFolder);
			}
			
		}
		
		// do only testing related stuff and then leave
		if (experimentalMode) {
			System.out.println(" ### processsing experimental mode;");			
			return;
		}
		
		doConversion();
			
		System.out.println("Transformation done.");
		
		if (sourceProjectInZip) {
			System.out.println("Creating new archive file with new portlet created from input archive");
			File newArchiveFile = new File(generateTargetProjectPath(sourceProjectPath));
			getFileManager().createArchive(newArchiveFile, new File(targetProjectPath));
		}
	}
	
	/**
	 * Have to create new name of project (add 'Portlet'),
	 *  but only on last folder or last file before extension.
	 * @param path
	 * @return
	 */
	private static String generateTargetProjectPath(String path){
		String newPath = null;
		File x = new File(path);
		newPath = x.getParentFile() + File.separator + x.getName() + "Portlet"; 		
		return newPath;
	}
	
	private static String getParamValue(String[] args, String key) {
		for (int i=0; i<args.length; ++i)
			if (key.equals(args[i]))
				return args[i+1];

		return null;
	}
	
	private static boolean check4Switch(String[] args, String switchStr) {
		if (switchStr == null) return false;		
		for (int i=0; i<args.length; ++i)
			if (switchStr.equals(args[i]))
				return true;
		
		return false;
	}
	
	private static void applyAppParams(String[] args) {
		debug = check4Switch(args, "--verbose");
		experimentalMode = check4Switch(args, "--testMode");
		File path2project = new File(args[0]);
		
		// path to project must exists
		if (path2project.exists()) {
			// if wanna choose name for new portlet
			if (check4Switch(args, "--portletName")) {
				projectName = getParamValue(args, "--portletName");
			} else {
				projectName = path2project.getName();
			}
			sourceProjectPath = path2project.getAbsolutePath();
		} else {
			System.out.println("\nERROR! Path to project you have provided is not valid: " + path2project + "\n");
		}
		if (debug) {
			System.out.println(" ### projectName = "+projectName );
			System.out.println(" ### sourceProjectPath = "+sourceProjectPath );
		}
		
		// process switches to specify app type
		richfaces = check4Switch(args, "--richfaces");
		facelets = check4Switch(args, "--facelets");
		
		seam = check4Switch(args, "--seam");
		jsp = check4Switch(args, "--jsp");
		
		String relativePath2WebInf = getParamValue(args, "--relativePath2WEB-INF");
		if (null != relativePath2WebInf) {
			relPath2WebInf = relativePath2WebInf;
		}
		
		String optPbrVersion = getParamValue(args, "--pbrVersion");
		if (null != optPbrVersion && !"".equals(optPbrVersion)) {
			pbrVersion = optPbrVersion; 
		} else {
			pbrVersion = DEFAULT_PBR_VERSION;
		}
		
		path2bridgeBinaries = getParamValue(args, "--path2bridgeBinaries");
		if (path2bridgeBinaries!= null && !new File(path2bridgeBinaries).isDirectory()){
			path2bridgeBinaries = null;
			System.out.println("\nERROR: Path given by --path2bridgeBinaries is not directory!" +
					"\n Binaries will not be copied into lib dir. Provide correct path, please.");
		}
		
		if (debug) {
			System.out.println(" Given arguments: ");
			for (int a=0; a < args.length; a++)	
				System.out.println(" args[" + a + "] = " + args[a]);
		}
	}
	
	private static void findJsfPagesAndChooseDefaultStates(String path2WebInf) {
		
		JsfPagesFinder pagesFinder = new JsfPagesFinder(path2WebInf);
		List<String> jsfFilesFound = pagesFinder.listJsfFiles();
		
		if (jsfFilesFound.size() > 0) {
			System.out.println(" Please, choose which JSF file found you want to as default portlet's VIEW state: ");
			System.out.println(" 	WARNING!!! Please, don't set JSP pages doing forward (by jsp:forward tag) to JSF page.");
			System.out.println(" 	Use the JSF page to which JSP forward request instead. ");
			
			int idx = 1;
			for (Iterator<String> it = jsfFilesFound.iterator(); it.hasNext(); ) {
				System.out.println(" " + idx + ": " + it.next());
				++idx;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			
			Integer optionSelected = null;
			while (optionSelected == null) {							
				try {
					optionSelected = new Integer(reader.readLine());					
				} catch (Exception e) {
					// wrong selection: not number
					System.out.println("Please select number from 1 to " + jsfFilesFound.size());
				}
			}
			
			// all paths must start with "/" - expected by portlet.xml
			defaultViewStatePage = "/" + jsfFilesFound.get(optionSelected.intValue()-1);	
			if (debug) System.out.println(" ### You have selected following page: " 
					+ defaultViewStatePage);
		}
		
	}
	
	public static void doConversion() {
		String pathBase = targetProjectPath + File.separator + relPath2WebInf + File.separator;
		
		File webXml = new File(pathBase + "WEB-INF" + File.separator + "web.xml");
		File facesConfig = new File(pathBase + "WEB-INF" + File.separator + "faces-config.xml");
		File portletXml = new File(pathBase + "WEB-INF" + File.separator + "portlet.xml");		
		File pomXml = new File(targetProjectPath + File.separator + "pom.xml");
		
		// XSLT transform phase
		try {
			System.out.println("Transforming using XSLT...");
			
			
		} catch (Exception e) {
			System.out.println("An error happened!");
			e.printStackTrace(System.err);
		}
		
		// JDOM based transform phase
		try {
			System.out.println("Transforming using JDOM...");
			SimpleConvertor webXmlConvertor = new WebXmlConvertor(webXml);
			SimpleConvertor facesConfigXmlConvertor = new FacesConfigConvertor(facesConfig);
			
			// portlet.xml isn't present in default JSF project, so lets get copy
			getFileManager().copyFileFromResource(FileManager.PORTLET_XML_PATH, portletXml);
			SimpleConvertor portletXmlConvertor = new PortletXmlConvertor(portletXml, defaultViewStatePage);
			
			SimpleConvertor pomXmlConvertor = null;
			if (pomXml.exists()) {
				pomXmlConvertor = new POMXmlConvertor(pomXml, pbrVersion);
			}
			
			portletXmlConvertor.transform();
			facesConfigXmlConvertor.transform();
			webXmlConvertor.transform();
			 
			/* some projects are not maven based */
			if (pomXmlConvertor!=null)
				pomXmlConvertor.transform(); 
			
			
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean checkFacelets() {
		return facelets;
	}
	
	public static boolean checkRichFaces() {
		return richfaces;
	}
	
	public static boolean checkSeam() {
		return seam;
	}

	public static boolean checkJSP() {
		return jsp;
	}
	
	public static String getProjectName() {
		return projectName;
	}
	
	public static String getDefaultViewStatePage() {
		return defaultViewStatePage;
	}
}
