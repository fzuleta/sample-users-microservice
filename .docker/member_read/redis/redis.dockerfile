FROM redis:alpine

MAINTAINER Felipe Zuleta
ARG REDIS_PASSWORD
ARG REDIS_PORT

ADD ./entrypoint.sh /entrypoint.sh

RUN apk update && \
    apk upgrade && \
    apk add nano && \
    apk add --update bash && \
    apk add rdiff-backup && \
    rm -rf /var/cache/apk/*

RUN chmod a+x /entrypoint.sh && \
    touch /redis.conf && \
    chmod 744 /redis.conf && \
    echo 'requirepass ' $REDIS_PASSWORD >> /redis.conf && \
    echo 'port ' $REDIS_PORT >> /redis.conf && \
    echo 'appendonly no' >> /redis.conf && \
    echo 'save 900 1' >> /redis.conf && \
    echo 'save 300 10' >> /redis.conf && \
    echo 'save 60 10000' >> /redis.conf && \
    echo 'stop-writes-on-bgsave-error yes' >> /redis.conf && \
    echo 'rdbcompression yes' >> /redis.conf && \
    echo 'rdbchecksum yes' >> /redis.conf && \
    echo 'dbfilename members.rdb' >> /redis.conf && \
    echo 'dir /data' >> /redis.conf && \
    echo 'maxmemory 2147483648' >> /redis.conf


VOLUME ["/data"]

EXPOSE $REDIS_PORT

ENTRYPOINT ["/entrypoint.sh"]