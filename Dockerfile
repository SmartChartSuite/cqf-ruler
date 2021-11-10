#!/bin/sh
FROM jetty:9-jre11

USER jetty:jetty

# Default database directory
RUN mkdir -p /var/lib/jetty/target

# Default config directory
RUN mkdir -p /var/lib/jetty/webapps/config

COPY --chown=jetty:jetty ./cqf-ruler-dstu3/target/cqf-ruler-dstu3.war /var/lib/jetty/webapps/cqf-ruler-stu3.war
COPY --chown=jetty:jetty ./cqf-ruler-r4/target/cqf-ruler-r4.war /var/lib/jetty/webapps/cqf-ruler-r4.war
EXPOSE 8080

ENV SERVER_ADDRESS_DSTU3="https://gt-apps.hdap.gatech.edu/cqf-ruler-stu3/fhir"
ENV SERVER_ADDRESS_R4="https://gt-apps.hdap.gatech.edu/cqf-ruler-r4/fhir"
ENV JAVA_OPTIONS=""

COPY ./r4/src/main/resources/hapi.properties /var/lib/jetty/webapps/config/r4.properties

COPY --chown=jetty:jetty ./scripts/docker-entrypoint-override.sh /docker-entrypoint-override.sh
ENTRYPOINT [ "sh", "/docker-entrypoint-override.sh" ]
