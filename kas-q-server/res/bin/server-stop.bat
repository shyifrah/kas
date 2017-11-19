@setlocal enableextensions
@setlocal enabledelayedexpansion
@echo off

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Arguments
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
set "KAS_HOST=%~1"
if "%KAS_HOST%" == "" (
  set "KAS_HOST=localhost"
)

set "KAS_PORT=%~2"
if "%KAS_PORT%" == "" (
  set "KAS_PORT=14560"
)


::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: extract KAS_HOME
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
set "BATCH_DIR=%~dp0"
pushd %BATCH_DIR%..
set "KAS_HOME=%CD%"
popd
pushd %KAS_HOME%


::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: determine which JAR should be excluded from CLASS_PATH and construct it
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
set "SCRIPT_NAME=%~n0"
set "SCRIPT_TYPE=%SCRIPT_NAME:~0,6%"
set "INCL_JAR=%SCRIPT_TYPE%.jar"

if "%SCRIPT_TYPE%" == "server" (
  set "EXCL_JAR=client.jar"
) else (
  set "EXCL_JAR=server.jar"
)

for %%I in (%KAS_HOME%\lib\*.jar) do (
  set "JAR_NAME=%%I"
  set "JAR_SUFF=!JAR_NAME:~-10!"
  if "!JAR_SUFF!" == "%INCL_JAR%" (
    set "MAIN_JAR=!JAR_NAME!"
  )
  if not "!JAR_SUFF!" == "%EXCL_JAR%" (
    set "CLASS_PATH=!JAR_NAME!;!CLASS_PATH!"
  )
)


::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: extract JAVA_EXEC
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
if "%JAVA_HOME%" == "" (
  set "JAVA_EXEC=java.exe"
) else (
  set "JAVA_EXEC=%JAVA_HOME%/bin/java.exe"
)


::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: append DEBUG_OPTS if needed
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
if "%KAS_DEBUG%" == "" (
  set "DEBUG_OPTS="
) else (
  if "%KAS_DEBUG_SUSPEND%" == "" (
    set "KAS_DEBUG_SUSPEND=n"
  )
  
  if "%KAS_DEBUG_PORT%" == "" (
    set "KAS_DEBUG_PORT=8567"
  )
  
  set "DEBUG_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,address=!KAS_DEBUG_PORT!,suspend=!KAS_DEBUG_SUSPEND!"
)


::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: layout variables before execution
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo ====================================================================================
echo KAS_HOME=%KAS_HOME%
echo MAIN_JAR=%MAIN_JAR%
echo CLASS_PATH=%CLASS_PATH%
echo JAVA_EXEC=%JAVA_EXEC%
echo ====================================================================================


::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Run command
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
"%JAVA_EXEC%" %DEBUG_OPTS% -classpath "%CLASS_PATH%" com.kas.q.server.KasqShutdown %KAS_HOST% %KAS_PORT%

popd
exit /b
