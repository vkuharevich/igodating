FROM keycloak/keycloak:latest

USER root
ENV TZ=Europe/Moscow
RUN cp /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true

ENV KEYCLOAK_ADMIN=admin
ENV KEYCLOAK_ADMIN_PASSWORD=admin

ENV KC_LOG_LEVEL: INFO
ENV KC_PROXY: edge

ENV KC_DB=postgres
ENV KC_DB_URL=jdbc:postgresql://host.docker.internal:5432/igodating
ENV KC_DB_USERNAME=postgres
ENV KC_DB_PASSWORD=postgres
ENV KC_DB_SCHEMA=igodating_keycloak

ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev"]
