package com.auction.house.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auction.house.repository.model.AuctionByAuctionHouseEntity;

public interface AuctionByAuctionHouseRepository extends JpaRepository<AuctionByAuctionHouseEntity, Long> {

    List<AuctionByAuctionHouseEntity> findAllByAuctionHouseNameEquals(String auctionHouseName);

    void deleteByAuctionId(Long auctionId);
}
