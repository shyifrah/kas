@setlocal enableextensions
@setlocal enabledelayedexpansion
@echo off

title KAS/MQ Server stop
set "PASSED_ARGS=%*"

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Setup
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
set "SCRIPT_DIR=%~dp0"
call %SCRIPT_DIR%/setup.bat

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Run command
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
"%JAVA_EXEC%" %DEBUG_OPTS% -classpath "%CLASS_PATH%" com.kas.mq.server.KasMqStopper %PASSED_ARGS%
