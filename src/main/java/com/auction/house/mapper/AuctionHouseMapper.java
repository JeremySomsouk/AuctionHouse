package com.auction.house.mapper;

import org.springframework.stereotype.Component;

import com.auction.house.controller.dto.AuctionHouseDto;
import com.auction.house.repository.model.AuctionHouseEntity;

@Component
public class AuctionHouseMapper implements Mapper<AuctionHouseDto, AuctionHouseEntity> {

    public final AuctionHouseDto toDto(AuctionHouseEntity auctionHouseEntity) {

        return AuctionHouseDto.builder()
            .auctionHouseName(auctionHouseEntity.getAuctionHouseName())
            .build();
    }

    public final AuctionHouseEntity toModel(AuctionHouseDto auctionHouseDto) {

        return AuctionHouseEntity.builder()
            .auctionHouseName(auctionHouseDto.getAuctionHouseName())
            .build();
    }
}
