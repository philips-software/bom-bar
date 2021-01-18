FROM philipssoftware/openjdk:11

COPY target/BOM-bar*.jar /app/service.jar
COPY docker/start.sh /app/start.sh

EXPOSE 8080

ENTRYPOINT ["/app/start.sh"]
