compile:
	javac -classpath /usr/share/java/jai_core.jar:/usr/share/java/jai_codec.jar JAISampleProgram.java
run: compile
	strace java -cp /usr/share/java/jai_core.jar:/usr/share/java/jai_codec.jar:. JAISampleProgram main file.png 2>&1
	
