package com.auction.house.controller.dto;

import java.time.Instant;

public enum AuctionStatusEnum {
    NOT_STARTED,
    RUNNING,
    FINISHED;

    public static AuctionStatusEnum computeAuctionStatus(Instant time,
                                                         Instant startingAuctionTime,
                                                         Instant endingAuctionTime) {

        if (time.isBefore(startingAuctionTime)) {
            return AuctionStatusEnum.NOT_STARTED;
        }

        if (time.isBefore(endingAuctionTime)) {
            return AuctionStatusEnum.RUNNING;
        }

        return AuctionStatusEnum.FINISHED;
    }
}
