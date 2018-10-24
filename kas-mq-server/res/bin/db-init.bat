@setlocal enableextensions
@setlocal enabledelayedexpansion
@echo off

title KAS/MQ Database initialization utility

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Properties that can be specified and their default values
:: kas.mq.db.type     -- the database type            -- mysql
:: kas.mq.db.host     -- the database host            -- localhost
:: kas.mq.db.port     -- the database port            -- 3306
:: kas.mq.db.schema   -- the schema name              -- kas
:: kas.mq.db.username -- the database user's name     -- kas
:: kas.mq.db.password -- the database user's password -- kas
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

set "BATCH_DIR=%~dp0"
call %BATCH_DIR%/launcher.bat kas.class=com.kas.mq.server.KasMqDbInitializer ^
  kas.mq.db.type=mysql ^
  kas.mq.db.username=kas ^
  kas.mq.db.password=kas ^
  kas.mq.db.host=localhost ^
  kas.mq.db.port=3306 ^
  kas.mq.db.schema=kas