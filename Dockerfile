FROM adoptopenjdk/openjdk8:alpine-jre
ADD target/session-manager.jar session-manager.jar
ENTRYPOINT ["java","-jar","session-manager.jar"]
