
FROM openjdk:17-jdk-slim
COPY target/project-assessment-0.0.1-SNAPSHOT.jar project-assessment.jar
ENTRYPOINT ["java", "-jar", "/project-assessment.jar"]
