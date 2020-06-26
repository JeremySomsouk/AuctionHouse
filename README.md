# AuctionHouse - Let's start bidding ! 
## Introduction

I used [Spring Initializr](https://start.spring.io/) to start my project.
It is a Java 8 project, using an H2 in memory database.

Using gradle, you can start the project with the following command :
> ./gradlew build test bootRun

After start up, see the [OpenApi documentations](http://localhost:8080/swagger-ui.html) for more information about the endpoints.
You can use it to craft your own cURL commands.

If you don't want to bother, I made available in the root folder (/postman) an export of postman which contains a collection of requests that can be sent to this service.

## Development details

### Step 1
In the AuctionHouseController calling the AuctionHouseService, you'll find the creation, update, deletion and retrieval of auction houses. This is merely a CRUD for the AuctionHouseEntity.

### Step 2
In the AuctionController calling the AuctionService, you'll find the creation and listing of auction for a given auction house. I used another db table to keep a link between which auction has been created for which auction house. This is mostly due to my past using Cassandra, but here I didn't want to use relational join or whatever.

### Step 3
This is the interesting part. I could have make another controller just for the bidding to separate the logic, but here I thought that it was kind of bound to the auction, so I went that way with the AuctionController once again. The interesting part here is I update the auction value after some validation, each time a bid is valid, and register a user bid into another table. This new table is only used to get back all the biddings of a given user.
Since we update at live time the currentBidderUsername of the auction entity, with the AuctionService refusing bidding after the end time, we then have the auctionWinner after the end time. The AuctionMapper has that business logic inside. I'm confronted with myself to know if a mapper should really have that kind of business logic, I really don't know here, but it was convenient in this case since I return the computed value for each read. The winner of the auction will not be displayed until the auction is finished.

### Misc
- I used some mappers to have a border control between DTOs and entities. This is quite overkill for this kind of test, but in my opinion it could be useful under certain conditions. It mostly forces to not have spaghetti code ; 
- I used a Spring global controller to handle the mapping between code thrown exceptions and http response status ;
- Since my project is based on H2 in-memory database configured by SpringBoot, I didn't have to mock the calls to the repository for the Services test ;
- I could have made some end-to-end, integration and mapper testing here, but I don't think in this case it would have helped you evaluate my project more, so I left that undone ;
- I used lombok for convenience, JUnit 5 for testing ;
- I try not to comment my code that much, but instead having meaningful method names ;
- The description of all the APIs are generated by Swagger / OpenAPI.

Thank you if you went that far on the review, I will gladly take any comment on my code to improve myself.