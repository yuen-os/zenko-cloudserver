FROM eclipse-temurin:17.0.2_8-jre-alpine
ADD target/zenko_cloudserver-0.0.1.jar zenko_cloudserver.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "zenko_cloudserver.jar"]

