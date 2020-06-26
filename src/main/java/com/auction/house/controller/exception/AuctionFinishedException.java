package com.auction.house.controller.exception;

public class AuctionFinishedException extends RuntimeException {
    public AuctionFinishedException(String errorMessage) {
        super(errorMessage);
    }
}
