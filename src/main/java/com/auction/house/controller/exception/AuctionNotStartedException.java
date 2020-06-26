package com.auction.house.controller.exception;

public class AuctionNotStartedException extends RuntimeException {
    public AuctionNotStartedException(String errorMessage) {
        super(errorMessage);
    }
}
