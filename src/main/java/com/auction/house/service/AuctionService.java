package com.auction.house.service;

import static java.time.ZoneOffset.UTC;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.house.controller.dto.AuctionDto;
import com.auction.house.controller.dto.UserBidDto;
import com.auction.house.controller.exception.AuctionFinishedException;
import com.auction.house.controller.exception.AuctionHouseNotFoundException;
import com.auction.house.controller.exception.AuctionNotFoundException;
import com.auction.house.controller.exception.AuctionNotStartedException;
import com.auction.house.controller.exception.UserBidTooLowException;
import com.auction.house.mapper.AuctionMapper;
import com.auction.house.mapper.UserBidMapper;
import com.auction.house.repository.AuctionByAuctionHouseRepository;
import com.auction.house.repository.AuctionHouseRepository;
import com.auction.house.repository.AuctionRepository;
import com.auction.house.repository.UserBidRepository;
import com.auction.house.repository.model.AuctionByAuctionHouseEntity;
import com.auction.house.repository.model.AuctionEntity;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final AuctionHouseRepository auctionHouseRepository;
    private final AuctionByAuctionHouseRepository auctionByAuctionHouseRepository;
    private final AuctionMapper auctionMapper;
    private final UserBidRepository userBidRepository;
    private final UserBidMapper userBidMapper;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository,
                          AuctionHouseRepository auctionHouseRepository,
                          AuctionByAuctionHouseRepository auctionByAuctionHouseRepository,
                          AuctionMapper auctionMapper, UserBidRepository userBidRepository,
                          UserBidMapper userBidMapper) {
        this.auctionRepository = auctionRepository;
        this.auctionHouseRepository = auctionHouseRepository;
        this.auctionByAuctionHouseRepository = auctionByAuctionHouseRepository;
        this.auctionMapper = auctionMapper;
        this.userBidRepository = userBidRepository;
        this.userBidMapper = userBidMapper;
    }

    @Transactional
    public AuctionDto insertOrUpdate(String auctionHouseName, AuctionDto auctionDto) {

        auctionHouseRepository.findById(auctionHouseName)
            .orElseThrow(() -> new AuctionHouseNotFoundException("Auction house does not exist"));

        AuctionEntity auction = auctionRepository.findByAuctionNameEquals(auctionDto.getAuctionName());
        if (auction != null) {
            updateExistingAuction(auctionDto, auction);
        } else {
            auction = auctionRepository.save(auctionMapper.toModel(auctionDto));
            saveAuctionIdByAuctionHouseName(auctionHouseName, auction);
        }

        return auctionMapper.toDto(auction);
    }

    @Transactional(readOnly = true)
    public List<AuctionDto> getAllAuctionsForAuctionHouse(String auctionHouseName) {

        List<Long> auctionsIds = auctionByAuctionHouseRepository
            .findAllByAuctionHouseNameEquals(auctionHouseName)
            .stream()
            .map(AuctionByAuctionHouseEntity::getAuctionId)
            .collect(Collectors.toList());

        return auctionRepository.findAllById(auctionsIds)
            .stream()
            .map(auctionMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAuction(Long auctionId) {

        auctionRepository.deleteById(auctionId);
        auctionByAuctionHouseRepository.deleteByAuctionId(auctionId);
    }

    @Transactional
    public AuctionDto bidForAuction(Long auctionId, UserBidDto userBidDto) {

        Optional<AuctionEntity> optAuction = auctionRepository.findById(auctionId);

        AuctionEntity auctionEntity = optAuction.orElseThrow(() -> new AuctionNotFoundException("Auction not found"));

        Instant now = Instant.now();
        throwExceptionIfAuctionNotRunning(auctionEntity, now);
        throwExceptionIfUserBidBelowAuctionPrice(userBidDto, auctionEntity);

        saveNewAuctionPrice(userBidDto, auctionEntity);
        saveUserBid(auctionId, userBidDto, now);

        return auctionMapper.toDto(auctionEntity);
    }

    @Transactional(readOnly = true)
    public List<UserBidDto> getAllBidsOfUser(String username) {

        return userBidRepository.findAllByUsername(username).stream()
            .map(userBidMapper::toDto)
            .collect(Collectors.toList());
    }

    private void saveUserBid(Long auctionId, UserBidDto userBidDto, Instant now) {

        userBidDto.setAuctionId(auctionId);
        userBidDto.setBidTime(LocalDateTime.ofInstant(now, ZoneId.of("UTC")));

        userBidRepository.save(userBidMapper.toModel(userBidDto));
    }

    private void saveNewAuctionPrice(UserBidDto userBidDto, AuctionEntity auctionEntity) {

        auctionEntity.setCurrentPrice(userBidDto.getUserBid());
        auctionEntity.setCurrentBidderUsername(userBidDto.getUserName());

        auctionRepository.save(auctionEntity);
    }

    private void throwExceptionIfUserBidBelowAuctionPrice(UserBidDto userBidDto, AuctionEntity auctionEntity) {

        if (auctionEntity.getCurrentPrice() >= userBidDto.getUserBid()) {
            throw new UserBidTooLowException("User bid is too low comparing to current price");
        }
    }

    private void throwExceptionIfAuctionNotRunning(AuctionEntity auctionEntity, Instant now) {

        if (now.isAfter(auctionEntity.getEndTime())) {
            throw new AuctionFinishedException("Auction is over.");
        } else if (now.isBefore(auctionEntity.getStartingTime())) {
            throw new AuctionNotStartedException("Auction not started yet.");
        }
    }

    private void updateExistingAuction(AuctionDto auctionDto, AuctionEntity auctionEntity) {

        auctionEntity.setDescription(auctionDto.getDescription());
        auctionEntity.setCurrentPrice(auctionDto.getCurrentPrice());
        auctionEntity.setStartPrice(auctionDto.getStartPrice());
        auctionEntity.setStartingTime(auctionDto.getStartingTime().toInstant(UTC));
        auctionEntity.setEndTime(auctionDto.getEndTime().toInstant(UTC));
        auctionRepository.save(auctionEntity);
    }

    private void saveAuctionIdByAuctionHouseName(String auctionHouseName, AuctionEntity auctionSaved) {

        auctionByAuctionHouseRepository.save(
            AuctionByAuctionHouseEntity.builder()
                .auctionHouseName(auctionHouseName)
                .auctionId(auctionSaved.getAuctionId())
                .build()
        );
    }
}
