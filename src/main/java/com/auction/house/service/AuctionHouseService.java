package com.auction.house.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.house.controller.dto.AuctionHouseDto;
import com.auction.house.mapper.AuctionHouseMapper;
import com.auction.house.repository.AuctionHouseRepository;
import com.auction.house.repository.model.AuctionHouseEntity;

@Service
public class AuctionHouseService {

    private final AuctionHouseRepository auctionHouseRepository;
    private final AuctionHouseMapper auctionHouseMapper;

    @Autowired
    public AuctionHouseService(AuctionHouseRepository auctionHouseRepository,
                               AuctionHouseMapper auctionHouseMapper) {
        this.auctionHouseRepository = auctionHouseRepository;
        this.auctionHouseMapper = auctionHouseMapper;
    }

    @Transactional
    public AuctionHouseDto insert(AuctionHouseDto auctionHouseDto) {

        AuctionHouseEntity auctionHouseSaved = auctionHouseRepository.save(auctionHouseMapper.toModel(auctionHouseDto));
        return auctionHouseMapper.toDto(auctionHouseSaved);
    }

    @Transactional(readOnly = true)
    public List<AuctionHouseDto> getAllAuctionHouses() {

        List<AuctionHouseEntity> allAuctionHouses = auctionHouseRepository.findAll();
        return allAuctionHouses.stream()
            .map(auctionHouseMapper::toDto)
            .collect(toList());
    }

    @Transactional
    public void deleteAuctionHouse(String auctionHouseName) {
        try {
            auctionHouseRepository.deleteById(auctionHouseName);
        } catch (EmptyResultDataAccessException e) {
            // do nothing if it's already deleted, send back no-content.
        }
    }
}
