<%
String[] libraries = fr.spironet.nativeutils.ClassScope.getLoadedLibraries(ClassLoader.getSystemClassLoader());
%>
<html>
<body>
<pre>
<ul>
<%

  for (String lib : libraries) {
    out.print("<li>");
    out.print(lib);
    out.print("</li>");
  }

%>
</ul>
</pre>
</body>
</html>
