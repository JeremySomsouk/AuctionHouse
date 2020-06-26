package com.auction.house.controller.exception;

public class AuctionNotFoundException extends RuntimeException {
    public AuctionNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
