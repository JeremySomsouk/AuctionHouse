package com.auction.house;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "AuctionHouse",
        description = "__Let's see who's the highest bidder !__",
        version = "0.42"
    )
)
public class AuctionHouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionHouseApplication.class, args);
    }
}
