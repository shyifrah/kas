@setlocal enableextensions
@setlocal enabledelayedexpansion
@echo off

set "BATCH_DIR=%~dp0"
call %BATCH_DIR%/launcher.bat kas.home=../kas-mq-server kas.class=com.kas.mq.client.KasMqClient
