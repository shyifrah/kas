@setlocal enableextensions
@setlocal enabledelayedexpansion
@echo off

set "BATCH_DIR=%~dp0"
call %BATCH_DIR%/launcher.bat kas.home=. kas.class=com.kas.mq.admcons.KasMqAdmin