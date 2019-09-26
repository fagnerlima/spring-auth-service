FROM openjdk:11

ENV _JAVA_OPTIONS="-Xmx512m -Xms256m"

ADD target/spring-auth-service.jar /opt/service.jar
ENTRYPOINT ["java", "-Duser.timezone=GMT-03:00", "-jar", "/opt/service.jar"]

EXPOSE 8080
