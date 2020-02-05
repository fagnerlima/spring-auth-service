FROM openjdk:11

ENV _JAVA_OPTIONS="-Xmx512m -Xms256m"

ADD target/spring-auth-service.jar /opt/spring-auth-service.jar
ENTRYPOINT ["java", "-Duser.timezone=GMT-03:00", "-jar", "/opt/spring-auth-service.jar"]

EXPOSE 8080