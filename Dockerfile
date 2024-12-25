FROM openjdk:21-jdk

WORKDIR /app

COPY target/sparesti-0.0.1-SNAPSHOT.jar /app/

CMD ["java", "-jar", "sparesti-0.0.1-SNAPSHOT.jar"]