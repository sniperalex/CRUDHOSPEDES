
FROM maven:3.9.6-eclipse-temurin-11


WORKDIR /app

#
COPY . .


RUN mvn clean compile


CMD ["mvn", "jetty:run"]