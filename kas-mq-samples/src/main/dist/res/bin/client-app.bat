@setlocal enableextensions
@setlocal enabledelayedexpansion
@echo off

title Client-App

:: the program creates messages by producer threads and
:: simultaneously consumes them by the consumer threads
::
:: available arguments to the application:
::
:: client.app.message.type=<num_msg_type>     -- 0-no body, 1-string, 2-object, 3-bytes, 4-map
:: client.app.put.queuename=<queue_name>      -- name of the queue to which messages are put
:: client.app.get.queuename=<queue_name>      -- name of the queue frmo which messages are get
:: client.app.total.messages=<total_messages> -- the number of messages to produce
:: client.app.total.producers=<prod_num>      -- the number of producer threads
:: client.app.total.consumers=<cons_num>      -- the number of consumer threads
:: client.app.username=<user_name>            -- the user that is used to connect the KAS/MQ server 
:: client.app.password=<password>             -- the password used for authentication
:: client.app.host=<host_or_ip>               -- host name or ip address of KAS/MQ server
:: client.app.port=<port_number>              -- port number on which KAS/MQ server listening
::
:: if an argument is specified more than once, its last occurrence takes place.
:: this means that any value passed to this batch script override the value specified below

set "BATCH_DIR=%~dp0"
call %BATCH_DIR%/launcher.bat kas.home=. kas.class=com.kas.mq.samples.clientapp.ClientApp ^
  client.app.message.type=0 ^
  client.app.put.queuename=mdb.req.queue ^
  client.app.get.queuename=mdb.rep.queue ^
  client.app.total.messages=10000 ^
  client.app.total.producers=1 ^
  client.app.total.consumers=1 ^
  client.app.username=admin ^
  client.app.password=admin ^
  client.app.host=localhost ^
  client.app.port=14560 ^
  %*
