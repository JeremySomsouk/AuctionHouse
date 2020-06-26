package com.auction.house.mapper;

import static java.time.ZoneOffset.UTC;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.auction.house.controller.dto.UserBidDto;
import com.auction.house.repository.model.UserBidEntity;

@Component
public class UserBidMapper implements Mapper<UserBidDto, UserBidEntity> {

    public final UserBidDto toDto(UserBidEntity userBidEntity) {

        return UserBidDto.builder()
            .auctionId(userBidEntity.getAuctionId())
            .bidTime(LocalDateTime.ofInstant(userBidEntity.getBidTime(), ZoneId.of("UTC")))
            .userBid(userBidEntity.getBidValue())
            .userName(userBidEntity.getUsername())
            .build();
    }

    public final UserBidEntity toModel(UserBidDto userBidDto) {

        return UserBidEntity.builder()
            .auctionId(userBidDto.getAuctionId())
            .username(userBidDto.getUserName())
            .bidTime(userBidDto.getBidTime().toInstant(UTC))
            .bidValue(userBidDto.getUserBid())
            .build();
    }

}
