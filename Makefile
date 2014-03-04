compile:
	javac -classpath '/usr/share/java/*' JAISampleProgram.java
run: compile
	strace java -cp '/usr/share/java/*:.' JAISampleProgram main file.png 2>&1
	
