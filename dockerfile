FROM  bellsoft/liberica-openjdk-alpine:17.0.6
LABEL maintainer="sulimov.dmitriy@otr.ru"
VOLUME /src
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
COPY src/main/resources/settings src/main/resources/settings
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
# CMD echo 'Run as command line application'
