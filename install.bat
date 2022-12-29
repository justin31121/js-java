if exist "%JAVA_HOME%\jre\lib\ext\" (
cp .\libs\js.jar "%JAVA_HOME%\jre\lib\ext\js.jar"
) else (
cp .\libs\js.jar "%JAVA_HOME%\..\ext\js.jar"
)
