package com.auction.house.controller.exception;

public class UserBidTooLowException extends RuntimeException {
    public UserBidTooLowException(String errorMessage) {
        super(errorMessage);
    }
}
