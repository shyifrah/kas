@setlocal enableextensions
@setlocal enabledelayedexpansion
@echo off

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: extract KAS_HOME
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
set "PASSED_ARGS=%*"
set "BATCH_DIR=%~dp0"
pushd %BATCH_DIR%..
set "KAS_HOME=%CD%"
popd
pushd %KAS_HOME%


::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: construct CLASS_PATH
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
for %%I in (%KAS_HOME%\lib\*.jar) do (
  set "JAR_NAME=%%I"
  set "CLASS_PATH=!JAR_NAME!;!CLASS_PATH!"
)

for %%I in (%KAS_HOME%\thirdparty\*.jar) do (
  set "JAR_NAME=%%I"
  set "CLASS_PATH=!JAR_NAME!;!CLASS_PATH!"
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
echo CLASS_PATH=%CLASS_PATH%
echo JAVA_EXEC=%JAVA_EXEC%
echo ====================================================================================


::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Run command
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
"%JAVA_EXEC%" %DEBUG_OPTS% -classpath "%CLASS_PATH%" com.kas.appl.KasApplLauncher kas.home=%KAS_HOME% %PASSED_ARGS%

popd
exit /b
