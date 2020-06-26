package com.auction.house.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auction.house.repository.model.UserBidEntity;

public interface UserBidRepository extends JpaRepository<UserBidEntity, Long> {

    List<UserBidEntity> findAllByUsername(String username);
}
