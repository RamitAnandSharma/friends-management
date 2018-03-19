
# Friends Management System
Friends Management System is simple friends management system, which implements simple functionalities like "Friend", "Unfriend", "Block", "Receive Updates" etc. 

## Technologies Used

 - App server - Tomcat container wrapper on Spring boot
     - A popular implementation of Java web server
     - Nicely integrates with Spring Boot
 - Development Framework - Spring Boot and Spring Framework
     - Most popular DI framework
     - Spring boot can run production-ready web services with minimal config
     - I use it every day in my work
 - Development Language - Java 8
     - One of the popular languages for developing web applications
     - I use it every day in my work
 - Database - MySQL
     - Popular opensource implementation of RDBMS
     - Integrates nicely with Hibernate ORM, while spring provides zero config integration with Hibernate
     - RDBMS is DB of choice for data which has relationships among them (social network)
     - RDBMS is suitable for read-intensive applications
 - API Documentation - Swagger
	 - Easy to view documentation and use API using UI
## Testing

 - Provided JUnit testing for service layer which contains the entire business logic of the application
 - Test cases are written with almost 100% coverage for FriendsServiceImpl and UserServiceImpl to give an idea of how I would approach the testing. Further SubscriptionServiceImpl should also have JUnit with good coverage
 - JUnit is not sufficient, it is also required to do Functional Testing and Integration Testing of the API itself. However, I have not written these test cases here
## How to Run?
 - Make sure how have docker installed https://www.docker.com/get-docker
 - Clone the project `git clone https://github.com/rajeshwar1247/friends-management.git`
 - Run command `docker-compose up -d`
 - Access swagger http://localhost:8080/friends-management/swagger-ui.html
