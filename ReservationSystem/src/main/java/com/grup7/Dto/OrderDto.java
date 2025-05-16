package com.grup7.Dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderDto {
    private String reservationCode;
    private List<String> categoryIds;
}
