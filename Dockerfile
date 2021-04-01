FROM adoptopenjdk/openjdk8:alpine-jre
EXPOSE 8080
RUN mkdir -p /dbo
WORKDIR /dbo
ENTRYPOINT ["java", "-Dderby.stream.error.file=log/derby.log", "-jar", "dbo-1.0-SNAPSHOT.jar", "--spring.profiles.active=qa"]
COPY target/dbo-1.0-SNAPSHOT.jar /dbo
