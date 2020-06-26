package com.auction.house.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auction.house.repository.model.AuctionHouseEntity;

public interface AuctionHouseRepository extends JpaRepository<AuctionHouseEntity, String> {
}
