FROM openjdk:17-jdk
ARG buildVersion
COPY target/api-terrier-crm-${buildVersion}.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]