package com.auction.house.controller.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuctionDto {


    @JsonProperty("auction_name")
    private String auctionName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("starting_time")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startingTime;

    @JsonProperty("end_time")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    @JsonProperty("start_price")
    private Float startPrice;

    @JsonProperty("current_price")
    private Float currentPrice;


    /** these fields are set afterward */

    @JsonProperty("auction_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long auctionId;

    /** the auction status is computed using the startingTime and endTime when it is requested */
    @JsonProperty("auction_status")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AuctionStatusEnum auctionStatus;

    /** the auction winner is set only if the AuctionStatusEnum == FINISHED */
    @JsonProperty("auction_winner")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String auctionWinner;
}
