compile:
	/usr/lib/jvm/java-6-openjdk-amd64/bin/javac -classpath '/usr/share/java/*' JAISampleProgram.java
run: compile
	/usr/lib/jvm/java-6-openjdk-amd64/bin/java -cp '/usr/share/java/jai_core.jar:/usr/share/java/jai_codec.jar:/usr/share/java/mlibwrapper_jai.jar:.' -Djava.library.path=/usr/lib/jni JAISampleProgram main file.png
