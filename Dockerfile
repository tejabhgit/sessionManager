FROM adoptopenjdk/openjdk8:alpine-jre
ADD target/svc-rps-support-session.jar svc-rps-support-session.jar
ENTRYPOINT ["java","-jar","svc-rps-support-session.jar"]
