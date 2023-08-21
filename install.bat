if exist "%JAVA_HOME%\jre\lib\ext\" (
move ".\libs\js.jar" "%JAVA_HOME%\jre\lib\ext\js.jar"
) else (
move ".\libs\js.jar" "%JAVA_HOME%\..\ext\js.jar"
)
