# Read Me First
The following was discovered as part of building this project:

* The original package name 'svc-rps-support-session' is invalid and this project uses 'com.hp.svc-rps-support-session' instead.

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
* 
# Getting Started

* docker compose up -d
* Start the application in debug/run mode
* docker compose down -v
* docker logs mongo-database -f

### Reference Documentation

###Docker commands:
* build image jar in docker:
######docker build -t support-ticketservice:1.0 .
* run mongodb in docker:
######docker run -d -p 27017:27017 --name rpssupportticketdb mongo:latest
* run both containers in docker from $PATH\src\main\resources
######docker-compose up
if error check docker ps -a # and stop container by "docker container prune" and then perform "docker-componse up"

### Open CMD and perform MongoDB verify commands:
######docker exec -it rpssupportticketdb bash
######mongo
######show dbs;
######use TicketService
######show collections
######db.Ticket.find().pretty()

###useful docker commands
* docker ps -a
* docker stop jshdbjshdbv


####remove all containers and images from power shell:
* docker stop $(docker ps -q)
* docker container prune
* docker rmi $(docker images -q)
####prometheus metrics
* http://localhost:8080/actuator/prometheus