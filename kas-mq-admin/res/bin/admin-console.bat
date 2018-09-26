@setlocal enableextensions
@setlocal enabledelayedexpansion
@echo off

title KAS/MQ Admin console

set "BATCH_DIR=%~dp0"
call %BATCH_DIR%/launcher.bat kas.home=. kas.class=com.kas.mq.admin.KasMqAdmin
