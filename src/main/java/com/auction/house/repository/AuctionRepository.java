package com.auction.house.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auction.house.repository.model.AuctionEntity;

public interface AuctionRepository extends JpaRepository<AuctionEntity, Long> {

    AuctionEntity findByAuctionNameEquals(String auctionName);
}
