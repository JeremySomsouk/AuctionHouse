package com.auction.house.controller;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.auction.house.controller.dto.AuctionHouseDto;
import com.auction.house.service.AuctionHouseService;

@RestController
@RequestMapping("/auction-houses")
public class AuctionHouseController {

    private final AuctionHouseService auctionHouseService;

    @Autowired
    AuctionHouseController(AuctionHouseService auctionHouseService) {
        this.auctionHouseService = auctionHouseService;
    }

    @PutMapping
    AuctionHouseDto createOrUpdateAuctionHouse(@RequestBody AuctionHouseDto auctionHouseDto) {
        return auctionHouseService.insert(auctionHouseDto);
    }

    @GetMapping
    List<AuctionHouseDto> getAllAuctionHouses() {
        return auctionHouseService.getAllAuctionHouses();
    }

    @DeleteMapping("/{auctionHouseName}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    void deleteAuctionHouseByName(@PathVariable(value = "auctionHouseName") String auctionHouseName) {

        checkArgument(auctionHouseName != null && !auctionHouseName.isEmpty());
        auctionHouseService.deleteAuctionHouse(auctionHouseName);
    }
}