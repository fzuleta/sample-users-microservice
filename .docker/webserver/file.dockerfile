FROM openjdk:alpine
ARG JAR_LOCATION
ARG SSL_LOCATION

COPY $JAR_LOCATION /opt/main.jar
COPY $SSL_LOCATION /opt/ssl/keystore
COPY ./.docker/webserver/entrypoint.sh /opt/entrypoint.sh

ENV SSLPATH /opt/ssl/keystore

RUN apk update && \
    apk upgrade && \
    apk add nano && \
    apk add --update bash && \
    rm -rf /var/cache/apk/* 

RUN chmod +x /opt/entrypoint.sh && \
    chmod +x /opt/$mainJar && \
    mkdir /opt/aurelia
    
EXPOSE 8080 8443

VOLUME ["/opt/aurelia"]

CMD ["/opt/entrypoint.sh"]