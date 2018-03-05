FROM openjdk:alpine
ARG JAR_LOCATION
ARG SSL_LOCATION
ARG VERTX_LOCATION

COPY $JAR_LOCATION /opt/main.jar
COPY $SSL_LOCATION /opt/ssl/keystore
COPY $VERTX_LOCATION /opt/ssl/keystore2
COPY ./.docker/webserver/entrypoint.sh /opt/entrypoint.sh

ENV SSLPATH /opt/ssl/keystore
ENV VERTX_SSLPATH /opt/ssl/keystore2

RUN apk update && \
    apk upgrade && \
    apk add nano && \
    apk add --update bash && \
    rm -rf /var/cache/apk/*

RUN chmod +x /opt/entrypoint.sh && \
    chmod +x /opt/$mainJar

EXPOSE 11190

CMD ["/opt/entrypoint.sh"]