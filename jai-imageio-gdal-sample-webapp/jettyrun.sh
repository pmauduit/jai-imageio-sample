#!/bin/bash
#export CLASSPATH=${CLASSPATH}:/opt/gdal-georchestra/java/gdal.jar
export JAVA_LIBRARY_PATH=/usr/lib/jni:/opt/gdal-georchestra/lib:/opt/gdal-georchestra/java:/usr/lib/x86_64-linux-gnu
export MAVEN_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 \
  -Xmx2048M -XX:MaxPermSize=1024M \
  -Djava.library.path=${JAVA_LIBRARY_PATH} \
"
# -cp ${CLASSPATH}"
mvn jetty:run
