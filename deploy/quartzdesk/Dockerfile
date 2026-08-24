FROM eclipse-temurin:17-jdk

WORKDIR /opt/quartzdesk-dist

COPY quartzdesk-web-*.war quartzdesk-web.war
COPY license.key license.key

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "mkdir -p /var/lib/quartzdesk && cp --update=none /opt/quartzdesk-dist/quartzdesk-web.war /var/lib/quartzdesk/quartzdesk-web.war && cp --update=none /opt/quartzdesk-dist/license.key /var/lib/quartzdesk/license.key && cd /var/lib/quartzdesk && exec java -jar quartzdesk-web.war --port 8080 --context-path /quartzdesk"]
