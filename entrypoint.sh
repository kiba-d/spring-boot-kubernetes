#!/usr/bin/env bash
set -x

#java -cp . JmxInvoke "service:jmx:rmi:///jndi/rmi://localhost:9010/jmxrmi" "spring-boot-kubernetes:type=Endpoint,name=Health" "health"

JMX_PORT=9010
HOST="0.0.0.0"

java \
  -Dsun.management.jmxremote.level=FINEST \
  -Dsun.management.jmxremote.handlers=java.util.logging.ConsoleHandler \
  -Djava.util.logging.ConsoleHandler.level=FINEST \
  -Dcom.sun.management.jmxremote.local.only=false \
  -Dcom.sun.management.jmxremote.ssl=false \
  -Dcom.sun.management.jmxremote.authenticate=false \
  -Dcom.sun.management.jmxremote.port=$JMX_PORT \
  -Dcom.sun.management.jmxremote.rmi.port=$JMX_PORT \
  -Dcom.sun.management.jmxremote.host=$HOST \
  -Djava.rmi.server.hostname=$HOST \
  -jar app.jar
