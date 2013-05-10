# Tool to convert JSF based app to portlet, using JBoss-PortletBridge

How to use this tool
	This tool need xalan.jar and jdom.jar on classpath to work correctly
	Script convert.sh provide simple routine to add appropriate jar files into classpath
	
	Assume you have richfaces based app named tictactoe, which you want convert to portlet:
	$ cd path_to_this_tool
	$ ./convert.sh $HOME/jsfprojects/tictactoe 

	This script is useful not only for maven based projects. If you want convert project with
	different directory structure than maven based project usually contains you are obliged to provide 
	some additional information such as relative path to WEB-INF folder, or some JSF improving libraries 
	used in your project, such as richFaces, Seam or Facelets
	List of all available switches to provide this information you get by --help option 
	
	$ ./convert --help
	
	After you have converted your project, you have to build it again, and then you can try deploy it - as portlet
	If you have converted non-maven based project (e.g. ANT based project), then you have to add portletbridge 
	libraries by hand. You can obtain them for example from http://www.jboss.org/portletbridge/downloads.html,
	or from http://sourceforge.net/projects/jboss/files/JBoss%20Portal/.
	In maven based project will be dependency in portletbridge libraries added automatically. 
	
	
How to build this tool
	This project is maven based. To build this project call:
	$ ./build.sh
	To create excecutable binary, script will copy simple-convertor-*.jar into lib directory.
	In this directory is convert.sh script looking for all required jars, and add them into CLASSPATH

