package com.challenge.meli.controller.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;

@Builder
@Getter
public class SatelliteDto {
    private String name;
    private Float distance;
    private String[] message;
}
