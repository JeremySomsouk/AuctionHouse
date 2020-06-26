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
public class UserBidEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long bidId;

    @Column(nullable = false)
    String username;

    @Column(nullable = false)
    Float bidValue;

    @Column(nullable = false)
    Long auctionId;

    @Column(nullable = false)
    Instant bidTime;
}
