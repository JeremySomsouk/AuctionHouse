package com.auction.house.controller;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.auction.house.controller.dto.AuctionDto;
import com.auction.house.controller.dto.UserBidDto;
import com.auction.house.service.AuctionService;

@RestController
@RequestMapping("/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    @Autowired
    AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @PutMapping("/{auctionHouseName}")
    AuctionDto createOrUpdateAuction(@PathVariable("auctionHouseName") String auctionHouseName,
                                     @RequestBody AuctionDto auctionDto) {

        checkArgument(auctionDto.getAuctionName() != null && !auctionDto.getAuctionName().isEmpty());
        checkArgument(auctionDto.getStartPrice() != null && auctionDto.getStartPrice() >= 0f);
        checkArgument(
            auctionDto.getCurrentPrice() != null && auctionDto.getStartPrice().equals(auctionDto.getCurrentPrice()));

        return auctionService.insertOrUpdate(auctionHouseName, auctionDto);
    }

    @GetMapping("/{auctionHouseName}")
    List<AuctionDto> getAllAuctionsForAuctionHouse(@PathVariable("auctionHouseName") String auctionHouseName) {

        checkArgument(auctionHouseName != null && !auctionHouseName.isEmpty());

        return auctionService.getAllAuctionsForAuctionHouse(auctionHouseName);
    }

    @DeleteMapping("/{auctionId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    void deleteAuctionById(@PathVariable(value = "auctionId") Long auctionId) {

        checkArgument(auctionId != null && auctionId >= 0);

        auctionService.deleteAuction(auctionId);
    }

    @PostMapping("/{auctionId}/bid")
    AuctionDto bidForAuction(@PathVariable(value = "auctionId") Long auctionId, @RequestBody UserBidDto userBidDto) {

        checkArgument(userBidDto.getUserName() != null && !userBidDto.getUserName().isEmpty());
        checkArgument(userBidDto.getUserBid() != null && userBidDto.getUserBid() >= 0f);
        checkArgument(auctionId != null && auctionId >= 0);

        return auctionService.bidForAuction(auctionId, userBidDto);
    }

    @GetMapping("/user/{username}/bids")
    List<UserBidDto> getAllBidsForUser(@PathVariable("username") String username) {

        return auctionService.getAllBidsOfUser(username);
    }
}