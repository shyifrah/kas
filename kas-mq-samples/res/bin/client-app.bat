@setlocal enableextensions
@setlocal enabledelayedexpansion
@echo off

:: the program creates messages by producer threads and
:: simultaneously consumes them by the consumer threads
::
:: available arguments to the application:
::
:: client.app.queue=<queue_name>            -- name of the queue to be used
:: client.app.max.messages=<total_messages> -- the number of messages to produce
:: client.app.producers=<prod_num>          -- the number of producer threads
:: client.app.consumers=<cons_num>          -- the number of consumer threads
:: client.app.username=<user_name>          -- the user that is used to connect the KAS/MQ server 
:: client.app.password=<password>           -- the password used for authentication
:: client.app.host=<host_or_ip>             -- host name or ip address of KAS/MQ server
:: client.app.port=<port_number>            -- port number on which KAS/MQ server listening

set "BATCH_DIR=%~dp0"
call %BATCH_DIR%/launcher.bat kas.home=. kas.class=com.kas.mq.samples.KasMqClient %* 
