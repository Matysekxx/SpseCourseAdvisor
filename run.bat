rem @ECHO OFF
SET JAVA_HOME=C:\Users\chalo\.jdks\ms-17.0.16\bin
SET JAR_DIR=C:\Users\chalo\IdeaProjects\SpseCourseAdvisor\build\libs
SET PROJECT_DIR=C:\Users\chalo\IdeaProjects\SpseCourseAdvisor\src\main\resources

%JAVA_HOME%\java.exe -jar %JAR_DIR%\SpseCourseAdvisor-1.0-SNAPSHOT.jar %PROJECT_DIR%

PAUSE
