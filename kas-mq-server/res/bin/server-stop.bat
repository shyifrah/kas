@setlocal enableextensions
@setlocal enabledelayedexpansion
@echo off

title KAS/MQ Server stop

set "BATCH_DIR=%~dp0"
call %BATCH_DIR%/launcher.bat kas.home=. kas.user=admin kas.pass=admin kas.class=com.kas.mq.server.KasMqStopper
