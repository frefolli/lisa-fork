@all:
	./gradlew jar

clean:
	./gradlew clean

example1:
	clear && ./gradlew run --args="inputs/example1.imp =v"

example2:
	clear && ./gradlew run --args="inputs/example2.imp =v"

example3:
	clear && ./gradlew run --args="inputs/example3.imp =v"

example4:
	clear && ./gradlew run --args="inputs/example4.imp =v"

all:
	clear && ./gradlew run --args="inputs =v"
