FROM nginx:latest

ARG SSL_KEY
ARG SSL_CRT_BUNDLE
ARG SSL_PASSWORD

RUN rm /etc/nginx/conf.d/default.conf && \
    mkdir /usr/share/nginx/html/root
COPY ./.docker/nginx/default.conf /etc/nginx/conf.d

COPY $SSL_CRT_BUNDLE /etc/nginx/site.crt
COPY $SSL_KEY /etc/nginx/site.key
COPY ./.docker/nginx/nginx.conf /etc/nginx/nginx.conf
COPY $SSL_PASSWORD /etc/nginx/ssl.pass

VOLUME [ "/usr/share/nginx/html" ]

EXPOSE 8080 8443
