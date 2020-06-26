package com.auction.house.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.auction.house.controller.exception.AuctionFinishedException;
import com.auction.house.controller.exception.AuctionHouseNotFoundException;
import com.auction.house.controller.exception.AuctionNotFoundException;
import com.auction.house.controller.exception.AuctionNotStartedException;
import com.auction.house.controller.exception.UserBidTooLowException;

@ControllerAdvice
class GlobalControllerExceptionHandler {
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Auction house not found")
    @ExceptionHandler(AuctionHouseNotFoundException.class)
    public void handleAuctionHouseNotFound() {
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Auction not found")
    @ExceptionHandler(AuctionNotFoundException.class)
    public void handleAuctionNotFound() {
    }

    @ResponseStatus(value = HttpStatus.GONE, reason = "Auction finished")
    @ExceptionHandler(AuctionFinishedException.class)
    public void handleAuctionFinished() {
    }

    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Auction not started yet")
    @ExceptionHandler(AuctionNotStartedException.class)
    public void handleAuctionNotStarted() {
    }

    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Auction bid too low")
    @ExceptionHandler(UserBidTooLowException.class)
    public void handleAuctionBidTooLow() {
    }
}