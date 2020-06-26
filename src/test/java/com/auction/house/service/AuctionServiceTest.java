package com.auction.house.service;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.auction.house.controller.dto.AuctionDto;
import com.auction.house.controller.dto.AuctionStatusEnum;
import com.auction.house.controller.dto.UserBidDto;
import com.auction.house.controller.exception.AuctionFinishedException;
import com.auction.house.controller.exception.AuctionHouseNotFoundException;
import com.auction.house.controller.exception.AuctionNotStartedException;
import com.auction.house.controller.exception.UserBidTooLowException;
import com.auction.house.repository.AuctionByAuctionHouseRepository;
import com.auction.house.repository.AuctionHouseRepository;
import com.auction.house.repository.AuctionRepository;
import com.auction.house.repository.UserBidRepository;
import com.auction.house.repository.model.AuctionByAuctionHouseEntity;
import com.auction.house.repository.model.AuctionHouseEntity;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles({"test"})
class AuctionServiceTest {

    private final AuctionService auctionService;
    private final AuctionRepository auctionRepository;
    private final AuctionHouseRepository auctionHouseRepository;
    private final AuctionByAuctionHouseRepository auctionByAuctionHouseRepository;
    private final UserBidRepository userBidRepository;

    @Autowired
    AuctionServiceTest(AuctionService auctionService,
                       AuctionRepository auctionRepository,
                       AuctionHouseRepository auctionHouseRepository,
                       AuctionByAuctionHouseRepository auctionByAuctionHouseRepository,
                       UserBidRepository userBidRepository) {
        this.auctionService = auctionService;
        this.auctionRepository = auctionRepository;
        this.auctionHouseRepository = auctionHouseRepository;
        this.auctionByAuctionHouseRepository = auctionByAuctionHouseRepository;
        this.userBidRepository = userBidRepository;
    }

    @AfterEach
    void cleanUp() {
        auctionRepository.deleteAll();
        auctionHouseRepository.deleteAll();
        auctionByAuctionHouseRepository.deleteAll();
        userBidRepository.deleteAll();
    }

    @Test
    void insertOrUpdate_shouldThrowException_whenUnregisteredAuctionHouse() {

        // GIVEN
        AuctionDto auctionDto = AuctionDto.builder().auctionName("firstAuction").build();

        // WHEN // THEN
        Assertions.assertThrows(
            AuctionHouseNotFoundException.class,
            () -> auctionService.insertOrUpdate("auction", auctionDto)
        );
    }

    @Test
    void insertOrUpdate_shouldInsertValidInput() {

        // GIVEN
        String auctionHouseName = "primaryAuctionHouse";
        insertAuctionHouse(auctionHouseName);

        // WHEN
        AuctionDto auctionDto = generateNotStartedAuctionDto("firstAuction");
        AuctionDto insertedReturnValue = auctionService.insertOrUpdate(auctionHouseName, auctionDto);

        // THEN
        assertThat(insertedReturnValue.getAuctionId()).isPositive();
        assertThat(insertedReturnValue.getAuctionName()).isEqualTo(auctionDto.getAuctionName());
        assertThat(insertedReturnValue.getDescription()).isEqualTo(auctionDto.getDescription());
        assertThat(insertedReturnValue.getStartingTime()).isEqualTo(auctionDto.getStartingTime());
        assertThat(insertedReturnValue.getCurrentPrice()).isEqualTo(auctionDto.getCurrentPrice());
        assertThat(insertedReturnValue.getStartingTime()).isEqualTo(auctionDto.getStartingTime());
        assertThat(insertedReturnValue.getEndTime()).isEqualTo(auctionDto.getEndTime());
    }


    @Test
    void insertOrUpdate_shouldUpdateExistingAuction() {

        // GIVEN
        String auctionHouseName = "primaryAuctionHouse";
        insertAuctionHouse(auctionHouseName);

        AuctionDto auctionDto = generateNotStartedAuctionDto("firstAuction");
        AuctionDto insertedReturnValue = auctionService.insertOrUpdate(auctionHouseName, auctionDto);

        // WHEN
        String newDescription = "Modified description !";
        auctionDto.setDescription(newDescription);
        AuctionDto updatedReturnValue = auctionService.insertOrUpdate(auctionHouseName, auctionDto);

        // THEN
        assertThat(updatedReturnValue.getAuctionId()).isEqualTo(insertedReturnValue.getAuctionId());
        assertThat(updatedReturnValue.getDescription()).isEqualTo(newDescription);
    }

    @Test
    void getAllAuctionsForAuctionHouse_shouldReturnNoAuction_whenNoAuctionHouse() {

        // GIVEN

        // WHEN
        List<AuctionDto> allAuctionsForAuctionHouse = auctionService.getAllAuctionsForAuctionHouse("a");

        // THEN
        assertThat(allAuctionsForAuctionHouse).isEmpty();
    }

    @Test
    void getAllAuctionsForAuctionHouse_shouldReturnNoAuction_whenNoAuction() {

        // GIVEN
        String auctionHouseName = "primaryAuctionHouse";
        insertAuctionHouse(auctionHouseName);

        // WHEN
        List<AuctionDto> allAuctionsForAuctionHouse = auctionService.getAllAuctionsForAuctionHouse(auctionHouseName);

        // THEN
        assertThat(allAuctionsForAuctionHouse).isEmpty();
    }

    @Test
    void getAllAuctionsForAuctionHouse_shouldReturnOneAuction_whenValidInsertedAuction() {

        // GIVEN
        String auctionHouseName = "primaryAuctionHouse";
        insertAuctionHouse(auctionHouseName);

        AuctionDto auctionDto = generateNotStartedAuctionDto("firstAuction");
        AuctionDto insertedAuction = auctionService.insertOrUpdate(auctionHouseName, auctionDto);

        // WHEN
        List<AuctionDto> allAuctionsForAuctionHouse = auctionService.getAllAuctionsForAuctionHouse(auctionHouseName);

        // THEN
        assertThat(allAuctionsForAuctionHouse).hasSize(1);
        assertThat(allAuctionsForAuctionHouse.get(0)).isEqualTo(insertedAuction);
    }


    @Test
    void getAllAuctionsForAuctionHouse_shouldReturnOneAuction_whenInsertionInAnotherHouse() {

        // GIVEN
        String auctionHouseName = "primaryAuctionHouse";
        insertAuctionHouse(auctionHouseName);
        String anotherAuctionHouseName = "AnotherAuctionHouse";
        insertAuctionHouse(anotherAuctionHouseName);

        AuctionDto auctionDto1 = generateNotStartedAuctionDto("firstAuction");
        AuctionDto insertedAuction1 = auctionService.insertOrUpdate(auctionHouseName, auctionDto1);

        AuctionDto auctionDto2 = generateNotStartedAuctionDto(anotherAuctionHouseName);
        auctionService.insertOrUpdate(anotherAuctionHouseName, auctionDto2);

        // WHEN
        List<AuctionDto> allAuctionsForAuctionHouse = auctionService.getAllAuctionsForAuctionHouse(auctionHouseName);

        // THEN
        assertThat(allAuctionsForAuctionHouse).hasSize(1);
        assertThat(allAuctionsForAuctionHouse.get(0)).isEqualTo(insertedAuction1);
    }

    @Test
    void getAllAuctionsForAuctionHouse_shouldReturnTwoAuctions_whenValidInsertedTwoAuctions() {

        // GIVEN
        String auctionHouseName = "primaryAuctionHouse";
        insertAuctionHouse(auctionHouseName);

        AuctionDto auctionDto1 = generateNotStartedAuctionDto("firstAuction");
        AuctionDto insertedAuction1 = auctionService.insertOrUpdate(auctionHouseName, auctionDto1);

        AuctionDto auctionDto2 = generateNotStartedAuctionDto("secondAuction");
        AuctionDto insertedAuction2 = auctionService.insertOrUpdate(auctionHouseName, auctionDto2);

        // WHEN
        List<AuctionDto> allAuctionsForAuctionHouse = auctionService.getAllAuctionsForAuctionHouse(auctionHouseName);

        // THEN
        assertThat(allAuctionsForAuctionHouse).hasSize(2);
        assertThat(allAuctionsForAuctionHouse.get(0)).isEqualTo(insertedAuction1);
        assertThat(allAuctionsForAuctionHouse.get(1)).isEqualTo(insertedAuction2);
    }

    @Test
    void deleteAuction_shouldDeleteInTheTwoTables_whenValidInsertedAuction() {

        // GIVEN
        String auctionHouseName = "primaryAuctionHouse";
        insertAuctionHouse(auctionHouseName);

        AuctionDto auctionDto = generateNotStartedAuctionDto("firstAuction");
        AuctionDto insertedAuction = auctionService.insertOrUpdate(auctionHouseName, auctionDto);

        // WHEN
        List<AuctionDto> allAuctionsForAuctionHouse = auctionService.getAllAuctionsForAuctionHouse(auctionHouseName);
        assertThat(allAuctionsForAuctionHouse).hasSize(1);

        List<AuctionByAuctionHouseEntity> auctionsByAuctionHouse = auctionByAuctionHouseRepository.findAll();
        assertThat(auctionsByAuctionHouse).hasSize(1);

        auctionService.deleteAuction(insertedAuction.getAuctionId());

        // THEN
        allAuctionsForAuctionHouse = auctionService.getAllAuctionsForAuctionHouse(auctionHouseName);
        assertThat(allAuctionsForAuctionHouse).isEmpty();

        auctionsByAuctionHouse = auctionByAuctionHouseRepository.findAll();
        assertThat(auctionsByAuctionHouse).isEmpty();
    }

    @Test
    void bidForAuction_shouldReturnAuctionNotStartedException_whenAuctionNotStartedYet() {

        // GIVEN
        String auctionHouseName = "primaryAuctionHouse";
        insertAuctionHouse(auctionHouseName);

        AuctionDto auctionDto = generateNotStartedAuctionDto("firstAuction");
        AuctionDto insertedAuction = auctionService.insertOrUpdate(auctionHouseName, auctionDto);

        UserBidDto userBidDto = UserBidDto.builder().userName("John").userBid(1f).build();

        // WHEN / THEN
        Assertions.assertThrows(
            AuctionNotStartedException.class,
            () -> auctionService.bidForAuction(insertedAuction.getAuctionId(), userBidDto)
        );
    }

    @Test
    void bidForAuction_shouldReturnAuctionFinishedException_whenAuctionFinished() {

        // GIVEN
        String auctionHouseName = "primaryAuctionHouse";
        insertAuctionHouse(auctionHouseName);

        AuctionDto auctionDto = generateFinishedAuctionDto("firstAuction");
        AuctionDto insertedAuction = auctionService.insertOrUpdate(auctionHouseName, auctionDto);

        UserBidDto userBidDto = UserBidDto.builder().userName("John").userBid(1f).build();

        // WHEN / THEN
        Assertions.assertThrows(
            AuctionFinishedException.class,
            () -> auctionService.bidForAuction(insertedAuction.getAuctionId(), userBidDto)
        );
    }

    @Test
    void bidForAuction_shouldReturnUserBidTooLowException_whenUsersBidTooLow() {

        // GIVEN
        String auctionHouseName = "primaryAuctionHouse";
        insertAuctionHouse(auctionHouseName);

        AuctionDto auctionDto = generateStartedAuctionDto("firstAuction");
        AuctionDto insertedAuction = auctionService.insertOrUpdate(auctionHouseName, auctionDto);

        UserBidDto userBidDto = UserBidDto.builder().userName("John").userBid(1f).build();

        // WHEN / THEN
        Assertions.assertThrows(
            UserBidTooLowException.class,
            () -> auctionService.bidForAuction(insertedAuction.getAuctionId(), userBidDto)
        );
    }

    @Test
    void bidForAuction_shouldReturnUserBidTooLowException_whenUsersBidEqualsCurrentPrice() {

        // GIVEN
        String auctionHouseName = "primaryAuctionHouse";
        insertAuctionHouse(auctionHouseName);

        AuctionDto auctionDto = generateStartedAuctionDto("firstAuction");
        AuctionDto insertedAuction = auctionService.insertOrUpdate(auctionHouseName, auctionDto);

        UserBidDto userBidDto = UserBidDto.builder().userName("John").userBid(50f).build();

        // WHEN / THEN
        Assertions.assertThrows(
            UserBidTooLowException.class,
            () -> auctionService.bidForAuction(insertedAuction.getAuctionId(), userBidDto)
        );
    }

    @Test
    void bidForAuction_shouldReturnAuctionWithValidVid_andAuctionIsRunning_whenUsersBidValid() {

        // GIVEN
        String auctionHouseName = "primaryAuctionHouse";
        insertAuctionHouse(auctionHouseName);

        AuctionDto auctionDto = generateStartedAuctionDto("firstAuction");
        AuctionDto insertedAuction = auctionService.insertOrUpdate(auctionHouseName, auctionDto);

        float userBid = 51f;
        UserBidDto userBidDto = UserBidDto.builder().userName("John").userBid(userBid).build();

        // WHEN
        AuctionDto returnedAuction = auctionService.bidForAuction(insertedAuction.getAuctionId(), userBidDto);

        // THEN
        assertThat(returnedAuction.getCurrentPrice()).isEqualTo(userBid);
        assertThat(returnedAuction.getAuctionStatus()).isEqualTo(AuctionStatusEnum.RUNNING);
    }

    @Test
    void getAllBidsOfUser_shouldReturnFourBidsOfThisUser() {

        // GIVEN
        String auctionHouseName = "primaryAuctionHouse";
        insertAuctionHouse(auctionHouseName);

        AuctionDto auctionDto = generateStartedAuctionDto("firstAuction");
        AuctionDto insertedAuction = auctionService.insertOrUpdate(auctionHouseName, auctionDto);

        String userName = "John";
        float firstUserBid = 51f;
        UserBidDto userBidDto1 = UserBidDto.builder().userName(userName).userBid(firstUserBid).build();
        UserBidDto userBidDto2 = UserBidDto.builder().userName(userName).userBid(52f).build();
        UserBidDto userBidDto3 = UserBidDto.builder().userName(userName).userBid(53f).build();
        UserBidDto userBidDto4 = UserBidDto.builder().userName(userName).userBid(54f).build();
        auctionService.bidForAuction(insertedAuction.getAuctionId(), userBidDto1);
        auctionService.bidForAuction(insertedAuction.getAuctionId(), userBidDto2);
        auctionService.bidForAuction(insertedAuction.getAuctionId(), userBidDto3);
        auctionService.bidForAuction(insertedAuction.getAuctionId(), userBidDto4);

        // WHEN
        List<UserBidDto> allBidsOfUser = auctionService.getAllBidsOfUser(userName);

        // THEN
        assertThat(allBidsOfUser).hasSize(4);
        assertThat(allBidsOfUser.get(0).getUserName()).isEqualTo(userName);
        assertThat(allBidsOfUser.get(0).getUserBid()).isEqualTo(firstUserBid);
    }

    private void insertAuctionHouse(String auctionHouseName) {

        AuctionHouseEntity auctionHouse = AuctionHouseEntity.builder().auctionHouseName(auctionHouseName).build();
        auctionHouseRepository.save(auctionHouse);
    }

    private AuctionDto generateNotStartedAuctionDto(String auctionName) {
        return AuctionDto.builder()
            .auctionName(auctionName)
            .description("description")
            .startPrice(50f)
            .currentPrice(50f)
            .startingTime(LocalDateTime.now(UTC).plusMinutes(10))
            .endTime(LocalDateTime.now(UTC).plusMinutes(10).plusHours(1))
            .build();
    }

    private AuctionDto generateStartedAuctionDto(String auctionName) {
        return AuctionDto.builder()
            .auctionName(auctionName)
            .description("description")
            .startPrice(50f)
            .currentPrice(50f)
            .startingTime(LocalDateTime.now(UTC).minusMinutes(10))
            .endTime(LocalDateTime.now(UTC).plusMinutes(10))
            .build();
    }

    private AuctionDto generateFinishedAuctionDto(String auctionName) {
        return AuctionDto.builder()
            .auctionName(auctionName)
            .description("description")
            .startPrice(50f)
            .currentPrice(50f)
            .startingTime(LocalDateTime.now(UTC).minusDays(7))
            .endTime(LocalDateTime.now(UTC).minusDays(1))
            .build();
    }
}