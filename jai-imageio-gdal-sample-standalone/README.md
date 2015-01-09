standalone
===========

```
$ export MAVEN_OPTS="-Djava.library.path=/path/to/jni:/path/to/nativelib"
$ GDAL_JAR=/path/to/gdal.jar mvn clean compile exec:java
```

It should show up a window with the sample PNG, if native JAI is enabled, as well as some other infos on stdout.

