@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF)
@REM Maven start up batch script
@REM ----------------------------------------------------------------------------

@REM Begin all REM://maven.apache.org/download.html
@echo off

set MAVEN_PROJECTBASEDIR=%~dp0

@REM Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto checkMavenWrapperJar
echo Error: JAVA_HOME is not set and no 'java' command could be found in your PATH.
goto error

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%\bin\java.exe
if exist "%JAVA_EXE%" goto checkMavenWrapperJar
echo Error: JAVA_HOME is not defined correctly.
goto error

:checkMavenWrapperJar
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
if exist %WRAPPER_JAR% goto runMaven

@REM Download maven-wrapper.jar if it doesn't exist
echo Downloading Maven Wrapper...
powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar' -OutFile '%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar' -UseBasicParsing}"

:runMaven
"%JAVA_EXE%" ^
  -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" ^
  -jar %WRAPPER_JAR% ^
  %*

if %ERRORLEVEL% neq 0 goto error
goto end

:error
set ERROR_CODE=1

:end
exit /b %ERROR_CODE%
