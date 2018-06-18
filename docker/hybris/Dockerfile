FROM tomcat:8.5.31-jre8-alpine

ARG HYBRIS_BASE=/opt/hybris

ENV PLATFORM_HOME="${HYBRIS_BASE}/bin/platform" \
    HYBRIS_TEMP_DIR="${HYBRIS_BASE}/temp/hybris" \
    HYBRIS_ROLES_DIR="${HYBRIS_BASE}/roles" \
    HYBRIS_LOG_DIR="${HYBRIS_BASE}/log" \
    HYBRIS_DATA_DIR="${HYBRIS_BASE}/data" \
    MEMORY_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=2" \
    ADDITIONAL_CATALINA_OPTS="" \
    CATALINA_OPTS="\$MEMORY_OPTS \$ADDITIONAL_CATALINA_OPTS -DPLATFORM_HOME=\$PLATFORM_HOME -DHYBRIS_TEMP_DIR=\$HYBRIS_TEMP_DIR -DHYBRIS_ROLES_DIR=\$HYBRIS_ROLES_DIR -DHYBRIS_LOG_DIR\$HYBRIS_LOG_DIR -DHYBRIS_DATA_DIR=\$HYBRIS_DATA_DIR -Dorg.apache.tomcat.util.digester.PROPERTY_SOURCE=de.hybris.tomcat.EnvPropertySource -Djava.net.preferIPv4Stack=true" \
    HTTP_PORT=8080 \
    y_ansi_colors=false \
    y_clustermode=true \
    y_cluster_nodes_autodiscovery=true \
    y_cluster_broadcast_method_jgroups_configuration=jgroups-kube.xml \
    y_cluster_broadcast_method_jgroups=com.hybris.yps.cloud.JGroups4BroadcastMethod \
    y_bundled_tomcat_home=$CATALINA_HOME

COPY ./buildout/bin ${HYBRIS_BASE}/bin
COPY ./buildout/config ${HYBRIS_BASE}/config
COPY ./buildout/tomcat ${CATALINA_HOME}

# block hybris ant from modifiying tomcat
RUN ln -sf $CATALINA_HOME ${HYBRIS_BASE}/bin/platform/tomcat && \
    touch $CATALINA_HOME/conf/hybris-wrapper-license.conf; \
    apk update; apk add curl;
