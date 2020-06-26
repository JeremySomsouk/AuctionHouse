package com.auction.house.controller.exception;

public class AuctionHouseNotFoundException extends RuntimeException {
    public AuctionHouseNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
