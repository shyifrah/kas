@setlocal enableextensions
@setlocal enabledelayedexpansion
@echo off

title KAS/MQ Server stop
set "PASSED_ARGS=%*"
set "CLASS_NAME=com.kas.mq.server.KasMqStopper"

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Setup
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
set "SCRIPT_DIR=%~dp0"
call %SCRIPT_DIR%/setup.bat

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Run command
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
"%JAVA_EXEC%" %DEBUG_OPTS% -classpath "%CLASS_PATH%" com.kas.appl.AppLauncher ^
  kas.class=%CLASS_NAME% ^
  kas.user=system ^
  kas.pass=system ^
  %PASSED_ARGS%
