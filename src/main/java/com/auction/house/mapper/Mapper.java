package com.auction.house.mapper;

public interface Mapper<A, T> {

    A toDto(T model);

    T toModel(A dto);
}
