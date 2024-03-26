@ECHO OFF

echo Starting Maven build...
mvn clean compile assembly:single

echo Maven build finished.
PAUSE