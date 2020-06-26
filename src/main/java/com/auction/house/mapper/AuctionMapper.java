package com.auction.house.mapper;

import static java.time.ZoneOffset.UTC;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.auction.house.controller.dto.AuctionDto;
import com.auction.house.controller.dto.AuctionStatusEnum;
import com.auction.house.repository.model.AuctionEntity;

@Component
public class AuctionMapper implements Mapper<AuctionDto, AuctionEntity> {

    public final AuctionDto toDto(AuctionEntity auctionEntity) {

        AuctionStatusEnum auctionStatus = AuctionStatusEnum.computeAuctionStatus(Instant.now(),
            auctionEntity.getStartingTime(), auctionEntity.getEndTime());
        String auctionWinner =
            (auctionStatus.equals(AuctionStatusEnum.FINISHED) ? auctionEntity.getCurrentBidderUsername() : null);

        return AuctionDto.builder()
            .auctionId(auctionEntity.getAuctionId())
            .auctionName(auctionEntity.getAuctionName())
            .description(auctionEntity.getDescription())
            .startingTime(LocalDateTime.ofInstant(auctionEntity.getStartingTime(), ZoneId.of("UTC")))
            .endTime(LocalDateTime.ofInstant(auctionEntity.getEndTime(), ZoneId.of("UTC")))
            .startPrice(auctionEntity.getStartPrice())
            .currentPrice(auctionEntity.getCurrentPrice())
            .auctionStatus(auctionStatus)
            .auctionWinner(auctionWinner)
            .build();
    }

    public final AuctionEntity toModel(AuctionDto auctionDto) {

        Instant startingTime =
            auctionDto.getStartingTime() != null ? auctionDto.getStartingTime().toInstant(UTC) : null;

        Instant endTime =
            auctionDto.getEndTime() != null ? auctionDto.getEndTime().toInstant(UTC) : null;

        return AuctionEntity.builder()
            .auctionName(auctionDto.getAuctionName())
            .description(auctionDto.getDescription())
            .startingTime(startingTime)
            .endTime(endTime)
            .startPrice(auctionDto.getStartPrice())
            .currentPrice(auctionDto.getCurrentPrice())
            .build();
    }
}