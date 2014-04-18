Setup
====== 


```
mvn clean package
```

Once deployed into Tomcat, make sure that it is able to resolve native libs

```
${CATALINA_HOME}/bin/setenv.sh sample:

export LD_LIBRARY_PATH=/opt/gdal-georchestra/lib:/opt/gdal-georchestra/java:/usr/lib/x86_64-linux-gnu:$LD_LIBRARY_PATH
export PATH=/opt/gdal-georchestra/bin:${PATH}
export GDAL_DATA="/opt/gdal-georchestra/share/data"
export CATALINA_OPTS="-Djava.awt.headless=true -Xms48m -Xmx4096m -XX:MaxPermSize=1024m \
  -Xdebug -Xrunjdwp:transport=dt_socket,address=5006,server=y,suspend=n \
  -Dfile.encoding=UTF8 \
  -Djava.library.path=${LD_LIBRARY_PATH} \
  -Djavax.servlet.request.encoding=UTF-8 \
  -Djavax.servlet.response.encoding=UTF-8 \
  -server \
  -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:ParallelGCThreads=2 \
  -XX:SoftRefLRUPolicyMSPerMB=36000 \
  -XX:NewRatio=2 \
  -XX:+AggressiveOpts "


ls ${CATALINA_HOME}/lib/
[...]
lrwxrwxrwx 1 pmauduit pmauduit      36 Apr 18 18:41 clibwrapper_jiio.jar -> /usr/share/java/clibwrapper_jiio.jar
lrwxrwxrwx 1 pmauduit pmauduit      35 Apr 18 18:38 gdal.jar -> /opt/gdal-georchestra/java/gdal.jar
lrwxrwxrwx 1 pmauduit pmauduit      29 Apr 18 18:41 jai_codec.jar -> /usr/share/java/jai_codec.jar
lrwxrwxrwx 1 pmauduit pmauduit      28 Apr 18 18:41 jai_core.jar -> /usr/share/java/jai_core.jar
lrwxrwxrwx 1 pmauduit pmauduit      31 Apr 18 18:41 jai_imageio.jar -> /usr/share/java/jai_imageio.jar
lrwxrwxrwx 1 pmauduit pmauduit      35 Apr 18 18:41 mlibwrapper_jai.jar -> /usr/share/java/mlibwrapper_jai.jar
[...]

```

YMMV
