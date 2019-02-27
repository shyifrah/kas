@setlocal enableextensions
@setlocal enabledelayedexpansion
@echo off

title KAS/MQ Admin console
set "PASSED_ARGS=%*"
set "CLASS_NAME=com.kas.mq.console.KasMqCons"

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Setup
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
set "SCRIPT_DIR=%~dp0"
call %SCRIPT_DIR%/setup.bat

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Run command
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
"%JAVA_EXEC%" %DEBUG_OPTS% -classpath "%CLASS_PATH%" %CLASS_NAME% %PASSED_ARGS%
