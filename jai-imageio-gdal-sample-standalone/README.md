standalone
===========

```
$ export MAVEN_OPTS="-Djava.library.path=/usr/lib/jni:/opt/gdal"
$ mvn clean compile exec:java
```

It should show up a window with the sample PNG, if native JAI is enabled, as well as some other infos on stdout.

