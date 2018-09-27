@setlocal enableextensions
@setlocal enabledelayedexpansion
@echo off

title MDB-Simulator

:: the program creates messages by producer threads and
:: simultaneously consumes them by the consumer threads
::
:: available arguments to the application:
::
:: mdb.sim.req.queuename=<queue_name>      -- name of the queue from which the mdb will read requests
:: mdb.sim.rep.queuename=<queue_name>      -- name of the queue to which the mdb will reply
:: mdb.sim.username=<user_name>            -- the user that is used to connect the KAS/MQ server 
:: mdb.sim.password=<password>             -- the password used for authentication
:: mdb.sim.host=<host_or_ip>               -- host name or ip address of KAS/MQ server
:: mdb.sim.port=<port_number>              -- port number on which KAS/MQ server listening
::
:: if an argument is specified more than once, its last occurrence takes place.
:: this means that any value passed to this batch script override the value specified below

set "BATCH_DIR=%~dp0"
call %BATCH_DIR%/launcher.bat kas.home=. kas.class=com.kas.mq.samples.mdbsim.MdbSimulator ^
  mdb.sim.create.res=true ^
  mdb.sim.req.queuename=mdb.req.queue ^
  mdb.sim.rep.queuename=mdb.rep.queue ^
  mdb.sim.username=admin ^
  mdb.sim.password=admin ^
  mdb.sim.host=localhost ^
  mdb.sim.port=14560 ^
  %*
