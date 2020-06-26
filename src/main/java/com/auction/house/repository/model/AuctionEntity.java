package com.auction.house.repository.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuctionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long auctionId;

    @Column(nullable = false, unique = true)
    String auctionName;

    @Column(nullable = false)
    String description;

    @Column(nullable = false)
    Instant startingTime;

    @Column(nullable = false)
    Instant endTime;

    @Column(nullable = false)
    Float startPrice;

    @Column(nullable = false)
    Float currentPrice;

    String currentBidderUsername;
}
