all:
	#javac -Werror -cp "./libs/*" ./src/js/http/*.java ./src/js/monad/*.java ./src/js/*.java ./src/tests/*.java -d .
	~/Downloads/android-studio/jre/bin/javac -Werror -cp "./libs/*" ./src/js/http/*.java ./src/js/monad/*.java ./src/js/*.java ./src/tests/*.java -d .
build:
	~/Downloads/android-studio/jre/bin/jar cvf ./libs/js.jar ./js/http/*.class ./js/monad/*.class ./js/*.class
	#jar cvf ./libs/js.jar ./js/http/*.class ./js/monad/*.class ./js/*.class
