FROM eclipse-temurin:25-jdk-alpine

RUN addgroup -S appgroup \
    && adduser -S -G appgroup appuser \
    && mkdir -p /tmp /cache \
    && chown -R appuser:appgroup /tmp /cache

VOLUME /tmp
COPY *.jar Solicitudes.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

USER appuser
ENTRYPOINT ["/bin/sh", "-c", "exec java $JAVA_OPTS -jar /Solicitudes.jar"]
