FROM openjdk:8u151-jre-alpine

  #exec /opt/solr/bin/solr start -force -f -p 8983 ${ADDITIONAL_SOLR_OPTS}
#   -a            Additional parameters to pass to the JVM when starting Solr, such as to setup
#                   Java debug options. For example, to enable a Java debugger to attach to the Solr JVM
#                   you could pass: -a "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=18983"
#                   In most cases, you should wrap the additional parameters in double quotes.

COPY ./solr /opt/solr
RUN apk update; apk add bash; chmod u+x /opt/solr/bin/*
ENV SOLR_PORT=8983 \
    SOLR_JVM_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=2"
ENTRYPOINT /opt/solr/bin/solr start -force -f -p $SOLR_PORT -a "${SOLR_JVM_OPTS}" ${ADDITIONAL_SOLR_OPTS}