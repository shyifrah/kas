@setlocal enableextensions
@setlocal enabledelayedexpansion
@echo off

title KAS/MQ Admin console
set "PASSED_ARGS=%*"
set "CLASS_NAME=com.kas.mq.admin.KasMqAdmin"

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
  %PASSED_ARGS%
