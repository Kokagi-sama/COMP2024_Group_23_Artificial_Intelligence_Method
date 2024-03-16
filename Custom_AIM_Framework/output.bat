@ECHO OFF

:: Clear the build directory
IF EXIST ".\build\" (
    RMDIR /S /Q ".\build"
)

:: Clear the out directory
IF EXIST ".\out\" (
    RMDIR /S /Q ".\out"
)

:: Create the build and out directories
MKDIR ".\build"
MKDIR ".\out"

:: Compile all Java files in the source directory at once
javac -d ".\build" src\com\aimframeworkgrp23\*.java
IF %ERRORLEVEL% EQU 0 (
    ECHO All .class files built successfully!
) ELSE (
    ECHO Compilation failed.
)

:: Create the JAR file
jar cf "./out/aimframeworkgrp23.jar" -C "./build" .
IF %ERRORLEVEL% EQU 0 (
    ECHO Framework built successfully!
) ELSE (
    ECHO Framework build failed.
)

PAUSE