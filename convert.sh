#!/bin/bash
function add2Classpath()
{
   export COUNT=0;
   for I in $(ls lib/|grep jar); do 
      echo " Adding $I to CLASSPATH"; 
      if [[ $COUNT == 0  &&  "x$CLASSPATH" == "x" ]]; then  
         export CLASSPATH=$(pwd)/lib/$I;
      else
         export CLASSPATH=$CLASSPATH:$(pwd)/lib/$I;
      fi;
      COUNT=$((COUNT+1));
   done;
}
# update classpath
echo "Setting new temp CLASSPATH";
add2Classpath;
# test for correct params
if [[ "$1" == "--help" || "$1" == "-h" || "x$1" == "x" ]]; then 
	echo "";
	java org.jboss.portletbridge.tools.simpleConverter.Main --help
	echo "";
	exit 0;
fi;
# call convert tool with appropriate params
echo "Transforming project $1"
java org.jboss.portletbridge.tools.simpleConverter.Main $*
