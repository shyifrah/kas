#!/bin/sh

###########################################################################
# Properties that can be specified and their default values
# kas.mq.db.type     -- the database type            -- mysql
# kas.mq.db.host     -- the database host            -- localhost
# kas.mq.db.port     -- the database port            -- 3306
# kas.mq.db.schema   -- the schema name              -- kas
# kas.mq.db.username -- the database user's name     -- kas
# kas.mq.db.password -- the database user's password -- kas
###########################################################################

cmd_dir=`dirname ${0}`
. ${cmd_dir}/launcher.sh kas.class=com.kas.mq.server.KasMqDbInitializer \
  kas.db.type=mysql \
  kas.db.username=kas \
  kas.db.password=kas 
  kas.db.host=localhost \
  kas.db.port=3306 \
  kas.db.schema=kas
