package com.challenge.meli.controller.api.dto;

import lombok.*;

import java.util.Arrays;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SatelliteDto {
    private String name;
    private Float distance;
    private String[] message;
}
