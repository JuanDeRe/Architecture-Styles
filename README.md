# Architecture Styles

ARSW Lab - Distributed Architecture Styles with Java

This repository contains the work developed for the ARSW architecture styles laboratory.

The main idea of the lab was to take simple distributed systems and implement them using different communication and architecture styles, starting from low-level TCP sockets and evolving into HTTP, RMI, gRPC, microservices, and an API Gateway.

The first two parts include both the guided movie example and the applied classroom exercise.

From the RMI section onward, the implementation focuses mainly on the applied exercises.

## What this lab covers

The lab explores different ways of building distributed applications:

- TCP sockets
- HTTP with Java HttpServer
- Java RMI
- gRPC with Protocol Buffers
- Microservices
- API Gateway

Each part helped me understand a different way of communication between client and server, and also the trade-offs between simplicity, interoperability, contracts, and service separation.

## 1. TCP Sockets

In this first part, the communication was done using raw TCP sockets.

The client sends a text message to the server, and the server processes it manually.

### Movie example

The movie example used a simple TCP protocol to request movie information by ID.

![movie1](docs/images/movie1.png)

### Classroom exercise

For the applied exercise, I implemented a classroom reservation system.

The client can check, reserve, and release classrooms.

![classroom1](docs/images/classrooms1.png)

This part was useful to understand that with sockets we have full control, but we also have to define the communication protocol manually.

## 2. HTTP Architecture

In this part, the classroom system was adapted to HTTP.

Instead of sending custom text commands, the operations were exposed through routes and HTTP methods.

### Movie HTTP example

The movie example was exposed using a simple HTTP server in Java.

![moviehttp](docs/images/moviehttp.png)

### Classroom HTTP exercise

The classroom system was implemented with routes such as:

- GET /rooms
- GET /rooms?id=E303
- POST /rooms/reserve?id=E303
- POST /rooms/release?id=E303

Evidence:

![roomhttp1](docs/images/classroomsHttp1.png)

![roomhttp2](docs/images/classroomsHttp2.png)

![roomhttp3](docs/images/classroomsHttp3.png)

![roomhttp4](docs/images/classroomsHttp4.png)

This part showed how HTTP gives a more standard way to interact with a service, using paths, methods, query parameters, and responses.

## 3. Java RMI

For the RMI exercise, I implemented a laboratory equipment inventory system.

The idea was to stop using manual text protocols and instead expose remote methods through a Java interface.

The system allows operations such as:

- List equipment
- Check equipment information
- Reserve equipment
- Release equipment

Evidence:

![labrpc](docs/images/labEquipment.png)

This part helped me understand RPC-style communication, where the client calls methods on a remote object. The main limitation is that RMI is strongly tied to Java.

## 4. gRPC

In the gRPC exercise, I implemented a university wellness appointment system.

The service uses a .proto file to define the contract between client and server.

The contract includes operations to:

- Request an appointment
- Cancel an appointment
- Get appointments by student

Evidence:

![grpc](docs/images/grpc.png)

This part was useful because gRPC gives a formal contract through Protocol Buffers. It also generates classes automatically and makes the communication more structured than plain HTTP or sockets.

## 5. Microservices

For the microservices part, I decomposed the wellness system into smaller services with different responsibilities.

The implemented services were:

- AppointmentService: manages wellness appointments.
- GymService: manages gym session reservations.

Each service runs independently on a different port and has its own gRPC contract.

Evidence:

![micro1](docs/images/micro1.png)

![micro2](docs/images/micro2.png)

This part helped me understand that microservices are not just about creating many services, but about separating responsibilities clearly.

## 6. API Gateway

Finally, I implemented a simple WellnessGateway.

The gateway works as a single entry point for the client.

Instead of making the client connect directly to every service, the gateway communicates internally with the available services.

Implemented gateway operations include:

- Request an appointment
- Reserve a gym session

Some other operations were left as pending implementation, but the structure shows how the gateway would centralize access to the system.

Evidence:

![apigateway](docs/images/gateway.png)

This part showed the main purpose of an API Gateway: reducing the coupling between the client and the internal microservices.

## 7. Final Integrator Exercise - ECICIENCIA Platform

For the final exercise, I designed a distributed architecture for the ECICIENCIA event platform.

The goal of this part was not to implement a very large system, but to propose a clear microservice-based solution using the architecture styles studied during the lab.

The platform supports basic operations such as:

- Register attendees
- Consult the event agenda
- Consult activities by time slot
- Book a spot in an activity or workshop
- Control the capacity of activities

For this exercise, I decided to keep the architecture simple and avoid creating too many services without a clear reason.

The implemented microservices were:

- ActivityService: manages the agenda, activities, schedules, activity types, locations, and capacity.
- RegistrationService: manages attendee registration and activity bookings.

A simple gateway was also implemented to centralize the access from the client.

The final structure is:

- EcicienciaClient: shows the menu and reads user input.
- EcicienciaGateway: connects to the internal services and hides their details from the client.
- ActivityService: runs on port 50051.
- RegistrationService: runs on port 50052.

The client does not connect directly to the microservices. Instead, it uses the gateway, and the gateway communicates with the services.

The main operations implemented were:

- Register an attendee
- Get the full agenda
- Get activities by time slot
- Get activity information by ID
- Book an activity
- Get the activities booked by an attendee

Evidence:

![eciciencia1](docs/images/eciciencia1.png)

![eciciencia2](docs/images/eciciencia2.png)

![eciciencia3](docs/images/eciciencia3.png)

This final exercise helped me understand that microservices should not be separated just for the sake of having many services. In this case, two services were enough to represent the main responsibilities of the system.

ActivityService owns the event activities and their capacity, while RegistrationService owns attendees and bookings. This keeps the system understandable and avoids unnecessary communication between many services.

The Gateway also helps reduce coupling because the client does not need to know the ports or internal contracts of each service.

## General reflection

This lab shows how distributed architectures evolve depending on the problem being solved.

With TCP sockets, the communication is very manual.

With HTTP, the communication becomes more standard and easier to test.

With RMI, the system works through remote method calls, but mainly inside the Java ecosystem.

With gRPC, the communication is based on formal contracts and can work across different languages.

With microservices, the system is divided into smaller responsibilities.

Finally, with an API Gateway, the client gets a single access point instead of knowing every internal service.

Overall, the lab helped me understand that each architecture style has advantages and limitations. There is no single best option for every case; the decision depends on the size of the system, the type of clients, the need for interoperability, and how responsibilities are divided.