@all:
	./gradlew build

example1:
	clear && ./gradlew run --args="=i example1 =v"

example2:
	clear && ./gradlew run --args="=i example2 =v"

example3:
	clear && ./gradlew run --args="=i example3 =v"
