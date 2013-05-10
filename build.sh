#!/bin/bash
mvn clean install
if [ ! -d lib ]; then
	echo "";
	echo " Directory ./lib doesn't exists, creating new.";
	echo "";
	mkdir lib;
 else
	echo "";
	echo " Directory ./lib exists, cleaning...";
	echo "";
	rm -drf lib/*;
fi;
cp target/dependency/*.jar lib/
# copy convertor jar
cp target/*.jar lib/
# Projects used to test this tool: tictactoe as maven based project
#rm -drf /media/velkyDisk/myProjects/tictactoe
#cp -drf /media/velkyDisk/myProjects/_tictactoe /media/velkyDisk/myProjects/tictactoe

# simple-jsf as ant based project, with different directory structure than default maven project
#rm -drf /media/velkyDisk/myProjects/simple-jsf
#cp -drf /media/velkyDisk/myProjects/_simple-jsf /media/velkyDisk/myProjects/simple-jsf
