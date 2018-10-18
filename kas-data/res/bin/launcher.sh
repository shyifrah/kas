#!/bin/sh

#:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
#: extract KAS_HOME
#:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
CMD_ARGS="${*}"
CMD_DIR=`dirname ${0}`
pushd ${CMD_DIR}/.. >> /dev/null
KAS_HOME=`pwd`
popd >> /dev/null
cd ${KAS_HOME}

#:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
#: construct CLASS_PATH
#:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
CLASS_PATH="."
for f in `ls ${KAS_HOME}/lib/*.jar`
do
  CLASS_PATH="${CLASS_PATH}:${f}"
done

#:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
#: find java executable
#:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
if [ -z "${JAVA_HOME}" ]
then
  JAVA_EXEC=`which java`
else
  JAVA_EXEC="${JAVA_HOME}/bin/java"
fi

#:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
#: append DEBUG_OPTS if needed
#:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
DEBUG_OPTS=""
if [ ! -z "${KAS_DEBUG}" ]
then

  if [ -z "${KAS_DEBUG_SUSPEND}" ]
  then
    KAS_DEBUG_SUSPEND=n
  fi
  
  if [ -z "${KAS_DEBUG_PORT}" ]
  then
    KAS_DEBUG_PORT=8567
  fi
  
  DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,address=${KAS_DEBUG_PORT},suspend=${KAS_DEBUG_SUSPEND}"
fi

#:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
#: layout variables before execution
#:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo "===================================================================================="
echo "KAS_HOME=${KAS_HOME}"
echo "CLASS_PATH=${CLASS_PATH}"
echo "JAVA_EXEC=${JAVA_EXEC}"
echo "===================================================================================="

#:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
#: Run command
#:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
${JAVA_EXEC} ${DEBUG_OPTS} -classpath ${CLASS_PATH} com.kas.appl.KasApplLauncher kas.home=${KAS_HOME} ${CMD_ARGS}
