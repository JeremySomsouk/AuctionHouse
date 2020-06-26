package com.auction.house.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.auction.house.controller.dto.AuctionHouseDto;
import com.auction.house.mapper.AuctionHouseMapper;
import com.auction.house.repository.AuctionHouseRepository;
import com.auction.house.repository.model.AuctionHouseEntity;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles({"test"})
class AuctionHouseServiceTest {

    private final AuctionHouseService auctionHouseService;
    private final AuctionHouseRepository auctionHouseRepository;
    private final AuctionHouseMapper auctionHouseMapper;

    @Autowired
    AuctionHouseServiceTest(AuctionHouseService auctionHouseService,
                            AuctionHouseRepository auctionHouseRepository,
                            AuctionHouseMapper auctionHouseMapper) {
        this.auctionHouseService = auctionHouseService;
        this.auctionHouseRepository = auctionHouseRepository;
        this.auctionHouseMapper = auctionHouseMapper;
    }

    @AfterEach
    void cleanUp() {
        auctionHouseRepository.deleteAll();
    }

    @Test
    void insert_withValidEntry_shouldSaveEntityAndReturnAccordingDto() {

        // GIVEN
        String auctionHouseName = "primaryAuctionHouse";
        AuctionHouseDto expectedHouse = AuctionHouseDto.builder().auctionHouseName(auctionHouseName).build();

        // WHEN
        AuctionHouseDto insertedReturnValue = auctionHouseService.insert(expectedHouse);

        // THEN
        assertThat(insertedReturnValue).isEqualTo(expectedHouse);
    }

    @Test
    void insert_withTwoHousesOfSameName_shouldRegisterOnlyOne() {

        // GIVEN
        String auctionHouseName = "primaryAuctionHouse";
        AuctionHouseDto expectedHouse1 = AuctionHouseDto.builder().auctionHouseName(auctionHouseName).build();
        AuctionHouseDto expectedHouse2 = AuctionHouseDto.builder().auctionHouseName(auctionHouseName).build();

        // WHEN
        AuctionHouseDto insertedReturnValue1 = auctionHouseService.insert(expectedHouse1);
        auctionHouseService.insert(expectedHouse2);

        // THEN
        assertThat(auctionHouseRepository.findAll()).hasSize(1);
        assertThat(insertedReturnValue1).isEqualTo(expectedHouse1);

    }

    @Test
    void getAllAuctionHouses_withEmptyAuction_shouldReturnNone() {

        // GIVEN

        // WHEN
        List<AuctionHouseDto> allAuctionHouses = auctionHouseService.getAllAuctionHouses();

        // THEN
        assertThat(allAuctionHouses).isEmpty();
    }

    @Test
    void getAllAuctionHouses_withOneHouseInserted_shouldReturnIt() {

        // GIVEN
        String auctionHouseName = "primaryAuctionHouse";
        AuctionHouseEntity expectedHouse = AuctionHouseEntity.builder().auctionHouseName(auctionHouseName).build();
        auctionHouseRepository.save(expectedHouse);

        // WHEN
        List<AuctionHouseDto> allAuctionHouses = auctionHouseService.getAllAuctionHouses();

        // THEN
        assertThat(allAuctionHouses).hasSize(1);
        assertThat(allAuctionHouses.get(0)).isEqualTo(auctionHouseMapper.toDto(expectedHouse));
    }

    @Test
    void getAllAuctionHouses_withTwoDifferentHouseNamesInserted_shouldReturnThemByOlderFirst() {

        // GIVEN
        String auctionHouseName1 = "primaryAuctionHouse";
        AuctionHouseEntity expectedHouse1 = AuctionHouseEntity.builder().auctionHouseName(auctionHouseName1).build();
        auctionHouseRepository.save(expectedHouse1);

        String auctionHouseName2 = "secondaryAuctionHouse";
        AuctionHouseEntity expectedHouse2 = AuctionHouseEntity.builder().auctionHouseName(auctionHouseName2).build();
        auctionHouseRepository.save(expectedHouse2);

        // WHEN
        List<AuctionHouseDto> allAuctionHouses = auctionHouseService.getAllAuctionHouses();

        // THEN
        assertThat(allAuctionHouses).hasSize(2);
        assertThat(allAuctionHouses.get(0)).isEqualTo(auctionHouseMapper.toDto(expectedHouse1));
        assertThat(allAuctionHouses.get(1)).isEqualTo(auctionHouseMapper.toDto(expectedHouse2));
    }

    @Test
    void deleteAuctionHouse_withOneInsertedHouse_shouldLeaveTheDBEmpty() {

        // GIVEN
        String auctionHouseName = "primaryAuctionHouse";
        AuctionHouseEntity expectedHouse = AuctionHouseEntity.builder().auctionHouseName(auctionHouseName).build();
        auctionHouseRepository.save(expectedHouse);

        // WHEN
        auctionHouseService.deleteAuctionHouse(auctionHouseName);

        // THEN
        assertThat(auctionHouseRepository.findAll()).isEmpty();
    }

    @Test
    void deleteAuctionHouse_withTwoInsertedHouses_shouldLeaveTheDBWithOnlyOneElement() {

        // GIVEN
        String auctionHouseName1 = "primaryAuctionHouse";
        AuctionHouseEntity expectedHouse1 = AuctionHouseEntity.builder().auctionHouseName(auctionHouseName1).build();
        auctionHouseRepository.save(expectedHouse1);

        String auctionHouseName2 = "secondaryAuctionHouse";
        AuctionHouseEntity expectedHouse2 = AuctionHouseEntity.builder().auctionHouseName(auctionHouseName2).build();
        auctionHouseRepository.save(expectedHouse2);

        // WHEN
        auctionHouseService.deleteAuctionHouse(auctionHouseName2);

        // THEN
        List<AuctionHouseEntity> allEntities = auctionHouseRepository.findAll();
        assertThat(allEntities).hasSize(1);
        assertThat(allEntities.get(0)).isEqualTo(expectedHouse1);
    }
}